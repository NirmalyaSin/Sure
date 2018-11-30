package com.surefiz.screens.accountability.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data implements Parcelable {
    @SerializedName("message")
    String message;
    @SerializedName("userList")
    ArrayList<User> userList;

    public Data() {
    }

    public Data(String message, ArrayList<User> userList) {
        this.message = message;
        this.userList = userList;
    }

    protected Data(Parcel in) {
        message = in.readString();
        userList = in.createTypedArrayList(User.CREATOR);
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeTypedList(userList);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public static Creator<Data> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "Data{" +
                "message='" + message + '\'' +
                ", userList=" + userList +
                '}';
    }
}
