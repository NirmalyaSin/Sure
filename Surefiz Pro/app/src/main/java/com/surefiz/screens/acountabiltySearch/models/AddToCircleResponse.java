package com.surefiz.screens.acountabiltySearch.models;

import com.google.gson.annotations.SerializedName;

public class AddToCircleResponse {
    @SerializedName("status")
    Integer status;
    @SerializedName("data")
    Data data;

    public AddToCircleResponse() {
    }

    public AddToCircleResponse(Integer status, Data data) {
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
    public class Data{
        @SerializedName("message")
        String message;
        @SerializedName("type")
        String type;

        public Data() {
        }

        public Data(String message, String type) {
            this.message = message;
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
