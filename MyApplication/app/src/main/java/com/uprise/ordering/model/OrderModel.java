package com.uprise.ordering.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/24/16.
 */

public class OrderModel implements Parcelable {

    private String orderId;
    private String date;
    private ArrayList<CartItemsModel> cartItemsModels;
    private int quantity;
    private double totalAmount;

    public OrderModel() {}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(date);
        dest.writeInt(quantity);
        dest.writeDouble(totalAmount);
        dest.writeTypedList(cartItemsModels);
    }

    protected OrderModel(Parcel in) {
        orderId = in.readString();
        date = in.readString();
        quantity = in.readInt();
        totalAmount = in.readDouble();
        cartItemsModels = in.createTypedArrayList(CartItemsModel.CREATOR);
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<CartItemsModel> getCartItemsModels() {
        return cartItemsModels;
    }

    public void setCartItemsModels(ArrayList<CartItemsModel> cartItemsModels) {
        this.cartItemsModels = cartItemsModels;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
