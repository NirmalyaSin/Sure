package com.surefiz.screens.notifications.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.notifications.models.Notification;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter
        .NotificationAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Notification> arrayListNotifications = new ArrayList<Notification>();
    private OnNotificationClickListener mOnNotificationClickListener;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications,
                               OnNotificationClickListener clickListener) {
        this.mContext = context;
        this.arrayListNotifications = notifications;
        this.mOnNotificationClickListener = clickListener;
    }

    @NonNull
    @Override
    public NotificationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_notification, viewGroup, false);

        return new NotificationAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapterViewHolder holder, int position) {
        holder.textMessage.setText(arrayListNotifications.get(position).getNotificationText());
        switch (arrayListNotifications.get(position).getNotificationType()){
            case "4":
                holder.linearAcceptRejectButton.setVisibility(View.VISIBLE);
                break;
            default:
                holder.linearAcceptRejectButton.setVisibility(View.GONE);
                break;
        }

        holder.textDate.setText(arrayListNotifications.get(position).getNotificationDate());
        holder.textTime.setText(arrayListNotifications.get(position).getNotificationTime());
    }

    @Override
    public int getItemCount() {
        return arrayListNotifications.size();
    }

    public class NotificationAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textDate, textTime;
        LinearLayout linearAcceptRejectButton;
        Button btnReject, btnAccept;

        public NotificationAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            linearAcceptRejectButton = itemView.findViewById(R.id.linearAcceptRejectButton);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnNotificationClickListener.onViewClick(getAdapterPosition());
                }
            });

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnNotificationClickListener.onAccept(getAdapterPosition());
                }
            });

            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnNotificationClickListener.onReject(getAdapterPosition());
                }
            });
        }
    }

    public interface OnNotificationClickListener{
        void onViewClick(int position);
        void onAccept(int position);
        void onReject(int position);
    }
}
