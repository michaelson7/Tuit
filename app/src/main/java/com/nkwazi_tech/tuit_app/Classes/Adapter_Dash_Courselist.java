package com.nkwazi_tech.tuit_app.Classes;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.Admin_CourseIconEdit_Frag;
import com.nkwazi_tech.tuit_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Adapter_Dash_Courselist extends RecyclerView.Adapter<Adapter_Dash_Courselist.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_MyCourse> dataHandler_myCourses;
    private List<DataHandler_MyCourse3> dataHandler_myCourses3;
    public static Dialog Cources;
    String data;
    private Dialog CourseUpdate;
    public static Uri selectedimg;
    public static ImageView carouselimg;

    public Adapter_Dash_Courselist(Context mCtx, List<DataHandler_MyCourse> dataHandler_myCourses, String data) {
        this.mCtx = mCtx;
        this.data = data;
        this.dataHandler_myCourses = dataHandler_myCourses;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_courselist, parent, false);
        Cources = new Dialog(mCtx);
        dataHandler_myCourses3 = new ArrayList<>();
        CourseUpdate = new Dialog(mCtx);
        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String course = dataHandler_myCourses.get(position).getCourse();
        holder.course.setText(dataHandler_myCourses.get(position).getCourse());
        holder.course.setBackgroundResource(R.drawable.txt_card_news);

        course =  course.replaceAll(System.getProperty("line.separator"), "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.fetch_student_img+"&name="+course,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        String img = obj.getString("img");

                        Glide.with(mCtx).
                                load(img).
                                placeholder(R.mipmap.logo).
                                error(R.mipmap.logo).
                                thumbnail(0.11f).
                                into(holder.courseimage);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(mCtx, error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                        Toast.makeText(mCtx,
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(mCtx).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (data.equals("true")) {
            holder.card.setOnClickListener(v -> {
                String courses = dataHandler_myCourses.get(position).getCourse();
                courses =  courses.replaceAll(System.getProperty("line.separator"), "");

                Show_Mod_Dialog(courses);
            });
        } else {
            holder.card.setOnClickListener(v -> {
                ShowCourseDialoge(dataHandler_myCourses.get(position).getCourse().replaceAll(System.getProperty("line.separator"), ""));
            });
        }
    }

    private void Show_Mod_Dialog(String course) {
        CourseUpdate.setContentView(R.layout.dialog_courseupdate);
        Objects.requireNonNull(CourseUpdate.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = CourseUpdate.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button imgchange, update;
        carouselimg = CourseUpdate.findViewById(R.id.imageView9);
        update = CourseUpdate.findViewById(R.id.button2);
        imgchange = CourseUpdate.findViewById(R.id.propic2);

        imgchange.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mCtx,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // this will request for permission when user has not granted permission for the app
                ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            ((Activity) mCtx).startActivityForResult(intent, 1);
        });

        update.setOnClickListener(v -> {
            if (selectedimg == null) {
                Toast.makeText(mCtx, "Please select course image", Toast.LENGTH_SHORT).show();
                return;
            }
            UpdateCourse(course, selectedimg);
        });
        CourseUpdate.show();
    }

    private void ShowCourseDialoge(String course) {
        dataHandler_myCourses3.clear();
        TextView year;
        RecyclerView corselist;

        Cources.setContentView(R.layout.dialog_dashcourse);
        Objects.requireNonNull(Cources.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = Cources.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        year = Cources.findViewById(R.id.textView3);
        corselist = Cources.findViewById(R.id.textView13);

        year.setText(course);

        if (course.equals("Nursing")) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getcourse,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //progressBar.setVisibility(View.GONE);
                            try {
                                //converting the string to json array object
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("Products");

                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = array.getJSONObject(i);

                                    //adding the product to product list class
                                    dataHandler_myCourses3.add(new DataHandler_MyCourse3(
                                            product.getInt("courseid"),
                                            product.getString("coursename"),
                                            ""
                                    ));
                                }
                                Adapter_StudentDialogCourse adapter = new Adapter_StudentDialogCourse(mCtx, dataHandler_myCourses3);
                                corselist.setLayoutManager(new LinearLayoutManager(mCtx));
                                corselist.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mCtx, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

            //adding our stringrequest to queue
            Volley.newRequestQueue(mCtx).add(stringRequest);
        }
        Cources.show();
    }

    private void UpdateCourse(String ids, Uri selectedimg) {
        File profile = null;
        Call<ServerResponse> call = null;
        MultipartBody.Part requestGroupImg = null;

        try {
            profile = new File(getRealPathFromURI(selectedimg));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
            requestGroupImg = MultipartBody.Part.createFormData("courseimg", profile.getName(), requestBody);
        } catch (Exception e) {
        }

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), ids.trim());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //creating our api
        MediaHandler api = retrofit.create(MediaHandler.class);

        call = api.update_Student_courseimg(id, requestGroupImg);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    CourseUpdate.hide();
                    Toast.makeText(mCtx, response.body().message, Toast.LENGTH_LONG).show();
                    FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                    FragmentTransaction fr = manager.beginTransaction();
                    fr.replace(R.id.flContent, new Admin_CourseIconEdit_Frag());
                    fr.commit();
                } else {
                    Toast.makeText(mCtx, response.body().message, Toast.LENGTH_LONG).show();
                }
                Cources.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(mCtx, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public int getItemCount() {
        return dataHandler_myCourses.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView course;
        CardView card;
        ImageView courseimage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.coursename);
            card = itemView.findViewById(R.id.layout);
            courseimage = itemView.findViewById(R.id.courseimage);
        }
    }
}
