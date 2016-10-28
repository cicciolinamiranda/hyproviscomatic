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
        settings = context.getSharedPreferences(ApplicationConstants.CART_ITEMS,Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String cartItems = gson.toJson(cartItemsModelList);
        editor.putString(ApplicationConstants.CART_ITEMS, cartItems);
        editor.commit();
    }

    public ArrayList loadCartItems(Context context, String username) {
// used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List<CartItemsModel> carts;
        settings = context.getSharedPreferences(ApplicationConstants.CART_ITEMS,Context.MODE_PRIVATE);
        if (settings.contains(ApplicationConstants.CART_ITEMS)) {
            String cartsStr = settings.getString(ApplicationConstants.CART_ITEMS, "");
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            CartItemsModel[] cartItems = gson.fromJson(cartsStr,CartItemsModel[].class);
            carts = Arrays.asList(cartItems);
            carts = new ArrayList(carts);
        } else
            return null;

        List<CartItemsModel> filteredCarts = new ArrayList<>();

        if(carts != null && !carts.isEmpty()) {
            for (CartItemsModel cart : carts) {
                if (cart.getUserName() != null && cart.getUserName().trim().equals(username.trim())) {
                    filteredCarts.add(cart);
                }
            }
        }
        return (ArrayList) filteredCarts;
    }

    public void addCartItems(Context context, CartItemsModel cartItemsModel) {
        List<CartItemsModel> cartItems = loadCartItems(context, cartItemsModel.getUserName());
        if (cartItems == null)
            cartItems = new ArrayList();
        cartItems.add(cartItemsModel);
        storeCartItems(context, cartItems);
    }

    public void editCardItem(Context context, CartItemsModel cartItemsModel) {
        List<CartItemsModel> cartItems = loadCartItems(context, cartItemsModel.getUserName());
        if (cartItems == null)
            cartItems = new ArrayList();
        //cartItems.add(cartItemsModel);

        int index =0;
        for(CartItemsModel model: cartItems) {
            if(model != null && model.getBranchId().equalsIgnoreCase(cartItemsModel.getBranchId()) &&
                    model.getProductModelId().equalsIgnoreCase(cartItemsModel.getProductModelId())) {
                cartItems.set(index, cartItemsModel);
            }
            index++;
        }
        storeCartItems(context, cartItems);
    }

    public void removeCardItem(Context context, CartItemsModel cartItemsModel) {
        List<CartItemsModel> cartItems = loadCartItems(context, cartItemsModel.getUserName());
        if (cartItems != null) {
            for(int i=0; i<cartItems.size(); i++) {
                if(cartItems.get(i) !=null && cartItems.get(i).getBranchId().equalsIgnoreCase(cartItemsModel.getBranchId())
                        && cartItems.get(i).getProductModelId().equalsIgnoreCase(cartItemsModel.getProductModelId())) {
                    cartItems.remove(cartItems.get(i));
                }
            }
            storeCartItems(context, cartItems);
        }
    }

    public void removeAll(Context context, String username) {
        List<CartItemsModel> cartItems = loadCartItems(context, username);
        cartItems.clear();
        storeCartItems(context, cartItems);
    }
}
