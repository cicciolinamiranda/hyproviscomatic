package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/22/16.
 */

public class ProductModel implements Parcelable {

    private String name;
    private ArrayList<BrandModel> brands;

    public ProductModel() {
        this.brands = new ArrayList<>();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedList(brands);
    }

    protected ProductModel(Parcel in) {
        this();
        name = in.readString();
        brands = in.createTypedArrayList(BrandModel.CREATOR);
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<BrandModel> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<BrandModel> brands) {
        this.brands = brands;
    }


    @Override
    public int describeContents() {
        return 0;
    }



}
