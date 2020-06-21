package com.example.skripsi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class ForgetPassword extends AppCompatActivity {

    EditText et_email;
    Button btn_resetPassword;
    ViewDialog viewDialog;

    ImageView btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et_email = findViewById(R.id.et_email);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);
        viewDialog = new ViewDialog(this);
        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_email.getText().toString().isEmpty()) {
                    viewDialog.showDialog();
                    try {
                        forget_password(et_email.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    et_email.setError("Field can't be empty");
                }
            }
        });
    }

//    public void validateEmail() {
//        if(et_email.getText().toString().isEmpty()){
//            et_email.setError("Field can't be empty");
//        }
//    }

    public void forget_password(String email) throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/User/forgetPasswordUser";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", email);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Reset Password Success, check your email to get your new password", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Reset Password Failed", Toast.LENGTH_LONG).show();
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
            public Map<String,String> getHeaders() throws AuthFailureError {
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }

}
