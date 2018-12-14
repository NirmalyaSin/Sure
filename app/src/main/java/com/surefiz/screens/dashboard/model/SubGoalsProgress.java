
package com.surefiz.screens.dashboard.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubGoalsProgress {

    @SerializedName("weeks")
    @Expose
    private List<String> weeks = null;
    @SerializedName("expectedWeightToGo")
    @Expose
    private List<Double> expectedWeightToGo = null;
    @SerializedName("acheivedWeight")
    @Expose
    private List<Double> acheivedWeight = null;

    public List<String> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<String> weeks) {
        this.weeks = weeks;
    }

    public List<Double> getExpectedWeightToGo() {
        return expectedWeightToGo;
    }

    public void setExpectedWeightToGo(List<Double> expectedWeightToGo) {
        this.expectedWeightToGo = expectedWeightToGo;
    }

    public List<Double> getAcheivedWeight() {
        return acheivedWeight;
    }

    public void setAcheivedWeight(List<Double> acheivedWeight) {
        this.acheivedWeight = acheivedWeight;
    }

}
