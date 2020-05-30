package com.example.skripsi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditVacancy extends AppCompatActivity {

    EditText  et_title, et_salary, et_description;

    Spinner sp_category, sp_location, sp_position;

    Button btn_save;

    TextView tv_dueDate;

    private SimpleDateFormat dateFormatter, dateFormatter2;

    Map<String, Boolean> validationChecks = new HashMap<>();

    HashMap<String, Integer> compared_position = new HashMap<>();
    ArrayList<String> positionArray = new ArrayList<>();

    private DatePickerDialog datePickerDialog;
    private String tanggal;

    static int PRIVATE_MODE = 0;
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

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

        sp_position = findViewById(R.id.sp_position);
        tv_dueDate = findViewById(R.id.tv_dueDate);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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

        String[] splitDob = getIntent().getExtras().getString("DUE_DATE").split("\\s+");
        tv_dueDate.setText(splitDob[0]);

        tv_dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                compared_position.clear();
                positionArray.clear();
                sp_position.setAdapter(null);
                if(position == 0){
                    et_salary.setText("");
                    et_salary.setEnabled(false);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card_gray);
                    sp_position.setEnabled(false);
                    sp_position.setBackgroundResource(R.drawable.edit_text_card_gray);
                }
                else if(position == 6){
                    et_salary.setText("0");
                    et_salary.setEnabled(false);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card_gray);
                    sp_position.setEnabled(false);
                    sp_position.setBackgroundResource(R.drawable.edit_text_card_gray);
                }
                else{
                    et_salary.setEnabled(true);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card);
                    sp_position.setEnabled(true);
                    sp_position.setBackgroundResource(R.drawable.edit_text_card);
                    try {
                        loadPositionData(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        et_salary.setText(String.valueOf(getIntent().getExtras().getInt("SALARY")));
//        String compareValue = getIntent().getExtras().getString("POSITION");
//        ArrayAdapter<String> positionArrayAdapter = new ArrayAdapter<String> (getApplicationContext(), android.R.layout.simple_spinner_item, positionArray);
//        positionArrayAdapter.setDropDownViewResource(android.R.layout
//                .simple_spinner_dropdown_item);
//        sp_position.setAdapter(positionArrayAdapter);
//        if (compareValue != null) {
//            int spinnerPosition = positionArrayAdapter.getPosition(compareValue);
//            sp_position.setSelection(spinnerPosition);
//        }

        final HashMap<String, String> business = sessionManager.getBusinessDetail();
        final String businessId = business.get(sessionManager.BUSINESS_ID);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCategory();
                validateTitle();
                validateLocation();
                validateSalary();
                validateDescription();
                validateDate();

                if(!validationChecks.containsValue(false)){
                    int tampung;
                    if(sp_category.getSelectedItemPosition() !=6){
                        tampung = compared_position.get(sp_position.getSelectedItem().toString());
                    }
                    else{
                        tampung = 7;
                    }
                    try {
                        editVacancy(businessId, sp_category.getSelectedItemPosition(), et_title.getText().toString(), et_description.getText().toString(), et_salary.getText().toString(), sp_location.getSelectedItemPosition(), tampung, tanggal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void showDateDialog(){

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                tv_dueDate.setText(dateFormatter.format(newDate.getTime()));
                tanggal = dateFormatter2.format(newDate.getTime());
                validateDate();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void validateDate(){
        if(tv_dueDate.getText().toString().isEmpty()){
            tv_dueDate.setError("Field can't be empty");
            validationChecks.put("Due_Date", false);
        }
        else{
            tv_dueDate.setError(null);
            validationChecks.put("Due_Date", true);
        }
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

    private void loadPositionData(int categoryId) throws JSONException {
        System.out.println(categoryId);
        if(sp_position.getSelectedItemPosition() != 0) {
            String URL = "http://25.54.110.177:8095/CategoryPosition/getCategoryPosition";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("category_id",categoryId);

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        if (status.equals("Success")) {
                            System.out.println("MASUK SUKSES");
                            positionArray.add("--- Choose position ---");
                            JSONArray positionJSON = response.getJSONArray("data");
                            JSONObject object;
                            for (int i=0;i<positionJSON.length();i++){
                                object = positionJSON.getJSONObject(i);
                                JSONObject object1 = object.getJSONObject("position");
                                positionArray.add(object1.getString("position_name"));
                                compared_position.put(object1.getString("position_name"), object1.getInt("position_id"));
                            }
                            ArrayAdapter<String> positionArrayAdapter = new ArrayAdapter<String> (getApplicationContext(), android.R.layout.simple_spinner_item, positionArray);
                            positionArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            sp_position.setAdapter(positionArrayAdapter);
                        }
                        else {
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
            }){
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError {
                    final Map<String,String> params = new HashMap<String, String>();
                    params.put("Context-Type","application/json");
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void editVacancy(String businessId, int categoryId, String title, String description, String salary, int locationId, int positionId, String date) throws JSONException {
        Context mContext = EditVacancy.this;
        String URL = "http://25.54.110.177:8095/Vacancy/editVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("business_id", businessId);
        jsonBody.put("category_id", categoryId);
        jsonBody.put("title", title);
        jsonBody.put("description", description);
        jsonBody.put("salary", salary);
        jsonBody.put("location_id", locationId);
        jsonBody.put("position_id", positionId);
        jsonBody.put("due_date", date);

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
