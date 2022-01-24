package com.nkwazi_tech.tuit_app.Classes;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.VideoPlayer_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Adapter_Related_Video extends RecyclerView.Adapter<Adapter_Related_Video.ProductViewHolder> implements Filterable {

    private List<DataHandler_VideoInfo> dataHandlerVideoInfoList;
    private List<DataHandler_VideoInfo> SearchList;
    private Context mCtx;
    VideoPlayer_Frag fragment;
    private long downloadID;
    public static String encrypted_Name;
    public static String state;
    DownloadManager manager;
    Boolean b;

    public Adapter_Related_Video(Context mCtx, List<DataHandler_VideoInfo> dataHandlerVideoInfoList, boolean b, String state,VideoPlayer_Frag dialog) {
        this.mCtx = mCtx;
        this.dataHandlerVideoInfoList = dataHandlerVideoInfoList;
        SearchList = new ArrayList<>(dataHandlerVideoInfoList);
        this.b = b;
        Adapter_Related_Video.state =state;
        this.fragment = dialog;
    }



    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);

        if (state.equals("compact_card")){
            views = mInflater.inflate(R.layout.adapter_compact_video_list, parent, false);
        }else{
            views = mInflater.inflate(R.layout.adapter_videolist, parent, false);
        }

        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        String path = dataHandlerVideoInfoList.get(position).getVideo();
        String thubnail = dataHandlerVideoInfoList.get(position).getThumbnail();

        if (state.equals("compact_card")){

        }else{
            //disabling if not subscribed
            if (!b) {
                int mul = 0xFF7F7F7F;
                int add = 0x00000000;
                LightingColorFilter lcf = new LightingColorFilter(mul, add);
                holder.imageViewimg.setColorFilter(lcf);

                holder.constraintLayout.setEnabled(false);
                holder.download.setVisibility(View.INVISIBLE);
                holder.textViewTitle.setTextColor(Color.parseColor("#919191"));

                holder.constraintLayout.setOnClickListener(v -> {
                    Toast.makeText(mCtx, "Subscribe to view videos", Toast.LENGTH_SHORT).show();
                });
            }

            Glide.with(mCtx).
                    load(dataHandlerVideoInfoList.get(position).getImg()).
                    placeholder(R.mipmap.logo).
                    error(R.mipmap.logo).
                    thumbnail(0.11f).
                    into(holder.userimg);
            holder.time.setText(dataHandlerVideoInfoList.get(position).getVideoDuration());
        }

        Glide.with(mCtx)
                .asBitmap()
                .load(thubnail)
                .into(holder.imageViewimg);

        holder.textViewTitle.setText(dataHandlerVideoInfoList.get(position).getTitle());
        holder.textViewShortDesc.setText(dataHandlerVideoInfoList.get(position).getName() + " ∘ " +
                dataHandlerVideoInfoList.get(position).getViews() + " views ∘ " + dataHandlerVideoInfoList.get(position).getVideoSize());

        //downloading files
        holder.download.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(mCtx, holder.download);

            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.popup_download, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(items -> {
                if (items.getItemId() == R.id.download) {
                    try{
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

                        DownloadFile(path, dataHandlerVideoInfoList.get(position).getTitle() + ".mp4");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            });
            popup.show();//showing popup menu
        });

        holder.constraintLayout.setOnClickListener(v -> {
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
            fragment.dismiss();
        });
    }

    private void DownloadFile(String path, String title) {
        title = title.replace("/", "∘");
        Uri uri = Uri.parse(path);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "EducationalApp");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            request.setTitle(title);
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(false);
            request.setDestinationInExternalPublicDir("/EducationalApp", title);
            manager = (DownloadManager) Objects.requireNonNull(mCtx).getSystemService(Context.DOWNLOAD_SERVICE);
            assert manager != null;
            downloadID = manager.enqueue(request);
            //set encrypted name
            encrypted_Name = title.replace(".mp4", ".enc");

            DownloadBroadcastReceiver.downloadID = downloadID;
            DownloadBroadcastReceiver.video_Name = title;
            DownloadBroadcastReceiver.encrypted_Name = encrypted_Name;
        }
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

        ConstraintLayout constraintLayout;
        TextView textViewTitle, textViewShortDesc, time, download;
        ImageView imageViewimg, userimg;

        public ProductViewHolder(View itemView) {
            super(itemView);

            if (state.equals("compact_card")){
                constraintLayout = itemView.findViewById(R.id.relativeLayout);
                textViewTitle = itemView.findViewById(R.id.title);
                textViewShortDesc = itemView.findViewById(R.id.name);
                imageViewimg = itemView.findViewById(R.id.imageView);
                download = itemView.findViewById(R.id.imageButton);
            }else{
                constraintLayout = itemView.findViewById(R.id.relativeLayout);
                textViewTitle = itemView.findViewById(R.id.message);
                textViewShortDesc = itemView.findViewById(R.id.cources);
                imageViewimg = itemView.findViewById(R.id.imageView);
                userimg = itemView.findViewById(R.id.userimg);
                time = itemView.findViewById(R.id.textView72);
                download = itemView.findViewById(R.id.downloadbtn3);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return examplefilter;
    }

    private Filter examplefilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DataHandler_VideoInfo> filteredlist = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredlist.addAll(SearchList);
            } else {
                String filterPatttern = constraint.toString().toLowerCase().trim();
                for (DataHandler_VideoInfo item : SearchList) {
                    if (item.getName().toLowerCase().contains(filterPatttern)) {
                        filteredlist.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredlist;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataHandlerVideoInfoList.clear();
            dataHandlerVideoInfoList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
