package com.example.skripsi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import java.util.Map;

import androidx.annotation.Nullable;

public class PopUpRating extends Activity {

    SessionManager sessionManager;

    String vacancyId, businessId;

    RatingBar rb_rateCompany;
    Button btn_rate;
    EditText et_komenRating;

    ViewDialog viewDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popuprating);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        viewDialog = new ViewDialog(PopUpRating.this);
        //viewDialog.showDialog();

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        businessId = getIntent().getExtras().getString("BUSINESS_ID");
        vacancyId = getIntent().getExtras().getString("VACANCY_ID");

        rb_rateCompany = findViewById(R.id.rb_rateCompany);
        et_komenRating = findViewById(R.id.et_komenRating);
        btn_rate = findViewById(R.id.btn_rate);
        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDialog.showDialog();
                try {
                    System.out.println("id bisnis: " + businessId);
                    System.out.println("id vac: " + vacancyId);
                    System.out.println("id user: " + userId);
                    System.out.println("reting: " + rb_rateCompany.getRating());
                    System.out.println("komen: " + et_komenRating.getText());
                    giveRating(Integer.parseInt(businessId), Integer.parseInt(vacancyId), Integer.parseInt(userId), (int) rb_rateCompany.getRating(), et_komenRating.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getWindow().setLayout((int)(width*.8), (int)(height*.5));
    }

    private void giveRating(int businessId, int vacId, int userid, int rate, Editable comment) throws JSONException {
        Context mContext = PopUpRating.this;
        String URL = "https://springjava.azurewebsites.net/BusinessRating/applyBusinessRating";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("business_id", businessId);
        jsonBody.put("vac_id", vacId);
        jsonBody.put("user_id", userid);
        jsonBody.put("rate", rate);
        jsonBody.put("comment", comment);

        System.out.println(jsonBody);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Success to give rating", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to give rating", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
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
                params.put("Context-Type", "applicatiom/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
