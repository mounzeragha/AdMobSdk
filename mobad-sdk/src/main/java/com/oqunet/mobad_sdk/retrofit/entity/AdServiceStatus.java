package com.oqunet.mobad_sdk.retrofit.entity;

import com.google.gson.annotations.SerializedName;

public class AdServiceStatus {

    @SerializedName("Status")
    private String status;

    @SerializedName("UserStatus")
    private String adServiceStatus;

    public AdServiceStatus() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdServiceStatus() {
        return adServiceStatus;
    }

    public void setAdServiceStatus(String adServiceStatus) {
        this.adServiceStatus = adServiceStatus;
    }

    @Override
    public String toString() {
        return "AdServiceStatus{" +
                "status='" + status + '\'' +
                ", adServiceStatus='" + adServiceStatus + '\'' +
                '}';
    }
}
