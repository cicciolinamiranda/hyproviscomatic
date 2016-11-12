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

import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.ShoppingCartListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShoppingCartActivity extends BaseAuthenticatedActivity implements ShoppingCartListView.ShoppingCartListViewListener,
View.OnClickListener, RestCallServices.RestServiceListener {

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
    private RestCallServices restCallServices;
    private View mProgressView;
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
//        cartItemsSharedPref = new CartItemsSharedPref();
//        loginSharedPref = new LoginSharedPref();
        //TODO: to be replaced with Rest Call
//        productModels = Util.getInstance().generateProductModels();
//        cartItemsModelArrayList = cartItemsSharedPref.loadCartItems(ShoppingCartActivity.this,
//                loginSharedPref.getUsername(ShoppingCartActivity.this));

        sqlDatabaseHelper = new SqlDatabaseHelper(ShoppingCartActivity.this);
        productModels = new ArrayList<>();
        restCallServices = new RestCallServices(this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        restCallServices.getProducts(this, this);
//        populateList();
        mProgressView = findViewById(R.id.rl_shopping_cart_loading_layout);
        mProgressView.setVisibility(View.VISIBLE);


    }

    @Override
    public void deleteCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginModel.getUsername());
//        cartItemsSharedPref.removeCardItem(ShoppingCartActivity.this, cartItemsModel);

        sqlDatabaseHelper.deleteCartItem(cartItemsModel);
//        cartItemsModelArrayList = sqlDatabaseHelper.getAllUserCartItems(loginSharedPref.getUsername(ShoppingCartActivity.this));
        populateList();
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginModel.getUsername());
        sqlDatabaseHelper.updateCartItems(cartItemsModel);
        populateList();
    }

    private void populateList() {
        llNoRecords.setVisibility(View.GONE);
        llShopCartList.setVisibility(View.VISIBLE);
        llLowerLayouts.setVisibility(View.VISIBLE);
        cartItemsModelArrayList = sqlDatabaseHelper.getAllUserCartItems(loginModel.getUsername());
        if (cartItemsModelArrayList != null && !cartItemsModelArrayList.isEmpty() &&
                productModels != null && !productModels.isEmpty()) {
            cartItemsModelArrayAdapter = new ShoppingCartListView(ShoppingCartActivity.this, cartItemsModelArrayList,
                    productModels, this);
            cartItemsModelArrayAdapter.notifyDataSetChanged();
            lvShoppingCartList.setAdapter(cartItemsModelArrayAdapter);
            registerForContextMenu(lvShoppingCartList);

            double total = Util.getInstance().computeEstimatedTotal(productModels, cartItemsModelArrayList);
            tvEstimatedTotal.setText(String.format("%.2f", total)+" Php");
        } else {
            llNoRecords.setVisibility(View.VISIBLE);
            llShopCartList.setVisibility(View.GONE);
            llLowerLayouts.setVisibility(View.GONE);
        }
        mProgressView.setVisibility(View.GONE);
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
//                cartItemsSharedPref.removeAll(ShoppingCartActivity.this, loginSharedPref.getUsername(ShoppingCartActivity.this));
                finish();
                startActivity(getIntent());
                break;
        }
    }

    @Override
    public int getResultCode() {
        return 0;
    }

    @Override
    public void onSuccess(RestCalls callType, String string) {
        try {
            JSONObject jsnobject = new JSONObject(string);
            JSONArray jsonArray = new JSONArray();
            if(jsnobject != null) {
                jsonArray = jsnobject.getJSONArray("results");
            }

            if(jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i) != null) {
                        productModels.add(Util.getInstance().generateProductModelFromJson(jsonArray.getJSONObject(i)));
                    }
                    populateList();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        mProgressView.setVisibility(View.GONE);
        llNoRecords.setVisibility(View.VISIBLE);
        llShopCartList.setVisibility(View.GONE);
        llLowerLayouts.setVisibility(View.GONE);
    }
}

