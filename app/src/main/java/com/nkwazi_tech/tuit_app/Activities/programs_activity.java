package com.nkwazi_tech.tuit_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.nkwazi_tech.tuit_app.Classes.Adapter_LibraryCourse;
import com.nkwazi_tech.tuit_app.Classes.Adapter_RecentVideo;
import com.nkwazi_tech.tuit_app.Classes.Adapter_products;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Explorer;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_programs;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.gauriinfotech.commons.Progress;

public class programs_activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<DataHandler_programs> dataHandler_programs = new ArrayList<>();
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(this).getSchoolId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ThemeState = SharedPrefManager.getInstance(programs_activity.this).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_programs);

        recyclerView = findViewById(R.id.recycler);
        progressBar = findViewById(R.id.progressBar11);

        load_programs();
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
                            Adapter_products adapter = new Adapter_products(programs_activity.this, dataHandler_programs,"");
                            recyclerView.setLayoutManager(new LinearLayoutManager(programs_activity.this));
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                (Response.ErrorListener) error -> {
                    Toast.makeText(programs_activity.this, error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                        Toast.makeText(programs_activity.this,
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}