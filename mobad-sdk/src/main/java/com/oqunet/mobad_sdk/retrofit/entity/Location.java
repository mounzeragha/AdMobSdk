package com.oqunet.mobad_sdk.retrofit.entity;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("Status")
    private String status;

    @SerializedName("Message")
    private String message;

    public Location() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Location{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
