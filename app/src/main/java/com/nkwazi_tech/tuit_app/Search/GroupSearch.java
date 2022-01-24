package com.nkwazi_tech.tuit_app.Search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Group_all_list;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Group_all_list_mygroup;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Group_all_list;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupSearch extends AppCompatActivity {
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    private List<DataHandler_Group_all_list> my_groups;

    RecyclerView recyclerView,mygroup_recycler;
    ProgressBar progressBar, loadmorePB;
    Adapter_Group_all_list adapter;
    Adapter_Group_all_list_mygroup My_adapter;
    ConstraintLayout nodata;
    TextView nofile;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(this).getSchoolId();

    int page = 1;
    String username = String.valueOf(SharedPrefManager.getInstance(this).getUsername()), searchQuery;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        String ThemeState =  SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")){
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Group Search");

        nofile = findViewById(R.id.textView40);
        recyclerView = findViewById(R.id.recycler2);
        progressBar = findViewById(R.id.progressBar16);
        loadmorePB = findViewById(R.id.loadmorePB);
        mygroup_recycler= findViewById(R.id.recyclerView3);
        nodata=  findViewById(R.id.nodata);
        progressBar.setVisibility(View.VISIBLE);
        //nodata = findViewById(R.id.nodata);

        dataHandler_group_all_lists = new ArrayList<>();
        my_groups= new ArrayList<>();

        Intent intents = getIntent();
        if (Intent.ACTION_SEARCH.equals(intents.getAction())) {
            searchQuery = intents.getStringExtra(SearchManager.QUERY);
        }
        LoadGroup(1,false);
    }

    private void LoadGroup(int pagenumber, boolean b) {
        if (b) {
            loadmorePB.setVisibility(View.VISIBLE);
        }
        String pages = "&page=" + pagenumber + "&username=" + username + "&search_Item=" + searchQuery;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadgroups + pages+schoolId,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    loadmorePB.setVisibility(View.GONE);
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);

                        //all groups
                        JSONArray array = obj.getJSONArray("Products");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject product = array.getJSONObject(i);
                            dataHandler_group_all_lists.add(new DataHandler_Group_all_list(
                                    product.getString("groupname"),
                                    product.getString("groupimage"),
                                    product.getString("groupdescription"),
                                    product.getString("admin"),
                                    product.getInt("members"),
                                    product.getString("newgroup")
                            ));
                        }
                        //my groups
                        JSONArray MY_array = obj.getJSONArray("user_groups");
                        for (int i = 0; i < MY_array.length(); i++) {
                            JSONObject product = MY_array.getJSONObject(i);
                            my_groups.add(new DataHandler_Group_all_list(
                                    product.getString("groupname"),
                                    product.getString("groupimage"),
                                    product.getString("groupdescription"),
                                    product.getString("admin"),
                                    product.getInt("members"),
                                    product.getString("newgroup")
                            ));
                        }

                        //load all group
                        if (!b) {
                            adapter = new Adapter_Group_all_list(GroupSearch.this, dataHandler_group_all_lists, "state");
                            recyclerView.setLayoutManager(new LinearLayoutManager(GroupSearch.this));
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            Scroll();
                        } else {
                            adapter.notifyDataSetChanged();
                            loadmorePB.setVisibility(View.GONE);
                        }
                        if (adapter.getItemCount() < 1) {
                            nodata.setVisibility(View.VISIBLE);
                            nofile.setText("No Groups found containg the text: "+searchQuery);
                        }

                        My_adapter = new Adapter_Group_all_list_mygroup(GroupSearch.this, my_groups,"my_group");
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        mygroup_recycler.setLayoutManager(layoutManager);
                        mygroup_recycler.setAdapter(My_adapter);

                        page++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(GroupSearch.this, "Timeout error, Please check your internet connection", Toast.LENGTH_LONG).show());
        //adding our stringrequest to queue
        Volley.newRequestQueue(GroupSearch.this).add(stringRequest);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
                    LoadGroup(page,true);
                }
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == R.id.search_bar) {
            Toast.makeText(this, "item2 pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.nav_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        searchItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
