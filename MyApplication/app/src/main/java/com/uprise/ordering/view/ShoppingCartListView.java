package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by cicciolina on 10/26/16.
 */

public class ShoppingCartListView extends ArrayAdapter<CartItemsModel> {

    private List<CartItemsModel> cartItemsModels;
    private Activity context;
    private Resources resources;
//    private LayoutInflater mLayoutInflater;
    private ImageButton plusBtn;
    private ImageButton minusBtn;
    private ImageButton deleteBtn;
    private EditText etQuantity;
    private ImageView itemImage;
    private int oldQtyValue;
    private View rowView;
    private TextView tvBrandName;
    private TextView tvBrandPrice;
    private TextView tvProductName;
    private CartItemsModel savedCardItem;
    private ShoppingCartListView.ShoppingCartListViewListener listener;

    private List<ProductModel> productModels;


    public ShoppingCartListView(Activity context,
                      List<CartItemsModel> cartItemsModels,
                                List<ProductModel> productModels, ShoppingCartListView.ShoppingCartListViewListener listener) {
        super(context, R.layout.custom_shopping_cart_list, cartItemsModels);

        this.context = context;
        this.cartItemsModels = cartItemsModels;
        this.resources = context.getResources();
        this.productModels = productModels;
        this.listener = listener;
        savedCardItem = new CartItemsModel();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.custom_shopping_cart_list, null, true);
        tvBrandName =(TextView) rowView.findViewById(R.id.tv_brand_name);
        itemImage = (ImageView) rowView.findViewById(R.id.iv_brand_image);
        tvBrandPrice =(TextView) rowView.findViewById(R.id.tv_brand_price);
        tvProductName = (TextView) rowView.findViewById(R.id.tv_product_name);
        etQuantity = (EditText) rowView.findViewById(R.id.et_brand_qty);
        minusBtn = (ImageButton) rowView.findViewById(R.id.btn_minus_brand_qty);
        plusBtn = (ImageButton)  rowView.findViewById(R.id.btn_plus_brand_qty);
        deleteBtn = (ImageButton) rowView.findViewById(R.id.btn_delete);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        if(isProductsAndCartItemsNotEmpty(productModels, cartItemsModels)) {

            for(CartItemsModel model: cartItemsModels) {
                ProductModel matchedProductModel = getMatchedProductModel(model, productModels);

                tvProductName.setText(matchedProductModel.getName());
                if(matchedProductModel != null &&
                        !matchedProductModel.getBrands().isEmpty()) {
                    BrandModel matchedBrandModel = getMatchedBrandModel(model, matchedProductModel.getBrands());
                    if(matchedBrandModel != null) {
                        tvBrandName.setText(matchedBrandModel.getBrandName().toString());
                        tvBrandPrice.setText(decimalFormat.format(matchedBrandModel.getPrice()) + " Php");
                        new ShoppingCartListView.LoadImageAsyncTask(itemImage).execute(matchedBrandModel.getBrandPhotoUrl());
                        etQuantity.setText(cartItemsModels.get(position).getQuantity());
                    }
                }
            }
//            BrandModel brandModel = productModels.get(cartItemsModels.get(position).getProductIndex()).getBrands()
//            .get(cartItemsModels.get(position).getBranchIndex());
//
//            if(brandModel != null) {
//                tvBrandName.setText(brandModel.getBrandName().toString());
//                tvBrandPrice.setText(decimalFormat.format(brandModel.getPrice()) + " Php");
//                new ShoppingCartListView.LoadImageAsyncTask(itemImage).execute(brandModel.getBrandPhotoUrl());
//                etQuantity.setText(cartItemsModels.get(position).getQuantity());
//            }

        }
        ShoppingCartListView.CountListener count = new ShoppingCartListView.CountListener(rowView, cartItemsModels.get(position).getBranchId(), cartItemsModels.get(position).getProductModelId());
        etQuantity.addTextChangedListener(count);
        minusBtn.setOnClickListener(count);
        plusBtn.setOnClickListener(count);
        deleteBtn.setOnClickListener(count);

//        List<CartItemsModel> cartItemsModelList = sharedPreferences.loadCartItems(context);
//        if(cartItemsModelList !=null && !cartItemsModelList.isEmpty()) {
//            for (CartItemsModel cartItemsModel : cartItemsModelList) {
//                if (cartItemsModel.getBranchIndex() == position && cartItemsModel.getProductIndex() == productPosition) {
////                    EditText etQuantity = (EditText) cartItemsModel.getCartItemsView().findViewById(R.id.et_brand_qty);
////                    Button addToCartBtn = (Button) cartItemsModel.getCartItemsView().findViewById(R.id.btn_add_to_cart);
//                    addToCartBtn.setEnabled(false);
//                    addToCartBtn.setText(resources.getString(R.string.added_to_cart));
//                    saveEditBtn.setVisibility(View.GONE);
//                    etQuantity.setText(cartItemsModel.getQuantity()+"");
//                }
//            }
//        }
        return rowView;
    }


    private class LoadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView bmImage;
        public LoadImageAsyncTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

