
package com.surefiz.screens.dashboard.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Composition {

    @SerializedName("overallData")
    @Expose
    private List<String> overallData = null;
    @SerializedName("muscle")
    @Expose
    private String muscle;
    @SerializedName("fat")
    @Expose
    private String fat;

    public List<String> getOverallData() {
        return overallData;
    }

    public void setOverallData(List<String> overallData) {
        this.overallData = overallData;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

}
