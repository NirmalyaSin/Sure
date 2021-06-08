package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryGoals {

    @SerializedName("hgoalsjson")
    @Expose
    private List<String> hgoalsjson = null;
    @SerializedName("hweeksjson")
    @Expose
    private List<String> hweeksjson = null;
    @SerializedName("hweighttext")
    @Expose
    private String hweighttext;
    @SerializedName("TargetWeight")
    @Expose
    private Double targetWeight;
    @SerializedName("hs1")
    @Expose
    private Integer hs1;
    @SerializedName("he1")
    @Expose
    private Integer he1;
    @SerializedName("hs2")
    @Expose
    private Integer hs2;
    @SerializedName("he2")
    @Expose
    private Integer he2;
    @SerializedName("minval")
    @Expose
    private String minval;


    public List<String> getHgoalsjson() {
        return hgoalsjson;
    }

    public void setHgoalsjson(List<String> hgoalsjson) {
        this.hgoalsjson = hgoalsjson;
    }

    public List<String> getHweeksjson() {
        return hweeksjson;
    }

    public void setHweeksjson(List<String> hweeksjson) {
        this.hweeksjson = hweeksjson;
    }

    public String getHweighttext() {
        return hweighttext;
    }

    public void setHweighttext(String hweighttext) {
        this.hweighttext = hweighttext;
    }

    public Double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Integer getHs1() {
        return hs1;
    }

    public void setHs1(Integer hs1) {
        this.hs1 = hs1;
    }

    public Integer getHe1() {
        return he1;
    }

    public void setHe1(Integer he1) {
        this.he1 = he1;
    }

    public Integer getHs2() {
        return hs2;
    }

    public void setHs2(Integer hs2) {
        this.hs2 = hs2;
    }

    public Integer getHe2() {
        return he2;
    }

    public void setHe2(Integer he2) {
        this.he2 = he2;
    }

    public String getMinval() {
        return minval;
    }

    public void setMinval(String minval) {
        this.minval = minval;
    }
}
