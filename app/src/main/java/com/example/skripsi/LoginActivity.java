package com.example.skripsi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    TextView tvSignup;
    Button btSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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
                Intent signinIntent = new Intent(getApplicationContext(),MainMenu.class);
                startActivity(signinIntent);
            }
        });
    }
}
