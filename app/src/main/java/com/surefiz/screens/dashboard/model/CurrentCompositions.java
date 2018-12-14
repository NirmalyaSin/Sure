
package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentCompositions {

    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("scaleMacAddress")
    @Expose
    private Integer scaleMacAddress;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("bodyFat")
    @Expose
    private String bodyFat;
    @SerializedName("boneKg")
    @Expose
    private String boneKg;
    @SerializedName("muscle")
    @Expose
    private String muscle;
    @SerializedName("BMI")
    @Expose
    private String bMI;
    @SerializedName("water")
    @Expose
    private String water;
    @SerializedName("protein")
    @Expose
    private String protein;
    @SerializedName("recordedOn")
    @Expose
    private String recordedOn;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getScaleMacAddress() {
        return scaleMacAddress;
    }

    public void setScaleMacAddress(Integer scaleMacAddress) {
        this.scaleMacAddress = scaleMacAddress;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(String bodyFat) {
        this.bodyFat = bodyFat;
    }

    public String getBoneKg() {
        return boneKg;
    }

    public void setBoneKg(String boneKg) {
        this.boneKg = boneKg;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public String getBMI() {
        return bMI;
    }

    public void setBMI(String bMI) {
        this.bMI = bMI;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(String recordedOn) {
        this.recordedOn = recordedOn;
    }

}
