package com.uprise.ordering.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.CartItemsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cicciolina on 10/26/16.
 */
//TODO: WILL BE REPLACED BY SQL
public class CartItemsSharedPref {

    public CartItemsSharedPref() {
        super();
    }

    public void storeCartItems(Context context, List<CartItemsModel> cartItemsModelList) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE,Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String cartItems = gson.toJson(cartItemsModelList);
        editor.putString(ApplicationConstants.CART_ITEMS, cartItems);
        editor.commit();
    }

    public ArrayList loadCartItems(Context context) {
// used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List<CartItemsModel> carts;
        settings = context.getSharedPreferences(ApplicationConstants.APP_CODE,Context.MODE_PRIVATE);
        if (settings.contains(ApplicationConstants.CART_ITEMS)) {
            String cartsStr = settings.getString(ApplicationConstants.CART_ITEMS, "");
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            CartItemsModel[] cartItems = gson.fromJson(cartsStr,CartItemsModel[].class);
            carts = Arrays.asList(cartItems);
            carts = new ArrayList(carts);
        } else
            return null;
        return (ArrayList) carts;
    }

    public void addCartItems(Context context, CartItemsModel cartItemsModel) {
        List<CartItemsModel> cartItems = loadCartItems(context);
        if (cartItems == null)
            cartItems = new ArrayList();
        cartItems.add(cartItemsModel);
        storeCartItems(context, cartItems);
    }

    public void editCardItem(Context context, CartItemsModel cartItemsModel) {
        List<CartItemsModel> cartItems = loadCartItems(context);
        if (cartItems == null)
            cartItems = new ArrayList();
        cartItems.add(cartItemsModel);

        int index =0;
        for(CartItemsModel model: cartItems) {
            if(model.getBranchIndex() == cartItemsModel.getBranchIndex() &&
                    model.getProductIndex() == cartItemsModel.getProductIndex()) {
                cartItems.set(index, cartItemsModel);
            }
            index++;
        }
        storeCartItems(context, cartItems);
    }

    public void removeCardItem(Context context, CartItemsModel cartItemsModel) {
        List<CartItemsModel> cartItems = loadCartItems(context);
        if (cartItems != null) {
            cartItems.remove(cartItemsModel);
            storeCartItems(context, cartItems);
        }
    }
}
