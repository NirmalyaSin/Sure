package com.surefiz.screens.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.chat.ChatConstant;
import com.surefiz.screens.chat.model.Conversation;
import com.surefiz.utils.ChatDateConverter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (position == 0) {
            Log.d("@@getItemCount : ", "fist-loading = " + firstLoading);
            firstLoading = true;
        }

        String processedMessage = "";
        try {
            //processedMessage = URLDecoder.decode(arrayListConversation.get(position).getMessage(), "utf-8");
            processedMessage = decodeFromNonLossyAscii(arrayListConversation.get(position).getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            processedMessage = "";
        }

        switch (arrayListConversation.get(position).getMessageFrom()) {
            case ChatConstant.CHAT_FROM_SENDER:
                holder.rlMessageRight.setVisibility(View.VISIBLE);
                holder.rlMessageLeft.setVisibility(View.GONE);
                //holder.texMessageRight.setText(arrayListConversation.get(position).getMessage());
                holder.texMessageRight.setText(processedMessage);
                holder.textDateTimeRight.setText(ChatDateConverter.DateConverter(arrayListConversation.get(position).getDateTime()));
                break;
            case ChatConstant.CHAT_FROM_RECEIVER:
                holder.rlMessageRight.setVisibility(View.GONE);
                holder.rlMessageLeft.setVisibility(View.VISIBLE);
                //holder.textMessageLeft.setText(arrayListConversation.get(position).getMessage());
                holder.textMessageLeft.setText(processedMessage);
                holder.textDateTimeLeft.setText(ChatDateConverter.DateConverter(arrayListConversation.get(position).getDateTime()));
                break;
        }
    }


    @Override
    public void onViewAttachedToWindow(@NonNull ChatAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getAdapterPosition();
        if (firstLoading) {
            firstLoading = false;
        } else {
            Log.d("@@Scrolling : ", "New-Pos = " + position);
            if (position == 0) {
                mOnChatScrollListener.onScrollToTop(position);
            }
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


    private static final Pattern UNICODE_HEX_PATTERN = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
    private static final Pattern UNICODE_OCT_PATTERN = Pattern.compile("\\\\([0-7]{3})");

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
}
