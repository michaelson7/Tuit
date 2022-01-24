package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.nkwazi_tech.tuit_app.Classes.Adapter_Course;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Dash_Courselist;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Explorer;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_MyCourse;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

import static android.app.Activity.RESULT_OK;

public class Admin_CourseIconEdit_Frag extends Fragment {

    private List<DataHandler_MyCourse> dataHandler_myCourses;
    RecyclerView recyclerView;
    private Dialog CourseUpdate;
    List<DataHandler_Explorer> data_handlerExplorers;
    private final int REQUEST_CODE_GALLERY = 999;
    Uri selectedimg;
    ProgressBar progressBar;
    public static String image,type=null;
    ImageView img;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_explorer, container, false);
        recyclerView = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar6);
        progressBar.setVisibility(View.VISIBLE);
        data_handlerExplorers = new ArrayList<>();
        dataHandler_myCourses = new ArrayList<>();
        CourseUpdate = new Dialog(getContext());

        if (type.equals("true")){
            load_Student_Cources();
        }else{
            loadCources();
        }

        if (image != null) {
            CourseUpdate.setContentView(R.layout.dialog_courseupdate);
            Objects.requireNonNull(CourseUpdate.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            Window window = CourseUpdate.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            Button imgchange,update;
            img = CourseUpdate.findViewById(R.id.imageView9);
            update = CourseUpdate.findViewById(R.id.button2);
            imgchange = CourseUpdate.findViewById(R.id.propic2);

            imgchange.setOnClickListener(v -> {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            });
            update.setOnClickListener(v -> {
                if (selectedimg == null){
                    Toast.makeText(getContext(), "Please select course image", Toast.LENGTH_SHORT).show();
                    return;
                }
                UpdateCourse(image,selectedimg);

            });
            CourseUpdate.show();
        }
        return root;
    }

    private void load_Student_Cources() {
        dataHandler_myCourses.add(new DataHandler_MyCourse(
                "Nursing\n\n"
        ));
        dataHandler_myCourses.add(new DataHandler_MyCourse(
                "Clinical\nMedicine\n(coming soon)"
        ));
        dataHandler_myCourses.add(new DataHandler_MyCourse(
                "Psychosocial Counselling\n(coming soon)"
        ));

        //creating adapter object and setting it to recyclerview
        Adapter_Dash_Courselist adapter = new Adapter_Dash_Courselist(getContext(), dataHandler_myCourses, "true");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void loadCources() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveCources+schoolId,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {

                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);

                            //adding the product to product list class
                            data_handlerExplorers.add(new DataHandler_Explorer(
                                    product.getInt("courseid"),
                                    product.getInt("subscription_price"),
                                    product.getString("coursename"),
                                    product.getString("courseimg")
                            ));
                        }

                        Adapter_Course adapter = new Adapter_Course(getContext(), data_handlerExplorers, "true");
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                        Toast.makeText(getContext(),
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getContext(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri url = data.getData();
            selectedimg = url;
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(url);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UpdateCourse(String ids, Uri selectedimg) {
        File profile = null;
        Call<ServerResponse> call = null;
        MultipartBody.Part requestGroupImg  = null;

        try {
            profile = new File(getRealPathFromURI(selectedimg));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
              requestGroupImg = MultipartBody.Part.createFormData("courseimg", profile.getName(), requestBody);
        } catch (Exception e) {}

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), ids);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //creating our api
        MediaHandler api = retrofit.create(MediaHandler.class);

        call = api.updatecourseimg(id,requestGroupImg);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    CourseUpdate.hide();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    FragmentTransaction fr = manager.beginTransaction();
                    fr.replace(R.id.flContent, new Admin_CourseIconEdit_Frag());
                    fr.commit();
                    image = null;
                } else {
                    //progressBar.setVisibility(View.GONE);
                    //button2.setVisibility(View.VISIBLE);
                    CourseUpdate.hide();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
