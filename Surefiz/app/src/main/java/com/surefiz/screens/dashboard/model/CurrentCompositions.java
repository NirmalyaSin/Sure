package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class CurrentCompositions {

    @SerializedName("bodyFat")
    private BodyFat bodyFat;

    @SerializedName("muscle")
    private BodyFat muscle;

    @SerializedName("protein")
    private BodyFat protein;

    @SerializedName("weight")
    private String weight;

    @SerializedName("boneKg")
    private BodyFat boneKg;

    @SerializedName("bmr")
    private BodyFat bmr;

    @SerializedName("bodyscore")
    private BodyFat bodyscore;

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
    private BodyFat water;

    @SerializedName("scaleMacAddress")
    private String scaleMacAddress;

    @SerializedName("height")
    private String height;

    @SerializedName("BMI")
    private BodyFat bMI;

    public BodyFat getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(BodyFat bodyFat) {
        this.bodyFat = bodyFat;
    }

    public BodyFat getMuscle() {
        return muscle;
    }

    public void setMuscle(BodyFat muscle) {
        this.muscle = muscle;
    }

    public BodyFat getProtein() {
        return protein;
    }

    public void setProtein(BodyFat protein) {
        this.protein = protein;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public BodyFat getBoneKg() {
        return boneKg;
    }

    public void setBoneKg(BodyFat boneKg) {
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

    public BodyFat getWater() {
        return water;
    }

    public void setWater(BodyFat water) {
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

    public BodyFat getbMI() {
        return bMI;
    }

    public void setbMI(BodyFat bMI) {
        this.bMI = bMI;
    }

    public BodyFat getBmr() {
        return bmr;
    }

    public void setBmr(BodyFat bmr) {
        this.bmr = bmr;
    }

    public BodyFat getBodyscore() {
        return bodyscore;
    }

    public void setBodyscore(BodyFat bodyscore) {
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