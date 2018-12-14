package com.surefiz.screens.chat.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Conversation /*implements Comparable<Conversation>*/{
    @SerializedName("senderId")
    Integer senderId;
    @SerializedName("reciverId")
    Integer reciverId;
    @SerializedName("messageFrom")
    String messageFrom;
    @SerializedName("message")
    String message;
    @SerializedName("dateTime")
    String dateTime;

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReciverId() {
        return reciverId;
    }

    public void setReciverId(Integer reciverId) {
        this.reciverId = reciverId;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "senderId=" + senderId +
                ", reciverId=" + reciverId +
                ", messageFrom='" + messageFrom + '\'' +
                ", message='" + message + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }

   /* @Override
    public int compareTo(@NonNull Conversation object) {
        return dateTime.compareTo(object.dateTime);
    }*/
}
