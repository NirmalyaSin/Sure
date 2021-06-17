package com.surefiz.screens.accountability.adapter;

import android.content.Context;

import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

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
        if (!image.equals("") && !image.equals("null")) {
            Picasso.with(mContext)
                    .load(image)
                    .fit()
                    .placeholder(R.drawable.prof_img_placeholder)
                    .error(R.drawable.prof_img_placeholder)
                    .into(holder.imageUserProfile);
        }

        if (arrayListCircleUser.get(position).getOnlineStatus().equals("1")) {
            holder.imageOnlineOffline.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_dot_online));
        }

        DrawableCompat.setTint(holder.ivViewOptions.getDrawable(), mContext.getResources().getColor(R.color.whiteColor));
    }

    @Override
    public int getItemCount() {
        return arrayListCircleUser.size();
    }

    public interface OnCircleViewClickListener {
        void onViewClick(int position);

        void onSendMessageClick(int position);

        void onPerformanceClick(int position);

        void onRemoveClick(int position);

        void onImageClick(int position);
    }

    public class CircleUserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageUserProfile;
        TextView textUserName;
        Button btnPerformance, btnRemove;
        ImageView imgSendMessage, imageOnlineOffline,ivViewOptions;

        public CircleUserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserProfile = itemView.findViewById(R.id.imageUserProfile);
            imageOnlineOffline = itemView.findViewById(R.id.imageOnlineOffline);
            textUserName = itemView.findViewById(R.id.textUserName);
            ivViewOptions = itemView.findViewById(R.id.ivViewOptions);
            btnPerformance = itemView.findViewById(R.id.btnPerformance);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            imgSendMessage = itemView.findViewById(R.id.imgSendMessage);

            btnPerformance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCircleViewClickListener.onPerformanceClick(getAdapterPosition());

                }
            });

            imageUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCircleViewClickListener.onImageClick(getAdapterPosition());

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

            ivViewOptions.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, ivViewOptions, Gravity.END|Gravity.TOP,0,R.style.PopupMenuWindow);

                    popup.inflate(R.menu.custom_menu);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menuPerformance:
                                    mOnCircleViewClickListener.onPerformanceClick(getAdapterPosition());
                                    return true;
                                case R.id.menuUnfriend:
                                    mOnCircleViewClickListener.onRemoveClick(getAdapterPosition());
                                    return true;
                            }
                            return false;
                        }
                    });

                    popup.show();
                }
            });

        }
    }
}