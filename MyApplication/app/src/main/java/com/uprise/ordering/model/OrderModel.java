package com.uprise.ordering.model;

import java.util.ArrayList;

/**
 * Created by cicciolina on 10/24/16.
 */

public class OrderModel {
    private ArrayList<ProductModel> brands;
    private int quantity;
    private double totalAmount;

    public ArrayList<ProductModel> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<ProductModel> brands) {
        this.brands = brands;
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
