package com.surefiz.screens.boardcast.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.boardcast.model.BroadcastItem;
import com.surefiz.screens.chat.ChatConstant;
import com.surefiz.utils.MessagDateConverter;

import java.util.ArrayList;

public class MesgAdapter extends RecyclerView.Adapter<MesgAdapter
        .ChatAdapterViewHolder> {

    private Context mContext;
    private ArrayList<BroadcastItem> arrayListConversation;
    private boolean firstLoading = true;

    public MesgAdapter(Context context, ArrayList<BroadcastItem> conversations) {
        this.mContext = context;
        this.arrayListConversation = conversations;
    }

    @NonNull
    @Override
    public ChatAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_item_view, viewGroup, false);

        return new ChatAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapterViewHolder holder, int position) {
        if (position == 0){
            Log.d("@@getItemCount : ", "fist-loading = " + firstLoading);
            firstLoading = true;
        }


        holder.textMessageLeft.setText(arrayListConversation.get(position).getMessage());
        holder.textDateTimeLeft.setText(MessagDateConverter.DateConverter(arrayListConversation.get(position).getDateTime()));

        /*switch (arrayListConversation.get(position).getMessageFrom()){
            case ChatConstant.CHAT_FROM_SENDER:
                holder.textMessageLeft.setText(arrayListConversation.get(position).getMessage());
                holder.textDateTimeLeft.setText(MessagDateConverter.DateConverter(arrayListConversation.get(position).getDateTime()));
                break;
        }*/
    }



    @Override
    public int getItemCount() {
        return arrayListConversation.size();
    }

    public class ChatAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageLeft, texMessageRight,textDateTimeLeft,textDateTimeRight;
        RelativeLayout rlMessageRight, rlMessageLeft;

        public ChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageLeft = itemView.findViewById(R.id.textMessageLeft);
            textDateTimeLeft = itemView.findViewById(R.id.textDateTimeLeft);
        }
    }

}
