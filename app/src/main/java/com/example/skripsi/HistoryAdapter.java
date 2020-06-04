package com.example.skripsi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    private Context context;
    private List<History> list;

    public HistoryAdapter(Context context, List<History> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.history_item, viewGroup, false);
        return new HistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final History history = list.get(i);

        viewHolder.tv_category.setText(history.getCategory());
        viewHolder.pembatas.setText("-");
        viewHolder.tv_position.setText(history.getPosition());
        viewHolder.tv_title.setText(history.getTitle());
        viewHolder.tv_companyName.setText(history.getCompanyName());
        viewHolder.tv_location.setText(history.getLocation());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        viewHolder.tv_salary.setText(formatRupiah.format((double)history.getSalary()));
        viewHolder.tv_rating.setText(history.getRating());
        viewHolder.tv_status.setText(history.getStatus());

        viewHolder.btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PopUpRating.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("VACANCY_ID", history.getVacId());
                intent.putExtra("BUSINESS_ID", history.getVacId());
                view.getContext().startActivity(intent);
            }
        });

        String flag = history.getFavoriteFlag();
        System.out.println(flag);

        if (flag.equals("Y")){
            viewHolder.btn_rate.setVisibility(View.GONE);
            viewHolder.rb_ratingDariUser.setVisibility(View.VISIBLE);
        }else {
            viewHolder.btn_rate.setVisibility(View.VISIBLE);
            viewHolder.rb_ratingDariUser.setVisibility(View.GONE);
        };

        if(history.getStatus().equals("PENDING")){
            viewHolder.tv_status.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        }
        else if(history.getStatus().equals("ACCEPTED")){
            viewHolder.tv_status.setTextColor(ContextCompat.getColor(context, R.color.greenA700));
            viewHolder.btn_rate.setVisibility(View.VISIBLE);
        }
        else if(history.getStatus().equals("REJECTED")){
            viewHolder.tv_status.setTextColor(ContextCompat.getColor(context, R.color.colorGrapeFruitDark));
        }

        viewHolder.img_bintang.setImageResource(R.drawable.star);

        viewHolder.layout_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DetailVacancy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("VACANCY_ID", history.getVacId());
                intent.putExtra("BUSINESS_ID", history.getBusId());
                intent.putExtra("FLAG", history.getFavoriteFlag());
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_category, pembatas, tv_position, tv_title, tv_companyName, tv_location, tv_salary, tv_rating, tv_status;

        ImageView img_company, img_bintang;

        Button btn_rate;

        RatingBar rb_ratingDariUser;

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
            img_bintang = itemView.findViewById(R.id.img_bintang);

            btn_rate = itemView.findViewById(R.id.btn_rate);

            rb_ratingDariUser = itemView.findViewById(R.id.rb_ratingDariUser);

            layout_data = itemView.findViewById(R.id.layout_data);
        }
    }
}
