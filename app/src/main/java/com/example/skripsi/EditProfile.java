package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class EditProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText et_firstName, et_lastName,et_description,et_phoneNumber;
    TextView tv_DOB, tv_email;
    Spinner sp_lastEducation,sp_location;
    Button btn_save;
    ImageView img_slideMenuProfile;

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    static int PRIVATE_MODE = 0;
    public static final String LOCATION_DATA = "LOCATION_DATA";
    public static final String EDUCATION_DATA = "EDUCATION_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_description = findViewById(R.id.et_description);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);

        tv_DOB = findViewById(R.id.tv_DOB);
        tv_email = findViewById(R.id.tv_email);

        sp_lastEducation = findViewById(R.id.sp_lastEducation);
        sp_location = findViewById(R.id.sp_location);

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //masih dites
        img_slideMenuProfile = (ImageView) findViewById(R.id.img_slideMenuProfile);
        img_slideMenuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mFirstName = user.get(sessionManager.FIRST_NAME);
        String mLastName = user.get(sessionManager.LAST_NAME);
        String mDescription = user.get(sessionManager.DESCRIPTION);
        String mPhone = user.get(sessionManager.PHONE);
        String mDob = user.get(sessionManager.DOB);
        String mEmail = user.get(sessionManager.EMAIL);

        et_firstName.setText(mFirstName);
        et_lastName.setText(mLastName);
        et_description.setText(mDescription);
        et_phoneNumber.setText(mPhone);

        tv_DOB.setText(mDob);
        tv_email.setText(mEmail);


//        if(user.get(sessionManager.LOCATION_DATA) == null || user.get(sessionManager.EDUCATION_DATA) == null) {
//            try {
//                sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN",PRIVATE_MODE);
//                editor = sharedPreferences.edit();
//                loadEducationData();
//                loadLocationData();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        try {
                sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN",PRIVATE_MODE);
                editor = sharedPreferences.edit();
                loadEducationData();
                loadLocationData();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        System.out.println("asdasd" + user.get(sessionManager.LOCATION_DATA));
        System.out.println("qwewqewq" + sessionManager.LOCATION_DATA);


        try {
            setEducationSpinner(user.get(sessionManager.EDUCATION_DATA), user.get(sessionManager.EDUCATION_ID));
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA),user.get(sessionManager.LOCATION_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menuBusinessCenter:
                Intent businessCenterIntent = new Intent(getApplicationContext(),BusinessCenter.class);
                startActivity(businessCenterIntent);
                break;
        }

        return true;
    }

    private void setEducationSpinner(String json, String id) throws JSONException {
        ArrayList<String> educationArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray educationJSON = jsonObject.getJSONArray("data");
        JSONObject object;
        educationArray.add("--- Choose Location ---");
        for (int i=0;i<educationJSON.length();i++){
            object = educationJSON.getJSONObject(i);
            educationArray.add(object.getString("education_name"));
        }
        ArrayAdapter<String> educationArrayAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, educationArray);
        educationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_lastEducation.setAdapter(educationArrayAdapter);
        sp_lastEducation.setSelection(Integer.parseInt(id));
    }

    private void setLocationSpinner(String json, String id) throws JSONException {
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
        sp_location.setSelection(Integer.parseInt(id));
    }

    private void loadLocationData() throws JSONException {
        String URL = "http://25.54.110.177:8095/Location/getAllLocation";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_email",sessionManager.EMAIL);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        System.out.println(response.toString());
                        editor.putString(LOCATION_DATA, response.toString());
                        editor.apply();
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

    private void loadEducationData() throws JSONException {
        String URL = "http://25.54.110.177:8095/Education/getAllEducation";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_email",sessionManager.EMAIL);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        editor.putString(EDUCATION_DATA, response.toString());
                        editor.apply();
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

    private void editProfile() throws JSONException {
        String URL = "http://25.54.110.177:8095/User/editUserProfile";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("first_name", et_firstName.getText().toString());
        jsonBody.put("last_name", et_lastName.getText().toString());
        jsonBody.put("lastEducation", sp_lastEducation.getSelectedItemPosition());
        jsonBody.put("location", sp_location.getSelectedItemPosition());
        jsonBody.put("description", et_description.getText().toString());
        jsonBody.put("upload_file", null);
        jsonBody.put("phone", et_phoneNumber.getText().toString());
        jsonBody.put("dateOfBirth", sessionManager.EMAIL);
        jsonBody.put("email", sessionManager.EMAIL);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        editor.putString(EDUCATION_DATA, jsonArray.toString());
                        editor.apply();
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
