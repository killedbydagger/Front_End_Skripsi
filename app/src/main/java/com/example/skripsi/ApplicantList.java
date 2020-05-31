package com.example.skripsi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicantList extends AppCompatActivity {

    ImageView img_close;

    private RecyclerView rv_applicantList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Applicant> applicantLists;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_list);

        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView close = findViewById(R.id.img_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String jobTitle = intent.getStringExtra("TITLE");

        TextView textView = findViewById(R.id.tv_jobTitle);
        textView.setText(jobTitle);

        rv_applicantList = findViewById(R.id.rv_applicantList);

        applicantLists = new ArrayList<>();
        adapter = new ApplicantAdapter(getApplicationContext(), applicantLists);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(rv_applicantList.getContext(), linearLayoutManager.getOrientation());

        rv_applicantList.setHasFixedSize(true);
        rv_applicantList.setLayoutManager(linearLayoutManager);
        rv_applicantList.addItemDecoration(dividerItemDecoration);
        rv_applicantList.setAdapter(adapter);

        try {
            loadListApplicant();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadListApplicant() throws JSONException {
        String URL = "http://25.54.110.177:8095/VacancyApplicant/getVacancyApplicant";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("vac_id", getIntent().getExtras().getString("VACANCY_ID"));
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    applicantLists.clear();
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Applicant applicant = new Applicant();

                            JSONObject object1 = object.getJSONObject("user");
                            applicant.setName(object1.getString("user_first_name") + " " + object1.getString("user_last_name"));
                            applicant.setEmail(object1.getString("user_email"));
                            applicant.setApplicant_id(object1.getString("user_id"));

                            JSONObject object2 = object.getJSONObject("vacancy");
                            applicant.setVac_id(object2.getString("vac_id"));

                            applicant.setStatusName(object.getString("status"));
                            applicantLists.add(applicant);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        // Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}
