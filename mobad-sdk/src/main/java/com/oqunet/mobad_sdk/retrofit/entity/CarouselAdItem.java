package com.oqunet.mobad_sdk.retrofit.entity;

import com.google.gson.annotations.SerializedName;

public class CarouselAdItem {

    @SerializedName("Title")
    private String title;

    @SerializedName("Image")
    private String image;

    @SerializedName("Description")
    private String description;

    @SerializedName("ButtonName")
    private String buttonName;

    @SerializedName("ButtonLink")
    private String buttonLink;

    @SerializedName("ButtonDestination")
    private String buttonDestination;

    public CarouselAdItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
