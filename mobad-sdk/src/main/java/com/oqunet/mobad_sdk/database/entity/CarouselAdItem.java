package com.oqunet.mobad_sdk.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "CarouselAdItem")
public class CarouselAdItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "ad_id")
    private int adId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "button_name")
    private String buttonName;

    @ColumnInfo(name = "button_link")
    private String buttonLink;

    @ColumnInfo(name = "button_destination")
    private String buttonDestination;

    public CarouselAdItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
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

    @Override
    public String toString() {
        return "CarouselAdItem{" +
                "id=" + id +
                ", adId=" + adId +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", buttonName='" + buttonName + '\'' +
                ", buttonLink='" + buttonLink + '\'' +
                ", buttonDestination='" + buttonDestination + '\'' +
                '}';
    }
}
