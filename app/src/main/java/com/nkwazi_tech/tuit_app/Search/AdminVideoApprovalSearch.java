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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Activities.AdminVideoApproval_Activity;
import com.nkwazi_tech.tuit_app.Classes.Adapter_AdminVideoApproval;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminVideoApprovalSearch extends AppCompatActivity {
    RecyclerView recyclerView;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList = new ArrayList<>();
    ProgressBar progressBar,loadmorePB;
    Adapter_AdminVideoApproval adapter;
    int page = 1;
    String pages;
    int fetch = 0;
    ConstraintLayout nodata;

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
        getSupportActionBar().setTitle("Search");

        recyclerView = findViewById(R.id.recycler2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadmorePB = findViewById(R.id.loadmorePB);
        progressBar = findViewById(R.id.progressBar16);
        nodata= findViewById(R.id.nodata);
        progressBar.setVisibility(View.VISIBLE);
        nodata.setVisibility(View.GONE);

        for (int i = 0; i < 10; i++) {
            login(i);
        }
        Scroll();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSearch();
    }
    private void handleSearch() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            Filterdata(searchQuery);

        }else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            String selectedSuggestionRowId =  intent.getDataString();
            //execution comes here when an item is selected from search suggestions
            //you can continue from here with user selected search item
            Toast.makeText(this, "selected search suggestion "+selectedSuggestionRowId,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void Filterdata(String searchQuery) {
        ArrayList<DataHandler_VideoInfo> filteredlist = new ArrayList<>();
        for (DataHandler_VideoInfo item : dataHandlerVideoInfoList){
            if (item.getTitle().toLowerCase().contains(searchQuery.toLowerCase())){
                filteredlist.add(item);
            }
        }
        adapter.filterlist(filteredlist);
    }

    private void login(int pagenumber) {
        pages = "&page=" + pagenumber;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + pages,
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
                            dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                    product.getInt("videoid"),
                                    product.getString("title"),
                                    product.getString("description"),
                                    product.getInt("lecturerid"),
                                    product.getInt("courseid"),
                                    product.getString("videopath"),
                                    product.getString("name"),
                                    product.getString("propic"),
                                    product.getInt("likes"),
                                    product.getInt("comments"),
                                    product.getInt("views"),
                                    product.getString("timestamp"),
                                    product.getString("tags"),
                                    "null", product.getString("thumb"),
                                    product.getString("file_Size"),
                                    product.getString("file_Duration")));
                            dataHandlerVideoInfoList.get(fetch).setApprovalresponse(product.getString("approval"));
                            fetch++;
                        }

                        //creating adapter object and setting it to recyclerview
                        adapter = new Adapter_AdminVideoApproval(AdminVideoApprovalSearch.this, dataHandlerVideoInfoList, "Edit");
                        recyclerView.setLayoutManager(new LinearLayoutManager(AdminVideoApprovalSearch.this));
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        page++;
                        handleSearch();
                        if (adapter.getItemCount() == 0 ){
                           // nodata.setVisibility(View.VISIBLE);
                           // postimg.setImageResource(R.mipmap.nodata);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AdminVideoApprovalSearch.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(AdminVideoApprovalSearch.this).add(stringRequest);
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
        loadmorePB.setVisibility(View.VISIBLE);
        pages = "&page=" + page3;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_Retrive + pages,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                // adding the product to product list class
                                dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                        product.getInt("videoid"),
                                        product.getString("title"),
                                        product.getString("description"),
                                        product.getInt("lecturerid"),
                                        product.getInt("courseid"),
                                        product.getString("videopath"),
                                        product.getString("name"),
                                        product.getString("propic"),
                                        product.getInt("likes"),
                                        product.getInt("comments"),
                                        product.getInt("views"),
                                        product.getString("timestamp"),
                                        product.getString("tags"),
                                        "null", product.getString("thumb"),
                                        product.getString("file_Size"),
                                        product.getString("file_Duration")));
                                dataHandlerVideoInfoList.get(fetch).setApprovalresponse(product.getString("approval"));
                                fetch++;
                            }

                            //creating adapter object and setting it to recyclerview
                            adapter.notifyDataSetChanged();
                            loadmorePB.setVisibility(View.GONE);
                            page++;

                            if (adapter.getItemCount() == 0 ){
                              //  nodata.setVisibility(View.VISIBLE);
                             //   postimg.setImageResource(R.mipmap.nodata);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Toast.makeText(AdminVideoApprovalSearch.this, error.toString(), Toast.LENGTH_LONG).show());

        //adding our stringrequest to queue
        Volley.newRequestQueue(AdminVideoApprovalSearch.this).add(stringRequest);
        adapter.notifyDataSetChanged();
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
        Intent intent = new Intent(this, AdminVideoApproval_Activity.class);
        this.startActivity(intent);
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminVideoApproval_Activity.class);
        this.startActivity(intent);
        super.onBackPressed();
    }
}
