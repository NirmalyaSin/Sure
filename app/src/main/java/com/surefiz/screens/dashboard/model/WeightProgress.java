
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

}
