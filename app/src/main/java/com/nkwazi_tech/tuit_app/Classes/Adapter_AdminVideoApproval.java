package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.VideoPlayer_Frag;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter_AdminVideoApproval extends RecyclerView.Adapter<Adapter_AdminVideoApproval.ProductViewHolder> {

    private Context mCtx;
    private static List<DataHandler_VideoInfo> dataHandlerVideoInfoList;
    ProgressDialog p;
    String state;
    private String video_url,uploader,video_title;


    public Adapter_AdminVideoApproval(Context mCtx, List<DataHandler_VideoInfo> dataHandlerVideoInfoList, String state) {
        this.mCtx = mCtx;
        Adapter_AdminVideoApproval.dataHandlerVideoInfoList = dataHandlerVideoInfoList;
        this.state = state;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_video_edit, parent, false);
        // click listener here
        //releasePlayer();
        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        Glide.with(mCtx).
                load(dataHandlerVideoInfoList.get(position).getThumbnail()).
                thumbnail(0.8f).
                into(holder.imageViewimg);

        uploader =  dataHandlerVideoInfoList.get(position).getName();
        video_title =  dataHandlerVideoInfoList.get(position).getTitle();

        holder.name.setText("Approval: " + dataHandlerVideoInfoList.get(position).getApprovalresponse());
        if (dataHandlerVideoInfoList.get(position).getApprovalresponse().equals("false")) {
            holder.name.setTextColor(ColorStateList.valueOf(Color.parseColor("#D82626")));
        }
        holder.textViewShortDesc.setText("Uploader: " + dataHandlerVideoInfoList.get(position).getName() +
                "\nTitle: " + dataHandlerVideoInfoList.get(position).getTitle() +
                "\nDescription: " + dataHandlerVideoInfoList.get(position).getDescription()
        );
        video_url = dataHandlerVideoInfoList.get(position).getVideo();

        if (state.equals("Edit")) {
            holder.action.setOnClickListener(v -> {
                String[] Option = {"Approve", "Deny", "Delete"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx,R.style.AlertDialog);
                builder.setTitle("Select Action");
                builder.setItems(Option, (dialog, which) -> {
                    if (which == 0) {
                        DeleteLibrary(String.valueOf(dataHandlerVideoInfoList.get(position).getId()),
                                "true", "false", position);
                    } else if (which == 1) {
                        DeleteLibrary(String.valueOf(dataHandlerVideoInfoList.get(position).getId()),
                                "false", "false", position);
                    } else if (which == 2) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(mCtx,R.style.AlertDialog);
                        builders.setCancelable(true);
                        builders.setTitle("Confirmation");
                        builders.setMessage("Are you sure you want to delete this video?");
                        builders.setPositiveButton("confirm", (dialogInterface, i) -> DeleteLibrary(String.valueOf(dataHandlerVideoInfoList.get(position).getId()),
                                "false", "true", position));
                        builders.setNegativeButton("cancel", (dialogInterface, i) -> {
                        });
                        builders.show();
                    }
                });
                builder.show();
            });
        }
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
            String ThemeState = SharedPrefManager.getInstance(mCtx).getTheme();
            if (ThemeState != null && ThemeState.equals("dark")) {
                DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                dialogFragment.show( ((AppCompatActivity)mCtx).getSupportFragmentManager(),"tag");
            }else {
                DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                dialogFragment.show( ((AppCompatActivity)mCtx).getSupportFragmentManager(),"tag");
            }
        });
    }

    private void DeleteLibrary(String id, String response, String delete, int position) {
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("adminresponse", response);
                params.put("delete", delete);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_sendvideoresponse, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Sending  Response...");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                p.hide();

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        if (delete.equals("true")){
                            dataHandlerVideoInfoList.remove(position);
                        }
                        if (response.equals("true")){
                            sendNotification("New video uploaded by: " + uploader, video_title, "global",id);
                        }

                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        DeleteVideo ul = new DeleteVideo();
        ul.execute();
    }

    private void sendNotification(String message, String getTitle, String global, String id) {
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("title", message);
                params.put("message", getTitle);
                params.put("push_type", "topic");
                params.put("groupname", global);
                params.put("click_event", "ShopFragment");
                params.put("intent", "video "+id);

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

    public void filterlist(ArrayList<DataHandler_VideoInfo> filteredlist) {
        dataHandlerVideoInfoList = filteredlist;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        CardView recyclerView;
        TextView textViewShortDesc, name;
        ImageView imageViewimg;
        ImageView action;

        public ProductViewHolder(View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.cardView4);
            textViewShortDesc = itemView.findViewById(R.id.title);
            imageViewimg = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            action = itemView.findViewById(R.id.imageButton);
        }
    }

    //player

}
