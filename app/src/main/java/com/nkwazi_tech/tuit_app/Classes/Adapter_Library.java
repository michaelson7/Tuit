package com.nkwazi_tech.tuit_app.Classes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.LibraryPdf_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Library_Files_Edit_Frag;
import com.nkwazi_tech.tuit_app.Fragments.PDFviewer_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Subscription_center_Frag;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Library extends RecyclerView.Adapter<Adapter_Library.ProductViewHolder> {

    private Context mCtx;
    DownloadManager manager;
    private long downloadID;
    public static String encrypted_Name, edit, user_balance, course_price, course_name, course_img, course_id;

    private List<DataHandler_careplans> dataHandler_careplans;
    ProgressDialog p;
    private Dialog editdialog, respondeDialog, lecturerresponse, sub_Dialog,pay_Response_Dialog;

    public Adapter_Library(Context mCtx, List<DataHandler_careplans> dataHandler_careplans, String edit) {
        this.mCtx = mCtx;
        this.dataHandler_careplans = dataHandler_careplans;
        this.edit = edit;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_careplan, parent, false);
        editdialog = new Dialog(mCtx);
        respondeDialog = new Dialog(mCtx);
        lecturerresponse = new Dialog(mCtx);
        sub_Dialog = new Dialog(mCtx);
        pay_Response_Dialog = new Dialog(mCtx);
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        if (edit.equals("research")) {
            holder.options.setVisibility(View.VISIBLE);

            holder.username.setText(dataHandler_careplans.get(position).getUsername());
            holder.title.setText(dataHandler_careplans.get(position).getTitle());
            holder.bookthubnail.setVisibility(View.GONE);

            holder.options.setOnClickListener(v -> {
                String[] Option = {"Responde"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx, R.style.AlertDialog);
                builder.setTitle("Select Action");
                builder.setItems(Option, (dialog, which) -> {
                    if (which == 0) {
                        ShowRespondeDialog(
                                dataHandler_careplans.get(position).getId()
                        );
                    } else if (which == 1) {
                    }
                });
                builder.show();
            });
            holder.cardView.setOnClickListener(v -> {
                PDFviewer_Frag.pdffilepath = dataHandler_careplans.get(position).getPdfFile();
                FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                FragmentTransaction fr = manager.beginTransaction();
                fr.replace(R.id.flContent, new PDFviewer_Frag());
                fr.addToBackStack(null);
                fr.commit();
            });

        } else if (edit.equals("viewresponse")) {
            String name = SharedPrefManager.getInstance(mCtx).getName();

            holder.username.setText(name);
            holder.title.setText(dataHandler_careplans.get(position).getTitle());

            holder.options.setVisibility(View.VISIBLE);
            holder.bookthubnail.setVisibility(View.GONE);
            holder.options.setOnClickListener(v -> {
                String[] Option = {"View Lecturer Response", "Delete"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx, R.style.AlertDialog);
                builder.setTitle("Select Action");
                builder.setItems(Option, (dialog, which) -> {
                    if (which == 0) {
                        ShowResponseDialog(dataHandler_careplans.get(position).getResponse());
                    } else if (which == 1) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(mCtx, R.style.AlertDialog);
                        builders.setCancelable(true);
                        builders.setTitle("Confirmation");
                        builders.setMessage("Are you sure you want to delete this video?");
                        builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteLibrary(String.valueOf(dataHandler_careplans.get(position).getId()), position);
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
            holder.cardView.setOnClickListener(v -> {
                PDFviewer_Frag.pdffilepath = dataHandler_careplans.get(position).getPdfFile();
                FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                FragmentTransaction fr = manager.beginTransaction();
                fr.replace(R.id.flContent, new PDFviewer_Frag());
                fr.addToBackStack(null);
                fr.commit();
            });
        } else {
            //Checking if subscribed'
            String id = String.valueOf(SharedPrefManager.getInstance(mCtx).getID());
            String courseid = dataHandler_careplans.get(position).getTopic();
            String acoount_Type = String.valueOf(SharedPrefManager.getInstance(mCtx).getAccounttype());

            Glide.with(mCtx)
                    .load(dataHandler_careplans.get(position).getProfilePicture())
                    .placeholder(R.mipmap.logo)
                    .error(R.mipmap.logo)
                    .into(holder.propic);
            holder.username.setText(dataHandler_careplans.get(position).getUsername() + " (" +
                    dataHandler_careplans.get(position).getTopic() + ")");
            holder.title.setText(dataHandler_careplans.get(position).getTitle());
            holder.bookthubnail.setVisibility(View.GONE);

            //check account type
            if (acoount_Type.equals("student")) {
                class LikesState extends AsyncTask<Void, Void, String> {
                    @Override
                    protected String doInBackground(Void... voids) {
                        RequestHandler requestHandler = new RequestHandler();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("courseid", String.valueOf(courseid));
                        params.put("userID", id);
                        params.put("state", "library");
                        return requestHandler.sendPostRequest(URLs.check_Subscription, params);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {
                            JSONObject obj = new JSONObject(s);
                            if (!obj.getBoolean("state") && acoount_Type.equals("student")) {
                                holder.cardView.setEnabled(false);
                                holder.options.setEnabled(false);

                                int mul = 0xFF7F7F7F;
                                int add = 0x00000000;
                                LightingColorFilter lcf = new LightingColorFilter(mul, add);
                                holder.propic.setColorFilter(lcf);
                                holder.username.setTextColor(Color.parseColor("#919191"));
                                holder.title.setTextColor(Color.parseColor("#919191"));
                                try {
                                    LibraryPdf_Frag.subscription.setVisibility(View.VISIBLE);
                                    LibraryPdf_Frag.subscription.setText("Click here to subscribe to course");
                                    LibraryPdf_Frag.subscription.setOnClickListener(v -> {
                                        Subscribe_Dialog(course_price, course_name, course_img, Integer.parseInt(course_id));
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return;
                            } else {
                                try {
                                    LibraryPdf_Frag.subscription.setVisibility(View.GONE);
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                LikesState ul = new LikesState();
                ul.execute();
            }

            holder.cardView.setOnClickListener(v -> {
                PDFviewer_Frag.pdffilepath = dataHandler_careplans.get(position).getPdfFile();
                FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                FragmentTransaction fr = manager.beginTransaction();
                fr.replace(R.id.flContent, new PDFviewer_Frag());
                fr.addToBackStack(null);
                fr.commit();
            });

            holder.options.setVisibility(View.VISIBLE);
            holder.options.setOnClickListener(v -> {
                String accounttype = SharedPrefManager.getInstance(mCtx).getAccounttype();
                if (!accounttype.equals("student")) {
                    String[] Option = {"Edit", "Delete", "Download"};
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mCtx, R.style.AlertDialog);
                    builder.setTitle("Select Action");
                    builder.setItems(Option, (dialog, which) -> {
                        if (which == 0) {
                            ShowVideoEditDialog(
                                    dataHandler_careplans.get(position).getTitle(), dataHandler_careplans.get(position).getDescription(),
                                    dataHandler_careplans.get(position).getTopic(), dataHandler_careplans.get(position).getId()
                            );
                        } else if (which == 1) {
                            AlertDialog.Builder builders = new AlertDialog.Builder(mCtx, R.style.AlertDialog);
                            builders.setCancelable(true);
                            builders.setTitle("Confirmation");
                            builders.setMessage("Are you sure you want to delete this video?");
                            builders.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DeleteCarePlan(String.valueOf(dataHandler_careplans.get(position).getId())
                                    );
                                }
                            });
                            builders.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            builders.show();
                        } else if (which == 2) {
                            if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mCtx,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                // this will request for permission when user has not granted permission for the app
                                ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                            Toast.makeText(mCtx, "Downloading...", Toast.LENGTH_LONG).show();
                            File folder = new File(Environment.getExternalStorageDirectory() +
                                    File.separator + "EducationalApp");
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }
                            String path = dataHandler_careplans.get(position).getPdfFile();
                            DownloadReceiver.data(dataHandler_careplans.get(position).getTitle().trim(),".pdf",path,mCtx);
                        }
                    });
                    builder.show();
                } else {
                    PopupMenu popup = new PopupMenu(mCtx, holder.options);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_download, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(items -> {
                        if (items.getItemId() == R.id.download) {
                            if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mCtx,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                // this will request for permission when user has not granted permission for the app
                                ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                            try {
                                File folder = new File(Environment.getExternalStorageDirectory() +
                                        File.separator + "EducationalApp");
                                if (!folder.exists()) {
                                    folder.mkdirs();
                                }
                                String path = dataHandler_careplans.get(position).getPdfFile();
                                DownloadReceiver.data(dataHandler_careplans.get(position).getTitle().trim(),".pdf",path,mCtx);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    });
                    popup.show();//showing popup menu
                }
            });
        }
    }

    private void DeleteLibrary(String id, int position) {
        @SuppressLint("StaticFieldLeak")
        class DeleteVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                return requestHandler.sendPostRequest(URLs.URL_deleteresearchfiles, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Deleting...");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                p.hide();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        dataHandler_careplans.remove(position);
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

    private void ShowResponseDialog(String response) {
        lecturerresponse.setContentView(R.layout.dialog_termsandconditions);
        lecturerresponse.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = lecturerresponse.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView title, Text;
        title = lecturerresponse.findViewById(R.id.textView3);
        Text = lecturerresponse.findViewById(R.id.textView13);
        title.setText("Lecturer Response");
        Text.setText(response);

        lecturerresponse.show();
    }

    private void ShowRespondeDialog(int id) {
        respondeDialog.setContentView(R.layout.dialog_termsandconditionsedit);
        respondeDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = respondeDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView title;
        EditText Text;
        Button btn;

        title = respondeDialog.findViewById(R.id.textView3);
        Text = respondeDialog.findViewById(R.id.textView13);
        btn = respondeDialog.findViewById(R.id.button8);

        btn.setText("Send");
        title.setText("Send Response");

        btn.setOnClickListener(v -> {
            String getText = Text.getText().toString().trim();

            if (TextUtils.isEmpty(getText)) {
                Text.setError("Enter Text");
                Text.requestFocus();
                return;
            }

            SendResponse(getText, id);
        });

        respondeDialog.show();
    }


    private void ShowVideoEditDialog(String title, String description, String topic, int id) {
        Button submit;
        EditText titles, descriptions;
        Spinner topics;
        final String[] selectedtopic = new String[1];

        editdialog.setContentView(R.layout.dialog_pdfedit);
        editdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = editdialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        topics = editdialog.findViewById(R.id.practicenumber);
        topics.setVisibility(View.GONE);
        titles = editdialog.findViewById(R.id.course);
        descriptions = editdialog.findViewById(R.id.phone);
        submit = editdialog.findViewById(R.id.button2);

        selectedtopic[0] = topic;
        String c1 = String.valueOf(SharedPrefManager.getInstance(mCtx).getCoursename1());
        String c2 = String.valueOf(SharedPrefManager.getInstance(mCtx).getCoursename2());
        String c3 = String.valueOf(SharedPrefManager.getInstance(mCtx).getCoursename3());
        String[] status = {topic, c1, c2, c3};
        topics.setAdapter(new ArrayAdapter<String>(mCtx, R.layout.support_simple_spinner_dropdown_item, status));
        topics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedtopic[0] = topics.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        titles.setText(title);
        descriptions.setText(description);

        submit.setOnClickListener(v -> {
            String Etopic = selectedtopic[0];
            String Etitle = titles.getText().toString().trim();
            String Edescriptiob = descriptions.getText().toString().trim();

            if (TextUtils.isEmpty(Etitle)) {
                titles.setError("Enter title");
                titles.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(Edescriptiob)) {
                descriptions.setError("Enter description");
                descriptions.requestFocus();
                return;
            }

            UpdatePdf(id, Etopic, Etitle, Edescriptiob);

        });
        editdialog.show();
    }

    private void SendResponse(String getText, int id) {
        @SuppressLint("StaticFieldLeak")
        class updatevideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("Lresponse", getText);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_updateresponse, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Sending Response...");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                p.hide();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        respondeDialog.hide();
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        updatevideo ul = new updatevideo();
        ul.execute();
    }

    private void UpdatePdf(int id, String etopic, String etitle, String edescriptiob) {
        @SuppressLint("StaticFieldLeak")
        class updatevideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("topic", etopic);
                params.put("description", edescriptiob);
                params.put("title", etitle);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_updatepdf, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("Updating Care Plan...");
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
                        fr.replace(R.id.flContent, new Library_Files_Edit_Frag());
                        fr.commit();
                        editdialog.hide();
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        updatevideo ul = new updatevideo();
        ul.execute();
    }

    private void DeleteCarePlan(String pdfid) {
        @SuppressLint("StaticFieldLeak")
        class updatevideo extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", pdfid);
                // params.put("lecturerid", userid);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_deletepdf, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                p = new ProgressDialog(mCtx);
                p.setMessage("DeleteCare Plan...");
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
                        fr.replace(R.id.flContent, new Library_Files_Edit_Frag());
                        fr.commit();
                        editdialog.hide();
                    } else {
                        Toast.makeText(mCtx, "File deleted", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        updatevideo ul = new updatevideo();
        ul.execute();
    }

    private void Subscribe_Dialog(String course_price, String name, String img_path, int id) {
        sub_Dialog.setContentView(R.layout.dialog_subscription);
        Objects.requireNonNull(sub_Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = sub_Dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView subText, title, C_price, balance;
        ImageView img;
        Button submit;
        ProgressBar progressBar;

        subText = sub_Dialog.findViewById(R.id.title3);
        C_price = sub_Dialog.findViewById(R.id.title8);
        img = sub_Dialog.findViewById(R.id.imageView3);
        submit = sub_Dialog.findViewById(R.id.button2);
        title = sub_Dialog.findViewById(R.id.title7);
        progressBar = sub_Dialog.findViewById(R.id.progressBar12);
        balance = sub_Dialog.findViewById(R.id.title4);

        int mul = 0xFF7F7F7F;
        int add = 0x00000000;
        LightingColorFilter lcf = new LightingColorFilter(mul, add);
        img.setColorFilter(lcf);
        Glide.with(Objects.requireNonNull(mCtx))
                .asBitmap()
                .load(img_path)
                .placeholder(R.mipmap.logo)
                .into(img);

        title.setText(course_name);
        balance.setText("Availabe Balance: K-" + user_balance);

        C_price.setText("K" + course_price + "/Month");
        subText.setText("Are you sure you want to subscribe to " + name + " for K-" + course_price + "?\n\n" +
                "Subscription will be valid for 30 days");


        submit.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            submit.setVisibility(View.INVISIBLE);

            String userID = String.valueOf(SharedPrefManager.getInstance(mCtx).getID());
            String names = String.valueOf(SharedPrefManager.getInstance(mCtx).getName());
            String email = String.valueOf(SharedPrefManager.getInstance(mCtx).getEmail());

            SubscribeUser(userID, id, names, email);
        });

        sub_Dialog.show();
    }

    private void SubscribeUser(String userID, int id, String names, String email) {
        @SuppressLint("StaticFieldLeak")
        class LikesState extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("userID", String.valueOf(userID));
                params.put("courseid", String.valueOf(course_id));
                params.put("name", String.valueOf(names));
                params.put("email", String.valueOf(email));
                params.put("nrc_num", String.valueOf(userID));
                params.put("state", String.valueOf(false));

                //returing the response
                return requestHandler.sendPostRequest(URLs.Course_Subscription, params);
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
                        String new_balance = obj.getString("balance");
                        String message = obj.getString("message");
                        sub_Dialog.dismiss();
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();

                        ShowSuccessDialog(Integer.parseInt(course_price), message);

                    } else {
                        sub_Dialog.dismiss();
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LikesState ul = new LikesState();
        ul.execute();
    }

    private void ShowSuccessDialog(int amount, String new_balance) {
        pay_Response_Dialog.setContentView(R.layout.dialog_paymeant_response);
        Objects.requireNonNull(pay_Response_Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = pay_Response_Dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView dates, names, emails, amounts, times, refrenceCode;
        CircleImageView imageView;

        dates = pay_Response_Dialog.findViewById(R.id.usernametxt28);
        names = pay_Response_Dialog.findViewById(R.id.usernametxt30);
        emails = pay_Response_Dialog.findViewById(R.id.usernametxt31);
        amounts = pay_Response_Dialog.findViewById(R.id.usernametxt33);
        times = pay_Response_Dialog.findViewById(R.id.usernametxt36);
        imageView = pay_Response_Dialog.findViewById(R.id.imageView13);
        refrenceCode = pay_Response_Dialog.findViewById(R.id.usernametxt38);

        String imgPath = SharedPrefManager.getInstance(mCtx).getPropic();
        String name = SharedPrefManager.getInstance(mCtx).getName();
        String email = SharedPrefManager.getInstance(mCtx).getEmail();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        Glide.with(mCtx)
                .asBitmap()
                .load(imgPath)
                .error(R.mipmap.girl)
                .placeholder(R.mipmap.girl)
                .into(imageView);
        names.setText(name);
        emails.setText(email);
        amounts.setText("K~" + amount);
        times.setText(currentTime);
        dates.setText(currentDate);

        refrenceCode.setText(new_balance);

        pay_Response_Dialog.setOnDismissListener(dialog -> {
            FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
            FragmentTransaction fr = manager.beginTransaction();
            fr.replace(R.id.flContent, new Subscription_center_Frag());
            fr.addToBackStack(null);
            fr.commit();
        });

        pay_Response_Dialog.show();
    }

    @Override
    public int getItemCount() {
        return dataHandler_careplans.size();
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

        CardView cardView;
        TextView username, title;
        ImageView propic;
        TextView bookthubnail;
        ImageView options;

        public ProductViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView4);
            username = itemView.findViewById(R.id.cources);
            title = itemView.findViewById(R.id.message);
            propic = itemView.findViewById(R.id.userimg);
            bookthubnail = itemView.findViewById(R.id.cources5);
            options = itemView.findViewById(R.id.imageButton2);
        }
    }

}
