
package com.surefiz.screens.dashboard.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("visibleCharts")
    @Expose
    private List<String> visibleCharts = null;
    @SerializedName("chartList")
    @Expose
    private ChartList chartList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getVisibleCharts() {
        return visibleCharts;
    }

    public void setVisibleCharts(List<String> visibleCharts) {
        this.visibleCharts = visibleCharts;
    }

    public ChartList getChartList() {
        return chartList;
    }

    public void setChartList(ChartList chartList) {
        this.chartList = chartList;
    }

}
