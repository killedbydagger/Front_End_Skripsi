package com.example.skripsi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchFragment extends Fragment {
    private RecyclerView rv_searchedList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<SearchVacancy> searchVacancies;
    private RecyclerView.Adapter adapter;

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;

    Button btn_search, btn_filter;
    Spinner sp_kategori, sp_kategoriJabatan, sp_lokasi;
    SearchView sv_keyword;
    EditText et_salary;

    LinearLayout layoutVacancyList;
    RelativeLayout layoutSearchFilter;
    public SharedPreferences.Editor editor;

    static int PRIVATE_MODE = 0;

    HashMap<String, Integer> compared_position = new HashMap<>();
    ArrayList<String> positionArray = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        rv_searchedList = v.findViewById(R.id.rv_searchedList);

        layoutVacancyList = v.findViewById(R.id.layoutVacancyList);
        layoutSearchFilter = v.findViewById(R.id.layoutSearchFilter);
        btn_search = v.findViewById(R.id.btn_searchVacancy);
        sp_kategori = v.findViewById(R.id.sp_kategori);
        sp_kategoriJabatan = v.findViewById(R.id.sp_kategoriJabatan);
        sp_lokasi = v.findViewById(R.id.sp_lokasi);
        sv_keyword = v.findViewById(R.id.sv_keyword);
        et_salary = v.findViewById(R.id.et_salary);

        btn_filter = v.findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutSearchFilter.getVisibility() == View.VISIBLE){
                    layoutVacancyList.setVisibility(View.VISIBLE);
                    layoutSearchFilter.setVisibility(View.GONE);
                    btn_filter.setText("Filter");
                }else {
                    layoutVacancyList.setVisibility(View.GONE);
                    layoutSearchFilter.setVisibility(View.VISIBLE);
                    btn_filter.setText("List");
                }
            }
        });

        searchVacancies = new ArrayList<>();
        adapter = new SearchVacancyAdapter(v.getContext(), searchVacancies);
        linearLayoutManager = new LinearLayoutManager(v.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(rv_searchedList.getContext(), linearLayoutManager.getOrientation());

        rv_searchedList.setHasFixedSize(true);
        rv_searchedList.setLayoutManager(linearLayoutManager);
        rv_searchedList.setAdapter(adapter);
        rv_searchedList.addItemDecoration(dividerItemDecoration);

        sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN",PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setCategorySpinner(user.get(sessionManager.CATEGORY_DATA));
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sp_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                compared_position.clear();
                positionArray.clear();
                sp_kategoriJabatan.setAdapter(null);
                if(position == 0){
                    et_salary.setText("");
                    et_salary.setEnabled(true);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card);
                    sp_kategoriJabatan.setEnabled(true);
                    sp_kategoriJabatan.setBackgroundResource(R.drawable.edit_text_card);
                    sp_kategoriJabatan.setVisibility(View.GONE);
                }
                else if(position == 6){
                    et_salary.setText("0");
                    et_salary.setEnabled(false);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card_gray);
                    sp_kategoriJabatan.setEnabled(false);
                    sp_kategoriJabatan.setBackgroundResource(R.drawable.edit_text_card_gray);
                    sp_kategoriJabatan.setVisibility(View.GONE);
                }
                else{
                    et_salary.setText("");
                    et_salary.setEnabled(true);
                    et_salary.setBackgroundResource(R.drawable.edit_text_card);
                    sp_kategoriJabatan.setEnabled(true);
                    sp_kategoriJabatan.setBackgroundResource(R.drawable.edit_text_card);
                    sp_kategoriJabatan.setVisibility(View.VISIBLE);
                    try {
                        loadPositionData(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp_kategori.getSelectedItemPosition() == 0 && sv_keyword.getQuery().toString().equals("") && sp_lokasi.getSelectedItemPosition() == 0 && et_salary.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Need to feel atleast one Filter", Toast.LENGTH_LONG).show();
                }
                else {
                    int tampung;
                    if(sp_kategori.getSelectedItemPosition() ==19){
                        tampung = 15;
                    }
                    else if(sp_kategori.getSelectedItemPosition() ==0){
                        tampung = -1;
                    }
                    else{
                        if(sp_kategoriJabatan.getSelectedItemPosition() ==0){
                            tampung = 0;
                        }
                        else{
                            tampung = compared_position.get(sp_kategoriJabatan.getSelectedItem().toString());
                        }
                    }
                    try {
                        loadSearchVacancy(Integer.parseInt(userId), sp_kategori.getSelectedItemPosition(), tampung, sv_keyword.getQuery(), sp_lokasi.getSelectedItemPosition(), et_salary.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return v;
    }

    private void setCategorySpinner(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray kategoriJson = jsonObject.getJSONArray("data");
        JSONObject object;

        ArrayList<String> kategoriArray = new ArrayList<>();
        kategoriArray.add("--- Choose Category ---");
        for (int i = 0; i < kategoriJson.length(); i++) {
            object = kategoriJson.getJSONObject(i);
            kategoriArray.add(object.getString("category_name"));
        }

        ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, kategoriArray);
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_kategori.setAdapter(kategoriAdapter);
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
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, locationArray);
        locationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_lokasi.setAdapter(locationArrayAdapter);
    }

    private void loadPositionData(int categoryId) throws JSONException {
        System.out.println(categoryId);
        if(sp_kategoriJabatan.getSelectedItemPosition() != 0) {
            String URL = "https://springjava-1591708327203.azurewebsites.net/CategoryPosition/getCategoryPosition";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("category_id",categoryId);

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        if (status.equals("Success")) {
                            System.out.println("MASUK SUKSES");
                            positionArray.add("--- Choose position ---");
                            JSONArray positionJSON = response.getJSONArray("data");
                            JSONObject object;
                            for (int i=0;i<positionJSON.length();i++){
                                object = positionJSON.getJSONObject(i);
                                JSONObject object1 = object.getJSONObject("position");
                                positionArray.add(object1.getString("position_name"));
                                compared_position.put(object1.getString("position_name"), object1.getInt("position_id"));
                            }
                            ArrayAdapter<String> positionArrayAdapter = new ArrayAdapter<String> (getContext(), android.R.layout.simple_spinner_item, positionArray);
                            positionArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            sp_kategoriJabatan.setAdapter(positionArrayAdapter);
                        }
                        else {
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
            }){
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError {
                    final Map<String,String> params = new HashMap<String, String>();
                    params.put("Context-Type","application/json");
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void loadSearchVacancy(int userId, int category, int position, CharSequence keyword, int location, Editable salary) throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/Vacancy/searchVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", userId);
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
                    System.out.println(status);
                    if (status.equals("Success")) {
                        layoutSearchFilter.setVisibility(View.GONE);
                        layoutVacancyList.setVisibility(View.VISIBLE);
                        btn_filter.setVisibility(View.VISIBLE);

                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            SearchVacancy searchVacancy = new SearchVacancy();

                            searchVacancy.setVacancyId(object.getString("vac_id"));
                            searchVacancy.setVacancyTitle(object.getString("vac_title"));
                            searchVacancy.setVacancySalary(object.getInt("vac_salary"));

                            JSONObject object1 = object.getJSONObject("category");
                            searchVacancy.setVacancyCategory(object1.getString("category_name"));

                            JSONObject object2 = object.getJSONObject("position");
                            searchVacancy.setVacancyPosition(object2.getString("position_name"));

                            JSONObject object3 = object.getJSONObject("business");
                            searchVacancy.setVacancyCompanyName(object3.getString("bus_name"));
                            searchVacancy.setVacancyCompanyRating(object3.getString("rating"));
                            searchVacancy.setVacancyBusId(object3.getString("bus_id"));

                            JSONObject object4 = object3.getJSONObject("location");
                            searchVacancy.setVacancyLocation(object4.getString("location_name"));

                            JSONObject object5 = object3.getJSONObject("user");
                            searchVacancy.setVacancyStatus(object5.getString("user_status"));

                            searchVacancy.setFavoriteFlag(object.getString("favoriteFlag"));

                            searchVacancies.add(searchVacancy);
                        }
                        adapter.notifyDataSetChanged();
                        //viewDialog.hideDialog();
                    } else if(status.equals("Not Found")){
                        layoutSearchFilter.setVisibility(View.GONE);
                        layoutVacancyList.setVisibility(View.VISIBLE);
                        btn_filter.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Data not found", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Search failed, try again", Toast.LENGTH_LONG).show();
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

}
