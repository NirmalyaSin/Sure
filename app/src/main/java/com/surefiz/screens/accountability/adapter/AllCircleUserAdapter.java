package com.surefiz.screens.accountability.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.surefiz.R;
import com.surefiz.screens.accountability.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllCircleUserAdapter extends RecyclerView.Adapter<AllCircleUserAdapter.CircleUserViewHolder> {

    private Context mContext;
    private ArrayList<User> arrayListCircleUser = new ArrayList<User>();
    private OnCircleViewClickListener mOnCircleViewClickListener;

    public AllCircleUserAdapter(Context context, ArrayList<User> users, OnCircleViewClickListener clickListener) {
        this.mContext = context;
        this.arrayListCircleUser = users;
        this.mOnCircleViewClickListener = clickListener;
    }

    @NonNull
    @Override
    public CircleUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_user_accountability, viewGroup, false);


        return new CircleUserViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull CircleUserViewHolder holder, int position) {
    //    Log.e("@@holder: ", arrayListCircleUser.get(position).toString());
        holder.textUserName.setText(arrayListCircleUser.get(position).getUser_name());
        String image = arrayListCircleUser.get(position).getUser_image();
        if(!image.equals("") && !image.equals("null")){
            Picasso.with(mContext)
                    .load(image)
                    .fit()
                    .placeholder(R.drawable.user_black)
                    .error(R.drawable.user_black)
                    .into(holder.imageUserProfile);
        }
    }

    @Override
    public int getItemCount() {
        return arrayListCircleUser.size();
    }

    public class CircleUserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageUserProfile;
        TextView textUserName;
        Button btnPerformance;
        ImageView imgSendMessage;

        public CircleUserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserProfile = itemView.findViewById(R.id.imageUserProfile);
            textUserName = itemView.findViewById(R.id.textUserName);
            btnPerformance = itemView.findViewById(R.id.btnPerformance);
            imgSendMessage = itemView.findViewById(R.id.imgSendMessage);

            btnPerformance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCircleViewClickListener.onPerformanceClick(getAdapterPosition());

                }
            });

            imgSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCircleViewClickListener.onSendMessageClick(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCircleViewClickListener.onViewClick(getAdapterPosition());
                }
            });

        }
    }

    public interface OnCircleViewClickListener{
        void onViewClick(int position);
        void onSendMessageClick(int position);
        void onPerformanceClick(int position);
    }
}
