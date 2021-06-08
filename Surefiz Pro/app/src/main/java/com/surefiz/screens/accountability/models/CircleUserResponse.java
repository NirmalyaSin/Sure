package com.surefiz.screens.accountability.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CircleUserResponse implements Parcelable {
    @SerializedName("status")
    Integer status;
    @SerializedName("data")
    Data data;

    public CircleUserResponse() { }

    public CircleUserResponse(Integer status, Data data) {
        this.status = status;
        this.data = data;
    }

    protected CircleUserResponse(Parcel in) {
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
    }

    public static final Creator<CircleUserResponse> CREATOR = new Creator<CircleUserResponse>() {
        @Override
        public CircleUserResponse createFromParcel(Parcel in) {
            return new CircleUserResponse(in);
        }

        @Override
        public CircleUserResponse[] newArray(int size) {
            return new CircleUserResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static Creator<CircleUserResponse> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "CircleUserResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}