
package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeightLoss {

    @SerializedName("achieved")
    @Expose
    private String achieved;
    @SerializedName("toGo")
    @Expose
    private String toGo;
    @SerializedName("percentage")
    @Expose
    private Integer percentage;

    public String getAchieved() {
        return achieved;
    }

    public void setAchieved(String achieved) {
        this.achieved = achieved;
    }

    public String getToGo() {
        return toGo;
    }

    public void setToGo(String toGo) {
        this.toGo = toGo;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

}
