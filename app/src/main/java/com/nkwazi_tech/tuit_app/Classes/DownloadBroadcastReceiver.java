package com.nkwazi_tech.tuit_app.Classes;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import static android.app.DownloadManager.EXTRA_DOWNLOAD_ID;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    public static long downloadID;
    public static String video_Name = null, encrypted_Name = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long id = intent.getLongExtra(EXTRA_DOWNLOAD_ID, -1);

            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
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
//          Create file
                File outputFileEncrypted = new File(myDir, encrypted_Name);
                try {
                    MyEncrypter.encryptToFile(my_key, my_spec_key, is, new FileOutputStream(outputFileEncrypted));
                    Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
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
//                DownloadThumb();
            }
        }
    }
}