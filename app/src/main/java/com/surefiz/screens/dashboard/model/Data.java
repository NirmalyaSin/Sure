
package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("chartList")
    @Expose
    private ChartList chartList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChartList getChartList() {
        return chartList;
    }

    public void setChartList(ChartList chartList) {
        this.chartList = chartList;
    }

}
