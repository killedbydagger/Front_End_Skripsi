package com.example.skripsi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SearchVacancyAdapter extends RecyclerView.Adapter<SearchVacancyAdapter.ViewHolder> {

    private Context context;
    private List<SearchVacancy> list;

    public SearchVacancyAdapter(Context context, List<SearchVacancy> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.applicantlist_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SearchVacancy searchVacancy = list.get(i);

        viewHolder.tv_category.setText(searchVacancy.getVacancyCategory());
        viewHolder.pembatas.setText("-");
        viewHolder.tv_position.setText(searchVacancy.getVacancyPosition());
        viewHolder.tv_title.setText(searchVacancy.getVacancyTitle());
        viewHolder.tv_companyName.setText(searchVacancy.getVacancyCompanyName());
        viewHolder.tv_location.setText(searchVacancy.getVacancyLocation());
        viewHolder.tv_salary.setText(searchVacancy.getVacancySalary());
        viewHolder.tv_rating.setText("rating");
        viewHolder.tv_status.setText(searchVacancy.getVacancyStatus());

        viewHolder.img_favorite.setImageResource(R.drawable.icon_favorite_red);
        viewHolder.img_bintang.setImageResource(R.drawable.star);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
