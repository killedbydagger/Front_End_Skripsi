package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ViewHolder> {

    private Context context;
    private List<Applicant> list;

    SessionManager sessionManager;

    public ApplicantAdapter(Context context, List<Applicant> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.applicantlist_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final Applicant applicant = list.get(i);

        sessionManager = new SessionManager(context);
        final HashMap<String, String> user = sessionManager.getUserDetail();
        final HashMap<String, String> business = sessionManager.getBusinessDetail();

        viewHolder.textName.setText(applicant.getName());
        viewHolder.textEmail.setText(applicant.getEmail());

        viewHolder.applicantData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ApplicantProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("applicantEmail", applicant.getEmail());
                view.getContext().startActivity(intent);
            }
        });

        if (applicant.getStatusName().equals("ACCEPTED")) {
            viewHolder.btn_accepted.setVisibility(View.GONE);
            viewHolder.btn_rejected.setVisibility(View.GONE);
            viewHolder.applicantStatus.setVisibility(View.VISIBLE);
            viewHolder.applicantStatus.setText("ACCEPTED");
            viewHolder.applicantStatus.setTextColor(ContextCompat.getColor(context, R.color.greenA700));
        } else if (applicant.getStatusName().equals("REJECTED")) {
            viewHolder.btn_accepted.setVisibility(View.GONE);
            viewHolder.btn_rejected.setVisibility(View.GONE);
            viewHolder.applicantStatus.setVisibility(View.VISIBLE);
            viewHolder.applicantStatus.setText("REJECTED");
            viewHolder.applicantStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGrapeFruitDark));
        } else {
            viewHolder.applicantStatus.setVisibility(View.GONE);
            viewHolder.btn_accepted.setVisibility(View.VISIBLE);
            viewHolder.btn_rejected.setVisibility(View.VISIBLE);
        }

        viewHolder.btn_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    respondApplicant(view.getContext(), applicant.getApplicant_id(), applicant.getVac_id(), business.get(sessionManager.BUSINESS_ID), "ACCEPTED", i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viewHolder.btn_accepted.setVisibility(View.GONE);
                viewHolder.btn_rejected.setVisibility(View.GONE);
                viewHolder.applicantStatus.setVisibility(View.VISIBLE);
                viewHolder.applicantStatus.setText("ACCEPTED");
                viewHolder.applicantStatus.setTextColor(ContextCompat.getColor(context, R.color.greenA700));
            }
        });

        viewHolder.btn_rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    respondApplicant(view.getContext(), applicant.getApplicant_id(), applicant.getVac_id(), business.get(sessionManager.BUSINESS_ID), "REJECTED", i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viewHolder.btn_accepted.setVisibility(View.GONE);
                viewHolder.btn_rejected.setVisibility(View.GONE);
                viewHolder.applicantStatus.setVisibility(View.VISIBLE);
                viewHolder.applicantStatus.setText("REJECTED");
                viewHolder.applicantStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGrapeFruitDark));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textName, textEmail, applicantStatus;
        public Button btn_accepted, btn_rejected;
        public LinearLayout applicantData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.tv_applicantName);
            textEmail = itemView.findViewById(R.id.tv_applicantPhone);
            applicantStatus = itemView.findViewById(R.id.tv_applicantStatus);
            applicantData = itemView.findViewById(R.id.applicantData);

            btn_accepted = itemView.findViewById(R.id.btn_accept);
            btn_rejected = itemView.findViewById(R.id.btn_reject);
        }
    }

    private void respondApplicant(final Context mContext, String userId, String vacId, String businessId, String respond, final int index) throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/VacancyApplicant/respondVacancyApplicant";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
        jsonBody.put("vac_id", vacId);
        jsonBody.put("business_id", businessId);
        jsonBody.put("respond", respond);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(mContext, "Success to respond", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "Failed to respond", Toast.LENGTH_LONG).show();
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
