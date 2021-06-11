
package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Composition {

    @SerializedName("overallData")
    @Expose
    private List<String> overallData = null;
    @SerializedName("muscle")
    @Expose
    private Double muscle;
    @SerializedName("fat")
    @Expose
    private Double fat;

    public List<String> getOverallData() {
        return overallData;
    }

    public void setOverallData(List<String> overallData) {
        this.overallData = overallData;
    }

    public Double getMuscle() {
        return muscle;
    }

    public void setMuscle(Double muscle) {
        this.muscle = muscle;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

}
