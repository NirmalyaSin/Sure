package com.surefiz.screens.reminders.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.reminders.model.User;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context mContext;
    private ArrayList<User> arrayListReminder = new ArrayList<User>();
    private OnReminderListener mOnReminderListener;

    public ReminderAdapter(Context context, ArrayList<User> reminders,
                           OnReminderListener clickListener) {
        this.mContext = context;
        this.arrayListReminder = reminders;
        this.mOnReminderListener = clickListener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_reminder, viewGroup, false);

        return new ReminderViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
    //    Log.e("@@holder: ", arrayListReminder.get(position).toString());
        holder.textReminderMessage.setText(arrayListReminder.get(position).getMessage());
        holder.textDate.setText(arrayListReminder.get(position).getDate());
        holder.textTime.setText(arrayListReminder.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return arrayListReminder.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView textReminderMessage, textDate, textTime;
        ImageView imageEditReminder;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textReminderMessage = itemView.findViewById(R.id.textReminderMessage);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            imageEditReminder = itemView.findViewById(R.id.imageEditReminder);

            imageEditReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnReminderListener.onEditEdit(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnReminderListener.onReminderSelected(getAdapterPosition());
                }
            });
        }
    }

    public interface OnReminderListener{
        void onReminderSelected(int position);
        void onEditEdit(int position);
    }
}
