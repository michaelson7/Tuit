package com.nkwazi_tech.tuit_app.group;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Group_all_list;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Group_all_list;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class groupalllist_Frag extends Fragment {
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    RecyclerView recyclerView;
    ProgressBar progressBar, loadmorePB;
    ConstraintLayout nodata;
    ImageView postimg;
    TextView nofile;
    int page = 1;
    Adapter_Group_all_list adapter;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_group_all_list, container, false);

        recyclerView = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar6);
        loadmorePB = root.findViewById(R.id.loadmorePB);
        nodata = root.findViewById(R.id.nodata);
        postimg = root.findViewById(R.id.postimg);
        nofile= root.findViewById(R.id.textView40);
        dataHandler_group_all_lists = new ArrayList<>();

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                startActivity(new Intent(getContext(), Home_Activity.class));
                return true;
            }
            return false;
        });

        Defaults();
        LoadGroup();
        Scroll();
        return root;
    }

    private void Defaults() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void LoadGroup() {
        String pages = "&page=" + page;
        @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadgroups + pages+schoolId,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    loadmorePB.setVisibility(View.GONE);
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {

                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);

                            dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                    product.getString("groupname"),
                                    product.getString("groupimage"),
                                    product.getString("groupdescription"),
                                    product.getString("admin"),
                                    product.getInt("members"),
                                    product.getString("newgroup")
                            ));
                            //Collections.sort(dataHandler_group_all_lists, (o1, o2) -> o1.getGroupName().compareTo(o2.getGroupName()));
                        }
                        adapter = new Adapter_Group_all_list(getContext(), dataHandler_group_all_lists, "state");
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
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
        String pages = "&page=" + page3;
        loadmorePB.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadgroups + pages+schoolId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadmorePB.setVisibility(View.GONE);
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                        product.getString("groupname"),
                                        product.getString("groupimage"),
                                        product.getString("groupdescription"),
                                        product.getString("admin"),
                                        product.getInt("members"),
                                        product.getString("newgroup")
                                ));
                                Collections.sort(dataHandler_group_all_lists, (o1, o2) -> o1.getGroupName().compareTo(o2.getGroupName()));
                            }

                            adapter.notifyDataSetChanged();
                            page++;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}
