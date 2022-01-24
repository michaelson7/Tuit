package com.nkwazi_tech.tuit_app.Classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nkwazi_tech.tuit_app.Fragments.VideoPlayer_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

public class Adapter_download_video extends RecyclerView.Adapter<Adapter_download_video.ProductViewHolder> {
    private Context mCtx;

    public Adapter_download_video(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_video_edit, parent, false);
        //releasePlayer();
        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String my_key = "ltVkg0knCiDc9K80";//16 char = 128 bit
        String my_spec_key = "BentH1dIPoOEawVa";//tod
        File myDir;

        String FILE_NAME_DECRYPTED = Constant.allMediaList.get(position).getName().replace(".enc", ".mp4");
        String FILE_NAME_ENCRYPTED = Constant.allMediaList.get(position).getName();
        myDir = new File(Environment.getExternalStorageDirectory().toString() + "/EducationalApp");

        File outputFileDecrypted = new File(myDir, FILE_NAME_DECRYPTED);
        File encFile = new File(myDir, FILE_NAME_ENCRYPTED);
        try {
            MyEncrypter.decryptToFile(my_key, my_spec_key, new FileInputStream(encFile),
                    new FileOutputStream(outputFileDecrypted));
            //After that, set for image view
            Glide.with(mCtx).
                    load(Uri.fromFile(outputFileDecrypted)).
                    thumbnail(0.8f).
                    listener(new RequestListener<Drawable>() {
                                 @Override
                                 public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                             boolean isFirstResource) {
                                     try {
                                         encFile.delete();
                                         Constant.allMediaList.remove(position);
                                         notifyDataSetChanged();
                                     } catch (Exception ex) {
                                         ex.printStackTrace();
                                     }

                                     return false;
                                 }

                                 @Override
                                 public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                                DataSource dataSource, boolean isFirstResource) {
                                     outputFileDecrypted.delete();
                                     return false;
                                 }
                             }
                    ).
                    into(holder.imageViewimg);

            // if you want to delete file after decryption, just keep this line
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

        holder.name.setVisibility(View.GONE);
        holder.textViewShortDesc.setText(FILE_NAME_DECRYPTED);
        holder.action.setOnClickListener(v -> {
        //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(mCtx, holder.action);
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

        holder.recyclerView.setOnClickListener(v -> {
            //open video player
            DataHandler_VideoPlayerInfo.getInstance(mCtx).DataHandler_VideoPlayerInfo(
                    FILE_NAME_DECRYPTED,
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    0,
                    0,
                    ""
            );
//        VideoPlayer_Frag.thumb_url =   dataHandlerVideoInfoList.get(position).getThumbnail();
            VideoPlayer_Frag.downloaded_Video_Path = Constant.allMediaList.get(position).getName();
            String ThemeState = SharedPrefManager.getInstance(mCtx).getTheme();
            if (ThemeState != null && ThemeState.equals("dark")) {
                DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                dialogFragment.show(((AppCompatActivity) mCtx).getSupportFragmentManager(), "tag");
            } else {
                DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                dialogFragment.show(((AppCompatActivity) mCtx).getSupportFragmentManager(), "tag");
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

        CardView recyclerView;
        TextView textViewShortDesc, name;
        ImageView imageViewimg;
        ImageView action;

        public ProductViewHolder(View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.cardView4);
            textViewShortDesc = itemView.findViewById(R.id.title);
            imageViewimg = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            action = itemView.findViewById(R.id.imageButton);
        }
    }


    //player

}
