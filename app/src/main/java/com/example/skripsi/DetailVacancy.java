package com.example.skripsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class DetailVacancy extends AppCompatActivity {

    String vacancyId, flag, businessId;

    TextView tv_category, tv_position, tv_title, tv_companyName, tv_location, tv_salary, tv_rating, tv_status;

    ImageView img_favorite, btn_close;

    Button btn_rate, btn_apply;

    TextView tv_description, notActive;

    SessionManager sessionManager;

    String active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_vacancy);
        vacancyId = getIntent().getExtras().getString("VACANCY_ID");
        flag = getIntent().getExtras().getString("FLAG");
        businessId = getIntent().getExtras().getString("BUSINESS_ID");

        tv_category = findViewById(R.id.tv_category);
        tv_position = findViewById(R.id.tv_position);
        tv_title = findViewById(R.id.tv_title);
        tv_companyName = findViewById(R.id.tv_companyName);
        tv_location = findViewById(R.id.tv_location);
        tv_salary = findViewById(R.id.tv_salary);
        tv_rating = findViewById(R.id.tv_rating);
        tv_status = findViewById(R.id.tv_status);
        img_favorite = findViewById(R.id.img_favorite);

        notActive = findViewById(R.id.notActive);

        if(flag.equals("Y")){
            img_favorite.setImageResource(R.drawable.icon_favorite_red);
        }
        else{
            img_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        btn_rate = findViewById(R.id.btn_rate);
        btn_apply = findViewById(R.id.btn_apply);
        tv_description = findViewById(R.id.tv_description);
        btn_close = findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            loadDetail(vacancyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag.equals("Y")){
                    try {
                        unFavorite(userId, vacancyId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    flag = "N";
                    img_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
                else{
                    try {
                        favorite(userId, vacancyId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    flag = "Y";
                    img_favorite.setImageResource(R.drawable.icon_favorite_red);
                }
            }
        });

        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rating = new Intent(DetailVacancy.this, Rating.class);
                rating.putExtra("NAMA", tv_companyName.getText().toString());
                rating.putExtra("IDENTIFIER","DETAIL");
                rating.putExtra("BUSINESS_ID",businessId);
                rating.putExtra("RATING",tv_rating.getText().toString());
                startActivity(rating);
            }
        });


        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    apply(userId ,vacancyId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void loadDetail(String id) throws JSONException {
        String URL = "http://25.54.110.177:8095/Vacancy/viewVacancyDetail";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("vac_id", id);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for(int i = 0;i<jsonArray.length();i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            JSONObject object1 = object.getJSONObject("category");
                            tv_category.setText(object1.getString("category_name"));

                            JSONObject object2 = object.getJSONObject("position");
                            tv_position.setText(object2.getString("position_name"));

                            tv_title.setText(object.getString("vac_title"));

                            JSONObject object3 = object.getJSONObject("business");
                            tv_companyName.setText(object3.getString("bus_name"));
                            tv_rating.setText(object3.getString("rating"));

                            JSONObject object4 = object.getJSONObject("location");
                            tv_location.setText(object4.getString("location_name"));

                            Locale localeID = new Locale("in", "ID");
                            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                            tv_salary.setText(formatRupiah.format((double)object.getInt("vac_salary")));

                            JSONObject object5 = object3.getJSONObject("user");
                            tv_status.setText(object5.getString("user_status"));

                            String tampung = object.getString("vac_description");
                            tampung = tampung.replace("/n", System.getProperty("line.separator"));
                            tv_description.setText(tampung);

                            active = object.getString("vac_activeYN");
                        }
                    }
                    else {
                        // Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(active.equals("N")){
                    notActive.setVisibility(View.VISIBLE);
                    notActive.bringToFront();
                    btn_apply.setEnabled(false);
                    btn_rate.setEnabled(false);
                    img_favorite.setEnabled(false);
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void apply(String userId, String vacId) throws JSONException {
        String URL = "http://25.54.110.177:8095/VacancyApplicant/applyVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
        jsonBody.put("vac_id", vacId);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Success to apply", Toast.LENGTH_LONG).show();
//                        FirebaseMessagingService firebaseMessagingService = new FirebaseMessagingService();
//                        firebaseMessagingService.showNotification("New applicant","");
                    }
                    else if(status.equals("Not Eligible")){
                        Toast.makeText(getApplicationContext(), "Please update your profile before applying", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void favorite(String userId, String vacId) throws JSONException {
        String URL = "http://25.54.110.177:8095/FavoriteVacancy/addFavoriteVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
        jsonBody.put("vac_id", vacId);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Favorite vacancy success", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Favorite vacancy failed", Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void unFavorite(String userId, String vacId) throws JSONException {
        String URL = "http://25.54.110.177:8095/FavoriteVacancy/removeFavoriteVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
        jsonBody.put("vac_id", vacId);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Unfavorite vacancy success", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Unfavorite vacancy failed", Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }
}
