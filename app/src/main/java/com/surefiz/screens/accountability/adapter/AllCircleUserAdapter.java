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
        String image = arrayListCircleUser.get(position).getUser_search_image();
        if(!image.equals("") && !image.equals("null")){
            Picasso.with(mContext)
                    .load(image)
                    .fit()
                    .placeholder(R.drawable.prof_img_placeholder)
                    .error(R.drawable.prof_img_placeholder)
                    .into(holder.imageUserProfile);
        }

        if(arrayListCircleUser.get(position).getOnlineStatus().equals("1")){
            holder.imageOnlineOffline.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_dot_online));
        }
    }

    @Override
    public int getItemCount() {
        return arrayListCircleUser.size();
    }

    public class CircleUserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageUserProfile;
        TextView textUserName;
        Button btnPerformance,btnRemove;
        ImageView imgSendMessage, imageOnlineOffline;

        public CircleUserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserProfile = itemView.findViewById(R.id.imageUserProfile);
            imageOnlineOffline = itemView.findViewById(R.id.imageOnlineOffline);
            textUserName = itemView.findViewById(R.id.textUserName);
            btnPerformance = itemView.findViewById(R.id.btnPerformance);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            imgSendMessage = itemView.findViewById(R.id.imgSendMessage);

            btnPerformance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCircleViewClickListener.onPerformanceClick(getAdapterPosition());

                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCircleViewClickListener.onRemoveClick(getAdapterPosition());

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
        void onRemoveClick(int position);
    }
}
