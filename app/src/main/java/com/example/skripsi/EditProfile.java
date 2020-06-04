package com.example.skripsi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    EditText et_firstName, et_lastName,et_description,et_phoneNumber;
    TextView tv_DOB, tv_email;
    Spinner sp_lastEducation,sp_location;
    Button btn_save;
    ImageView btn_close;

    static int PRIVATE_MODE = 0;

    private String tanggal;

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    private DatePickerDialog datePickerDialog;

    private SimpleDateFormat dateFormatter, dateFormatter2;

    ViewDialog viewDialog;

    Map<String, Boolean> validationChecks = new HashMap<>();

    public static final String EMAIL = "EMAIL";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String PHONE = "PHONE";
    public static final String DOB = "DOB";
    public static final String DESCRIPTION = "DESCRIPTION";

    public static final String EDUCATION_ID = "EDUCATION_ID";
    public static final String EDUCATION_NAME = "EDUCATION_NAME";
    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String LOCATION_NAME = "LOCATION_NAME";


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

        viewDialog = new ViewDialog(EditProfile.this);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFirstName();
                validateLastName();
                validateDate();
                validateLocation();
                validateEducation();
                validatePhoneNumber();

                if (!validationChecks.containsValue(false)){
                    try {
                        editProfile();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        tv_DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
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

        tv_DOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateDate();
            }
        });

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mFirstName = user.get(sessionManager.FIRST_NAME);
        String mLastName = user.get(sessionManager.LAST_NAME);
        String mDescription = user.get(sessionManager.DESCRIPTION);
        String mPhone = user.get(sessionManager.PHONE);
        String mDob = user.get(sessionManager.DOB);
        String mEmail = user.get(sessionManager.EMAIL);

        String[] splitDob = mDob.split("\\s+");
        String tampungTanggal = splitDob[0];


        String phone = mPhone.substring(2,mPhone.length());

        et_firstName.setText(mFirstName);
        et_lastName.setText(mLastName);
        et_description.setText(mDescription);
        et_phoneNumber.setText(phone);

        tv_DOB.setText(tampungTanggal);
        tv_email.setText(mEmail);



        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN",PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setEducationSpinner(user.get(sessionManager.EDUCATION_DATA), user.get(sessionManager.EDUCATION_ID));
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA),user.get(sessionManager.LOCATION_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showDateDialog(){

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                System.out.println(dateFormatter.format(newDate.getTime()));

                tv_DOB.setText(dateFormatter.format(newDate.getTime()));
                tanggal = dateFormatter2.format(newDate.getTime());
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
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
        if(tv_DOB.getText().toString().isEmpty()){
            tv_DOB.setError("Field can't be empty");
            validationChecks.put("DOB", false);
        }
        else{
            tv_DOB.setError(null);
            validationChecks.put("DOB", true);
        }
    }

    private void validateEducation(){
        if(sp_lastEducation.getSelectedItemPosition() == 0){
            ((TextView) sp_lastEducation.getSelectedView()).setError("Please choose your last education");
            validationChecks.put("Education", false);
        }
        else{
            ((TextView) sp_lastEducation.getSelectedView()).setError(null);
            validationChecks.put("Education", true);
        }
    }

    private void validateLocation(){
        if(sp_location.getSelectedItemPosition() == 0){
            ((TextView) sp_location.getSelectedView()).setError("Please choose your location");
            validationChecks.put("Location", false);
        }
        else{
            ((TextView) sp_location.getSelectedView()).setError(null);
            validationChecks.put("Location", true);
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

    private void setEducationSpinner(String json, String id) throws JSONException {
        ArrayList<String> educationArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray educationJSON = jsonObject.getJSONArray("data");
        JSONObject object;
        educationArray.add("--- Choose Education ---");
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

    private void editProfile() throws JSONException {
        Context mContext = EditProfile.this;
        String URL = "http://25.54.110.177:8095/User/editUserProfile";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("first_name", et_firstName.getText().toString());
        jsonBody.put("last_name", et_lastName.getText().toString());
        jsonBody.put("lastEducation", sp_lastEducation.getSelectedItemPosition());
        jsonBody.put("location", sp_location.getSelectedItemPosition());
        jsonBody.put("description", et_description.getText().toString());
        jsonBody.put("upload_file", null);
        jsonBody.put("phone", et_phoneNumber.getText().toString());
        jsonBody.put("dateOfBirth", tv_DOB.getText().toString());
        jsonBody.put("email", tv_email.getText().toString());

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            String userEmail = object.getString("user_email");
                            String userFirstName = object.getString("user_first_name");
                            String userLastName = object.getString("user_last_name");
                            String userPhone = object.getString("user_phone");
                            String userDOB = object.getString("user_dateOfBirth");
                            String userDescription = object.getString("user_description");

                            JSONObject object1 = object.getJSONObject("education");
                            String educationId = object1.getString("education_id");
                            String educationName = object1.getString("education_name");

                            JSONObject object2 = object.getJSONObject("location");
                            String locationId = object2.getString("location_id");
                            String locationName = object2.getString("location_name");

                            editor.putString(EMAIL, userEmail);
                            editor.putString(FIRST_NAME, userFirstName);
                            editor.putString(LAST_NAME, userLastName);
                            editor.putString(PHONE, userPhone);
                            editor.putString(DOB, userDOB);
                            editor.putString(DESCRIPTION, userDescription);
                            editor.putString(LOCATION_ID, locationId);
                            editor.putString(LOCATION_NAME, locationName);
                            editor.putString(EDUCATION_ID, educationId);
                            editor.putString(EDUCATION_NAME, educationName);
                            editor.apply();

                            Toast.makeText(getApplicationContext(), "Edit profile success", Toast.LENGTH_LONG).show();
                            finish();

                        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }


}
