package com.example.skripsi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class EditVacancy extends AppCompatActivity {

    EditText  et_title, et_salary, et_description;

    Spinner sp_category, sp_location;

    Button btn_save;

    Map<String, Boolean> validationChecks = new HashMap<>();

    static int PRIVATE_MODE = 0;
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public static final String BUSINESS_ID = "BUSINESS_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vacancy);

        et_title = findViewById(R.id.et_title);
        et_salary = findViewById(R.id.et_salary);
        et_description = findViewById(R.id.et_description);
        sp_category = findViewById(R.id.sp_category);
        sp_location = findViewById(R.id.sp_location);
        btn_save = findViewById(R.id.btn_save);

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

        sp_location.setSelection(Integer.parseInt(getIntent().getExtras().getString("LOCATION_ID")));
        sp_category.setSelection(Integer.parseInt(getIntent().getExtras().getString("CATEGORY_ID")));
        et_title.setText(getIntent().getExtras().getString("TITLE"));
        et_description.setText(getIntent().getExtras().getString("DESCRIPTION"));

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 6){
                    et_salary.setText("0");
                    et_salary.setEnabled(false);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card_gray);
                }
                else{
                    et_salary.setText(getIntent().getExtras().getString("SALARY"));
                    et_salary.setEnabled(true);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final HashMap<String, String> business = sessionManager.getBusinessDetail();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCategory();
                validateTitle();
                validateLocation();
                validateSalary();
                validateDescription();

                if(!validationChecks.containsValue(false)){
                    try {
                        editVacancy(business.get(sessionManager.BUSINESS_ID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void validateCategory(){
        if(sp_category.getSelectedItemPosition() == 0){
            ((TextView) sp_category.getSelectedView()).setError("Please choose your category");
            validationChecks.put("Category", false);
        }
        else{
            ((TextView) sp_category.getSelectedView()).setError(null);
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
        if(et_salary.getText().toString().isEmpty()){
            et_salary.setError("Field can't be empty");
            validationChecks.put("Salary", false);
        }
        else{
            validationChecks.put("Salary", true);
        }
    }

    private void validateDescription(){
        if(et_description.getText().toString().isEmpty()){
            et_description.setError("Field can't be empty");
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
        sp_category.setAdapter(locationArrayAdapter);
    }

    private void editVacancy(String businessId) throws JSONException {
        Context mContext = EditVacancy.this;
        String URL = "http://25.54.110.177:8095/Vacancy/editVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("business_id", businessId);
        jsonBody.put("vac_id", getIntent().getExtras().getString("VACANCY_ID"));
        jsonBody.put("category_id", sp_category.getSelectedItemPosition());
        jsonBody.put("title", et_title.getText().toString());
        jsonBody.put("description", et_description.getText().toString());
        jsonBody.put("salary", et_salary.getText().toString());
        jsonBody.put("location_id", sp_location.getSelectedItemPosition());

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Edit vacancy success", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                         Toast.makeText(getApplicationContext(), "Edit vacancy failed", Toast.LENGTH_LONG).show();
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
