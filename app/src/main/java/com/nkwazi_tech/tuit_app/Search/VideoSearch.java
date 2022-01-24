package com.nkwazi_tech.tuit_app.Search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Video;
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

public class VideoSearch extends AppCompatActivity {
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList;

    public Adapter_Video adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar, loadmorePB;
    ConstraintLayout nodata;
    public static TextView nofile;

    int page = 1;
    Boolean b = true;
    String id = String.valueOf(SharedPrefManager.getInstance(this).getID()),
            accountType = SharedPrefManager.getInstance(this).getAccounttype(), searchQuery;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        String ThemeState = SharedPrefManager.getInstance(this).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
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

        dataHandlerVideoInfoList = new ArrayList<>();


        progressBar = findViewById(R.id.progressBar16);
        nodata = findViewById(R.id.nodata);

        progressBar.setVisibility(View.VISIBLE);
        loadmorePB = findViewById(R.id.loadmorePB);
        nofile = findViewById(R.id.textView40);

        Intent intents = getIntent();
        if (Intent.ACTION_SEARCH.equals(intents.getAction())) {
            searchQuery = intents.getStringExtra(SearchManager.QUERY);
        }

        loadProducts(1, false);
    }

    private void loadProducts(int pagenumber, boolean b) {
        if (b) {
            loadmorePB.setVisibility(View.VISIBLE);
        }
        String pages = "&page=" + pagenumber + "&account_type=" + accountType + "&userid=" + id + "&search_Item=" + searchQuery;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.Search_Function + pages,
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
                        }

                        //creating adapter object and setting it to recyclerview
                        if (!b) {
                            adapter = new Adapter_Video(VideoSearch.this, dataHandlerVideoInfoList, this.b, "state");
                            recyclerView.setLayoutManager(new LinearLayoutManager(VideoSearch.this));
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            Scroll();
                        } else {
                            adapter.notifyDataSetChanged();
                            loadmorePB.setVisibility(View.GONE);
                        }
                        page++;

                        if (adapter.getItemCount() < 1) {
                            nodata.setVisibility(View.VISIBLE);
                            nofile.setText("No videos found containg the text: "+searchQuery);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                        Toast.makeText(VideoSearch.this,
                                "Timeout error, Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(VideoSearch.this).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Scroll() {
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (isLastItemDisplaying(recyclerView)) {
                loadProducts(page, true);
            }
        });
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
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
