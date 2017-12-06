package com.uprise.ordering.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/24/16.
 */

public class OrderModel implements Parcelable {

    private String orderId;
    private String date;
//    private ArrayList<CartItemsModel> cartItemsModels;
    private ArrayList<OrderItemsModel> orderItemsModels;
//    private int quantity;
    private double totalAmount;
    private double discount;
    private double shippingFee;
    private String paymentMethod;
//    private OrderStatus orderStatus;
    private String orderStatus;

    public OrderModel() {}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(date);
//        dest.writeInt(quantity);
        dest.writeDouble(totalAmount);
//        dest.writeTypedList(cartItemsModels);
        dest.writeTypedList(orderItemsModels);
//        dest.writeInt(orderStatus);
        dest.writeString(orderStatus);
        dest.writeDouble(discount);
        dest.writeDouble(shippingFee);
        dest.writeString(paymentMethod);
    }


    @SuppressLint("NewApi")
    protected OrderModel(Parcel in) {
        orderId = in.readString();
        date = in.readString();
//        quantity = in.readInt();
        totalAmount = in.readDouble();
//        cartItemsModels = in.createTypedArrayList(CartItemsModel.CREATOR);
        orderItemsModels = in.createTypedArrayList(OrderItemsModel.CREATOR);
//        orderStatus = in.readTypedObject(OrderStatus.CREATOR);
        orderStatus = in.readString();
        discount = in.readDouble();
        shippingFee = in.readDouble();
        paymentMethod = in.readString();
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

//    public ArrayList<CartItemsModel> getCartItemsModels() {
//        return cartItemsModels;
//    }
//
//    public void setCartItemsModels(ArrayList<CartItemsModel> cartItemsModels) {
//        this.cartItemsModels = cartItemsModels;
//    }

//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

//    public OrderStatus getOrderStatus() {
//        return orderStatus;
//    }
//
//    public void setOrderStatus(OrderStatus orderStatus) {
//        this.orderStatus = orderStatus;
//    }


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public ArrayList<OrderItemsModel> getOrderItemsModels() {
        return orderItemsModels;
    }

    public void setOrderItemsModels(ArrayList<OrderItemsModel> orderItemsModels) {
        this.orderItemsModels = orderItemsModels;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
