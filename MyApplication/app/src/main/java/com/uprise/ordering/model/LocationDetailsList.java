package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by cicciolina on 11/4/16.
 */

public class LocationDetailsList implements Parcelable {

    private List<LocationDetailsModel> shopOnMapModelList;


    public LocationDetailsList() {}
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(shopOnMapModelList);
    }

    protected LocationDetailsList(Parcel in) {
        shopOnMapModelList = in.createTypedArrayList(LocationDetailsModel.CREATOR);
    }

    public static final Creator<LocationDetailsList> CREATOR = new Creator<LocationDetailsList>() {
        @Override
        public LocationDetailsList createFromParcel(Parcel in) {
            return new LocationDetailsList(in);
        }

        @Override
        public LocationDetailsList[] newArray(int size) {
            return new LocationDetailsList[size];
        }
    };

    public List<LocationDetailsModel> getShopOnMapModelList() {
        return shopOnMapModelList;
    }

    public void setShopOnMapModelList(List<LocationDetailsModel> shopOnMapModelList) {
        this.shopOnMapModelList = shopOnMapModelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
