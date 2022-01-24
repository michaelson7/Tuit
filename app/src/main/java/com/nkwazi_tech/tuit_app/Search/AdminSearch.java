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
import com.nkwazi_tech.tuit_app.Activities.AdminActivity;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Admin_GroupMod;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Admin_UserMod;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Group_all_list;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
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

public class AdminSearch extends AppCompatActivity {
    List<DataHandler_User> dataHandler_User;
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    RecyclerView recyclerView;
    ProgressBar progressBar, loadmorePB;
    //ConstraintLayout nodata;
    ImageView postimg;
    int page = 1;
    Adapter_Admin_UserMod adapter;
    Adapter_Admin_GroupMod adapter1;
    ConstraintLayout nodata;
    public static String approval;
    ArrayList<String> Headers = new ArrayList<String>();
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(this).getSchoolId();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        String ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Admin Search");

        recyclerView = findViewById(R.id.recycler2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataHandler_User = new ArrayList<>();
        dataHandler_group_all_lists = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar16);
        loadmorePB = findViewById(R.id.loadmorePB);
        nodata = findViewById(R.id.nodata);
        progressBar.setVisibility(View.VISIBLE);
        postimg = findViewById(R.id.postimg);


        if (approval != null){
            for (int i = 0; i < 10; i++) {
                LoadGroup(i);
            }
        }else{
            for (int i = 0; i < 10; i++) {
                LoadGroups(i);
            }
        }

        // Scroll();
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
            String searchQuery = Objects.requireNonNull(intent.getStringExtra(SearchManager.QUERY)).trim();

            Filterdata(searchQuery);

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String selectedSuggestionRowId = intent.getDataString();
            //execution comes here when an item is selected from search suggestions
            //you can continue from here with user selected search item
            Toast.makeText(this, "selected search suggestion " + selectedSuggestionRowId,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void Filterdata(String searchQuery) {
        if (approval != null){
            ArrayList<DataHandler_User> filteredlist = new ArrayList<>();
            for (DataHandler_User item : dataHandler_User) {
                if (item.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredlist.add(item);
                }
            }
            adapter.filterlist(filteredlist);
        }else{
            ArrayList<DataHandler_Group_all_list> filteredlist = new ArrayList<>();
            for (DataHandler_Group_all_list item : dataHandler_group_all_lists) {
                if (item.getGroupName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredlist.add(item);
                }
            }
            adapter1.filterlist(filteredlist);
        }

    }


    private void LoadGroup(int pagenumber) {
        //first getting the values
        final int lecturerID = SharedPrefManager.getInstance(AdminSearch.this).getID();
        //if everything is fine
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                if (approval.equals("true")) {
                    params.put("approval", "true");
                }
                params.put("page", String.valueOf(pagenumber));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_GetUsers, params);
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
                        if (approval.equals("true")) {
                            if (Headers.contains(product.getString("username"))) {
                                Headers.remove(product.getString("username"));
                                Headers.add(product.getString("username"));
                            }else{
                                Headers.add(product.getString("username"));
                                try {
                                    dataHandler_User.add(new DataHandler_User(
                                            product.getInt("id"),
                                            product.getInt("courseid"),
                                            product.getString("email"),
                                            product.getString("phonenumber"),
                                            product.getString("name"),
                                            product.getString("propic"),
                                            product.getString("coverpic"),
                                            product.getString("accounttype"),
                                            product.getString("vcode"),
                                            product.getString("lecturercourse"),
                                            product.getInt("schoolId")
                                    ));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                dataHandler_User.add(new DataHandler_User(
                                        product.getInt("id"),
                                        product.getInt("courseid"),
                                        product.getString("email"),
                                        product.getString("phonenumber"),
                                        product.getString("name"),
                                        product.getString("propic"),
                                        product.getString("coverpic"),
                                        product.getString("accounttype"),
                                        product.getString("vcode"),
                                        product.getString("lecturercourse"),
                                        product.getInt("schoolId")
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (approval.equals("true")) {
                        adapter = new Adapter_Admin_UserMod(AdminSearch.this, dataHandler_User, "Approval");
                    } else {
                        adapter = new Adapter_Admin_UserMod(AdminSearch.this, dataHandler_User, "Edit");
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(AdminSearch.this));
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    page++;
                    handleSearch();

                    if (adapter.getItemCount() == 0) {
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

    private void LoadGroups(int i) {
        approval = null;
            String pages = "&page=" + i;
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
                                adapter1 = new Adapter_Admin_GroupMod(AdminSearch.this, dataHandler_group_all_lists);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AdminSearch.this));
                                recyclerView.setAdapter(adapter1);
                                page++;
                                handleSearch();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AdminSearch.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            //adding our stringrequest to queue
            Volley.newRequestQueue(AdminSearch.this).add(stringRequest);
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
    public void onBackPressed() {
        if (approval.equals("true")){
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("action", "Approval");
            this.startActivity(intent);
        }else if (approval.equals("false")){
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("action", "ModifyUser");
            this.startActivity(intent);
        }else{
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("action", "ModifyGroups");
            this.startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (approval == null) {
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("action", "ModifyGroups");
            this.startActivity(intent);
        }
        else if (approval.equals("true")){
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("action", "Approval");
            this.startActivity(intent);
        }else if (approval.equals("false")){
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("action", "ModifyUser");
            this.startActivity(intent);
        }
        //onBackPressed();
        return true;
    }
}
