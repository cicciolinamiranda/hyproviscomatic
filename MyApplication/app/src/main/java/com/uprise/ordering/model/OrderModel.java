package com.uprise.ordering.model;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/24/16.
 */

public class OrderModel {

    private String orderId;
    private String date;
    private ArrayList<CartItemsModel> cartItemsModels;
    private int quantity;
    private double totalAmount;

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
}
