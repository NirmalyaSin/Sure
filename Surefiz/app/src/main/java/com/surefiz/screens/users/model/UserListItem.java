package com.surefiz.screens.users.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserListItem implements Serializable {

    @SerializedName("userWeight")
    private String userWeight;

    @SerializedName("scaleUserId")
    private int scaleUserId;

    @SerializedName("isUserHaveCompleteInfo")
    private int isUserHaveCompleteInfo;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userName")
    private String userName;

    @SerializedName("serverUserId")
    private int serverUserId;

    @SerializedName("mainuservisibility")
    private int mainuservisibility;

    @SerializedName("user_image")
    private String user_image;

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }

    public int getScaleUserId() {
        return scaleUserId;
    }

    public void setScaleUserId(int scaleUserId) {
        this.scaleUserId = scaleUserId;
    }

    public int getIsUserHaveCompleteInfo() {
        return isUserHaveCompleteInfo;
    }

    public void setIsUserHaveCompleteInfo(int isUserHaveCompleteInfo) {
        this.isUserHaveCompleteInfo = isUserHaveCompleteInfo;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getServerUserId() {
        return serverUserId;
    }

    public void setServerUserId(int serverUserId) {
        this.serverUserId = serverUserId;
    }

    public int getMainuservisibility() {
        return mainuservisibility;
    }
}