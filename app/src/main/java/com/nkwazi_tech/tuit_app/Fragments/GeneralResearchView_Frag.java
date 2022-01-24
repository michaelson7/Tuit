package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Classes.Adapter_GeneralResearchList;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_GeneralResearch;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeneralResearchView_Frag extends Fragment {
    int page;
    RecyclerView year1;
    private List<DataHandler_GeneralResearch> dataHandler_generalResearches;
    ProgressBar progressBar;
    ArrayList<String> Headers = new ArrayList<String>();
    TextView nofile;
    ConstraintLayout nodata;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_home, container, false);

        Headers.clear();
        getActivity().setTitle("Select Header");
        year1 = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar5);
        nodata = root.findViewById(R.id.nodata);
        nofile = root.findViewById(R.id.textView40);
        dataHandler_generalResearches = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        Defaults();
        return root;
    }

    private void Defaults() {
       LoadHeader();
    }

    private void LoadHeader() {
        page = 1;
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("page", String.valueOf(page));
                return requestHandler.sendPostRequest(URLs.URL_generalresearch, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("Products");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject product = array.getJSONObject(i);
                        if (Headers.contains(product.getString("header"))) {
                            // Headers.remove(product.getString("header"));
                            //  Headers.add(product.getString("header"));
                        }else{
                            Headers.add(product.getString("header"));
                            dataHandler_generalResearches.add(new DataHandler_GeneralResearch(
                                    product.getInt("id"),
                                    product.getString("header"),
                                    product.getString("subheading"),
                                    product.getString("notes"),
                                    product.getString("pdffile")
                            ));
                        }
                    }

                    Adapter_GeneralResearchList adapter = new Adapter_GeneralResearchList(getContext(), dataHandler_generalResearches, "data", "header");
                    year1.setLayoutManager(new LinearLayoutManager(getContext()));
                    year1.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                    if (adapter.getItemCount() < 1) {
                        nodata.setVisibility(View.VISIBLE);
                        nofile.setText("No notes uploaded");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }
}
