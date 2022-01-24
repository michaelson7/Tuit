package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Settings_Frag extends Fragment {

    private CardView profilecard;
    private TextView userinfo;
    private CircleImageView userimg;
    Button btncache, button17, button18, button9,btncache2;
    private Dialog privacy, terms;
    Switch nighttoggle, datatoogle;
    String ThemeState;
    String Data;
    String Terms, Policies;

    @SuppressLint("CutPasteId")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_settings, container, false);

        ThemeState = SharedPrefManager.getInstance(getContext()).getTheme();
        Data = SharedPrefManager.getInstance(getContext()).getdatasaving();

        profilecard = root.findViewById(R.id.profilecard);
        userinfo = root.findViewById(R.id.userinfo);
        userimg = root.findViewById(R.id.userimg);
        btncache = root.findViewById(R.id.btncache);
        btncache2= root.findViewById(R.id.btncache2);
        button9 = root.findViewById(R.id.button9);
        nighttoggle = root.findViewById(R.id.nighttoggle);
        datatoogle = root.findViewById(R.id.datatoggle);

        Defaults();
        ProfileCard();
        ThemeChange();
        Datachange();
        return root;
    }

    private void Datachange() {
        if (Data != null && Data.equals("set")) {
            datatoogle.setChecked(true);
            datatoogle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPrefManager.getInstance(getContext()).setdatasaving("Notset");
            });
        } else {
            datatoogle.setChecked(false);
            datatoogle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPrefManager.getInstance(getContext()).setdatasaving("set");
            });
        }
    }

    private void ThemeChange() {
        if (ThemeState != null && ThemeState.equals("dark")) {
            nighttoggle.setChecked(true);
            nighttoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPrefManager.getInstance(getContext()).setTheme("light");
                Objects.requireNonNull(getActivity()).setTheme(R.style.AppTheme);
                getActivity().finish();
                startActivity(new Intent(getContext(), Home_Activity.class));
            });
        } else {
            nighttoggle.setChecked(false);
            nighttoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPrefManager.getInstance(getContext()).setTheme("dark");
                Objects.requireNonNull(getActivity()).setTheme(R.style.AppThemeDark);
                getActivity().finish();
                startActivity(new Intent(getContext(), Home_Activity.class));
            });
        }
    }

    private void Defaults() {
        btncache.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialog);
            builder.setCancelable(true);
            builder.setTitle("Confirmation");
            builder.setMessage("Cache helps improve the performance of this application. \nAre you sure you want to delete current cache?");
            builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FileUtils.deleteQuietly(getContext().getCacheDir());
                    Snackbar.make(Objects.requireNonNull(getView()), "Cache Deleted", Snackbar.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();

        });

        btncache2.setOnClickListener(v -> {
            String packageName = getContext().getPackageName();
            try {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", packageName);
                    intent.putExtra("app_uid", getContext().getApplicationInfo().uid);
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + packageName));
                } else {
                    return;
                }
                startActivity(intent);
            } catch (Exception e) {
            }
        });

        button9.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialog);
            builder.setCancelable(true);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to Sign out?");
            builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPrefManager.getInstance(getContext()).logout();

                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        });
    }

    private void ProfileCard() {
        String profileimg = SharedPrefManager.getInstance(getContext()).getPropic();
        String userinformation = SharedPrefManager.getInstance(getContext()).getName() + '\n' +
                SharedPrefManager.getInstance(getContext()).getEmail();

            Glide.with(Objects.requireNonNull(getContext()))
                    .asBitmap()
                    .load(SharedPrefManager.getInstance(getContext()).getPropic())
                    .placeholder(R.mipmap.girl)
                    .error(R.mipmap.girl)
                    .into(userimg);

        userinfo.setText(userinformation);

        profilecard.setOnClickListener(v -> {
            FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager();
            FragmentTransaction fr = manager.beginTransaction();
            fr.replace(R.id.flContent, new Profile_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });
    }


}
