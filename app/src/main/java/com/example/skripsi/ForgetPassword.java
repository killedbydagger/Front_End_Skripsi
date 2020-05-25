package com.example.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPassword extends AppCompatActivity {

    EditText et_email;
    Button btn_resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        et_email = findViewById(R.id.et_email);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);
        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_email.getText().toString().equals("")) {
                    reset_password(et_email.getText().toString());
                }else {

                }
            }
        });
    }

    public void reset_password(String email) {
        String URL = "http://25.54.110.177:8095/User/forgetPasswordUser";

    }


}
