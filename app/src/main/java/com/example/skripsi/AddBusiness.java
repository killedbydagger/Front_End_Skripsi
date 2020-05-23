package com.example.skripsi;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
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

public class AddBusiness extends AppCompatActivity {

    SessionManager sessionManager;
    private RequestQueue requestQueue;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String userId = user.get(sessionManager.ID);

    }

    private void createBisnis(String id, String imageURL, String namaBisnis, String locationId, String overview) throws JSONException {
        Context mContext = AddBusiness.this;
        String URL = "http://25.54.110.177:8095/Business/createNewBusiness";
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("user_id", id);
        jsonBody.put("business_image", imageURL);
        jsonBody.put("business_name", namaBisnis);
        jsonBody.put("location_id", locationId);
        jsonBody.put("business_overview", overview);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if(status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "New business has been created", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();

                    }else {
                        Toast.makeText(getApplicationContext(), "Failed to create new business", Toast.LENGTH_LONG).show();
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
            public Map<String,String> getHeaders() throws AuthFailureError {
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","applicatiom/json");
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
