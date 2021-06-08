package com.surefiz.screens.accountability.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    @SerializedName("user_id")
    String user_id;
    @SerializedName("user_name")
    String user_name;
    @SerializedName("user_email")
    String user_email;
    @SerializedName("user_permission")
    String user_permission;
    @SerializedName("user_image")
    String user_image;
    @SerializedName("user_photo")
    String user_search_image;
    @SerializedName("user_LastLogin")
    String user_LastLogin;
    @SerializedName("connectionStatus")
    String connectionStatus;
    @SerializedName("onlineStatus")
    String onlineStatus;

    public User() { }

    protected User(Parcel in) {
        user_id = in.readString();
        user_name = in.readString();
        user_email = in.readString();
        user_permission = in.readString();
        user_image = in.readString();
        user_search_image = in.readString();
        user_LastLogin = in.readString();
        connectionStatus = in.readString();
        onlineStatus = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(user_name);
        dest.writeString(user_email);
        dest.writeString(user_permission);
        dest.writeString(user_image);
        dest.writeString(user_search_image);
        dest.writeString(user_LastLogin);
        dest.writeString(connectionStatus);
        dest.writeString(onlineStatus);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_permission() {
        return user_permission;
    }

    public void setUser_permission(String user_permission) {
        this.user_permission = user_permission;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_search_image() {
        return user_search_image;
    }

    public void setUser_search_image(String user_search_image) {
        this.user_search_image = user_search_image;
    }

    public String getUser_LastLogin() {
        return user_LastLogin;
    }

    public void setUser_LastLogin(String user_LastLogin) {
        this.user_LastLogin = user_LastLogin;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_permission='" + user_permission + '\'' +
                ", user_image='" + user_image + '\'' +
                ", user_search_image='" + user_search_image + '\'' +
                ", user_LastLogin='" + user_LastLogin + '\'' +
                ", connectionStatus='" + connectionStatus + '\'' +
                ", onlineStatus='" + onlineStatus + '\'' +
                '}';
    }
}
