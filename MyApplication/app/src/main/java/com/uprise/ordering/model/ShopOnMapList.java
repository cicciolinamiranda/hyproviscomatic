package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by cicciolina on 11/4/16.
 */

public class ShopOnMapList implements Parcelable {

    private List<ShopOnMapModel> shopOnMapModelList;


    public ShopOnMapList() {}
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(shopOnMapModelList);
    }

    protected ShopOnMapList(Parcel in) {
        shopOnMapModelList = in.createTypedArrayList(ShopOnMapModel.CREATOR);
    }

    public static final Creator<ShopOnMapList> CREATOR = new Creator<ShopOnMapList>() {
        @Override
        public ShopOnMapList createFromParcel(Parcel in) {
            return new ShopOnMapList(in);
        }

        @Override
        public ShopOnMapList[] newArray(int size) {
            return new ShopOnMapList[size];
        }
    };

    public List<ShopOnMapModel> getShopOnMapModelList() {
        return shopOnMapModelList;
    }

    public void setShopOnMapModelList(List<ShopOnMapModel> shopOnMapModelList) {
        this.shopOnMapModelList = shopOnMapModelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
