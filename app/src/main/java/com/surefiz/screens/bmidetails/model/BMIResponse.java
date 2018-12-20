package com.surefiz.screens.bmidetails.model;

import com.google.gson.annotations.SerializedName;
import com.surefiz.screens.chat.model.Conversation;

import java.util.ArrayList;

public class BMIResponse {
    @SerializedName("status")
    Integer status;
    @SerializedName("data")
    Data data;

    public BMIResponse() {
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
        @SerializedName("BMIDetails")
        BMIDetails BMIDetails;

        public Data() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public com.surefiz.screens.bmidetails.model.BMIDetails getBMIDetails() {
            return BMIDetails;
        }

        public void setBMIDetails(com.surefiz.screens.bmidetails.model.BMIDetails BMIDetails) {
            this.BMIDetails = BMIDetails;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", BMIDetails=" + BMIDetails +
                    '}';
        }
    }
}
