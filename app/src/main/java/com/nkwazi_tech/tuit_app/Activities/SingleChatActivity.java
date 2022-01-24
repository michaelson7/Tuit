package com.nkwazi_tech.tuit_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Chat;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Chat;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_FirebaseUsers;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Notifications.APIService;
import com.nkwazi_tech.tuit_app.Notifications.Client;
import com.nkwazi_tech.tuit_app.Notifications.Data;
import com.nkwazi_tech.tuit_app.Notifications.MyResponse;
import com.nkwazi_tech.tuit_app.Notifications.Sender;
import com.nkwazi_tech.tuit_app.Notifications.Token;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleChatActivity extends AppCompatActivity {
    EditText Message;
    CircleImageView groupimg;
    TextView groupname,send;
    RecyclerView recyclerView;
    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    Adapter_Chat adapter_chat;
    private List<DataHandler_Chat> mChat;

    ValueEventListener seenlistner;

    APIService apiService;
    boolean notify = false;

    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //theme
        String ThemeState =  SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")){
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        groupimg = findViewById(R.id.groupimg);
        groupname = findViewById(R.id.groupname);
        Message = findViewById(R.id.textmessage);
        send = findViewById(R.id.btnsend);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();

        userid = intent.getStringExtra("userid");
        Defaults();
        getToekn();

    }

    private void Defaults() {
        Glide.with(this)
                .load(SharedPrefManager.getInstance(this).getPropic())
                .into(groupimg);
        //groupname.setText(SharedPrefManager.getInstance(this).getUsername());


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataHandler_FirebaseUsers users = dataSnapshot.getValue(DataHandler_FirebaseUsers.class);
                groupname.setText(users.getUsername());

                String img = SharedPrefManager.getInstance(getApplicationContext()).getPropic();
                readmessages(fuser.getUid(), userid, img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(v -> {
            notify = true;
            String message = Message.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                Message.setError("Message Field Cannot Be Empty");
                Message.requestFocus();
                return;
            } else {
                sendmessage(fuser.getUid(), userid, message);
                Message.setText("");
            }
        });

        seenMessage(userid);
    }

    private void sendmessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataHandler_FirebaseUsers user = dataSnapshot.getValue(DataHandler_FirebaseUsers.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + msg, "New Message",
                            userid);

                    Sender sender = new Sender();

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1) {
                                            Toast.makeText(SingleChatActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readmessages(final String myid, final String userid, final String imgeurl) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataHandler_Chat chat = snapshot.getValue(DataHandler_Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChat.add(chat);
                    }

                    adapter_chat = new Adapter_Chat(SingleChatActivity.this, mChat, imgeurl);
                    recyclerView.setAdapter(adapter_chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getToekn() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SingleChatActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                Token token = new Token(newToken);
                reference.child(fuser.getUid()).setValue(token);

            }
        });
    }

    private void seenMessage(String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenlistner = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataHandler_Chat chat = snapshot.getValue(DataHandler_Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenlistner);
    }
}
