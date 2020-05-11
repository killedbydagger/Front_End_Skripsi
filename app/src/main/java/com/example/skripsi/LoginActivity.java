package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView tvSignup;
    Button btSignin;
    EditText et_email, et_password;
    CheckBox cb_showPassword;

    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_showPassword = (CheckBox) findViewById(R.id.cb_showPassword);

        viewDialog = new ViewDialog(LoginActivity.this);

        tvSignup = findViewById(R.id.tv_signup);
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singupIntent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(singupIntent);
            }
        });

        btSignin = findViewById(R.id.btn_signin);
        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDialog.showDialog();
                try {
                    login(et_email.getText().toString(),et_password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void login(String email, String password) throws JSONException {
        Context mContext = LoginActivity.this;

        String URL = "http://25.54.110.177:8095/User/getUserLogIn";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("email",email);
        jsonBody.put("password",password);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");

                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        Intent singinIntent = new Intent(getApplicationContext(),MainMenu.class);
                        startActivity(singinIntent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
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
