
package com.surefiz.screens.dashboard.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeightProgress {

    @SerializedName("label")
    @Expose
    private List<String> label = null;
    @SerializedName("data")
    @Expose
    private List<String> data = null;
    @SerializedName("noOfWeek")
    @Expose
    private String noOfWeek;
    @SerializedName("chart1min")
    @Expose
    private String chart1min;
    @SerializedName("minval")
    @Expose
    private String minval;

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getNoOfWeek() {
        return noOfWeek;
    }

    public void setNoOfWeek(String noOfWeek) {
        this.noOfWeek = noOfWeek;
    }

    public String getChart1min() {
        return chart1min;
    }

    public void setChart1min(String chart1min) {
        this.chart1min = chart1min;
    }

    public String getMinval() {
        return minval;
    }

    public void setMinval(String minval) {
        this.minval = minval;
    }
}
