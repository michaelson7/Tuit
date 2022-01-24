package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Activities.GroupChat_activity;
import com.nkwazi_tech.tuit_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adapter_MyGroup extends RecyclerView.Adapter<Adapter_MyGroup.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    DatabaseReference GroupNameRef;

    public Adapter_MyGroup(Context mCtx, List<DataHandler_Group_all_list> dataHandler_group_all_lists) {
        this.mCtx = mCtx;
        this.dataHandler_group_all_lists = dataHandler_group_all_lists;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_my_groups, parent, false);
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        GroupNameRef = FirebaseDatabase.getInstance().getReference("Groups").
                child((dataHandler_group_all_lists.get(position).getGroupName()));
        GroupNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataHandler_Chat chat = snapshot.getValue(DataHandler_Chat.class);

                    String text = chat.getMessage();
                    if (text.contains("//myhost.nkwazitech")){
                        text = "Image";
                    }

                    holder.GroupMessage.setText(chat.getSender() + " : " +text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Glide.with(mCtx)
                .load(dataHandler_group_all_lists.get(position).getGroupImg())
                .into(holder.groupimg);

        if (dataHandler_group_all_lists.get(position).getNewGroupName() != "null") {
            holder.groupname.setText(dataHandler_group_all_lists.get(position).getNewGroupName());
        } else {
            holder.groupname.setText(dataHandler_group_all_lists.get(position).getGroupName());
        }

        holder.cardgroup.setOnClickListener(v -> {
            Intent intent = new Intent(mCtx, GroupChat_activity.class);
            if (!dataHandler_group_all_lists.get(position).getNewGroupName().equals("null")) {
                //intent.putExtra("GroupName", dataHandler_group_all_lists.get(position).getNewGroupName());
                //intent.putExtra("new",  dataHandler_group_all_lists.get(position).getGroupName());

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

        TextView groupname, GroupMessage;
        ImageView groupimg;
        ConstraintLayout cardgroup;

        public ProductViewHolder(View itemView) {
            super(itemView);

            groupname = itemView.findViewById(R.id.textView12);
            GroupMessage = itemView.findViewById(R.id.GroupMessage);
            groupimg = itemView.findViewById(R.id.groupimg);
            cardgroup = itemView.findViewById(R.id.layout);

        }
    }
}
