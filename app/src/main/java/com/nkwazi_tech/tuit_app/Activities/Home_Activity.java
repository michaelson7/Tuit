package com.nkwazi_tech.tuit_app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_FirebaseUsers;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoPlayerInfo;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.Fragments.Book_Library_Frag;
import com.nkwazi_tech.tuit_app.Fragments.ExplorerCourse_Frag;
import com.nkwazi_tech.tuit_app.Fragments.GeneralResearchView_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Home_Frag;
import com.nkwazi_tech.tuit_app.Fragments.LecturerDashbored_Frag;
import com.nkwazi_tech.tuit_app.Fragments.MyCourses_Frag;
import com.nkwazi_tech.tuit_app.Fragments.News_Frag;
import com.nkwazi_tech.tuit_app.Fragments.NursingCarePlans_header_Frag;
import com.nkwazi_tech.tuit_app.Fragments.NursingResearch_Frag;
import com.nkwazi_tech.tuit_app.Fragments.StudentDashbord_Frag;
import com.nkwazi_tech.tuit_app.Fragments.student_dash_frag;
import com.nkwazi_tech.tuit_app.Fragments.Subscription_center_Frag;
import com.nkwazi_tech.tuit_app.Fragments.VideoPlayer_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Video_Library_Frag;
import com.nkwazi_tech.tuit_app.Fragments.student_dash_frag;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Objects.*;

