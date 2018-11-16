package com.oqunet.admob_sdk.models;

public class DummyData {
    private static String advertiser;
    public static String[] advertisers = {"adidas", "hm", "real-estate"};

    public DummyData() {
    }

    public static String getAdvertiser() {
        return advertiser;
    }

    public static void setAdvertiser(String advertiser) {
        DummyData.advertiser = advertiser;
    }
}
