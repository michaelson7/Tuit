package com.nkwazi_tech.tuit_app.Fragments;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.nkwazi_tech.tuit_app.Classes.Adapter_Explorerlecturers;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_VideoInfo;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import static android.app.Activity.RESULT_OK;

public class Admin_AboutusEdit_Frag extends Fragment implements OnMapReadyCallback {

    RecyclerView recyclerView;
    List<DataHandler_User> dataHandler_User;
    private final int REQUEST_CODE_GALLERY = 999;
    MapView map;
    Uri selectedimg;
    TextView missionstatement;
    TextView addressstatment;
    Button team, mission, address;
    Dialog teamDialog, missoonDialog, addressDialog;
    String mapviewbuncle = "MapViewBundleKey",missiontext,addresstext,Xtext,Ytext;
    ImageView profilepicture;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_aboutus, container, false);

        recyclerView = root.findViewById(R.id.recycler2);
        map = root.findViewById(R.id.mapView);
        missionstatement= root.findViewById(R.id.yeartwo);
        addressstatment= root.findViewById(R.id.yearthree);
        team = root.findViewById(R.id.TeamEdit);
        mission = root.findViewById(R.id.MissionEdit);
        address = root.findViewById(R.id.AddressEdit);
        team.setVisibility(View.VISIBLE);
        mission.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        dataHandler_User = new ArrayList<>();
        teamDialog = new Dialog(getContext());
        missoonDialog = new Dialog(getContext());
        addressDialog = new Dialog(getContext());

        Bundle mapviewbundle = null;
        if (savedInstanceState != null) {
            mapviewbundle = savedInstanceState.getBundle(mapviewbuncle);
        }
        map.onCreate(mapviewbundle);
        map.getMapAsync(this);

        Default();
        return root;
    }

    private void Default() {
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

        Button submit,imgselect;
        EditText name;

        profilepicture = teamDialog.findViewById(R.id.imageView9);
        imgselect = teamDialog.findViewById(R.id.propic2);
        name = teamDialog.findViewById(R.id.groupname);
        submit = teamDialog.findViewById(R.id.button2);

        imgselect.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        });
        submit.setOnClickListener(v -> {
            final String getname = name.getText().toString().trim();
            if (selectedimg == null){
                Toast.makeText(getContext(), "Please select profile picture", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(getname)) {
                name.setError("Please enter name");
                name.requestFocus();
                return;
            }
            UpdateTeam(getname,selectedimg);
        });

        teamDialog.show();
    }

    private void UpdateTeam(String name, Uri selectedimg) {
        File profile = null;
        Call<ServerResponse> call = null;
        MultipartBody.Part requestGroupImg  = null;

        try {
            profile = new File(getRealPathFromURI(selectedimg));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
            requestGroupImg = MultipartBody.Part.createFormData("img", profile.getName(), requestBody);
        } catch (Exception e) {}

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

        call = api.updatecmebers(names,requestGroupImg);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {
                    teamDialog.hide();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    FragmentTransaction fr = manager.beginTransaction();
                    fr.replace(R.id.flContent, new Admin_AboutusEdit_Frag());
                    fr.commit();
                } else {
                    //progressBar.setVisibility(View.GONE);
                    //button2.setVisibility(View.VISIBLE);
                    teamDialog.hide();
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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
            UpdateTerms(getMission,"mission","","");
        });

        missoonDialog.show();
    }

    private void ShowAddressDoalog() {
        addressDialog.setContentView(R.layout.dialog_addressedit);
        Objects.requireNonNull(addressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = addressDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText addresss,x,y;

        addresss = addressDialog.findViewById(R.id.practicenumber);
        x = addressDialog.findViewById(R.id.course);
        y = addressDialog.findViewById(R.id.phone);
        submit = addressDialog.findViewById(R.id.button2);

        addresss.setText(addresstext);
        x.setText(Xtext);
        y.setText(Ytext);

        submit.setOnClickListener(v -> {
            final String getaddress = addresss.getText().toString().trim();
            final String getx = x.getText().toString().trim();
            final String gety = y.getText().toString().trim();

            if (TextUtils.isEmpty(getaddress)) {
                address.setError("Please address");
                address.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(getx)) {
                x.setError("Please Enter x");
                x.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(gety)) {
                y.setError("Please y");
                y.requestFocus();
                return;
            }

            UpdateTerms(getaddress,"address",getx,gety);
        });

        addressDialog.show();
    }

    private void UpdateTerms(String terms,String data,String X,String Y) {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                if (data.equals("mission")){
                    params.put("mission", String.valueOf(terms));
                }else{
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
                    Toast.makeText(getContext(),
                            obj.getString("message"),
                            Toast.LENGTH_LONG).show();
                    if (data.equals("mission")){
                        missoonDialog.hide();
                        missionstatement.setText(terms);
                        FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                        FragmentTransaction fr = manager.beginTransaction();
                        fr.replace(R.id.flContent, new Admin_AboutusEdit_Frag());
                        fr.commit();
                    }else{
                        addressDialog.hide();
                        FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                        FragmentTransaction fr = manager.beginTransaction();
                        fr.replace(R.id.flContent, new Admin_AboutusEdit_Frag());
                        fr.commit();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void LoadData(GoogleMap googleMap) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getmembers+schoolId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                            product.getInt("id"),
                                            product.getInt("courseid"),
                                            product.getString("email"),
                                            product.getString("phonenumber"),
                                            product.getString("name"),
                                            product.getString("propic"),
                                            product.getString("coverpic"),
                                            product.getString("accounttype"),
                                            product.getString("practisenumber"),
                                            product.getString("lecturercourse"),
                                            product.getInt("schoolId")
                                    ));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            missiontext = obj.getString("mission");
                            addresstext = obj.getString("address");
                            Xtext = obj.getString("x");
                            Ytext = obj.getString("y");

                            missionstatement.setText(missiontext);
                            addressstatment.setText(addresstext);
                            Adapter_Explorerlecturers adapter = new Adapter_Explorerlecturers(getContext(), dataHandler_User,"admin");
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);

                            //settup Map
                            double lat = Double.parseDouble(Xtext);
                            double longi = Double.parseDouble(Ytext);

                            LatLng sydney = new LatLng(lat,longi);
                            googleMap.addMarker(new MarkerOptions().position(sydney)
                                    .title("Our Location"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                                error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {

                            Toast.makeText(getContext(),
                                    "Please check your internet connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getContext(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
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
                InputStream inputStream = getContext().getContentResolver().openInputStream(url);
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
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LoadData(googleMap);

        //-15.419620
        //28.283150
    }

    @Override
    public void onResume() {
        map.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }
}
