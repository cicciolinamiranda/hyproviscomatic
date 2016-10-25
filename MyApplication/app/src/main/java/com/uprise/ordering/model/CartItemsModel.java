package com.uprise.ordering.model;

import android.view.View;

/**
 * Created by cicciolina on 10/25/16.
 */

public class CartItemsModel
{
    private int productIndex;
    private int branchIndex;
    private int quantity;
    private View cartItemsView;

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
