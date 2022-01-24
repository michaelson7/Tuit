package com.nkwazi_tech.tuit_app.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nkwazi_tech.tuit_app.group.SectionsPagerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupDiscussion_activity extends AppCompatActivity {

    FloatingActionButton fab;
    private Dialog GroupCreationDialog;
    private final int REQUEST_CODE_GALLERY = 999;
    private Uri selectedimg;
    CircleImageView img;
    ProgressBar progressBar;
    Button button2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //theme
        String ThemeState =  SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")){
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupdiscussion);

        fab = findViewById(R.id.fab);
        GroupCreationDialog = new Dialog(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Group Discussions");

        Defaults();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Defaults() {
        fab.setOnClickListener(view -> CreateGroup());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CreateGroup() {
        EditText name,description;
        Button imgselect;

        GroupCreationDialog.setContentView(R.layout.dialog_groupcreation);
        Objects.requireNonNull(GroupCreationDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = GroupCreationDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        progressBar = GroupCreationDialog.findViewById(R.id.progressBar12);
        name = GroupCreationDialog.findViewById(R.id.groupname);
        description = GroupCreationDialog.findViewById(R.id.groupdescription);
        img = GroupCreationDialog.findViewById(R.id.imageView9);
        imgselect = GroupCreationDialog.findViewById(R.id.propic2);
        button2 = GroupCreationDialog.findViewById(R.id.button2);

        progressBar.setVisibility(View.GONE);

        imgselect.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        });

        button2.setOnClickListener(v -> {
            String getname = name.getText().toString().trim();
            String getdescription = description.getText().toString().trim();
            String username = SharedPrefManager.getInstance(this).getUsername();

            if (selectedimg == null){
                Toast.makeText(this, "Select Group Image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(getname)) {
                name.setError("Please group enter name");
                name.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(getdescription)) {
                description.setError("Please group description");
                description.requestFocus();
                return;
            }

            UploadGroup(getname,getdescription,selectedimg,username);
        });

        GroupCreationDialog.show();
    }

    private void UploadGroup(String getname, String getdescription, Uri selectedimg, String usernames) {
        progressBar.setVisibility(View.VISIBLE);
        button2.setVisibility(View.INVISIBLE);
        //creating a file
        File profile = new File(getRealPathFromURI(selectedimg));
        //creating request body for file

        RequestBody groupname = RequestBody.create(MediaType.parse("text/plain"), getname);
        RequestBody groupdescription = RequestBody.create(MediaType.parse("text/plain"), getdescription);
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), usernames);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
        MultipartBody.Part requestGroupImg = MultipartBody.Part.createFormData("groupimg", profile.getName(), requestBody);

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
        Call<ServerResponse> call = api.uploadgroup(groupname, groupdescription, username,requestGroupImg);
        //finally performing the call
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    //Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                    //adding group name to firebase databse
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child("Groups").child(getname).setValue("")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        String name = SharedPrefManager.getInstance(getApplicationContext()).getUsername();
                                        Toast.makeText(getApplicationContext(),"Group Created...", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        button2.setVisibility(View.VISIBLE);
                                        GroupDiscussion_activity.this.finish();
                                        Join(getname);
                                        startActivity(new Intent( GroupDiscussion_activity.this, GroupDiscussion_activity.class));
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Error...", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        button2.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                    GroupCreationDialog.hide();

                } else {
                    progressBar.setVisibility(View.GONE);
                    button2.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Please check your internet connection",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),
                        "Please check your internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Join(String getname) {
        String topic = getname.replaceAll("\\s+","");
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg;
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                        }
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
                Toast.makeText(this, "You do not have permission to access file", Toast.LENGTH_SHORT).show();
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
                InputStream inputStream = this.getContentResolver().openInputStream(url);
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
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.search_bar:
                //start search dialog
                super.onSearchRequested();
                break;
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
        //onBackPressed();
        Intent intent = new Intent(GroupDiscussion_activity.this, Home_Activity.class);
        GroupDiscussion_activity.this.startActivity(intent);
        return true;


    }
}