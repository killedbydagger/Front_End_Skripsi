package com.example.skripsi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context context;
    private List<Favorite> list;

    public FavoriteAdapter(Context context, List<Favorite> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.favorite_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Favorite favorite = list.get(i);

        viewHolder.tv_category.setText(favorite.getCategory());
        viewHolder.pembatas.setText("-");
        viewHolder.tv_position.setText(favorite.getPosition());
        viewHolder.tv_title.setText(favorite.getTitle());
        viewHolder.tv_companyName.setText(favorite.getCompanyName());
        viewHolder.tv_location.setText(favorite.getLocation());

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        viewHolder.tv_salary.setText(formatRupiah.format((double)favorite.getSalary()));

        viewHolder.tv_rating.setText(favorite.getRating());
        viewHolder.tv_status.setText(favorite.getStatus());

        viewHolder.img_favorite.setImageResource(R.drawable.icon_favorite_red);
        viewHolder.img_bintang.setImageResource(R.drawable.star);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_category, pembatas, tv_position, tv_title, tv_companyName, tv_location, tv_salary, tv_rating, tv_status;

        ImageView img_company, img_favorite, img_bintang;

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
        }
    }


}
