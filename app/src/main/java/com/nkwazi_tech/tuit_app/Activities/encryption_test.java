package com.nkwazi_tech.tuit_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nkwazi_tech.tuit_app.Classes.MyEncrypter;
import com.nkwazi_tech.tuit_app.R;

import java.io.ByteArrayOutputStream;
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

public class encryption_test extends AppCompatActivity {

    ImageView img;
    Button enccrypt, decrypt;

    String FILE_NAME_ENCRYPTED = "Decrypt";
    String FILE_NAME_DECRYPTED = "Decrypt.mp4";

    String my_key = "ltVkg0knCiDc9K80";//16 char = 128 bit
    String my_spec_key = "BentH1dIPoOEawVa";//tod

    File myDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption_test);

        img = findViewById(R.id.imageView11);
        enccrypt = findViewById(R.id.button13);
        decrypt = findViewById(R.id.button14);

//        img.setImageResource(R.mipmap.nonet);
        Defaults();
    }

    private void Defaults() {
        myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures");

        ActivityCompat.requestPermissions(encryption_test.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        enccrypt.setOnClickListener(v -> {
            Drawable drawable = ContextCompat.getDrawable(encryption_test.this, R.mipmap.nonet);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            File outputFileDecrypted = new File(myDir, FILE_NAME_DECRYPTED);
            InputStream is = null;
            try {
                is = new FileInputStream(outputFileDecrypted);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//          Create file
            File outputFileEncrypted = new File(myDir, FILE_NAME_ENCRYPTED);
            try {
                MyEncrypter.encryptToFile(my_key, my_spec_key, is, new FileOutputStream(outputFileEncrypted));
                Toast.makeText(encryption_test.this, "Encrypted!", Toast.LENGTH_SHORT).show();
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
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
        });

        decrypt.setOnClickListener(v -> {
            File outputFileDecrypted = new File(myDir, FILE_NAME_DECRYPTED);
            File encFile = new File(myDir, FILE_NAME_ENCRYPTED);
            try {
                MyEncrypter.decryptToFile(my_key, my_spec_key, new FileInputStream(encFile),
                        new FileOutputStream(outputFileDecrypted));
                //After that, set for image view
                img.setImageURI(Uri.fromFile(outputFileDecrypted));

                // if you want to delete file after decryption, just keep this line
                outputFileDecrypted.delete();

                Toast.makeText(encryption_test.this, "Decrypted!", Toast.LENGTH_SHORT).show();

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

        });
    }
}
