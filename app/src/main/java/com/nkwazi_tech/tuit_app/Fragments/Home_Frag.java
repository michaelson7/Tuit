package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Video;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home_Frag extends Fragment {
    RecyclerView recyclerView;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList;
    ProgressBar loadmorePB, progressBar;
    public Adapter_Video adapter;
    int page = 1;
    String items = "null";
    String defaultload = "timestamp";
    ConstraintLayout nodata;
    String id = String.valueOf(SharedPrefManager.getInstance(getContext()).getID());
    TextView nofile;
    Boolean b = true;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_home, container, false);

        getActivity().setTitle("Explore");

        recyclerView = root.findViewById(R.id.recycler2);
        nofile = root.findViewById(R.id.textView40);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = root.findViewById(R.id.progressBar5);
        dataHandlerVideoInfoList = new ArrayList<>();
        loadmorePB = root.findViewById(R.id.loadmorePB);
        nodata = root.findViewById(R.id.nodata);

        Home_Activity.sorting.setVisible(false);
        Home_Activity.sorting.setOnMenuItemClickListener(item -> {
            String[] SortyBy = {"Likes", "Views", "Time", "Suggested"};
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(), R.style.AlertDialog);
            builder.setTitle("Sort Videos By: ");
            builder.setItems(SortyBy, (dialog, which) -> {
                if (which == 0) {
                    Sort("likes", page);
                    items = "likes";
                } else if (which == 1) {
                    Sort("views", page);
                    items = "views";
                } else if (which == 2) {
                    Sort("timestamp", page);
                    items = "timestamp";
                } else if (which == 3) {
                    Sort("description", page);
                    items = "description";
                }

            });
            builder.show();
            return false;
        });

        //shimmer effect
//        List<DataHandler_MyCourse> shimmer;
//        shimmer = new ArrayList<>();
//
//        for (int i = 0; i < 2; i++) {
//            shimmer.add(new DataHandler_MyCourse(
//                    ""
//            ));
//        }
//        Adapter_Shimmer adapter = new Adapter_Shimmer(getContext(), shimmer,"home");
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
//

        loadProducts(page);

        return root;
    }

    private void loadProducts(int pagenumber) {
        progressBar.setVisibility(View.VISIBLE);
        String year = SharedPrefManager.getInstance(getContext()).getStudentcourse();
        String pages;
        if (!year.equals("null")) {
            switch (year) {
                case "Year One":
                    year = "year-one";
                    break;
                case "Year Two":
                    year = "year-two";
                    break;
                case "Year Three":
                    year = "year-three";
                    break;
            }
            pages = "&page=" + pagenumber + "&year=" + year + "&userid=" + id;
        } else {
            pages = "&page=" + pagenumber;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + pages+schoolId,
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
                                        "null",
                                        product.getString("thumb"),
                                        product.getString("file_Size"),
                                        product.getString("file_Duration")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            adapter = new Adapter_Video(getContext(), dataHandlerVideoInfoList, b, "state");
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            recyclerView.getViewTreeObserver().addOnPreDrawListener(
                                    new ViewTreeObserver.OnPreDrawListener() {

                                        @Override
                                        public boolean onPreDraw() {
                                            recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                                            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                                                View v = recyclerView.getChildAt(i);
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
                            progressBar.setVisibility(View.GONE);

                            if (adapter.getItemCount() < 1) {
                                nodata.setVisibility(View.VISIBLE);
                                nofile.setText("Please Subscribe to view videos");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



    private void Sort(String item, int pagenumber) {
        pagenumber = 1;
        page = 1;
        dataHandlerVideoInfoList.clear();
        String pages;

        String year = SharedPrefManager.getInstance(getContext()).getStudentcourse();
        if (!year.equals("null")) {
            switch (year) {
                case "Year One":
                    year = "year-one";
                    break;
                case "Year Two":
                    year = "year-two";
                    break;
                case "Year Three":
                    year = "year-three";
                    break;
            }
            pages = "&page=" + pagenumber + "&sort=" + item + "&year=" + year + "&userid=" + id;
        } else {
            pages = "&page=" + pagenumber + "&sort=" + item;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + pages+schoolId,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject product = array.getJSONObject(i);
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
                                    "null",
                                    product.getString("thumb"),
                                    product.getString("file_Size"),
                                    product.getString("file_Duration")));
                            if (item.equals("Likes")) {
                                Collections.sort(dataHandlerVideoInfoList, (o1, o2) -> o2.getLikes() - o1.getLikes());
                            } else if (item.equals("Views")) {
                                Collections.sort(dataHandlerVideoInfoList, (o1, o2) -> o2.getViews() - o1.getViews());
                            } else if (item.equals("Suggested")) {
                                Collections.shuffle(dataHandlerVideoInfoList);
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        adapter = new Adapter_Video(getContext(), dataHandlerVideoInfoList, b, "state");
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                        page++;
                        defaultload = item;
                        Scroll();

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
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (isLastItemDisplaying(recyclerView)) {
                //Calling the method getdata again
                loadMore(page, items);
            }
        });
    }

    private void loadMore(int pagenumber, String item) {
        //first getting the values
        loadmorePB.setVisibility(View.VISIBLE);
        //String pages = "&page=" + pagenumber + "&sort=" + defaultload;
        String year = SharedPrefManager.getInstance(getContext()).getStudentcourse();
        String pages;
        if (!year.equals("null")) {
            switch (year) {
                case "Year One":
                    year = "year-one";
                    break;
                case "Year Two":
                    year = "year-two";
                    break;
                case "Year Three":
                    year = "year-three";
                    break;
            }
            pages = "&page=" + pagenumber + "&year=" + year + "&sort=" + defaultload + "&userid=" + id;
        } else {
            pages = "&page=" + pagenumber + "&sort=" + defaultload;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + pages+schoolId,
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
                                        "null",
                                        product.getString("thumb"),
                                        product.getString("file_Size"),
                                        product.getString("file_Duration")));
                                if (item.equals("Likes")) {
                                    Collections.sort(dataHandlerVideoInfoList, (o1, o2) -> o2.getLikes() - o1.getLikes());
                                } else if (item.equals("Views")) {
                                    Collections.sort(dataHandlerVideoInfoList, (o1, o2) -> o2.getViews() - o1.getViews());
                                } else if (item.equals("Suggested")) {
                                    Collections.shuffle(dataHandlerVideoInfoList);
                                }
                            }

                            //creating adapter object and setting it to recyclerview
                            try {
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
