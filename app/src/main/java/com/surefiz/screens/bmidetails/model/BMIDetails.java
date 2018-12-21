package com.surefiz.screens.bmidetails.model;

import com.google.gson.annotations.SerializedName;

public class BMIDetails {
    @SerializedName("Weight")
    String Weight;
    @SerializedName("BMI")
    Double BMI;
    @SerializedName("subgoal1")
    Double subgoal1;
    @SerializedName("subgoal2")
    Double subgoal2;
    @SerializedName("percentage")
    Double percentage;
    @SerializedName("weighttolose")
    Double weighttolose;

    public BMIDetails() {
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public Double getWeighttolose() {
        return weighttolose;
    }

    public void setWeighttolose(Double weighttolose) {
        this.weighttolose = weighttolose;
    }

    public Double getBMI() {
        return BMI;
    }

    public void setBMI(Double BMI) {
        this.BMI = BMI;
    }

    public Double getSubgoal1() {
        return subgoal1;
    }

    public void setSubgoal1(Double subgoal1) {
        this.subgoal1 = subgoal1;
    }

    public Double getSubgoal2() {
        return subgoal2;
    }

    public void setSubgoal2(Double subgoal2) {
        this.subgoal2 = subgoal2;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
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
                '}';
    }
}
