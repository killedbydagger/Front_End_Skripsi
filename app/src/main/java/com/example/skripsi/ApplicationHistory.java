package com.example.skripsi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ApplicationHistory extends AppCompatActivity {

    private RecyclerView mList;

    ViewDialog viewDialog;

    TextView empty;

    ImageView btn_close;

    RatingBar rb_ratingDariUser;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<History> historyList;
    private RecyclerView.Adapter adapter;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_history);

        rb_ratingDariUser = findViewById(R.id.rb_ratingDariUser);

        viewDialog = new ViewDialog(ApplicationHistory.this);
        viewDialog.showDialog();

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        empty = findViewById(R.id.empty);

        mList = findViewById(R.id.rv_applicantHistory);

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(getApplicationContext(),historyList);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();
        String userId = user.get(sessionManager.ID);

        try {
            applicantHistory(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void applicantHistory(String id) throws JSONException {
        String URL = "http://25.54.110.177:8095/VacancyApplicant/getUserApplicant";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", id);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    historyList.clear();
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for(int i = 0;i<jsonArray.length();i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            History history = new History();

                            JSONObject object1 = object.getJSONObject("vacancy");
                            JSONObject object2 = object1.getJSONObject("category");
                            history.setCategory(object2.getString("category_name"));

                            history.setTitle(object1.getString("vac_title"));

                            JSONObject object3 = object1.getJSONObject("business");
                            history.setCompanyName(object3.getString("bus_name"));
                            history.setRating(object3.getString("rating"));
                            history.setBusId(object3.getString("bus_id"));

                            JSONObject object4 = object1.getJSONObject("location");
                            history.setLocation(object4.getString("location_name"));

                            history.setSalary(object1.getInt("vac_salary"));

                            history.setStatus(object.getString("status"));
                            history.setFlagRating(object.getString("businessRatedFlag"));
                            history.setRateDariUser(object.getInt("businessUserRate"));

                            JSONObject object5 = object1.getJSONObject("position");
                            history.setPosition(object5.getString("position_name"));

                            history.setVacId(object1.getString("vac_id"));
                            history.setFavoriteFlag(object1.getString("favoriteFlag"));

                            historyList.add(history);
                        }

                        adapter.notifyDataSetChanged();
                        viewDialog.hideDialog();
                    }
                    else {
                        // Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                        empty.setVisibility(View.VISIBLE);
                        viewDialog.hideDialog();
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
