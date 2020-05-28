package com.example.skripsi;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

    private String url = "http://25.54.110.177:8095/VacancyApplicant/getVacancyApplicant";

    private RecyclerView rv_applicantList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<ListApplicant> applicantLists;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_list);

        rv_applicantList = findViewById(R.id.rv_applicantList);

        applicantLists = new ArrayList<>();
        adapter = new ListApplicantAdapter(getApplicationContext(), applicantLists);
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

                        for(int i = 0;i<jsonArray.length();i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ListApplicant listApplicant = new ListApplicant();

                            JSONObject object1 = object.getJSONObject("user");
                            listApplicant.setName(object1.getString("user_first_name") + " " + object1.getString("user_last_name"));
                            listApplicant.setEmail(object1.getString("user_email"));

                            applicantLists.add(listApplicant);
                        }

                        adapter.notifyDataSetChanged();
                    }
                    else {
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
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
