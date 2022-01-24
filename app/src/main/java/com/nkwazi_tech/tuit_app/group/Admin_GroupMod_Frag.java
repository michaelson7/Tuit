package com.nkwazi_tech.tuit_app.group;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Admin_GroupMod;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Group_all_list;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.nkwazi_tech.tuit_app.Search.AdminSearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Admin_GroupMod_Frag extends Fragment {
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    RecyclerView recyclerView;
    ProgressBar progressBar, loadmorePB;
    ConstraintLayout nodata;
    ImageView postimg;
    int page = 1;
    Adapter_Admin_GroupMod adapter;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_group_all_list, container, false);
        AdminSearch.approval = null;
        recyclerView = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar6);
        loadmorePB = root.findViewById(R.id.loadmorePB);
        nodata = root.findViewById(R.id.nodata);
        postimg = root.findViewById(R.id.postimg);
        dataHandler_group_all_lists = new ArrayList<>();

        Defaults();
        LoadGroup();
        Scroll();
        return root;
    }

    private void Defaults() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void LoadGroup() {
        String pages = "&page=" + page;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadgroups + pages+schoolId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        loadmorePB.setVisibility(View.GONE);
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                        product.getString("groupname"),
                                        product.getString("groupimage"),
                                        product.getString("groupdescription"),
                                        product.getString("admin"),
                                        product.getInt("members"),
                                        product.getString("newgroup")
                                ));
                                //Collections.sort(dataHandler_group_all_lists, (o1, o2) -> o1.getGroupName().compareTo(o2.getGroupName()));
                            }
                            adapter = new Adapter_Admin_GroupMod(getContext(), dataHandler_group_all_lists);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            page++;

                            if (adapter.getItemCount() == 0) {
                                nodata.setVisibility(View.VISIBLE);
                                postimg.setImageResource(R.mipmap.nodata);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        String pages = "&page=" + page3;
        loadmorePB.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadgroups + pages,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadmorePB.setVisibility(View.GONE);
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                        product.getString("groupname"),
                                        product.getString("groupimage"),
                                        product.getString("groupdescription"),
                                        product.getString("admin"),
                                        product.getInt("members"),
                                        product.getString("newgroup")
                                ));
                                // Collections.sort(dataHandler_group_all_lists, (o1, o2) -> o1.getGroupName().compareTo(o2.getGroupName()));
                            }

                            adapter.notifyDataSetChanged();
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
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}
