package com.surefiz.screens.reminders.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.surefiz.R;
import com.surefiz.screens.reminders.model.User;
import com.surefiz.utils.ChatDateConverter;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context mContext;
    private ArrayList<User> arrayListReminder = new ArrayList<User>();
    private OnReminderListener mOnReminderListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

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
        holder.textTime.setText(ChatDateConverter.Hours12(arrayListReminder.get(position).getTime()));


        viewBinderHelper.bind(holder.swipeRevealLayout, arrayListReminder.get(position).getId());
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public int getItemCount() {
        return arrayListReminder.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView textReminderMessage, textDate, textTime;
        ImageView imageEditReminder;
        public FrameLayout frameDelete;
        public LinearLayout viewForeground;
        public SwipeRevealLayout swipeRevealLayout;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textReminderMessage = itemView.findViewById(R.id.textReminderMessage);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            imageEditReminder = itemView.findViewById(R.id.imageEditReminder);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            frameDelete = itemView.findViewById(R.id.frameDelete);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);

            imageEditReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnReminderListener.onEditEdit(getAdapterPosition());
                }
            });

            viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnReminderListener.onReminderSelected(getAdapterPosition());
                }
            });

            frameDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnReminderListener.onRemiderDeleted(getAdapterPosition());
                }
            });
        }
    }

    public interface OnReminderListener{
        void onReminderSelected(int position);
        void onEditEdit(int position);
        void onRemiderDeleted(int position);
    }

    public void saveStates(Bundle outState) {
        viewBinderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        viewBinderHelper.restoreStates(inState);
    }
}
