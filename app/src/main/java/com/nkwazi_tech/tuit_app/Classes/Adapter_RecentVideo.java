package com.nkwazi_tech.tuit_app.Classes;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.VideoPlayer_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.crypto.NoSuchPaddingException;

import static com.nkwazi_tech.tuit_app.Classes.Adapter_Video.findFilesForId;

public class Adapter_RecentVideo extends RecyclerView.Adapter<Adapter_RecentVideo.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_VideoInfo> dataHandlerVideoInfoList;
    String data;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    public Adapter_RecentVideo(Context mCtx, List<DataHandler_VideoInfo> dataHandlerVideoInfoList, String data) {
        this.mCtx = mCtx;
        this.dataHandlerVideoInfoList = dataHandlerVideoInfoList;
        this.data = data;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_recentvideolist, parent, false);
        // click listener here
        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Glide.with(mCtx)
                .asBitmap()
                .load(dataHandlerVideoInfoList.get(position).getThumbnail())
                .into(holder.imageViewimg);

        //string limiter
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(22);
        holder.textViewShortDesc.setFilters(filterArray);

        if (data.equals("false")) {
            holder.recyclerView.setEnabled(false);
        }

        holder.textViewTitle.setText(dataHandlerVideoInfoList.get(position).getName());
        holder.textViewShortDesc.setText(dataHandlerVideoInfoList.get(position).getTitle());
        holder.recyclerView.setOnClickListener(v -> {
            DataHandler_VideoPlayerInfo.getInstance(mCtx).DataHandler_VideoPlayerInfo(
                    dataHandlerVideoInfoList.get(position).getTitle(),
                    dataHandlerVideoInfoList.get(position).getDescription(),
                    dataHandlerVideoInfoList.get(position).getVideo(),
                    dataHandlerVideoInfoList.get(position).getName(),
                    dataHandlerVideoInfoList.get(position).getImg(),
                    dataHandlerVideoInfoList.get(position).getLikes(),
                    dataHandlerVideoInfoList.get(position).getComments(),
                    dataHandlerVideoInfoList.get(position).getViews(),
                    dataHandlerVideoInfoList.get(position).getId(),
                    dataHandlerVideoInfoList.get(position).getTags()
            );
            VideoPlayer_Frag.views_Num = String.valueOf(dataHandlerVideoInfoList.get(position).getViews());
            VideoPlayer_Frag.duration_Num = dataHandlerVideoInfoList.get(position).getVideoDuration();
            VideoPlayer_Frag.date_Num = dataHandlerVideoInfoList.get(position).getTimestamp();

            String ThemeState = SharedPrefManager.getInstance(mCtx).getTheme();
            if (ThemeState != null && ThemeState.equals("dark")) {
                DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                dialogFragment.show(((AppCompatActivity) mCtx).getSupportFragmentManager(), "tag");
            } else {
                DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                dialogFragment.show(((AppCompatActivity) mCtx).getSupportFragmentManager(), "tag");
            }
        });

        holder.download_btn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(mCtx, holder.download_btn);

            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.popup_download, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(items -> {
                if (items.getItemId() == R.id.download) {
                    try {
                        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mCtx,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // this will request for permission when user has not granted permission for the app
                            ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                        Toast.makeText(mCtx, "Downloading...", Toast.LENGTH_LONG).show();
                        File folder = new File(Environment.getExternalStorageDirectory() +
                                File.separator + "EducationalApp");
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }

                        String path = dataHandlerVideoInfoList.get(position).getVideo();
                        DownloadReceiver.data(dataHandlerVideoInfoList.get(position).getTitle().trim(),".mp4",path,mCtx);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            });
            popup.show();//showing popup menu
        });
    }


    @Override
    public int getItemCount() {
        return dataHandlerVideoInfoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        CardView recyclerView;
        TextView textViewTitle, textViewShortDesc, download_btn;
        //VideoView imageView;
        ImageView imageViewimg;

        public ProductViewHolder(View itemView) {
            super(itemView);

            download_btn = itemView.findViewById(R.id.downloadbtn2);
            recyclerView = itemView.findViewById(R.id.cardView4);
            textViewTitle = itemView.findViewById(R.id.cources);
            textViewShortDesc = itemView.findViewById(R.id.message);
            imageViewimg = itemView.findViewById(R.id.imageView);
        }
    }


}
