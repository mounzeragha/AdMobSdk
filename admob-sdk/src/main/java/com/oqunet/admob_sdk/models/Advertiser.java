package com.oqunet.admob_sdk.models;

public class Advertiser {
    private static String advertiser;
    public static String[] advertisers = {"adidas", "hm", "real-estate"};

    public Advertiser() {
    }

    public static String getAdvertiser() {
        return advertiser;
    }

    public static void setAdvertiser(String advertiser) {
        Advertiser.advertiser = advertiser;
    }
}
