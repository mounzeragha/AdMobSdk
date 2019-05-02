package com.oqunet.mobad_sdk.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "Ad")
public class Ad implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "advertiser_name")
    private String advertiserName;

    @ColumnInfo(name = "advertiser_image")
    private String advertiserImage;

    @ColumnInfo(name = "ad_poster")
    private String adPoster;

    @ColumnInfo(name = "format")
    private String format;

    @ColumnInfo(name = "ad_title")
    private String adTitle;

    @ColumnInfo(name = "ad_description")
    private String adDescription;

    @ColumnInfo(name = "ad_path")
    private String adPath;

    @ColumnInfo(name = "ad_id")
    private int adId;

    @ColumnInfo(name = "button_name")
    private String buttonName;

    @ColumnInfo(name = "button_link")
    private String buttonLink;

    @ColumnInfo(name = "button_destination")
    private String buttonDestination;

    @ColumnInfo(name = "invasive")
    private boolean isInvasive;


    public Ad() {
    }

    private Ad(Parcel in) {
        super();
        this.id = in.readLong();
        this.advertiserName = in.readString();
        this.advertiserImage = in.readString();
        this.adPoster = in.readString();
        this.format = in.readString();
        this.adTitle = in.readString();
        this.adDescription = in.readString();
        this.adPath = in.readString();
        this.adId = in.readInt();
        this.buttonName = in.readString();
        this.buttonLink = in.readString();
        this.buttonDestination = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getAdPoster() {
        return adPoster;
    }

    public void setAdPoster(String adPoster) {
        this.adPoster = adPoster;
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

    public boolean isInvasive() {
        return isInvasive;
    }

    public void setInvasive(boolean invasive) {
        isInvasive = invasive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getAdvertiserName());
        dest.writeString(getAdvertiserImage());
        dest.writeString(getAdPoster());
        dest.writeString(getFormat());
        dest.writeString(getAdTitle());
        dest.writeString(getAdDescription());
        dest.writeString(getAdPath());
        dest.writeInt(getAdId());
        dest.writeString(getButtonName());
        dest.writeString(getButtonLink());
        dest.writeString(getButtonDestination());


    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    @Override
    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", advertiserName='" + advertiserName + '\'' +
                ", advertiserImage='" + advertiserImage + '\'' +
                ", adPoster='" + adPoster + '\'' +
                ", format='" + format + '\'' +
                ", adTitle='" + adTitle + '\'' +
                ", adDescription='" + adDescription + '\'' +
                ", adPath='" + adPath + '\'' +
                ", adId=" + adId +
                ", buttonName='" + buttonName + '\'' +
                ", buttonLink='" + buttonLink + '\'' +
                ", buttonDestination='" + buttonDestination + '\'' +
                ", isInvasive=" + isInvasive +
                '}';
    }
}
