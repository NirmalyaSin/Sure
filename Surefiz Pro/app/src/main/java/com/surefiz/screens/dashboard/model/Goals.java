
package com.surefiz.screens.dashboard.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Goals {

    @SerializedName("minigoalsjson")
    @Expose
    private List<String> minigoalsjson = null;
    @SerializedName("miniweeksjson")
    @Expose
    private List<String> miniweeksjson = null;
    @SerializedName("miniweighttext")
    @Expose
    private String miniweighttext;
    @SerializedName("TargetWeight")
    @Expose
    private Double targetWeight;
    @SerializedName("ms1")
    @Expose
    private Integer ms1;
    @SerializedName("me1")
    @Expose
    private Integer me1;
    @SerializedName("ms2")
    @Expose
    private Integer ms2;
    @SerializedName("me2")
    @Expose
    private Integer me2;
    @SerializedName("minval")
    @Expose
    private String minval;

    public List<String> getMinigoalsjson() {
        return minigoalsjson;
    }

    public void setMinigoalsjson(List<String> minigoalsjson) {
        this.minigoalsjson = minigoalsjson;
    }

    public List<String> getMiniweeksjson() {
        return miniweeksjson;
    }

    public void setMiniweeksjson(List<String> miniweeksjson) {
        this.miniweeksjson = miniweeksjson;
    }

    public String getMiniweighttext() {
        return miniweighttext;
    }

    public void setMiniweighttext(String miniweighttext) {
        this.miniweighttext = miniweighttext;
    }

    public Double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Integer getMs1() {
        return ms1;
    }

    public void setMs1(Integer ms1) {
        this.ms1 = ms1;
    }

    public Integer getMe1() {
        return me1;
    }

    public void setMe1(Integer me1) {
        this.me1 = me1;
    }

    public Integer getMs2() {
        return ms2;
    }

    public void setMs2(Integer ms2) {
        this.ms2 = ms2;
    }

    public Integer getMe2() {
        return me2;
    }

    public void setMe2(Integer me2) {
        this.me2 = me2;
    }

    public String getMinval() {
        return minval;
    }

    public void setMinval(String minval) {
        this.minval = minval;
    }
}
