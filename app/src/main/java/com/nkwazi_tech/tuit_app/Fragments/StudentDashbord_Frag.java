package com.nkwazi_tech.tuit_app.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Activities.GroupDiscussion_activity;
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Dash_Courselist;
import com.nkwazi_tech.tuit_app.Classes.Adapter_RecentVideo;
import com.nkwazi_tech.tuit_app.Classes.Adapter_products;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_MyCourse;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_programs;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.SpacesItemDecoration;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentDashbord_Frag extends Fragment {
    RecyclerView mostviewd, suggestedvideos, prog, recyclerView;
    NestedScrollView ScrollContent;
    ProgressBar progressBar;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList2;
    List<DataHandler_programs> dataHandler_programs = new ArrayList<>();
    String id = String.valueOf(SharedPrefManager.getInstance(getContext()).getID());
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_studentdash, container, false);

        getActivity().setTitle("Home");
        mostviewd = root.findViewById(R.id.recycler2);
        suggestedvideos = root.findViewById(R.id.suggestedvideosadapter);
        ScrollContent = root.findViewById(R.id.ScrollContent);
        progressBar = root.findViewById(R.id.progressBar15);
        recyclerView = root.findViewById(R.id.recycler);
        prog = root.findViewById(R.id.prog);
        dataHandlerVideoInfoList = new ArrayList<>();
        dataHandlerVideoInfoList2 = new ArrayList<>();

        progressBar.setVisibility(View.GONE);
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                startActivity(new Intent(getContext(), Home_Activity.class));
                return true;
            }
            return false;
        });

        Defaults();
        loadMostViewd();
        load_programs();
        return root;
    }

    private void Defaults() {
        ScrollContent.setVisibility(View.INVISIBLE);
    }

    private void loadMostViewd() {
        String page = "&page=1&sort=views&year=1&userid=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + page+schoolId,
                new Response.Listener<String>() {
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

                                //adding the product to product list class
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
                                        "null", product.getString("thumb"),
                                        product.getString("file_Size"),
                                        product.getString("file_Duration")));
                                Collections.sort(dataHandlerVideoInfoList, (o1, o2) -> o2.getViews() - o1.getViews());
                            }

                            //creating adapter object and setting it to recyclerview
                            Adapter_RecentVideo adapter = new Adapter_RecentVideo(getContext(), dataHandlerVideoInfoList, "data");
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            mostviewd.setLayoutManager(layoutManager);
                            mostviewd.setAdapter(adapter);
                            ScrollContent.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            loadSuggestions();

                            if (adapter.getItemCount() < 1) {

                                dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                        0, "Subscribe to view", "", 0, 0,
                                        "", "", "", 0, 0, 0, "",
                                        "", "", "", "", ""
                                ));
                                Adapter_RecentVideo adapter2 = new Adapter_RecentVideo(getContext(), dataHandlerVideoInfoList, "false");
                                LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                mostviewd.setLayoutManager(layoutManager2);
                                mostviewd.setAdapter(adapter2);
                                ScrollContent.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                                error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                            Toast.makeText(getContext(),
                                    "Please check your internet connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }



    private void loadSuggestions() {
        String page = "&page=1&sort=description&year=1&userid=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + page+schoolId,
                new Response.Listener<String>() {
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

                                //adding the product to product list class
                                dataHandlerVideoInfoList2.add(new DataHandler_VideoInfo(
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
                                        "null", product.getString("thumb"),
                                        product.getString("file_Size"),
                                        product.getString("file_Duration"))
                                );
                                Collections.shuffle(dataHandlerVideoInfoList2);
                            }

                            //creating adapter object and setting it to recyclerview
                            Adapter_RecentVideo adapter = new Adapter_RecentVideo(getContext(), dataHandlerVideoInfoList2, "data");
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            suggestedvideos.setLayoutManager(layoutManager);
                            suggestedvideos.setAdapter(adapter);

                            if (adapter.getItemCount() < 1) {
                                dataHandlerVideoInfoList2.add(new DataHandler_VideoInfo(
                                        0, "Subscribe to view", "", 0, 0,
                                        "", "", "", 0, 0, 0, "",
                                        "", "", "", "", ""
                                ));
                                Adapter_RecentVideo adapter2 = new Adapter_RecentVideo(getContext(), dataHandlerVideoInfoList2, "false");
                                LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                suggestedvideos.setLayoutManager(layoutManager2);
                                suggestedvideos.setAdapter(adapter2);
                                suggestedvideos.setClickable(false);
                                suggestedvideos.setEnabled(false);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                                error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                            Toast.makeText(getContext(),
                                    "Please check your internet connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void load_programs() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.fetch_programs+schoolId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("results");
                            //traversing through all the object

                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                dataHandler_programs.add(new DataHandler_programs(
                                        product.getInt("prog_id"),
                                        0,"",
                                        product.getString("img"),
                                        product.getString("prog_name")
                                ));
                            }

                            progressBar.setVisibility(View.GONE);
                            Adapter_products adapter = new Adapter_products(getContext(), dataHandler_programs,"");
                            prog.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            prog.setItemAnimator(new DefaultItemAnimator());
                            prog.setAdapter(adapter);
                            prog.addItemDecoration(new SpacesItemDecoration(0));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                (Response.ErrorListener) error -> {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                        Toast.makeText(getContext(),
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(Intent.createChooser(i, "Select Video"), 100);
            } else {
                Toast.makeText(getActivity(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
