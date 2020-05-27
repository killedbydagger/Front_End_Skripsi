package com.example.skripsi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

public class VacancyAdapter extends RecyclerView.Adapter<VacancyAdapter.ViewHolder>{

    private Context context;
    private List<Vacancy> list;

    public VacancyAdapter(Context context, List<Vacancy> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.vacancy_item, viewGroup ,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Vacancy vacancy = list.get(position);

        viewHolder.textTitle.setText(vacancy.getTitle());
        viewHolder.textCategory.setText(vacancy.getCategory());
        viewHolder.textLocation.setText(vacancy.getLocationName());
        viewHolder.textSalary.setText(vacancy.getSalary());
        viewHolder.imgEdit.setImageResource(R.drawable.icon_edit);

        viewHolder.layout_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), ApplicantList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("VACANCY_ID", vacancy.getId());
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), EditVacancy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("VACANCY_ID", vacancy.getId());
                intent.putExtra("LOCATION_ID",vacancy.getLocationId());
                intent.putExtra("CATEGORY_ID",vacancy.getCategoryId());
                intent.putExtra("TITLE",vacancy.getTitle());
                intent.putExtra("SALARY",vacancy.getSalary());
                intent.putExtra("DESCRIPTION",vacancy.getDescription());
                v.getContext().startActivity(intent);
            }
        });
        viewHolder.imgDelete.setImageResource(R.drawable.icon_dusbin);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle, textCategory, textSalary, textLocation;
        public ImageView imgEdit,imgDelete;
        public LinearLayout layout_data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.tv_title);
            textCategory = itemView.findViewById(R.id.tv_category);
            textSalary = itemView.findViewById(R.id.tv_salary);
            textLocation = itemView.findViewById(R.id.tv_location);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            layout_data = itemView.findViewById(R.id.layout_data);
        }
    }
}
