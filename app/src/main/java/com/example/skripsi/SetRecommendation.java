package com.example.skripsi;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class SetRecommendation extends AppCompatActivity {

    SessionManager sessionManager;

    Spinner sp_lokasi, sp_kategori1, sp_kategori2, sp_kategori3;
    Button btn_done;

    Map<String, Boolean> validationChecks = new HashMap<>();

    ImageView img_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_recommendation);

        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp_lokasi = findViewById(R.id.sp_lokasi);
        sp_kategori1 = findViewById(R.id.sp_kategori1);
        sp_kategori2 = findViewById(R.id.sp_kategori2);
        sp_kategori3 = findViewById(R.id.sp_kategori3);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);
        try {
            setCategorySpinner(user.get(sessionManager.CATEGORY_DATA));
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLocation();
                validateCategory();
                if (!validationChecks.containsValue(false)) {
                    String kategori = sp_kategori1.getSelectedItemPosition() + ", " + sp_kategori2.getSelectedItemPosition() + ", " + sp_kategori3.getSelectedItemPosition();
                    try {
                        setRecommendation(Integer.parseInt(userId), sp_lokasi.getSelectedItemPosition(), kategori);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void validateLocation() {
        if (sp_lokasi.getSelectedItemPosition() == 0) {
            ((TextView) sp_lokasi.getSelectedView()).setError("Please choose your location");
            validationChecks.put("Location", false);
        } else {
            ((TextView) sp_lokasi.getSelectedView()).setError(null);
            validationChecks.put("Location", true);
        }
    }

    private void validateCategory() {
        if (sp_kategori1.getSelectedItemPosition() == 0 && sp_kategori2.getSelectedItemPosition() == 0 && sp_kategori3.getSelectedItemPosition() == 0) {
            ((TextView) sp_kategori1.getSelectedView()).setError("Please choose your location");
            validationChecks.put("Category", false);
        } else {
            ((TextView) sp_kategori1.getSelectedView()).setError(null);
            validationChecks.put("Category", true);
        }
    }

    private void setCategorySpinner(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray kategoriJson = jsonObject.getJSONArray("data");
        JSONObject object;

        ArrayList<String> kategoriArray = new ArrayList<>();
        kategoriArray.add("--- Choose Category ---");
        for (int i = 0; i < kategoriJson.length(); i++) {
            object = kategoriJson.getJSONObject(i);
            kategoriArray.add(object.getString("category_name"));
        }

        ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategoriArray);
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_kategori1.setAdapter(kategoriAdapter);
        sp_kategori2.setAdapter(kategoriAdapter);
        sp_kategori3.setAdapter(kategoriAdapter);
    }

    private void setLocationSpinner(String json) throws JSONException {
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
        sp_lokasi.setAdapter(locationArrayAdapter);
    }

    private void setRecommendation(int userid, int location, String category) throws JSONException {
        Context mContext = SetRecommendation.this;
        String URL = "http://25.56.11.101:8095/Recommendation/setUserRecommendation";
        //String URL = "https://springjava.azurewebsites.net/Recommendation/setUserRecommendation";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userid);
        jsonBody.put("location_id", location);
        jsonBody.put("categories", category);

        System.out.println(jsonBody);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    System.out.println(status);
                    if (status.equals("Success Insert")) {
                        Toast.makeText(getApplicationContext(), "Success to set recommendation", Toast.LENGTH_LONG).show();
                        finish();
                    } else if (status.equals("Success Update")) {
                        Toast.makeText(getApplicationContext(), "Success to update recommendation", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to set recommendation", Toast.LENGTH_LONG).show();
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
