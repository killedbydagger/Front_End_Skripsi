package com.example.skripsi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class BusinessCenter extends AppCompatActivity {
    ViewDialog viewDialog;

    TextView tv_namaPerusahaan, tv_lokasiPerusahaan, tv_ratingPerusahaan, premiumTag;

    ImageView img_Business;

    SessionManager sessionManager;

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    static int PRIVATE_MODE = 0;

    public static final String BUSINESS_ID = "BUSINESS_ID";
    public static final String BUSINESS_IMAGE = "BUSINESS_IMAGE";
    public static final String BUSINESS_NAME = "BUSINESS_NAME";
    public static final String BUSINESS_LOCATION_ID = "BUSINESS_LOCATION_ID";
    public static final String BUSINESS_LOCATION_NAME = "BUSINESS_LOCATION_NAME";
    public static final String BUSINESS_OVERVIEW = "BUSINESS_OVERVIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_center);

        viewDialog = new ViewDialog(BusinessCenter.this);

        viewDialog.showDialog();

        tv_namaPerusahaan = findViewById(R.id.tv_namaPerusahaan);
        tv_lokasiPerusahaan = findViewById(R.id.tv_lokasiPerusahaan);
        tv_ratingPerusahaan = findViewById(R.id.tv_ratingPerusahaan);
        premiumTag = findViewById(R.id.premiumTag);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String userId = user.get(sessionManager.ID);

        HashMap<String, String> business = sessionManager.getBusinessDetail();

        if(business.get(sessionManager.BUSINESS_ID) == null) {
            try {
                sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN",PRIVATE_MODE);
                editor = sharedPreferences.edit();
                checkBisnis(userId);
                viewDialog.hideDialog();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        System.out.println(business.get(sessionManager.BUSINESS_NAME));

        tv_namaPerusahaan.setText(business.get(sessionManager.BUSINESS_NAME));
        tv_lokasiPerusahaan.setText(business.get(sessionManager.BUSINESS_LOCATION_NAME));
        premiumTag.setText(user.get(sessionManager.STATUS));

    }

    private void checkBisnis(String id) throws JSONException {
        Context mContext = BusinessCenter.this;

        String URL = "http://25.54.110.177:8095/Business/checkUserBusiness";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id",id);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Registered")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            String busId = object.getString("bus_id");
                            String busName = object.getString("bus_name");

                            JSONObject object1 = object.getJSONObject("location");
                            String locationId = object1.getString("location_id");
                            String locationName = object1.getString("location_name");

                            String busOverview = object.getString("bus_overview");
                            String busImage = object.getString("bus_image");

                            editor.putString(BUSINESS_ID, busId);
                            editor.putString(BUSINESS_NAME, busName);
                            editor.putString(BUSINESS_LOCATION_ID, locationId);
                            editor.putString(BUSINESS_LOCATION_NAME, locationName);
                            editor.putString(BUSINESS_OVERVIEW, busOverview);
                            editor.putString(BUSINESS_IMAGE, busImage);
                            editor.apply();
                        }

                    }
                    else if(status.equals("Not Registered")) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BusinessCenter.this);
                        alertDialog.setMessage("Your don't have any business registered. Do you want to register a new business ?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent createIntent = new Intent(getApplicationContext(),AddBusiness.class);
                                startActivity(createIntent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                        AlertDialog alert = alertDialog.create();
                        alert.setTitle("Create new business");
                        alert.show();
                        viewDialog.hideDialog();
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

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);

    }
}
