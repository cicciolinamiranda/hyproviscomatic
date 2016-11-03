package com.uprise.ordering;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cicciolina on 11/3/16.
 */

public enum OrderStatus implements Parcelable {
    APPROVED(0,"Approved"),
    PENDING(1,"Pending"),
    CANCELED(2,"Canceled");

    private int orderStatus;
    private String label;

    OrderStatus(int orderStatus, String label) {
        this.orderStatus = orderStatus;
        this.label = label;
    }

    OrderStatus(Parcel in) {
        orderStatus = in.readInt();
        label = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderStatus);
        dest.writeString(label);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderStatus> CREATOR = new Creator<OrderStatus>() {
        @Override
        public OrderStatus createFromParcel(Parcel in) {
            OrderStatus option = OrderStatus.values()[in.readInt()];
            return option;
        }

        @Override
        public OrderStatus[] newArray(int size) {
            return new OrderStatus[size];
        }
    };

    public int getOrderStatus() {
        return orderStatus;
    }

    public String getLabel() {
        return label;
    }
}
