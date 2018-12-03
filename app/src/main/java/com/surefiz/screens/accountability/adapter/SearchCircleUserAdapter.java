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
import com.surefiz.screens.accountability.RequestState;
import com.surefiz.screens.accountability.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchCircleUserAdapter extends RecyclerView.Adapter<SearchCircleUserAdapter
        .SearchCircleUserViewHolder> {

    private Context mContext;
    private ArrayList<User> arrayListCircleUser = new ArrayList<User>();
    private OnSearchCircleUserClickListener mOnSearchCircleUserClickListener;

    public SearchCircleUserAdapter(Context context, ArrayList<User> users, OnSearchCircleUserClickListener clickListener) {
        this.mContext = context;
        this.arrayListCircleUser = users;
        this.mOnSearchCircleUserClickListener = clickListener;
    }

    @NonNull
    @Override
    public SearchCircleUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_search_accountability, viewGroup, false);


        return new SearchCircleUserViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCircleUserViewHolder holder, int position) {
        holder.textUserName.setText(arrayListCircleUser.get(position).getUser_name());
        Log.d("@@ListUser : ", arrayListCircleUser.get(position).toString());

        String image = arrayListCircleUser.get(position).getUser_image();
        if(!image.equals("") && !image.equals("null")){
            Picasso.with(mContext)
                    .load(image)
                    .fit()
                    .placeholder(R.drawable.user_black)
                    .error(R.drawable.user_black)
                    .into(holder.imageUserProfile);
        }

        switch (arrayListCircleUser.get(position).getConnectionStatus()){
            case RequestState.STATUS_IDLE:
                holder.btnAddToCircle.setVisibility(View.VISIBLE);
                holder.btnCancelRequest.setVisibility(View.GONE);
                holder.btnAccepted.setVisibility(View.GONE);
                holder.btnRequestSent.setVisibility(View.GONE);
                break;

            case RequestState.STATUS_REQUEST_SENT:
                holder.btnAddToCircle.setVisibility(View.GONE);
                holder.btnCancelRequest.setVisibility(View.VISIBLE);
                holder.btnAccepted.setVisibility(View.GONE);
                holder.btnRequestSent.setVisibility(View.VISIBLE);
                break;

            case RequestState.STATUS_REQUEST_ACCEPTED:
                holder.btnAddToCircle.setVisibility(View.GONE);
                holder.btnCancelRequest.setVisibility(View.GONE);
                holder.btnAccepted.setVisibility(View.VISIBLE);
                holder.btnRequestSent.setVisibility(View.GONE);
                break;
        }

     //   Picasso.with(mContext).load(arrayListCircleUser.get(position).g)
    }

    @Override
    public int getItemCount() {
        return arrayListCircleUser.size();
    }

    public class SearchCircleUserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageUserProfile;
        TextView textUserName;
        Button btnAddToCircle, btnRequestSent, btnCancelRequest, btnAccepted;

        public SearchCircleUserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserProfile = itemView.findViewById(R.id.imageUserProfile);
            textUserName = itemView.findViewById(R.id.textUserName);
            btnRequestSent = itemView.findViewById(R.id.btnRequestSent);
            btnAddToCircle = itemView.findViewById(R.id.btnAddToCircle);
            btnCancelRequest = itemView.findViewById(R.id.btnCancelRequest);
            btnAccepted = itemView.findViewById(R.id.btnAccepted);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnSearchCircleUserClickListener.onViewClick(getAdapterPosition());
                }
            });

            btnAddToCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnSearchCircleUserClickListener.onAddToCircle(getAdapterPosition());
                }
            });

            btnCancelRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnSearchCircleUserClickListener.onCancelRequest(getAdapterPosition());
                }
            });
        }
    }

    public interface OnSearchCircleUserClickListener{
        void onViewClick(int position);
        void onCancelRequest(int position);
        void onAddToCircle(int position);
    }
}
