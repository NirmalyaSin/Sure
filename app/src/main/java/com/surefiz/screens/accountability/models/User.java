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
    @SerializedName("user_LastLogin")
    String user_LastLogin;
    @SerializedName("connectionStatus")
    Integer connectionStatus;

    public User() { }

    public User(String user_id, String user_name, String user_email, String user_permission,
                String user_LastLogin, Integer connectionStatus) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_permission = user_permission;
        this.user_LastLogin = user_LastLogin;
        this.connectionStatus = connectionStatus;
    }

    protected User(Parcel in) {
        user_id = in.readString();
        user_name = in.readString();
        user_email = in.readString();
        user_permission = in.readString();
        user_LastLogin = in.readString();
        if (in.readByte() == 0) {
            connectionStatus = null;
        } else {
            connectionStatus = in.readInt();
        }
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
        dest.writeString(user_LastLogin);
        if (connectionStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(connectionStatus);
        }
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

    public String getUser_LastLogin() {
        return user_LastLogin;
    }

    public void setUser_LastLogin(String user_LastLogin) {
        this.user_LastLogin = user_LastLogin;
    }

    public Integer getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(Integer connectionStatus) {
        this.connectionStatus = connectionStatus;
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
                ", user_LastLogin='" + user_LastLogin + '\'' +
                ", connectionStatus=" + connectionStatus +
                '}';
    }
}
