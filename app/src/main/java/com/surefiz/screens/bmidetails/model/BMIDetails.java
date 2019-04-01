package com.surefiz.screens.bmidetails.model;

import com.google.gson.annotations.SerializedName;

public class BMIDetails {
    @SerializedName("Weight")
    String Weight;
    @SerializedName("BMI")
    String BMI;
    @SerializedName("subgoal1")
    String subgoal1;
    @SerializedName("subgoal2")
    String subgoal2;
    @SerializedName("percentage")
    String percentage;
    @SerializedName("weighttolose")
    String weighttolose;
    @SerializedName("name")
    String name;
    @SerializedName("preferredUnit")
    String preferredUnit;
    @SerializedName("batteryStaus")
    String batteryStaus;

    @SerializedName("subgoalImageName")
    String subgoalImageName;


    public BMIDetails() {
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getWeighttolose() {
        return weighttolose;
    }

    public void setWeighttolose(String weighttolose) {
        this.weighttolose = weighttolose;
    }

    public String getBMI() {
        return BMI;
    }

    public void setBMI(String BMI) {
        this.BMI = BMI;
    }

    public String getSubgoal1() {
        return subgoal1;
    }

    public void setSubgoal1(String subgoal1) {
        this.subgoal1 = subgoal1;
    }

    public String getSubgoal2() {
        return subgoal2;
    }

    public void setSubgoal2(String subgoal2) {
        this.subgoal2 = subgoal2;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredUnit() {
        return preferredUnit;
    }

    public void setPreferredUnit(String preferredUnit) {
        this.preferredUnit = preferredUnit;
    }

    public String getBatteryStaus() {
        return batteryStaus;
    }

    public void setBatteryStaus(String batteryStaus) {
        this.batteryStaus = batteryStaus;
    }

    public String getSubgoalImageName() {
        return subgoalImageName;
    }

    public void setSubgoalImageName(String subgoalImageName) {
        this.subgoalImageName = subgoalImageName;
    }

    @Override
    public String toString() {
        return "BMIDetails{" +
                "Weight='" + Weight + '\'' +
                ", BMI=" + BMI +
                ", subgoal1=" + subgoal1 +
                ", subgoal2=" + subgoal2 +
                ", percentage=" + percentage +
                ", weighttolose=" + weighttolose +
                ", preferredUnit=" + preferredUnit +
                ", batteryStaus=" + batteryStaus +
                ", subgoalImageName=" + subgoalImageName +
                ", name=" + name +
                '}';
    }
}
