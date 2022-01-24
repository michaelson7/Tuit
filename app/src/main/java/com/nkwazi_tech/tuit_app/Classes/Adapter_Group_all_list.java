package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Activities.GroupChat_activity;
import com.nkwazi_tech.tuit_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Group_all_list extends RecyclerView.Adapter<Adapter_Group_all_list.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    public static String state;

    public Adapter_Group_all_list(Context mCtx, List<DataHandler_Group_all_list> dataHandler_group_all_lists, String state) {
        this.mCtx = mCtx;
        Adapter_Group_all_list.state = state;
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

        } else {
            Glide.with(mCtx)
                    .load(dataHandler_group_all_lists.get(position).getGroupImg())
                    .into(holder.groupimg);

            holder.groupdiscruption.setText(dataHandler_group_all_lists.get(position).getGroupDiscription());
            holder.join.setOnClickListener(v -> {
                String username = SharedPrefManager.getInstance(mCtx).getUsername();
                String groupname = dataHandler_group_all_lists.get(position).getGroupName();

                holder.join.setVisibility(View.INVISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);

                @SuppressLint("StaticFieldLeak")
                class LikesState extends AsyncTask<Void, Void, String> {
                    @Override
                    protected String doInBackground(Void... voids) {
                        //creating request handler object
                        RequestHandler requestHandler = new RequestHandler();
                        //creating request parameters
                        HashMap<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("groupname", groupname);

                        //returing the response
                        return requestHandler.sendPostRequest(URLs.URL_joinGroup, params);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(s);
                            //if no error in response
                            if (!obj.getBoolean("error")) {

                                String topic = groupname.replaceAll("\\s+", "");
                                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String msg;
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(mCtx, "failed", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    try {
                                                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Intent intent = new Intent(mCtx, GroupChat_activity.class);
                                                    if (dataHandler_group_all_lists.get(position).getNewGroupName() != "null") {
                                                        intent.putExtra("GroupName", dataHandler_group_all_lists.get(position).getNewGroupName());
                                                    } else {
                                                        intent.putExtra("GroupName", dataHandler_group_all_lists.get(position).getGroupName());
                                                    }
                                                    intent.putExtra("GroupName", dataHandler_group_all_lists.get(position).getGroupName());
                                                    intent.putExtra("GroupImg", dataHandler_group_all_lists.get(position).getGroupImg());
                                                    mCtx.startActivity(intent);
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                            holder.join.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.INVISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                LikesState ul = new LikesState();
                ul.execute();
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

        TextView groupname, groupdiscruption;
        ImageView groupimg;
        ProgressBar progressBar;
        Button join;

        public ProductViewHolder(View itemView) {
            super(itemView);
            if (state.equals("my_group")) {
                groupname = itemView.findViewById(R.id.lecturername);
            } else {
                groupname = itemView.findViewById(R.id.textView12);
                groupdiscruption = itemView.findViewById(R.id.textView47);
                groupimg = itemView.findViewById(R.id.groupimg);
                join = itemView.findViewById(R.id.button10);
                progressBar = itemView.findViewById(R.id.progressBar13);
            }

        }
    }

}
