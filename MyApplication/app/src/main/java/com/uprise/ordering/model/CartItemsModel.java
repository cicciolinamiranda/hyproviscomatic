package com.uprise.ordering.model;

import android.view.View;

import com.google.gson.annotations.Expose;

/**
 * Created by cicciolina on 10/25/16.
 */

public class CartItemsModel
{
//    @Expose
//    private int productIndex;
//    @Expose
//    private int branchIndex;
    @Expose
    private int quantity;
    @Expose
    private View cartItemsView;

    @Expose
    private String productModelId;

    @Expose
    private String branchId;

    public CartItemsModel() {
        super();
    }

    public View getCartItemsView() {
        return cartItemsView;
    }

    public void setCartItemsView(View cartItemsView) {
        this.cartItemsView = cartItemsView;
    }

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

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
