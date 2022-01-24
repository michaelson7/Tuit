package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Fragments.Admin_CourseIconEdit_Frag;
import com.nkwazi_tech.tuit_app.Fragments.ExplorerCourse_Frag;
import com.nkwazi_tech.tuit_app.Fragments.Subscription_center_Frag;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Course extends RecyclerView.Adapter<Adapter_Course.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_Explorer> data_handlerExplorers;
    public static String courseid;
    public static String course_price;
    String admin;
    Dialog sub_Dialog, pay_Response_Dialog;

    public Adapter_Course(Context mCtx, List<DataHandler_Explorer> data_handlerExplorers, String admin) {
        this.mCtx = mCtx;
        this.data_handlerExplorers = data_handlerExplorers;
        this.admin = admin;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        sub_Dialog = new Dialog(mCtx);
        pay_Response_Dialog = new Dialog(mCtx);

        if (admin.equals("fetch_Available") || admin.equals("fetch_Subs")) {
            views = mInflater.inflate(R.layout.adapter_video_edit, parent, false);
        } else {
            views = mInflater.inflate(R.layout.adapter_courselist, parent, false);
        }

        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        String imagecourse = data_handlerExplorers.get(position).getImg();
        String price = String.valueOf(data_handlerExplorers.get(position).getPrice());
        String coursename = String.valueOf(data_handlerExplorers.get(position).getCoursename());
        String course_id = String.valueOf(data_handlerExplorers.get(position).getId());

        if (admin.equals("fetch_Available") || admin.equals("fetch_Subs")) {
            Glide.with(mCtx)
                    .load(imagecourse)
                    .placeholder(R.mipmap.logo)
                    .error(R.mipmap.logo)
                    .into(holder.imagecourse);
            holder.coursename.setText(data_handlerExplorers.get(position).getCoursename());
            holder.price.setText("K" + price + "/Month");

            holder.action.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(mCtx, holder.action);

                if (admin.equals("fetch_Available")) {
                    popup.getMenuInflater().inflate(R.menu.popup_subscribe, popup.getMenu());
                } else if (admin.equals("fetch_Subs")) {
                    popup.getMenuInflater().inflate(R.menu.popup_unsubscribe, popup.getMenu());
                }

                popup.setOnMenuItemClickListener(items -> {
                    if (admin.equals("fetch_Available")) {
                        if (items.getItemId() == R.id.subscribe) {
                            Subscribe_Dialog(imagecourse, price, coursename, course_id, false);
                        }
                    } else if (admin.equals("fetch_Subs")) {
                        if (items.getItemId() == R.id.unsubscribe) {
                            Subscribe_Dialog(imagecourse, price, coursename, course_id, true);
                        } else if (items.getItemId() == R.id.details) {
                            Show_description(course_id, coursename, price, imagecourse);
                        }
                    }
                    return true;
                });
                popup.show();//showing popup menu
            });
            holder.cardView.setOnClickListener(v -> {
                ExplorerCourse_Frag.price = String.valueOf(data_handlerExplorers.get(position).getPrice());
                ExplorerCourse_Frag.imagecourse = data_handlerExplorers.get(position).getImg();
                ExplorerCourse_Frag.coursename = data_handlerExplorers.get(position).getCoursename();
                ExplorerCourse_Frag.courseida = String.valueOf(data_handlerExplorers.get(position).getId());

                FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                FragmentTransaction fr = manager.beginTransaction();
                fr.replace(R.id.flContent, new ExplorerCourse_Frag());
                fr.addToBackStack(null);
                fr.commit();
            });
        } else {
            Random random = new Random();
            int color = Color.argb(160, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            holder.cardView.setCardBackgroundColor(color);
            holder.coursename.setText(data_handlerExplorers.get(position).getCoursename());

            try {
                Glide.with(mCtx)
                        .load(imagecourse)
                        .placeholder(R.mipmap.logo)
                        .error(R.mipmap.logo)
                        .into(holder.imagecourse);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!admin.equals("true")) {
                holder.cardView.setOnClickListener(v -> {
              /*DataHandler_VideoPlayerInfo.getInstance(mCtx).DataHandler_VideoPlayerInfo(videoDisplayHandlerList.get(position).getTitle(),
                    videoDisplayHandlerList.get(position).getdescription(), videoDisplayHandlerList.get(position).getVideo());*/
                    ExplorerCourse_Frag.price = String.valueOf(data_handlerExplorers.get(position).getPrice());
                    ExplorerCourse_Frag.imagecourse = data_handlerExplorers.get(position).getImg();
                    ExplorerCourse_Frag.coursename = data_handlerExplorers.get(position).getCoursename();
                    ExplorerCourse_Frag.courseida = String.valueOf(data_handlerExplorers.get(position).getId());
                    FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                    FragmentTransaction fr = manager.beginTransaction();
                    fr.replace(R.id.flContent, new ExplorerCourse_Frag());
                    fr.addToBackStack(null);
                    fr.commit();
                });
            } else {
                holder.cardView.setOnClickListener(v -> {
                    courseid = String.valueOf(data_handlerExplorers.get(position).getId());
                    Admin_CourseIconEdit_Frag.image = courseid;
                    FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                    FragmentTransaction fr = manager.beginTransaction();
                    fr.replace(R.id.flContent, new Admin_CourseIconEdit_Frag());
                    fr.commit();
                    //ShowCourseUpdate(courseid);
                });
            }
        }
    }

    private void Show_description(String course_id, String coursename, String price, String imagecourse) {
        String id = String.valueOf(SharedPrefManager.getInstance(mCtx).getID());
        String data = "&courseid=" + course_id + "&userID=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.fetch_course_details + data,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONObject obj = new JSONObject(response);

                        String exp_date, date_subbed;
                        exp_date = obj.getString("exp_date");
                        date_subbed = obj.getString("date_subbed");

                        Show_Dets_Dialog(coursename, price, exp_date, date_subbed, imagecourse);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mCtx, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(mCtx, error.toString(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(mCtx).add(stringRequest);
    }

    private void Show_Dets_Dialog(String coursename, String price, String exp_date, String date_subbed, String imagecourse) {
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
                .load(imagecourse)
                .placeholder(R.mipmap.logo)
                .into(img);

        title.setText(coursename);
        balance.setText("Availabe Balance: K" + price);

        C_price.setText("K" + price + "/Month");
        subText.setText("Expiry Date: " + exp_date + "\nSubscription Date: " + date_subbed);
        submit.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);

        sub_Dialog.show();
    }

    public void Subscribe_Dialog(String imagecourse, String price, String coursename, String course_id, boolean b) {
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
                .load(imagecourse)
                .placeholder(R.mipmap.logo)
                .into(img);

        title.setText(coursename);
        balance.setText("Availabe Balance: K" + course_price);

        if (b) {
            C_price.setText("Unsubcribe");
            subText.setText("Are you sure you want to Unsubscribe from " + coursename + "?\n\n You will not be refunded if you wish to proceed");
            submit.setText("Unsubscribe");
            submit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#882323")));
        } else {
            C_price.setText("K" + price + "/Month");
            subText.setText("Are you sure you want to subscribe to " + coursename + " for K" + price + "?\n\n" +
                    "Subscription will be valid for 30 days");
        }

        submit.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            submit.setVisibility(View.INVISIBLE);

            String userID = String.valueOf(SharedPrefManager.getInstance(mCtx).getID());
            String name = String.valueOf(SharedPrefManager.getInstance(mCtx).getName());
            String email = String.valueOf(SharedPrefManager.getInstance(mCtx).getEmail());

            SubscribeUser(userID, course_id, name, email, b);
        });

        sub_Dialog.show();
    }

    private void SubscribeUser(String userID, String course_id, String name, String email, boolean b) {
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
                params.put("name", String.valueOf(name));
                params.put("email", String.valueOf(email));
                params.put("nrc_num", String.valueOf(userID));
                params.put("state", String.valueOf(b));

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

                        if (!b) {
                            ShowSuccessDialog(Integer.parseInt(course_price), message);
                        }else{
                            FragmentManager manager = ((AppCompatActivity) mCtx).getSupportFragmentManager();
                            FragmentTransaction fr = manager.beginTransaction();
                            fr.replace(R.id.flContent, new Subscription_center_Frag());
                            fr.addToBackStack(null);
                            fr.commit();
                        }

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
        return data_handlerExplorers.size();
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
        TextView coursename, price;
        ImageView imagecourse;
        ImageView action;

        public ProductViewHolder(View itemView) {
            super(itemView);

            if (admin.equals("fetch_Available") || admin.equals("fetch_Subs")) {
                imagecourse = itemView.findViewById(R.id.imageView);
                coursename = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.name);
                action = itemView.findViewById(R.id.imageButton);
                cardView = itemView.findViewById(R.id.cardView4);
            } else {
                cardView = itemView.findViewById(R.id.layout);
                coursename = itemView.findViewById(R.id.coursename);
                imagecourse = itemView.findViewById(R.id.courseimage);
            }
        }
    }
}
