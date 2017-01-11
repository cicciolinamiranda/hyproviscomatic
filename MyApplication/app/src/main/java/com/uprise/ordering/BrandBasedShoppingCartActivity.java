package com.uprise.ordering;

import android.content.DialogInterface;
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
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.LoginModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BrandBasedShoppingCartListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BrandBasedShoppingCartActivity extends BaseAuthenticatedActivity implements BrandBasedShoppingCartListView.ShoppingCartListViewListener,
        View.OnClickListener, RestCallServices.RestServiceListener {

    private LinearLayout llNoRecords;
    private RelativeLayout llShopCartList;
    private LinearLayout llLowerLayouts;
    private LinearLayout llProceedToCheckout;
    private ListView lvShoppingCartList;
    private TextView tvEstimatedTotal;
    private TextView tvDiscount;
    private TextView tvSubTotal;
    private ArrayAdapter<CartItemsModel> cartItemsModelArrayAdapter;
    //    private CartItemsSharedPref cartItemsSharedPref;
//    private LoginSharedPref loginSharedPref;
//    private ArrayList<ProductModel> productModels;
    private ArrayList<BrandModel> brandModels;
    private ArrayList<CartItemsModel> cartItemsModelArrayList;
    private RestCallServices restCallServices;
    private View mProgressView;
    private double total;
    private double netTotal;
    private double discountValue;
    private double computedDiscountPercentage;
    private String nextUrl;
    private boolean isNextUrl;
//    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        llNoRecords = (LinearLayout) findViewById(R.id.ll_shopping_cart_no_records);
        llShopCartList = (RelativeLayout) findViewById(R.id.ll_shopping_cart_list);
        lvShoppingCartList = (ListView) findViewById(R.id.list_shopping_cart);
        llLowerLayouts = (LinearLayout) findViewById(R.id.ll_shopping_cart_lower_layout);
        tvEstimatedTotal = (TextView) findViewById(R.id.tv_estimated_total_value);
        tvDiscount = (TextView) findViewById(R.id.tv_shopping_cart_discount_value);
        tvSubTotal = (TextView) findViewById(R.id.tv_order_item__sub_total_value);
        llProceedToCheckout = (LinearLayout) findViewById(R.id.ll_shopping_cart_proceed_checkout);
        llProceedToCheckout.setOnClickListener(this);

        loginModel = new LoginModel();
        sqlDatabaseHelper = new SqlDatabaseHelper(BrandBasedShoppingCartActivity.this);
        brandModels = new ArrayList<>();
        restCallServices = new RestCallServices(this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        final String brandEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_brand);
        final String discountEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_discount);
        if(loginModel != null && loginModel.getToken() != null &&
                Util.getInstance().isNetworkAvailable(this)) {
            restCallServices.getProducts(this, this, loginModel.getToken(), brandEndpoint);
            restCallServices.getDiscount(this, this, loginModel.getToken(), discountEndpoint);
            mProgressView = findViewById(R.id.rl_shopping_cart_loading_layout);
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            llNoRecords.setVisibility(View.VISIBLE);
            llShopCartList.setVisibility(View.GONE);
            llLowerLayouts.setVisibility(View.GONE);
        }


    }

    @Override
    public void deleteCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginModel.getUsername());
//        cartItemsSharedPref.removeCardItem(ShoppingCartActivity.this, cartItemsModel);
        isNextUrl = false;
        sqlDatabaseHelper.deleteCartItem(cartItemsModel);
