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

import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.tabs.TabLayout;

public class Book_Library_Frag extends Fragment {

    public static int int_items = 2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_video_library, container, false);

        getActivity().setTitle("Book Library");
        ViewPager viewPager = root.findViewById(R.id.view_pager);
        TabLayout tabLayout = root.findViewById(R.id.tabs);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));

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
                    return new LibraryCourse_Frag();
                case 1:
                    Downloaded_Videos_Frag.Type = "book";
                    return new Downloaded_Videos_Frag();
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
                    String recent_news = "Online";
                    return recent_news;
                case 1:
                    String category = "Downloaded";
                    return category;
            }
            return null;
        }
    }
}
