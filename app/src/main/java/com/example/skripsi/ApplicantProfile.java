package com.example.skripsi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ApplicantProfile extends AppCompatActivity {
    private static final int REQUEST_CALL = 1;

    Button btn_applicantViewFile, btn_applicantCall, btn_applicantEmail;
    ImageView img_close;
    TextView tv_applicantName, tv_applicantDOB, tv_applicantEducation, tv_applicantLocation, tv_applicantDesc, tv_userPhoneNum, tv_userEmailAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_profile);

        tv_applicantName = findViewById(R.id.tv_applicantName);
        tv_applicantDOB = findViewById(R.id.tv_applicantDOB);
        tv_applicantEducation = findViewById(R.id.tv_applicantEducation);
        tv_applicantLocation = findViewById(R.id.tv_applicantLocation);
        tv_applicantDesc = findViewById(R.id.tv_applicantDesc);
        tv_userPhoneNum = findViewById(R.id.tv_userPhoneNum);
        tv_userEmailAdd = findViewById(R.id.tv_userEmailAdd);

        btn_applicantCall = findViewById(R.id.btn_applicantCall);
        btn_applicantCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        btn_applicantEmail = findViewById(R.id.btn_applicantEmail);
        btn_applicantEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

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

    private void makePhoneCall() {
        String number = (String) tv_userPhoneNum.getText();
        if (ContextCompat.checkSelfPermission(ApplicantProfile.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ApplicantProfile.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }

    private void sendMail() {
        String email = (String) tv_userEmailAdd.getText();
        String[] recipients = email.split(",");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
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
                            String applicantPhone = object.getString("user_phone");
                            String applicantEmail = object.getString("user_email");

                            JSONObject object1 = object.getJSONObject("education");
                            String applicantEducation = object1.getString("education_name");

                            JSONObject object2 = object.getJSONObject("location");
                            String applicantLocation = object2.getString("location_name");

                            tv_applicantName.setText(applicantName);

                            String date = applicantDOB;
                            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                            Date oneWayTripDate = null;
                            try {
                                oneWayTripDate = input.parse(date);
                                tv_applicantDOB.setText(output.format(oneWayTripDate));
                                System.out.println("ini tanggalnya: "+ oneWayTripDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            tv_applicantDesc.setText(applicantDesc);
                            tv_applicantEducation.setText(applicantEducation);
                            tv_applicantLocation.setText(applicantLocation);
                            tv_userPhoneNum.setText(applicantPhone);
                            tv_userEmailAdd.setText(applicantEmail);

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
