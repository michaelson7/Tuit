package com.nkwazi_tech.tuit_app.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nkwazi_tech.tuit_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Adapter_MyCourse extends RecyclerView.Adapter<Adapter_MyCourse.ProductViewHolder> {

    private Context mCtx;
    //accessing the products added to class
    private List<DataHandler_MyCourse> dataHandler_myCourses;
    private List<DataHandler_MyCourse3> dataHandler_myCourses3;
    String data;

    public Adapter_MyCourse(Context mCtx, List<DataHandler_MyCourse> dataHandler_myCourses, String data) {
        this.mCtx = mCtx;
        this.dataHandler_myCourses = dataHandler_myCourses;
        this.data = data;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views;
        LayoutInflater mInflater = LayoutInflater.from(mCtx);
        if (data.equals("true")){
            views = mInflater.inflate(R.layout.dialog_dashcourse, parent, false);
        }else{
            views = mInflater.inflate(R.layout.adapter_mycourses, parent, false);
        }
        dataHandler_myCourses3 = new ArrayList<>();

        // click listener here
        return new ProductViewHolder(views);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //loading the image
        if (data.equals("true")){
            holder.year.setText(dataHandler_myCourses.get(position).getCourse());
            String course = dataHandler_myCourses.get(position).getCourse();
            course =  course.replaceAll(System.getProperty("line.separator"), "");

            if (course.equals("Nursing")) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_getcourse,
                        response -> {
                            //progressBar.setVisibility(View.GONE);
                            try {
                                //converting the string to json array object
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("Products");

                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = array.getJSONObject(i);

                                    //adding the product to product list class
                                    dataHandler_myCourses3.add(new DataHandler_MyCourse3(
                                            product.getInt("courseid"),
                                            product.getString("coursename"),
                                            ""
                                    ));
                                }
                                Adapter_StudentDialogCourse adapter = new Adapter_StudentDialogCourse(mCtx, dataHandler_myCourses3);
                                holder.corselist.setLayoutManager(new LinearLayoutManager(mCtx));
                                holder.corselist.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mCtx,
                                        "Please check your internet connection",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                //adding our stringrequest to queue
                Volley.newRequestQueue(mCtx).add(stringRequest);
            }
        }else{
            holder.course.setText(dataHandler_myCourses.get(position).getCourse());
        }
    }

    @Override
    public int getItemCount() {
        return dataHandler_myCourses.size();
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
        TextView course;
        TextView year;
        RecyclerView corselist;

        public ProductViewHolder(View itemView) {
            super(itemView);

            if (data.equals("true")){
                year = itemView.findViewById(R.id.textView3);
                corselist = itemView.findViewById(R.id.textView13);
            }else{
                course = itemView.findViewById(R.id.cources);
            }

        }
    }
}
