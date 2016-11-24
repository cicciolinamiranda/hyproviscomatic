package com.uprise.ordering.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.uprise.ordering.R;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.LoginModel;
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
import java.util.List;

/**
 * Created by cicciolina on 11/21/16.
 */

public class ProductsFragment extends Fragment implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupExpandListener,
        BrandsPagerAdapter.BrandsAdapterListener,
        ProductsAdapter.ProductsAdapterListener,
        RestCallServices.RestServiceListener {

    private View rowView;
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
    private RestCallServices restCallServices;
    private View mProgressView;
    private LinearLayout llNoRecords;
    private SqlDatabaseHelper sqlDatabaseHelper;
    private LoginModel loginModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rowView = inflater.inflate(R.layout.layout_shop_now, container, false);
        expandableListView = (ExpandableListView) rowView.findViewById(R.id.el_shop_now_products);
        productModels = new ArrayList<>();
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupExpandListener(this);
        sqlDatabaseHelper = new SqlDatabaseHelper(getContext());
        loginModel = new LoginModel();
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        llNoRecords =(LinearLayout) rowView.findViewById(R.id.ll_existing_products_no_records);
        if(loginModel != null && loginModel.getUsername() != null) username = loginModel.getUsername();

        restCallServices = new RestCallServices(getContext());
        if(loginModel != null && loginModel.getToken() != null) restCallServices.getProducts(getContext(), this, loginModel.getToken());
        mProgressView = rowView.findViewById(R.id.rl_shop_now_loading_layout);
        mProgressView.setVisibility(View.VISIBLE);
        return rowView;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return true;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        isAddOrSaved = false;
        lastExpandedPosition = groupPosition;
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

            if(jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i) != null) {
                        productModels.add(Util.getInstance().generateProductModelFromJson(jsonArray.getJSONObject(i)));
                    }
                    populateProductList();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(RestCalls callType, String string) {

    }

    @Override
    public void addToCart(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(username);
        sqlDatabaseHelper.createCartItems(cartItemsModel);
        populateProductList();
        isAddOrSaved = true;
        Util.getInstance().showSnackBarToast(getContext(), getString(R.string.changes_saved));
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(username);
        sqlDatabaseHelper.updateCartItems(cartItemsModel);
        populateProductList();
        isAddOrSaved = true;
        Util.getInstance().showSnackBarToast(getContext(), getString(R.string.changes_saved));
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

    private void populateProductList() {
        List<CartItemsModel> items = sqlDatabaseHelper.getAllUserCartItems(username);

        if(items !=null && !items.isEmpty()) {
            productsAdapter = new ProductsAdapter(getContext(), productModels, expandableListView, this, this, items);
            productsAdapter.notifyDataSetChanged();
        }
        else {
//            cartItemsSharedPref.storeCartItems(this, new ArrayList<CartItemsModel>());
            productsAdapter = new ProductsAdapter(getContext(), productModels, expandableListView, this, this,new ArrayList<CartItemsModel>());
        }
        expandableListView.setAdapter(productsAdapter);
        mProgressView.setVisibility(View.GONE);

        if(productModels.size() <=0) llNoRecords.setVisibility(View.VISIBLE);
        if(lastExpandedPosition != -1) {
            expandableListView.expandGroup(lastExpandedPosition);
        }
    }
}
