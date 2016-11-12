package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by cicciolina on 10/25/16.
 */

public class CartItemsModel implements Parcelable
{
    @Expose
    private int quantity;

    @Expose
    private String productModelId;

    @Expose
    private String brandId;

    @Expose
    private String userName;

    @Expose
    private double price;

    public CartItemsModel() {
        super();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(quantity);
        dest.writeString(productModelId);
        dest.writeString(brandId);
        dest.writeString(userName);
        dest.writeDouble(price);
    }

    protected CartItemsModel(Parcel in) {
        quantity = in.readInt();
        productModelId = in.readString();
        brandId = in.readString();
        userName = in.readString();
        price = in.readDouble();
    }

    public static final Creator<CartItemsModel> CREATOR = new Creator<CartItemsModel>() {
        @Override
        public CartItemsModel createFromParcel(Parcel in) {
            return new CartItemsModel(in);
        }

        @Override
        public CartItemsModel[] newArray(int size) {
            return new CartItemsModel[size];
        }
    };

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductModelId() {
        return productModelId;
    }

    public void setProductModelId(String productModelId) {
        this.productModelId = productModelId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
