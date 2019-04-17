package com.surefiz.screens.notifications.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Notification implements Parcelable {
    @SerializedName("notificationId")
    String notificationId;
    @SerializedName("notificationSenderId")
    String notificationSenderId;
    @SerializedName("notificationText")
    String notificationText;
    @SerializedName("notificationReadStatus")
    String notificationReadStatus;
    @SerializedName("lastServerUpdateDate")
    String notificationDate;
    @SerializedName("lastServerUpdateTime")
    String notificationTime;
    @SerializedName("notificationType")
    String notificationType;
    @SerializedName("contentId")
    String contentId;

    public Notification() {
    }

    public Notification(String notificationId, String notificationSenderId,
                        String notificationText, String notificationReadStatus,
                        String notificationDate, String notificationTime, String notificationType, String contentId) {
        this.notificationId = notificationId;
        this.notificationSenderId = notificationSenderId;
        this.notificationText = notificationText;
        this.notificationReadStatus = notificationReadStatus;
        this.notificationDate = notificationDate;
        this.notificationTime = notificationTime;
        this.notificationType = notificationType;
        this.contentId = contentId;
    }

    protected Notification(Parcel in) {
        notificationId = in.readString();
        notificationSenderId = in.readString();
        notificationText = in.readString();
        notificationReadStatus = in.readString();
        notificationDate = in.readString();
        notificationTime = in.readString();
        notificationType = in.readString();
        contentId = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notificationId);
        dest.writeString(notificationSenderId);
        dest.writeString(notificationText);
        dest.writeString(notificationReadStatus);
        dest.writeString(notificationDate);
        dest.writeString(notificationTime);
        dest.writeString(notificationType);
        dest.writeString(contentId);
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationSenderId() {
        return notificationSenderId;
    }

    public void setNotificationSenderId(String notificationSenderId) {
        this.notificationSenderId = notificationSenderId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getNotificationReadStatus() {
        return notificationReadStatus;
    }

    public void setNotificationReadStatus(String notificationReadStatus) {
        this.notificationReadStatus = notificationReadStatus;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public static Creator<Notification> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", notificationSenderId='" + notificationSenderId + '\'' +
                ", notificationText='" + notificationText + '\'' +
                ", notificationReadStatus='" + notificationReadStatus + '\'' +
                ", notificationDate='" + notificationDate + '\'' +
                ", notificationTime='" + notificationTime + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", contentId='" + contentId + '\'' +
                '}';
    }
}

