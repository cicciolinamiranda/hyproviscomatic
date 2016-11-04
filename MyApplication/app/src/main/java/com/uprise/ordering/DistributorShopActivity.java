package com.uprise.ordering;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.shared.CartItemsSharedPref;
import com.uprise.ordering.shared.LoginSharedPref;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BrandsPagerAdapter;
import com.uprise.ordering.view.ProductsAdapter;

import java.util.ArrayList;
import java.util.List;

public class DistributorShopActivity extends LandingSubPageBaseActivity implements ExpandableListView.OnChildClickListener,
        BrandsPagerAdapter.BrandsAdapterListener,
        ProductsAdapter.ProductsAdapterListener {

    private ProductsAdapter productsAdapter;
    private ExpandableListView expandableListView;
    private ArrayList<ProductModel> productModels;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_now);

        expandableListView = (ExpandableListView) findViewById(R.id.el_shop_now_products);

        //TODO: to be replaced with Rest Call
        productModels = Util.getInstance().generateProductModels();
        expandableListView.setOnChildClickListener(this);
        loginSharedPref = new LoginSharedPref();
        cartItemsSharedPref = new CartItemsSharedPref();
        username = loginSharedPref.getUsername(this);

        List<CartItemsModel> items = cartItemsSharedPref.loadCartItems(this, username);
        populateProductList(items);
//        getSupportActionBar().setTitle("Products");
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
//        for(int i=0; i < productsAdapter.getChildrenCount(groupPosition); i++) {
//            LinearLayout llBrandPrice = (LinearLayout) v.findViewById(R.id.ll_brand_price);
//            LinearLayout llBrandQty = (LinearLayout) v.findViewById(R.id.ll_brand_qty);
//            LinearLayout llQtyButtons = (LinearLayout) v.findViewById(R.id.ll_item_qty_buttons);
//            LinearLayout llTransacBtn = (LinearLayout) v.findViewById(R.id.ll_transac_buttons);
//
//            if(i == childPosition) {
//                llBrandPrice.setVisibility(View.GONE);
//                llQtyButtons.setVisibility(View.GONE);
//                llBrandQty.setVisibility(View.GONE);
//                llTransacBtn.setVisibility(View.GONE);
//            }
//        }


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

//        if(lastExpandedPosition != -1) {
//            expandableListView.expandGroup(lastExpandedPosition);
//        }
    }

}
