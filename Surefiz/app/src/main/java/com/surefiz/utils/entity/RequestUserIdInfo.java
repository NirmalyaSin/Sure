package com.surefiz.utils.entity;


public class RequestUserIdInfo {

    private String dataId;
    private int weight;

    private boolean selected = false;

    public RequestUserIdInfo() {
    }

    public RequestUserIdInfo(String dataId, int weight) {
        this.dataId = dataId;
        this.weight = weight;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "RequestUserIdInfo{" +
                "dataId='" + dataId + '\'' +
                ", weight=" + weight +
                ", selected=" + selected +
                '}';
    }
}
