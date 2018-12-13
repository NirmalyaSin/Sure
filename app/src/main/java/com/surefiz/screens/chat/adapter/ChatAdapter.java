package com.surefiz.screens.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.chat.ChatConstant;
import com.surefiz.screens.chat.model.Conversation;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter
        .ChatAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Conversation> arrayListConversation = new ArrayList<Conversation>();
    private OnChatScrollListener mOnChatScrollListener;
    private boolean firstLoading = true;

    public ChatAdapter(Context context, ArrayList<Conversation> conversations,
                       OnChatScrollListener listener) {
        this.mContext = context;
        this.arrayListConversation = conversations;
        this.mOnChatScrollListener = listener;
    }

    @NonNull
    @Override
    public ChatAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_chat, viewGroup, false);

        return new ChatAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapterViewHolder holder, int position) {
        if (position == 0){
            Log.d("@@getItemCount : ", "fist-loading = " + firstLoading);
            firstLoading = true;
        }

        switch (arrayListConversation.get(position).getMessageFrom()){
            case ChatConstant.CHAT_FROM_SENDER:
                holder.texMessageRight.setVisibility(View.VISIBLE);
                holder.textMessageLeft.setVisibility(View.GONE);
                holder.texMessageRight.setText(arrayListConversation.get(position).getMessage());
                break;
            case ChatConstant.CHAT_FROM_RECEIVER:
                holder.texMessageRight.setVisibility(View.GONE);
                holder.textMessageLeft.setVisibility(View.VISIBLE);
                holder.textMessageLeft.setText(arrayListConversation.get(position).getMessage());
                break;
        }
    }



    @Override
    public void onViewAttachedToWindow(@NonNull ChatAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getAdapterPosition();
        if(firstLoading){
            firstLoading = false;
        }else {
            Log.d("@@Scrolling : ", "New-Pos = " +position);
            if(position==0){
                mOnChatScrollListener.onScrollToTop(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayListConversation.size();
    }

    public class ChatAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageLeft, texMessageRight;

        public ChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageLeft = itemView.findViewById(R.id.textMessageLeft);
            texMessageRight = itemView.findViewById(R.id.texMessageRight);
        }
    }

    public interface OnChatScrollListener{
        void onScrollToTop(int scrollPosition);
    }
}