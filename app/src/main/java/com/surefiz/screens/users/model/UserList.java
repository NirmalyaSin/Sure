
package com.surefiz.screens.users.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserList {

    @SerializedName("serverUserId")
    @Expose
    private Integer serverUserId;
    @SerializedName("scaleUserId")
    @Expose
    private Integer scaleUserId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userEmail")
    @Expose
    private String userEmail;
    @SerializedName("userWeight")
    @Expose
    private String userWeight;

    public Integer getServerUserId() {
        return serverUserId;
    }

    public void setServerUserId(Integer serverUserId) {
        this.serverUserId = serverUserId;
    }

    public Integer getScaleUserId() {
        return scaleUserId;
    }

    public void setScaleUserId(Integer scaleUserId) {
        this.scaleUserId = scaleUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }

}
