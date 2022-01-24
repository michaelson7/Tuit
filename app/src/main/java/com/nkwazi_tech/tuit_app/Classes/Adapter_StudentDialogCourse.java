package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Activities.explorer_course_activity;
import com.nkwazi_tech.tuit_app.Fragments.ExplorerCourse_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.util.List;

public class Adapter_StudentDialogCourse extends RecyclerView.Adapter<Adapter_StudentDialogCourse.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_MyCourse3> dataHandler_myCourses3;

    public Adapter_StudentDialogCourse(Context mCtx, List<DataHandler_MyCourse3> dataHandler_myCourses3) {
        this.mCtx = mCtx;
        this.dataHandler_myCourses3 = dataHandler_myCourses3;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_librarycourses, parent, false);
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        holder.course.setText(dataHandler_myCourses3.get(position).getCoursename());
        holder.course.setOnClickListener(v -> {
//            try{
//                Adapter_Dash_Courselist.Cources.hide();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ExplorerCourse_Frag.coursename = dataHandler_myCourses3.get(position).getCoursename();
//            ExplorerCourse_Frag.courseida = String.valueOf(dataHandler_myCourses3.get(position).getId());
//            FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
//            FragmentTransaction fr = manager.beginTransaction();
//            fr.replace(R.id.flContent, new ExplorerCourse_Frag());
//            fr.addToBackStack(null);
//            fr.commit();

            explorer_course_activity.coursename = dataHandler_myCourses3.get(position).getCoursename();
            explorer_course_activity.courseida = String.valueOf(dataHandler_myCourses3.get(position).getId());
            Intent intent = new Intent(mCtx, explorer_course_activity.class);
            mCtx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataHandler_myCourses3.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        Button course;

        public ProductViewHolder(View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.button11);
        }
    }
}
