package com.surefiz.screens.notifications.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationsResponse {
    @SerializedName("status")
    Integer status;
    @SerializedName("data")
    Data data;

    public NotificationsResponse() { }

    public NotificationsResponse(Integer status, Data data) {
        this.status = status;
        this.data = data;
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
        return "NotificationsResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }



    //Inner class for Data
    public class Data{
        @SerializedName("message")
        String message;
        @SerializedName("notifications")
        ArrayList<Notification> notifications;

        public Data() {
        }

        public Data(String message, ArrayList<Notification> notifications) {
            this.message = message;
            this.notifications = notifications;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<Notification> getNotifications() {
            return notifications;
        }

        public void setNotifications(ArrayList<Notification> notifications) {
            this.notifications = notifications;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", notifications=" + notifications +
                    '}';
        }
    }
}
