package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class CurrentCompositions {

    @SerializedName("bodyFat")
    private Fat bodyFat;

    @SerializedName("muscle")
    private Fat muscle;

    @SerializedName("protein")
    private Fat protein;

    @SerializedName("weight")
    private String weight;

    @SerializedName("boneKg")
    private Fat boneKg;

    @SerializedName("bmr")
    private Fat bmr;

    @SerializedName("bodyscore")
    private Fat bodyscore;

    @SerializedName("recordedOn")
    private String recordedOn;

    @SerializedName("userName")
    private String userName;

    @SerializedName("battery")
    private int battery;

    @SerializedName("friendrequest_count")
    private int friendrequest_count;

    @SerializedName("unit")
    private String unit;

    @SerializedName("water")
    private Fat water;

    @SerializedName("scaleMacAddress")
    private String scaleMacAddress;

    @SerializedName("height")
    private String height;

    @SerializedName("BMI")
    private Fat bMI;

    public Fat getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(Fat bodyFat) {
        this.bodyFat = bodyFat;
    }

    public Fat getMuscle() {
        return muscle;
    }

    public void setMuscle(Fat muscle) {
        this.muscle = muscle;
    }

    public Fat getProtein() {
        return protein;
    }

    public void setProtein(Fat protein) {
        this.protein = protein;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Fat getBoneKg() {
        return boneKg;
    }

    public void setBoneKg(Fat boneKg) {
        this.boneKg = boneKg;
    }

    public String getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(String recordedOn) {
        this.recordedOn = recordedOn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public Fat getWater() {
        return water;
    }

    public void setWater(Fat water) {
        this.water = water;
    }

    public String getScaleMacAddress() {
        return scaleMacAddress;
    }

    public void setScaleMacAddress(String scaleMacAddress) {
        this.scaleMacAddress = scaleMacAddress;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Fat getbMI() {
        return bMI;
    }

    public void setbMI(Fat bMI) {
        this.bMI = bMI;
    }

    public Fat getBmr() {
        return bmr;
    }

    public void setBmr(Fat bmr) {
        this.bmr = bmr;
    }

    public Fat getBodyscore() {
        return bodyscore;
    }

    public void setBodyscore(Fat bodyscore) {
        this.bodyscore = bodyscore;
    }

    public int getFriendrequest_count() {
        return friendrequest_count;
    }

    public void setFriendrequest_count(int friendrequest_count) {
        this.friendrequest_count = friendrequest_count;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}