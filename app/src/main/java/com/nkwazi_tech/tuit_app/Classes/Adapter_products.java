package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alespero.expandablecardview.ExpandableCardView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nkwazi_tech.tuit_app.Activities.programs_activity;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Adapter_products extends RecyclerView.Adapter<Adapter_products.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    List<DataHandler_programs> dataHandler_programs;
    String data;
    public static Dialog Cources;

    private List<DataHandler_MyCourse3> dataHandler_myCourses3;

    public Adapter_products(Context mCtx, List<DataHandler_programs> dataHandler_programs, String data) {
        this.mCtx = mCtx;
        this.dataHandler_programs = dataHandler_programs;
        this.data = data;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        views = mInflater.inflate(R.layout.adapter_products, parent, false);
        Cources = new Dialog(mCtx);
        dataHandler_myCourses3 = new ArrayList<>();
        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        DataHandler_programs p = dataHandler_programs.get(position);
        ProductViewHolder h = holder;

        h.cardView.setOnClickListener(v -> {
            ShowCourseDialoge(String.valueOf(p.getProg_id()),p.getProg_name());
        });

        Glide.with(mCtx)
                .load(p.getImg())
                .placeholder(R.mipmap.logo)
                .error(R.mipmap.logo)
                .into(h.groupimg);
        h.title.setText(p.getProg_name());
        h.expand.setOnClickListener(v -> {

            if (h.recyclerView.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(h.cardView, new AutoTransition());
                h.recyclerView.setVisibility(View.VISIBLE);
                holder.expand.setBackgroundResource(R.drawable.ic_up);
            } else {
                TransitionManager.beginDelayedTransition(h.cardView, new AutoTransition());
                h.recyclerView.setVisibility(View.GONE);
                holder.expand.setBackgroundResource(R.drawable.ic_down);
            }
        });
    }

    private void ShowCourseDialoge(String course, String prog_name) {
        String get = "&id=" + course;
        dataHandler_myCourses3.clear();
        TextView year;
        RecyclerView corselist;

        Cources.setContentView(R.layout.dialog_dashcourse);
        Objects.requireNonNull(Cources.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = Cources.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        year = Cources.findViewById(R.id.textView3);
        corselist = Cources.findViewById(R.id.textView13);

        year.setText(prog_name);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.fetch_program_course + get,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.GONE);
                        try {
                            //converting the string to json array object
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("results");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list class
                                dataHandler_myCourses3.add(new DataHandler_MyCourse3(
                                        product.getInt("course_id"),
                                        product.getString("course_name"),
                                        ""
                                ));
                            }
                            Adapter_StudentDialogCourse adapter = new Adapter_StudentDialogCourse(mCtx, dataHandler_myCourses3);
                            corselist.setLayoutManager(new LinearLayoutManager(mCtx));
                            corselist.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(mCtx).add(stringRequest);

        Cources.show();
    }

    @Override
    public int getItemCount() {
        return dataHandler_programs.size();
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
        ImageButton expand;
        RecyclerView recyclerView;
        TextView title;
        CardView cardView;
        ImageView groupimg;

        public ProductViewHolder(View itemView) {
            super(itemView);
            groupimg = itemView.findViewById(R.id.groupimg);
            title = itemView.findViewById(R.id.textView24);
            recyclerView = itemView.findViewById(R.id.recycler);
            expand = itemView.findViewById(R.id.imageButton5);
            cardView = itemView.findViewById(R.id.cardgroup);
        }
    }
}
