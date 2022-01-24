package com.nkwazi_tech.tuit_app.Classes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Adapter_News extends RecyclerView.Adapter<Adapter_News.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_News> dataHandler_news;
    private Dialog imgview;

    public Adapter_News(Context mCtx, List<DataHandler_News> dataHandler_news) {
        this.mCtx = mCtx;
        this.dataHandler_news = dataHandler_news;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_news, parent, false);
        imgview = new Dialog(mCtx);
        // click listener here
        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String profilephoto = dataHandler_news.get(position).getProfilepicture();
        String img = dataHandler_news.get(position).getFile();
        String accountType = SharedPrefManager.getInstance(mCtx).getAccounttype();

        if (!accountType.equals("student")){
            holder.delete.setVisibility(View.VISIBLE);
        }

        if (!img.contains("/null")) {
            Glide.with(mCtx)
                    .asBitmap()
                    .load(dataHandler_news.get(position).getFile())
                    .into(holder.img);
        } else {
            holder.img.setVisibility(View.GONE);
        }

        Glide.with(mCtx)
                .load(profilephoto)
                .placeholder(R.mipmap.girl)
                .error(R.mipmap.girl)
                .into(holder.lecturerimg);

        holder.name.setText(" To: " + dataHandler_news.get(position).getTo() + "\n From: " + dataHandler_news.get(position).getLecturername() + "\n Subject: " +
                dataHandler_news.get(position).getSubject());
        holder.Mbody.setText(dataHandler_news.get(position).getMbody());
        holder.time.setText(dataHandler_news.get(position).getTime());
        holder.delete.setOnClickListener(v -> DeleteNews(dataHandler_news.get(position).getId(),position));
        holder.img.setOnClickListener(v -> {
            String title = "Post by " + dataHandler_news.get(position).getLecturername();
            String imgpath = dataHandler_news.get(position).getFile();
            OpenImg(title, imgpath);
        });

    }

    private void DeleteNews(int id,int position) {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_deletnews, params);
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
                        Toast.makeText(mCtx, response, Toast.LENGTH_SHORT).show();
                        dataHandler_news.remove(position);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void OpenImg(String title, String imgpath) {
        imgview.setContentView(R.layout.dialog_imgview);
        Objects.requireNonNull(imgview.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = imgview.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView titles, downloadbtn;
        ImageView img;

        titles = imgview.findViewById(R.id.userpost);
        downloadbtn = imgview.findViewById(R.id.downloadbtn);
        img = imgview.findViewById(R.id.postimg);

        titles.setText(title);
        Glide.with(mCtx)
                .load(imgpath)
                .into(img);

        downloadbtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mCtx,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // this will request for permission when user has not granted permission for the app
                ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Toast.makeText(mCtx, "Downloading...", Toast.LENGTH_LONG).show();
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
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, num + ".jpg");
                DownloadManager manager = (DownloadManager) Objects.requireNonNull(mCtx).getSystemService(Context.DOWNLOAD_SERVICE);
                assert manager != null;
                manager.enqueue(request);
            }
        });

        imgview.show();
    }

    @Override
    public int getItemCount() {
        return dataHandler_news.size();
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

        TextView Mbody, time, name, delete;
        ImageView lecturerimg, img;

        public ProductViewHolder(View itemView) {
            super(itemView);

            delete = itemView.findViewById(R.id.imagecancel3);
            Mbody = itemView.findViewById(R.id.post_text_content);
            name = itemView.findViewById(R.id.textView46);
            time = itemView.findViewById(R.id.post_video_time);
            lecturerimg = itemView.findViewById(R.id.post_video_img);
            img = itemView.findViewById(R.id.imageView8);
        }
    }
}
