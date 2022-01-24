package com.nkwazi_tech.tuit_app.Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.BuildConfig;
import com.nkwazi_tech.tuit_app.Fragments.Downloaded_Videos_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import javax.crypto.NoSuchPaddingException;

public class Adapter_download_book extends RecyclerView.Adapter<Adapter_download_book.ProductViewHolder> {
    private Context mCtx;
    Uri path = null;

    public Adapter_download_book(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_careplan, parent, false);
        //releasePlayer();
        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String my_key = "ltVkg0knCiDc9K80";//16 char = 128 bit
        String my_spec_key = "BentH1dIPoOEawVa";//tod
        File myDir;

        String FILE_NAME_DECRYPTED = Constant.allMediaList.get(position).getName().replace(".bok", ".pdf");
        String FILE_NAME_ENCRYPTED = Constant.allMediaList.get(position).getName();

        myDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/EducationalApp/");
        File outputFileDecrypted = new File(myDir, FILE_NAME_DECRYPTED);
        File encFile = new File(myDir, FILE_NAME_ENCRYPTED);

        String FILE_NAME_DECRYPTED2 = Constant.allMediaList.get(position).getName().replace(".bok", ".pdf");

        holder.username.setText(FILE_NAME_DECRYPTED2);
        holder.title.setVisibility(View.GONE);
        holder.bookthubnail.setVisibility(View.GONE);
        holder.options.setVisibility(View.VISIBLE);
        Glide.with(Objects.requireNonNull(mCtx))
                .asBitmap()
                .load(R.mipmap.logo)
                .placeholder(R.mipmap.logo)
                .error(R.mipmap.logo)
                .into(holder.propic);

        holder.options.setOnClickListener(v -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(mCtx, holder.options);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.popup_alter_downloads, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.Delete) {
                    encFile.delete();
                    Constant.allMediaList.remove(position);
                    notifyDataSetChanged();
                }
                return true;
            });
            popup.show();
        });

        holder.cardView.setOnClickListener(v -> {
            try {
                MyEncrypter.decryptToFile(my_key, my_spec_key, new FileInputStream(encFile),
                        new FileOutputStream(outputFileDecrypted));

                path = FileProvider.getUriForFile(mCtx,
                        BuildConfig.APPLICATION_ID + ".provider",
                        outputFileDecrypted);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                Downloaded_Videos_Frag.outputFileDecrypted = outputFileDecrypted;

                Intent intents = Intent.createChooser(intent, "Open File");
                mCtx.startActivity(intents);

                new Handler().postDelayed(() -> outputFileDecrypted.delete(), 4000);

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

    @Override
    public int getItemCount() {
        return Constant.allMediaList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView username, title;
        ImageView propic;
        TextView bookthubnail;
        ImageView options;

        public ProductViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView4);
            username = itemView.findViewById(R.id.cources);
            title = itemView.findViewById(R.id.message);
            propic = itemView.findViewById(R.id.userimg);
            bookthubnail = itemView.findViewById(R.id.cources5);
            options = itemView.findViewById(R.id.imageButton2);
        }
    }


}
