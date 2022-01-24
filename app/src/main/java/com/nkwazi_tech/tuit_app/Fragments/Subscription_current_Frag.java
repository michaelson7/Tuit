package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Course;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Explorer;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.Classes.dialog_class;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Subscription_current_Frag extends Fragment {
    RecyclerView recyclerView;
    TextView no_data;
    FloatingActionButton floatingActionButton;
    ProgressBar progressBar;
    String balance;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
    List<DataHandler_Explorer> data_handlerExplorers = new ArrayList<>();
    int id = SharedPrefManager.getInstance(getContext()).getID();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_subscription, container, false);

        data_handlerExplorers.clear();
        getActivity().setTitle("Subscriptions");

        no_data =root.findViewById(R.id.textView84);
        recyclerView = root.findViewById(R.id.recyclerView2);
        progressBar = root.findViewById(R.id.progressBar5);
        floatingActionButton = root.findViewById(R.id.textView48);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Load_Sub();

        return root;
    }

    private void Load_Sub() {
        progressBar.setVisibility(View.VISIBLE);
        String data = "&state=load_user_subs&userid=" + id;
        @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveCources + data+schoolId,
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
                            data_handlerExplorers.add(new DataHandler_Explorer(
                                    product.getInt("courseid"),
                                    product.getInt("subscription_price"),
                                    product.getString("coursename"),
                                    product.getString("courseimg")
                            ));
                        }

                        //getting account balance
                         balance = obj.getString("balance");
                        if (balance.equals("null")) {
                            balance = "0";
                        }

                        Adapter_Course.course_price = balance;
                        Adapter_Course adapter = new Adapter_Course(getContext(), data_handlerExplorers, "fetch_Subs");
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
                        progressBar.setVisibility(View.GONE);

                        if (adapter.getItemCount() < 1) {
                            no_data.setVisibility(View.VISIBLE);
                            no_data.setText("You have not subscribed to any course");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(),"Please check your internet connection", Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}
