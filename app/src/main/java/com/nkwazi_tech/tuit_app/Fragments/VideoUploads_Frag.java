package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wseemann.media.FFmpegMediaMetadataRetriever;

import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;

public class VideoUploads_Frag extends Fragment {
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
    private EditText title, description, tags;
    private Spinner audience;
    private Button sub, upload;
    private ProgressBar progressBar;
    private ProgressDialog p;
    private String selectedVideo = null, path, selectedcourse;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private TextView username6, email;
    CircleImageView imageView;
    ConstraintLayout video_Constraint;
    FFmpegMediaMetadataRetriever mFFmpegMediaMetadataRetriever = new FFmpegMediaMetadataRetriever();

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    final int REQUEST_CODE_GALLERY = 100;

    Bitmap bitmap = null;
    Uri selectedvideopath;
    private PlaybackStateListener playbackStateListener;
    ArrayList<String> Courses = new ArrayList<String>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_upload, container, false);

        getActivity().setTitle("Video Upload");
        audience = root.findViewById(R.id.audience2);
        title = root.findViewById(R.id.title2);
        description = root.findViewById(R.id.description);
        sub = root.findViewById(R.id.button3);
        progressBar = root.findViewById(R.id.progressBar2);
        upload = root.findViewById(R.id.uploadimg2);
        playerView = root.findViewById(R.id.videoView1);
        username6 = root.findViewById(R.id.usernametxt);
        email = root.findViewById(R.id.course);
        tags = root.findViewById(R.id.tags);
        imageView = root.findViewById(R.id.userimga);
        video_Constraint = root.findViewById(R.id.video_Constraint);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        playbackStateListener = new PlaybackStateListener();

        LoadCourses();
        return root;
    }

    private void LoadCourses() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getcourse+schoolId,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {
                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);
                            // adding the product to product list class
                            Courses.add(product.getString("coursename"));
                        }
                        Courses.add("General Research Process");
                        Courses.add("BioStats Statistics");
                        Defaults();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void Defaults() {

        username6.setText(SharedPrefManager.getInstance(getContext()).getName());
        email.setText(SharedPrefManager.getInstance(getContext()).getEmail());

        String[] status = Courses.toArray(new String[0]);
        audience.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, status));
        audience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedcourse = audience.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Glide.with(Objects.requireNonNull(getContext()))
                .asBitmap()
                .load(SharedPrefManager.getInstance(getContext()).getPropic())
                .placeholder(R.mipmap.girl)
                .error(R.mipmap.girl)
                .into(imageView);

        sub.setOnClickListener(v -> {
            //calling the upload file method after choosing the file
            if (selectedVideo != null) {
                uploadVideo();
            } else {
                Toast.makeText(getContext(), "Please select video", Toast.LENGTH_LONG).show();
            }
        });

        upload.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(Intent.createChooser(i, "Select Video"), 100);
            } else {
                Toast.makeText(getActivity(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            //the image URI
            Uri selectedImageUri = data.getData();

            File file = new File(getRealPathFromURI(selectedImageUri));
            Long file_bytes = file.length();
            long file_kb = file_bytes / 1024;
            long file_mb = file_kb / 1024;

            if (file_mb > 115) {
                Toast.makeText(getContext(), "Please select a file less than 115MB", Toast.LENGTH_LONG).show();
                return;
            } else {
                selectedvideopath = selectedImageUri;
                selectedVideo = getPath(selectedImageUri);
                path = getRealPathFromURI(selectedImageUri);
                mFFmpegMediaMetadataRetriever.setDataSource(path);
                video_Constraint.setVisibility(View.GONE);
                iniexoplayer();

                //thubnail
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getContext(), selectedImageUri);
                bitmap = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            }
        }
    }

    private void uploadVideo() {
        final String GetTitle = title.getText().toString();
        final String GetDescription = description.getText().toString();
        final String GetTags = tags.getText().toString();
        int lecturerid = SharedPrefManager.getInstance(getContext()).getID();

        //validating inputs
        if (TextUtils.isEmpty(GetTitle)) {
            title.setError("Please enter title");
            title.requestFocus();
            return;
        }
        //validating inputs
        if (TextUtils.isEmpty(GetDescription)) {
            description.setError("Please enter description");
            description.requestFocus();
            return;
        }

        p = new ProgressDialog(getContext());
        p.setMessage("Uploading File...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();

        //save thumbnail
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String path2 = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Title", null);
        Uri uri = Uri.parse(path2);

        File file = new File(getRealPathFromURI(selectedvideopath));
        File file2 = new File(getRealPathFromURI(uri));

        //generate size and length
        String mVideoDuration = mFFmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        String videosize = mFFmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE);

        long mTimeInMilliseconds = Long.parseLong(mVideoDuration);
        String seconds = String.valueOf((mTimeInMilliseconds % 60000) / 1000);
        String minutes = String.valueOf(mTimeInMilliseconds / 60000);

        long msize = Long.parseLong(videosize);
        String Actual_Legth;

        if (seconds.length() == 1) {
            Actual_Legth = minutes + ":0" + seconds;
        } else {
            Actual_Legth = minutes + ":" + seconds;
        }
        String actual_Size = humanReadableByteCountSI(msize);

        RequestBody setTitle = RequestBody.create(MediaType.parse("text/plain"), GetTitle);
        RequestBody setDescription = RequestBody.create(MediaType.parse("text/plain"), GetDescription);
        RequestBody setTags = RequestBody.create(MediaType.parse("text/plain"), GetTags);
        RequestBody setId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lecturerid));
        RequestBody setCourse = RequestBody.create(MediaType.parse("text/plain"), selectedcourse);
        RequestBody file_Duration = RequestBody.create(MediaType.parse("text/plain"), Actual_Legth);
        RequestBody file_Size = RequestBody.create(MediaType.parse("text/plain"), actual_Size);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
        MultipartBody.Part video = MultipartBody.Part.createFormData("video", file.getName(), requestBody);
        MultipartBody.Part thumbnail = MultipartBody.Part.createFormData("thumb", file2.getName(), requestBody2);

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
        Call<ServerResponse> call = api.uploadvideo(setTitle, setDescription, setTags, setId,
                setCourse, file_Duration, file_Size, video, thumbnail);

        //finally performing the call
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();

                    String name = String.valueOf(SharedPrefManager.getInstance(getContext()).getName());

                    p.hide();
                } else {
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    p.hide();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                p.hide();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContext().getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    //////
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    private void iniexoplayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext());
        playerView.setPlayer(simpleExoPlayer);
        Uri uri = Uri.parse(path);
        MediaSource mediaSource = buildMediaSource(uri);
        simpleExoPlayer.setPlayWhenReady(playWhenReady);
        simpleExoPlayer.seekTo(currentWindow, playbackPosition);
        simpleExoPlayer.prepare(mediaSource, false, false);
        simpleExoPlayer.addListener(playbackStateListener);
        simpleExoPlayer.prepare(mediaSource, false, false);

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
        Uri audioUri = Uri.parse(path);
        MediaSource mediaSource2 = mediaSourceFactory.createMediaSource(audioUri);

        return new ConcatenatingMediaSource(mediaSource1, mediaSource2);
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || simpleExoPlayer == null)) {
            path = getRealPathFromURI(selectedVideo);
            iniexoplayer();
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
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


}