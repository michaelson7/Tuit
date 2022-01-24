package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nkwazi_tech.tuit_app.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFviewer_Frag extends Fragment   {
    public static String pdffilepath;
    private PDFView pdf;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_pdfviewer, container, false);
       // setContentView(R.layout.activity_main);
        pdf = root.findViewById(R.id.ViewPdf);

        Defaults();
        return root;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void Defaults() {
        try{
            new RetrievePdfStream().execute(pdffilepath);
        }
        catch (Exception e){
            Toast.makeText(getContext(), "Failed to load Url :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;

            }
            return inputStream;
        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdf.fromStream(inputStream).load();
        }
    }


}
