package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BrandsPagerAdapter;
import com.uprise.ordering.view.ProductsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DistributorShopActivity extends LandingSubPageBaseActivity implements ExpandableListView.OnChildClickListener,
        BrandsPagerAdapter.BrandsAdapterListener,
        ProductsAdapter.ProductsAdapterListener,
        RestCallServices.RestServiceListener {

    private ProductsAdapter productsAdapter;
    private ExpandableListView expandableListView;
    private ArrayList<ProductModel> productModels;
    private View mProgressView;
    private RestCallServices restCallServices;
    private LinearLayout llNoRecords;
    private MenuItem previousMenu;
    private MenuItem nextMenu;
    private String nextUrl;
    private String prevUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_now);
        productModels = new ArrayList<>();
        expandableListView = (ExpandableListView) findViewById(R.id.el_shop_now_products);

        expandableListView.setOnChildClickListener(this);
        llNoRecords =(LinearLayout) findViewById(R.id.ll_existing_products_no_records);
        restCallServices = new RestCallServices(this);
        final String productsEndpoint = getResources().getString(R.string.endpoint_server)
        + getResources().getString(R.string.endpoint_get_products);
        restCallServices.getDistributorShop(this, this, productsEndpoint);
        mProgressView = findViewById(R.id.rl_shop_now_loading_layout);
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        ProductsAdapter productsAdapter = (ProductsAdapter) parent.getExpandableListAdapter();
        int iCount;
        int iIdx;
        Object item;

        iCount = productsAdapter.getChildrenCount(groupPosition);
        for ( iIdx = 0; iIdx < iCount; ++iIdx )
        {
            item = productsAdapter.getChild(groupPosition, iIdx);
            if ( item != null )
            {
                // Here you would cast item to the appropriate type for this row
                LinearLayout llBrandPrice = (LinearLayout) v.findViewById(R.id.ll_brand_price);
                LinearLayout llBrandQty = (LinearLayout) v.findViewById(R.id.ll_brand_qty);
                LinearLayout llQtyButtons = (LinearLayout) v.findViewById(R.id.ll_item_qty_buttons);
                LinearLayout llTransacBtn = (LinearLayout) v.findViewById(R.id.ll_transac_buttons);

                if ( iIdx == childPosition )
                {
                    // Here you would toggle checked state in the data for this item
                     llBrandPrice.setVisibility(View.GONE);
                    llQtyButtons.setVisibility(View.GONE);
                    llBrandQty.setVisibility(View.GONE);
                    llTransacBtn.setVisibility(View.GONE);
                }
                else
                {
                    // Here you would clear checked state in the data for this item
                }
            }
        }
        parent.invalidateViews();


        return false;
    }


    @Override
    public void addToCart(CartItemsModel cartItemsModel) {
        //nothing to do
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        //nothing to do
    }

    @Override
    public void onPageChange(ViewPager viewPager, int position) {

    }

    @Override
    public boolean isAddOrSaved() {
        return false;
    }

    @Override
    public int pageSaved() {
        return 0;
    }

    private void populateProductList() {
        productsAdapter = new ProductsAdapter(this, productModels, expandableListView, this, this,new ArrayList<CartItemsModel>());
        productsAdapter.notifyDataSetChanged();
        expandableListView.setAdapter(productsAdapter);
        mProgressView.setVisibility(View.GONE);
        if(productModels.size() <=0) llNoRecords.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent mainIntent;
        switch (item.getItemId()) {
            case android.R.id.home:
                mainIntent = new Intent(this, LandingActivity.class);
                startActivity(mainIntent);
                finish();
                break;
            case R.id.menu_orderlist_prev:
                productModels.clear();
                restCallServices.getDistributorShop(DistributorShopActivity.this, this, prevUrl);
                break;
            case R.id.menu_orderlist_next:
                productModels.clear();
                restCallServices.getDistributorShop(DistributorShopActivity.this, this, nextUrl);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.order_list_menu, menu);
        previousMenu = menu.findItem(R.id.menu_orderlist_prev);
        previousMenu.setVisible(false);
        nextMenu = menu.findItem(R.id.menu_orderlist_next);
        nextMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent mainIntent = new Intent(this, LandingActivity.class);
            startActivity(mainIntent);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public int getResultCode() {
        return 0;
    }

    @Override
    public void onSuccess(RestCalls callType, String string) {
        mProgressView.setVisibility(View.GONE);
        try {
            JSONObject jsnobject = new JSONObject(string);
            JSONArray jsonArray = new JSONArray();
            if(jsnobject != null) {
                jsonArray = jsnobject.getJSONArray("results");
            }

            if(jsnobject.getString("next") != null && !jsnobject.getString("next").isEmpty() && !jsnobject.getString("next").contentEquals("null"))  {

                nextUrl = jsnobject.getString("next");
                nextMenu.setVisible(true);
            }
            if(jsnobject.getString("previous") != null && !jsnobject.getString("previous").isEmpty()
                    && !jsnobject.getString("previous").contentEquals("null")) {
                prevUrl = jsnobject.getString("previous");
                previousMenu.setVisible(true);
            }


            if(jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i) != null) {
                        productModels.add(Util.getInstance().generateDistributorShopFromJson(jsonArray.getJSONObject(i)));
                    }
                    populateProductList();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            if(productModels.size() <=0) {llNoRecords.setVisibility(View.VISIBLE);}
        }

    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        mProgressView.setVisibility(View.GONE);

        if(productModels.size() <=0) llNoRecords.setVisibility(View.VISIBLE);
    }

}
