package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.Adapter_AdminCarePlans;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_AdminCarePlans;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

public class give_new_name extends Fragment {
    RecyclerView videorecycler;
    List<DataHandler_AdminCarePlans> dataHandler_adminCarePlans;
   public static String selectedtopic,selectedsub;
    String imageDelete;
    private static Dialog editcareplans;
    Uri selectedimg;
    TextView topicname;
    TextView dot;
    static ImageView careimg;
    private static final int REQUEST_CODE_GALLERY = 999;
    ProgressBar progressBar;
    ConstraintLayout nodata;
    ImageView postimg;
    int page = 1;
    Adapter_AdminCarePlans adapter;
    ArrayList<String> Headers = new ArrayList<String>();
    public static String id2,topic2,notes2,img2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_careplans, container, false);

        topicname = root.findViewById(R.id.textView54);

        getActivity().setTitle(selectedsub);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        dot= root.findViewById(R.id.textView80);
        videorecycler = root.findViewById(R.id.videorecycler2);
        progressBar = root.findViewById(R.id.progressBar8);
        progressBar.setVisibility(View.VISIBLE);
        nodata = root.findViewById(R.id.nodata);
        postimg = root.findViewById(R.id.postimg);
        dataHandler_adminCarePlans = new ArrayList<>();
        AppBarLayout appBarLayout =root.findViewById(R.id.appbarlayout);
        appBarLayout.setVisibility(View.GONE);
        topicname.setVisibility(View.GONE);
        editcareplans = new Dialog(getContext());
        dot.setVisibility(View.INVISIBLE);

        LoadHeaders();
        Sort(selectedtopic,selectedsub);
        //Scroll();

        if (id2 != null){
            ShowCarePlanEditDialog2(id2,topic2,notes2,img2);
            id2 = null;
        }
        return root;
    }

    private void Sort(String selectedtopic,String sub) {
        page = 1;
        dataHandler_adminCarePlans.clear();
        progressBar.setVisibility(View.VISIBLE);
        nodata.setVisibility(View.GONE);
        @SuppressLint("StaticFieldLeak")
        class AddComment extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("page", String.valueOf(page));
                params.put("header", selectedtopic);
                params.put("subheader", sub);
                return requestHandler.sendPostRequest(URLs.URL_getAdmincareplans, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    progressBar.setVisibility(View.GONE);

                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("Products");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject product = array.getJSONObject(i);
                        //adding the product to product list class
                        dataHandler_adminCarePlans.add(new DataHandler_AdminCarePlans(
                                product.getInt("id"),
                                product.getString("header"),
                                product.getString("topic"),
                                product.getString("img"),
                                product.getString("notes")
                        ));
                    }
                    //Collections.sort(dataHandler_adminCarePlans, (o1, o2) -> o1.getId()-o2.getId());

                    //creating adapter object and setting it to recyclerview
                    adapter = new Adapter_AdminCarePlans(getContext(), dataHandler_adminCarePlans);
                    videorecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    videorecycler.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    page++;

                    if (adapter.getItemCount() == 0) {
                        nodata.setVisibility(View.VISIBLE);
                        postimg.setImageResource(R.mipmap.nodata);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AddComment ul = new AddComment();
        ul.execute();
    }

    private void LoadHeaders() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_loadheader,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                if (Headers.contains(product.getString("header"))) {
                                    Headers.remove(product.getString("header"));
                                    Headers.add(product.getString("header"));
                                }else{
                                    Headers.add(product.getString("header"));
                                }
                            }
                           // Default();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    public void ShowCarePlanEditDialog2(String id, String topic, String notes, String img) {
        editcareplans.setContentView(R.layout.dialog_admincareplanedit);
        Objects.requireNonNull(editcareplans.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = editcareplans.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText notess;
        TextView imgselector,title,imagecancel2;
        imageDelete = null;

        submit = editcareplans.findViewById(R.id.button7);
        notess = editcareplans.findViewById(R.id.notestxt2);
        careimg = editcareplans.findViewById(R.id.imageView7);
        imgselector = editcareplans.findViewById(R.id.btncomment);
        title = editcareplans.findViewById(R.id.textView64);
        imagecancel2= editcareplans.findViewById(R.id.imagecancel2);

        if (!img.equals("http://nawa777.000webhostapp.com/uploads/null")) {
            Glide.with(Objects.requireNonNull(getContext())).
                    load(img).
                    thumbnail(0.11f).
                    into(careimg);
        }else{
            careimg.setImageResource(R.mipmap.nodata);
        }
        notess.setText(notes);
        title.setText(topic);
        notess.setMovementMethod(new ScrollingMovementMethod());
        imgselector.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        });
        imagecancel2.setOnClickListener(v -> {
            careimg.setImageDrawable(null);
            selectedimg = null;
            careimg.setImageResource(R.mipmap.nodata);
            imageDelete = "true";
        });
        submit.setOnClickListener(v -> {
            String getnotes = notess.getText().toString().trim();
            UpdateCarePlans(id, getnotes, imageDelete);
        });
        editcareplans.show();
    }

    private void UpdateCarePlans(String id, String notes, String imgdelete) {
        //progressBar.setVisibility(View.VISIBLE);
        //btn.setVisibility(View.INVISIBLE);
        File profile;
        Call<ServerResponse> call;
        MultipartBody.Part requestprofil  = null;
        try {
            profile = new File(getRealPathFromURI(selectedimg));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
            requestprofil = MultipartBody.Part.createFormData("img", profile.getName(), requestBody);
        } catch (Exception e) {}

        RequestBody Rnotes = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(notes));
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
        RequestBody imgafedelete = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(imgdelete));
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MediaHandler api = retrofit.create(MediaHandler.class);
        call = api.UpdateCarePlans(rid,Rnotes,requestprofil,imgafedelete);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                assert response.body() != null;
                if (!response.body().error) {
                    editcareplans.hide();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    imageDelete = null;
                    FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    FragmentTransaction fr = manager.beginTransaction();
                    fr.replace(R.id.flContent, new give_new_name());
                    fr.commit();
                   // progressBar.setVisibility(View.GONE);
                    //btn.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(),"Error while uploading account", Toast.LENGTH_LONG).show();
                    imageDelete = null;
                    //progressBar.setVisibility(View.GONE);
                   // btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
                InputStream inputStream = getActivity().getContentResolver().openInputStream(url);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                careimg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
