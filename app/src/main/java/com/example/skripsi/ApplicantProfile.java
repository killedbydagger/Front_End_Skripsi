package com.example.skripsi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ApplicantProfile extends AppCompatActivity {
    private static final int REQUEST_CALL = 1;

    ViewDialog viewDialog;

    Button btn_applicantViewFile, btn_applicantCall, btn_applicantEmail;
    ImageView img_close, img_applicantPhoto;
    TextView tv_applicantName, tv_applicantDOB, tv_applicantEducation, tv_applicantLocation, tv_applicantDesc, tv_userPhoneNum, tv_userEmailAdd, tv_noPhoto;

    private RecyclerView mList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Portfolio> portfolioList;
    private RecyclerView.Adapter adapter;

    ProgressBar pbLoading;

    String cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_profile);

        pbLoading = findViewById(R.id.pb_loading);

        img_applicantPhoto = findViewById(R.id.img_applicantPhoto);

        viewDialog = new ViewDialog(ApplicantProfile.this);
        viewDialog.showDialog();

        mList = findViewById(R.id.rv_photo);

        tv_noPhoto = findViewById(R.id.tv_noPhoto);

        portfolioList = new ArrayList<>();
        adapter = new PortfolioAdapter(getApplicationContext(),portfolioList);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        btn_applicantViewFile = findViewById(R.id.btn_applicantViewFile);
        btn_applicantViewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cv.equals(null)||cv.equals("null")){
                    Toast.makeText(getApplicationContext(), "No file can be viewed", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(cv));
                    startActivity(browserIntent);
                }
            }
        });


        mList.setHasFixedSize(true);
        mList.setAdapter(adapter);
        mList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        tv_applicantName = findViewById(R.id.tv_applicantName);
        tv_applicantDOB = findViewById(R.id.tv_applicantDOB);
        tv_applicantEducation = findViewById(R.id.tv_applicantEducation);
        tv_applicantLocation = findViewById(R.id.tv_applicantLocation);
        tv_applicantDesc = findViewById(R.id.tv_applicantDesc);
        tv_userPhoneNum = findViewById(R.id.tv_userPhoneNum);
        tv_userEmailAdd = findViewById(R.id.tv_userEmailAdd);

        btn_applicantCall = findViewById(R.id.btn_applicantCall);
        btn_applicantCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        btn_applicantEmail = findViewById(R.id.btn_applicantEmail);
        btn_applicantEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            showLoading(true);
            showApplicantProfile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makePhoneCall() {
        String number = (String) tv_userPhoneNum.getText();
        if (ContextCompat.checkSelfPermission(ApplicantProfile.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ApplicantProfile.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }

    private void sendMail() {
        String email = (String) tv_userEmailAdd.getText();
        String[] recipients = email.split(",");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showApplicantProfile() throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/User/getUserApplicant";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", getIntent().getExtras().getString("applicantEmail"));
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String applicantId = object.getString("user_id");

                            String applicantName = object.getString("user_first_name") + " " + object.getString("user_last_name");
                            String applicantDOB = object.getString("user_dateOfBirth");
                            String applicantDesc = object.getString("user_description");
                            String applicantPhone = object.getString("user_phone");
                            String applicantEmail = object.getString("user_email");
                            String applicantImgURL = object.getString("user_imageURL");
                            cv = object.getString("user_cv");

                            JSONObject object1 = object.getJSONObject("education");
                            String applicantEducation = object1.getString("education_name");

                            JSONObject object2 = object.getJSONObject("location");
                            String applicantLocation = object2.getString("location_name");

                            tv_applicantName.setText(applicantName);

                            String date = applicantDOB;
                            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                            Date oneWayTripDate = null;

                            if(applicantImgURL.equals(null) || applicantImgURL.equals("null")){
                                img_applicantPhoto.setImageResource(R.drawable.logo1);
                            }
                            else {
                                try {
                                    oneWayTripDate = input.parse(date);
                                    tv_applicantDOB.setText(output.format(oneWayTripDate));
                                    System.out.println("ini tanggalnya: " + oneWayTripDate);
                                } catch (ParseException e) {
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
                                try {
                                    java.net.URL url = new URL(applicantImgURL);
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream inputStream = connection.getInputStream();
                                    Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                                    img_applicantPhoto.setImageBitmap(myBitmap);
                                    img_applicantPhoto.setScaleType(ImageView.ScaleType.FIT_XY);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }



                            tv_applicantDesc.setText(applicantDesc);
                            tv_applicantEducation.setText(applicantEducation);
                            tv_applicantLocation.setText(applicantLocation);
                            tv_userPhoneNum.setText(applicantPhone);
                            tv_userEmailAdd.setText(applicantEmail);

                            loadImagePortfolio(applicantId);
                        }
                    } else {
                        //Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void loadImagePortfolio(String id) throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/UserPortfolio/getAllUserPortfolio";
        //String URL = "http://25.54.110.177:8095/UserPortfolio/getAllUserPortfolio";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", id);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    portfolioList.clear();
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Portfolio portfolio = new Portfolio();

                            portfolio.setImgId(object.getString("portfolio_id"));
                            portfolio.setImgURL(object.getString("image_url"));
                            portfolio.setUserId(object.getString("user_id"));

                            portfolioList.add(portfolio);
                        }
                        adapter.notifyDataSetChanged();
                        viewDialog.hideDialog();
                        showLoading(false);
                    } else {
                         //Toast.makeText(getApplicationContext(), "Load failed", Toast.LENGTH_LONG).show();
                        tv_noPhoto.setVisibility(View.VISIBLE);
                        viewDialog.hideDialog();
                        showLoading(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }

    public void showLoading(boolean visible) {
        if (visible) {
            pbLoading.setVisibility(View.VISIBLE);
        } else {
            pbLoading.setVisibility(View.GONE);
        }
    }
}
