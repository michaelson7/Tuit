package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NursingResearch_UploadFrag extends Fragment {
    private Spinner topic;
    private EditText name, description;
    private Button submit;
    private TextView upload, filepath, title;
    ProgressBar progressBar;
    int FILE_REQUEST_CODE = 1;
    Uri selectedimg;
    final int usernameid = SharedPrefManager.getInstance(getContext()).getID();
    private String selectedtopic;
    ArrayList<String> Courses = new ArrayList<String>();
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_careplan_upload, container, false);

        getActivity().setTitle("Research Uploads");
        topic = root.findViewById(R.id.audience);
        name = root.findViewById(R.id.titletxt);
        description = root.findViewById(R.id.description);
        upload = root.findViewById(R.id.btncomment);
        filepath = root.findViewById(R.id.textView44);
        progressBar = root.findViewById(R.id.progressBar);
        submit = root.findViewById(R.id.btn);
        title = root.findViewById(R.id.textView18);

        LoadCourse();
        return root;
    }

    private void LoadCourse() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,URLs.get_course+schoolId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("Products");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                Courses.add(product.getString("coursename"));
                            }
                            Defaults();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void Defaults() {
        title.setText("Select Course");
        String[] status =  Courses.toArray(new String[0]);
        topic.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status));
        topic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedtopic = topic.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        upload.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
        });
        submit.setOnClickListener(v -> {
            UploadCarePlans();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FILE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DialogConfig dialogConfig = new DialogConfig.Builder()
                        .enableMultipleSelect(false) // default is false
                        .enableFolderSelect(false) // default is false
                        // .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android") // default is sdcard
                        .supportFiles(new SupportFile(".pdf", R.drawable.ic_bookshelf2)) // default is showing all file types.
                        .build();

                new FilePickerDialogFragment.Builder()
                        .configs(dialogConfig)
                        .onFilesSelected(list -> {
                            for (File file : list) {
                                filepath.setText(file.getAbsolutePath());
                                selectedimg = Uri.fromFile(file);
                            }
                        })
                        .build()
                        .show(getActivity().getSupportFragmentManager(), null);

            } else {
                Toast.makeText(getActivity(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void UploadCarePlans() {
        final String title = name.getText().toString().trim();
        final String descriptiontxt = description.getText().toString().trim();
        final int courseid = SharedPrefManager.getInstance(getContext()).getCourseid();
        File file;

        if (TextUtils.isEmpty(title)) {
            name.setError("Please enter name");
            name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(descriptiontxt)) {
            description.setError("Please enter description");
            description.requestFocus();
            return;
        }
        if (selectedimg == null) {
            Toast.makeText(getContext(), "Please upload pdf file", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = String.valueOf(selectedimg);
        String path_lastPart = path.substring(path.indexOf("/storage"));
        path_lastPart = path_lastPart.replace("%20", " ");
        file = new File(path_lastPart);
        progressBar.setVisibility(View.VISIBLE);
        submit.setVisibility(View.INVISIBLE);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(usernameid));
        RequestBody Uname = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody Udescription = RequestBody.create(MediaType.parse("text/plain"), descriptiontxt);
        RequestBody filetopic = RequestBody.create(MediaType.parse("application/pdf"), selectedtopic);
       // RequestBody Courseid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedcourseid));

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filerequest = MultipartBody.Part.createFormData("pdffile", file.getName(), requestBody);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        MediaHandler api = retrofit.create(MediaHandler.class);
        Call<ServerResponse> call = api.setResearchUpload(id, Uname, Udescription, filerequest, filetopic);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                assert response.body() != null;
                if (!response.body().error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Error while uploading files", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
