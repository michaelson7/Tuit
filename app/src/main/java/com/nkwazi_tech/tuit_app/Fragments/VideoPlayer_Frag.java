package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Related_Video;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoPlayerInfo;
import com.nkwazi_tech.tuit_app.Classes.DownloadBroadcastReceiver;
import com.nkwazi_tech.tuit_app.Classes.DownloadReceiver;
import com.nkwazi_tech.tuit_app.Classes.MyEncrypter;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.crypto.NoSuchPaddingException;

import static android.app.DownloadManager.*;
import static com.android.volley.VolleyLog.TAG;

public class VideoPlayer_Frag extends DialogFragment {
    public static VideoPlayer_Frag newInstanse() {
        return new VideoPlayer_Frag();
    }

    private TextView title, description, name, fullscreenButton, video_COunt, duration, views, date;
    private ProgressBar progressBar;
    private PlayerView playerView;
    private ImageView img;
    ImageButton expandable_text,DownloadBtn;
    private SimpleExoPlayer simpleExoPlayer;
    File outputFileDecrypted2;
    RecyclerView recycler_R_Fields;
    ConstraintLayout.LayoutParams params;
    private PlaybackStateListener playbackStateListener;

    private long downloadID;
    public static String encrypted_Name;
    public static String video_Name;
    public static String downloaded_Video_Path;
    public static String views_Num;
    public static String date_Num = "2020/10/10 10:10:10";
    public static String duration_Num;
    String video_url = DataHandler_VideoPlayerInfo.getInstance(getContext()).getVideo();
    private boolean playWhenReady = true;
    private long playbackPosition = 0;
    private int currentWindow = 0;
    final boolean[] fullscreen = {false};

