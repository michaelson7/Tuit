package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.R;

import java.util.List;

public class Adapter_MyCourse3 extends RecyclerView.Adapter<Adapter_MyCourse3.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_MyCourse3> dataHandler_myCourses3;

    public Adapter_MyCourse3(Context mCtx, List<DataHandler_MyCourse3> dataHandler_myCourses3) {
        this.mCtx = mCtx;
        this.dataHandler_myCourses3 = dataHandler_myCourses3;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_mycourses, parent, false);
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        holder.course.setText(dataHandler_myCourses3.get(position).getCoursename());
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
        TextView course;

        public ProductViewHolder(View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.cources);
        }
    }
}
