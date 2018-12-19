
package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeightLoss {

    @SerializedName("achieved")
    @Expose
    private Double achieved;
    @SerializedName("toGo")
    @Expose
    private Double toGo;
    @SerializedName("percentage")
    @Expose
    private Double percentage;

    public Double getAchieved() {
        return achieved;
    }

    public void setAchieved(Double achieved) {
        this.achieved = achieved;
    }

    public Double getToGo() {
        return toGo;
    }

    public void setToGo(Double toGo) {
        this.toGo = toGo;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

}
