package com.example.skripsi;

import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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

import androidx.appcompat.app.AppCompatActivity;

public class ChangePassword extends AppCompatActivity {

    SessionManager sessionManager;

    ImageView img_close;
    Button btn_save;
    EditText et_oldPassword, et_newPassword, et_confirmPassword;
    CheckBox cb_showPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et_oldPassword = findViewById(R.id.et_oldPassword);
        et_newPassword = findViewById(R.id.et_newPassword);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);

        cb_showPassword = findViewById(R.id.cb_showPassword);
        cb_showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_oldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    et_oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userEmail = user.get(sessionManager.EMAIL);

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (et_newPassword.getText().toString().equals(et_confirmPassword.getText().toString())) {
                        changePassword(userEmail, et_oldPassword.getText().toString(), et_newPassword.getText().toString());
                    } else et_confirmPassword.setError("Password do not match!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changePassword(String userEmail, String userOlpPw, String userNewPw) throws JSONException {
        Context mContext = ChangePassword.this;
        String URL = "https://springjava-1591708327203.azurewebsites.net/User/changePasswordUser";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", userEmail);
        jsonBody.put("old_password", userOlpPw);
        jsonBody.put("new_password", userNewPw);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Success to change password", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to change password", Toast.LENGTH_LONG).show();
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
