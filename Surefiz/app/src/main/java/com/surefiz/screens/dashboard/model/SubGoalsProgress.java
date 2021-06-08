
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
    private List<String> expectedWeightToGo = null;
    @SerializedName("acheivedWeight")
    @Expose
    private List<String> acheivedWeight = null;
    @SerializedName("minval")
    @Expose
    private String minval;

    public List<String> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<String> weeks) {
        this.weeks = weeks;
    }

    public List<String> getExpectedWeightToGo() {
        return expectedWeightToGo;
    }

    public void setExpectedWeightToGo(List<String> expectedWeightToGo) {
        this.expectedWeightToGo = expectedWeightToGo;
    }

    public List<String> getAcheivedWeight() {
        return acheivedWeight;
    }

    public void setAcheivedWeight(List<String> acheivedWeight) {
        this.acheivedWeight = acheivedWeight;
    }

    public String getMinval() {
        return minval;
    }

    public void setMinval(String minval) {
        this.minval = minval;
    }
}
