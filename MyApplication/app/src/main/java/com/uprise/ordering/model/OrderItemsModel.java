package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cicciolina on 11/28/16.
 */

public class OrderItemsModel implements Parcelable {
    private String productName;
    private String brandName;
    private double price;
    private int quantity;
    private String photoUrl;

    public OrderItemsModel() {
        super();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(brandName);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(photoUrl);
    }

    protected OrderItemsModel(Parcel in) {
        productName = in.readString();
        brandName = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        photoUrl = in.readString();
    }

    public static final Creator<OrderItemsModel> CREATOR = new Creator<OrderItemsModel>() {
        @Override
        public OrderItemsModel createFromParcel(Parcel in) {
            return new OrderItemsModel(in);
        }

        @Override
        public OrderItemsModel[] newArray(int size) {
            return new OrderItemsModel[size];
        }
    };

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
