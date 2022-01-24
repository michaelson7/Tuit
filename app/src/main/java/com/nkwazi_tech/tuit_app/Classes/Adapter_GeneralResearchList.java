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

import com.nkwazi_tech.tuit_app.Fragments.GeneralResearchContent_Frag;
import com.nkwazi_tech.tuit_app.Fragments.NursingCarePlans_sub_Frag;
import com.nkwazi_tech.tuit_app.Fragments.give_new_name;
import com.nkwazi_tech.tuit_app.R;

import java.util.List;
import java.util.Objects;

public class Adapter_GeneralResearchList extends RecyclerView.Adapter<Adapter_GeneralResearchList.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_GeneralResearch> dataHandler_generalResearches;
    String data,header;

    public Adapter_GeneralResearchList(Context mCtx, List<DataHandler_GeneralResearch> dataHandler_generalResearches, String data, String header) {
        this.mCtx = mCtx;
        this.dataHandler_generalResearches = dataHandler_generalResearches;
        this.data = data;
        this.header = header;
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
        if (data.equals("careplan")){
            holder.course.setText(dataHandler_generalResearches.get(position).getHeader());
            holder.course.setOnClickListener(v -> {
                NursingCarePlans_sub_Frag.Sub = String.valueOf(dataHandler_generalResearches.get(position).getHeader());
                FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(mCtx)).getSupportFragmentManager();
                FragmentTransaction fr = manager.beginTransaction();
                fr.replace(R.id.flContent, new NursingCarePlans_sub_Frag());
                fr.addToBackStack(null);
                fr.commit();
            });
        }else if (data.equals("sub")){
            holder.course.setText(dataHandler_generalResearches.get(position).getHeader());
            holder.course.setOnClickListener(v -> {
                //header
                //Toast.makeText(mCtx, header, Toast.LENGTH_LONG).show();
                give_new_name.selectedsub =String.valueOf(dataHandler_generalResearches.get(position).getHeader());
                give_new_name.selectedtopic = header;
                FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(mCtx)).getSupportFragmentManager();
                FragmentTransaction fr = manager.beginTransaction();
                fr.replace(R.id.flContent, new give_new_name());
                fr.addToBackStack(null);
                fr.commit();
            });
        }else{
            holder.course.setText(dataHandler_generalResearches.get(position).getHeader());
            holder.course.setOnClickListener(v -> {
                GeneralResearchContent_Frag.id2 = String.valueOf(dataHandler_generalResearches.get(position).getHeader());
                FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(mCtx)).getSupportFragmentManager();
                FragmentTransaction fr = manager.beginTransaction();
                fr.replace(R.id.flContent, new GeneralResearchContent_Frag());
                fr.addToBackStack(null);
                fr.commit();
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataHandler_generalResearches.size();
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
