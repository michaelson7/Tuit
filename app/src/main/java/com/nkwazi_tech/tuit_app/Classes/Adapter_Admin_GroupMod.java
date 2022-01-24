package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.R;
import com.nkwazi_tech.tuit_app.group.Admin_GroupMod_Frag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter_Admin_GroupMod extends RecyclerView.Adapter<Adapter_Admin_GroupMod.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    ProgressDialog p;
    private List<DataHandler_Group_all_list> dataHandler_group_all_lists;
    private Dialog suspendDialog;

    public Adapter_Admin_GroupMod(Context mCtx, List<DataHandler_Group_all_list> dataHandler_group_all_lists) {
        this.mCtx = mCtx;
        this.dataHandler_group_all_lists = dataHandler_group_all_lists;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_admin_usermod, parent, false);
        suspendDialog = new Dialog(mCtx);
        return new ProductViewHolder(views);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String profileimg = dataHandler_group_all_lists.get(position).getGroupImg();
        Glide.with(mCtx).
                load(dataHandler_group_all_lists.get(position).getGroupImg()).
                thumbnail(0.8f).
                placeholder(R.mipmap.girl).
                error(R.mipmap.girl).
                into(holder.profilePicture);
        holder.name.setText("Group Name: " + dataHandler_group_all_lists.get(position).getGroupName());
        holder.gender.setText("Description: " + dataHandler_group_all_lists.get(position).getGroupDiscription());
        holder.email.setText("Admin: " + dataHandler_group_all_lists.get(position).getAdmin());
        holder.dob.setText("#Members: " + dataHandler_group_all_lists.get(position).getMembers());
        holder.action.setOnClickListener(v -> {
            String[] Option = {"Suspend Group", "Delete Group"};
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx);
            builder.setTitle("Select Action");
            builder.setItems(Option, (dialog, which) -> {
                if (which == 0) {
                    SuspendAccountDialog(dataHandler_group_all_lists.get(position).getGroupName(), position);
                } else if (which == 1) {
                    AlertDialog.Builder builders = new AlertDialog.Builder(mCtx);
                    builders.setCancelable(true);
                    builders.setTitle("Confirmation");
                    builders.setMessage("Are you sure you want to delete this Group?");
                    builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DeleteGroup(dataHandler_group_all_lists.get(position).getGroupName(), position);
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

        holder.accounttype.setVisibility(View.GONE);
        holder.year.setVisibility(View.GONE);
        holder.lecturingcourse.setVisibility(View.GONE);
    }

    private void SuspendAccountDialog(String groupname, int position) {
        suspendDialog.setContentView(R.layout.dialog_termsandconditionsedit);
        suspendDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = suspendDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText Text;
        TextView title;

        title = suspendDialog.findViewById(R.id.textView3);
        Text = suspendDialog.findViewById(R.id.textView13);
        submit = suspendDialog.findViewById(R.id.button8);

        title.setText("Suspend Group");
        Text.setInputType(InputType.TYPE_CLASS_NUMBER);
        Text.setHint("Set Suspension Duration");
        submit.setText("Suspend");

        submit.setOnClickListener(v -> {
            final String getterms = Text.getText().toString().trim();
            if (TextUtils.isEmpty(getterms)) {
                Text.setError("Please Duration");
                Text.requestFocus();
                return;
            }

            SuspengGroup(groupname, getterms);
        });
        suspendDialog.show();
    }

    private void SuspengGroup(String groupname, String duration) {
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("groupname", groupname);
                params.put("duration", duration);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_modeuser, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Suspending Group...");
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
                        FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                        FragmentTransaction fr = manager.beginTransaction();
                        fr.replace(R.id.flContent, new Admin_GroupMod_Frag());
                        fr.commit();
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

    private void DeleteGroup(String GroupName, int position) {
        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("groupname", GroupName);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_deletegroup, params);
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
                        dataHandler_group_all_lists.remove(position);
                        notifyDataSetChanged();
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

        TextView name, gender, email, dob, accounttype, year, lecturingcourse;
        ImageView profilePicture;
        ImageView action;

        public ProductViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            email = itemView.findViewById(R.id.email);
            dob = itemView.findViewById(R.id.dob);
            accounttype = itemView.findViewById(R.id.accounttype);
            year = itemView.findViewById(R.id.year);
            lecturingcourse = itemView.findViewById(R.id.lecturingcourse);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            action = itemView.findViewById(R.id.action);
        }
    }

    public void filterlist(ArrayList<DataHandler_Group_all_list> filteredlist) {
        dataHandler_group_all_lists = filteredlist;
        notifyDataSetChanged();
    }
}
