package com.oqunet.mobad_sdk.retrofit.entity;

import com.google.gson.annotations.SerializedName;

public class Action {

    @SerializedName("Status")
    private String status;

    @SerializedName("Earned")
    private String earnedCoins;

    @SerializedName("Wallet")
    private String wallet;

    @SerializedName("Message")
    private String message;

    public Action() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEarnedCoins() {
        return earnedCoins;
    }

    public void setEarnedCoins(String earnedCoins) {
        this.earnedCoins = earnedCoins;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Action{" +
                "status='" + status + '\'' +
                ", earnedCoins='" + earnedCoins + '\'' +
                ", wallet='" + wallet + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
