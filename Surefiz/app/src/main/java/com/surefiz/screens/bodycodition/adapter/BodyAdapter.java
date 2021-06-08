package com.surefiz.screens.bodycodition.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.screens.bodycodition.BodyActivity;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.utils.OnitemClickListner;

import java.util.ArrayList;
import java.util.List;


public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.ViewHolder> {
    BodyActivity genresActivity;
    List<BodyItem> list;
    OnitemClickListner onitemClickListner;
    public BodyAdapter(BodyActivity genresActivity, ArrayList<BodyItem> list, OnitemClickListner onitemClickListner) {
        this.genresActivity=genresActivity;
        this.list=list;
        this.onitemClickListner=onitemClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate( R.layout.genres_view,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.text.setText(list.get(position).getName());

        if(list.get(position).isSelection()){
            holder.checkbox.setImageResource(R.drawable.tick);
        }else{
            holder.checkbox.setImageResource(android.R.color.transparent);

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView checkbox;
        protected TextView text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text=itemView.findViewById(R.id.text);
            checkbox=itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onitemClickListner.onClick(itemView,getAdapterPosition());
                }
            });
        }
    }
}
