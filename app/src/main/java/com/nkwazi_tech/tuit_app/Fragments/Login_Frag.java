package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_User;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Login_Frag extends Fragment {

    private EditText Getusername;
    private EditText Getpassword;
    private Button submit, ForgotPassword, submit2;
    private ProgressBar progressBar;
    static String accountype;
    private FirebaseAuth firebaseAuth;
    private Dialog passwordreset;
    TextView response, link, googlelink;
    ImageView background;
    GoogleSignInClient googleSignInClient;
    ProgressDialog p;
    private static final int RC_SIGN_IN = 9001;
    ArrayList arrayList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_login, container, false);

        link = root.findViewById(R.id.signUpLink);
        Getusername = root.findViewById(R.id.usertxb);
        Getpassword = root.findViewById(R.id.passtxb);
        submit = root.findViewById(R.id.btnsub);
        progressBar = root.findViewById(R.id.progressBar3);
        ForgotPassword = root.findViewById(R.id.ForgottenPassword);
        background = root.findViewById(R.id.imageView12);
        googlelink = root.findViewById(R.id.textView10);

        firebaseAuth = FirebaseAuth.getInstance();
        passwordreset = new Dialog(getContext());

        Defaults();
        return root;
    }

    private void Defaults() {
        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
            getActivity().finish();
            startActivity(new Intent(getContext(), Home_Activity.class));
        }

        //set image view
        try{
            int mul = 0xFF7F7F7F;
            int add = 0x00000000;
            LightingColorFilter lcf = new LightingColorFilter(mul, add);
            background.setColorFilter(lcf);
        }catch (Exception e){

        }


        //load image
        Glide.with(getContext())
                .asBitmap()
                .load(R.mipmap.back11)
                .into(background);

        //dialog when signup button is pressed
        link.setOnClickListener(v -> {
            String[] Option = {"Student", "Lecturer"};
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(),R.style.AlertDialog);
            builder.setTitle("Select account type");
            builder.setItems(Option, (dialog, which) -> {
                if (which == 0) {
                    accountype = "student";
                    assert getFragmentManager() != null;
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.MainFrame, new Register_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                } else if (which == 1) {
                    accountype = "lecturer";
                    assert getFragmentManager() != null;
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.MainFrame, new Register_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                }
            });
            builder.show();
        });

        ForgotPassword.setOnClickListener(v -> {
            ShowResetDialog();
        });

        submit.setOnClickListener(v -> {
            final String username = Getusername.getText().toString().trim();
            final String password = Getpassword.getText().toString().trim();

            //validating inputs
            if (TextUtils.isEmpty(username)) {
                Getusername.setError("Please enter your username");
                Getusername.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Getusername.setError("Please enter your password");
                Getpassword.requestFocus();
                return;
            }
            login(username, password, "false");
        });

        //google sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();

        googlelink.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
            p = new ProgressDialog(getContext());
            p.setMessage("Signing in...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        });
    }

    private void ShowResetDialog() {
        EditText code;
        Button submit;

        passwordreset.setContentView(R.layout.dialog_recovery);
        Objects.requireNonNull(passwordreset.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = passwordreset.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        code = passwordreset.findViewById(R.id.practicenumber);
        response = passwordreset.findViewById(R.id.response);
        submit2 = passwordreset.findViewById(R.id.button2);

        submit2.setOnClickListener(v -> {
            final String emails = code.getText().toString();
            if (TextUtils.isEmpty(emails)) {
                code.setError("Please enter your email");
                code.requestFocus();
                return;
            }
            response.setVisibility(View.GONE);

            String email = emails;
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("verifying..");
            progressDialog.show();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Reset password instructions has been sent to your email",
                                        Toast.LENGTH_LONG).show();
                                passwordreset.dismiss();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),
                                        "Email don't exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        });

        passwordreset.show();
    }

    private void login(String username, String password, String authType) {
        //if everything is fine
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("authType", authType);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
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

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        //getting the dataHandlerUser from the response
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

                        if (!userJson.getString("accounttype").equals("student")) {
                            JSONArray array = obj.getJSONArray("Products");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                if (i == 0) {
                                    SharedPrefManager.getInstance(getContext()).setCourseid(product.getInt("courseid"));
                                    SharedPrefManager.getInstance(getContext()).setCoursename1(product.getString("coursename"));
                                } else if (i == 1) {
                                    SharedPrefManager.getInstance(getContext()).setCourseid2(product.getInt("courseid"));
                                    SharedPrefManager.getInstance(getContext()).setCoursename2(product.getString("coursename"));
                                } else if (i == 2) {
                                    SharedPrefManager.getInstance(getContext()).setCourseid3(product.getInt("courseid"));
                                    SharedPrefManager.getInstance(getContext()).setCoursename3(product.getString("coursename"));
                                }
                            }
                        }

                        if (authType.equals("true")) {
                            AuthCredential credential = GoogleAuthProvider.getCredential(password, null);
                            firebaseAuth.signInWithCredential(credential)
                                    .addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                submit.setVisibility(View.VISIBLE);
                                                try {
                                                    Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getContext(),
                                                            "Please check your internet connection",
                                                            Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    p.dismiss();
                                                    submit.setVisibility(View.VISIBLE);
                                                }
                                                SharedPrefManager.getInstance(getContext()).userLogin(dataHandlerUser);
                                                Objects.requireNonNull(getActivity()).finish();
                                                startActivity(new Intent(getContext(), Home_Activity.class));
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                submit.setVisibility(View.VISIBLE);
                                                Toast.makeText(getContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            firebaseAuth.signInWithEmailAndPassword(username, password)
                                    .addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                submit.setVisibility(View.VISIBLE);
                                                try {
                                                    Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getContext(),
                                                            "Please check your internet connection",
                                                            Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    p.dismiss();
                                                    submit.setVisibility(View.VISIBLE);
                                                }
                                                SharedPrefManager.getInstance(getContext()).userLogin(dataHandlerUser);
                                                Objects.requireNonNull(getActivity()).finish();
                                                startActivity(new Intent(getContext(), Home_Activity.class));
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                submit.setVisibility(View.VISIBLE);
                                                Toast.makeText(getContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                        //storing the dataHandlerUser in shared preferences
                    } else {
                        progressBar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(),
                                "Please check your username and password",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Login Error", Objects.requireNonNull(e.getLocalizedMessage()));
                    Toast.makeText(getContext(),
                            "Please check your username and password",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FirebaseGoogleAuth(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                p.dismiss();
            }
        }
    }

    private void FirebaseGoogleAuth(String googleSignInAccount) {
        UpdateUI(googleSignInAccount);
    }

    private void UpdateUI(String user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri photo = account.getPhotoUrl();
            login(email, user, "true");
        }
    }


}
