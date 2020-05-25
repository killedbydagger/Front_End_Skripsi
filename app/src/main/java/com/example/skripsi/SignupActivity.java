package com.example.skripsi;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private DatePickerDialog datePickerDialog;

    private TextView mDisplayDate;

    private SimpleDateFormat dateFormatter, dateFormatter2;

    private RequestQueue requestQueue;
    private EditText et_firstName, et_lastName, et_email, et_phoneNumber, et_password, et_confirmPassword;
    private RadioGroup rb_genderGroup;
    private Button btn_signup;
    private RadioButton rb_gender;
    private String tanggal;
    CheckBox cb_showPassword;

    Map<String, Boolean> validationChecks = new HashMap<>();

    ViewDialog viewDialog;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" + //at least 1 digit
                    "(?=.*[a-z])" + //at least 1 lower case letter
                    "(?=.*[A-Z])" + //at least 1 upper case letter
                    "(?=\\S+$)" + //no white spaces
                    ".{6,}" + //at least 6 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mDisplayDate = (TextView) findViewById(R.id.tv_DOB);
        et_firstName = (EditText) findViewById(R.id.et_firstName);
        et_lastName = (EditText) findViewById(R.id.et_email);
        et_email = (EditText) findViewById(R.id.et_email);
        et_phoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        rb_genderGroup = (RadioGroup) findViewById(R.id.rb_genderGroup);

        viewDialog = new ViewDialog(SignupActivity.this);

        cb_showPassword = (CheckBox) findViewById(R.id.cb_showPassword);

        cb_showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        et_firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateFirstName();
            }
        });

        et_lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateLastName();
            }
        });

        mDisplayDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateDate();
            }
        });

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateEmail();
            }
        });

        et_phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validatePhoneNumber();
            }
        });

        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validatePassword();
            }
        });

        et_confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateConfirm();
            }
        });

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        mDisplayDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateDate();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateFirstName();
                validateLastName();
                validateDate();
                validateEmail();
                validatePhoneNumber();
                validatePassword();
                validateConfirm();

                if (!validationChecks.containsValue(false)){
                    viewDialog.showDialog();
                    try {
                        submit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void validateFirstName(){
        if(et_firstName.getText().toString().isEmpty()){
            et_firstName.setError("Field can't be empty");
            validationChecks.put("FirstName", false);
        }
        else{
            validationChecks.put("FirstName", true);
        }
    }

    private void validateLastName(){
        if(et_lastName.getText().toString().isEmpty()){
            et_lastName.setError("Field can't be empty");
            validationChecks.put("LastName", false);
        }
        else{
            et_lastName.setError(null);
            validationChecks.put("LastName", true);
        }
    }

    private void validateDate(){
        if(mDisplayDate.getText().toString().isEmpty()){
            mDisplayDate.setError("Field can't be empty");
            validationChecks.put("DOB", false);
        }
        else{
            mDisplayDate.setError(null);
            validationChecks.put("DOB", true);
        }
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
            try {
                checkEmail(et_email.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void validatePhoneNumber(){

        if(et_phoneNumber.getText().toString().isEmpty()){
            et_phoneNumber.setError("Field can't be empty");
            validationChecks.put("Phone", false);
        }
        else{
            String first = et_phoneNumber.getText().toString().substring(0,1);
            if(first.equals("0")){
                et_phoneNumber.setError("Phone number can't start with 0");
                validationChecks.put("Phone", false);
            }
            else{
                et_phoneNumber.setError(null);
                validationChecks.put("Phone", true);
            }
        }
    }

    private void validatePassword(){
        if(et_password.getText().toString().isEmpty()){
            et_password.setError("Field can't be empty");
            validationChecks.put("Password", false);
        }
        else if(!PASSWORD_PATTERN.matcher(et_password.getText().toString()).matches()){
            et_password.setError("Field can't be empty");
            validationChecks.put("Password", false);
        }
        else{
            et_password.setError(null);
            validationChecks.put("Password", true);
        }
    }

    private void validateConfirm(){
        if(et_confirmPassword.getText().toString().isEmpty()){
            et_confirmPassword.setError("Field can't be empty");
            validationChecks.put("Confirm", false);
        }
        else if(!et_confirmPassword.getText().toString().equals(et_confirmPassword.getText().toString())){
            et_confirmPassword.setError("Password and confirm password don't match");
            validationChecks.put("Confirm", false);
        }
        else{
            et_confirmPassword.setError(null);
            validationChecks.put("Confirm", true);
        }
    }


    private void showDateDialog(){

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                mDisplayDate.setText(dateFormatter.format(newDate.getTime()));
                tanggal = dateFormatter2.format(newDate.getTime());
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void checkEmail(String email) throws JSONException {
        Context mContext = SignupActivity.this;

        String URL = "http://25.54.110.177:8095/User/checkEmailRegister";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", email);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");

                    Log.d(TAG, status);

                    if (status.equals("Exist") ) {
                        et_email.setError("Email already exist");
                        validationChecks.put("Email", false);
                    } else {
                        et_email.setError(null);
                        validationChecks.put("Email", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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

    private void submit() throws JSONException {
        Context mContext = SignupActivity.this;
        String URL = "http://25.54.110.177:8095/User/createNewUser";
        JSONObject jsonBody = new JSONObject();

        int selectedId = rb_genderGroup.getCheckedRadioButtonId();
        rb_gender = (RadioButton) findViewById(selectedId);

        jsonBody.put("email", et_email.getText().toString());
        jsonBody.put("password", et_password.getText().toString());
        jsonBody.put("first_name", et_firstName.getText().toString());
        jsonBody.put("last_name", et_lastName.getText().toString());
        jsonBody.put("phone", "62" + et_phoneNumber.getText().toString());
        jsonBody.put("gender", rb_gender.getText().toString());
        jsonBody.put("dateOfBirth", tanggal);

        Log.d(TAG, jsonBody.toString());

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if(status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "New account has been created", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();

                    }else {
                        Toast.makeText(getApplicationContext(), "Failed to create new account", Toast.LENGTH_LONG).show();
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
            public Map<String,String> getHeaders() throws AuthFailureError {
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","applicatiom/json");
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }

}
