package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.shared.CartItemsSharedPref;
import com.uprise.ordering.shared.LoginSharedPref;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BrandsPagerAdapter;
import com.uprise.ordering.view.ProductsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends BaseAuthenticatedActivity implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupExpandListener,
        BrandsPagerAdapter.BrandsAdapterListener,
        ProductsAdapter.ProductsAdapterListener {

    private ProductsAdapter productsAdapter;
    private ExpandableListView expandableListView;
    private ArrayList<ProductModel> productModels;
//    private CartItemsSharedPref sharedPreferences;
//    private LoginSharedPref loginSharedPref;
    private String username;
    private int lastExpandedPosition = -1;
    private int lastViewPagerPosition = -1;
    private ViewPager childView;
    private boolean isAddOrSaved;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_now);
        expandableListView = (ExpandableListView) findViewById(R.id.el_shop_now_products);

        //TODO: to be replaced with Rest Call
        productModels = Util.getInstance().generateProductModels();
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupExpandListener(this);
        loginSharedPref = new LoginSharedPref();
        cartItemsSharedPref = new CartItemsSharedPref();
        username = loginSharedPref.getUsername(this);

        List<CartItemsModel> items = cartItemsSharedPref.loadCartItems(this, username);
        populateProductList(items);
        getSupportActionBar().setTitle("Products");
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        return true;
    }

    @Override
    public void onGroupExpand(int i) {
        isAddOrSaved = false;
        lastExpandedPosition = i;
    }

    @Override
    public void addToCart(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(username);
        cartItemsSharedPref.addCartItems(this, cartItemsModel);
        List<CartItemsModel> items = cartItemsSharedPref.loadCartItems(this, username);
        populateProductList(items);
        isAddOrSaved = true;
        Util.getInstance().showSnackBarToast(this, getString(R.string.changes_saved));

    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(username);
        cartItemsSharedPref.editCardItem(this, cartItemsModel);
        List<CartItemsModel> items = cartItemsSharedPref.loadCartItems(this, username);
        populateProductList(items);
        isAddOrSaved = true;
        Util.getInstance().showSnackBarToast(this, getString(R.string.changes_saved));

    }

    @Override
    public void onPageChange(ViewPager viewPager, int position) {
        childView = viewPager;
        lastViewPagerPosition = position;
    }

    @Override
    public boolean isAddOrSaved() {
        return isAddOrSaved;
    }

    @Override
    public int pageSaved() {
        return lastViewPagerPosition;
    }

    private void populateProductList(List<CartItemsModel> items) {
//        List<CartItemsModel> items = sharedPreferences.loadCartItems(getContext(), username);

        if(items !=null && !items.isEmpty()) {
            productsAdapter = new ProductsAdapter(this, productModels, expandableListView, this, this, items);
            productsAdapter.notifyDataSetChanged();
        }
        else {
            cartItemsSharedPref.storeCartItems(this, new ArrayList<CartItemsModel>());
            productsAdapter = new ProductsAdapter(this, productModels, expandableListView, this, this,new ArrayList<CartItemsModel>());
        }
        expandableListView.setAdapter(productsAdapter);

        if(lastExpandedPosition != -1) {
            expandableListView.expandGroup(lastExpandedPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(ProductsActivity.this, MainActivity.class));
                break;
        }
        return true;
    }
}
