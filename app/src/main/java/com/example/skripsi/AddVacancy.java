package com.example.skripsi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.bumptech.glide.load.engine.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddVacancy extends AppCompatActivity {

    private RequestQueue requestQueue;

    EditText et_title, et_alamat, et_gaji, et_deskripsi;

    Spinner sp_kategori, sp_location;

    Button btn_add;

    Map<String, Boolean> validationChecks = new HashMap<>();

    static int PRIVATE_MODE = 0;
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacancy);

        sp_kategori = findViewById(R.id.sp_kategori);
        sp_location = findViewById(R.id.sp_location);

        et_title = findViewById(R.id.et_title);
        et_alamat = findViewById(R.id.et_alamat);
        et_gaji = findViewById(R.id.et_gaji);
        et_deskripsi = findViewById(R.id.et_deskripsi);
        btn_add = findViewById(R.id.btn_add);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();

        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN",PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setCategorySpinner(user.get(sessionManager.CATEGORY_DATA));
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sp_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 6){
                    et_gaji.setText("0");
                    et_gaji.setEnabled(false);
                    et_gaji.setBackgroundResource(R.drawable.edit_text_card_gray);
                }
                else{
                    et_gaji.setText("");
                    et_gaji.setEnabled(true);
                    et_gaji.setBackgroundResource(R.drawable.edit_text_card);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        HashMap<String, String> business = sessionManager.getBusinessDetail();
        final String businessId = business.get(sessionManager.BUSINESS_ID);
        final String businessLocationId = business.get(sessionManager.BUSINESS_LOCATION_ID);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCategory();
                validateTitle();
                validateLocation();
                validateSalary();
                validateDescription();

                if(!validationChecks.containsValue(false)){
                    try {
                        add(businessId, sp_kategori.getSelectedItemPosition(), et_title.getText().toString(), et_deskripsi.getText().toString(), et_gaji.getText().toString(), sp_location.getSelectedItemPosition());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void validateCategory(){
        if(sp_kategori.getSelectedItemPosition() == 0){
            ((TextView) sp_kategori.getSelectedView()).setError("Please choose your category");
            validationChecks.put("Category", false);
        }
        else{
            ((TextView) sp_kategori.getSelectedView()).setError(null);
            validationChecks.put("Category", true);
        }
    }

    private void validateTitle(){
        if(et_title.getText().toString().isEmpty()){
            et_title.setError("Field can't be empty");
            validationChecks.put("Title", false);
        }
        else{
            validationChecks.put("Title", true);
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
    private void validateSalary(){
        if(et_gaji.getText().toString().isEmpty()){
            et_gaji.setError("Field can't be empty");
            validationChecks.put("Salary", false);
        }
        else{
            validationChecks.put("Salary", true);
        }
    }

    private void validateDescription(){
        if(et_deskripsi.getText().toString().isEmpty()){
            et_deskripsi.setError("Field can't be empty");
            validationChecks.put("Description", false);
        }
        else{
            validationChecks.put("Description", true);
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

    private void setCategorySpinner(String json) throws JSONException {
        ArrayList<String> locationArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray locationJSON = jsonObject.getJSONArray("data");
        JSONObject object;
        locationArray.add("--- Choose category ---");
        for (int i=0;i<locationJSON.length();i++){
            object = locationJSON.getJSONObject(i);
            locationArray.add(object.getString("category_name"));
        }
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, locationArray);
        locationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_kategori.setAdapter(locationArrayAdapter);
    }

    private void add(String businessId, int categoryId, String title, String description, String salary, int locationId) throws JSONException {
        Context mContext = AddVacancy.this;
        String URL = "http://25.54.110.177:8095/Vacancy/addNewVacancy";
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("business_id", businessId);
        jsonBody.put("category_id", categoryId);
        jsonBody.put("title", title);
        jsonBody.put("description", description);
        jsonBody.put("salary", salary);
        jsonBody.put("location_id", locationId);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if(status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Succes to add New Vacancy", Toast.LENGTH_LONG).show();
                        finish();

                    }else {
                        Toast.makeText(getApplicationContext(), "Failed to add New Vacancy", Toast.LENGTH_LONG).show();
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
