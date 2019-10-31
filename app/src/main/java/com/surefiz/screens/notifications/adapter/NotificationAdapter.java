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
import com.surefiz.utils.MessagDateConverter;

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
        switch (arrayListNotifications.get(position).getNotificationType()) {
            case "4":
                holder.linearAcceptRejectButton.setVisibility(View.VISIBLE);
                break;
            default:
                holder.linearAcceptRejectButton.setVisibility(View.GONE);
                break;
        }


        //System.out.println("friendTime: " + MessagDateConverter.DateConverterForNotification(arrayListNotifications.get(position).getNotificationDate(), arrayListNotifications.get(position).getNotificationTime(),arrayListNotifications.get(position).getNotificationType()));
        String[] strings = MessagDateConverter.DateConverterForNotification(arrayListNotifications.get(position).getNotificationDate(), arrayListNotifications.get(position).getNotificationTime(), arrayListNotifications.get(position).getNotificationType()).split(",");
        holder.textDate.setText(strings != null ? strings[0] : arrayListNotifications.get(position).getNotificationDate());
        holder.textTime.setText(" , " + strings != null ? strings[1].trim() : arrayListNotifications.get(position).getNotificationTime());
    }

    @Override
    public int getItemCount() {
        return arrayListNotifications.size();
    }

    public interface OnNotificationClickListener {
        void onViewClick(int position);

        void onAccept(int position);

        void onReject(int position);
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

            itemView.setOnClickListener(v -> mOnNotificationClickListener.onViewClick(getAdapterPosition()));

            btnAccept.setOnClickListener(v -> mOnNotificationClickListener.onAccept(getAdapterPosition()));

            btnReject.setOnClickListener(v -> mOnNotificationClickListener.onReject(getAdapterPosition()));
        }
    }
}
