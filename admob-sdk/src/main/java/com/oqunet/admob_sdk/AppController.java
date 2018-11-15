package com.oqunet.admob_sdk;

import android.app.Application;

import java.util.ArrayList;

public class AppController extends Application {
    private static String advertiser;
    public static String[] advertisers = {"adidas", "hm", "real-estate"};

    public static String getAdvertiser() {
        return advertiser;
    }

    public static void setAdvertiser(String advertiser) {
        AppController.advertiser = advertiser;
    }

    public static ArrayList<String> getAdvertisers() {
        ArrayList<String> advertisersList = new ArrayList<>();
        advertisersList.add("adidas");
        advertisersList.add("hm");
        advertisersList.add("real-estate");

        return advertisersList;
    }
}
