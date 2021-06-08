package com.surefiz.screens.chat.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChatListResponse {
    @SerializedName("status")
    Integer status;
    @SerializedName("data")
    Data data;

    public ChatListResponse() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChatListResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }

    public class Data{
        @SerializedName("message")
        String message;
        @SerializedName("receivername")
        String receivername;
        @SerializedName("receiverphoto")
        String receiverphoto;
        @SerializedName("conversations")
        ArrayList<Conversation> conversations;

        public Data() {
        }

        public String getMessage() {
            return message;
        }

        public String getReceivername() {
            return receivername;
        }

        public void setReceivername(String receivername) {
            this.receivername = receivername;
        }

        public String getReceiverphoto() {
            return receiverphoto;
        }

        public void setReceiverphoto(String receiverphoto) {
            this.receiverphoto = receiverphoto;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<Conversation> getConversations() {
            return conversations;
        }

        public void setConversations(ArrayList<Conversation> conversations) {
            this.conversations = conversations;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", conversations=" + conversations +
                    '}';
        }
    }
}
