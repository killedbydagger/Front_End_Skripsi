package com.example.skripsi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ListApplicantAdapter extends RecyclerView.Adapter<ListApplicantAdapter.ViewHolder> {

    private Context context;
    private List<ListApplicant> list;

    public ListApplicantAdapter(Context context, List<ListApplicant> list) {
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
        ListApplicant listApplicant = list.get(i);

        viewHolder.textName.setText(listApplicant.getName());
        viewHolder.textEmail.setText(listApplicant.getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textName, textEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.tv_applicantName);
            textEmail = itemView.findViewById(R.id.tv_applicantPhone);
        }
    }
}
