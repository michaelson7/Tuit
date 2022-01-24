package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Course;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Explorer;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.SpacesItemDecoration;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Explorer_Frag extends Fragment {
    RecyclerView recyclerView;
    List<DataHandler_Explorer> data_handlerExplorers;
    ProgressBar progressBar;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_explorer, container, false);
        recyclerView = root.findViewById(R.id.recycler2);
        progressBar  = root.findViewById(R.id.progressBar6);
        data_handlerExplorers = new ArrayList<>();

        //shimmer effect
//        List<DataHandler_MyCourse> shimmer;
//        shimmer = new ArrayList<>();
//
//        for (int i = 0; i < 9; i++) {
//            shimmer.add(new DataHandler_MyCourse(
//                    ""
//            ));
//        }
//        Adapter_Shimmer adapter = new Adapter_Shimmer(getContext(), shimmer,"explorer");
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new SpacesItemDecoration(0));
        loadCources();

        return root;
    }

    private void loadCources() {
        progressBar.setVisibility(View.VISIBLE);
        final int  schoolId = SharedPrefManager.getInstance(getContext()).getSchoolId();
        Log.d("SCHOOLID:", String.valueOf(schoolId));

        try{
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveCources+ "&schoolId=" + schoolId,
                    response -> {
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list class
                                data_handlerExplorers.add(new DataHandler_Explorer(
                                        product.getInt("courseid"),
                                        product.getInt("subscription_price"),
                                        product.getString("coursename"),
                                        product.getString("courseimg")
                                ));
                            }

                            Adapter_Course adapter = new Adapter_Course(getContext(), data_handlerExplorers, "user");
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.addItemDecoration(new SpacesItemDecoration(0));
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

                        } catch (JSONException e) {
                            e.printStackTrace();
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
        }catch (Exception e){
            Log.d("ERROR",e.getLocalizedMessage());
        }
    }
}
