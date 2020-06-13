package com.example.skripsi;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    EditText et_firstName, et_lastName, et_description, et_phoneNumber;
    TextView tv_DOB, tv_email;
    Spinner sp_lastEducation, sp_location;
    Button btn_save;
    ImageView btn_close, add, img_profile;

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

    public static final String IMG_URL = "IMG_URL";

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    File imageFile;

    String flag = "N";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        img_profile = findViewById(R.id.img_profile);

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }else{
                        pickImageFromGallery();
                    }
                }
                else{
                    pickImageFromGallery();
                }
            }
        });

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

        final HashMap<String, String> user = sessionManager.getUserDetail();
        String mFirstName = user.get(sessionManager.FIRST_NAME);
        String mLastName = user.get(sessionManager.LAST_NAME);
        String mDescription = user.get(sessionManager.DESCRIPTION);
        String mPhone = user.get(sessionManager.PHONE);
        String mDob = user.get(sessionManager.DOB);
        String mEmail = user.get(sessionManager.EMAIL);

        tanggal = mDob;

//        String[] splitDob = mDob.split("\\s+");
//        String tampungTanggal = splitDob[0];

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        if (user.get(sessionManager.IMG_URL) == null) {
            img_profile.setImageResource(R.drawable.logo1);
        }
        else {
            try {
                URL url = new URL(user.get(sessionManager.IMG_URL));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                img_profile.setImageBitmap(myBitmap);
                img_profile.setScaleType(ImageView.ScaleType.FIT_XY);

//                System.out.println("img url :" + user.get(sessionManager.IMG_URL));
//                URL url = new URL(user.get(sessionManager.IMG_URL));
//                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                img_profile.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String date = user.get(sessionManager.DOB);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
        Date oneWayTripDate = null;
        try {
            oneWayTripDate = input.parse(date);
            tv_DOB.setText(output.format(oneWayTripDate));
            System.out.println("ini tanggalnya: " + oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String phone = mPhone.substring(2, mPhone.length());

        et_firstName.setText(mFirstName);
        et_lastName.setText(mLastName);
        et_description.setText(mDescription);
        et_phoneNumber.setText(phone);

        //tv_DOB.setText(tampungTanggal);
        tv_email.setText(mEmail);


        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN", PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setEducationSpinner(user.get(sessionManager.EDUCATION_DATA), user.get(sessionManager.EDUCATION_ID));
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA), user.get(sessionManager.LOCATION_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

                if (!validationChecks.containsValue(false)) {
                    try {
                        if(!flag.equals("N") ){
                            editPhoto(imageFile, user.get(sessionManager.ID));
                        }
                        editProfile();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }
                else{
                    Toast.makeText(this,"Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri selectedImageUri = data.getData();
            String filePath = FetchPath.getPath(this, selectedImageUri);
            imageFile = new File(filePath);
            img_profile.setImageURI(selectedImageUri);
            img_profile.setScaleType(ImageView.ScaleType.FIT_XY);
            flag = "Y";
        }
    }


    private void showDateDialog() {

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                String date = dateFormatter.format(newDate.getTime());
                SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                Date oneWayTripDate = null;
                try {
                    oneWayTripDate = input.parse(date);
                    tv_DOB.setText(output.format(oneWayTripDate));
                    System.out.println("ini tanggalnya: " + oneWayTripDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                tanggal = dateFormatter2.format(newDate.getTime());
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void validateFirstName() {
        if (et_firstName.getText().toString().isEmpty()) {
            et_firstName.setError("Field can't be empty");
            validationChecks.put("FirstName", false);
        } else {
            validationChecks.put("FirstName", true);
        }
    }

    private void validateLastName() {
        if (et_lastName.getText().toString().isEmpty()) {
            et_lastName.setError("Field can't be empty");
            validationChecks.put("LastName", false);
        } else {
            et_lastName.setError(null);
            validationChecks.put("LastName", true);
        }
    }

    private void validateDate() {
        if (tv_DOB.getText().toString().isEmpty()) {
            tv_DOB.setError("Field can't be empty");
            validationChecks.put("DOB", false);
        } else {
            tv_DOB.setError(null);
            validationChecks.put("DOB", true);
        }
    }

    private void validateEducation() {
        if (sp_lastEducation.getSelectedItemPosition() == 0) {
            ((TextView) sp_lastEducation.getSelectedView()).setError("Please choose your last education");
            validationChecks.put("Education", false);
        } else {
            ((TextView) sp_lastEducation.getSelectedView()).setError(null);
            validationChecks.put("Education", true);
        }
    }

    private void validateLocation() {
        if (sp_location.getSelectedItemPosition() == 0) {
            ((TextView) sp_location.getSelectedView()).setError("Please choose your location");
            validationChecks.put("Location", false);
        } else {
            ((TextView) sp_location.getSelectedView()).setError(null);
            validationChecks.put("Location", true);
        }
    }

    private void validatePhoneNumber() {

        if (et_phoneNumber.getText().toString().isEmpty()) {
            et_phoneNumber.setError("Field can't be empty");
            validationChecks.put("Phone", false);
        } else {
            String first = et_phoneNumber.getText().toString().substring(0, 1);
            if (first.equals("0")) {
                et_phoneNumber.setError("Phone number can't start with 0");
                validationChecks.put("Phone", false);
            } else {
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
        for (int i = 0; i < educationJSON.length(); i++) {
            object = educationJSON.getJSONObject(i);
            educationArray.add(object.getString("education_name"));
        }
        ArrayAdapter<String> educationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, educationArray);
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
        for (int i = 0; i < locationJSON.length(); i++) {
            object = locationJSON.getJSONObject(i);
            locationArray.add(object.getString("location_name"));
        }
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationArray);
        locationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_location.setAdapter(locationArrayAdapter);
        sp_location.setSelection(Integer.parseInt(id));
    }

    private void editProfile() throws JSONException {
        Context mContext = EditProfile.this;
        String URL = "https://springjava-1591708327203.azurewebsites.net/User/editUserProfile";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("first_name", et_firstName.getText().toString());
        jsonBody.put("last_name", et_lastName.getText().toString());
        jsonBody.put("lastEducation", sp_lastEducation.getSelectedItemPosition());
        jsonBody.put("location", sp_location.getSelectedItemPosition());
        jsonBody.put("description", et_description.getText().toString());
        jsonBody.put("upload_file", null);
        jsonBody.put("phone", "62" + et_phoneNumber.getText().toString());
        jsonBody.put("dateOfBirth", tanggal);
        jsonBody.put("email", tv_email.getText().toString());

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String userEmail = object.getString("user_email");
                            String userFirstName = object.getString("user_first_name");
                            String userLastName = object.getString("user_last_name");
                            String userPhone = object.getString("user_phone");
                            String userDOB = object.getString("user_dateOfBirth");
                            String userDescription = object.getString("user_description");
                            String userImage = object.getString("user_imageURL");

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
                            editor.putString(IMG_URL, userImage);
                            editor.apply();

                            Toast.makeText(getApplicationContext(), "Edit profile success", Toast.LENGTH_LONG).show();
                            finish();

                        }
                    } else {
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }

    private void editPhoto(File imageView, String userId) throws JSONException {
        final String URL = "https://springjava-1591708327203.azurewebsites.net/User/setUserPhotoProfile";
        //final String URL = "http://25.54.110.177:8095/User/setUserPhotoProfile";
        Map<String,String> bodypart = new HashMap<>();
        bodypart.put("user_id", userId);

        MultipartRequest multipartRequest = new MultipartRequest(URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    System.out.println("status : " + status);
                    if(status.equals("Success")){

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Failed to change photo", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, imageView, bodypart );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);
    }
}
