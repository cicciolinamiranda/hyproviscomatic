package com.uprise.ordering.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.shared.CartItemsSharedPref;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BrandsPagerAdapter;
import com.uprise.ordering.view.ProductsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 10/22/16.
 */

public class ProductsFragment extends Fragment implements ExpandableListView.OnChildClickListener, BrandsPagerAdapter.BrandsAdapterListener {

    private View fragmentView;
    private View childView;
    private ProductsAdapter productsAdapter;
    private ExpandableListView expandableListView;
    private ArrayList<ProductModel> productModels;
//    private List<CartItemsModel> cartItemsModelList;
    private CartItemsSharedPref sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.layout_shop_now, container, false);
        expandableListView = (ExpandableListView) fragmentView.findViewById(R.id.el_shop_now_products);

        //TODO: to be replaced with Rest Call
        productModels = Util.getInstance().generateProductModels();
        productsAdapter = new ProductsAdapter(getContext(), productModels, expandableListView, this);
        expandableListView.setAdapter(productsAdapter);
        expandableListView.setOnChildClickListener(this);
        sharedPreferences = new CartItemsSharedPref();

        return fragmentView;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, final int i, final int i1, long l) {
        return true;
    }


    @Override
    public void addToCart(CartItemsModel cartItemsModel) {
        List<CartItemsModel> items = sharedPreferences.loadCartItems(getContext());

        if(items != null && !items.isEmpty()) {
            sharedPreferences.addCartItems(getContext(), cartItemsModel);
        } else {
            sharedPreferences.storeCartItems(getContext(), new ArrayList<CartItemsModel>());
        }
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        sharedPreferences.editCardItem(getContext(), cartItemsModel);

    }
}

