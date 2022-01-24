package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Classes.Adapter_Library;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_careplans;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NursingResarchUploads_Frag extends Fragment {
    RecyclerView videorecycler;
    List<DataHandler_careplans> dataHandler_careplans;
    TextView topicname;
    TextView nofile;
    ProgressBar progressBar;
    ConstraintLayout nodata;
    int page = 1;
    Adapter_Library adapter;
    private Spinner topic;
    AppBarLayout appbarlayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_careplans, container, false);
        topic = root.findViewById(R.id.recycler2);
        topicname = root.findViewById(R.id.textView54);
        appbarlayout= root.findViewById(R.id.appbarlayout);
        videorecycler = root.findViewById(R.id.videorecycler2);
        nofile = root.findViewById(R.id.textView40);
        progressBar = root.findViewById(R.id.progressBar8);
        progressBar.setVisibility(View.VISIBLE);
        nodata = root.findViewById(R.id.nodata);
        dataHandler_careplans = new ArrayList<>();

        Default();
        loadProducts();
        //Scroll();
        return root;
    }

    private void Default() {
        appbarlayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String[] status = {"Nursing Problems", "Nursing Diagnosis", "Aim/Goals/Objectives", "Interventions", "Evaluations"};
        topicname.setText("PDF Files");
        topic.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status));
        topic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                topicname.setText("PDF Files");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void loadProducts() {
        int id = SharedPrefManager.getInstance(getContext()).getID();
        @SuppressLint("StaticFieldLeak")
        class AddComment extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("page", String.valueOf(page));
                params.put("id", String.valueOf(id));
                return requestHandler.sendPostRequest(URLs.URL_getnursingresearch, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    progressBar.setVisibility(View.GONE);

                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("Products");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);
                        dataHandler_careplans.add(new DataHandler_careplans(
                                product.getInt("id"),
                                product.getString("username"),
                                product.getString("propic"),
                                product.getString("name"),
                                product.getString("description"),
                                product.getString("topic"),
                                product.getString("pdffile"),
                                product.getString("lresponse")
                        ));
                    }

                    //creating adapter object and setting it to recyclerview
                    adapter = new Adapter_Library(getContext(), dataHandler_careplans,"viewresponse");
                    videorecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    videorecycler.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    page++;

                    if (adapter.getItemCount() < 1) {
                        nodata.setVisibility(View.VISIBLE);
                        nofile.setText("You have not uploaded a research proposal");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AddComment ul = new AddComment();
        ul.execute();
    }

}
