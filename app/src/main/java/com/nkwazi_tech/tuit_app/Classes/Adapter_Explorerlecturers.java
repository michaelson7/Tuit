package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Activities.AboutUs_Activity;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Adapter_Explorerlecturers extends RecyclerView.Adapter<Adapter_Explorerlecturers.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_User> dataHandler_User;
    String data;

    public Adapter_Explorerlecturers(Context mCtx, List<DataHandler_User> dataHandler_User, String data) {
        this.mCtx = mCtx;
        this.dataHandler_User = dataHandler_User;
        this.data = data;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_explorelecturers, parent, false);
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String profileimg =dataHandler_User.get(position).getProfilepicture();
            Glide.with(mCtx)
                    .load(dataHandler_User.get(position).getProfilepicture())
                    .placeholder(R.mipmap.girl)
                    .error(R.mipmap.girl)
                    .into(holder.img);
        holder.name.setText(dataHandler_User.get(position).getName());

        if(data.equals("admin")){
           holder.delete.setVisibility(View.VISIBLE);
           holder.delete.setOnClickListener(v -> {
               String[] options2 = new String[]{"Delete", "Cancel"};
               String[] SortyBy2 = options2;
               androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(mCtx,R.style.AlertDialog);
               builder2.setTitle("Are you sure you want to delete this member?");
               builder2.setItems(SortyBy2, (dialog2, which2) -> {
                   if (which2 == 0) {
                      DeleteUser(dataHandler_User.get(position).getId());
                   } else if (which2 == 1) {
                   }
               });
               builder2.show();
           });
        }
    }

    private void DeleteUser(int id) {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_deletmember, params);
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //  progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // progressBar.setVisibility(View.GONE);
                try {
                    //converting the string to json array object
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        //getting the dataHandlerUser from the response
                        String response = obj.getString("message");
                        Toast.makeText(mCtx, response, Toast.LENGTH_SHORT).show();
                        mCtx.startActivity(new Intent(mCtx, AboutUs_Activity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    @Override
    public int getItemCount() {
        return dataHandler_User.size();
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
        TextView name,delete;
        ImageView img;

        public ProductViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.lecturername);
            img = itemView.findViewById(R.id.lecturerimg);
            delete= itemView.findViewById(R.id.imagecancel4);
        }
    }
}
