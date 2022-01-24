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

import com.nkwazi_tech.tuit_app.Activities.Home_Activity;
import com.nkwazi_tech.tuit_app.Classes.dialog_class;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.material.tabs.TabLayout;

public class Video_Library_Frag extends Fragment {

    public static int int_items = 2;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.frag_video_library, container, false);

        getActivity().setTitle("Video Library");

        ViewPager viewPager = root.findViewById(R.id.view_pager);
        TabLayout tabLayout = root.findViewById(R.id.tabs);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));

        dialog_class.dialog("Delete Video", "To free up space, you can delete a downloaded video. To delete a video, " +
                "tap video library at the bottom of the screen, then tap the downloaded " +
                "tab. A list of videos will show, then tap the 3 dots on the video you want " +
                "to delete. Tap delete on the popup menu.", "video", getContext());
        dialog_class.dialog("Download Videos", "You can watch videos even if you are not connected to the internet. To " +
                "save a video offline, go to the video library and tap the online tab. Select " +
                "the course, then a list of videos will show. On the video you want to save, " +
                "tap the 3 dots. Tap download on the popup menu. ", "video", getContext());


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
                    return new Explorer_Frag();
                case 1:
                    Downloaded_Videos_Frag.Type = "false";
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
                    return "Uploaded Videos";
                case 1:
                    return "Downloaded Videos";
            }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Home_Activity.search_icon.setVisible(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Home_Activity.search_icon.setVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Home_Activity.search_icon.setVisible(false);
    }
}
