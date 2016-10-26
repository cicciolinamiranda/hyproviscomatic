package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cicciolina on 10/22/16.
 */

public class BrandModel implements Parcelable {

    private String brandPhotoUrl;
    private String brandName;
    private double price;

    public BrandModel() {}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(brandName);
        parcel.writeDouble(price);
        parcel.writeString(brandPhotoUrl);
    }

    public BrandModel(Parcel in) {
        this();
        brandName = in.readString();
        price = in.readDouble();
        brandPhotoUrl = in.readString();
    }

    public static final Creator<BrandModel> CREATOR = new Creator<BrandModel>() {
        @Override
        public BrandModel createFromParcel(Parcel in) {
            return new BrandModel(in);
        }

        @Override
        public BrandModel[] newArray(int size) {
            return new BrandModel[size];
        }
    };

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBrandPhotoUrl() {
        return brandPhotoUrl;
    }

    public void setBrandPhotoUrl(String brandPhotoUrl) {
        this.brandPhotoUrl = brandPhotoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
