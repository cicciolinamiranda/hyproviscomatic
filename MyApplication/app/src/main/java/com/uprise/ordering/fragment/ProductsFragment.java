package com.uprise.ordering.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.view.BrandsPagerAdapter;
import com.uprise.ordering.view.ProductsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 10/22/16.
 */

public class ProductsFragment extends Fragment implements ExpandableListView.OnChildClickListener, BrandsPagerAdapter.BrandsAdapterListener, ProductsAdapter.ProductsAdapterListener {

    private View fragmentView;
    private View childView;
    private ProductsAdapter productsAdapter;
    private ExpandableListView expandableListView;
    private ArrayList<ProductModel> productModels;
    private List<CartItemsModel> cartItemsModelList;

//    private EditText etQuantity;
//    private ImageButton minusBtn;
//    private ImageButton plusBtn;
//    private Button addToCartBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.layout_shop_now, container, false);
        expandableListView = (ExpandableListView) fragmentView.findViewById(R.id.el_shop_now_products);
        productModels = generateProductModels();
        productsAdapter = new ProductsAdapter(getContext(), productModels, expandableListView, this, this);
        expandableListView.setAdapter(productsAdapter);
        expandableListView.setOnChildClickListener(this);
        cartItemsModelList = new ArrayList<>();
        return fragmentView;
    }

    // generate some random amount of child objects (1..10)
    private ArrayList<ProductModel> generateProductModels() {
        ArrayList<ProductModel> productModels = new ArrayList<>();
        for(int i=0; i < 11; i++) {
            ProductModel productModel = new ProductModel();
            productModel.setName("Product "+i);
            productModel.setBrands(generateBrands());
            productModels.add(productModel);
        }

        return productModels;
    }

    private ArrayList<BrandModel> generateBrands() {
        ArrayList<BrandModel> brands = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            BrandModel brandModel = new BrandModel();
            brandModel.setBrandName("Brand " + i);
            brandModel.setPrice(10d + brandModel.getPrice() + i);
            brands.add(brandModel);
        }
        return brands;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, final int i, final int i1, long l) {
        return true;
    }


    @Override
    public void addToCart(CartItemsModel cartItemsModel) {
        cartItemsModelList.add(cartItemsModel);
    }

    @Override
    public void editCartItem(CartItemsModel cartItemsModel) {
        if(!cartItemsModelList.isEmpty()) {

            for (CartItemsModel localCartItems : cartItemsModelList) {
                if (localCartItems.getProductIndex() == cartItemsModel.getProductIndex() &&
                        localCartItems.getBranchIndex() == cartItemsModel.getBranchIndex()) {
                    localCartItems.setQuantity(cartItemsModel.getQuantity());
                }
            }
        }
    }

    @Override
    public void onBrandPageSelected(int position) {

        if(!cartItemsModelList.isEmpty()) {

            for (CartItemsModel cartItemsModel : cartItemsModelList) {
                if (cartItemsModel.getBranchIndex() == position) {
                    EditText etQuantity = (EditText) cartItemsModel.getCartItemsView().findViewById(R.id.et_brand_qty);
//                    ImageButton minusBtn = (ImageButton) cartItemsModel.getCartItemsView().findViewById(R.id.btn_minus_brand_qty);
//                    ImageButton plusBtn = (ImageButton)  cartItemsModel.getCartItemsView().findViewById(R.id.btn_plus_brand_qty);
                    Button addToCartBtn = (Button) cartItemsModel.getCartItemsView().findViewById(R.id.btn_add_to_cart);
                    addToCartBtn.setEnabled(false);
                    addToCartBtn.setText(getResources().getString(R.string.added_to_cart));
                     etQuantity.setText(cartItemsModel.getQuantity()+"");
                }
            }
        }
    }
}

