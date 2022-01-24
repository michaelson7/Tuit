package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_GeneralResearchList;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_GeneralResearch;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NursingCarePlans_header_Frag extends Fragment {

    RecyclerView year1;
    private List<DataHandler_GeneralResearch> dataHandler_generalResearches;
    ProgressBar progressBar;
    ArrayList<String> Headers = new ArrayList<String>();
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_home, container, false);
        Headers.clear();
        getActivity().setTitle("Body System");
        year1 = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar5);
        dataHandler_generalResearches = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        Defaults();
        return root;
    }

    private void Defaults() {
        LoadHeader();
    }

    private void LoadHeader() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadheader+schoolId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                if (Headers.contains(product.getString("header"))) {
                                   // Headers.remove(product.getString("header"));
                                  //  Headers.add(product.getString("header"));
                                }else{
                                    Headers.add(product.getString("header"));
                                    dataHandler_generalResearches.add(new DataHandler_GeneralResearch(
                                            1,
                                            product.getString("header"),
                                            "",
                                            "",
                                            ""
                                    ));
                                }
                            }
                            Adapter_GeneralResearchList adapter = new Adapter_GeneralResearchList(getContext(), dataHandler_generalResearches,"careplan", "header");
                            year1.setLayoutManager(new LinearLayoutManager(getContext()));
                            year1.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Toast.makeText(getContext(),"Please check your internet connection", Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }


}
