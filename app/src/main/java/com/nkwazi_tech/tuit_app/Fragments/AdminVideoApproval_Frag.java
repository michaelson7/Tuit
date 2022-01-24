package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Build;
import android.os.Bundle;
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
import com.nkwazi_tech.tuit_app.Classes.Adapter_AdminVideoApproval;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminVideoApproval_Frag extends Fragment {
    RecyclerView recyclerView;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList = new ArrayList<>();
    ProgressBar progressBar,loadmorePB;
    Adapter_AdminVideoApproval adapter;
    int page = 1;
    String pages;
    ConstraintLayout nodata;
    ImageView postimg;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_edit_videos, container, false);

        getActivity().setTitle("Video Approval");
        recyclerView = root.findViewById(R.id.recycler2);

        loadmorePB = root.findViewById(R.id.loadmorePB);
        progressBar = root.findViewById(R.id.progressBar6);
        nodata= root.findViewById(R.id.nodata);
        postimg= root.findViewById(R.id.postimg);
        progressBar.setVisibility(View.VISIBLE);

        login();
        Scroll();
        //initScrollListener();
        return root;
    }

    private void login() {
        pages = "&page=" + page;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + pages,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {
                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);
                            // adding the product to product list class
                            dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                    product.getInt("videoid"),
                                    product.getString("title"),
                                    product.getString("description"),
                                    product.getInt("lecturerid"),
                                    product.getInt("courseid"),
                                    product.getString("videopath"),
                                    product.getString("name"),
                                    product.getString("propic"),
                                    product.getInt("likes"),
                                    product.getInt("comments"),
                                    product.getInt("views"),
                                    product.getString("timestamp"),
                                    product.getString("tags"),
                                    product.getString("approval"),
                                    product.getString("thumb"),
                                    product.getString("file_Size"),
                                    product.getString("file_Duration")
                            ));
                        }

                        //creating adapter object and setting it to recyclerview
                        adapter = new Adapter_AdminVideoApproval(getContext(), dataHandlerVideoInfoList, "Edit");
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        page++;
                        if (adapter.getItemCount() == 0 ){
                            nodata.setVisibility(View.VISIBLE);
                            postimg.setImageResource(R.mipmap.nodata);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
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
        loadmorePB.setVisibility(View.VISIBLE);
        pages = "&page=" + page3;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + pages,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                // adding the product to product list class
                                dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                        product.getInt("videoid"),
                                        product.getString("title"),
                                        product.getString("description"),
                                        product.getInt("lecturerid"),
                                        product.getInt("courseid"),
                                        product.getString("videopath"),
                                        product.getString("name"),
                                        product.getString("propic"),
                                        product.getInt("likes"),
                                        product.getInt("comments"),
                                        product.getInt("views"),
                                        product.getString("timestamp"),
                                        product.getString("tags"),
                                        product.getString("approval"),
                                        product.getString("thumb"),
                                        product.getString("file_Size"),
                                        product.getString("file_Duration")
                                ));

                            }

                            //creating adapter object and setting it to recyclerview
                            adapter.notifyDataSetChanged();
                            loadmorePB.setVisibility(View.GONE);
                            page++;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
        adapter.notifyDataSetChanged();
    }



}

