package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Activities.GroupChat_activity;
import com.nkwazi_tech.tuit_app.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Group_all_list_mygroup extends RecyclerView.Adapter<Adapter_Group_all_list_mygroup.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    public static String state;

    public Adapter_Group_all_list_mygroup(Context mCtx, List<DataHandler_Group_all_list> dataHandler_group_all_lists, String state) {
        this.mCtx = mCtx;
        Adapter_Group_all_list_mygroup.state = state;
        this.dataHandler_group_all_lists = dataHandler_group_all_lists;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);

        if (state.equals("my_group")) {
            views = mInflater.inflate(R.layout.adapter_explorelecturers, parent, false);
        } else {
            views = mInflater.inflate(R.layout.adapter_group_all_list, parent, false);
        }
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image

        if (dataHandler_group_all_lists.get(position).getNewGroupName() != "null") {
            holder.groupname.setText(dataHandler_group_all_lists.get(position).getNewGroupName());
        } else {
            holder.groupname.setText(dataHandler_group_all_lists.get(position).getGroupName());
        }

        if (state.equals("my_group")) {
            Glide.with(mCtx)
                    .load(dataHandler_group_all_lists.get(position).getGroupImg())
                    .into(holder.groupimg_2);

            holder.recyclerView.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, GroupChat_activity.class);
                if (!dataHandler_group_all_lists.get(position).getNewGroupName().equals("null")) {
                    intent.putExtra("GroupName", dataHandler_group_all_lists.get(position).getGroupName());
                    intent.putExtra("new",  dataHandler_group_all_lists.get(position).getNewGroupName());
                } else {
                    intent.putExtra("GroupName", dataHandler_group_all_lists.get(position).getGroupName());
                    intent.putExtra("new",  dataHandler_group_all_lists.get(position).getGroupName());
                }
                intent.putExtra("GroupImg", dataHandler_group_all_lists.get(position).getGroupImg());
                intent.putExtra("Admin", dataHandler_group_all_lists.get(position).getAdmin());
                intent.putExtra("Description", dataHandler_group_all_lists.get(position).getGroupDiscription());
                mCtx.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataHandler_group_all_lists.size();
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

        TextView groupname;
        CircleImageView groupimg_2;
        ConstraintLayout recyclerView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            if (state.equals("my_group")) {
                groupname = itemView.findViewById(R.id.lecturername);
                groupimg_2 = itemView.findViewById(R.id.lecturerimg);
                recyclerView= itemView.findViewById(R.id.layout);
            } else {
                groupname = itemView.findViewById(R.id.textView12);
            }

        }
    }

}
