package com.surefiz.screens.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.screens.chat.ChatConstant;
import com.surefiz.screens.chat.model.Conversation;
import com.surefiz.utils.ChatDateConverter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter
        .ChatAdapterViewHolder> {

    private static final Pattern UNICODE_HEX_PATTERN = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
    private static final Pattern UNICODE_OCT_PATTERN = Pattern.compile("\\\\([0-7]{3})");
    private Context mContext;
    private ArrayList<Conversation> arrayListConversation = new ArrayList<Conversation>();
    private OnChatScrollListener mOnChatScrollListener;
    private boolean firstLoading = true;
    private boolean shouldLoadMore = true;

    public ChatAdapter(Context context, ArrayList<Conversation> conversations,
                       OnChatScrollListener listener) {
        this.mContext = context;
        this.arrayListConversation = conversations;
        this.mOnChatScrollListener = listener;
    }

    public static String decodeFromNonLossyAscii(String original) {
        Matcher matcher = UNICODE_HEX_PATTERN.matcher(original);
        StringBuffer charBuffer = new StringBuffer(original.length());
        while (matcher.find()) {
            String match = matcher.group(1);
            char unicodeChar = (char) Integer.parseInt(match, 16);
            matcher.appendReplacement(charBuffer, Character.toString(unicodeChar));
        }
        matcher.appendTail(charBuffer);
        String parsedUnicode = charBuffer.toString();

        matcher = UNICODE_OCT_PATTERN.matcher(parsedUnicode);
        charBuffer = new StringBuffer(parsedUnicode.length());
        while (matcher.find()) {
            String match = matcher.group(1);
            char unicodeChar = (char) Integer.parseInt(match, 8);
            matcher.appendReplacement(charBuffer, Character.toString(unicodeChar));
        }
        matcher.appendTail(charBuffer);
        return charBuffer.toString();
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

        String processedMessage = "";
        try {
            processedMessage = decodeFromNonLossyAscii(arrayListConversation.get(position).getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            processedMessage = "";
        }

        switch (arrayListConversation.get(position).getMessageFrom()) {
            case ChatConstant.CHAT_FROM_SENDER:
                holder.rlMessageRight.setVisibility(View.VISIBLE);
                holder.rlMessageLeft.setVisibility(View.GONE);
                holder.texMessageRight.setText(processedMessage);
                holder.textDateTimeRight.setText(ChatDateConverter.DateConverter(arrayListConversation.get(position).getDateTime()));
                break;
            case ChatConstant.CHAT_FROM_RECEIVER:
                holder.rlMessageRight.setVisibility(View.GONE);
                holder.rlMessageLeft.setVisibility(View.VISIBLE);
                holder.textMessageLeft.setText(processedMessage);
                holder.textDateTimeLeft.setText(ChatDateConverter.DateConverter(arrayListConversation.get(position).getDateTime()));
                break;
        }
    }

    public void setLoadMore(boolean shouldLoadMore) {
        this.shouldLoadMore = shouldLoadMore;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ChatAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getAdapterPosition();

        if (firstLoading&& position == 0){
            firstLoading=false;
            return;
        }

        if (shouldLoadMore && position == 0) {
            mOnChatScrollListener.onScrollToTop(position);
        }
    }

    @Override
    public int getItemCount() {
        return arrayListConversation.size();
    }

    public interface OnChatScrollListener {
        void onScrollToTop(int scrollPosition);
    }

    public class ChatAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageLeft, texMessageRight, textDateTimeLeft, textDateTimeRight;
        LinearLayout rlMessageRight, rlMessageLeft;

        public ChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageLeft = itemView.findViewById(R.id.textMessageLeft);
            texMessageRight = itemView.findViewById(R.id.texMessageRight);
            textDateTimeLeft = itemView.findViewById(R.id.textDateTimeLeft);
            textDateTimeRight = itemView.findViewById(R.id.textDateTimeRight);

            rlMessageLeft = itemView.findViewById(R.id.rlMessageLeft);
            rlMessageRight = itemView.findViewById(R.id.rlMessageRight);
        }
    }
}
