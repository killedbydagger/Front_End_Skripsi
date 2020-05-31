package com.example.skripsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rating extends AppCompatActivity {

    ImageView btn_close;

    TextView tv_companyName, tv_dataNotFound, tv_count;

    RatingBar ratingBar;

    private RecyclerView mList;

    SessionManager sessionManager;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<RatingData> ratingList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_companyName = findViewById(R.id.tv_companyName);
        ratingBar = findViewById(R.id.ratingBar);
        tv_dataNotFound = findViewById(R.id.tv_dataNotFound);
        tv_count = findViewById(R.id.tv_count);

        tv_companyName.setText(getIntent().getExtras().getString("NAMA"));

        String s = getIntent().getExtras().getString("RATING");
        if(!s.equals("0.0")){
            ratingBar.setRating(Float.parseFloat(s));
        }

        mList = findViewById(R.id.rv_rating);

        ratingList = new ArrayList<>();
        adapter = new RatingDataAdapter(getApplicationContext(),ratingList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        sessionManager = new SessionManager(this);
        HashMap<String, String> business = sessionManager.getBusinessDetail();

        try {
            loadRating(business.get(sessionManager.BUSINESS_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadRating(String id) throws JSONException {
        String URL = "http://25.54.110.177:8095/BusinessRating/getBusinessRating";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("business_id", id);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ratingList.clear();
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        int tampung  = jsonArray.length();
                        tv_count.setText(Integer.toString(tampung));
                        for(int i = 0;i<jsonArray.length();i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            RatingData ratingData = new RatingData();

                            JSONObject object1 = object.getJSONObject("user");
                            ratingData.setUserName(object1.getString("user_first_name") + " " + object1.getString("user_last_name"));

                            ratingData.setValue(object.getString("busrat_value"));
                            ratingData.setComment(object.getString("busrat_comment"));

                            ratingList.add(ratingData);
                        }

                        adapter.notifyDataSetChanged();
                    }
                    else {
                        tv_dataNotFound.setVisibility(View.VISIBLE);
                        tv_count.setText("0");
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
