package com.nkwazi_tech.tuit_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.nkwazi_tech.tuit_app.Classes.Adapter_Dash_Courselist;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Fragments.AdminCarePlansUpload_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Admin_AboutusEdit_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Admin_CourseIconEdit_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Admin_UserMod_Frag;
import com.nkwazi_tech.tuit_app.Fragments.GeneralResearch_Uploads_Frag;
import com.nkwazi_tech.tuit_app.R;
import com.nkwazi_tech.tuit_app.group.Admin_GroupMod_Frag;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //theme
        String ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Admin Activity");

        intent = getIntent();
        String actiion = intent.getStringExtra("action");
        if (actiion != null){
            Load(actiion);
        }else{
            Toast.makeText(getApplicationContext(),
                   "No action selected",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void Load(String actiion) {
        Fragment fragment = null;
        Class fragmentClass = null;

        if (actiion.equals("Change Course Icons")){
            Admin_CourseIconEdit_Frag.type = "null";
            fragmentClass = Admin_CourseIconEdit_Frag.class;
        }else if (actiion.equals("Change Student Course Icons")){
            Admin_CourseIconEdit_Frag.type = "true";
            fragmentClass = Admin_CourseIconEdit_Frag.class;
        }else if (actiion.equals("Change About-Us")){
            fragmentClass = Admin_AboutusEdit_Frag.class;
        }else if (actiion.equals("ModifyUser")){
            fragmentClass = Admin_UserMod_Frag.class;
            Admin_UserMod_Frag.approval = "false";
        }else if (actiion.equals("ModifyGroups")){
            fragmentClass = Admin_GroupMod_Frag.class;
        }else if (actiion.equals("Approval")){
            fragmentClass = Admin_UserMod_Frag.class;
            Admin_UserMod_Frag.approval = "true";
        }else if (actiion.equals("Change Care Plans")){
            fragmentClass = AdminCarePlansUpload_Frag.class;
        }
        else if (actiion.equals("generalresearch")){
            fragmentClass = GeneralResearch_Uploads_Frag.class;
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == R.id.search_bar) {
            super.onSearchRequested();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.nav_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
       // searchItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, Home_Activity.class);
        this.startActivity(intent);
        onBackPressed();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //the image URI
            Uri url = data.getData();
            Adapter_Dash_Courselist.selectedimg = url;
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(url);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Adapter_Dash_Courselist.carouselimg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
