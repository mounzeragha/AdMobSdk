package com.oqunet.admob_sdk.models;

import java.util.ArrayList;

public class CarouselAd {

    private String text;
    private ArrayList<CarouselAdItem> carouselAdItemsList;

    public CarouselAd() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<CarouselAdItem> getCarouselAdItemsList() {
        return carouselAdItemsList;
    }

    public void setCarouselAdItemsList(ArrayList<CarouselAdItem> carouselAdItemsList) {
        this.carouselAdItemsList = carouselAdItemsList;
    }
}
