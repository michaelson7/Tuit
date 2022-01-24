package com.nkwazi_tech.tuit_app.Classes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Chat extends RecyclerView.Adapter<Adapter_Chat.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    String mine;
    private Dialog imgview;
    private List<DataHandler_Chat> mChat;
    String imgurl;

    public Adapter_Chat(Context mCtx, List<DataHandler_Chat> mChat, String imgurl) {
        this.mCtx = mCtx;
        this.mChat = mChat;
        this.imgurl = imgurl;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        imgview = new Dialog(mCtx);
        if (viewType == MSG_TYPE_RIGHT) {
            mine = "true";
            views = mInflater.inflate(R.layout.adapter_chat_right, parent, false);
            // click listener here
            return new ProductViewHolder(views);
        } else {
            mine = "false";
            views = mInflater.inflate(R.layout.adapter_chat_left, parent, false);
            // click listener here
            return new ProductViewHolder(views);
        }
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        DataHandler_Chat chat = mChat.get(position);
        holder.showmessage.setText(chat.getMessage());

        if (mine.equals("true")){
            holder.showmessage.setOnLongClickListener(v -> {
                String[] options2 = new String[]{"Delete"};
                String[] SortyBy2 = options2;
                androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(mCtx,R.style.AlertDialog);
                builder2.setTitle("Select Option");
                builder2.setItems(SortyBy2, (dialog2, which2) -> {
                    if (which2 == 0) {
                        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Groups").child(imgurl).child(chat.getMessageid());
                        dR.removeValue();
                        Toast.makeText(mCtx, "Message Deleted", Toast.LENGTH_LONG).show();
                    }
                });
                builder2.show();
                return false;
            });
        }

        holder.image.setOnLongClickListener(v -> {
            String[] options2 = new String[]{"Delete"};
            String[] SortyBy2 = options2;
            androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(mCtx,R.style.AlertDialog);
            builder2.setTitle("Select Option");
            builder2.setItems(SortyBy2, (dialog2, which2) -> {
                if (which2 == 0) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Groups").child(imgurl).child(chat.getMessageid());
                    dR.removeValue();
                    Toast.makeText(mCtx, "Image Deleted", Toast.LENGTH_LONG).show();
                }
            });
            builder2.show();
            return false;
        });

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txtseen.setText("Seen");
            } else {
                holder.txtseen.setText("Delivered");
            }
        } else {
            holder.txtseen.setVisibility(View.GONE);
        }

        String userimg = chat.getProfilephoto();
        if (!userimg.contains("/null")) {
            Glide.with(mCtx.getApplicationContext())
                    .asBitmap()
                    .load(chat.getProfilephoto())
                    .into(holder.profileimg);
        }else{
            holder.profileimg.setImageResource(R.mipmap.girl);
        }

        holder.username.setText(chat.getName());

        String state = chat.getImage();
        if (state.equals("false")) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.showmessage.setVisibility(View.GONE);
            Glide.with(mCtx.getApplicationContext())
                    .asBitmap()
                    .load(chat.getMessage())
                    .into(holder.image);
            holder.image.setOnClickListener(v -> {
                String imgpath = chat.getMessage();
                OpenImg(imgpath);
            });
        }
    }

    private void OpenImg(String imgpath) {
        imgview.setContentView(R.layout.dialog_imgview);
        Objects.requireNonNull(imgview.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = imgview.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView titles, downloadbtn;
        ImageView img;

        titles = imgview.findViewById(R.id.userpost);
        downloadbtn = imgview.findViewById(R.id.downloadbtn);
        img = imgview.findViewById(R.id.postimg);

        titles.setVisibility(View.GONE);
        Glide.with(mCtx)
                .load(imgpath)
                .into(img);
        downloadbtn.setOnClickListener(v -> {
            Toast.makeText(mCtx, "Downloading...", Toast.LENGTH_LONG).show();
            if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mCtx,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // this will request for permission when user has not granted permission for the app
                ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Uri uri = Uri.parse(imgpath);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                int min = 111111111;
                int max = 999999980;
                int random = new Random().nextInt((max - min) + 1) + min;
                String num = String.valueOf(random);
                request.setTitle(num + ".jpg");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, num+".jpg");
                DownloadManager manager = (DownloadManager) Objects.requireNonNull(mCtx).getSystemService(Context.DOWNLOAD_SERVICE);
                assert manager != null;
                manager.enqueue(request);
            }

        });

        imgview.show();
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView showmessage;
        CircleImageView profileimg;
        TextView txtseen, username;

        public ProductViewHolder(View itemView) {
            super(itemView);
            showmessage = itemView.findViewById(R.id.showmessage);
            profileimg = itemView.findViewById(R.id.profileimg);
            txtseen = itemView.findViewById(R.id.seen);
            username = itemView.findViewById(R.id.txtname);
            image = itemView.findViewById(R.id.imageView13);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String Username = SharedPrefManager.getInstance(mCtx).getUsername();

        if (mChat.get(position).getSender().equals(Username)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

        //get profile img and name


    }
}
