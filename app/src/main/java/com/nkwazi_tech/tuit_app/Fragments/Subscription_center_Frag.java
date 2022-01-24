package com.nkwazi_tech.tuit_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nkwazi_tech.tuit_app.Classes.dialog_class;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.tabs.TabLayout;

public class Subscription_center_Frag extends Fragment {

    public static int int_items = 2;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_video_library, container, false);

        getActivity().setTitle("Subscription Center");
        ViewPager viewPager = root.findViewById(R.id.view_pager);
        TabLayout tabLayout = root.findViewById(R.id.tabs);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));

        dialog_class.dialog("Unsubscribe from a course", "To Unsubscribe from a course, tap the 3 dots on the course you want to unsubscribe and" +
                " select unsubscribe on the pop up menu.", "sub", getContext());
        dialog_class.dialog("Subscribe to a course", "To subscribe to a course, tap “Subscription” at the bottom of the screen" +
                "A list of courses will appear and to subscribe, tap the 3 dots on a course" +
                " and select subscribe.", "sub", getContext());
        dialog_class.dialog("Top up your Balance", "To subscribe to courses, you need to top up your balance. To top up, tap" +
                " the + sign. Enter the Transaction ID you received from your mobile" +
                " money service provider after making a mobile payment. Free courses do" +
                " not require you to top up your balance.", "sub", getContext());

        return root;
    }


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Subscription_available_Frag();
                case 1:
                    return new Subscription_current_Frag();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    String recent_news = "Available Courses";
                    return recent_news;
                case 1:
                    String category = "Subscribed Courses";
                    return category;
            }
            return null;
        }
    }
}
