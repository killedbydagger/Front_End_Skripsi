package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView tvSignup, tvForgot;
    Button btSignin;
    EditText et_email, et_password;
    CheckBox cb_showPassword;

    ViewDialog viewDialog;

    SessionManager sessionManager;

    Map<String, Boolean> validationChecks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sessionManager = new SessionManager(this);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_showPassword = (CheckBox) findViewById(R.id.cb_showPassword);

        viewDialog = new ViewDialog(LoginActivity.this);

        tvSignup = findViewById(R.id.tv_signup);
        tvForgot = findViewById(R.id.tv_forgotPassword);

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singupIntent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(singupIntent);
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(forgotIntent);
            }
        });

        btSignin = findViewById(R.id.btn_signin);
        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEmail();
                validatePassword();
                if (!validationChecks.containsValue(false)){
                    viewDialog.showDialog();
                    try {
                        login(et_email.getText().toString(),et_password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cb_showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }

    private void validateEmail(){
        if(et_email.getText().toString().isEmpty()){
            et_email.setError("Field can't be empty");
            validationChecks.put("Email", false);
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()){
            et_email.setError("Please enter a valid email format");
            validationChecks.put("Email", false);
        }
        else {
            et_email.setError(null);
            validationChecks.put("Email", true);
        }
    }

    private void validatePassword(){
        if(et_password.getText().toString().isEmpty()){
            et_password.setError("Field can't be empty");
            validationChecks.put("Password", false);
        }
        else{
            et_password.setError(null);
            validationChecks.put("Password", true);
        }
    }

    private void login(final String email, String password) throws JSONException {
        Context mContext = LoginActivity.this;

        String URL = "http://25.54.110.177:8095/User/getUserLogIn";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("email",email);
        jsonBody.put("password",password);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();

                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            String id = object.getString("user_id");
                            String email = object.getString("user_email");
                            String firstName = object.getString("user_first_name");
                            String lastName = object.getString("user_last_name");
                            String phone = object.getString("user_phone");
                            String gender = object.getString("user_gender");
                            String dateOfBirth = object.getString("user_dateOfBirth");
                            String description = "";

                            if(object.isNull("user_description")){
                                description = "";
                            }
                            else {
                                description = object.getString("user_description");
                            }

                            String user_status = object.getString("user_status");

                            String educationId = "0";
                            String educationName = "";

                            String locationId = "0";
                            String locationName = "";

                            if(object.isNull("education")){
                                educationId = "0";
                                educationName = "";
                            }
                            else {
                                JSONObject object1 = object.getJSONObject("education");
                                educationId = object1.getString("education_id");
                                educationName = object1.getString("education_name");
                            }

                            if(object.isNull("location")){
                                locationId = "0";
                                locationName = "";
                            }
                            else {
                                JSONObject object2 = object.getJSONObject("location");
                                locationId = object2.getString("location_id");
                                locationName = object2.getString("location_name");
                            }

                            sessionManager.createSession(id,email,firstName,lastName,phone,gender,dateOfBirth,description,user_status,educationId,educationName,locationId,locationName);
                            viewDialog.hideDialog();
                            Intent singinIntent = new Intent(getApplicationContext(),MainMenu.class);
                            startActivity(singinIntent);
                            finish();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
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
            public Map<String,String> getHeaders() throws AuthFailureError{
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
