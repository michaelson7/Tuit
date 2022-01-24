package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter_Admin_UserMod extends RecyclerView.Adapter<Adapter_Admin_UserMod.ProductViewHolder> {
    private Context mCtx;
    private static List<DataHandler_User> dataHandler_User;
    ProgressDialog p;
    private Dialog suspendDialog;
    String state;

    public Adapter_Admin_UserMod(Context mCtx, List<DataHandler_User> dataHandler_User, String state) {
        this.mCtx = mCtx;
        Adapter_Admin_UserMod.dataHandler_User = dataHandler_User;
        this.state = state;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_admin_usermod, parent, false);
        // click listener here
        suspendDialog = new Dialog(mCtx);
        //releasePlayer();
        return new ProductViewHolder(views);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String profileimg = dataHandler_User.get(position).getProfilepicture();

        Glide.with(mCtx).
                load(dataHandler_User.get(position).getProfilepicture()).
                thumbnail(0.8f).
                placeholder(R.mipmap.girl).
                error(R.mipmap.girl).
                into(holder.profilePicture);


        holder.name.setText("Email: " + dataHandler_User.get(position).getEmail() +
                "Name:" + dataHandler_User.get(position).getName());
        holder.email.setText(dataHandler_User.get(position).getEmail());
        holder.accounttype.setText(dataHandler_User.get(position).getAccounttype());
        holder.dob.setVisibility(View.GONE);
        holder.year.setVisibility(View.GONE);
        holder.lecturingcourse.setVisibility(View.GONE);

        if (state.equals("Approval")) {
            holder.action.setOnClickListener(v -> {
                String[] Option = {"View Qualifications", "Accept Request", "Deny Request"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx,R.style.AlertDialog);
                builder.setTitle("Select Action");
                builder.setItems(Option, (dialog, which) -> {
                    if (which == 0) {
                        Uri path = Uri.parse(dataHandler_User.get(position).getName());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mCtx.startActivity(intent);
                    } else if (which == 1) {
                        DeleteVideo(String.valueOf(dataHandler_User.get(position).getId()), "accepted", position);
                    } else if (which == 2) {
                        DeleteVideo(String.valueOf(dataHandler_User.get(position).getId()), "denied", position);
                    }
                });
                builder.show();
            });
        } else {
            holder.action.setOnClickListener(v -> {
                String[] Option = {"Suspend Account", "Delete Account"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx,R.style.AlertDialog);
                builder.setTitle("Select Action");
                builder.setItems(Option, (dialog, which) -> {
                    if (which == 0) {
                        SuspendAccountDialog(dataHandler_User.get(position).getId(), position);
                    } else if (which == 1) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(mCtx,R.style.AlertDialog);
                        builders.setCancelable(true);
                        builders.setTitle("Confirmation");
                        builders.setMessage("Are you sure you want to delete this user?");
                        builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteVideo(String.valueOf(dataHandler_User.get(position).getId()), "", position);
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

    private void SuspendAccountDialog(int id, int position) {
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

        title.setText("Suspend Account");
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

            DeleteVideo(String.valueOf(id), getterms, position);
        });
        suspendDialog.show();
    }

    private void DeleteVideo(String id, String num, int position) {
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                if (num.equals("")) {
                    params.put("id", id);
                } else if (num.equals("accepted")) {
                    params.put("ids", id);
                    params.put("responsee", num);
                } else if (num.equals("denied")) {
                    params.put("ids", id);
                    params.put("responsee", num);
                } else {
                    params.put("id", id);
                    params.put("duration", num);
                }
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_modeuser, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                if (num == null) {
                    p.setMessage("Deleting User...");
                } else if (num.equals("accepted")) {
                    p.setMessage("Sending Accepted Response...");
                } else if (num.equals("denied")) {
                    p.setMessage("Sending Denied Response...");
                } else {
                    p.setMessage("Suspending User...");
                }
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
                        dataHandler_User.remove(position);
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

    public void filterlist(ArrayList<DataHandler_User> filteredlist) {
        dataHandler_User = filteredlist;
        notifyDataSetChanged();
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


}
