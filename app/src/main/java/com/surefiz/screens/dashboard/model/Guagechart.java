package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class Guagechart {

    @SerializedName("lastweight")
    String lastweight;

    @SerializedName("gtw")
    String gtw;

    @SerializedName("maxweight")
    String maxweight;

    @SerializedName("diff")
    String diff;

    public String getLastweight() {
        return lastweight;
    }

    public void setLastweight(String lastweight) {
        this.lastweight = lastweight;
    }

    public String getGtw() {
        return gtw;
    }

    public void setGtw(String gtw) {
        this.gtw = gtw;
    }

    public String getMaxweight() {
        return maxweight;
    }

    public void setMaxweight(String maxweight) {
        this.maxweight = maxweight;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }
}
