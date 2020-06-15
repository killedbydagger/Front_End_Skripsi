package com.example.skripsi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Map;

public class ViewPortfolio extends AppCompatActivity {


    ImageView img_portfolio, btn_close, img_delete;

    String imgUrl, imgUserId, imgId;

    SessionManager sessionManager;

    ViewDialog viewDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_portfolio);

        imgUrl = getIntent().getExtras().getString("IMG_URL");
        imgUserId = getIntent().getExtras().getString("IMG_USER_ID");
        imgId = getIntent().getExtras().getString("IMG_ID");


        img_portfolio = findViewById(R.id.img_portfolio);
        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userID = user.get(sessionManager.ID);

        img_delete = findViewById(R.id.img_delete);
        if(imgUserId.equals(userID)){
            img_delete.setVisibility(View.VISIBLE);
        }
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewDialog = new ViewDialog(ViewPortfolio.this);
                viewDialog.showDialog();
                try {
                    deletePortfolio(userID, imgId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            img_portfolio.setImageBitmap(myBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deletePortfolio(String userId, String businessId) throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/UserPortfolio/removeUserPortfolio";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
        jsonBody.put("portfolio_id", businessId);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Success to delete portfolio", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "failed to delete portfolio", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
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
}
