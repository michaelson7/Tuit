package com.nkwazi_tech.tuit_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoStats extends AppCompatActivity {

    BarChart barChart;
    TextView Username, Email, tvideo, tlikes, tviews, payment, previouspay, previouspay2;
    CircleImageView propic;
    String ThemeState;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> dates;
    ProgressBar progressBar;
    ScrollView scrollView;
    int week1, week2, week3, week4, week5 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stats);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Video Stats");

        Username = findViewById(R.id.usernametxt2);
        Email = findViewById(R.id.course2);
        tvideo = findViewById(R.id.usernametxt5z);
        tlikes = findViewById(R.id.usernametxt5d);
        tviews = findViewById(R.id.usernametxt5);
        payment = findViewById(R.id.course3);
        propic = findViewById(R.id.userimga2);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView3);
        barChart = findViewById(R.id.bargraphy);
        previouspay = findViewById(R.id.ppPayment);
        previouspay2 = findViewById(R.id.ppayment);
        barEntries = new ArrayList<>();
        dates = new ArrayList<>();

        Defaults();
        LoadStats();
    }

    private void Defaults() {
        scrollView.setVisibility(View.GONE);
        String profileimg = SharedPrefManager.getInstance(getApplicationContext()).getPropic();

        Glide.with(Objects.requireNonNull(getApplicationContext()))
                .asBitmap()
                .placeholder(R.mipmap.girl)
                .error(R.mipmap.girl)
                .load(SharedPrefManager.getInstance(getApplicationContext()).getPropic())
                .into(propic);

        Username.setText(SharedPrefManager.getInstance(getApplicationContext()).getName());
        Email.setText(SharedPrefManager.getInstance(getApplicationContext()).getEmail());

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        if (ThemeState != null && ThemeState.equals("dark")) {
            barChart.getAxisLeft().setTextColor(Color.parseColor("#FFFFFF"));
            barChart.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));
            barChart.getLegend().setTextColor(Color.parseColor("#FFFFFF"));

        } else {
            barChart.getAxisLeft().setTextColor(Color.parseColor("#000000"));
            barChart.getXAxis().setTextColor(Color.parseColor("#000000"));
            barChart.getLegend().setTextColor(Color.parseColor("#000000"));
        }
        previouspay.setVisibility(View.GONE);
        previouspay2.setVisibility(View.GONE);
    }

    private void LoadStats() {
        //first getting the values
        final int lecturerid = SharedPrefManager.getInstance(VideoStats.this).getID();
        //if everything is fine
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("lecturerid", String.valueOf(lecturerid));
                return requestHandler.sendPostRequest(URLs.URL_getstats, params);
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
                scrollView.setVisibility(View.VISIBLE);
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getString("previouspayment").equals("NULL")) {
//                        previouspay.setVisibility(View.VISIBLE);
//                        previouspay2.setVisibility(View.VISIBLE);
//                        previouspay.setText("K: " + obj.getString("previouspayment"));
                    }

                    tvideo.setText(obj.getString("totalvideos"));

                    if (obj.getString("totallikes").equals("null")){
                        tlikes.setText("0");
                    }else{
                        tlikes.setText(obj.getString("totallikes"));
                    }
                    if (obj.getString("totolviews").equals("null")){
                        tviews.setText("0");
                    }else{
                        tviews.setText(obj.getString("totolviews"));
                    }
                    payment.setText("K: " + obj.getString("payment"));

                    //chart
                    if (!obj.getString("Week1Views").equals("null")) {
                        week1 = Integer.parseInt(obj.getString("Week1Views"));
                    }
                    if (!obj.getString("Week2Views").equals("null")) {
                        week2 = Integer.parseInt(obj.getString("Week2Views"));
                    }
                    if (!obj.getString("Week3Views").equals("null")) {
                        week3 = Integer.parseInt(obj.getString("Week3Views"));
                    }
                    if (!obj.getString("Week4Views").equals("null")) {
                        week4 = Integer.parseInt(obj.getString("Week4Views"));
                    }
                    if (!obj.getString("Week5Views").equals("null")) {
                        week5 = Integer.parseInt(obj.getString("Week5Views"));
                    }

                    barEntries.add(new BarEntry(week1, 0));
                    barEntries.add(new BarEntry(week2, 1));
                    barEntries.add(new BarEntry(week3, 2));
                    barEntries.add(new BarEntry(week4, 3));
                    barEntries.add(new BarEntry(week5, 4));
                    BarDataSet barDataSet = new BarDataSet(barEntries, "Views");

                    dates.add("Week 1");
                    dates.add("Week 2");
                    dates.add("Week 3");
                    dates.add("Week 4");
                    dates.add("Week 5");

                    BarData barData = new BarData(dates, barDataSet);
                    barChart.setData(barData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.search_bar:
                Toast.makeText(this, "item2 pressed", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.nav_toolbar, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
