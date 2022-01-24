package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.Bookmark_Frag;
import com.nkwazi_tech.tuit_app.Fragments.History_Frag;
import com.nkwazi_tech.tuit_app.Fragments.VideoEdit_Frag;
import com.nkwazi_tech.tuit_app.Fragments.VideoPlayer_Frag;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class Adapter_VideoEdit extends RecyclerView.Adapter<Adapter_VideoEdit.ProductViewHolder> {
    private Context mCtx;
    private static List<DataHandler_VideoInfo> dataHandlerVideoInfoList;
    ProgressDialog p;
    private Dialog editdialog;
    String state;
    private static SimpleExoPlayer simpleExoPlayer;
    private static PlaybackStateListener playbackStateListener;
    private String video_url;
    private ProgressBar progressBar;


    public Adapter_VideoEdit(Context mCtx, List<DataHandler_VideoInfo> dataHandlerVideoInfoList, String state) {
        this.mCtx = mCtx;
        Adapter_VideoEdit.dataHandlerVideoInfoList = dataHandlerVideoInfoList;
        this.state = state;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_video_edit, parent, false);
        // click listener here
        editdialog = new Dialog(mCtx);
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

        holder.textViewShortDesc.setText(dataHandlerVideoInfoList.get(position).getTitle());
        holder.name.setText(dataHandlerVideoInfoList.get(position).getName());
        video_url = dataHandlerVideoInfoList.get(position).getVideo();

        if (state.equals("likes")) {
            holder.action.setVisibility(View.GONE);
        }
        if (state.equals("Edit")) {
            holder.action.setOnClickListener(v -> {
                String[] Option = {"Edit", "Delete"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx, R.style.AlertDialog);
                builder.setTitle("Select Action");
                builder.setItems(Option, (dialog, which) -> {
                    if (which == 0) {
                        ShowVideoEditDialog(String.valueOf(dataHandlerVideoInfoList.get(position).getId()),
                                dataHandlerVideoInfoList.get(position).getTitle(),
                                dataHandlerVideoInfoList.get(position).getDescription()
                        );

                    } else if (which == 1) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(mCtx, R.style.AlertDialog);
                        builders.setCancelable(true);
                        builders.setTitle("Confirmation");
                        builders.setMessage("Are you sure you want to delete this video?");
                        builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteVideo(String.valueOf(dataHandlerVideoInfoList.get(position).getId()), position);
                            }
                        });
                        builders.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builders.show();
                    }
                });
                builder.show();
            });
        } else {
            holder.action.setOnClickListener(v -> {
                String[] Option = {"Delete"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx, R.style.AlertDialog);
                builder.setTitle("Select action");
                builder.setItems(Option, (dialog, which) -> {
                    if (which == 0) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(mCtx, R.style.AlertDialog);
                        builders.setCancelable(true);
                        builders.setTitle("Confirmation");
                        builders.setMessage("Are you sure you want to delete this?");
                        builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteLibrary(String.valueOf(dataHandlerVideoInfoList.get(position).getId()),
                                        String.valueOf(SharedPrefManager.getInstance(mCtx).getID()), position);
                            }
                        });
                        builders.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
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
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppThemeDark);
                dialogFragment.show(((AppCompatActivity) mCtx).getSupportFragmentManager(), "tag");
            } else {
                DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
                dialogFragment.show(((AppCompatActivity) mCtx).getSupportFragmentManager(), "tag");
            }
        });
    }

    private void DeleteLibrary(String videoid, String Userid, int position) {
        //String path = "http://nawa777.000webhostapp.com/Api.php?apicall=DeleteLibrary";
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("videoid", videoid);
                params.put("lecturerid", Userid);
                params.put("state", state);
                //returing the response

                return requestHandler.sendPostRequest(URLs.URL_DeleteLibrary, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Deleting Video...");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                p.dismiss();

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(mCtx, "Video Deleted", Toast.LENGTH_SHORT).show();
                        dataHandlerVideoInfoList.remove(position);
                        notifyDataSetChanged();

                        if (state.equals("history")) {
                            FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                            FragmentTransaction fr = manager.beginTransaction();
                            fr.replace(R.id.flContent, new History_Frag());
                            fr.commit();
                        }
                        if (state.equals("bookmark")) {
                            FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                            FragmentTransaction fr = manager.beginTransaction();
                            fr.replace(R.id.flContent, new Bookmark_Frag());
                            fr.commit();
                        }

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

    private void DeleteVideo(String videoid, int position) {
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("videoid", videoid);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_videodelete, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Deleting Video...");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                p.hide();
                try {
                    Toast.makeText(mCtx, "Video Deleted", Toast.LENGTH_SHORT).show();
                    dataHandlerVideoInfoList.remove(position);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    dataHandlerVideoInfoList.remove(position);
                    notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        DeleteVideo ul = new DeleteVideo();
        ul.execute();
    }

    private void UpdateVideo(String videoid, String title, String description, String tags) {
        @SuppressLint("StaticFieldLeak")
        class updatevideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("videoid", videoid);
                params.put("title", title);
                params.put("description", description);
                params.put("tags", tags);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_videoupdate, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Updating Video...");
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
                        editdialog.dismiss();
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                        FragmentTransaction fr = manager.beginTransaction();
                        fr.replace(R.id.flContent, new VideoEdit_Frag());
                        fr.commit();
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        updatevideo ul = new updatevideo();
        ul.execute();
    }

    @SuppressLint("SetTextI18n")
    private void ShowVideoEditDialog(String id, String Title, String Description) {

        Button submit;
        EditText title, description;

        editdialog.setContentView(R.layout.dialog_video_edit);
        editdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = editdialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        title = editdialog.findViewById(R.id.practicenumber);
        description = editdialog.findViewById(R.id.course);
        submit = editdialog.findViewById(R.id.button2);

        progressBar = editdialog.findViewById(R.id.progressBar4);
        playbackStateListener = new PlaybackStateListener();
        EditText tagss = editdialog.findViewById(R.id.phone);

        title.setText(Title);
        description.setText(Description);

        submit.setOnClickListener(v -> {
            String Etitle = title.getText().toString().trim();
            String Edescription = description.getText().toString().trim();
            String Etags = tagss.getText().toString().trim();

            if (TextUtils.isEmpty(Etitle)) {
                title.setError("Enter title");
                title.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(Edescription)) {
                description.setError("Enter description");
                description.requestFocus();
                return;
            }

            UpdateVideo(id, Etitle, Edescription, Etags);
        });
        // iniexoplayer(video);
        editdialog.show();
        editdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                releasePlayer();
            }
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

    private class PlaybackStateListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady,
                                         int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    progressBar.setVisibility(View.GONE);
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    releasePlayer();
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady);
        }
    }

    public static void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.removeListener(playbackStateListener);
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    //player

}
