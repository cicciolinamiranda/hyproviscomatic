package com.uprise.ordering.model;

import com.google.gson.annotations.Expose;

/**
 * Created by cicciolina on 10/25/16.
 */

public class CartItemsModel
{
    @Expose
    private int quantity;

    @Expose
    private String productModelId;

    @Expose
    private String branchId;

    @Expose
    private String userName;

    public CartItemsModel() {
        super();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
