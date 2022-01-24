package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Classes.Adapter_GeneralResearchContent;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_GeneralResearch;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeneralResearchContent_Frag extends Fragment {

    RecyclerView videorecycler;
    List<DataHandler_GeneralResearch> dataHandler_generalResearches;
    TextView topicname;
    ProgressBar progressBar;
    public static String id2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_careplans, container, false);

        topicname = root.findViewById(R.id.textView54);
        videorecycler = root.findViewById(R.id.videorecycler2);
        progressBar = root.findViewById(R.id.progressBar8);
        progressBar.setVisibility(View.VISIBLE);
        dataHandler_generalResearches = new ArrayList<>();
        AppBarLayout appBarLayout =root.findViewById(R.id.appbarlayout);
        appBarLayout.setVisibility(View.GONE);
        topicname.setVisibility(View.GONE);

        LoadHeaders();
        //Scroll();
        return root;
    }

    private void LoadHeaders() {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("page", String.valueOf(1));
                params.put("id", String.valueOf(id2));
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
                        dataHandler_generalResearches.add(new DataHandler_GeneralResearch(
                                product.getInt("id"),
                                product.getString("header"),
                                product.getString("subheading"),
                                product.getString("notes"),
                                product.getString("pdffile")
                        ));
                    }

                    Adapter_GeneralResearchContent adapter = new Adapter_GeneralResearchContent(getContext(), dataHandler_generalResearches);
                    videorecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    videorecycler.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }
}
