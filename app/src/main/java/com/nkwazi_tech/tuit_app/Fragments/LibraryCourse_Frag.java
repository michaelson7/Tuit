package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_LibraryCourse;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_MyCourse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LibraryCourse_Frag extends Fragment {
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    RecyclerView year1;
    private List<DataHandler_MyCourse> dataHandler_myCourses;
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_home, container, false);

        year1 = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar5);
        dataHandler_myCourses = new ArrayList<>();

        LoadCourses();
        return root;
    }

    private void LoadCourses() {
        progressBar.setVisibility(View.VISIBLE);
         String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getcourse + "&books=true" + schoolId,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {
                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);
                            dataHandler_myCourses.add(new DataHandler_MyCourse(
                                    product.getString("coursename")
                            ));
                        }
                        Adapter_LibraryCourse adapter = new Adapter_LibraryCourse(getContext(), dataHandler_myCourses);
                        year1.setLayoutManager(new LinearLayoutManager(getContext()));
                        year1.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        year1.getViewTreeObserver().addOnPreDrawListener(
                                new ViewTreeObserver.OnPreDrawListener() {

                                    @Override
                                    public boolean onPreDraw() {
                                        year1.getViewTreeObserver().removeOnPreDrawListener(this);

                                        for (int i = 0; i < year1.getChildCount(); i++) {
                                            View v = year1.getChildAt(i);
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
    }
}
