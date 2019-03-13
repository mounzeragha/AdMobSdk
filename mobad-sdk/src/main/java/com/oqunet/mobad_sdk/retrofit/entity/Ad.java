package com.oqunet.mobad_sdk.retrofit.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Ad {

    @SerializedName("AdvertiserName")
    private String advertiserName;

    @SerializedName("AdvertiserImage")
    private String advertiserImage;

    @SerializedName("Layout")
    private String format;

    @SerializedName("Title")
    private String adTitle;

    @SerializedName("Description")
    private String adDescription;

    @SerializedName("Path")
    private String adPath;

    @SerializedName("ADID")
    private int adId;

    @SerializedName("ButtonName")
    private String buttonName;

    @SerializedName("ButtonLink")
    private String buttonLink;

    @SerializedName("ButtonDestination")
    private String buttonDestination;

    @SerializedName("CarouselItems")
    private List<CarouselAdItem> carouselAdItems;

    public Ad() {
        carouselAdItems = new ArrayList<>();
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public String getAdvertiserImage() {
        return advertiserImage;
    }

    public void setAdvertiserImage(String advertiserImage) {
        this.advertiserImage = advertiserImage;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getAdDescription() {
        return adDescription;
    }

    public void setAdDescription(String adDescription) {
        this.adDescription = adDescription;
    }

    public String getAdPath() {
        return adPath;
    }

    public void setAdPath(String adPath) {
        this.adPath = adPath;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonLink() {
        return buttonLink;
    }

    public void setButtonLink(String buttonLink) {
        this.buttonLink = buttonLink;
    }

    public String getButtonDestination() {
        return buttonDestination;
    }

    public void setButtonDestination(String buttonDestination) {
        this.buttonDestination = buttonDestination;
    }

    public List<CarouselAdItem> getCarouselAdItems() {
        return carouselAdItems;
    }

    public void setCarouselAdItems(ArrayList<CarouselAdItem> carouselAdItems) {
        this.carouselAdItems = carouselAdItems;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "advertiserName='" + advertiserName + '\'' +
                ", advertiserImage='" + advertiserImage + '\'' +
                ", format='" + format + '\'' +
                ", adTitle='" + adTitle + '\'' +
                ", adDescription='" + adDescription + '\'' +
                ", adPath='" + adPath + '\'' +
                ", adId=" + adId +
                ", buttonName='" + buttonName + '\'' +
                ", buttonLink='" + buttonLink + '\'' +
                ", buttonDestination='" + buttonDestination + '\'' +
                ", carouselAdItems=" + carouselAdItems +
                '}';
    }
}
