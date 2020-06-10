package com.example.skripsi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavoriteFragment extends Fragment {

    private RecyclerView mList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Favorite> favoriteList;
    private RecyclerView.Adapter adapter;

    SessionManager sessionManager;

    ViewDialog viewDialog;

    TextView empty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite, container, false);

        viewDialog = new ViewDialog(getActivity());
        viewDialog.showDialog();

        empty = v.findViewById(R.id.empty);

        mList = v.findViewById(R.id.rv_listFavorite);

        favoriteList = new ArrayList<>();
        adapter = new FavoriteAdapter(v.getContext(), favoriteList);
        adapter.notifyDataSetChanged();

        linearLayoutManager = new LinearLayoutManager(v.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        sessionManager = new SessionManager(v.getContext());
        HashMap<String, String> user = sessionManager.getUserDetail();
        String userId = user.get(sessionManager.ID);

        try {
            loadFavorite(v.getContext(), userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private void loadFavorite(final Context context, String id) throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/FavoriteVacancy/getFavoriteVacancy";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", id);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    favoriteList.clear();
                    String status = response.getString("status");
                    System.out.println(status);
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Favorite favorite = new Favorite();

                            JSONObject object1 = object.getJSONObject("vac");

                            favorite.setVacId(object1.getString("vac_id"));

                            JSONObject object2 = object1.getJSONObject("category");
                            favorite.setCategory(object2.getString("category_name"));

                            favorite.setTitle(object1.getString("vac_title"));

                            JSONObject object3 = object1.getJSONObject("business");
                            favorite.setCompanyId(object3.getString("bus_id"));
                            favorite.setCompanyName(object3.getString("bus_name"));
                            favorite.setRating(object3.getString("rating"));

                            JSONObject object4 = object1.getJSONObject("location");
                            favorite.setLocation(object4.getString("location_name"));

                            favorite.setSalary(object1.getInt("vac_salary"));

                            JSONObject object5 = object1.getJSONObject("business");
                            JSONObject object6 = object5.getJSONObject("user");
                            favorite.setStatus(object6.getString("user_status"));

                            JSONObject object7 = object1.getJSONObject("position");
                            favorite.setPosition(object7.getString("position_name"));


                            favoriteList.add(favorite);
                        }
                        adapter.notifyDataSetChanged();
                        viewDialog.hideDialog();
                    } else {
                        empty.setVisibility(View.VISIBLE);
                        // Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                        viewDialog.hideDialog();
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

}
