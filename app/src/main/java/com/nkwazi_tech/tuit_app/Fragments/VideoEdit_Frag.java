package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Classes.Adapter_VideoEdit;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoEdit_Frag extends Fragment {
    RecyclerView recyclerView;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList = new ArrayList<>();
    ProgressBar progressBar,loadmorePB;
    Adapter_VideoEdit adapter;
    int page = 1;
    ConstraintLayout nodata;
    ImageView postimg;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_edit_videos, container, false);

        getActivity().setTitle("Video Edit");
        recyclerView = root.findViewById(R.id.recycler2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadmorePB = root.findViewById(R.id.loadmorePB);
        progressBar = root.findViewById(R.id.progressBar6);
        nodata= root.findViewById(R.id.nodata);
        postimg= root.findViewById(R.id.postimg);
        progressBar.setVisibility(View.VISIBLE);

        login();
        Scroll();
        //initScrollListener();
        return root;
    }

    private void login() {
        //first getting the values
        final int lecturerID = SharedPrefManager.getInstance(getContext()).getID();
        //if everything is fine
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("lecturerid", String.valueOf(lecturerID));
                params.put("page", String.valueOf(page));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_RetriveID, params);
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
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in respon
                    //getting the dataHandlerUser from the response
                    JSONArray array = obj.getJSONArray("Products");
                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        //adding the product to product list class
                        dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                product.getInt("videoid"),
                                product.getString("title"),
                                product.getString("description"),
                                product.getInt("lecturerid"),
                                product.getInt("courseid"),
                                product.getString("videopath"),
                                "", "", 0, 0, 0,"","",
                                "null", product.getString("thumb"),
                                product.getString("file_Size"),
                                product.getString("file_Duration")));
                    }


                    adapter = new Adapter_VideoEdit(getContext(), dataHandlerVideoInfoList, "Edit");
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    page++;

                    if (adapter.getItemCount() == 0 ){
                        nodata.setVisibility(View.VISIBLE);
                        postimg.setImageResource(R.mipmap.nodata);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }
    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Scroll() {
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isLastItemDisplaying(recyclerView)) {
                    //Calling the method getdata again
                    loadMore(page);
                }
            }
        });
    }

    private void loadMore(int page3) {
        //first getting the values
        final int lecturerID = SharedPrefManager.getInstance(getContext()).getID();
        final int pages = page3;
        //if everything is fine

        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("lecturerid", String.valueOf(lecturerID));
                params.put("page", String.valueOf(pages));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_RetriveID, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadmorePB.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loadmorePB.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in respon
                    //getting the dataHandlerUser from the response
                    JSONArray array = obj.getJSONArray("Products");
                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {
                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);
                        //adding the product to product list class
                        dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                product.getInt("videoid"),
                                product.getString("title"),
                                product.getString("description"),
                                product.getInt("lecturerid"),
                                product.getInt("courseid"),
                                product.getString("videopath"),
                                "", "", 0, 0, 0,"","",
                                "null", product.getString("thumb"),
                                product.getString("file_Size"),
                                product.getString("file_Duration")));
                    }

                    adapter.notifyDataSetChanged();
                    loadmorePB.setVisibility(View.GONE);
                    page++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onResume() {
        super.onResume();
//        Adapter_VideoEdit.iniexoplayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
           Adapter_VideoEdit.releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            Adapter_VideoEdit.releasePlayer();
        }
    }

}

