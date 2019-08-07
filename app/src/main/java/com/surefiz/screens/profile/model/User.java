package com.surefiz.screens.profile.model;

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
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("user_permission")
    @Expose
    private String userPermission;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("addressline1")
    @Expose
    private String addressline1;
    @SerializedName("addressline2")
    @Expose
    private String addressline2;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("user_phoneNumber")
    @Expose
    private String userPhoneNumber;
    @SerializedName("user_dob")
    @Expose
    private String userDob;
    @SerializedName("user_gender")
    @Expose
    private String userGender;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("user_LastLogin")
    @Expose
    private String userLastLogin;
    @SerializedName("preferredUnits")
    @Expose
    private String preferredUnits;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("target_weight")
    @Expose
    private String targetWeight;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("IsServerWeight")
    @Expose
    private String isServerWeight;
    @SerializedName("scaleid")
    @Expose
    private String scaleid;
    @SerializedName("mainuservisibility")
    @Expose
    private String mainuservisibility;
    @SerializedName("googleAccountLinked")
    @Expose
    private int googleAccountLinked;
    @SerializedName("facebookAccountLinked")
    @Expose
    private int facebookAccountLinked;

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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(String userPermission) {
        this.userPermission = userPermission;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddressline1() {
        return addressline1;
    }

    public void setAddressline1(String addressline1) {
        this.addressline1 = addressline1;
    }

    public String getAddressline2() {
        return addressline2;
    }

    public void setAddressline2(String addressline2) {
        this.addressline2 = addressline2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserDob() {
        return userDob;
    }

    public void setUserDob(String userDob) {
        this.userDob = userDob;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserLastLogin() {
        return userLastLogin;
    }

    public void setUserLastLogin(String userLastLogin) {
        this.userLastLogin = userLastLogin;
    }

    public String getPreferredUnits() {
        return preferredUnits;
    }

    public void setPreferredUnits(String preferredUnits) {
        this.preferredUnits = preferredUnits;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(String targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsServerWeight() {
        return isServerWeight;
    }

    public void setIsServerWeight(String isServerWeight) {
        this.isServerWeight = isServerWeight;
    }

    public String getScaleid() {
        return scaleid;
    }

    public void setScaleid(String scaleid) {
        this.scaleid = scaleid;
    }

    public String getMainuservisibility() {
        return mainuservisibility;
    }

    public void setMainuservisibility(String mainuservisibility) {
        this.mainuservisibility = mainuservisibility;
    }

    public int getGoogleAccountLinked() {
        return googleAccountLinked;
    }

    public void setGoogleAccountLinked(int googleAccountLinked) {
        this.googleAccountLinked = googleAccountLinked;
    }

    public int getFacebookAccountLinked() {
        return facebookAccountLinked;
    }

    public void setFacebookAccountLinked(int facebookAccountLinked) {
        this.facebookAccountLinked = facebookAccountLinked;
    }
}