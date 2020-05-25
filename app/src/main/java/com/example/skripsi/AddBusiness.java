package com.example.skripsi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class AddBusiness extends AppCompatActivity {

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private RequestQueue requestQueue;
    ViewDialog viewDialog;

    static int PRIVATE_MODE = 0;

    EditText et_businessName, et_businessOverview;

    Spinner sp_location;

    Button btn_save;

    Map<String, Boolean> validationChecks = new HashMap<>();

    public static final String LOCATION_DATA = "LOCATION_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);

        et_businessName = findViewById(R.id.et_businessName);
        sp_location = findViewById(R.id.sp_location);
        et_businessOverview = findViewById(R.id.et_businessOverview);
        btn_save = findViewById(R.id.btn_save);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN",PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBusinessName();
                validateLocation();
                validateBusinessOverview();

                if(!validationChecks.containsValue(false)){
                    int locationId = sp_location.getSelectedItemPosition();
                    try {
                        createBisnis(userId,"URL",et_businessName.getText().toString(), locationId,et_businessOverview.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void validateBusinessName(){
        if(et_businessName.getText().toString().isEmpty()){
            et_businessName.setError("Field can't be empty");
            validationChecks.put("BusinessName", false);
        }
        else{
            validationChecks.put("BusinessName", true);
        }
    }

    private void validateBusinessOverview(){
        if(et_businessOverview.getText().toString().isEmpty()){
            et_businessOverview.setError("Field can't be empty");
            validationChecks.put("BusinessOverview", false);
        }
        else{
            validationChecks.put("BusinessOverview", true);
        }
    }

    private void validateLocation(){
        if(sp_location.getSelectedItemPosition() == 0){
            ((TextView) sp_location.getSelectedView()).setError("Please choose your location");
            validationChecks.put("Location", false);
        }
        else{
            ((TextView) sp_location.getSelectedView()).setError(null);
            validationChecks.put("Location", true);
        }
    }

    private void setLocationSpinner(String json) throws JSONException {
        ArrayList<String> locationArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray locationJSON = jsonObject.getJSONArray("data");
        JSONObject object;
        locationArray.add("--- Choose Location ---");
        for (int i=0;i<locationJSON.length();i++){
            object = locationJSON.getJSONObject(i);
            locationArray.add(object.getString("location_name"));
        }
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, locationArray);
        locationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_location.setAdapter(locationArrayAdapter);
    }

    private void createBisnis(String id, String imageURL, String namaBisnis, int locationId, String overview) throws JSONException {
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
