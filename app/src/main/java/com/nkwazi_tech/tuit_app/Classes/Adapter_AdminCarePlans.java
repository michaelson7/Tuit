package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.give_new_name;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Adapter_AdminCarePlans extends RecyclerView.Adapter<Adapter_AdminCarePlans.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_AdminCarePlans> dataHandler_AdminCarePlans;
    ProgressDialog p;

    public Adapter_AdminCarePlans(Context mCtx, List<DataHandler_AdminCarePlans> dataHandler_AdminCarePlans) {
        this.mCtx = mCtx;
        this.dataHandler_AdminCarePlans = dataHandler_AdminCarePlans;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_admincareplan, parent, false);
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String id = String.valueOf(dataHandler_AdminCarePlans.get(position).getId());
        String img = dataHandler_AdminCarePlans.get(position).getImg();
        String topic = dataHandler_AdminCarePlans.get(position).getTopic();
        String notes = dataHandler_AdminCarePlans.get(position).getNotes();

        if (!img.contains("/null")) {
            Glide.with(mCtx).
                    load(img).
                    thumbnail(0.11f).
                    into(holder.img);
        } else {
            holder.img.setVisibility(View.GONE);
        }

        holder.topic.setText(topic + ":");
        holder.notes.setText(notes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.notes.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        String admin = SharedPrefManager.getInstance(mCtx).getAccounttype();
        if (admin.equals("admin")) {
            holder.edit.setVisibility(View.VISIBLE);
            holder.edit.setOnClickListener(v -> {
                String[] SortyBy = {"Edit", "Delete"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx,R.style.AlertDialog);
                builder.setTitle("Select Option");
                builder.setItems(SortyBy, (dialog, which) -> {
                    if (which == 0) {
                        give_new_name.id2 = id;
                        give_new_name.topic2 = topic;
                        give_new_name.notes2 = notes;
                        give_new_name.img2 = img;
                        FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                        FragmentTransaction fr = manager.beginTransaction();
                        fr.replace(R.id.flContent, new give_new_name());
                        fr.commit();
                    } else if (which == 1) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(mCtx,R.style.AlertDialog);
                        builders.setCancelable(true);
                        builders.setTitle("Confirmation");
                        builders.setMessage("Are you sure you want to delete this topic?");
                        builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteCarePlan(id,position);
                            }
                        });
                        builders.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builders.show();
                    }

                });
                builder.show();
            });
        }
    }

    private void DeleteCarePlan(String id,int position) {
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                //returing the response

                return requestHandler.sendPostRequest(URLs.URL_DeleteAdminCarePlan, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Deleting CarePlan...");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                p.hide();

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                       dataHandler_AdminCarePlans.remove(position);
                       notifyDataSetChanged();
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        DeleteVideo ul = new DeleteVideo();
        ul.execute();
    }

    @Override
    public int getItemCount() {
        return dataHandler_AdminCarePlans.size();
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
        TextView topic;
        TextView notes;
        ImageView img;
        Button edit;

        public ProductViewHolder(View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.textView46);
            img = itemView.findViewById(R.id.imageView8);
            notes = itemView.findViewById(R.id.notesRS);
            edit = itemView.findViewById(R.id.teamEdit);
        }
    }

}
