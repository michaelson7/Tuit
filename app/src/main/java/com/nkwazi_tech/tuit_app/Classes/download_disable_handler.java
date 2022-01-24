package com.nkwazi_tech.tuit_app.Classes;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Activities.Settings_activity;

import java.io.File;
import java.net.HttpURLConnection;

public class download_disable_handler extends Activity {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static String file_name = null;
    public static HttpURLConnection httpURLConnection;
    public static NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        //delete file
        try{
            httpURLConnection.disconnect();
            notificationManagerCompat.cancel(getIntent().getIntExtra(NOTIFICATION_ID,-1));

            File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/EducationalApp/");
            File encFile = new File(myDir, file_name);
            encFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(getApplicationContext(), Home_Activity.class));
    }

    public static PendingIntent getDismissIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, download_disable_handler.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }
}