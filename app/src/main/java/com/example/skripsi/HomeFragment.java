package com.example.skripsi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
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
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private RecyclerView rv_listRecommended;
    SessionManager sessionManager;

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    static int PRIVATE_MODE = 0;

    public static final String RECOMMENDATION = "RECOMMENDATION";
    public static final String RECOMMENDATION_LOCATION = "RECOMMENDATION LOCATION";

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Recommended> recommendeds;
    private RecyclerView.Adapter adapter;

    Button btn_setRecommendation;

    ViewDialog viewDialog;

    ImageView img_edit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        viewDialog = new ViewDialog(getActivity());
        viewDialog.showDialog();

        img_edit = v.findViewById(R.id.img_edit);
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SetRecommendation.class);
                startActivity(intent);
            }
        });
        rv_listRecommended = v.findViewById(R.id.rv_listRecommended);
        btn_setRecommendation = v.findViewById(R.id.btn_setRecommendation);
        btn_setRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SetRecommendation.class);
                startActivity(intent);
            }
        });

        recommendeds = new ArrayList<>();
        adapter = new RecommendedAdapter(v.getContext(), recommendeds);
        linearLayoutManager = new LinearLayoutManager(v.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //dividerItemDecoration = new DividerItemDecoration(rv_listRecommended.getContext(), linearLayoutManager.getOrientation());

        rv_listRecommended.setHasFixedSize(true);
        rv_listRecommended.setLayoutManager(linearLayoutManager);
        rv_listRecommended.setAdapter(adapter);
        //rv_listRecommended.addItemDecoration(dividerItemDecoration);

        sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);
        if (user.get(sessionManager.RECOMMENDATION) == null) {
            try {
                sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN", PRIVATE_MODE);
                editor = sharedPreferences.edit();
                getUserRecommendation(Integer.parseInt(userId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                loadRecommendation(userId, user.get(sessionManager.RECOMMENDATION), Integer.parseInt(user.get(sessionManager.RECOMMENDATION_LOCATION)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

            try {
                sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN", PRIVATE_MODE);
                editor = sharedPreferences.edit();
                getUserRecommendation(Integer.parseInt(userId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void getUserRecommendation(int id) throws JSONException {
        String URL = "https://springjava.azurewebsites.net/Recommendation/getUserRecommendation";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", id);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    recommendeds.clear();
                    String status = response.getString("status");
                    if (status.equals("Not Found")) {
                        btn_setRecommendation.setVisibility(View.VISIBLE);
                        viewDialog.hideDialog();
                    } else if (status.equals("Success")) {
                        btn_setRecommendation.setVisibility(View.GONE);

                        sessionManager = new SessionManager(getContext());
                        HashMap<String, String> user = sessionManager.getUserDetail();
                        final String userId = user.get(sessionManager.ID);

                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            editor.putString(RECOMMENDATION, object.getString("recom_categories"));
                            editor.putString(RECOMMENDATION_LOCATION, object.getString("location_id"));
                            editor.apply();

                            loadRecommendation(userId, object.getString("recom_categories"), Integer.parseInt(object.getString("location_id")));
                        }
                        img_edit.setVisibility(View.VISIBLE);
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void loadRecommendation(String id, String categories, int locationid) throws JSONException {
        String URL = "https://springjava.azurewebsites.net/Vacancy/recommendVacancy";
        //String URL = "http://25.54.110.177:8095/Vacancy/recommendVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", id);
        jsonBody.put("categories", categories);
        jsonBody.put("location_id", locationid);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    recommendeds.clear();
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Recommended recommended = new Recommended();

                            recommended.setVacancyId(object.getString("vac_id"));
                            recommended.setVacancyTitle(object.getString("vac_title"));
                            recommended.setVacancySalary(object.getInt("vac_salary"));

                            JSONObject object1 = object.getJSONObject("category");
                            recommended.setVacancyCategory(object1.getString("category_name"));

                            JSONObject object2 = object.getJSONObject("position");
                            recommended.setVacancyPosition(object2.getString("position_name"));

                            JSONObject object3 = object.getJSONObject("business");
                            recommended.setVacancyCompanyName(object3.getString("bus_name"));
                            recommended.setVacancyCompanyRating(object3.getString("rating"));
                            recommended.setBusinessId(object3.getString("bus_id"));
                            recommended.setBusinessImage(object3.getString("bus_image"));

                            JSONObject object4 = object3.getJSONObject("location");
                            recommended.setVacancyLocation(object4.getString("location_name"));

                            JSONObject object5 = object3.getJSONObject("user");
                            recommended.setVacancyStatus(object5.getString("user_status"));

                            recommended.setFavoriteFlag(object.getString("favoriteFlag"));


                            recommendeds.add(recommended);
                        }
                        adapter.notifyDataSetChanged();
                        img_edit.setVisibility(View.VISIBLE);
                        viewDialog.hideDialog();
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
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

//    private void setupViewPager (ViewPager viewPager){
//        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
//
//        adapter.addFragment(new JobsFragment() , "Jobs");
//        adapter.addFragment(new EventsFragment(), "Events");
//
//        viewPager.setAdapter(adapter);
//    }


}

