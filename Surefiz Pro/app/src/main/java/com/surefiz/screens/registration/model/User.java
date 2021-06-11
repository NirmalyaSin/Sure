
package com.surefiz.screens.registration.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("scaleUserId")
    @Expose
    private String scaleUserId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("user_password")
    @Expose
    private String userPassword;
    @SerializedName("user_photo")
    @Expose
    private String userPhoto;
    @SerializedName("user_mac")
    @Expose
    private String userMac;
    @SerializedName("user_permission")
    @Expose
    private String userPermission;
    @SerializedName("user_LastLogin")
    @Expose
    private String userLastLogin;

    @SerializedName("user_Profile_Complete_Status")
    @Expose
    private Integer userProfileCompleteStatus;

    @SerializedName("isfirsttime")
    @Expose
    private Integer isfirsttime;

    @SerializedName("imageName")
    private Object imageName;

    @SerializedName("count")
    private String count;

    @SerializedName("regtype")
    private String regtype;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScaleUserId() {
        return scaleUserId;
    }

    public void setScaleUserId(String scaleUserId) {
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserMac() {
        return userMac;
    }

    public void setUserMac(String userMac) {
        this.userMac = userMac;
    }

    public String getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(String userPermission) {
        this.userPermission = userPermission;
    }

    public String getUserLastLogin() {
        return userLastLogin;
    }

    public void setUserLastLogin(String userLastLogin) {
        this.userLastLogin = userLastLogin;
    }

    public Integer getUserProfileCompleteStatus() {
        return userProfileCompleteStatus;
    }

    public void setUserProfileCompleteStatus(Integer userProfileCompleteStatus) {
        this.userProfileCompleteStatus = userProfileCompleteStatus;
    }

    public Integer getIsfirsttime() {
        return isfirsttime;
    }

    public void setIsfirsttime(Integer isfirsttime) {
        this.isfirsttime = isfirsttime;
    }

    public Object getImageName() {
        return imageName;
    }

    public void setImageName(Object imageName) {
        this.imageName = imageName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getRegtype() {
        return regtype;
    }

    public void setRegtype(String regtype) {
        this.regtype = regtype;
    }
}
