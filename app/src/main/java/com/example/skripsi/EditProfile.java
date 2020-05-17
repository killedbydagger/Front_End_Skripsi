package com.example.skripsi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    EditText et_firstName, et_lastName,et_description,et_phoneNumber;

    TextView tv_DOB, tv_email;

    Spinner sp_lastEducation,sp_location;

    SessionManager sessionManager;

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    int PRIVATE_MODE = 0;


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


    }

    private void loadLocationData(){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOCATION",PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }


}
