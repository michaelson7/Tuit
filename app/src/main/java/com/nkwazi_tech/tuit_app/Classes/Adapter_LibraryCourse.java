package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.Fragments.LibraryPdf_Frag;
import com.nkwazi_tech.tuit_app.R;

import java.util.List;
import java.util.Objects;

public class Adapter_LibraryCourse extends RecyclerView.Adapter<Adapter_LibraryCourse.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_MyCourse> dataHandler_myCourses;

    public Adapter_LibraryCourse(Context mCtx, List<DataHandler_MyCourse> dataHandler_myCourses) {
        this.mCtx = mCtx;
        this.dataHandler_myCourses = dataHandler_myCourses;
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
        holder.course.setText(dataHandler_myCourses.get(position).getCourse());
        holder.course.setOnClickListener(v -> {
            LibraryPdf_Frag.LoadCourse = dataHandler_myCourses.get(position).getCourse();
            FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(mCtx)).getSupportFragmentManager();
            FragmentTransaction fr = manager.beginTransaction();
            fr.replace(R.id.flContent, new LibraryPdf_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });
    }

    @Override
    public int getItemCount() {
        return dataHandler_myCourses.size();
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
