package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/22/16.
 */

public class ProductModel implements Parcelable {

    private String id;
    private String name;
    private ArrayList<BrandModel> brands;
    private String productPhotoUrl;
    private String attributeId;
    private double price;

    public ProductModel() {
        this.brands = new ArrayList<>();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeTypedList(brands);
        parcel.writeString(productPhotoUrl);
        parcel.writeString(attributeId);
        parcel.writeDouble(price);
    }

    protected ProductModel(Parcel in) {
        this();
        id = in.readString();
        name = in.readString();
        brands = in.createTypedArrayList(BrandModel.CREATOR);
        productPhotoUrl = in.readString();
        attributeId = in.readString();
        price = in.readDouble();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductPhotoUrl() {
        return productPhotoUrl;
    }

    public void setProductPhotoUrl(String productPhotoUrl) {
        this.productPhotoUrl = productPhotoUrl;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }



}
