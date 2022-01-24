package com.nkwazi_tech.tuit_app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Chat;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Chat;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.Notifications.APIService;
import com.nkwazi_tech.tuit_app.Notifications.Client;
import com.nkwazi_tech.tuit_app.Notifications.Token;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupChat_activity extends AppCompatActivity {
    EditText Message;
    CircleImageView groupimg;
    TextView groupname, send;
    private ProgressDialog p;
    private Dialog GroupCreationDialog, suspensionDialog, ViewProfile;
    RecyclerView recyclerView;
    MenuItem sorting;
    ProgressBar progressBar;
    private Uri selectedpro;
    FirebaseUser fuser;
    ImageView img;
    TextView imgselecter;
    String[] isadmin;
    Button imgselect, button2;
    private final int REQUEST_CODE_GALLERY = 999;
    DatabaseReference UsersRef, GroupNameRef, GroupMessageKetRef;
    Intent intent;
    Adapter_Chat adapter_chat;
    private List<DataHandler_Chat> mChat;
    String GroupName;
    String GroupImg;
    String GroupNameNew;
    String CurrentUserId;
    String checker = "";
    String admin;
    String uploadid, uploadimage;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //theme
        String ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GroupCreationDialog = new Dialog(this);
        suspensionDialog = new Dialog(this);
        ViewProfile = new Dialog(this);
        imgselecter = findViewById(R.id.btnsend3);
        groupimg = findViewById(R.id.groupimg);
        groupname = findViewById(R.id.groupname);
        Message = findViewById(R.id.textmessage);
        //imgsend = findViewById(R.id.textmessage);
        send = findViewById(R.id.btnsend);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        GroupName = intent.getStringExtra("GroupName");
        GroupNameNew = intent.getStringExtra("new");
        GroupImg = intent.getStringExtra("GroupImg");
        admin = intent.getStringExtra("Admin");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName);
        try {
            CurrentUserId = fuser.getUid();
        } catch (Exception e) {
            CurrentUserId = "oJaVuaAPPyZio8aQ025xXRD8ByP2";
            e.printStackTrace();
        }

        Defaults();
        getToekn();
        CheckSuspension(GroupName);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Defaults() {
        Glide.with(this)
                .load(GroupImg)
                .into(groupimg);

        groupname.setText(GroupNameNew);
        groupimg.setOnClickListener(v -> {
            ShowProfile(admin);
        });
        UsersRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(v -> {
            String message = Message.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                Message.setError("Message Field Cannot Be Empty");
                Message.requestFocus();
                return;
            } else {
                sendmessage(message, SharedPrefManager.getInstance(getApplicationContext()).getUsername(),
                        SharedPrefManager.getInstance(getApplicationContext()).getName(),
                        SharedPrefManager.getInstance(getApplicationContext()).getPropic());
                Message.setText("");
            }
        });

        imgselecter.setOnClickListener(v -> {
            checker = "images";
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        });
    }

    private void sendmessage(String message, String Username, String Name, String Photo) {
        String messagekey = GroupNameRef.push().getKey();
        HashMap<String, Object> groupMessageKey = new HashMap<>();
        GroupNameRef.updateChildren(groupMessageKey);
        GroupMessageKetRef = GroupNameRef.child(messagekey);

        HashMap<String, Object> MessageInfoApp = new HashMap<>();
        MessageInfoApp.put("sender", Username);
        MessageInfoApp.put("receiver", GroupName);
        MessageInfoApp.put("message", message);
        MessageInfoApp.put("date", "10:20");
        MessageInfoApp.put("name", Name);
        MessageInfoApp.put("profilephoto", Photo);
        MessageInfoApp.put("image", "false");
        MessageInfoApp.put("imageid", "null");
        MessageInfoApp.put("messageid", messagekey);
        GroupMessageKetRef.updateChildren(MessageInfoApp);

        String topic = GroupName.replaceAll("\\s+", "");
        sendNotification(GroupName + ": " + Username, message, "global");
    }

    private void DisplayMessages() {
        mChat = new ArrayList<>();

        GroupNameRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupName);
        GroupNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataHandler_Chat chat = snapshot.getValue(DataHandler_Chat.class);
                    mChat.add(chat);

                    adapter_chat = new Adapter_Chat(GroupChat_activity.this, mChat, GroupName);
                    recyclerView.setAdapter(adapter_chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getToekn() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(GroupChat_activity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                Token token = new Token(newToken);
                try {
                    reference.child(fuser.getUid()).setValue(token);
                } catch (Exception e) {
                    reference.child("oJaVuaAPPyZio8aQ025xXRD8ByP2").setValue(token);
                    e.printStackTrace();
                }

            }
        });
    }

    private void sendNotification(String title, String message, String group) {

        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("push_type", "topic");
                params.put("groupname", group);
                params.put("click_event", "ShopFragment");
                params.put("intent", "group_message");

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

    private void CheckSuspension(String groupName) {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("groupName", groupName);
                return requestHandler.sendPostRequest(URLs.URL_checksuspension, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    String suspension = obj.getString("suspension");
                    if (suspension.equals("true")) {
                        ShowSuspensionDialog(obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void ShowSuspensionDialog(String message) {
        suspensionDialog.setContentView(R.layout.dialog_termsandconditions);
        Objects.requireNonNull(suspensionDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = suspensionDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView title, text;
        title = suspensionDialog.findViewById(R.id.textView3);
        text = suspensionDialog.findViewById(R.id.textView13);

        title.setText("Group Suspended");
        text.setText(message);
        text.setGravity(Gravity.CENTER);
        //mDrawer.setVisibility(View.GONE);
        text.setTextSize(18);

        suspensionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startActivity(new Intent(GroupChat_activity.this, GroupDiscussion_activity.class));
            }
        });
        suspensionDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void ShowProfile(String admin) {
        ViewProfile.setContentView(R.layout.dialog_profileview);
        Objects.requireNonNull(ViewProfile.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = ViewProfile.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView text;
        CircleImageView imageView;

        text = ViewProfile.findViewById(R.id.groupdescription);
        imageView = ViewProfile.findViewById(R.id.imageView9);

        Glide.with(this)
                .load(GroupImg)
                .into(imageView);
        text.setText("Group Name: " + GroupNameNew + "\n\nDescription: " + intent.getStringExtra("Description") + "\n\nAdmin: " +
                admin);
        ViewProfile.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    DisplayMessages();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    DisplayMessages();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(GroupChat_activity.this, GroupDiscussion_activity.class);
        GroupChat_activity.this.startActivity(intent);
        //sorting.setVisible(false);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.search_bar:
                Toast.makeText(GroupChat_activity.this, "Exit!", Toast.LENGTH_LONG).show();
                break;
            case R.id.sorting:
                String Admin = intent.getStringExtra("Admin");
                String currentuser = SharedPrefManager.getInstance(getApplicationContext()).getUsername();

                if (Admin.equals(currentuser)) {
                    isadmin = new String[]{"Exit Group", "Update Group", "Delete Group"};
                } else {
                    isadmin = new String[]{"Exit Group"};
                }
                String[] SortyBy = isadmin;
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialog);
                builder.setTitle("Select Option");
                builder.setItems(SortyBy, (dialog, which) -> {
                    if (which == 0) {
                        String[] Option = {"Yes,Exit Group", "Cancel"};
                        androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialog);
                        builder2.setTitle("Are you sure you want to exit group?");
                        builder2.setItems(Option, (dialog2, which2) -> {
                            if (which2 == 0) {
                                ExitGroup();  //DeleteGroup
                            } else if (which2 == 1) {
                            }
                        });
                        builder2.show();
                    } else if (which == 1) {
                        updategroup();
                    } else if (which == 2) {
                        String[] Option = {"Yes,Delete Group", "Cancel"};
                        androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialog);
                        builder2.setTitle("Are you sure you want to delete group?");
                        builder2.setItems(Option, (dialog2, which2) -> {
                            if (which2 == 0) {
                                DeleteGroup();
                            } else if (which2 == 1) {
                            }
                        });
                        builder2.show();
                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updategroup() {
        EditText name, description;

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

        Glide.with(this)
                .load(GroupImg)
                .into(img);

        String GroupNameNewx = intent.getStringExtra("new");
        name.setText(GroupNameNewx);
        description.setText(intent.getStringExtra("Description"));
        progressBar.setVisibility(View.GONE);
        button2.setText("Update Group");

        imgselect.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        });

        button2.setOnClickListener(v -> {
            String getname = name.getText().toString().trim();
            String getdescription = description.getText().toString().trim();
            String username = SharedPrefManager.getInstance(this).getName();

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
            UploadGroup(getname, getdescription, selectedpro, username);
        });

        GroupCreationDialog.show();
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
            if (checker == "") {
                Uri url = data.getData();
                selectedpro = url;
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(url);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    img.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                checker = "";
                Uri url = data.getData();
                SendImg(url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void SendImg(Uri url) {
        String usernames = SharedPrefManager.getInstance(this).getEmail();
        p = new ProgressDialog(this);
        p.setMessage("Uploading Image...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();

        File profile = null;
        Call<ServerResponse> call = null;
        MultipartBody.Part requestGroupImg  = null;
        try {
            profile = new File(getRealPathFromURI(url));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
            requestGroupImg = MultipartBody.Part.createFormData("file", profile.getName(), requestBody);
        } catch (Exception e) {}

        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), usernames);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //creating our api
        MediaHandler api = retrofit.create(MediaHandler.class);

        call = api.sendimg(username, requestGroupImg);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                    uploadid = String.valueOf(response.body().id);
                    uploadimage = String.valueOf(response.body().image);
                    SendMessageFile(uploadid, uploadimage);
                    p.hide();
                } else {
                    p.hide();
                    Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                p.hide();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SendMessageFile(String uploadid, String uploadimage) {
        String messagekey = GroupNameRef.push().getKey();
        String Username = SharedPrefManager.getInstance(this).getUsername();
        String Name = SharedPrefManager.getInstance(this).getName();
        String Photo = SharedPrefManager.getInstance(this).getPropic();

        HashMap<String, Object> groupMessageKey = new HashMap<>();
        GroupNameRef.updateChildren(groupMessageKey);
        GroupMessageKetRef = GroupNameRef.child(messagekey);

        HashMap<String, Object> MessageInfoApp = new HashMap<>();
        MessageInfoApp.put("sender", Username);
        MessageInfoApp.put("receiver", GroupName);
        MessageInfoApp.put("message", uploadimage);
        MessageInfoApp.put("date", "10:20");
        MessageInfoApp.put("name", Name);
        MessageInfoApp.put("profilephoto", Photo);
        MessageInfoApp.put("image", "true");
        MessageInfoApp.put("imageid", uploadid);
        MessageInfoApp.put("messageid", messagekey);
        GroupMessageKetRef.updateChildren(MessageInfoApp);

        String topic = GroupName.replaceAll("\\s+", "");
        sendNotification(GroupName + ": " + Username, "Image Uploaded", topic);
    }

    private void UploadGroup(String getname, String getdescription, Uri selectedpro, String usernames) {
        progressBar.setVisibility(View.VISIBLE);
        button2.setVisibility(View.INVISIBLE);
        String state = intent.getStringExtra("GroupName");
        File profile = null;
        Call<ServerResponse> call = null;
        MultipartBody.Part requestGroupImg = null;

        try {
            profile = new File(getRealPathFromURI(selectedpro));

            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
             requestGroupImg = MultipartBody.Part.createFormData("groupimg", profile.getName(), requestBody);
        } catch (Exception e) {
        }

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), GroupName);
        RequestBody groupname = RequestBody.create(MediaType.parse("text/plain"), getname);
        RequestBody groupdescription = RequestBody.create(MediaType.parse("text/plain"), getdescription);
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), usernames);
        RequestBody states = RequestBody.create(MediaType.parse("text/plain"), state);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //creating our api
        MediaHandler api = retrofit.create(MediaHandler.class);

        if (selectedpro != null) {
            call = api.updategroup(name, groupname, groupdescription, requestGroupImg, states);
        } else {
            call = api.updategroupNI(name, groupname, groupdescription, states);
        }

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                    GroupCreationDialog.hide();
                    Intent intent = new Intent(GroupChat_activity.this, GroupDiscussion_activity.class);
                    GroupChat_activity.this.startActivity(intent);
                } else {
                    progressBar.setVisibility(View.GONE);
                    button2.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ExitGroup() {
        //first getting the values
        final String username = SharedPrefManager.getInstance(this).getUsername();
        //if everything is fine

        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("groupname", GroupName);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_exitgroup, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //  progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // progressBar.setVisibility(View.GONE);
                try {
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        //getting the dataHandlerUser from the response
                        String response = obj.getString("message");
                        Toast.makeText(GroupChat_activity.this, response, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GroupChat_activity.this, GroupDiscussion_activity.class);
                        GroupChat_activity.this.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void DeleteGroup() {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("groupname", GroupName);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_deletegroup, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //  progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // progressBar.setVisibility(View.GONE);
                try {
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        //getting the dataHandlerUser from the response
                        String response = obj.getString("message");
                        Toast.makeText(GroupChat_activity.this, response, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GroupChat_activity.this, GroupDiscussion_activity.class);
                        GroupChat_activity.this.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.nav_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        sorting = menu.findItem(R.id.sorting);

        searchItem.setVisible(false);
        sorting.setVisible(true);

        return true;
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

}
