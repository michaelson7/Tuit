package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.Adapter_MyCourse;
import com.nkwazi_tech.tuit_app.Classes.Adapter_MyCourse2;
import com.nkwazi_tech.tuit_app.Classes.Adapter_MyCourse3;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_MyCourse;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_MyCourse2;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_MyCourse3;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class Profile_Frag extends Fragment {
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
    private Button update, edituser, lecturerdialog, change_passworf;
    TextView profilepicture, coverpicture;
    private final int REQUEST_CODE_GALLERY = 999;
    private String imgselector;
    private CircleImageView propic;
    private ImageView cover;
    private Uri selectedpro, selectedcover;
    private ProgressBar p;
    private TextView name, phone, email, pnum, lcourse;
    private Dialog UserEditDialog, LecturerDialog, CourseSelect;
    private ProgressDialog pd;
    TextView course;
    CardView lecturer;
    ArrayList<String> Courses = new ArrayList<String>();
    ArrayList<String> selectedcourse = new ArrayList<String>();
    ArrayList<String> array_course_id = new ArrayList<String>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_profile, container, false);

        getActivity().setTitle("Profile");
        name = root.findViewById(R.id.names);
        phone = root.findViewById(R.id.phone);
        email = root.findViewById(R.id.course);
        p = root.findViewById(R.id.progressBar10);
        profilepicture = root.findViewById(R.id.propic);
        coverpicture = root.findViewById(R.id.coverpic);
        propic = root.findViewById(R.id.imageView6);
        cover = root.findViewById(R.id.imageView5);
        update = root.findViewById(R.id.button16);
        edituser = root.findViewById(R.id.edituser);
        pnum = root.findViewById(R.id.practicenumber);
        lcourse = root.findViewById(R.id.lcourse);
        lecturerdialog = root.findViewById(R.id.lecturerdialog);
        change_passworf = root.findViewById(R.id.button22);
        lecturer = root.findViewById(R.id.lecturcard);
        UserEditDialog = new Dialog(getContext());
        LecturerDialog = new Dialog(getContext());
        CourseSelect = new Dialog(getContext());
        Setdefaults();

        return root;
    }

    private void Setdefaults() {
        String profileimg = SharedPrefManager.getInstance(getContext()).getPropic();
        String coverimg = SharedPrefManager.getInstance(getContext()).getCoverpic();
        String accounttype = SharedPrefManager.getInstance(getContext()).getAccounttype();

        if (accounttype.equals("student")) {
            lecturer.setVisibility(View.GONE);
        }
        if (!profileimg.contains("/null")) {
            Glide.with(Objects.requireNonNull(getContext()))
                    .asBitmap()
                    .load(SharedPrefManager.getInstance(getContext()).getPropic())
                    .error(R.mipmap.girl)
                    .into(propic);
        }
        if (!coverimg.contains("/null")) {
            Glide.with(Objects.requireNonNull(getContext()))
                    .asBitmap()
                    .load(SharedPrefManager.getInstance(getContext()).getCoverpic())
                    .error(R.mipmap.girl)
                    .into(cover);
        }

        propic.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // this will request for permission when user has not granted permission for the app
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                String img = SharedPrefManager.getInstance(getContext()).getPropic();
                Uri uri = Uri.parse(img);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                int min = 111111111;
                int max = 999999980;
                int random = new Random().nextInt((max - min) + 1) + min;
                String num = String.valueOf(random);
                request.setTitle(num + ".jpg");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, num + ".jpg");
                DownloadManager manager = (DownloadManager) Objects.requireNonNull(getContext()).getSystemService(Context.DOWNLOAD_SERVICE);
                assert manager != null;
                manager.enqueue(request);
            }
        });

        name.setText(SharedPrefManager.getInstance(getContext()).getName());
        phone.setText(SharedPrefManager.getInstance(getContext()).getPhone());
        email.setText(SharedPrefManager.getInstance(getContext()).getEmail());
        pnum.setText(SharedPrefManager.getInstance(getContext()).getPractisenumber());

        try {
            lcourse.setText(SharedPrefManager.getInstance(getContext()).getCoursename1() + ", " +
                    SharedPrefManager.getInstance(getContext()).getCoursename2() + ", " +
                    SharedPrefManager.getInstance(getContext()).getCoursename3());
        } catch (Exception e) {
            lcourse.setVisibility(View.GONE);
        }

        profilepicture.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            imgselector = "ProPic";
        });
        coverpicture.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            imgselector = "coverpicture";
        });

        update.setOnClickListener(v -> {
            if (selectedpro == null && selectedcover == null) {
                Snackbar.make(Objects.requireNonNull(getView()), "Select Profile And Cover Photo", Snackbar.LENGTH_LONG).show();
            } else {
                updateaccount(selectedpro, selectedcover);
            }
        });

        change_passworf.setOnClickListener(v -> {
            change_pass();
        });

        edituser.setOnClickListener(v -> EditUserDialog());
        lecturerdialog.setOnClickListener(v -> EditLecturerDialog());
    }

    private void change_pass() {
        Button submit;
        EditText name, email, phone, password;
        TextView dob,title;

        UserEditDialog.setContentView(R.layout.dialog_userinfoedit);
        Objects.requireNonNull(UserEditDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = UserEditDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        submit = UserEditDialog.findViewById(R.id.button2);
        name = UserEditDialog.findViewById(R.id.practicenumber);
        email = UserEditDialog.findViewById(R.id.course);
        phone = UserEditDialog.findViewById(R.id.phone);
        password = UserEditDialog.findViewById(R.id.address);
        dob = UserEditDialog.findViewById(R.id.dateofbirth);
        title = UserEditDialog.findViewById(R.id.textView3);


        dob.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        phone.setVisibility(View.GONE);

        email.setHint("Enter new Password");
        password.setHint("Confirm old Password");
        title.setText("Change Password");

        submit.setOnClickListener(v -> {

            final String getPassword = email.getText().toString().trim();
            final String getCOnfirmPassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(getPassword)) {
                email.setError("Please enter password");
                email.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(getCOnfirmPassword)) {
                password.setError("Please confirm password");
                password.requestFocus();
                return;
            }

            String user_email = SharedPrefManager.getInstance(getContext()).getEmail();

            //check if password is alright
            FirebaseUser user;
            user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user_email,getCOnfirmPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    user.updatePassword(getPassword).addOnCompleteListener(task1 -> {
                        if(!task1.isSuccessful()){
                            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "password updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "wrong password entered", Toast.LENGTH_SHORT).show();
                }
            });
        });

        UserEditDialog.show();
    }

    private void EditLecturerDialog() {
        LoadCourses();

        Button submit;
        EditText pnumber;
        TextView course;

        LecturerDialog.setContentView(R.layout.dialog_lecturerinfoedit);
        Objects.requireNonNull(LecturerDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = LecturerDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        submit = LecturerDialog.findViewById(R.id.button2);
        pnumber = LecturerDialog.findViewById(R.id.practicenumber);
        course = LecturerDialog.findViewById(R.id.course);

        pnumber.setText(SharedPrefManager.getInstance(getContext()).getPractisenumber());
        course.setText(SharedPrefManager.getInstance(getContext()).getLecturcourse());

        course.setOnClickListener(v -> {
            selectedcourse.clear();
            String[] Option = Courses.toArray(new String[0]);
            final int[] count = {0};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
            builder.setTitle("Choose 3 courses");

            builder.setMultiChoiceItems(Option, null, (dialog, which, isChecked) -> {
                if (isChecked) {
                    count[0] = count[0] + 1;
                    if (count[0] > 3) {
                        Toast.makeText(getContext(),
                                "Maximum number of courses selected", Toast.LENGTH_SHORT)
                                .show();

                    } else if (count[0] < 4) {
                        selectedcourse.add(Option[which]);
                        Toast.makeText(getContext(),
                                Option[which], Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {
                    count[0] = count[0] - 1;
                    selectedcourse.remove(Option[which]);
                }
            });

            // Add OK and Cancel buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                if (count[0] > 3) {
                    Toast.makeText(getContext(),
                            "Please select only 3 courses", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    StringBuilder builder1 = new StringBuilder();
                    String[] arr = selectedcourse.toArray(new String[0]);
                    for (String s : arr) {
                        builder1.append(s).append(", ");
                        course.setText(builder1.toString());
                    }
                }
            });
            builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        submit.setOnClickListener(v ->

        {
            final String GetPnum = pnumber.getText().toString().trim();
            final String GetCourse = course.getText().toString().trim();

            if (TextUtils.isEmpty(GetPnum)) {
                pnumber.setError("Please enter practise number");
                pnumber.requestFocus();
                return;
            }

            int id = SharedPrefManager.getInstance(getContext()).getID();
            UpdateLecturerinfo(id, GetPnum, GetCourse);

        });

        LecturerDialog.show();

    }

    private void EditUserDialog() {
        Button submit;
        EditText name, email, phone, password;
        TextView dob;

        UserEditDialog.setContentView(R.layout.dialog_userinfoedit);
        Objects.requireNonNull(UserEditDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = UserEditDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        submit = UserEditDialog.findViewById(R.id.button2);
        name = UserEditDialog.findViewById(R.id.practicenumber);
        email = UserEditDialog.findViewById(R.id.course);
        phone = UserEditDialog.findViewById(R.id.phone);
        password = UserEditDialog.findViewById(R.id.address);
        dob = UserEditDialog.findViewById(R.id.dateofbirth);

        dob.setVisibility(View.GONE);

        name.setText(SharedPrefManager.getInstance(getContext()).getName());
        phone.setText(SharedPrefManager.getInstance(getContext()).getPhone());
        email.setText(SharedPrefManager.getInstance(getContext()).getEmail());

        submit.setOnClickListener(v -> {
            final String Getname = name.getText().toString().trim();
            final String Getemail = email.getText().toString().trim();
            final String Getphone = phone.getText().toString().trim();
            final String getPassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(Getname)) {
                name.setError("Please enter name");
                name.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(Getemail)) {
                email.setError("Please enter email");
                email.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(Getphone)) {
                phone.setError("Please enter phone number");
                phone.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(getPassword)) {
                phone.setError("Please enter password");
                phone.requestFocus();
                return;
            }

            String user_email = SharedPrefManager.getInstance(getContext()).getEmail();
            int id = SharedPrefManager.getInstance(getContext()).getID();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //check if password is alright
            firebaseAuth.signInWithEmailAndPassword(user_email, getPassword)
                    .addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), task -> {
                        if (task.isSuccessful()) {
                            UpdateUsersInformation(Getname, Getemail, Getphone, id, getPassword);
                        } else {
                            Toast.makeText(getContext(), "Wrong password entered", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        UserEditDialog.show();
    }

    private void UpdateLecturerinfo(int id, String practisenum, String course) {

        String[] Option = selectedcourse.toArray(new String[0]);
        String[] course_id = array_course_id.toArray(new String[0]);

        @SuppressLint("StaticFieldLeak")
        class RegisterUser extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("practisenum", practisenum);
                params.put("course", course);

                try {
                    params.put("lecturercourse1", Option[0]);
                    params.put("lecturercourse2", Option[1]);
                    params.put("lecturercourse3", Option[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                params.put("id", String.valueOf(id));

                return requestHandler.sendPostRequest(URLs.URL_updatelecturer, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(getContext());
                pd.setMessage("Uploading Account...");
                pd.setIndeterminate(false);
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                pd.hide();
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        //getting the dataHandlerUser from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new dataHandlerUser object
                        //selecting values from table
                        DataHandler_User dataHandlerUser = new DataHandler_User(
                                userJson.getInt("id"),
                                userJson.getInt("courseid"),
                                userJson.getString("email"),
                                userJson.getString("phonenumber"),
                                userJson.getString("name"),
                                userJson.getString("propic"),
                                userJson.getString("coverpic"),
                                userJson.getString("accounttype"),
                                userJson.getString("practisenumber"),
                                userJson.getString("lecturercourse"),
                                userJson.getInt("schoolId")
                        );

                        //storing the dataHandlerUser in shared preferences
                        SharedPrefManager.getInstance(getContext()).userLogin(dataHandlerUser);
                        SharedPrefManager.getInstance(getContext()).setLecturcourse(userJson.getString("lecturercourse"));
                        SharedPrefManager.getInstance(getContext()).setCourseid(userJson.getInt("courseid"));
                        SharedPrefManager.getInstance(getContext()).setCoursename1(userJson.getString("lecturercourse"));
                        try {
                            SharedPrefManager.getInstance(getContext()).setPractisenumber(practisenum);

                            JSONArray array = obj.getJSONArray("user_2");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                if (i == 0) {

                                } else if (i == 1) {
                                    SharedPrefManager.getInstance(getContext()).setCourseid2(product.getInt("courseid"));
                                    SharedPrefManager.getInstance(getContext()).setCoursename2(product.getString("lecturercourse"));

                                } else if (i == 2) {
                                    SharedPrefManager.getInstance(getContext()).setCourseid3(product.getInt("courseid"));
                                    SharedPrefManager.getInstance(getContext()).setCoursename3(product.getString("lecturercourse"));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //ShowVerificationDialog();
                        LecturerDialog.hide();
                        pnum.setText(practisenum);
                        lcourse.setText(course);

                    } else {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void UpdateUsersInformation(String Dname, String Demail, String Dphone, int id, String getPassword) {


        @SuppressLint("StaticFieldLeak")
        class RegisterUser extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("name", Dname);
                params.put("email", Demail);
                params.put("phone", Dphone);
                params.put("id", String.valueOf(id));

                return requestHandler.sendPostRequest(URLs.URL_updateuserinfo, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(getContext());
                pd.setMessage("Uploading Account...");
                pd.setIndeterminate(false);
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                pd.hide();
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        //getting the dataHandlerUser from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new dataHandlerUser object
                        //selecting values from table
                        DataHandler_User dataHandlerUser = new DataHandler_User(
                                userJson.getInt("id"),
                                userJson.getInt("courseid"),
                                userJson.getString("email"),
                                userJson.getString("phonenumber"),
                                userJson.getString("name"),
                                userJson.getString("propic"),
                                userJson.getString("coverpic"),
                                userJson.getString("accounttype"),
                                userJson.getString("practisenumber"),
                                userJson.getString("lecturercourse"),
                                userJson.getInt("schoolId")
                        );

                        //storing the dataHandlerUser in shared preferences
                        SharedPrefManager.getInstance(getContext()).userLogin(dataHandlerUser);
                        //ShowVerificationDialog();
                        UserEditDialog.hide();
                        name.setText(SharedPrefManager.getInstance(getContext()).getName());
                        phone.setText(SharedPrefManager.getInstance(getContext()).getPhone());
                        email.setText(SharedPrefManager.getInstance(getContext()).getEmail());

                    } else {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void updateaccount(Uri Profile, Uri Cover) {
        p.setVisibility(View.VISIBLE);
        update.setVisibility(View.INVISIBLE);
        int username = SharedPrefManager.getInstance(getContext()).getID();
        MultipartBody.Part requestprofil = null, requestcover = null;
        File profile, cover2;
        Call<ServerResponse> call;
        RequestBody requestBody;

        try {
            profile = new File(getRealPathFromURI(Profile));
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile);
            requestprofil = MultipartBody.Part.createFormData("propic", profile.getName(), requestBody);

        } catch (Exception e) {
        }

        try {
            cover2 = new File(getRealPathFromURI(Cover));
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), cover2);
            requestcover = MultipartBody.Part.createFormData("coverpic", cover2.getName(), requestBody);
        } catch (Exception e) {
        }

        RequestBody usernames = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(username));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MediaHandler api = retrofit.create(MediaHandler.class);

        call = api.updateaccount(requestprofil, requestcover, usernames);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //put response in array
                assert response.body() != null;
                if (!response.body().error) {

                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                    p.setVisibility(View.GONE);
                    update.setVisibility(View.VISIBLE);

                    SharedPrefManager.getInstance(getContext()).setProImg(response.body().propic);
                    SharedPrefManager.getInstance(getContext()).setCoverimg(response.body().coverpic);

                    String profileimg = SharedPrefManager.getInstance(getContext()).getPropic();
                    String coverimg = SharedPrefManager.getInstance(getContext()).getCoverpic();

                    Glide.with(Objects.requireNonNull(getContext()))
                            .asBitmap()
                            .load(SharedPrefManager.getInstance(getContext()).getPropic())

                            .error(R.mipmap.girl)
                            .into(propic);

                    Glide.with(Objects.requireNonNull(getContext()))
                            .asBitmap()
                            .load(SharedPrefManager.getInstance(getContext()).getCoverpic())
                            .error(R.mipmap.girl)
                            .into(cover);


                } else {
                    Snackbar.make(Objects.requireNonNull(getView()), "Error while uploading account", Snackbar.LENGTH_LONG).show();
                    p.setVisibility(View.GONE);
                    update.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void LoadCourses() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getcourse+schoolId,
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

                                // adding the product to product list class
                                Courses.add(product.getString("coursename"));
                                array_course_id.add(product.getString("courseid"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getActivity(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null && imgselector == "ProPic") {
            Uri url = data.getData();
            selectedpro = url;
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(url);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                propic.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null && imgselector == "coverpicture") {
            Uri url = data.getData();
            selectedcover = url;
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(url);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                cover.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void CourseSelectDialog() {
        RecyclerView year1, year2, year3;
        TextView yearone, yeartwo, yearthree;
        CardView y1, y2, y3;
        Button b1, b2, b3;
        CourseSelect.setContentView(R.layout.dialog_courseselect);
        Objects.requireNonNull(CourseSelect.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = CourseSelect.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        year1 = CourseSelect.findViewById(R.id.yearone);
        year2 = CourseSelect.findViewById(R.id.yearone1);
        year3 = CourseSelect.findViewById(R.id.yearone2);
        yearone = CourseSelect.findViewById(R.id.textView49);
        yeartwo = CourseSelect.findViewById(R.id.textView491);
        yearthree = CourseSelect.findViewById(R.id.textView492);
        y1 = CourseSelect.findViewById(R.id.cardView6);
        y2 = CourseSelect.findViewById(R.id.cardView);
        y3 = CourseSelect.findViewById(R.id.cardviewlast);
        b1 = CourseSelect.findViewById(R.id.button6);
        b2 = CourseSelect.findViewById(R.id.button61);
        b3 = CourseSelect.findViewById(R.id.button62);

        List<DataHandler_MyCourse> dataHandler_myCourses;
        List<DataHandler_MyCourse2> dataHandler_myCourses2;
        List<DataHandler_MyCourse3> dataHandler_myCourses3;

        dataHandler_myCourses = new ArrayList<>();
        dataHandler_myCourses2 = new ArrayList<>();
        dataHandler_myCourses3 = new ArrayList<>();

        yearone.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(y1, new AutoTransition());
            if (year1.getVisibility() == View.GONE) {
                year1.setVisibility(View.VISIBLE);
                b1.setVisibility(View.VISIBLE);
            } else {
                year1.setVisibility(View.GONE);
                b1.setVisibility(View.GONE);
            }

            b1.setOnClickListener(v1 -> {
                course.setText("Year One");
                CourseSelect.hide();
            });

        });
        yeartwo.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(y2, new AutoTransition());
            if (year2.getVisibility() == View.GONE) {
                year2.setVisibility(View.VISIBLE);
                b2.setVisibility(View.VISIBLE);
            } else {
                year2.setVisibility(View.GONE);
                b2.setVisibility(View.GONE);
            }
            b2.setOnClickListener(v1 -> {
                course.setText("Year Two");
                CourseSelect.hide();
            });
        });
        yearthree.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(y3, new AutoTransition());
            if (year3.getVisibility() == View.GONE) {
                year3.setVisibility(View.VISIBLE);
                b3.setVisibility(View.VISIBLE);
            } else {
                year3.setVisibility(View.GONE);
                b3.setVisibility(View.GONE);
            }
            b3.setOnClickListener(v1 -> {
                course.setText("Year Three");
                CourseSelect.hide();
            });
        });

        //yearOne
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getMycourseyearone,
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
                                dataHandler_myCourses.add(new DataHandler_MyCourse(
                                        product.getString("coursename")
                                ));
                            }

                            Adapter_MyCourse adapter = new Adapter_MyCourse(getContext(), dataHandler_myCourses, "data");
                            year1.setLayoutManager(new LinearLayoutManager(getContext()));
                            year1.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);

        //year2
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, URLs.URL_getMycourseyeartwo,
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
                                dataHandler_myCourses2.add(new DataHandler_MyCourse2(
                                        product.getString("coursename")
                                ));
                            }

                            Adapter_MyCourse2 adapter = new Adapter_MyCourse2(getContext(), dataHandler_myCourses2);
                            year2.setLayoutManager(new LinearLayoutManager(getContext()));
                            year2.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest2);

        //year 3
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, URLs.URL_getMycourseyearthree,
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
                                dataHandler_myCourses3.add(new DataHandler_MyCourse3(
                                        product.getInt("courseid"),
                                        product.getString("coursename"),
                                        ""
                                ));
                            }

                            Adapter_MyCourse3 adapter = new Adapter_MyCourse3(getContext(), dataHandler_myCourses3);
                            year3.setLayoutManager(new LinearLayoutManager(getContext()));
                            year3.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest3);
        CourseSelect.show();
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
}
