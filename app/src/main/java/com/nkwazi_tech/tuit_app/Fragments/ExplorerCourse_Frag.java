package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Course;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Explorerlecturers;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Video;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Explorer;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExplorerCourse_Frag extends Fragment {

    List<DataHandler_User> dataHandler_User;
    List<DataHandler_VideoInfo> dataHandlerVideoInfoList;
    Dialog sub_Dialog, pay_Response_Dialog;
    RecyclerView recyclerView, videorecycler;
    TextView views, subscription, nofile, lecturer_title;
    ProgressBar progressBar, loadmorePB;
    ConstraintLayout nodata;
    Adapter_Video adapter;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public static String coursename,courseida,imagecourse,price,balance;
    String id = String.valueOf(SharedPrefManager.getInstance(getContext()).getID());
    int page = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_explorercource, container, false);
        recyclerView = root.findViewById(R.id.recycler2);
        views = root.findViewById(R.id.textView2);
        videorecycler = root.findViewById(R.id.videorecycler2);
        progressBar = root.findViewById(R.id.progressBar8);
        progressBar.setVisibility(View.VISIBLE);
        loadmorePB = root.findViewById(R.id.loadmorePB);
        nofile = root.findViewById(R.id.textView40);
        nodata = root.findViewById(R.id.nodata);
        lecturer_title = root.findViewById(R.id.textView55);
        subscription = root.findViewById(R.id.textView78);
        dataHandler_User = new ArrayList<>();
        dataHandlerVideoInfoList = new ArrayList<>();
        views.setText(coursename);
        sub_Dialog = new Dialog(getContext());
        pay_Response_Dialog= new Dialog(getContext());
        Check_Subscription(courseida, id);
        getBalance();
        Default();
        Scroll();
        return root;
    }

    private void getBalance() {
        String data = "&state=load_available_courses&userid=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveCources + data+schoolId,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        //getting account balance
                        balance = obj.getString("balance");
                        if (balance.equals("null")) {
                            balance = "0";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(getContext()).add(stringRequest);
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
         subscription.setOnClickListener(v -> {
             Subscribe_Dialog(imagecourse, price, coursename, courseida);
         });

    }

    private void Subscribe_Dialog(String imagecourse, String price, String coursename, String id) {

        sub_Dialog.setContentView(R.layout.dialog_subscription);
        Objects.requireNonNull(sub_Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = sub_Dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView subText, title, C_price, balance_txt;
        ImageView img;
        Button submit;
        ProgressBar progressBar;

        subText = sub_Dialog.findViewById(R.id.title3);
        C_price = sub_Dialog.findViewById(R.id.title8);
        img = sub_Dialog.findViewById(R.id.imageView3);
        submit = sub_Dialog.findViewById(R.id.button2);
        title = sub_Dialog.findViewById(R.id.title7);
        progressBar = sub_Dialog.findViewById(R.id.progressBar12);
        balance_txt = sub_Dialog.findViewById(R.id.title4);

        int mul = 0xFF7F7F7F;
        int add = 0x00000000;
        LightingColorFilter lcf = new LightingColorFilter(mul, add);
        img.setColorFilter(lcf);
        Glide.with(Objects.requireNonNull(getContext()))
                .asBitmap()
                .load(imagecourse)
                .placeholder(R.mipmap.logo)
                .into(img);

        title.setText(coursename);
        balance_txt.setText("Availabe Balance: K-" + balance.toString());

        C_price.setText("K" + price + "/Month");
        subText.setText("Are you sure you want to subscribe to " + coursename + " for K-" + price + "?\n\n" +
                "Subscription will be valid for 30 days");

        submit.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            submit.setVisibility(View.INVISIBLE);

            String userID = String.valueOf(SharedPrefManager.getInstance(getContext()).getID());
            String name = String.valueOf(SharedPrefManager.getInstance(getContext()).getName());
            String email = String.valueOf(SharedPrefManager.getInstance(getContext()).getEmail());

            SubscribeUser(userID, id, name, email);
        });

        sub_Dialog.show();
    }

    private void SubscribeUser(String userID, String id, String name, String email) {
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("userID", String.valueOf(userID));
                params.put("courseid", String.valueOf(id));
                params.put("name", String.valueOf(name));
                params.put("email", String.valueOf(email));
                params.put("nrc_num", String.valueOf(userID));
                params.put("state", String.valueOf(false));

                //returing the response
                return requestHandler.sendPostRequest(URLs.Course_Subscription, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        String new_balance = obj.getString("balance");
                        String message = obj.getString("message");
                        sub_Dialog.dismiss();
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();

                        ShowSuccessDialog(Integer.parseInt(price), message);

                    } else {
                        sub_Dialog.dismiss();
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    private void ShowSuccessDialog(int amount, String new_balance) {
        pay_Response_Dialog.setContentView(R.layout.dialog_paymeant_response);
        Objects.requireNonNull(pay_Response_Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = pay_Response_Dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView dates, names, emails, amounts, times, refrenceCode;
        CircleImageView imageView;

        dates = pay_Response_Dialog.findViewById(R.id.usernametxt28);
        names = pay_Response_Dialog.findViewById(R.id.usernametxt30);
        emails = pay_Response_Dialog.findViewById(R.id.usernametxt31);
        amounts = pay_Response_Dialog.findViewById(R.id.usernametxt33);
        times = pay_Response_Dialog.findViewById(R.id.usernametxt36);
        imageView = pay_Response_Dialog.findViewById(R.id.imageView13);
        refrenceCode = pay_Response_Dialog.findViewById(R.id.usernametxt38);

        String imgPath = SharedPrefManager.getInstance(getContext()).getPropic();
        String name = SharedPrefManager.getInstance(getContext()).getName();
        String email = SharedPrefManager.getInstance(getContext()).getEmail();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        Glide.with(getContext())
                .asBitmap()
                .load(imgPath)
                .error(R.mipmap.girl)
                .placeholder(R.mipmap.girl)
                .into(imageView);
        names.setText(name);
        emails.setText(email);
        amounts.setText("K~" + amount);
        times.setText(currentTime);
        dates.setText(currentDate);

        refrenceCode.setText(new_balance);

        pay_Response_Dialog.setOnDismissListener(dialog -> {
            FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            FragmentTransaction fr = manager.beginTransaction();
            fr.replace(R.id.flContent, new ExplorerCourse_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });

        pay_Response_Dialog.show();
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
                    adapter = new Adapter_Video(getContext(), dataHandlerVideoInfoList, b, "state");
                    videorecycler.setLayoutManager(new LinearLayoutManager(getContext()));
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

                    Adapter_Explorerlecturers adapter = new Adapter_Explorerlecturers(getContext(), dataHandler_User, "data");
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
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