public class Home_Activity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Dialog verification;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    TextView name;
    public static MenuItem sorting,search_icon;
    Dialog termsDialog, policesDialog, adminaddDialog, suspensionDialog;
    String[] options, options2;
    String Terms, Policies;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //theme
        String ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //screenshots
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        termsDialog = new Dialog(this);
        policesDialog = new Dialog(this);
        adminaddDialog = new Dialog(this);
        suspensionDialog = new Dialog(this);
        verification = new Dialog(this);

        CheckSuspension(SharedPrefManager.getInstance(this).getID());
        Check_Exp_Date(SharedPrefManager.getInstance(this).getID());

        FirebaseMessaging.getInstance().subscribeToTopic("global");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        mDrawer.addDrawerListener(drawerToggle);

        //text in menu
        View header = nvDrawer.getHeaderView(0);
        name = header.findViewById(R.id.navName);
        TextView email = header.findViewById(R.id.navEmail);
        CircleImageView Proimg = header.findViewById(R.id.navImg);
        ImageView Coverimg = header.findViewById(R.id.navCover);

        int mul = 0xFF7F7F7F;
        int add = 0x00000000;
        LightingColorFilter lcf = new LightingColorFilter(mul, add);
        Coverimg.setColorFilter(lcf);

        Coverimg.setImageResource(R.mipmap.black);

        String profileimg = SharedPrefManager.getInstance(getApplicationContext()).getPropic();
        String coverimg = SharedPrefManager.getInstance(getApplicationContext()).getCoverpic();
        String accountype = SharedPrefManager.getInstance(getApplicationContext()).getAccounttype();
        String user_ID = String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getID());

        email.setText(SharedPrefManager.getInstance(getApplicationContext()).getEmail());
        name.setText(SharedPrefManager.getInstance(getApplicationContext()).getName());

        if (!profileimg.contains("/null")) {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .error(R.mipmap.girl)
                    .load(SharedPrefManager.getInstance(getApplicationContext()).getPropic())
                    .into(Proimg);
        }

        if (!coverimg.contains("/null")) {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .error(R.mipmap.girl)
                    .load(SharedPrefManager.getInstance(getApplicationContext()).getCoverpic())
                    .into(Coverimg);
        }
        //firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        } catch (Exception e) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child("oJaVuaAPPyZio8aQ025xXRD8ByP2");
            e.printStackTrace();
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataHandler_FirebaseUsers user = dataSnapshot.getValue(DataHandler_FirebaseUsers.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //creating click events for bottom navigation
        BottomNavigationView bn = findViewById(R.id.Bottombar);
        bn.setOnNavigationItemSelectedListener(bottomnavlis);

        //notification handler
        Bundle bundle = getIntent().getExtras();
        String message = null;
        try {
            message = bundle.getString("message");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //checking notification state
        if (message != null) {

            Fragment fragment = null;
            Class fragmentClass = null;

            //opening appropriate page depending on state
            if (message.equals("news")) {
                fragmentClass = News_Frag.class;
            } else if (message.equals("group_message")) {
                startActivity(new Intent(getApplicationContext(), GroupDiscussion_activity.class));
            } else if (message.contains("video")) {
                String topic = message.replaceAll("video", "");
                topic = topic.trim();

                Check_Subscription(user_ID, topic);
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (accountype.equals("student")) {
                StudentDash();
            } else {
                LecturerDashboard();
            }
        }

        if (!accountype.equals("admin")) {
            Menu nav_Menu = nvDrawer.getMenu();
            nav_Menu.findItem(R.id.nav_ui).setVisible(false);
            nav_Menu.findItem(R.id.nav_accounts).setVisible(false);
        }
        LoadTerms();
    }

    private void Check_Subscription(String user_id, String video_id) {
        String data = "&userID=" + user_id + "&video_id=" + video_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.notification_Response + data,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);

                        //if not subscribed
                        if (!obj.getBoolean("error")) {
                            setTitle("Subscribe");
                            JSONArray array = obj.getJSONArray("Products");
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                ExplorerCourse_Frag.coursename = product.getString("coursename");
                                ExplorerCourse_Frag.courseida = product.getString("courseid");
                                FragmentManager manager = this.getSupportFragmentManager();
                                FragmentTransaction fr = manager.beginTransaction();
                                fr.replace(R.id.flContent, new ExplorerCourse_Frag());
                                fr.addToBackStack(null);
                                fr.commit();
                            }

                        } else {
                            //if subscribed
                            setTitle("Videos");
                            JSONArray array = obj.getJSONArray("Products");
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                DataHandler_VideoPlayerInfo.getInstance(this).DataHandler_VideoPlayerInfo(
                                        product.getString("title"),
                                        product.getString("description"),
                                        product.getString("videopath"),
                                        product.getString("title"),
                                        product.getString("propic"),
                                        0,
                                       0,
                                        product.getInt("views"),
                                        product.getInt("videoid"),
                                        ""
                                );
                                VideoPlayer_Frag.views_Num = String.valueOf(product.getInt("views"));
                                VideoPlayer_Frag.duration_Num = product.getString("file_Duration");
                                VideoPlayer_Frag.date_Num = product.getString("timestamp");

                                String ThemeState = SharedPrefManager.getInstance(this).getTheme();
                                if (ThemeState != null && ThemeState.equals("dark")) {
                                    DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                                    dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                                    dialogFragment.show(this.getSupportFragmentManager(), "tag");
                                } else {
                                    DialogFragment dialogFragment = VideoPlayer_Frag.newInstanse();
                                    dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MainDialog);
                                    dialogFragment.show(this.getSupportFragmentManager(), "tag");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void Check_Exp_Date(int id) {
        String data = "&userID=" + id + "&state=home_check&courseid=0";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.check_Subscription + data,
                response -> {
                },
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void CheckSuspension(int id) {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return requestHandler.sendPostRequest(URLs.URL_checksuspension, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    String suspension = obj.getString("suspension");
                    String aproval = obj.getString("adminapproval");
                    String verified = obj.getString("verified");
                    if (suspension.equals("true")) {
                        ShowSuspensionDialog(obj.getString("message"));
                    }
                    if (aproval.equals("false")) {
                        ShowSuspensionDialog(obj.getString("message"));
                    }
                    if (verified.equals("false")) {
                        ShowVerificationDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }


    public void LecturerDashboard() {
        Fragment fragment = null;
        Class fragmentClass;

        fragmentClass = LecturerDashbored_Frag.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Lecturer Dashboard");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }

    public void StudentDash() {
        Fragment fragment = null;
        Class fragmentClass;

        fragmentClass = StudentDashbord_Frag.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Student Dashboard");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener bottomnavlis = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment = null;
            Class fragmentClass;
            switch (menuItem.getItemId()) {
                case R.id.nav_Subscriptions:
                    fragmentClass = Subscription_center_Frag.class;
                    sorting.setVisible(false);
                    break;
                case R.id.nav_Explore:
                    fragmentClass = Video_Library_Frag.class;
                    sorting.setVisible(false);
                    search_icon.setVisible(true);
                    break;
                case R.id.nav_library:
                    fragmentClass = Book_Library_Frag.class;
                    sorting.setVisible(false);
                    break;
                default:
                    fragmentClass = Home_Frag.class;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fr = fragmentManager.beginTransaction();
            fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawer.closeDrawers();
            return true;
        }
    };

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        String accountype = SharedPrefManager.getInstance(getApplicationContext()).getAccounttype();
        Fragment fragment = null;
        final Class[] fragmentClass = new Class[1];
        switch (menuItem.getItemId()) {
            case R.id.nav_CarePlans:
                fragmentClass[0] = NursingCarePlans_header_Frag.class;
                sorting.setVisible(false);
                break;
            case R.id.nav_Home:
                fragmentClass[0] = StudentDashbord_Frag.class;
                sorting.setVisible(false);
                break;
            case R.id.nav_Dashboard:
                fragmentClass[0] = student_dash_frag.class;
                sorting.setVisible(false);
                break;
            case R.id.nav_News:
                fragmentClass[0] = News_Frag.class;
                sorting.setVisible(false);
                break;
            case R.id.nav_research:
                fragmentClass[0] = NursingResearch_Frag.class;
                sorting.setVisible(false);
                break;
            default:
                if (accountype.equals("student")) {
                    fragmentClass[0] = student_dash_frag.class;
                    sorting.setVisible(false);
                } else {
                    fragmentClass[0] = LecturerDashbored_Frag.class;
                    sorting.setVisible(false);
                }
        }

        try {
            fragment = (Fragment) fragmentClass[0].newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

        if (menuItem.getItemId() == R.id.nav_logoutt) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);

            builder.setCancelable(true);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to Sign out?");
            builder.setPositiveButton("confirm", (dialogInterface, i) -> {
                GoogleSignInClient googleSignInClient;
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);

                FirebaseAuth.getInstance().signOut();
                googleSignInClient.signOut();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }

        if (menuItem.getItemId() == R.id.nav_Discussions) {
            startActivity(new Intent(getApplicationContext(), GroupDiscussion_activity.class));
            sorting.setVisible(false);
        }
        if (menuItem.getItemId() == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), Settings_activity.class));
            sorting.setVisible(false);
        }
        if (menuItem.getItemId() == R.id.nav_aboutus) {
            startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
            sorting.setVisible(false);
        }

        if (menuItem.getItemId() == R.id.nav_ui) {
            options = new String[]{"Change Course Icons", "Change Student Course Icons", "Change Care Plans", "Change About-Us", "Change T&C's", "Change General Research Outlines"};
            String[] SortyBy = options;
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog);
            builder.setTitle("Select Option");
            builder.setItems(SortyBy, (dialog, which) -> {
                if (which == 0) {
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.putExtra("action", options[0]);
                    this.startActivity(intent);
                } else if (which == 1) {
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.putExtra("action", options[1]);
                    this.startActivity(intent);
                } else if (which == 2) {
                    options2 = new String[]{"Add Care Plans"};
                    String[] SortyBy2 = options2;
                    androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog);
                    builder2.setTitle("Select Option");
                    builder2.setItems(SortyBy2, (dialog2, which2) -> {
                        if (which2 == 0) {
                            Intent intent = new Intent(this, AdminActivity.class);
                            intent.putExtra("action", options[2]);
                            this.startActivity(intent);
                        }
                    });
                    builder2.show();
                } else if (which == 3) {
                    startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
                    sorting.setVisible(false);
                } else if (which == 4) {
                    options2 = new String[]{"Change Terms & Conditions", "Change Policies"};
                    String[] SortyBy2 = options2;
                    androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog);
                    builder2.setTitle("Select Option");
                    builder2.setItems(SortyBy2, (dialog2, which2) -> {
                        if (which2 == 0) {
                            ShowTermsDialog();
                        } else if (which2 == 1) {
                            ShowPolicesDialog();
                        }
                    });
                    builder2.show();
                } else if (which == 5) {
                    options2 = new String[]{"Add General Research Notes","Edit Current Notes"};
                    String[] SortyBy2 = options2;
                    androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog);
                    builder2.setTitle("Select Option");
                    builder2.setItems(SortyBy2, (dialog2, which2) -> {
                        if (which2 == 0) {
                            Intent intent = new Intent(this, AdminActivity.class);
                            intent.putExtra("action", "generalresearch");
                            this.startActivity(intent);
                        }
                        else if (which2 == 1) {
                            FragmentManager manager = this.getSupportFragmentManager();
                            FragmentTransaction fr = manager.beginTransaction();
                            fr.replace(R.id.flContent, new GeneralResearchView_Frag());
                            fr.addToBackStack(null);
                            fr.commit();
                        }
                    });
                    builder2.show();
                }
            });
            builder.show();
        }
        if (menuItem.getItemId() == R.id.nav_accounts) {
            options = new String[]{"Add Admins", "Account Approval", "User Management", "Video Approval"};
            String[] SortyBy = options;
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog);
            builder.setTitle("Select Option");
            builder.setItems(SortyBy, (dialog, which) -> {
                if (which == 0) {
                    ShowAdminAddDialog();
                } else if (which == 1) {
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.putExtra("action", "Approval");
                    this.startActivity(intent);
                } else if (which == 2) {
                    options2 = new String[]{"Modify User Accounts", "Modify Groups"};
                    String[] SortyBy2 = options2;
                    androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog);
                    builder2.setTitle("Select Option");
                    builder2.setItems(SortyBy2, (dialog2, which2) -> {
                        if (which2 == 0) {
                            Intent intent = new Intent(this, AdminActivity.class);
                            intent.putExtra("action", "ModifyUser");
                            this.startActivity(intent);
                        } else if (which2 == 1) {
                            Intent intent = new Intent(this, AdminActivity.class);
                            intent.putExtra("action", "ModifyGroups");
                            this.startActivity(intent);
                        }
                    });
                    builder2.show();
                } else if (which == 3) {
                    Intent intent = new Intent(this, AdminVideoApproval_Activity.class);
                    //intent.putExtra("action", "ModifyGroups");
                    this.startActivity(intent);
                }
            });

            builder.show();
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void ShowAdminAddDialog() {
        adminaddDialog.setContentView(R.layout.dialog_reset);
        requireNonNull(adminaddDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = adminaddDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView title;
        EditText username, password;
        Button submit;

        title = adminaddDialog.findViewById(R.id.textView3);
        username = adminaddDialog.findViewById(R.id.oldpassword);
        password = adminaddDialog.findViewById(R.id.passwordconfirm);
        submit = adminaddDialog.findViewById(R.id.button2);

        title.setText("Add Admin");
        username.setHint("Enter Email Address");
        password.setHint("Enter Password");
        submit.setText("Submit");

        submit.setOnClickListener(v -> {
            final String getusername = username.getText().toString().trim();
            final String getpassword = password.getText().toString().trim();
            if (TextUtils.isEmpty(getusername)) {
                username.setError("Please enter Email");
                username.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(getpassword)) {
                username.setError("Please enter Password");
                username.requestFocus();
                return;
            }
            UpdateTerms(getusername, "addamin", getpassword);
        });

        adminaddDialog.show();
    }

    private void ShowTermsDialog() {
        termsDialog.setContentView(R.layout.dialog_termsandconditionsedit);
        requireNonNull(termsDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = termsDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText Text;
        TextView title;

        title = termsDialog.findViewById(R.id.textView3);
        Text = termsDialog.findViewById(R.id.textView13);
        submit = termsDialog.findViewById(R.id.button8);

        title.setText("Edit Terms And Conditions");
        Text.setText(Terms);

        submit.setOnClickListener(v -> {
            final String getterms = Text.getText().toString().trim();
            if (TextUtils.isEmpty(getterms)) {
                Text.setError("Please enter Terms");
                Text.requestFocus();
                return;
            }
            UpdateTerms(getterms, "terms", "");
        });

        termsDialog.show();
    }

    private void ShowVerificationDialog() {
        TextView text;
        Button submit;
        EditText code;

        verification.setContentView(R.layout.dialog_verification);
        requireNonNull(verification.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = verification.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        text = verification.findViewById(R.id.veritext);
        submit = verification.findViewById(R.id.btnauthenticate);
        code = verification.findViewById(R.id.vericodetxt);

        text.setText("Please enter the one time verification code sent to \n" + SharedPrefManager.getInstance(this).getEmail());
        submit.setOnClickListener(v -> {

            String veritext = code.getText().toString().trim();

            if (TextUtils.isEmpty(veritext)) {
                code.setError("Enter Verification code");
                code.requestFocus();
                return;
            }

            if (!veritext.equals(SharedPrefManager.getInstance(this).getCode())) {
                Toast.makeText(Home_Activity.this, "Wrong Verification Code", Toast.LENGTH_SHORT).show();
            } else {
                SetVerified();
            }
        });
        verification.setOnDismissListener(dialog -> SharedPrefManager.getInstance(getApplicationContext()).logout());
        verification.show();
    }

    private void SetVerified() {
        int id = SharedPrefManager.getInstance(this).getID();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_setVerified + "&id=" + id,
                response -> {
                    Toast.makeText(Home_Activity.this, "Account Verified", Toast.LENGTH_SHORT).show();
                    Home_Activity.this.finish();
                    startActivity(new Intent(Home_Activity.this, Home_Activity.class));
                },
                error -> Toast.makeText(Home_Activity.this, error.toString(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(Home_Activity.this).add(stringRequest);
    }

    private void ShowSuspensionDialog(String message) {
        suspensionDialog.setContentView(R.layout.dialog_termsandconditions);
        requireNonNull(suspensionDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = suspensionDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView title, text;

        title = suspensionDialog.findViewById(R.id.textView3);
        text = suspensionDialog.findViewById(R.id.textView13);

        title.setText("Account Status");
        text.setText(message);
        text.setGravity(Gravity.CENTER);
        mDrawer.setVisibility(View.GONE);
        text.setTextSize(18);

        suspensionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });
        suspensionDialog.show();
    }

    private void ShowPolicesDialog() {
        policesDialog.setContentView(R.layout.dialog_termsandconditionsedit);
        requireNonNull(policesDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = policesDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText Text;
        TextView title;

        title = policesDialog.findViewById(R.id.textView3);
        Text = policesDialog.findViewById(R.id.textView13);
        submit = policesDialog.findViewById(R.id.button8);

        title.setText("Edit Policies");
        Text.setText(Policies);

        submit.setOnClickListener(v -> {
            final String getterms = Text.getText().toString().trim();
            if (TextUtils.isEmpty(getterms)) {
                Text.setError("Please enter Policies");
                Text.requestFocus();
                return;
            }
            UpdateTerms(getterms, "policies", "");
        });
        policesDialog.show();
    }

    private void UpdateTerms(String terms, String data, String data3) {
        int id = SharedPrefManager.getInstance(this).getID();
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                if (data.equals("terms")) {
                    params.put("terms", String.valueOf(terms));

                } else if (data.equals("addamin")) {
                    params.put("email", String.valueOf(terms));
                    params.put("password", String.valueOf(data3));
                } else {
                    params.put("policies", String.valueOf(terms));
                }
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_updateterms, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //  progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //  progressBar.setVisibility(View.GONE);

                try {
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    Toast.makeText(getApplicationContext(),
                            obj.getString("message"),
                            Toast.LENGTH_LONG).show();
                    if (data.equals("terms")) {
                        termsDialog.hide();
                    } else if (data.equals("addamin")) {
                        add_admin(terms,data3);
                        adminaddDialog.hide();
                    } else {
                        policesDialog.hide();
                    }

                    if (obj.getBoolean("admin")) {
                        firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.createUserWithEmailAndPassword(terms, data3)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                            String userid = firebaseUser.getUid();

                                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                            FirebaseMessaging.getInstance().subscribeToTopic("global");

                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("id", userid);
                                            hashMap.put("imageURL", "default");

                                            reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                                if (task.isSuccessful()) {
                                                    try {
                                                        Toast.makeText(Home_Activity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    Toast.makeText(Home_Activity.this, "Error while registering", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            String localizedMessage = task.getException().getLocalizedMessage();
                                            Toast.makeText(Home_Activity.this, localizedMessage + " "+terms, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void add_admin(String email, String password) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            FirebaseMessaging.getInstance().subscribeToTopic("global");

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("imageURL", "default");

                            reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Error while registering", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });
                        } else {
                            String localizedMessage = task.getException().getLocalizedMessage();
                            Toast.makeText(this, localizedMessage, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadTerms() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getTP,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        Terms = obj.getString("Terms");
                        Policies = obj.getString("Policies");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                        Toast.makeText(getApplicationContext(),
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.search_bar:
                //start search dialog
                super.onSearchRequested();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.nav_toolbar, menu);
        sorting = menu.findItem(R.id.sorting);
        search_icon= menu.findItem(R.id.search_bar);
        return true;
    }

}
