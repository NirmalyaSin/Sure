package com.surefiz.screens.signup.response;

import com.google.gson.annotations.SerializedName;
import com.surefiz.screens.registration.model.User;

import java.util.List;

public class Data {

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private List<User> user;

    @SerializedName("status")
    private int status;

    @SerializedName("token")
    private String token;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public List<User> getUser() {
        return user;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}