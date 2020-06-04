package com.example.skripsi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchVacancyAdapter extends RecyclerView.Adapter<SearchVacancyAdapter.ViewHolder> {

    private Context context;
    private List<SearchVacancy> list;

    SessionManager sessionManager;


    public SearchVacancyAdapter(Context context, List<SearchVacancy> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.searchvacancy_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final SearchVacancy searchVacancy = list.get(i);

        viewHolder.tv_category.setText(searchVacancy.getVacancyCategory());
        viewHolder.pembatas.setText("-");
        viewHolder.tv_position.setText(searchVacancy.getVacancyPosition());
        viewHolder.tv_title.setText(searchVacancy.getVacancyTitle());
        viewHolder.tv_companyName.setText(searchVacancy.getVacancyCompanyName());
        viewHolder.tv_location.setText(searchVacancy.getVacancyLocation());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        viewHolder.tv_salary.setText(formatRupiah.format((double)searchVacancy.getVacancySalary()));
        viewHolder.tv_rating.setText(searchVacancy.getVacancyCompanyRating());
        viewHolder.tv_status.setText(searchVacancy.getVacancyStatus());

        String flag = searchVacancy.getFavoriteFlag();

        if(flag.equals("Y")){
            viewHolder.img_favorite.setImageResource(R.drawable.icon_favorite_red);
        }
        else{
            viewHolder.img_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        viewHolder.img_bintang.setImageResource(R.drawable.star);

        viewHolder.layout_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DetailVacancy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("VACANCY_ID", searchVacancy.getVacancyId());
                intent.putExtra("BUSINESS_ID", searchVacancy.getVacancyBusId());
                intent.putExtra("FLAG", searchVacancy.getFavoriteFlag());
                v.getContext().startActivity(intent);
            }
        });

        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String userId = user.get(sessionManager.ID);

        viewHolder.img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchVacancy.getFavoriteFlag().equals("Y")){
                    try {
                        unFavorite(userId, searchVacancy.getVacancyId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    searchVacancy.setFavoriteFlag("N");
                    viewHolder.img_favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
                else{
                    try {
                        favoriteVacancy(userId, searchVacancy.getVacancyId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    searchVacancy.setFavoriteFlag("Y");
                    viewHolder.img_favorite.setImageResource(R.drawable.icon_favorite_red);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_category, pembatas, tv_position, tv_title, tv_companyName, tv_location, tv_salary, tv_rating, tv_status;

        ImageView img_company, img_favorite, img_bintang;

        LinearLayout layout_data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_category = itemView.findViewById(R.id.tv_category);
            pembatas = itemView.findViewById(R.id.pembatas);
            tv_position = itemView.findViewById(R.id.tv_position);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_companyName = itemView.findViewById(R.id.tv_companyName);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_salary = itemView.findViewById(R.id.tv_salary);
            tv_rating = itemView.findViewById(R.id.tv_rating);
            tv_status = itemView.findViewById(R.id.tv_status);

            img_company = itemView.findViewById(R.id.img_company);
            img_favorite = itemView.findViewById(R.id.img_favorite);
            img_bintang = itemView.findViewById(R.id.img_bintang);

            layout_data = itemView.findViewById(R.id.layout_data);
        }
    }

    private void favoriteVacancy(String userId, String vacId) throws JSONException {
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
                        Toast.makeText(context, "Favorite vacancy success", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context, "Favorite vacancy failed", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, "Unfavorite vacancy success", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context, "Unfavorite vacancy failed", Toast.LENGTH_LONG).show();
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
