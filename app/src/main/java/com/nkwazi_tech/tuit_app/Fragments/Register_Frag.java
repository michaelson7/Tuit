package com.nkwazi_tech.tuit_app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Activities.pdf_viewer_activity;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
import com.nkwazi_tech.tuit_app.Classes.MediaHandler;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.ServerResponse;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class Register_Frag extends Fragment {
    private EditText Getemail, Getpassword, Getconfirmpassword, Getphone, Getfullname, getpronum;
    private ProgressBar progressBar;
    private TextView lecturercourse, aa;
    private Button submit, uploadqualifications,terms_btn;
    private FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    GoogleSignInClient googleSignInClient;
    private CheckBox checkBox;
    String Terms;
    private Dialog CourseSelect, terms;
    Uri selectedimg;
    private final int FILE_REQUEST_CODE = 1;
    ArrayList<String> Courses = new ArrayList<String>(),selectedcourse = new ArrayList<String>();
    ImageView background;
    private static final int RC_SIGN_IN = 9001;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
    Spinner schoolIdSelector;
    int selectedSchoolId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_register, container, false);

        schoolIdSelector= root.findViewById(R.id.spinner);
        checkBox = root.findViewById(R.id.checkBox);
        background = root.findViewById(R.id.imageView14);
        Getemail = root.findViewById(R.id.emailtxt);
        Getpassword = root.findViewById(R.id.passwordtxt);
        Getconfirmpassword = root.findViewById(R.id.ConfirmPassword);
        Getphone = root.findViewById(R.id.phonenum);
        Getfullname = root.findViewById(R.id.Nametxt);
        getpronum = root.findViewById(R.id.ProNum);
        lecturercourse = root.findViewById(R.id.course);
        submit = root.findViewById(R.id.subbtn);
        progressBar = root.findViewById(R.id.progressBar);
        uploadqualifications = root.findViewById(R.id.UploadFile);
        firebaseAuth = FirebaseAuth.getInstance();
        CourseSelect = new Dialog(getContext());
        terms_btn = root.findViewById(R.id.button17);
        //content to be hiden
        aa = root.findViewById(R.id.textView75);

        terms = new Dialog(getContext());
        //load courses
        loadSchoolsData();
        LoadCourses();
        LoadTC();
        Defaults();
        return root;
    }

    private void loadSchoolsData() {
        ArrayList<String> schoolList = new ArrayList<String>();
        ArrayList<Integer> schoolId = new ArrayList<Integer>();

        schoolList.add("Select School");
        schoolId.add(0);

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.getSchools,
//                response -> {
//                    try {
//                        //converting the string to json array object
//                        JSONObject obj = new JSONObject(response);
//                        Terms = obj.getString("Terms");
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                },
//                error -> {
//                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
//                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
//                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {
//                        Toast.makeText(getContext(),
//                                "Please check your internet connection",
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//        //adding our stringrequest to queue
//        Volley.newRequestQueue(getContext()).add(stringRequest);
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//    }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.getSchools,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject product = array.getJSONObject(i);
                            schoolId.add(product.getInt("Id"));
                            schoolList.add(product.getString("Title"));

                            Log.d("HAS DATA",product.getString("Title"));
                        }

                        schoolIdSelector.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, schoolList));
                        schoolIdSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedSchoolId = schoolId.get(i);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("HAS NO DATA",e.getLocalizedMessage());
                    }
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void LoadCourses() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getcourse+schoolId,
                response -> {
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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
    }

    private void Defaults() {
        //set image view
        try {
            int mul = 0xFF7F7F7F;
            int add = 0x00000000;
            LightingColorFilter lcf = new LightingColorFilter(mul, add);
            background.setColorFilter(lcf);

            Glide.with(getContext())
                    .asBitmap()
                    .load(R.mipmap.back11)
                    .into(background);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //load image

        Window window = CourseSelect.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Window window2 = terms.getWindow();
        window2.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        submit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E2E2E")));
        submit.setEnabled(false);
        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                submit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00574B")));
                submit.setEnabled(true);
            } else {
                submit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E2E2E")));
                submit.setEnabled(false);
            }
        });

        terms_btn.setOnClickListener(v -> {
            pdf_viewer_activity.pdffilepath = "http://myhost.nkwazitech.com/rules_reg.pdf";
            Intent intent = new Intent(getContext(), pdf_viewer_activity.class);
            startActivity(intent);
        });

        if (Login_Frag.accountype.equals("student")) {
            aa.setVisibility(View.GONE);
            getpronum.setVisibility(View.GONE);
            lecturercourse.setVisibility(View.GONE);
            uploadqualifications.setVisibility(View.GONE);
        }else{

        }

        if (SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
            startActivity(new Intent(getContext(), Home_Activity.class));
        }

        submit.setOnClickListener(v -> {
                    final String email = Getemail.getText().toString().trim();
                    final String name = Getfullname.getText().toString().trim();
                    final String password = Getpassword.getText().toString().trim();
                    final String cpassword = Getconfirmpassword.getText().toString().trim();
                    final String phone = Getphone.getText().toString().trim();
                    final String practisingnum = getpronum.getText().toString().trim();
                    final String LecturerCources = lecturercourse.getText().toString().trim();


                    if (TextUtils.isEmpty(name)) {
                        Getfullname.setError("Enter Full Name");
                        Getfullname.requestFocus();
                        return;
                    }
                    if (name.length() > 25) {
                        Getfullname.setError("character limit exceeded, please enter less than 25 characters");
                        Getfullname.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(email)) {
                        Getemail.setError("Please enter your email");
                        Getemail.requestFocus();
                        return;
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Getemail.setError("Enter a valid email");
                        Getemail.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(phone)) {
                        Getphone.setError("Enter Phone Number");
                        Getphone.requestFocus();
                        return;
                    }
                    if (phone.length() > 14) {
                        Getphone.setError("character limit exceeded, please enter less than 14 characters");
                        Getphone.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Getpassword.setError("Enter a password");
                        Getpassword.requestFocus();
                        return;
                    }

                    if (password.length() < 6) {
                        Getpassword.setError("Password must have more than 6 characters");
                        Getpassword.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(cpassword)) {
                        Getconfirmpassword.setError("Enter a password");
                        Getconfirmpassword.requestFocus();
                        return;
                    }

                    if (!password.equals(cpassword)) {
                        Getpassword.setError("Passwords do not match");
                        Getconfirmpassword.setError("Passwords do not match");
                        Getpassword.requestFocus();
                        return;
                    }

            if (selectedSchoolId ==0) {
                schoolIdSelector.requestFocus();
                Toast.makeText(getContext(), "Select School", Toast.LENGTH_LONG).show();
                return;
            }

                    if (!Login_Frag.accountype.equals("student")) {
                        if (TextUtils.isEmpty(practisingnum)) {
                            getpronum.setError("Enter practising number");
                            getpronum.requestFocus();
                            return;
                        }

                        if (TextUtils.isEmpty(LecturerCources)) {
                            lecturercourse.setError("Please enter Lecturer Courses");
                            lecturercourse.requestFocus();
                            return;
                        }

                        if (selectedimg == null) {
                            Toast.makeText(getContext(), "Please upload your qualification", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Register(email, name, password, phone, practisingnum, LecturerCources);
                }
        );

        //student lecturer course
        lecturercourse.setOnClickListener(v -> {
            // Set up the alert builder
            lecturercourse.setText("");
            selectedcourse.clear();
            final int[] count = {0};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
            builder.setTitle("Choose 3 courses");

            String[] Option = Courses.toArray(new String[0]);
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
                        lecturercourse.setText(builder1.toString());
                    }
                }
            });
            builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        uploadqualifications.setOnClickListener(v -> {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
        });

        //Google Sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
        //gmail

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FILE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                DialogConfig dialogConfig = new DialogConfig.Builder()
                        .enableMultipleSelect(false) // default is false
                        .enableFolderSelect(false) // default is false
                        // .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android") // default is sdcard
                        .supportFiles(new SupportFile(".pdf", R.drawable.ic_bookshelf2)) // default is showing all file types.
                        .build();

                new FilePickerDialogFragment.Builder()
                        .configs(dialogConfig)
                        .onFilesSelected(list -> {
                            for (File file : list) {
                                uploadqualifications.setText(file.getAbsolutePath());
                                selectedimg = Uri.fromFile(file);
                            }
                        })
                        .build()
                        .show(getActivity().getSupportFragmentManager(), null);

            } else {
                Toast.makeText(getActivity(), "You do not have permission to access file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri1 = data.getData();
            selectedimg = uri1;
            String path = String.valueOf(uri1);
            String path_lastPart = path.substring(path.indexOf("/storage"));
            path_lastPart = path_lastPart.replace("%20", " ");
            uploadqualifications.setText(path_lastPart);
        }

        //gmail
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FirebaseGoogleAuth(account);
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Google sign in failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                UpdateUI(user);
            } else {
                Toast.makeText(getContext(), "Failed to sign in", Toast.LENGTH_LONG).show();
                UpdateUI(null);
            }
        });
    }

    private void UpdateUI(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri photo = account.getPhotoUrl();
            Register(email, name, "", "", "", "");
        }
    }

    private void Register(String email, String name, String password, String phone, String practisingnum, String LecturerCources) {

        String[] Option = selectedcourse.toArray(new String[0]);

        @SuppressLint("StaticFieldLeak")
        class RegisterUser extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                if (Login_Frag.accountype.equals("student")) {
                    params.put("accounttype", "student");
                    params.put("email", email);
                    params.put("schoolId", String.valueOf(selectedSchoolId));
                    params.put("password", password);
                    params.put("phonenumber", phone);
                    params.put("name", name);
                    params.put("propic", "null");
                    params.put("coverpic", "null");
                    params.put("practisenumber", "null");
                    params.put("lecturercourse", "null");
                    params.put("lecturermodule", "null");

                } else {
                    params.put("accounttype", "lecturer");
                    params.put("email", email);
                    params.put("schoolId", String.valueOf(selectedSchoolId));
                    params.put("password", password);
                    params.put("phonenumber", phone);
                    params.put("name", name);
                    params.put("propic", "null");
                    params.put("coverpic", "null");
                    params.put("studentcourse", "");
                    params.put("learningmod", "");
                    params.put("schoolname", "");
                    params.put("practisenumber", practisingnum);

                    try {
                        params.put("lecturercourse1", Option[0]);
                        params.put("lecturercourse2", Option[1]);
                        params.put("lecturercourse3", Option[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                submit.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //upload qualifications
                        JSONObject userJson = obj.getJSONObject("user");
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

                        if (Login_Frag.accountype.equals("lecturer")) {
                            upload(selectedimg, userJson.getInt("id"));
                        }

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
                                                    progressBar.setVisibility(View.GONE);
                                                    submit.setVisibility(View.VISIBLE);
                                                    try {
                                                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    SharedPrefManager.getInstance(getContext()).userLogin(dataHandlerUser);
                                                    startActivity(new Intent(getContext(), Home_Activity.class));
                                                    getActivity().finish();
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    submit.setVisibility(View.VISIBLE);
                                                    Toast.makeText(getContext(), "Error while registering", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            String localizedMessage = task.getException().getLocalizedMessage();
                                            Toast.makeText(getContext(), localizedMessage + " " + email, Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                            submit.setVisibility(View.VISIBLE);
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();

                            Toast.makeText(getContext(), "Account Created", Toast.LENGTH_SHORT).show();
                            SharedPrefManager.getInstance(getContext()).userLogin(dataHandlerUser);
                            startActivity(new Intent(getContext(), Home_Activity.class));
                            getActivity().finish();
                        }


                    } else {
                        progressBar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    @SuppressLint("SetTextI18n")
    private void TermsandConditionsDialog() {
        TextView termsText;
        terms.setContentView(R.layout.dialog_termsandconditions);
        Objects.requireNonNull(terms.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = terms.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        termsText = terms.findViewById(R.id.textView13);
        termsText.setText(Terms);

        terms.show();
    }



    private void upload(Uri selectedimg, int username) {
        String path = String.valueOf(selectedimg);
        String path_lastPart = path.substring(path.indexOf("/storage"));
        path_lastPart = path_lastPart.replace("%20", " ");
        File file = new File(path_lastPart);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(username));

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filerequest = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaHandler.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        MediaHandler api = retrofit.create(MediaHandler.class);
        Call<ServerResponse> call = api.uploadqalification(id, filerequest);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error while uploading account", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void LoadTC() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getTP,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        Terms = obj.getString("Terms");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof NetworkError || error instanceof ServerError || error instanceof AuthFailureError ||
                            error instanceof ParseError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                        Toast.makeText(getContext(),
                                "Please check your internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}


