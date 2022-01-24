package com.nkwazi_tech.tuit_app.group;

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
import com.nkwazi_tech.tuit_app.Classes.Adapter_MyGroup;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Group_all_list;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyGroups_Frag extends Fragment {
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    RecyclerView recyclerView;
    ProgressBar progressBar, loadmorePB;
    String username;
    ConstraintLayout nodata;
    ImageView postimg;
    Adapter_MyGroup adapter;
    TextView nofile;
    int page = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_group_all_list, container, false);

        recyclerView = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar6);
        postimg = root.findViewById(R.id.postimg);
        loadmorePB = root.findViewById(R.id.loadmorePB);
        nofile= root.findViewById(R.id.textView40);
        username = SharedPrefManager.getInstance(getContext()).getUsername();
        dataHandler_group_all_lists = new ArrayList<>();
        nodata = root.findViewById(R.id.nodata);

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                startActivity(new Intent(getContext(), Home_Activity.class));
                return true;
            }
            return false;
        });

        LoadGroup();
        Scroll();
        return root;
    }

    private void LoadGroup() {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("page", String.valueOf(page));
                return requestHandler.sendPostRequest(URLs.URL_getmygroups, params);
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
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("Products");

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        // adding the product to product list class
                        dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                product.getString("groupname"),
                                product.getString("groupimage"),
                                product.getString("groupdescription"),
                                product.getString("admin"),
                                100,
                                product.getString("newgroup")
                        ));
                        //Collections.sort(dataHandler_group_all_lists, (o1, o2) -> o1.getGroupName().compareTo(o2.getGroupName()));
                    }
                    adapter = new Adapter_MyGroup(getContext(), dataHandler_group_all_lists);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                    page++;

                    if (adapter.getItemCount() < 1) {
                        nodata.setVisibility(View.VISIBLE);
                        nofile.setText("You are not registered in any group");
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

        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("page", String.valueOf(page3));
                return requestHandler.sendPostRequest(URLs.URL_getmygroups, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadmorePB.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loadmorePB.setVisibility(View.GONE);

                try {
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("Products");

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        // adding the product to product list class
                        dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                product.getString("groupname"),
                                product.getString("groupimage"),
                                product.getString("groupdescription"),
                                product.getString("admin"),
                                100,
                                product.getString("newgroup")
                        ));
                        //Collections.sort(dataHandler_group_all_lists, (o1, o2) -> o1.getGroupName().compareTo(o2.getGroupName()));
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
    }

}
