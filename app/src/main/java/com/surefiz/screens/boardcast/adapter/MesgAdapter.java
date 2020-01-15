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
import com.surefiz.screens.chat.adapter.ChatAdapter;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MessagDateConverter;

import java.util.ArrayList;

public class MesgAdapter extends RecyclerView.Adapter<MesgAdapter
        .ChatAdapterViewHolder> {

    private Context mContext;
    private ArrayList<BroadcastItem> arrayListConversation;
    private boolean firstLoading = true;
    private OnChatScrollListener onChatScrollListener;
    private boolean shouldLoadMore=true;

    public MesgAdapter(Context context, ArrayList<BroadcastItem> conversations,OnChatScrollListener onChatScrollListener) {
        this.mContext = context;
        this.arrayListConversation = conversations;
        this.onChatScrollListener = onChatScrollListener;
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


        if (LoginShared.getRegistrationDataModel(mContext).getData().getUser().get(0).getUserId().equals(arrayListConversation.get(position).getSenderId())){
            holder.tvUserName.setText("You");
        }else {
            holder.tvUserName.setText(arrayListConversation.get(position).getName());
        }
        holder.textMessageLeft.setText(arrayListConversation.get(position).getMessage());
        holder.textDateTimeLeft.setText(MessagDateConverter.boardDateConverter(mContext,arrayListConversation.get(position).getDateTime()));
    }

    public void setLoadMore(boolean shouldLoadMore) {
        this.shouldLoadMore = shouldLoadMore;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MesgAdapter.ChatAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getAdapterPosition();
        if (shouldLoadMore && position == 0) {
            onChatScrollListener.onScrollToTop(position);
        }
    }

    public interface OnChatScrollListener {
        void onScrollToTop(int scrollPosition);
    }



    @Override
    public int getItemCount() {
        return arrayListConversation.size();
    }

    public class ChatAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageLeft,textDateTimeLeft,tvUserName;
        RelativeLayout rlMessageRight, rlMessageLeft;

        public ChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            textMessageLeft = itemView.findViewById(R.id.textMessageLeft);
            textDateTimeLeft = itemView.findViewById(R.id.textDateTimeLeft);
        }
    }

}
