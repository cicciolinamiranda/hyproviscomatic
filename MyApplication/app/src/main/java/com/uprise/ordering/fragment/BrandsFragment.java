package com.uprise.ordering.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.uprise.ordering.R;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.LoginModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.BrandsAdapter;
import com.uprise.ordering.view.ProductPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 12/10/16.
 */

public class BrandsFragment extends Fragment implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupExpandListener,
        ProductPagerAdapter.ProductPagerAdapterListener,
        BrandsAdapter.BrandsAdapterListener,
        RestCallServices.RestServiceListener {

    private MenuItem previousMenu;
    private MenuItem nextMenu;
    private String nextUrl;
    private String prevUrl;
    private View rowView;
//    private ProductsAdapter productsAdapter;
    private BrandsAdapter brandsAdapter;
    private ExpandableListView expandableListView;
//    private ArrayList<ProductModel> productModels;
    private ArrayList<BrandModel> brandModels;
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
//        productModels = new ArrayList<>();
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupExpandListener(this);
        sqlDatabaseHelper = new SqlDatabaseHelper(getContext());
        loginModel = new LoginModel();
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        llNoRecords =(LinearLayout) rowView.findViewById(R.id.ll_existing_products_no_records);
        if(loginModel != null && loginModel.getUsername() != null) username = loginModel.getUsername();

        restCallServices = new RestCallServices(getContext());
        final String brandEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_brand);
        if(loginModel != null && loginModel.getToken() != null &&
                Util.getInstance().isNetworkAvailable(getContext())) {
            restCallServices.getProducts(getContext(), this, loginModel.getToken(), brandEndpoint);
            mProgressView = rowView.findViewById(R.id.rl_shop_now_loading_layout);
            mProgressView.setVisibility(View.VISIBLE);
            setHasOptionsMenu(true);
        } else {
            llNoRecords.setVisibility(View.VISIBLE);
        }
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
        previousMenu.setVisible(false);
        nextMenu.setVisible(false);
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
//                productModels = new ArrayList<>();
                 brandModels = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i) != null) {
                        brandModels.add(Util.getInstance().generateBrandModelFromJson(jsonArray.getJSONObject(i)));
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
        mProgressView.setVisibility(View.GONE);
        llNoRecords.setVisibility(View.VISIBLE);
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

        if(getContext() != null) {
            if (items != null && !items.isEmpty()) {
//                productsAdapter = new ProductsAdapter(getContext(), productModels, expandableListView, this, this, items);
                brandsAdapter = new BrandsAdapter(getContext(), brandModels, expandableListView, this, this, items);
            } else {
//            cartItemsSharedPref.storeCartItems(this, new ArrayList<CartItemsModel>());
                brandsAdapter = new BrandsAdapter(getContext(), brandModels, expandableListView, this, this, new ArrayList<CartItemsModel>());
//                productsAdapter = new ProductsAdapter(getContext(), productModels, expandableListView, this, this, new ArrayList<CartItemsModel>());
            }
//            productsAdapter.notifyDataSetChanged();
//            expandableListView.setAdapter(productsAdapter);
            brandsAdapter.notifyDataSetChanged();
            expandableListView.setAdapter(brandsAdapter);
            mProgressView.setVisibility(View.GONE);

            if (brandModels.size() <= 0) llNoRecords.setVisibility(View.VISIBLE);
            if (lastExpandedPosition != -1) {
                expandableListView.expandGroup(lastExpandedPosition);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pagination_menu, menu);
        previousMenu = menu.findItem(R.id.menu_orderlist_prev);
        previousMenu.setVisible(false);
        nextMenu = menu.findItem(R.id.menu_orderlist_next);
        nextMenu.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_orderlist_prev:
                lastExpandedPosition = -1;
                mProgressView.setVisibility(View.VISIBLE);
                brandModels = new ArrayList<>();
                restCallServices.getProducts(getActivity(), this, loginModel.getToken(), prevUrl);
                break;
            case R.id.menu_orderlist_next:
                lastExpandedPosition = -1;
                mProgressView.setVisibility(View.VISIBLE);
                brandModels = new ArrayList<>();
                restCallServices.getProducts(getActivity(), this, loginModel.getToken(), nextUrl);
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restCallServices = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restCallServices = null;
    }
}
