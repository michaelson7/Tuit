package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class NewsUpload_Frag extends Fragment {
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
    private Spinner audience;
    private EditText subject, description;
    private TextView textViewPrice4;
    private String selectedaudience;
    private final int FILE_REQUEST_CODE = 1, REQUEST_CODE_GALLERY = 999;
    private ImageView newsimg;
    private Button btn;
    ProgressBar progressBar;
    Uri selectedimg;
    ArrayList<String> Courses = new ArrayList<String>();
    DateFormat dateFormat = new SimpleDateFormat("d MMM yyy\nHH:MM");
    String time = dateFormat.format(Calendar.getInstance().getTime());

    final String name = SharedPrefManager.getInstance(getContext()).getName();
    final String username = SharedPrefManager.getInstance(getContext()).getUsername();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_news_upload, container, false);

        getActivity().setTitle("News Upload");
        audience = root.findViewById(R.id.audience);
        subject = root.findViewById(R.id.subject);
        description = root.findViewById(R.id.description);
        audience = root.findViewById(R.id.audience);
        newsimg = root.findViewById(R.id.imageView7);
        btn = root.findViewById(R.id.btn);
        textViewPrice4 = root.findViewById(R.id.btncomment);
        progressBar = root.findViewById(R.id.progressBar);

        LoadCourses();
        return root;
    }

    private void LoadCourses() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getcourse+schoolId,
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
                            Courses.add("Students Taking: " + product.getString("coursename"));
                        }
                        Courses.add("Students Taking: All Courses");

                        //Load page when courses are loaded
                        Defaults();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void Defaults() {
        String[] status;
        status = Courses.toArray(new String[0]);
        audience.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status));
        audience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedaudience = audience.getSelectedItem().toString();
                ((TextView) audience.getChildAt(0)).setTextSize(12);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        textViewPrice4.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        });

        btn.setOnClickListener(v -> {
            if (selectedimg != null) {
                Uploadwithfile(selectedimg);
            } else {
                Upload();
            }
        });
    }

    private void Uploadwithfile(Uri selectedimg) {
        final String audiencetxt = selectedaudience;
        final String subjecttxt = subject.getText().toString().trim();
        final String descriptiontxt = description.getText().toString().trim();

        if (TextUtils.isEmpty(subjecttxt)) {
            subject.setError("Please enter subject");
            subject.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(descriptiontxt)) {
            description.setError("Please enter description");
            description.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btn.setVisibility(View.INVISIBLE);

        File file = new File(getRealPathFromURI(selectedimg));

        RequestBody audiencetxtbody = RequestBody.create(MediaType.parse("text/plain"), audiencetxt);
        RequestBody subjecttxtbody = RequestBody.create(MediaType.parse("text/plain"), subjecttxt);
        RequestBody descriptiontxtbody = RequestBody.create(MediaType.parse("text/plain"), descriptiontxt);
        RequestBody usernamebody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody namebody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody timebody = RequestBody.create(MediaType.parse("text/plain"), time);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        MediaHandler api = retrofit.create(MediaHandler.class);

        //creating a call and calling the upload image method
        Call<ServerResponse> call = api.updatenews(audiencetxtbody, subjecttxtbody, descriptiontxtbody, usernamebody,
                namebody, timebody, fileToUpload);

        //finally performing the call
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);
                    sendNotification("News Added by: " + name, subjecttxt, "global");

                } else {
                    Snackbar.make(Objects.requireNonNull(getView()), "Error while uploading account", Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void Upload() {
        final String audiencetxt = selectedaudience;
        final String subjecttxt = subject.getText().toString().trim();
        final String descriptiontxt = description.getText().toString().trim();

        if (TextUtils.isEmpty(subjecttxt)) {
            subject.setError("Please enter subject");
            subject.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(descriptiontxt)) {
            description.setError("Please enter StudentModule");
            description.requestFocus();
            return;
        }

        @SuppressLint("StaticFieldLeak")
        class UploadNews extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("audience", audiencetxt);
                params.put("subject", subjecttxt);
                params.put("description", descriptiontxt);
                params.put("username", username);
                params.put("name", name);
                params.put("time", time);
                params.put("img", "null");
                params.put("file", "null");

                return requestHandler.sendPostRequest(URLs.URL_uploadnews, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                btn.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        sendNotification("News Added by: " + name, subjecttxt, "global");
                    } else {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UploadNews ru = new UploadNews();
        ru.execute();
    }

    private void sendNotification(String message, String getTitle, String global) {
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("title", message);
                params.put("message", getTitle);
                params.put("push_type", "topic");
                params.put("groupname", global);
                params.put("click_event", "ShopFragment");
                params.put("intent", "news");

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_postnotifications, params);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FILE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*application/pdf*");
                intent = Intent.createChooser(intent, "Select PDF");
                startActivityForResult(intent, FILE_REQUEST_CODE);
            } else {
                Toast.makeText(getActivity(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getActivity(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
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
                newsimg.setImageBitmap(bitmap);
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
