package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Classes.Adapter_download_book;
import com.nkwazi_tech.tuit_app.Classes.Adapter_download_video;
import com.nkwazi_tech.tuit_app.Classes.Constant;
import com.nkwazi_tech.tuit_app.R;

import java.io.File;

public class Downloaded_Videos_Frag extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    private File storage;
    public static String Type;
    public static File outputFileDecrypted;
    ConstraintLayout nodata;
    TextView nofile;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_home, container, false);

        recyclerView = root.findViewById(R.id.recycler2);
        progressBar = root.findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.VISIBLE);
        nodata = root.findViewById(R.id.nodata);

        nofile = root.findViewById(R.id.textView40);

        Fetch_Videos();
        return root;
    }

    private void Fetch_Videos() {
        Constant.allMediaList.clear();
        storage = new File(Environment.getExternalStorageDirectory().toString() + "/EducationalApp");

        if (Type.equals("book")) {
            Methods.load_Books(storage);
        } else {
            Methods.load_Directory_Files(storage);
        }
        Data();
    }

    private void Data() {
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (Type.equals("book")) {
                    Adapter_download_book adapter_download_video = new Adapter_download_book(getContext());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter_download_video);
                    progressBar.setVisibility(View.GONE);

                    if (adapter_download_video.getItemCount() < 1) {
                        nodata.setVisibility(View.VISIBLE);
                        nofile.setText("No Book Downloaded");
                    }
                } else {
                    Adapter_download_video adapter_download_video = new Adapter_download_video(getContext());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter_download_video);
                    progressBar.setVisibility(View.GONE);

                    if (adapter_download_video.getItemCount() < 1) {
                        nodata.setVisibility(View.VISIBLE);
                        nofile.setText("No Video Downloaded");
                    }
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    @Override
    public void onResume() {
        try {
            outputFileDecrypted.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }
}