    List<DataHandler_VideoInfo> dataHandlerVideoInfoList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_videoview, container, false);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        progressBar = root.findViewById(R.id.progressBar4);
        playerView = root.findViewById(R.id.video_view);
        title = root.findViewById(R.id.videonametxt);
        description = root.findViewById(R.id.videonametxt2);
        name = root.findViewById(R.id.videoowner);
        img = root.findViewById(R.id.profilephoto);
        video_COunt = root.findViewById(R.id.videoowner2);
        expandable_text = root.findViewById(R.id.imageButton3);
        recycler_R_Fields = root.findViewById(R.id.recyclercomment);
        DownloadBtn = root.findViewById(R.id.imageButton4);
        duration = root.findViewById(R.id.textView81);
        views = root.findViewById(R.id.textView82);
        date = root.findViewById(R.id.textView83);

        playbackStateListener = new PlaybackStateListener();
        dataHandlerVideoInfoList = new ArrayList<>();

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                ReturnNormal();
                String ThemeState = SharedPrefManager.getInstance(getContext()).getTheme();
                if (ThemeState != null && ThemeState.equals("dark")) {
                    Objects.requireNonNull(getActivity()).setTheme(R.style.AppThemeDark);
                }
                dismiss();
                return true;
            }
            return false;
        });

        LoadRelatedVideos();
        return root;
    }

    private void LoadRelatedVideos() {
        int id = SharedPrefManager.getInstance(getContext()).getID();
        String acoount_Type = SharedPrefManager.getInstance(getContext()).getAccounttype();
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("videoid", String.valueOf(DataHandler_VideoPlayerInfo.getInstance(getContext()).getVideoid()));
                params.put("userid", String.valueOf(id));
                params.put("page", String.valueOf(1));
                params.put("acoount_Type", acoount_Type);

                return requestHandler.sendPostRequest(URLs.fetch_related_Videos, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("Products");

                    //traversing through all the object
                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        // adding the product to product list class
                        dataHandlerVideoInfoList.add(new DataHandler_VideoInfo(
                                product.getInt("videoid"),
                                product.getString("title"),
                                product.getString("description"),
                                product.getInt("lecturerid"),
                                product.getInt("courseid"),
                                product.getString("videopath"),
                                product.getString("name"),
                                product.getString("propic"),
                                product.getInt("likes"),
                                product.getInt("comments"),
                                product.getInt("views"),
                                product.getString("timestamp"),
                                product.getString("tags"),
                                "null",
                                product.getString("thumb"),
                                product.getString("file_Size"),
                                product.getString("file_Duration")
                        ));
                    }

                    //creating adapter object and setting it to recyclerview
                    boolean b = false;
                    Adapter_Related_Video adapter = new Adapter_Related_Video(getContext(), dataHandlerVideoInfoList, b, "compact_card", VideoPlayer_Frag.this);
                    recycler_R_Fields.setLayoutManager(new LinearLayoutManager(getContext()));
                    recycler_R_Fields.setAdapter(adapter);
                    recycler_R_Fields.getViewTreeObserver().addOnPreDrawListener(
                            new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    recycler_R_Fields.getViewTreeObserver().removeOnPreDrawListener(this);

                                    for (int i = 0; i < recycler_R_Fields.getChildCount(); i++) {
                                        View v = recycler_R_Fields.getChildAt(i);
                                        v.setAlpha(0.0f);
                                        v.animate().alpha(1.0f)
                                                .setDuration(300)
                                                .setStartDelay(i * 50)
                                                .start();
                                    }
                                    return true;
                                }
                            });
                    DefaultValues();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    private void ReturnNormal() {
        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (200 * Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density);
        playerView.setLayoutParams(params);
        fullscreen[0] = false;
    }

    private void DefaultValues() {
        //expandable card
        expandable_text.setOnClickListener(v -> {
            Expand(description);
        });

        video_Name = video_url.replace("https://myhost.nkwazitech.com/documents/", "");
        String profileimg = DataHandler_VideoPlayerInfo.getInstance(getContext()).getImg();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String niceDateStr = null;
        Date dates = null;
        try {
            dates = inputFormat.parse(date_Num);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
           try {
               niceDateStr = (String) DateUtils.getRelativeTimeSpanString(dates.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
           } catch (Exception e) {
               e.printStackTrace();
           }
        } else {
            niceDateStr = date_Num;
        }

        video_COunt.setText(DataHandler_VideoPlayerInfo.getInstance(getContext()).getVideoid() + " Videos");
        title.setText(DataHandler_VideoPlayerInfo.getInstance(getContext()).getTitle());
        description.setText(DataHandler_VideoPlayerInfo.getInstance(getContext()).getDescription());
        name.setText(DataHandler_VideoPlayerInfo.getInstance(getContext()).getUsername());

        duration.setText(duration_Num);
        views.setText(views_Num);
        date.setText(niceDateStr);

        Glide.with(getContext()).
                load(profileimg).
                thumbnail(0.11f).
                placeholder(R.mipmap.logo).
                error(R.mipmap.logo).
                into(img);

        DownloadBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // this will request for permission when user has not granted permission for the app
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Toast.makeText(getContext(), "Downloading...", Toast.LENGTH_LONG).show();
                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "EducationalApp");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                DownloadReceiver.data(DataHandler_VideoPlayerInfo.getInstance(getContext()).getTitle(),".mp4",video_url,getContext());
            }
        });

        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        fullscreenButton.setOnClickListener(view -> {
            if (fullscreen[0]) {
                Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                    Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
                }
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = (int) (200 * Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density);
                playerView.setLayoutParams(params);
                fullscreen[0] = false;
            } else {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                }
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                playerView.setLayoutParams(params);
                fullscreen[0] = true;
            }
        });
    }

    private void Expand(TextView view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Set initial height to 0 and show the view
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
            view.setLayoutParams(layoutParams);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
        anim.start();
    }

    private void updateView() {
        /////use for like
        int Videoid = DataHandler_VideoPlayerInfo.getInstance(getContext()).getVideoid();
        int VideoViews = DataHandler_VideoPlayerInfo.getInstance(getContext()).getViews();
        int studentid = SharedPrefManager.getInstance(getContext()).getID();
        VideoViews = VideoViews + 1;

        int finalVideoViews = VideoViews;
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("videoid", String.valueOf(Videoid));
                params.put("viewnum", String.valueOf(finalVideoViews));
                params.put("studentid", String.valueOf(studentid));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_setViews, params);
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
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    public void iniexoplayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext());
        playerView.setPlayer(simpleExoPlayer);

        Uri uri = null;
        if (downloaded_Video_Path != null) {
            String my_key = "ltVkg0knCiDc9K80";//16 char = 128 bit
            String my_spec_key = "BentH1dIPoOEawVa";//tod
            File myDir;

            String FILE_NAME_DECRYPTED = downloaded_Video_Path.replace(".enc", ".mp4");
            String FILE_NAME_ENCRYPTED = downloaded_Video_Path;

            myDir = new File(Environment.getExternalStorageDirectory().toString() + "/EducationalApp");
            outputFileDecrypted2 = new File(myDir, FILE_NAME_DECRYPTED);
            File encFile = new File(myDir, FILE_NAME_ENCRYPTED);
            try {
                MyEncrypter.decryptToFile(my_key, my_spec_key, new FileInputStream(encFile),
                        new FileOutputStream(outputFileDecrypted2));
                //After that, set for image view

                uri = Uri.parse(String.valueOf(outputFileDecrypted2));
                downloaded_Video_Path = null;
                // if you want to delete file after decryption, just keep this line
//                outputFileDecrypted.delete();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        } else {
            uri = Uri.parse(video_url);
        }

        MediaSource mediaSource = buildMediaSource(uri);
        simpleExoPlayer.setPlayWhenReady(playWhenReady);
        simpleExoPlayer.seekTo(currentWindow, playbackPosition);
        simpleExoPlayer.prepare(mediaSource, false, false);
        simpleExoPlayer.addListener(playbackStateListener);
        simpleExoPlayer.prepare(mediaSource, false, false);
        simpleExoPlayer.setRepeatMode(SimpleExoPlayer.REPEAT_MODE_OFF);
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
                    updateView();
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        // These factories are used to construct two media sources below
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getContext(), "exoplayer-codelab");
        ProgressiveMediaSource.Factory mediaSourceFactory =
                new ProgressiveMediaSource.Factory(dataSourceFactory);

        // Create a media source using the supplied URI
        MediaSource mediaSource1 = mediaSourceFactory.createMediaSource(uri);

        // Additionally create a media source using an MP3
        Uri audioUri = Uri.parse(video_url);
        MediaSource mediaSource2 = mediaSourceFactory.createMediaSource(audioUri);

        return new ConcatenatingMediaSource(mediaSource1, mediaSource2);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            iniexoplayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || simpleExoPlayer == null)) {
            iniexoplayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
            try {
                outputFileDecrypted2.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
            try {
                outputFileDecrypted2.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            playbackPosition = simpleExoPlayer.getCurrentPosition();
            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            simpleExoPlayer.removeListener(playbackStateListener);
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
