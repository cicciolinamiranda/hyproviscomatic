package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cicciolina on 12/8/16.
 */

public class SorterBrandModel implements Parcelable {

    private String id;
    private String name;
    private String url;

    public SorterBrandModel() {
        super();
    }

    protected SorterBrandModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<SorterBrandModel> CREATOR = new Creator<SorterBrandModel>() {
        @Override
        public SorterBrandModel createFromParcel(Parcel in) {
            return new SorterBrandModel(in);
        }

        @Override
        public SorterBrandModel[] newArray(int size) {
            return new SorterBrandModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(url);
    }
}
