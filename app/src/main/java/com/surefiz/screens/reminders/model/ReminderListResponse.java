package com.surefiz.screens.reminders.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReminderListResponse {
    @SerializedName("status")
    Integer status;
    @SerializedName("data")
    Data data;

    public ReminderListResponse() {
    }

    public ReminderListResponse(Integer status, Data data) {
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
        return "ReminderListResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }

    //Inner class for Data
    public class Data {
        @SerializedName("message")
        String message;
        @SerializedName("userList")
        ArrayList<User> reminderList;

        public Data() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<User> getReminderList() {
            return reminderList;
        }

        public void setReminderList(ArrayList<User> reminderList) {
            this.reminderList = reminderList;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", reminderList=" + reminderList +
                    '}';
        }
    }
}
