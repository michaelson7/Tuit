package com.nkwazi_tech.tuit_app.Search;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.nkwazi_tech.tuit_app.Classes.Adapter_VideoEdit;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class VideoEditSearch extends AppCompatActivity {
    RecyclerView recyclerView;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList = new ArrayList<>();
    ProgressBar progressBar,loadmorePB;
    Adapter_VideoEdit adapter;
    int page = 1;
    ConstraintLayout nodata;
    ImageView postimg;

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
        nodata=  findViewById(R.id.nodata);
        postimg= findViewById(R.id.postimg);
        progressBar.setVisibility(View.VISIBLE);

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
        final int lecturerID = SharedPrefManager.getInstance(this).getID();

        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("lecturerid", String.valueOf(lecturerID));
                params.put("page", String.valueOf(pagenumber));

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

                    adapter = new Adapter_VideoEdit(VideoEditSearch.this, dataHandlerVideoInfoList, "Edit");
                    recyclerView.setLayoutManager(new LinearLayoutManager(VideoEditSearch.this));
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    page++;
                    handleSearch();
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
        final int lecturerID = SharedPrefManager.getInstance(VideoEditSearch.this).getID();
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
