package com.uprise.ordering.model;

import java.util.List;

/**
 * Created by cicciolina on 10/15/16.
 */
public class RegistrationModel {


    private String shopName;
    private String shopAddress;
    private String contactNum;
    private String shippingAddress;
    private String email;
    private String password;
    private List<BranchModel> branchModels;

    public RegistrationModel() {
        super();
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(final String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(final String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(final String contactNum) {
        this.contactNum = contactNum;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BranchModel> getBranchModels() {
        return branchModels;
    }

    public void setBranchModels(List<BranchModel> branchModels) {
        this.branchModels = branchModels;
    }
}
