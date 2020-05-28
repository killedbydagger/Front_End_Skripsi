package com.example.skripsi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VacancyAdapter extends RecyclerView.Adapter<VacancyAdapter.ViewHolder>{

    private Context context;
    private List<Vacancy> list;

    SessionManager sessionManager;

    public VacancyAdapter(Context context, List<Vacancy> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.vacancy_item, viewGroup ,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Vacancy vacancy = list.get(position);

        viewHolder.textTitle.setText(vacancy.getTitle());
        viewHolder.textCategory.setText(vacancy.getCategory());
        viewHolder.textLocation.setText(vacancy.getLocationName());
        viewHolder.textSalary.setText(vacancy.getSalary());

        sessionManager = new SessionManager(context);
        final HashMap<String, String> business = sessionManager.getBusinessDetail();

        viewHolder.layout_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), ApplicantList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TITLE",vacancy.getTitle());
                intent.putExtra("VACANCY_ID", vacancy.getId());
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.imgEdit.setImageResource(R.drawable.icon_edit);
        viewHolder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), EditVacancy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("VACANCY_ID", vacancy.getId());
                intent.putExtra("LOCATION_ID",vacancy.getLocationId());
                intent.putExtra("CATEGORY_ID",vacancy.getCategoryId());
                intent.putExtra("TITLE",vacancy.getTitle());
                intent.putExtra("SALARY",vacancy.getSalary());
                intent.putExtra("DESCRIPTION",vacancy.getDescription());
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.imgDelete.setImageResource(R.drawable.icon_dusbin);
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());

                alertDialog.setMessage("Are you sure to delete this vacancy?").setCancelable(false);

                alertDialog.setPositiveButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    deleteVacancy(v.getContext(), business.get(sessionManager.BUSINESS_ID), vacancy.getId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                AlertDialog alert = alertDialog.create();
                alert.setTitle("Create new business");
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle, textCategory, textSalary, textLocation;
        public ImageView imgEdit,imgDelete;
        public LinearLayout layout_data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.tv_title);
            textCategory = itemView.findViewById(R.id.tv_category);
            textSalary = itemView.findViewById(R.id.tv_salary);
            textLocation = itemView.findViewById(R.id.tv_location);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            layout_data = itemView.findViewById(R.id.layout_data);
        }
    }

    private void deleteVacancy(final Context mContext , String id, String vacId) throws JSONException {
        String URL = "http://25.54.110.177:8095/Vacancy/deleteVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("business_id", id);
        jsonBody.put("vac_id", vacId);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(mContext, "Delete vacancy success", Toast.LENGTH_LONG).show();

                    }
                    else {
                        Toast.makeText(mContext, "Delete vacancy failed", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
