package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cicciolina on 11/3/16.
 */

public class LocationDetailsModel implements Parcelable {

//    private String title;
    private String address;
    private LatLng location;

    public LocationDetailsModel() {

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(title);
        dest.writeString(address);
        dest.writeParcelable(location, flags);
    }

    protected LocationDetailsModel(Parcel in) {
//        title = in.readString();
        address = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<LocationDetailsModel> CREATOR = new Creator<LocationDetailsModel>() {
        @Override
        public LocationDetailsModel createFromParcel(Parcel in) {
            return new LocationDetailsModel(in);
        }

        @Override
        public LocationDetailsModel[] newArray(int size) {
            return new LocationDetailsModel[size];
        }
    };

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
