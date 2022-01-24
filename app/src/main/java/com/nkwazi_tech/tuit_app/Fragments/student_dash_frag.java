package com.nkwazi_tech.tuit_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Activities.pdf_viewer_activity;
import com.nkwazi_tech.tuit_app.Activities.recyclable_activity;
import com.nkwazi_tech.tuit_app.Classes.Adapter_Course;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_Explorer;
import com.nkwazi_tech.tuit_app.Classes.SharedPrefManager;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import in.gauriinfotech.commons.Progress;

public class student_dash_frag extends Fragment {

    CardView profile, account, subscription;
    TextView name, balance, courses_num;
    String user_balance;
    int course_num = 0;
    String  schoolId = "&schoolId=" + SharedPrefManager.getInstance(getContext()).getSchoolId();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_student_dash, container, false);

        profile = root.findViewById(R.id.cardView2);
        account = root.findViewById(R.id.cardView10);
        subscription = root.findViewById(R.id.sub);
        name = root.findViewById(R.id.user_name);
        balance = root.findViewById(R.id.user_name2);
        courses_num = root.findViewById(R.id.user_name3);
        getActivity().setTitle("My Account");

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }
            return false;
        });

        load_balance();
        return root;
    }

    private void load_balance() {
        int id = SharedPrefManager.getInstance(getContext()).getID();
        String data = "&state=load_user_subs&userid=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_RetriveCources + data+schoolId,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("Products");
                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {
                            course_num = array.length();
                        }

                        //getting account balance
                        user_balance = obj.getString("balance");
                        if (user_balance.equals("null")) {
                            user_balance = "0";
                        }
                        defaults();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void defaults() {
        name.setText(SharedPrefManager.getInstance(getContext()).getName());
        balance.setText("K " + user_balance);
        courses_num.setText(String.valueOf(course_num));

        profile.setOnClickListener(v -> {
            recyclable_activity.cmd = "profile";
            Intent intent = new Intent(getContext(), recyclable_activity.class);
            startActivity(intent);
        });

        account.setOnClickListener(v -> {
            FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager();
            FragmentTransaction fr = manager.beginTransaction();
            fr.replace(R.id.flContent, new Subscription_center_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });

        subscription.setOnClickListener(v -> {
            FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager();
            FragmentTransaction fr = manager.beginTransaction();
            fr.replace(R.id.flContent, new Subscription_center_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });
    }
}
