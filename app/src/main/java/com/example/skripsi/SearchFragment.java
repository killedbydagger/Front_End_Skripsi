package com.example.skripsi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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

public class SearchFragment extends Fragment {
    private RecyclerView rv_searchVacancyList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<SearchVacancy> searchVacancies;
    private RecyclerView.Adapter adapter;

    Button btn_search;
    Spinner sp_kategori, sp_kategoriJabatan, sp_lokasi;
    SearchView sv_keyword;
    EditText et_salary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.searchvacancy_item, container, false);

        rv_searchVacancyList = v.findViewById(R.id.rv_searchVacancyList);

        btn_search = v.findViewById(R.id.btn_searchVacancy);
        sp_kategori = v.findViewById(R.id.sp_kategori);
        sp_kategoriJabatan = v.findViewById(R.id.sp_kategoriJabatan);
        sp_lokasi = v.findViewById(R.id.sp_lokasi);
        sv_keyword = v.findViewById(R.id.sv_keyword);
        et_salary = v.findViewById(R.id.et_salary);

        searchVacancies = new ArrayList<>();
        adapter = new SearchVacancyAdapter(v.getContext(), searchVacancies);
        linearLayoutManager = new LinearLayoutManager(v.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(rv_searchVacancyList.getContext(), linearLayoutManager.getOrientation());

        rv_searchVacancyList.setHasFixedSize(true);
        rv_searchVacancyList.setLayoutManager(linearLayoutManager);
        rv_searchVacancyList.setAdapter(adapter);
        rv_searchVacancyList.addItemDecoration(dividerItemDecoration);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    searchVacancy(view.getContext(), sp_kategori.getSelectedItemPosition(), sp_kategoriJabatan.getSelectedItemPosition(), sv_keyword.getQuery(), sp_lokasi.getSelectedItemPosition(), et_salary.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return v;
    }

    private void searchVacancy(final Context context, int category, int position, CharSequence keyword, int location, Editable salary) throws JSONException {
        String URL = "http://25.54.110.177:8095/Vacancy/searchVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("category_id", category);
        jsonBody.put("position_id", position);
        jsonBody.put("keyword", keyword);
        jsonBody.put("location_id", location);
        jsonBody.put("salary", salary);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    searchVacancies.clear();
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for(int i = 0;i<jsonArray.length();i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            SearchVacancy searchVacancy = new SearchVacancy();

                            JSONObject object1 = object.getJSONObject("vac");

                            JSONObject object2 = object1.getJSONObject("category");
                            searchVacancy.setVacancyCategory(object2.getString("category_name"));

                            searchVacancy.setVacancyTitle(object1.getString("vac_title"));

                            JSONObject object3 = object1.getJSONObject("business");
                            searchVacancy.setVacancyCompanyName(object3.getString("bus_name"));

                            JSONObject object4 = object1.getJSONObject("location");
                            searchVacancy.setVacancyLocation(object4.getString("location_name"));

                            searchVacancy.setVacancySalary(object1.getString("vac_salary"));

                            JSONObject object5 = object1.getJSONObject("business");
                            JSONObject object6 = object5.getJSONObject("user");
                            searchVacancy.setVacancyStatus(object6.getString("user_status"));

                            searchVacancies.add(searchVacancy);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(context, "NO VACANCY FOUND", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                final Map<String,String> params = new HashMap<String, String>();
                params.put("Context-Type","application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
