package com.uprise.ordering.model;

import android.view.View;

import com.google.gson.annotations.Expose;

/**
 * Created by cicciolina on 10/25/16.
 */

public class CartItemsModel
{
    @Expose
    private int productIndex;
    @Expose
    private int branchIndex;
    @Expose
    private int quantity;
    @Expose
    private View cartItemsView;

    public CartItemsModel() {
        super();
    }

    public CartItemsModel(int productIndex, int branchIndex, int quantity, View cartItemsView) {
        this.productIndex = productIndex;
        this.branchIndex = branchIndex;
        this.quantity = quantity;
        this.cartItemsView = cartItemsView;
    }

    public View getCartItemsView() {
        return cartItemsView;
    }

    public void setCartItemsView(View cartItemsView) {
        this.cartItemsView = cartItemsView;
    }

    public int getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(int productIndex) {
        this.productIndex = productIndex;
    }

    public int getBranchIndex() {
        return branchIndex;
    }

    public void setBranchIndex(int branchIndex) {
        this.branchIndex = branchIndex;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
