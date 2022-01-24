package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nkwazi_tech.tuit_app.R;

public class NursingResearch_Frag extends Fragment {
    CardView gresearchprocess, biostats, gresearchoutline, advisorty, viewuploads;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_nursingresearch, container, false);
        getActivity().setTitle("Nursing Research");

        gresearchprocess = root.findViewById(R.id.card1);
        biostats = root.findViewById(R.id.cardView3);
        gresearchoutline = root.findViewById(R.id.newscrd);
        advisorty = root.findViewById(R.id.viewcard);
        viewuploads= root.findViewById(R.id.uploadcard);

        gresearchprocess.setOnClickListener(v -> {
            NursingResearchVideo_Frag.research = "research";
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.flContent, new NursingResearchVideo_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });
        biostats.setOnClickListener(v -> {
            NursingResearchVideo_Frag.research = "bio";
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.flContent, new NursingResearchVideo_Frag());
            fr.addToBackStack(null);
            fr.commit();
        }); gresearchoutline.setOnClickListener(v -> {
           // LibraryPdf_Frag.loadresearch = "research";
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.flContent, new GeneralResearchView_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });advisorty.setOnClickListener(v -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.flContent, new NursingResearch_UploadFrag());
            fr.addToBackStack(null);
            fr.commit();
        });
        viewuploads.setOnClickListener(v -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.flContent, new NursingResarchUploads_Frag());

            fr.addToBackStack(null);
            fr.commit();
        });
        return root;
    }
    //token: dtOCAbTNmYI:APA91bH8O_5uqyfOxvdybvluuO_DBFRGhyRlsce6rRo3TaSrvvsQrHus32mnqn6qTr4WS1RWlJ8Xauzu7APoa3XFRIGZ0hPNkXEVLEY-ZL-GWr9ADDaNkxvnowM09fVRC5z5Gi4giaWr
}
