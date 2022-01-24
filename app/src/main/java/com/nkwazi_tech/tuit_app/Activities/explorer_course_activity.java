package com.nkwazi_tech.tuit_app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nkwazi_tech.tuit_app.Classes.Adapter_Explorerlecturers;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Video;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
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

public class explorer_course_activity extends AppCompatActivity {
    List<DataHandler_User> dataHandler_User;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList;

    RecyclerView recyclerView, videorecycler;
    TextView views, subscription, nofile, lecturer_title;
    ProgressBar progressBar, loadmorePB;
    ConstraintLayout nodata;
    Adapter_Video adapter;

    public static String coursename;
    public static String courseida;
    String id = String.valueOf(SharedPrefManager.getInstance(this).getID());
    int page = 1;
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_course_activity);

        recyclerView = findViewById(R.id.recycler2);
        views = findViewById(R.id.textView2);
        videorecycler = findViewById(R.id.videorecycler2);
        progressBar = findViewById(R.id.progressBar8);
        progressBar.setVisibility(View.VISIBLE);
        loadmorePB = findViewById(R.id.loadmorePB);
        nofile = findViewById(R.id.textView40);
        nodata = findViewById(R.id.nodata);
        lecturer_title = findViewById(R.id.textView55);
        subscription = findViewById(R.id.textView78);
        dataHandler_User = new ArrayList<>();
        dataHandlerVideoInfoList = new ArrayList<>();
        views.setText(coursename);

        Check_Subscription(courseida, id);
        Default();
        Scroll();
    }

    private void Check_Subscription(String courseid, String id) {
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("courseid", String.valueOf(courseid));
                params.put("userID", String.valueOf(id));
                params.put("state", "explorer");
                return requestHandler.sendPostRequest(URLs.check_Subscription, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("state")) {
                        subscription.setVisibility(View.VISIBLE);
                        subscription.setText("Subscribe to access videos");
                        loadProducts(false);
                    } else {
                        subscription.setText("Unsubscribe");
                        loadProducts(true);
                    }
                    loadlecturer();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    private void Default() {
    }

    private void loadProducts(boolean b) {
        int courseid = Integer.parseInt(courseida);

        @SuppressLint("StaticFieldLeak")
        class AddComment extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("courseid", String.valueOf(courseid));
                params.put("page", String.valueOf(page));

                return requestHandler.sendPostRequest(URLs.URL_getCourseVideo, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);

                    JSONArray array = obj.getJSONArray("Products");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);

                        //adding the product to product list class
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
                    adapter = new Adapter_Video(explorer_course_activity.this, dataHandlerVideoInfoList, b, "state");
                    videorecycler.setLayoutManager(new LinearLayoutManager(explorer_course_activity.this));
                    videorecycler.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    page++;

                    if (adapter.getItemCount() == 0) {
                        nodata.setVisibility(View.VISIBLE);
                        nofile.setText("No Videos Uploaded");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AddComment ul = new AddComment();
        ul.execute();
    }


    private void loadlecturer() {
        int courseid = Integer.parseInt(courseida);
        @SuppressLint("StaticFieldLeak")
        class AddComment extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("courseid", String.valueOf(courseid));
                return requestHandler.sendPostRequest(URLs.URL_getCourseLecturer, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);

                    JSONArray array = obj.getJSONArray("Products");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);

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
                                    product.getString("practisenumber"),
                                    product.getString("lecturercourse"),
                                    product.getInt("schoolId")
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Adapter_Explorerlecturers adapter = new Adapter_Explorerlecturers(explorer_course_activity.this, dataHandler_User, "data");
                    LinearLayoutManager layoutManager = new LinearLayoutManager(explorer_course_activity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                    if (adapter.getItemCount() < 1) {
                        lecturer_title.setVisibility(View.INVISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AddComment ul = new AddComment();
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
        videorecycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isLastItemDisplaying(videorecycler)) {
                    //Calling the method getdata again
                    loadMore(page);
                }
            }
        });
    }

    private void loadMore(int page3) {
        int courseid = Integer.parseInt(courseida);

        @SuppressLint("StaticFieldLeak")
        class AddComment extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("courseid", String.valueOf(courseid));
                params.put("page", String.valueOf(page3));

                return requestHandler.sendPostRequest(URLs.URL_getCourseVideo, params);
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
                    JSONObject obj = new JSONObject(s);

                    JSONArray array = obj.getJSONArray("Products");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);

                        //adding the product to product list class
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

                    adapter.notifyDataSetChanged();
                    loadmorePB.setVisibility(View.GONE);
                    page++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        AddComment ul = new AddComment();
        ul.execute();
        adapter.notifyDataSetChanged();
    }
}