package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Classes.Adapter_MyCourse;
import com.nkwazi_tech.tuit_app.Classes.DataHandler_MyCourse;
import com.nkwazi_tech.tuit_app.R;

import java.util.ArrayList;
import java.util.List;

public class MyCourses_Frag extends Fragment {

    RecyclerView recyclerView;
    private List<DataHandler_MyCourse> dataHandler_myCourses;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_home, container, false);

        dataHandler_myCourses = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recycler2);

        LoadCourses();
        return root;
    }

    private void LoadCourses() {
        dataHandler_myCourses.add(new DataHandler_MyCourse(
                "Nursing"
        ));
        dataHandler_myCourses.add(new DataHandler_MyCourse(
                "Clinical\nMedicine\n(coming soon)"
        ));
        dataHandler_myCourses.add(new DataHandler_MyCourse(
                "Psychosocial Counselling\n(coming soon)"
        ));

        //creating adapter object and setting it to recyclerview
        Adapter_MyCourse adapter = new Adapter_MyCourse(getContext(), dataHandler_myCourses, "true");
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
    }


}