//        cartItemsModelArrayList = sqlDatabaseHelper.getAllUserCartItems(loginSharedPref.getUsername(ShoppingCartActivity.this));
        populateList();
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(loginModel.getUsername());
        isNextUrl = false;
        sqlDatabaseHelper.updateCartItems(cartItemsModel);
        populateList();
    }

    private void populateList() {
        llNoRecords.setVisibility(View.GONE);
        llShopCartList.setVisibility(View.VISIBLE);
        llLowerLayouts.setVisibility(View.VISIBLE);


        if(!isNextUrl) cartItemsModelArrayList = sqlDatabaseHelper.getAllUserCartItems(loginModel.getUsername());
        if (cartItemsModelArrayList != null && !cartItemsModelArrayList.isEmpty() &&
                brandModels != null && !brandModels.isEmpty()) {
            cartItemsModelArrayAdapter = new BrandBasedShoppingCartListView(BrandBasedShoppingCartActivity.this, cartItemsModelArrayList,
                    brandModels, this);
            cartItemsModelArrayAdapter.notifyDataSetChanged();
            lvShoppingCartList.setAdapter(cartItemsModelArrayAdapter);
            registerForContextMenu(lvShoppingCartList);

            total = Util.getInstance().computeBrandEstimatedTotal(brandModels, cartItemsModelArrayList);
            computedDiscountPercentage = 0;
            computedDiscountPercentage = total * (discountValue/100);
            netTotal = total - computedDiscountPercentage;
            tvSubTotal.setText(String.format("%.2f", total)+" Php");
            tvDiscount.setText(String.format("%.2f", computedDiscountPercentage)+" Php");
            tvEstimatedTotal.setText(String.format("%.2f", netTotal)+" Php");
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
                startActivity(new Intent(BrandBasedShoppingCartActivity.this, MainActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_shopping_cart_proceed_checkout:

                mProgressView.setVisibility(View.VISIBLE);
                llShopCartList.setVisibility(View.GONE);
                llLowerLayouts.setVisibility(View.GONE);

                //TODO: Must transfer in MyOrders
                restCallServices.postPurchase(BrandBasedShoppingCartActivity.this, new RestCallServices.RestServiceListener() {
                    @Override
                    public int getResultCode() {
                        return 0;
                    }

                    @Override
                    public void onSuccess(RestCalls callType, String string) {
                        mProgressView.setVisibility(View.GONE);
                        for(CartItemsModel cartItemsModel: cartItemsModelArrayList) {
                            cartItemsModel.setUserName(loginModel.getUsername());
                            sqlDatabaseHelper.deleteCartItem(cartItemsModel);
                        }

                        Util.getInstance().showDialog(BrandBasedShoppingCartActivity.this, getString(R.string.purchase_approval_msg), getString(R.string.action_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        startActivity(new Intent(BrandBasedShoppingCartActivity.this, OrderListActivity.class));
                                    }
                                });

                    }

                    @Override
                    public void onFailure(RestCalls callType, final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Util.getInstance().showDialog(BrandBasedShoppingCartActivity.this, msg,BrandBasedShoppingCartActivity.this.getString(R.string.action_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                mProgressView.setVisibility(View.GONE);
                                                llShopCartList.setVisibility(View.VISIBLE);
                                                llLowerLayouts.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        });
                    }
                }, loginModel, cartItemsModelArrayList, total);

                break;
        }
    }

    @Override
    public int getResultCode() {
        return 0;
    }

    @Override
    public void onSuccess(RestCalls callType, String string) {

        if(callType == RestCalls.PRODUCTS) {
            try {
                JSONObject jsnobject = new JSONObject(string);
                JSONArray jsonArray = new JSONArray();
                if(jsnobject != null) {
                    jsonArray = jsnobject.getJSONArray("results");
                }

                if(jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if(jsonArray.getJSONObject(i) != null) {
                            brandModels.add(Util.getInstance().generateBrandModelFromJson(jsonArray.getJSONObject(i)));
                        }
                        populateList();
                    }
                }

                if(jsnobject.getString("next") != null && !jsnobject.getString("next").isEmpty() && !jsnobject.getString("next").contentEquals("null"))  {
                    nextUrl = jsnobject.getString("next");
                    isNextUrl = true;
                    restCallServices.getProducts(this, this, loginModel.getToken(), nextUrl);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if(callType == RestCalls.DISCOUNT) {
            try {
                JSONObject jsnobject = new JSONObject(string);
                JSONArray jsonArray = new JSONArray();
                if(jsnobject != null) {
                    jsonArray = jsnobject.getJSONArray("results");
                }

                if(jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if(jsonArray.getJSONObject(i) != null) {
                            if(jsonArray.getJSONObject(i).getString("value") != null) {
                                discountValue = Double.parseDouble(jsonArray.getJSONObject(i).getString("value"));
                            }
                        }
                        populateList();
                    }
                }

                if(jsnobject.getString("next") != null && !jsnobject.getString("next").isEmpty() && !jsnobject.getString("next").contentEquals("null"))  {
                    nextUrl = jsnobject.getString("next");
                    isNextUrl = true;
                    restCallServices.getProducts(this, this, loginModel.getToken(), nextUrl);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        mProgressView.setVisibility(View.GONE);
        if(brandModels == null || brandModels.isEmpty()) {
            llNoRecords.setVisibility(View.VISIBLE);
            llShopCartList.setVisibility(View.GONE);
            llLowerLayouts.setVisibility(View.GONE);
        }
    }
}
