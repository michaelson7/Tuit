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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nkwazi_tech.tuit_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Adapter_GeneralResearchContent extends RecyclerView.Adapter<Adapter_GeneralResearchContent.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_GeneralResearch> dataHandler_generalResearches;
    public static ImageView careimg;
    Dialog editcareplans;
    ProgressDialog p;

    public Adapter_GeneralResearchContent(Context mCtx, List<DataHandler_GeneralResearch> dataHandler_generalResearches) {
        this.mCtx = mCtx;
        this.dataHandler_generalResearches = dataHandler_generalResearches;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_generalresearchlist, parent, false);
        editcareplans = new Dialog(mCtx);
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        String id = String.valueOf(dataHandler_generalResearches.get(position).getId());
        String topic = dataHandler_generalResearches.get(position).getSubheader();
        String notes = dataHandler_generalResearches.get(position).getNotes();
        String path = dataHandler_generalResearches.get(position).getPdffile();

        holder.topic.setText(topic + ":");
        holder.notes.setText(notes);

        if (!path.contains("/null")){
            holder.filepath.setText("PDF File");
            holder.cardView.setOnClickListener(v -> {
                Uri paths =  Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(paths, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mCtx.startActivity(intent);
            });
        }else{
            holder.cardView.setVisibility(View.GONE);
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
                       Open_Edit_Dialog(id,topic,notes);

                    } else if (which == 1) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(mCtx,R.style.AlertDialog);
                        builders.setCancelable(true);
                        builders.setTitle("Confirmation");
                        builders.setMessage("Are you sure you want to delete this topic?");
                        builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteCarePlan(id,position,null);
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

    private void Open_Edit_Dialog(String id, String topic, String notes) {
        editcareplans.setContentView(R.layout.dialog_admincareplanedit);
        Objects.requireNonNull(editcareplans.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = editcareplans.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit;
        EditText notess;
        TextView imgselector,title,imagecancel2;

        submit = editcareplans.findViewById(R.id.button7);
        notess = editcareplans.findViewById(R.id.notestxt2);
        careimg = editcareplans.findViewById(R.id.imageView7);
        imgselector = editcareplans.findViewById(R.id.btncomment);
        title = editcareplans.findViewById(R.id.textView64);
        imagecancel2= editcareplans.findViewById(R.id.imagecancel2);

        careimg.setVisibility(View.GONE);
        imgselector.setVisibility(View.GONE);
        imagecancel2.setVisibility(View.GONE);

        notess.setText(notes);
        title.setText(topic);
        notess.setMovementMethod(new ScrollingMovementMethod());

        submit.setOnClickListener(v -> {
            String getnotes = notess.getText().toString().trim();
            DeleteCarePlan(id,0,getnotes);
        });
        editcareplans.show();
    }

    private void DeleteCarePlan(String id, int position,String text) {
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                if (text != null){
                    params.put("text", text);
                }
                //returing the response

                return requestHandler.sendPostRequest(URLs.delete_General, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (text != null){
                    p = new ProgressDialog(mCtx);
                    p.setMessage("Updating Notes...");
                    p.setIndeterminate(false);
                    p.setCancelable(false);
                    p.show();
                }else{
                    p = new ProgressDialog(mCtx);
                    p.setMessage("Deleting Notes...");
                    p.setIndeterminate(false);
                    p.setCancelable(false);
                    p.show();
                }

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
                        if (text == null){
                            dataHandler_generalResearches.remove(position);
                            notifyDataSetChanged();
                        }

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
        TextView topic, notes,filepath;
        CardView cardView;
        Button edit;

        public ProductViewHolder(View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.textView46);
            filepath = itemView.findViewById(R.id.textView44);
            notes = itemView.findViewById(R.id.notesRS);
            edit = itemView.findViewById(R.id.teamEdit);
            cardView = itemView.findViewById(R.id.userimg);
        }
    }

}
