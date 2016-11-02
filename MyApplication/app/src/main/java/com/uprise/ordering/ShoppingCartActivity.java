package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.shared.CartItemsSharedPref;
import com.uprise.ordering.shared.LoginSharedPref;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.ShoppingCartListView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends BaseAuthenticatedActivity implements ShoppingCartListView.ShoppingCartListViewListener,
View.OnClickListener {

    private LinearLayout llNoRecords;
    private RelativeLayout llShopCartList;
    private LinearLayout llLowerLayouts;
    private LinearLayout llProceedToCheckout;
    private ListView lvShoppingCartList;
    private TextView tvEstimatedTotal;
    private ArrayAdapter<CartItemsModel> cartItemsModelArrayAdapter;
//    private CartItemsSharedPref cartItemsSharedPref;
//    private LoginSharedPref loginSharedPref;
    private ArrayList<ProductModel> productModels;
    private ArrayList<CartItemsModel> cartItemsModelArrayList;
//    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

//        decimalFormat = new DecimalFormat("#.##");
        llNoRecords = (LinearLayout) findViewById(R.id.ll_shopping_cart_no_records);
        llShopCartList = (RelativeLayout) findViewById(R.id.ll_shopping_cart_list);
        lvShoppingCartList = (ListView) findViewById(R.id.list_shopping_cart);
        llLowerLayouts = (LinearLayout) findViewById(R.id.ll_shopping_cart_lower_layout);
        tvEstimatedTotal = (TextView) findViewById(R.id.tv_estimated_total_value);
        llProceedToCheckout = (LinearLayout) findViewById(R.id.ll_shopping_cart_proceed_checkout);
        llProceedToCheckout.setOnClickListener(this);
        cartItemsSharedPref = new CartItemsSharedPref();
        loginSharedPref = new LoginSharedPref();
        //TODO: to be replaced with Rest Call
        productModels = Util.getInstance().generateProductModels();
        cartItemsModelArrayList = cartItemsSharedPref.loadCartItems(ShoppingCartActivity.this,
                loginSharedPref.getUsername(ShoppingCartActivity.this));
        populateList();
        getSupportActionBar().setTitle("Cart Items");


    }

    @Override
    public void deleteCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginSharedPref.getUsername(ShoppingCartActivity.this));
        cartItemsSharedPref.removeCardItem(ShoppingCartActivity.this, cartItemsModel);
        cartItemsModelArrayList = cartItemsSharedPref.loadCartItems(ShoppingCartActivity.this,
                loginSharedPref.getUsername(ShoppingCartActivity.this));
        populateList();
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginSharedPref.getUsername(ShoppingCartActivity.this));
        cartItemsSharedPref.editCardItem(ShoppingCartActivity.this, cartItemsModel);
        cartItemsModelArrayList = cartItemsSharedPref.loadCartItems(ShoppingCartActivity.this,
                loginSharedPref.getUsername(ShoppingCartActivity.this));
        populateList();
    }

    private void populateList() {
        llNoRecords.setVisibility(View.GONE);
        llShopCartList.setVisibility(View.VISIBLE);
        llLowerLayouts.setVisibility(View.VISIBLE);

        if (cartItemsModelArrayList != null && !cartItemsModelArrayList.isEmpty()) {
            cartItemsModelArrayAdapter = new ShoppingCartListView(ShoppingCartActivity.this, cartItemsModelArrayList,
                    productModels, this);
            cartItemsModelArrayAdapter.notifyDataSetChanged();
            lvShoppingCartList.setAdapter(cartItemsModelArrayAdapter);
            registerForContextMenu(lvShoppingCartList);

            double total = computeEstimatedTotal(cartItemsModelArrayList);
            tvEstimatedTotal.setText(String.format("%.2f", total)+" Php");
        } else {
            llNoRecords.setVisibility(View.VISIBLE);
            llShopCartList.setVisibility(View.GONE);
            llLowerLayouts.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(ShoppingCartActivity.this, MainActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_shopping_cart_proceed_checkout:
                Util.getInstance().showSnackBarToast(this, getString(R.string.action_checkout));

                //TODO: Must transfer in MyOrders
                cartItemsSharedPref.removeAll(ShoppingCartActivity.this, loginSharedPref.getUsername(ShoppingCartActivity.this));
                finish();
                startActivity(getIntent());
                break;
        }
    }

    private double computeEstimatedTotal(List<CartItemsModel> cartItemsModels) {
        double total = 0d;


        for (CartItemsModel model : cartItemsModels) {
            if (Util.getInstance().isProductsAndCartItemsNotEmpty(productModels, cartItemsModels)) {


                ProductModel matchedProductModel = Util.getInstance().getMatchedProductModel(model, productModels);

                if (matchedProductModel != null &&
                        !matchedProductModel.getBrands().isEmpty()) {
                    BrandModel matchedBrandModel = Util.getInstance().getMatchedBrandModel(model, matchedProductModel.getBrands(), matchedProductModel.getId());
                    if (matchedBrandModel != null) {
                        total += matchedBrandModel.getPrice() * model.getQuantity();
                    }
                }

            }
        }
        return total;
    }
}

