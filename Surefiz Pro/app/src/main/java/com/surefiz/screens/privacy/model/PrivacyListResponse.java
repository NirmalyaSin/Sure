package com.surefiz.screens.privacy.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PrivacyListResponse {
    @SerializedName("status")
    Integer status;
    @SerializedName("data")
    Data data;

    public PrivacyListResponse() {
    }

    public PrivacyListResponse(Integer status, Data data) {
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
        return "AddToCircleResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }

    //Inner class for Data
    public class Data {
        @SerializedName("message")
        String message;
        @SerializedName("privacySettings")
        ArrayList<PrivacySetting> privacySettings;

        public Data() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<PrivacySetting> getPrivacySettings() {
            return privacySettings;
        }

        public void setPrivacySettings(ArrayList<PrivacySetting> privacySettings) {
            this.privacySettings = privacySettings;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", privacySettings=" + privacySettings +
                    '}';
        }
    }
}
