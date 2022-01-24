package com.nkwazi_tech.tuit_app.Classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nkwazi_tech.tuit_app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.NoSuchPaddingException;

public class DownloadReceiver2 {

    public static void data(String txt, String ext, String fileUrl, Context mCtx) {
        String txt_no_ext = txt;
        txt = txt + ext;
        String finalTitle = txt;

        final InputStream[] input = {null};
        final OutputStream[] output = {null};
        final HttpURLConnection[] connection = {null};

        int notificationId = new Random().nextInt();
        PendingIntent dismissIntent = download_disable_handler.getDismissIntent(notificationId, mCtx);

        //creating folder
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "EducationalApp");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mCtx);

                String contentTitle = "downloading";
                NotificationCompat.Builder notificationBuilder = createNotificationBuilder("my_group_01", mCtx);
                notificationBuilder.setTicker("Start downloading from the server")
                        .setOngoing(true)
                        .addAction(R.drawable.exo_icon_stop, "Stop", dismissIntent)
                        .setAutoCancel(false)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setContentTitle(contentTitle)
                        .setContentText("0%")
                        .setProgress(100, 0, false)
                        .setContentTitle(finalTitle)
                        .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                        .setColor(mCtx.getResources().getColor(R.color.colorAccent));

                // Gets an instance of the NotificationManager service
                boolean success = false;

                try {
                    String fileName = finalTitle;

                    URL url = new URL(fileUrl);
                    connection[0] = (HttpURLConnection) url.openConnection();
                    connection[0].connect();

                    download_disable_handler.httpURLConnection = connection[0];
                    download_disable_handler.file_name = txt_no_ext + ".enc";
                    download_disable_handler.notificationManagerCompat=notificationManagerCompat;

                    int fileLength = connection[0].getContentLength();

                    // input stream to read file - with 8k buffer
                    input[0] = connection[0].getInputStream();
                    output[0] = new FileOutputStream(Environment.getExternalStorageDirectory() +
                            File.separator + "EducationalApp/" + fileName);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count, tmpPercentage = 0;
                    while ((count = input[0].read(data)) != -1) {
                        total += count;
                        output[0].write(data, 0, count);
                        int percentage = (int) ((total * 100) / fileLength);
                        if (percentage > tmpPercentage) {
                            notificationBuilder.setContentText(percentage + "%");
                            notificationBuilder.setProgress(100, percentage, false);
                            tmpPercentage = percentage;

                            //display noti
                            NotificationManager notifyMgr = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
                            notifyMgr.notify(notificationId, notificationBuilder.build());
                        }
                    }

                } catch (Exception e) {

                } finally {
                    try {
                        if (output[0] != null)
                            output[0].close();
                        if (input[0] != null)
                            input[0].close();
                        success = true;
                        contentTitle = "File Downloaded";
                        String statusText = success ? "Done" : "Fail";
                        int resId = success ? android.R.drawable.stat_sys_download_done : android.R.drawable.stat_notify_error;
                        notificationBuilder.setContentTitle(contentTitle);
                        notificationBuilder.setSmallIcon(resId);
                        notificationBuilder.setOngoing(false);
                        notificationBuilder.setAutoCancel(true);
                        notificationBuilder.setContentText(statusText);
                        notificationBuilder.setProgress(0, 0, false);
                        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
//                        notificationManagerCompat.cancel(notificationId);

                        String encrypted_Name;
                        if (ext.equals(".pdf")) {
                            encrypted_Name = finalTitle.replace(ext, ".bok");
                        } else {
                            encrypted_Name = finalTitle.replace(ext, ".enc");
                        }

                        encrypt(encrypted_Name, finalTitle);
                    } catch (IOException ignored) {
                    }
                    if (connection[0] != null)
                        connection[0].disconnect();
                }
            }
        };
        thread.start();
    }

    private static void encrypt(String encrypted_Name, String video_Name) {
        String my_key = "ltVkg0knCiDc9K80";//16 char = 128 bit
        String my_spec_key = "BentH1dIPoOEawVa";//tod
        File myDir;

        myDir = new File(Environment.getExternalStorageDirectory().toString() + "/EducationalApp");

        File outputFileDecrypted = new File(myDir, video_Name);
        InputStream is = null;
        try {
            is = new FileInputStream(outputFileDecrypted);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        File outputFileEncrypted = new File(myDir, encrypted_Name);
        try {
            MyEncrypter.encryptToFile(my_key, my_spec_key, is, new FileOutputStream(outputFileEncrypted));
//            Toast.makeText(mCtx, "Download Complete", Toast.LENGTH_SHORT).show();
            outputFileDecrypted.delete();

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
    }

    private static NotificationCompat.Builder createNotificationBuilder(String channelId, Context mCtx) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = mCtx.getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        return new NotificationCompat.Builder(mCtx, channelId);
    }

}