//                Util.showToast(MainActivity.this,"Unable to load Profile Picture due to network connectivity loss");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private boolean isProductsAndCartItemsNotEmpty(List<ProductModel> productModels,
                                                   List<CartItemsModel> cartItemsModels) {
     return cartItemsModels != null && !cartItemsModels.isEmpty() && productModels != null
             && !productModels.isEmpty();
    }

    private class CountListener implements View.OnClickListener, TextWatcher {
        int count;
        EditText etQuantity;
        ImageButton minusBtn;
        ImageButton plusBtn;
        Button addToCartBtn;
        Button saveEditBtn;
        //        int brandPosition;
//        int productPosition;
        String brandId;
        String productId;
        String beforeTextChanged;
        View itemView;
        public CountListener(View itemView, String brandId, String productId) {
            this.count = 0;
            this.itemView = itemView;
            etQuantity = (EditText) itemView.findViewById(R.id.et_brand_qty);
            minusBtn = (ImageButton) itemView.findViewById(R.id.btn_minus_brand_qty);
            plusBtn = (ImageButton)  itemView.findViewById(R.id.btn_plus_brand_qty);
//            addToCartBtn = (Button) itemView.findViewById(R.id.btn_add_to_cart);
//            saveEditBtn = (Button) itemView.findViewById(R.id.btn_save_edit_brand_item);
            this.brandId = brandId;
            this.productId = productId;
        }
        @Override
        public void onClick(View view) {

            String qtyStr = etQuantity.getText().toString();
            if(!qtyStr.isEmpty()) count = Integer.parseInt(qtyStr);

            switch(view.getId()) {
                case R.id.btn_minus_brand_qty:
                    if(count > 0) count--;
                    saveItem();
                    listener.editCartItem(savedCardItem);
                    break;
                case R.id.btn_plus_brand_qty:
                    count++;
                    saveItem();
                    listener.editCartItem(savedCardItem);
                    break;
                case R.id.btn_delete:
                    listener.deleteCartItem(brandId, productId);
                    break;
            }

            isCountZero();

            if(this.etQuantity != null) {
                this.etQuantity.setText(count+"");
            }

        }


        private void isCountZero() {
            minusBtn.setVisibility(View.GONE);
//            addToCartBtn.setVisibility(View.GONE);
//            saveEditBtn.setVisibility(View.GONE);
            if(count > 0) {
                minusBtn.setVisibility(View.VISIBLE);
//                addToCartBtn.setVisibility(View.VISIBLE);
            }

//            if(!addToCartBtn.isEnabled() && oldQtyValue != count && count > 0) saveEditBtn.setVisibility(View.VISIBLE);

//            etQuantity.setText(count);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            beforeTextChanged = etQuantity.getText().toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!etQuantity.getText().toString().isEmpty() && !etQuantity.getText().toString().contentEquals(beforeTextChanged)) {
                count = Integer.parseInt(etQuantity.getText().toString());
                isCountZero();
            }
        }

        private void saveItem() {
            savedCardItem.setQuantity(count);
            savedCardItem.setBranchId(brandId);
            savedCardItem.setProductModelId(productId);
            savedCardItem.setCartItemsView(itemView);
//            addToCartBtn.setEnabled(false);
//            saveEditBtn.setVisibility(View.GONE);
            oldQtyValue = count;
//            addToCartBtn.setText(resources.getString(R.string.added_to_cart));
        }
    }

    public interface ShoppingCartListViewListener {
        void deleteCartItem(String branchId, String productId);
        void editCartItem(CartItemsModel cartItemsModel);
    }

    private ProductModel getMatchedProductModel(CartItemsModel cartItemsModel, List<ProductModel> productModels) {
        ProductModel result = new ProductModel();
        for(int i=0; i<productModels.size(); i++) {
            if(productModels.get(i).getId().equalsIgnoreCase(cartItemsModel.getProductModelId())) {
                result = productModels.get(i);
            }
        }
        return result;
    }

    private BrandModel getMatchedBrandModel(CartItemsModel cartItemsModel, List<BrandModel> brandModels) {
        BrandModel result = new BrandModel();
        for(int i=0; i<brandModels.size(); i++) {
            if(brandModels.get(i).getId().equalsIgnoreCase(cartItemsModel.getBranchId())) {
                result = brandModels.get(i);
            }
        }
        return result;
    }
}
