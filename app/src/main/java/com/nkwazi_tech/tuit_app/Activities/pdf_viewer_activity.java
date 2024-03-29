package com.nkwazi_tech.tuit_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.nkwazi_tech.tuit_app.Fragments.PDFviewer_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class pdf_viewer_activity extends AppCompatActivity {
    public static String pdffilepath;
    private PDFView pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer_activity);

        pdf = findViewById(R.id.ViewPdf);

        Defaults();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void Defaults() {
        try{
            new RetrievePdfStream().execute(pdffilepath);
        }
        catch (Exception e){
            Toast.makeText(this, "Failed to load Url :" + e.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}