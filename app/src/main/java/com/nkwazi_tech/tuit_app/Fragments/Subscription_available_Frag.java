package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Course;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Explorer;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Subscription_available_Frag extends Fragment {
    RecyclerView recyclerView;
    TextView user_balance ;
    FloatingActionButton floatingActionButton;
    ProgressBar progressBar, progressBar2;
    Dialog topup_Dialog;
    String balance;
    Button submit;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();
    List<DataHandler_Explorer> data_handlerExplorers = new ArrayList<>();
    int id = SharedPrefManager.getInstance(getContext()).getID();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_subscription, container, false);

        getActivity().setTitle("Subscriptions");
        data_handlerExplorers.clear();
        recyclerView = root.findViewById(R.id.recyclerView2);
        progressBar = root.findViewById(R.id.progressBar5);
        floatingActionButton = root.findViewById(R.id.textView48);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topup_Dialog = new Dialog(getContext());

        Load_Sub();
        Defaults();

        return root;
    }

    private void Defaults() {
        floatingActionButton.setOnClickListener(v -> {
            Open_Top_Dialog();
        });
    }

    private void Open_Top_Dialog() {
        topup_Dialog.setContentView(R.layout.dialog_topup);
        Objects.requireNonNull(topup_Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = topup_Dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText transaction;
        TextView title_txt;

        submit = topup_Dialog.findViewById(R.id.button2);
        transaction = topup_Dialog.findViewById(R.id.usertxb);
        progressBar2 = topup_Dialog.findViewById(R.id.progressBar12);
        user_balance = topup_Dialog.findViewById(R.id.title4);
        title_txt = topup_Dialog.findViewById(R.id.title5);

        user_balance.setText("Available Balance: K" + balance);
        title_txt.setText("Top Up");

        submit.setOnClickListener(v -> {
            final String get_tran = transaction.getText().toString().trim();

            if (TextUtils.isEmpty(get_tran)) {
                transaction.setError("Please enter your TransactionID");
                transaction.requestFocus();
                return;
            }

            String userID = String.valueOf(SharedPrefManager.getInstance(getContext()).getID());
            String name = String.valueOf(SharedPrefManager.getInstance(getContext()).getName());
            String email = String.valueOf(SharedPrefManager.getInstance(getContext()).getEmail());

            progressBar2.setVisibility(View.VISIBLE);
            submit.setVisibility(View.INVISIBLE);

            Topup(userID, name, email, get_tran);
        });

        topup_Dialog.show();
    }

    private void Topup(String userID, String name, String email, String trans_ID) {
        String state = "false";
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("userID", String.valueOf(userID));
                params.put("name", String.valueOf(name));
                params.put("trans_ID", String.valueOf(trans_ID));
                params.put("email", String.valueOf(email));
                params.put("nrc_num", String.valueOf(userID));
                params.put("state", state);
                params.put("institution", "null");

                //returing the response
                return requestHandler.sendPostRequest(URLs.balance_Topup, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        topup_Dialog.dismiss();
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();

                        data_handlerExplorers.clear();
                        Load_Sub();
                    } else {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                    progressBar2.setVisibility(View.INVISIBLE);
                    submit.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    private void Load_Sub() {
        progressBar.setVisibility(View.VISIBLE);
        String data = "&state=load_available_courses&userid=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveCources + data+schoolId,
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
                            data_handlerExplorers.add(new DataHandler_Explorer(
                                    product.getInt("courseid"),
                                    product.getInt("subscription_price"),
                                    product.getString("coursename"),
                                    product.getString("courseimg")
                            ));
                        }

                        //getting account balance
                        balance = obj.getString("balance");
                        if (balance.equals("null")) {
                            balance = "0";
                        }

                        Adapter_Course.course_price = balance;
                        Adapter_Course adapter = new Adapter_Course(getContext(), data_handlerExplorers, "fetch_Available");
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                                new ViewTreeObserver.OnPreDrawListener() {

                                    @Override
                                    public boolean onPreDraw() {
                                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                                            View v = recyclerView.getChildAt(i);
                                            v.setAlpha(0.0f);
                                            v.animate().alpha(1.0f)
                                                    .setDuration(300)
                                                    .setStartDelay(i * 50)
                                                    .start();
                                        }
                                        return true;
                                    }
                                });
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}
