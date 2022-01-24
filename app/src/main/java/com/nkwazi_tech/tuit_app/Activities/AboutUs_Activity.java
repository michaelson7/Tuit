package com.nkwazi_tech.tuit_app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.nkwazi_tech.tuit_app.Classes.Adapter_Explorerlecturers;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.Fragments.PDFviewer_Frag;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.maps.MapView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AboutUs_Activity extends AppCompatActivity {

    ScrollView constraintLayout;
    ProgressBar progressBar2;
    List<DataHandler_User> dataHandler_User;
    private final int REQUEST_CODE_GALLERY = 999;
    Uri selectedimg;
    TextView missionstatement;
    TextView addressstatment;
    Button team, mission, address, policy, site, agreement, terms;
    Dialog teamDialog, missoonDialog, addressDialog;
    String missiontext;
    String addresstext;
    ImageView profilepicture;
    String accounttype = SharedPrefManager.getInstance(this).getAccounttype();
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(this).getSchoolId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ThemeState = SharedPrefManager.getInstance(getApplicationContext()).getTheme();
        if (ThemeState != null && ThemeState.equals("dark")) {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("About Us2");

        missionstatement = findViewById(R.id.yeartwo);
        addressstatment = findViewById(R.id.yearthree);
        constraintLayout = findViewById(R.id.scroll);
        progressBar2 = findViewById(R.id.progressBar20);

        team = findViewById(R.id.TeamEdit);
        mission = findViewById(R.id.MissionEdit);
        address = findViewById(R.id.AddressEdit);

        policy = findViewById(R.id.button21);
        site = findViewById(R.id.button12);
        agreement = findViewById(R.id.button20);
        terms = findViewById(R.id.button19);

        site.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://myhost.nkwazitech.com/official/"));
            startActivity(browserIntent);
        });

        policy.setOnClickListener(v -> {
            pdf_viewer_activity.pdffilepath = "http://myhost.nkwazitech.com/privacy.pdf";
            Intent intent = new Intent(this, pdf_viewer_activity.class);
            startActivity(intent);
        });
        agreement.setOnClickListener(v -> {
            pdf_viewer_activity.pdffilepath = "http://myhost.nkwazitech.com/licence.pdf";
            Intent intent = new Intent(this, pdf_viewer_activity.class);
            startActivity(intent);
        });

        terms.setOnClickListener(v -> {
            pdf_viewer_activity.pdffilepath = "http://myhost.nkwazitech.com/rules_reg.pdf";
            Intent intent = new Intent(this, pdf_viewer_activity.class);
            startActivity(intent);
        });

        if (accounttype.equals("admin")) {
            mission.setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
        }

        dataHandler_User = new ArrayList<>();

        teamDialog = new Dialog(this);
        missoonDialog = new Dialog(this);
        addressDialog = new Dialog(this);

        Log.d("SchoolId:",schoolId);
        LoadData();
        Default();
    }

    private void LoadData() {
        Log.d("SchoolId:",schoolId);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getmembers+schoolId,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {

                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);
                            //adding the product to product list class
                            try {
                                dataHandler_User.add(new DataHandler_User(
                                        product.getInt("id"), 0, "", "",
                                        product.getString("name"),
                                        product.getString("img"), "", "", "", "", 0
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        missiontext = obj.getString("mission");
                        addresstext = obj.getString("address");

                        missionstatement.setText(missiontext);
                        addressstatment.setText(addresstext);

                        constraintLayout.setVisibility(View.VISIBLE);
                        progressBar2.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(AboutUs_Activity.this, error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                        Toast.makeText(AboutUs_Activity.this,
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(AboutUs_Activity.this).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void Default() {
        constraintLayout.setVisibility(View.GONE);
        progressBar2.setVisibility(View.VISIBLE);

        team.setOnClickListener(v -> {
            ShowMembersDialog();
        });
        mission.setOnClickListener(v -> {
            ShowMissionDialog();
        });
        address.setOnClickListener(v -> {
            ShowAddressDoalog();
        });
    }

    private void ShowMembersDialog() {
        teamDialog.setContentView(R.layout.dialog_aboutusmembersedit);
        Objects.requireNonNull(teamDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = teamDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit, imgselect;
        EditText name;

        profilepicture = teamDialog.findViewById(R.id.imageView9);
        imgselect = teamDialog.findViewById(R.id.propic2);
        name = teamDialog.findViewById(R.id.groupname);
        submit = teamDialog.findViewById(R.id.button2);

        imgselect.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        });
        submit.setOnClickListener(v -> {
            final String getname = name.getText().toString().trim();
            if (selectedimg == null) {
                Toast.makeText(AboutUs_Activity.this, "Please select profile picture", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(getname)) {
                name.setError("Please enter name");
                name.requestFocus();
                return;
            }
            UpdateTeam(getname, selectedimg);
        });

        teamDialog.show();
    }

    private void UpdateTeam(String name, Uri selectedimg) {
        File profile = null;
        Call<ServerResponse> call = null;
        MultipartBody.Part requestGroupImg = null;

        try {
            profile = new File(getRealPathFromURI(selectedimg));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
            requestGroupImg = MultipartBody.Part.createFormData("img", profile.getName(), requestBody);
        } catch (Exception e) {
        }

        RequestBody names = RequestBody.create(MediaType.parse("text/plain"), name);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //creating our api
        MediaHandler api = retrofit.create(MediaHandler.class);

        call = api.updatecmebers(names, requestGroupImg);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    teamDialog.hide();
                    Toast.makeText(AboutUs_Activity.this, response.body().message, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
                } else {
                    //progressBar.setVisibility(View.GONE);
                    //button2.setVisibility(View.VISIBLE);
                    teamDialog.hide();
                    Toast.makeText(AboutUs_Activity.this, response.body().message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(AboutUs_Activity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ShowMissionDialog() {
        missoonDialog.setContentView(R.layout.dialog_termsandconditionsedit);
        Objects.requireNonNull(missoonDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = missoonDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText Text;
        TextView title;

        title = missoonDialog.findViewById(R.id.textView3);
        Text = missoonDialog.findViewById(R.id.textView13);
        submit = missoonDialog.findViewById(R.id.button8);

        title.setText("Edit Misson");
        Text.setText(missiontext);
        //Text.setText(Terms);

        submit.setOnClickListener(v -> {
            final String getMission = Text.getText().toString().trim();
            if (TextUtils.isEmpty(getMission)) {
                Text.setError("Please Mission");
                Text.requestFocus();
                return;
            }
            UpdateTerms(getMission, "mission", "", "");
        });

        missoonDialog.show();
    }

    private void ShowAddressDoalog() {
        addressDialog.setContentView(R.layout.dialog_addressedit);
        Objects.requireNonNull(addressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = addressDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText addresss, x, y;

        addresss = addressDialog.findViewById(R.id.practicenumber);
        x = addressDialog.findViewById(R.id.course);
        y = addressDialog.findViewById(R.id.phone);
        submit = addressDialog.findViewById(R.id.button2);

        addresss.setText(addresstext);
        x.setVisibility(View.GONE);
        y.setVisibility(View.GONE);

        submit.setOnClickListener(v -> {
            final String getaddress = addresss.getText().toString().trim();

            if (TextUtils.isEmpty(getaddress)) {
                address.setError("Please address");
                address.requestFocus();
                return;
            }

            UpdateTerms(getaddress, "address", "0", "0");
        });

        addressDialog.show();
    }

    private void UpdateTerms(String terms, String data, String X, String Y) {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                if (data.equals("mission")) {
                    params.put("mission", String.valueOf(terms));
                } else {
                    params.put("address", String.valueOf(terms));
                    params.put("X", String.valueOf(X));
                    params.put("Y", String.valueOf(Y));
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
                    if (data.equals("mission")) {
                        missoonDialog.hide();
                        missionstatement.setText(terms);
                        startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
                    } else {
                        addressDialog.hide();
                        startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(AboutUs_Activity.this, "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri url = data.getData();
            selectedimg = url;
            try {
                InputStream inputStream = AboutUs_Activity.this.getContentResolver().openInputStream(url);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profilepicture.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(AboutUs_Activity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == R.id.search_bar) {
            Toast.makeText(this, "item2 pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.nav_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        searchItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, Home_Activity.class));
        return true;
    }
}
