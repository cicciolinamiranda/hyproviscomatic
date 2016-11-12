package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.BrandModel;
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
import java.util.List;

public class ProductsActivity extends BaseAuthenticatedActivity implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupExpandListener,
        BrandsPagerAdapter.BrandsAdapterListener,
        ProductsAdapter.ProductsAdapterListener,
        RestCallServices.RestServiceListener {

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_now);
        expandableListView = (ExpandableListView) findViewById(R.id.el_shop_now_products);

        //TODO: to be replaced with Rest Call
        productModels = new ArrayList<>();
//        productModels = Util.getInstance().generateProductModels();
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupExpandListener(this);
//        loginSharedPref = new LoginSharedPref();
//        cartItemsSharedPref = new CartItemsSharedPref();
//        username = loginSharedPref.getUsername(this);
        sqlDatabaseHelper = new SqlDatabaseHelper(ProductsActivity.this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        if(loginModel != null && loginModel.getUsername() != null) username = loginModel.getUsername();

//        List<CartItemsModel> items = cartItemsSharedPref.loadCartItems(this, username);
        restCallServices = new RestCallServices(this);
        restCallServices.getProducts(ProductsActivity.this, this);
//        populateProductList();
        mProgressView = findViewById(R.id.rl_shop_now_loading_layout);
        mProgressView.setVisibility(View.VISIBLE);
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
//        cartItemsSharedPref.addCartItems(this, cartItemsModel);
//        List<CartItemsModel> items = cartItemsSharedPref.loadCartItems(this, username);
        sqlDatabaseHelper.createCartItems(cartItemsModel);
        populateProductList();
        isAddOrSaved = true;
        Util.getInstance().showSnackBarToast(this, getString(R.string.changes_saved));

    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        cartItemsModel.setUserName(username);
//        cartItemsSharedPref.editCardItem(this, cartItemsModel);
//        List<CartItemsModel> items = cartItemsSharedPref.loadCartItems(this, username);
        sqlDatabaseHelper.updateCartItems(cartItemsModel);
        populateProductList();
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

    private void populateProductList() {
        List<CartItemsModel> items = sqlDatabaseHelper.getAllUserCartItems(username);

        if(items !=null && !items.isEmpty()) {
            productsAdapter = new ProductsAdapter(this, productModels, expandableListView, this, this, items);
            productsAdapter.notifyDataSetChanged();
        }
        else {
//            cartItemsSharedPref.storeCartItems(this, new ArrayList<CartItemsModel>());
            productsAdapter = new ProductsAdapter(this, productModels, expandableListView, this, this,new ArrayList<CartItemsModel>());
        }
        expandableListView.setAdapter(productsAdapter);
        mProgressView.setVisibility(View.GONE);
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
                        productModels.add(generateProductModelFromJson(jsonArray.getJSONObject(i)));
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

    private ProductModel generateProductModelFromJson(JSONObject jsonObject) {
        ProductModel productModel = new ProductModel();



        try {
            if(jsonObject.getString("id") != null) productModel.setId(jsonObject.getString("id"));
            if(jsonObject.getString("name") != null) productModel.setName(jsonObject.getString("name"));

            if(jsonObject.getString("brands") != null) {
                JSONArray jsonBrandsArray = jsonObject.getJSONArray("brands");
                ArrayList<BrandModel> brands = new ArrayList<>();

                for(int i = 0; i < jsonBrandsArray.length(); i++) {
                    BrandModel brandModel = new BrandModel();
                    brandModel.setId(jsonBrandsArray.getJSONObject(i).getString("id"));
                    brandModel.setBrandName(jsonBrandsArray.getJSONObject(i).getString("name"));

                    //TODO INCORRECT!
                    brandModel.setBrandPhotoUrl("http://gazettereview.com/wp-content/uploads/2015/12/PEN-STYLE-2.jpg");
                    brandModel.setPrice(Double.parseDouble("100")*i);
                    brands.add(brandModel);
                }
                productModel.setBrands(brands);
            }
            return productModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
