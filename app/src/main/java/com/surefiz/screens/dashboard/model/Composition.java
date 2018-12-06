
package com.surefiz.screens.dashboard.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Composition {

    @SerializedName("overallData")
    @Expose
    private List<Object> overallData = null;
    @SerializedName("muscle")
    @Expose
    private Object muscle;
    @SerializedName("fat")
    @Expose
    private Object fat;

    public List<Object> getOverallData() {
        return overallData;
    }

    public void setOverallData(List<Object> overallData) {
        this.overallData = overallData;
    }

    public Object getMuscle() {
        return muscle;
    }

    public void setMuscle(Object muscle) {
        this.muscle = muscle;
    }

    public Object getFat() {
        return fat;
    }

    public void setFat(Object fat) {
        this.fat = fat;
    }

}
