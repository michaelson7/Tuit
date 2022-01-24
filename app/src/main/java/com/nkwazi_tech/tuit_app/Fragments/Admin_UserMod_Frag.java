package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Admin_UserMod;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.nkwazi_tech.tuit_app.Search.AdminSearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Admin_UserMod_Frag extends Fragment {
    RecyclerView recyclerView;
    List<DataHandler_User> dataHandler_User = new ArrayList<>();
    TextView nofile;
    ProgressBar progressBar, loadmorePB;
    Adapter_Admin_UserMod adapter;
    int page = 1;
    ConstraintLayout nodata;
    public static String approval;
    ArrayList<String> Headers = new ArrayList<String>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_edit_videos, container, false);

        getActivity().setTitle("Account Modification");
        //AdminSearch.state = approval;
        recyclerView = root.findViewById(R.id.recycler2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadmorePB = root.findViewById(R.id.loadmorePB);
        progressBar = root.findViewById(R.id.progressBar6);
        nodata = root.findViewById(R.id.nodata);
        nofile = root.findViewById(R.id.textView40);
        progressBar.setVisibility(View.VISIBLE);
        AdminSearch.approval = approval;

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                startActivity(new Intent(getContext(), Home_Activity.class));
                return true;
            }
            return false;
        });

        login();
        Scroll();
        //initScrollListener();
        return root;
    }

    private void login() {
        dataHandler_User.clear();

        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                if (approval.equals("true")) {
                    params.put("approval", "true");
                }else{
                    params.put("page", String.valueOf(page));
                }
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_GetUsers, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in respon
                    //getting the dataHandlerUser from the response
                    JSONArray array = obj.getJSONArray("Products");
                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        //adding the product to product list class
                        if (approval.equals("true")) {
                            if (Headers.contains(product.getString("username"))) {
                                Headers.remove(product.getString("username"));
                                Headers.add(product.getString("username"));
                            }else{
                                Headers.add(product.getString("username"));
                                try {
                                    dataHandler_User.add(new DataHandler_User(
                                            product.getInt("id"),
                                            0,
                                            product.getString("email"),
                                            product.getString("phonenumber"),
                                            product.getString("name"),
                                            product.getString("propic"),
                                            product.getString("coverpic"),
                                            product.getString("accounttype"),
                                            "",
                                            "",0
                                    ));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                dataHandler_User.add(new DataHandler_User(
                                        product.getInt("id"),
                                       0,
                                        product.getString("email"),
                                        product.getString("phonenumber"),
                                        product.getString("name"),
                                        product.getString("propic"),
                                        product.getString("coverpic"),
                                        product.getString("accounttype"),
                                        "",
                                        "",0
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (approval.equals("true")) {
                        adapter = new Adapter_Admin_UserMod(getContext(), dataHandler_User, "Approval");
                    } else {
                        adapter = new Adapter_Admin_UserMod(getContext(), dataHandler_User, "Edit");
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    page++;

                    if (adapter.getItemCount() < 1) {
                        nodata.setVisibility(View.VISIBLE);
                        nofile.setText("No new users found");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Scroll() {
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isLastItemDisplaying(recyclerView)) {
                    //Calling the method getdata again
                    loadMore(page);
                }
            }
        });
    }

    private void loadMore(int page3) {
        //first getting the values
        final int lecturerID = SharedPrefManager.getInstance(getContext()).getID();
        //if everything is fine

        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                if (approval.equals("true")) {
                    params.put("approval", "true");
                }
                params.put("page", String.valueOf(page3));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_GetUsers, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in respon
                    //getting the dataHandlerUser from the response
                    JSONArray array = obj.getJSONArray("Products");
                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        //adding the product to product list class
                        if (approval.equals("true")) {
                            if (Headers.contains(product.getString("username"))) {
                                Headers.remove(product.getString("username"));
                                Headers.add(product.getString("username"));
                            }else{
                                Headers.add(product.getString("username"));
                                try {
                                    dataHandler_User.add(new DataHandler_User(
                                            product.getInt("id"),
                                            product.getInt("courseid"),
                                            product.getString("email"),
                                            product.getString("phonenumber"),
                                            product.getString("name"),
                                            product.getString("propic"),
                                            product.getString("coverpic"),
                                            product.getString("accounttype"),
                                            product.getString("practisenumber"),
                                            product.getString("lecturercourse"),
                                            product.getInt("schoolId")
                                    ));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                dataHandler_User.add(new DataHandler_User(
                                        product.getInt("id"),
                                        product.getInt("courseid"),
                                        product.getString("email"),
                                        product.getString("phonenumber"),
                                        product.getString("name"),
                                        product.getString("propic"),
                                        product.getString("coverpic"),
                                        product.getString("accounttype"),
                                        product.getString("practisenumber"),
                                        product.getString("lecturercourse"),
                                        product.getInt("schoolId")
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    loadmorePB.setVisibility(View.GONE);
                    page++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
        adapter.notifyDataSetChanged();
    }


}

