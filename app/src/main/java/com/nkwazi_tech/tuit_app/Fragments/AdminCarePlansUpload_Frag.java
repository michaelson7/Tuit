package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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

import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class AdminCarePlansUpload_Frag extends Fragment {
TextView imgselector,imgremove;
    private Spinner topics;
    private EditText header, notes,subheader;
    private String selectedtopics;
    ImageView img;
    private final int FILE_REQUEST_CODE = 1;
    private final int REQUEST_CODE_GALLERY = 999;
    private int order = 1;
    private Button btn;
    ProgressBar progressBar;
    Uri selectedimg;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_admincareplans, container, false);

        getActivity().setTitle("CarePlans Upload");
        topics = root.findViewById(R.id.topictxt);
        header = root.findViewById(R.id.headertxt);
        notes = root.findViewById(R.id.notestxt);
        btn = root.findViewById(R.id.button8);
        subheader= root.findViewById(R.id.headertxt3);
        imgremove = root.findViewById(R.id.imagecancel);
        imgselector= root.findViewById(R.id.btncomment);
        progressBar = root.findViewById(R.id.progressBar18);
        img = root.findViewById(R.id.imageView7);
        notes.setMovementMethod(new ScrollingMovementMethod());

        String[] status = {"Nursing Problems", "Nursing Diagnosis", "Aim/Goals/Objectives", "Interventions", "Evaluations"};
        topics.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status));
        topics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedtopics = topics.getSelectedItem().toString();
                if (selectedtopics.equals("Nursing Problems")){
                    order = 1;
                }else if(selectedtopics.equals("Nursing Diagnosis")){
                    order = 2;
                }else if(selectedtopics.equals("Aim/Goals/Objectives")){
                    order = 3;
                }else if(selectedtopics.equals("Interventions")){
                    order = 4;
                }else if(selectedtopics.equals("Evaluations")){
                    order = 5;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgselector.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        });
        imgremove.setOnClickListener(v -> {
            img.setImageDrawable(null);
            selectedimg = null;
        });

        btn.setOnClickListener(v -> {
            String getHeader = header.getText().toString();
            String getnotes = notes.getText().toString();
            String getsubheader = subheader.getText().toString();

            if (TextUtils.isEmpty(getHeader)) {
                header.setError("Please enter Header");
                header.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(getsubheader)) {
                header.setError("Please enter SubHeader");
                header.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(getnotes)) {
                notes.setError("Please enter Notes");
                notes.requestFocus();
                return;
            }
            Uploadwithfile(getHeader,getnotes,selectedtopics,selectedimg,getsubheader);
        });

        return root;
    }

    private void Uploadwithfile(String getHeader, String getnotes, String selectedtopics, Uri selectedimg, String getsubheader) {
        progressBar.setVisibility(View.VISIBLE);
        btn.setVisibility(View.INVISIBLE);
        File profile;
        Call<ServerResponse> call;
        MultipartBody.Part requestprofil  = null;

        try {
            profile = new File(getRealPathFromURI(selectedimg));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
            requestprofil = MultipartBody.Part.createFormData("img", profile.getName(), requestBody);
        } catch (Exception e) {}

        RequestBody Rheader = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(getHeader));
        RequestBody Rnotes = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(getnotes));
        RequestBody Rtopics = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedtopics));
        RequestBody Rsubheader = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(getsubheader));
        RequestBody Rorder = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(order));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MediaHandler api = retrofit.create(MediaHandler.class);
        call = api.addCarePlans(Rheader, Rtopics, Rnotes,requestprofil,Rsubheader,Rorder);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getContext(),response.body().message, Toast.LENGTH_LONG).show();
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
                img.setImageBitmap(bitmap);
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
