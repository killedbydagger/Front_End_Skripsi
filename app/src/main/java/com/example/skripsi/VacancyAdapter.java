package com.example.skripsi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Vacancy vacancy = list.get(position);

        viewHolder.textTitle.setText(vacancy.getTitle());
        viewHolder.textCategory.setText(vacancy.getCategory());
        viewHolder.textLocation.setText(vacancy.getLocationName());
        viewHolder.textSalary.setText(vacancy.getSalary());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle, textCategory, textSalary, textLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.tv_title);
            textCategory = itemView.findViewById(R.id.tv_category);
            textSalary = itemView.findViewById(R.id.tv_salary);
            textLocation = itemView.findViewById(R.id.tv_location);
        }
    }
}
