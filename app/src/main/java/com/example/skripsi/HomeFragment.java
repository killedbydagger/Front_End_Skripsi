package com.example.skripsi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class HomeFragment extends Fragment {

    private RecyclerView rv_listRecommended;
    SessionManager sessionManager;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Recommended> recommendeds;
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        rv_listRecommended = v.findViewById(R.id.rv_listRecommended);

        recommendeds = new ArrayList<>();
        adapter = new RecommendedAdapter(v.getContext(), recommendeds);
        linearLayoutManager = new LinearLayoutManager(v.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(rv_listRecommended.getContext(), linearLayoutManager.getOrientation());

        rv_listRecommended.setHasFixedSize(true);
        rv_listRecommended.setLayoutManager(linearLayoutManager);
        rv_listRecommended.setAdapter(adapter);
        rv_listRecommended.addItemDecoration(dividerItemDecoration);

        sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            System.out.println("ini jalanin func");
            recomendationList();
            System.out.println("ini kelar func");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void recomendationList() throws JSONException {
        String URL = "http://25.54.110.177:8095/Vacancy/recommendVacancy";
        final JSONObject jsonBody = new JSONObject();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    recommendeds.clear();
                    String status = response.getString("status");
                    System.out.println(status);
                    if (status.equals(". . .")) { //diisi
                        JSONArray jsonArray = response.getJSONArray("data");

                        for(int i = 0;i<jsonArray.length();i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Recommended recommended = new Recommended();

                            JSONObject object1 = object.getJSONObject("vac");

                            JSONObject object2 = object1.getJSONObject("category");
                            recommended.setVacancyCategory(object2.getString("category_name"));

                            recommended.setVacancyTitle(object1.getString("vac_title"));

                            JSONObject object3 = object1.getJSONObject("business");
                            recommended.setVacancyCompanyName(object3.getString("bus_name"));

                            JSONObject object4 = object1.getJSONObject("location");
                            recommended.setVacancyLocation(object4.getString("location_name"));

                            recommended.setVacancySalary(object1.getString("vac_salary"));

                            JSONObject object5 = object1.getJSONObject("business");
                            JSONObject object6 = object5.getJSONObject("user");
                            recommended.setVacancyStatus(object6.getString("user_status"));

                            recommendeds.add(recommended);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getView().getContext());
                        alertDialog.setMessage("Your haven't set recommendation. You need to set recommendation first!").setCancelable(false)
                                .setPositiveButton("Set Recommendation", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent createIntent = new Intent(getContext(),SetRecommendation.class);
                                        startActivity(createIntent);
                                    }
                                });
//                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                    }
//                                });

                        AlertDialog alert = alertDialog.create();
                        alert.setTitle("Set Recommendation");
                        alert.show();
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
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","application/json");
                return params;
            }
        };

        //RequestQueue requestQueue = Volley.newRequestQueue(context);
       // requestQueue.add(jsonObjectRequest);
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

