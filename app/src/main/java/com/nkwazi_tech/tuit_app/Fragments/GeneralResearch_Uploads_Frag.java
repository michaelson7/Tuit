package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

public class GeneralResearch_Uploads_Frag extends Fragment {

    private EditText name, description,topic;
    private Button submit;
    private TextView upload,filepath;
    ProgressBar progressBar;
    int FILE_REQUEST_CODE = 1;
    Uri selectedimg;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_generalresearch_upload, container, false);

        getActivity().setTitle("General Research Uploads");
        topic = root.findViewById(R.id.titletxt2);
        name = root.findViewById(R.id.titletxt);
        description = root.findViewById(R.id.description);
        upload = root.findViewById(R.id.btncomment);
        filepath = root.findViewById(R.id.textView44);
        progressBar = root.findViewById(R.id.progressBar);
        submit = root.findViewById(R.id.btn);
        Defaults();

        return root;
    }

    private void Defaults() {
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
        final String header = topic.getText().toString().trim();
        final String subheading = name.getText().toString().trim();
        final String notes = description.getText().toString().trim();
        final int courseid = SharedPrefManager.getInstance(getContext()).getCourseid();

        File file = null;

        if (TextUtils.isEmpty(header)) {
            topic.setError("Please enter header");
            topic.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(subheading)) {
            name.setError("Please enter subheading");
            name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(notes)) {
            description.setError("Please enter notes");
            description.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        submit.setVisibility(View.INVISIBLE);

        MultipartBody.Part requestprofil = null, requestcover = null;
        File profile;
        Call<ServerResponse> call;

        try {
            String path = String.valueOf(selectedimg);
            String path_lastPart = path.substring(path.indexOf("/storage"));
            path_lastPart = path_lastPart.replace("%20", " ");
            file = new File(path_lastPart);

            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            requestprofil = MultipartBody.Part.createFormData("pdffile", file.getName(), requestBody);
        } catch (Exception e) {}

        RequestBody getheader = RequestBody.create(MediaType.parse("text/plain"), header);
        RequestBody getsub = RequestBody.create(MediaType.parse("text/plain"), subheading);
        RequestBody getnotes = RequestBody.create(MediaType.parse("text/plain"), notes);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MediaHandler api = retrofit.create(MediaHandler.class);
        call = api.addGeneralResearch(getheader, getsub, getnotes,requestprofil);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getContext(),response.body().message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
