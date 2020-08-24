package com.example.skripsi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddBusiness extends AppCompatActivity {

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private RequestQueue requestQueue;

    static int PRIVATE_MODE = 0;

    EditText et_businessName, et_businessOverview;

    Spinner sp_location;

    Button btn_save;

    Map<String, Boolean> validationChecks = new HashMap<>();

    ImageView img_business, add, btn_close;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    File imageFile;

    String flag = "N";

    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_business = findViewById(R.id.img_business);
        img_business.setImageResource(R.drawable.logo1);
        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
            }
        });

        et_businessName = findViewById(R.id.et_businessName);
        sp_location = findViewById(R.id.sp_location);
        et_businessOverview = findViewById(R.id.et_businessOverview);
        btn_save = findViewById(R.id.btn_save);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN", PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBusinessName();
                validateLocation();
                validateBusinessOverview();

                if (!validationChecks.containsValue(false)) {
                    int locationId = sp_location.getSelectedItemPosition();
                    //createBisnis(userId , et_businessName.getText().toString(), locationId, et_businessOverview.getText().toString());
                    viewDialog = new ViewDialog(AddBusiness.this);
                    viewDialog.showDialog();
                    if (flag.equals("N")) {
                        try {
                            createBisnisWithoutImage(userId, et_businessName.getText().toString(), locationId, et_businessOverview.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        creatBusinessNew(imageFile, userId, et_businessName.getText().toString(), String.valueOf(locationId), et_businessOverview.getText().toString());
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
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri selectedImageUri = data.getData();
            String filePath = FetchPath.getPath(this, selectedImageUri);
            imageFile = new File(filePath);
            img_business.setImageURI(selectedImageUri);
            img_business.setScaleType(ImageView.ScaleType.FIT_XY);
            flag = "Y";

            System.out.println(imageFile);
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

    private void setLocationSpinner(String json) throws JSONException {
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
    }

    private void createBisnis(String id, String namaBisnis, int locationId, String overview) throws JSONException {
        Context mContext = AddBusiness.this;
        String URL = "http://25.56.11.101:8095/Business/createNewBusiness";
        //String URL = "https://springjava-1591708327203.azurewebsites.net/Business/createNewBusiness";
        //String URL = "http://25.54.110.177:8095/Business/createNewBusiness";
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("user_id", id);
        jsonBody.put("business_name", namaBisnis);
        jsonBody.put("location_id", locationId);
        jsonBody.put("business_overview", overview);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    System.out.println(status);
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String businessId = object.getString("bus_id");

                            if (!flag.equals("N")) {
                                addPhotoBusiness(imageFile, businessId);
                            }
                        }

                        Toast.makeText(getApplicationContext(), "New business has been created", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create new business", Toast.LENGTH_LONG).show();
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

        requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }

    private void addPhotoBusiness(File imageView, String businessId) throws JSONException {
        final String URL = "http://25.56.11.101:8095/Business/setBusinessImage";
        //final String URL = "https://springjava.azurewebsites.net/Business/setBusinessImage";
        //final String URL = "http://25.54.110.177:8095/Business/setBusinessImagee";

        Map<String, String> bodypart = new HashMap<>();
        bodypart.put("bus_id", businessId);

        MultipartRequest multipartRequest = new MultipartRequest(URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    System.out.println("status : " + status);
                    if (status.equals("Success")) {

                    } else {
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
        }, imageView, bodypart);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);
    }

    private void creatBusinessNew(File imageView, String id, String namaBisnis, String locationId, String overview) {
        String URL = "http://25.56.11.101:8095/Business/createNewBusiness";
        //String URL = "https://springjava.azurewebsites.net/Business/createNewBusiness";
        Map<String, String> bodypart = new HashMap<>();

        bodypart.put("user_id", id);
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
                        Toast.makeText(getApplicationContext(), "New business has been created", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create new business", Toast.LENGTH_LONG).show();
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

    private void createBisnisWithoutImage(String id, String namaBisnis, int locationId, String overview) throws JSONException {
        Context mContext = AddBusiness.this;
        String URL = "http://25.56.11.101:8095/Business/createNewBusinessWithoutImage";
        //String URL = "https://springjava.azurewebsites.net/Business/createNewBusinessWithoutImage";
        //String URL = "http://25.54.110.177:8095/Business/createNewBusiness";
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("user_id", id);
        jsonBody.put("business_name", namaBisnis);
        jsonBody.put("location_id", locationId);
        jsonBody.put("business_overview", overview);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    System.out.println(status);
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                        }

                        Toast.makeText(getApplicationContext(), "New business has been created", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create new business", Toast.LENGTH_LONG).show();
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20), //After the set time elapses the request will timeout
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
