package com.surefiz.screens.wificonfig;

import android.os.Parcel;
import android.os.Parcelable;

public class Wifi implements Parcelable {
    private String ssid;
    private String bssid;

    protected Wifi(Parcel in) {
        ssid = in.readString();
        bssid = in.readString();
    }

    public Wifi(String ssid, String bssid) {
        this.ssid = ssid;
        this.bssid = bssid;
    }

    public static final Creator<Wifi> CREATOR = new Creator<Wifi>() {
        @Override
        public Wifi createFromParcel(Parcel in) {
            return new Wifi(in);
        }

        @Override
        public Wifi[] newArray(int size) {
            return new Wifi[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ssid);
        dest.writeString(bssid);
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public static Creator<Wifi> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "ssid='" + ssid + '\'' +
                ", bssid='" + bssid + '\'' +
                '}';
    }
}
