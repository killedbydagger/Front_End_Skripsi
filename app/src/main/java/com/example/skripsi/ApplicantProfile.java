package com.example.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class ApplicantProfile extends AppCompatActivity {
    Button btn_applicantViewFile, btn_applicantCall, btn_applicantEmail;
    ImageView img_close;
    TextView tv_applicantName, tv_applicantDOB, tv_applicantEducation, tv_applicantLocation, tv_applicantDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_profile);

        tv_applicantName = findViewById(R.id.tv_applicantName);
        tv_applicantDOB = findViewById(R.id.tv_applicantDOB);
        tv_applicantEducation = findViewById(R.id.tv_applicantEducation);
        tv_applicantLocation = findViewById(R.id.tv_applicantLocation);
        tv_applicantDesc = findViewById(R.id.tv_applicantDesc);

        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            showApplicantProfile();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showApplicantProfile() throws JSONException {
        String URL = "http://25.54.110.177:8095/User/getUserApplicant";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", getIntent().getExtras().getString("applicantEmail"));
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(0);

                            String applicantName = object.getString("user_first_name") + " " + object.getString("user_last_name");
                            String applicantDOB = object.getString("user_dateOfBirth");
                            String applicantDesc = object.getString("user_description");

                            JSONObject object1 = object.getJSONObject("education");
                            String applicantEducation = object1.getString("education_name");

                            JSONObject object2 = object.getJSONObject("location");
                            String applicantLocation = object2.getString("location_name");

                            tv_applicantName.setText(applicantName);
                            String[] splitDob = applicantDOB.split("\\s+");
                            System.out.println(splitDob[0]);
                            tv_applicantDOB.setText(splitDob[0]);
                            tv_applicantDesc.setText(applicantDesc);
                            tv_applicantEducation.setText(applicantEducation);
                            tv_applicantLocation.setText(applicantLocation);

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
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
