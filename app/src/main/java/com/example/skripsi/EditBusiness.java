package com.example.skripsi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditBusiness extends AppCompatActivity {

    SessionManager sessionManager;
    public SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    static int PRIVATE_MODE = 0;

    EditText et_businessName, et_businessOverview;

    Spinner sp_location;

    Button btn_save;

    Map<String, Boolean> validationChecks = new HashMap<>();

    ImageView img_business, btn_close, add;

    ViewDialog viewDialog;

    public static final String BUSINESS_ID = "BUSINESS_ID";
    public static final String BUSINESS_IMAGE = "BUSINESS_IMAGE";
    public static final String BUSINESS_NAME = "BUSINESS_NAME";
    public static final String BUSINESS_LOCATION_ID = "BUSINESS_LOCATION_ID";
    public static final String BUSINESS_LOCATION_NAME = "BUSINESS_LOCATION_NAME";
    public static final String BUSINESS_OVERVIEW = "BUSINESS_OVERVIEW";

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    File imageFile;

    String flag = "N";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        et_businessName = findViewById(R.id.et_businessName);
        et_businessOverview = findViewById(R.id.et_businessOverview);
        sp_location = findViewById(R.id.sp_location);

        img_business = findViewById(R.id.img_business);
        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

        sessionManager = new SessionManager(this);
        final HashMap<String, String> business = sessionManager.getBusinessDetail();
        final String business_id = business.get(sessionManager.BUSINESS_ID);
        String businessName = business.get(sessionManager.BUSINESS_NAME);
        String businessOverview = business.get(sessionManager.BUSINESS_OVERVIEW);
        String businessImage = business.get(sessionManager.BUSINESS_IMAGE);

        et_businessName.setText(businessName);
        et_businessOverview.setText(businessOverview);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN", PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA), business.get(sessionManager.BUSINESS_LOCATION_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        if (businessImage.equals("null")) {
            img_business.setImageResource(R.drawable.logo1);
        }
        else {
            try {
                URL url = new URL(businessImage);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                img_business.setImageBitmap(myBitmap);
                img_business.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBusinessName();
                validateLocation();
                validateBusinessOverview();
                if (!validationChecks.containsValue(false)) {
                    viewDialog = new ViewDialog(EditBusiness.this);
                    viewDialog.showDialog();
//                    try {
//                        editProfile(userId, business_id, et_businessName.getText().toString(), sp_location.getSelectedItemPosition(), et_businessOverview.getText().toString(), imageFile);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    if(flag.equals("N")){
                        try {
                            editProfileWithoutImage(userId, business_id , et_businessName.getText().toString(), String.valueOf(sp_location.getSelectedItemPosition()), et_businessOverview.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        editBusinessMulti(imageFile ,userId, business_id , et_businessName.getText().toString(), String.valueOf(sp_location.getSelectedItemPosition()), et_businessOverview.getText().toString());
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
            img_business.setImageURI(selectedImageUri);
            img_business.setScaleType(ImageView.ScaleType.FIT_XY);
            flag = "Y";
        }
    }

    private void validateBusinessName() {
        if (et_businessName.getText().toString().isEmpty()) {
            et_businessName.setError("Field can't be empty");
            validationChecks.put("BusinessName", false);
        } else {
            validationChecks.put("BusinessName", true);
        }
    }

    private void validateBusinessOverview() {
        if (et_businessOverview.getText().toString().isEmpty()) {
            et_businessOverview.setError("Field can't be empty");
            validationChecks.put("BusinessOverview", false);
        } else {
            validationChecks.put("BusinessOverview", true);
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

    private void editProfileWithoutImage(String userId, String businessId, String businessName, String locationId, String businessOverview) throws JSONException {
        Context mContext = EditBusiness.this;
        String URL = "https://springjava-1591708327203.azurewebsites.net/Business/editUserBusinessWithoutImage";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
        jsonBody.put("business_id", businessId);
        jsonBody.put("business_name", businessName);
        jsonBody.put("location_id", locationId);
        jsonBody.put("business_overview", businessOverview);


        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Edit business success", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();
                    } else {
                        viewDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Failed to edit business", Toast.LENGTH_LONG).show();
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

//    private void editBusiness(File imageView, String id, String namaBisnis, String locationId, String overview){
//        String URL = "https://springjava-1591708327203.azurewebsites.net/Business/editUserBusiness";
//        Map<String,String> bodypart = new HashMap<>();
//
//        bodypart.put("user_id", id);
//        bodypart.put("business_name", namaBisnis);
//        bodypart.put("location_id", locationId);
//        bodypart.put("business_overview", overview);
//
//        MultipartTest multipartTest = new MultipartTest(URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    String status = jsonObject.getString("status");
//                    if (status.equals("Success")) {
//                        Toast.makeText(getApplicationContext(), "Edit business success", Toast.LENGTH_LONG).show();
//                        viewDialog.hideDialog();
//                        finish();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Failed to edit business", Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }, imageView, bodypart);
//
//        multipartTest.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(multipartTest);
//    }

    private void editBusinessMulti(final File imageView, String id, String businessId, String namaBisnis, String locationId, String overview){
        String URL = "https://springjava-1591708327203.azurewebsites.net/Business/editUserBusiness";
        Map<String,String> bodypart = new HashMap<>();

        bodypart.put("user_id", id);
        bodypart.put("business_id", businessId);
        bodypart.put("business_name", namaBisnis);
        bodypart.put("location_id", locationId);
        bodypart.put("business_overview", overview);

        MultipartTest multipartTest = new MultipartTest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("Success")) {
                        System.out.println(imageView);
                        Toast.makeText(getApplicationContext(), "Edit business success", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();
                    } else {
                        viewDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Failed to edit business", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, imageView, bodypart);

        multipartTest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartTest);
    }
}
