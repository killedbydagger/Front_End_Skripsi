package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class EditBusiness extends AppCompatActivity {

    SessionManager sessionManager;
    public SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    static int PRIVATE_MODE = 0;

    EditText et_businessName, et_businessOverview;

    Spinner sp_location;

    Button btn_save;

    Map<String, Boolean> validationChecks = new HashMap<>();

    public static final String BUSINESS_ID = "BUSINESS_ID";
    public static final String BUSINESS_IMAGE = "BUSINESS_IMAGE";
    public static final String BUSINESS_NAME = "BUSINESS_NAME";
    public static final String BUSINESS_LOCATION_ID = "BUSINESS_LOCATION_ID";
    public static final String BUSINESS_LOCATION_NAME = "BUSINESS_LOCATION_NAME";
    public static final String BUSINESS_OVERVIEW = "BUSINESS_OVERVIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        et_businessName = findViewById(R.id.et_businessName);
        et_businessOverview = findViewById(R.id.et_businessOverview);
        sp_location = findViewById(R.id.sp_location);


        sessionManager = new SessionManager(this);
        HashMap<String, String> business = sessionManager.getBusinessDetail();
        final String business_id = business.get(sessionManager.BUSINESS_ID);
        String businessName = business.get(sessionManager.BUSINESS_NAME);
        String businessOverview = business.get(sessionManager.BUSINESS_OVERVIEW);

        et_businessName.setText(businessName);
        et_businessOverview.setText(businessOverview);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN", PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA), business.get(sessionManager.BUSINESS_LOCATION_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBusinessName();
                validateLocation();
                validateBusinessOverview();
                if (!validationChecks.containsValue(false)) {
                    try {
                        editProfile(userId, business_id, "IMG_URL", et_businessName.getText().toString(), sp_location.getSelectedItemPosition(), et_businessOverview.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void validateBusinessName() {
        if (et_businessName.getText().toString().isEmpty()) {
            et_businessName.setError("Field can't be empty");
            validationChecks.put("BusinessName", false);
        } else {
            validationChecks.put("BusinessName", true);
        }
    }

    private void validateBusinessOverview() {
        if (et_businessOverview.getText().toString().isEmpty()) {
            et_businessOverview.setError("Field can't be empty");
            validationChecks.put("BusinessOverview", false);
        } else {
            validationChecks.put("BusinessOverview", true);
        }
    }

    private void validateLocation() {
        if (sp_location.getSelectedItemPosition() == 0) {
            ((TextView) sp_location.getSelectedView()).setError("Please choose your location");
            validationChecks.put("Location", false);
        } else {
            ((TextView) sp_location.getSelectedView()).setError(null);
            validationChecks.put("Location", true);
        }
    }

    private void setLocationSpinner(String json, String id) throws JSONException {
        ArrayList<String> locationArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray locationJSON = jsonObject.getJSONArray("data");
        JSONObject object;
        locationArray.add("--- Choose Location ---");
        for (int i = 0; i < locationJSON.length(); i++) {
            object = locationJSON.getJSONObject(i);
            locationArray.add(object.getString("location_name"));
        }
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationArray);
        locationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_location.setAdapter(locationArrayAdapter);
        sp_location.setSelection(Integer.parseInt(id));
    }

    private void editProfile(String userId, String businessId, String imgURL, String businessName, int locationId, String businessOverview) throws JSONException {
        Context mContext = EditBusiness.this;
        String URL = "http://25.54.110.177:8095/Business/editUserBusiness";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
        jsonBody.put("business_id", businessId);
        jsonBody.put("business_image", imgURL);
        jsonBody.put("business_name", businessName);
        jsonBody.put("location_id", locationId);
        jsonBody.put("business_overview", businessOverview);


        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String business_Name = object.getString("bus_name");

                            JSONObject object1 = object.getJSONObject("location");
                            String location_Id = object1.getString("location_id");
                            String location_Name = object1.getString("location_name");

                            System.out.println(business_Name + " " + location_Id + " " + location_Name);

                            editor.putString(BUSINESS_NAME, business_Name);
                            editor.putString(BUSINESS_LOCATION_ID, location_Id);
                            editor.putString(BUSINESS_LOCATION_NAME, location_Name);
                            editor.apply();

                            Toast.makeText(getApplicationContext(), "Edit business success", Toast.LENGTH_LONG).show();
//                            Intent businessIntent = new Intent(getApplicationContext(),BusinessCenter.class);
//                            startActivity(businessIntent);
                            finish();

                        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
