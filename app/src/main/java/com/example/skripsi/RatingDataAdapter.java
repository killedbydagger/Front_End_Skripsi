package com.example.skripsi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class RatingDataAdapter extends RecyclerView.Adapter<RatingDataAdapter.ViewHolder> {

    private Context context;
    private List<RatingData> list;

    public RatingDataAdapter(Context context, List<RatingData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RatingDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.rating_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingDataAdapter.ViewHolder viewHolder, int i) {
        RatingData ratingData = list.get(i);

        viewHolder.tv_name.setText(ratingData.getUserName());

        String s = ratingData.getValue();
        viewHolder.ratingBarValue.setRating(Float.parseFloat(s));

        viewHolder.tv_comment.setText(ratingData.getComment());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name, tv_comment;

        public RatingBar ratingBarValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            ratingBarValue = itemView.findViewById(R.id.ratingBarValue);
        }
    }
}
