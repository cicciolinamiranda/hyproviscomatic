package com.uprise.ordering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.shared.CartItemsSharedPref;
import com.uprise.ordering.shared.LoginSharedPref;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.ShoppingCartListView;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity implements ShoppingCartListView.ShoppingCartListViewListener {

    private LinearLayout llNoRecords;
    private LinearLayout llShopCartList;
    private ListView lvShoppingCartList;
    private ArrayAdapter<CartItemsModel> cartItemsModelArrayAdapter;
    private CartItemsSharedPref cartItemsSharedPref;
    private LoginSharedPref loginSharedPref;
    private ArrayList<ProductModel> productModels;
    private ArrayList<CartItemsModel> cartItemsModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        llNoRecords = (LinearLayout) findViewById(R.id.ll_shopping_cart_no_records);
        llShopCartList = (LinearLayout) findViewById(R.id.ll_shopping_cart_list);
        lvShoppingCartList = (ListView) findViewById(R.id.list_shopping_cart);
        cartItemsSharedPref = new CartItemsSharedPref();
        loginSharedPref = new LoginSharedPref();
        //TODO: to be replaced with Rest Call
        productModels = Util.getInstance().generateProductModels();
        cartItemsModelArrayList = cartItemsSharedPref.loadCartItems(ShoppingCartActivity.this,
                loginSharedPref.getUsername(ShoppingCartActivity.this));
        populateListt();
        getSupportActionBar().setTitle("Cart Items");

    }

    @Override
    public void deleteCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginSharedPref.getUsername(ShoppingCartActivity.this));
        cartItemsSharedPref.removeCardItem(ShoppingCartActivity.this, cartItemsModel);
        cartItemsModelArrayList = cartItemsSharedPref.loadCartItems(ShoppingCartActivity.this,
                loginSharedPref.getUsername(ShoppingCartActivity.this));
//        populateListt(cartItemsModelArrayList);
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginSharedPref.getUsername(ShoppingCartActivity.this));
        cartItemsSharedPref.editCardItem(ShoppingCartActivity.this, cartItemsModel);
        cartItemsModelArrayList = cartItemsSharedPref.loadCartItems(ShoppingCartActivity.this,
                loginSharedPref.getUsername(ShoppingCartActivity.this));
//        populateListt(cartItemsModelArrayList);
    }

    private void populateListt() {
        llNoRecords.findViewById(View.GONE);
        llShopCartList.findViewById(View.VISIBLE);

        if (cartItemsModelArrayList != null && !cartItemsModelArrayList.isEmpty()) {
            cartItemsModelArrayAdapter = new ShoppingCartListView(ShoppingCartActivity.this, cartItemsModelArrayList,
                    productModels, this);
            cartItemsModelArrayAdapter.notifyDataSetChanged();
            lvShoppingCartList.setAdapter(cartItemsModelArrayAdapter);
            registerForContextMenu(lvShoppingCartList);
        } else {
            llNoRecords.setVisibility(View.VISIBLE);
            llShopCartList.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    }

