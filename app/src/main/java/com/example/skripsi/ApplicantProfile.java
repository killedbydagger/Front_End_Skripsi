package com.example.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class ApplicantProfile extends AppCompatActivity {
    Button btn_applicantViewFile, btn_applicantCall, btn_applicantEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_profile);

        getIntent().getExtras().getString("user_email");
    }
}
