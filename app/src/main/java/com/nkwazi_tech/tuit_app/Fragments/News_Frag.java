package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.nkwazi_tech.tuit_app.Classes.Adapter_News;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_News;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class News_Frag extends Fragment {
    RecyclerView recycler;
    ProgressBar loadmorePB;
    int page = 1;
    Adapter_News adapter;
    private List<DataHandler_News> dataHandler_news;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_news, container, false);

        getActivity().setTitle("Notice Board");
        recycler = root.findViewById(R.id.recyclerView);
        loadmorePB = root.findViewById(R.id.loadmorePB);
        dataHandler_news = new ArrayList<>();

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        LoadNews(page);
        return root;
    }

    private void LoadNews(int pages) {
        String path = "&page="+pages;
        String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveNews+path+schoolId  ,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("results");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject results = array.getJSONObject(i);

                                //adding the product to product list class
                                dataHandler_news.add(new DataHandler_News(
                                        results.getInt("id"),
                                        results.getString("name"),
                                        results.getString("audience"),
                                        results.getString("subject"),
                                        results.getString("description"),
                                        results.getString("time"),
                                        results.getString("file"),
                                        results.getString("profilepicture")
                                ));
                            }

                            adapter = new Adapter_News(getContext(), dataHandler_news);
                            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                            recycler.setAdapter(adapter);
                            recycler.getViewTreeObserver().addOnPreDrawListener(
                                    new ViewTreeObserver.OnPreDrawListener() {

                                        @Override
                                        public boolean onPreDraw() {
                                            recycler.getViewTreeObserver().removeOnPreDrawListener(this);

                                            for (int i = 0; i < recycler.getChildCount(); i++) {
                                                View v = recycler.getChildAt(i);
                                                v.setAlpha(0.0f);
                                                v.animate().alpha(1.0f)
                                                        .setDuration(300)
                                                        .setStartDelay(i * 50)
                                                        .start();
                                            }
                                            return true;
                                        }
                                    });
                            page++;
                            Scroll();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error ->  Toast.makeText(getContext(),
                        "Please check your internet connection",
                        Toast.LENGTH_LONG).show());

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
    //load more
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Scroll() {
        recycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isLastItemDisplaying(recycler)) {
                    //Calling the method getdata again
                    loadMore(page);
                }
            }
        });
    }

    private void loadMore(int pagenumber) {
        //first getting the values
        loadmorePB.setVisibility(View.VISIBLE);
        String path = "&page="+pagenumber;
        String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveNews+path+schoolId,
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
                                JSONObject results = array.getJSONObject(i);

                                // adding the product to product list class
                                dataHandler_news.add(new DataHandler_News(
                                        results.getInt("id"),
                                        results.getString("name"),
                                        results.getString("audience"),
                                        results.getString("subject"),
                                        results.getString("description"),
                                        results.getString("time"),
                                        results.getString("file"),
                                        results.getString("profilepicture")
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof NetworkError ||error instanceof ServerError || error instanceof AuthFailureError ||
                                error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                            Toast.makeText(getContext(),
                                    "Please check your internet connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
