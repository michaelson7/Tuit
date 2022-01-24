package com.nkwazi_tech.tuit_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Fragments.Profile_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Settings_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.util.Objects;

public class recyclable_activity extends AppCompatActivity {

    public static String cmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //theme
        String ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclable);

        Toolbar toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(cmd);

        Class fragmentClass = null;
        if (cmd.equals("profile")){
            fragmentClass = Profile_Frag.class;
        }

        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}