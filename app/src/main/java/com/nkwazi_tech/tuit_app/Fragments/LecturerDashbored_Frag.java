package com.nkwazi_tech.tuit_app.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nkwazi_tech.tuit_app.Activities.VideoEdit_Activity;
import com.nkwazi_tech.tuit_app.Activities.VideoStats;
import com.nkwazi_tech.tuit_app.Classes.RequestHandler;
import com.nkwazi_tech.tuit_app.Classes.URLs;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LecturerDashbored_Frag extends Fragment {
    CardView upload, viewing, newscrd, statscard;
    Button uploadbtn, newsbtn,viewstats;
    String[] options;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_lecturerdash, container, false);
        getActivity().setTitle("Dashboard");

        upload = root.findViewById(R.id.uploadcard);
        uploadbtn = root.findViewById(R.id.btnupload);
        viewing = root.findViewById(R.id.viewcard);
        newsbtn = root.findViewById(R.id.newsbtn);
        newscrd = root.findViewById(R.id.newscrd);
        statscard = root.findViewById(R.id.card1);
        viewstats= root.findViewById(R.id.viewstats);

        viewing.setOnClickListener(v -> {
            options = new String[]{"View Library Uploads","View Video Uploads","View Research Submissions"};
            String[] SortyBy = options;
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(),R.style.AlertDialog);
            builder.setTitle("Select Option");
            builder.setItems(SortyBy, (dialog, which) -> {
                if (which == 0) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new Library_Files_Edit_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                } else if (which == 1) {
                    startActivity(new Intent(getContext(), VideoEdit_Activity.class));
                } else if (which == 2) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new NursingResearchView_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                }
            });
            builder.show();
        });
        upload.setOnClickListener(v -> {
            options = new String[]{"Upload Video","Upload Nursing Care Plans","Upload Library Files"};
            String[] SortyBy = options;
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(),R.style.AlertDialog);
            builder.setTitle("Select Option");
            builder.setItems(SortyBy, (dialog, which) -> {
                if (which == 0) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new VideoUploads_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                } else if (which == 1) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new AdminCarePlansUpload_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                } else if (which == 2) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new Library_Upload_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                }
            });
            builder.show();
        });
        uploadbtn.setOnClickListener(v -> {
            options = new String[]{"Upload Video","Upload Nursing Care Plans","Upload Library Files"};
            String[] SortyBy = options;
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(),R.style.AlertDialog);
            builder.setTitle("Select Option");
            builder.setItems(SortyBy, (dialog, which) -> {
                if (which == 0) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new VideoUploads_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                } else if (which == 1) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new AdminCarePlansUpload_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                } else if (which == 2) {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fr.replace(R.id.flContent, new Library_Upload_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                }
            });
            builder.show();
        });
        newsbtn.setOnClickListener(v -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fr.replace(R.id.flContent, new NewsUpload_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });
        newscrd.setOnClickListener(v -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fr.replace(R.id.flContent, new NewsUpload_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });
        statscard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), VideoStats.class));
        });
        viewstats.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), VideoStats.class));
        });

        return root;
    }

    //token: dtOCAbTNmYI:APA91bH8O_5uqyfOxvdybvluuO_DBFRGhyRlsce6rRo3TaSrvvsQrHus32mnqn6qTr4WS1RWlJ8Xauzu7APoa3XFRIGZ0hPNkXEVLEY-ZL-GWr9ADDaNkxvnowM09fVRC5z5Gi4giaWr
}
