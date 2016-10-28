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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.util.Util;

import java.io.InputStream;
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
//    private DecimalFormat decimalFormat;
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
        deleteBtn = (ImageButton) rowView.findViewById(R.id.btn_delete_cart_item);
//        decimalFormat = new DecimalFormat("#.##");

        if(Util.getInstance().isProductsAndCartItemsNotEmpty(productModels, cartItemsModels)) {

                ProductModel matchedProductModel = Util.getInstance().getMatchedProductModel(cartItemsModels.get(position), productModels);

                if(matchedProductModel != null &&
                        !matchedProductModel.getBrands().isEmpty()) {
                    BrandModel matchedBrandModel = Util.getInstance().getMatchedBrandModel(cartItemsModels.get(position), matchedProductModel.getBrands(), matchedProductModel.getId());
                    if(matchedBrandModel != null) {
                        tvProductName.setText(matchedProductModel.getName());
                        tvBrandName.setText(matchedBrandModel.getBrandName().toString());
                        tvBrandPrice.setText(String.format("%.2f",matchedBrandModel.getPrice()) + " Php");
                        new ShoppingCartListView.LoadImageAsyncTask(itemImage).execute(matchedBrandModel.getBrandPhotoUrl());
                        etQuantity.setText(cartItemsModels.get(position).getQuantity()+"");
                    }
                }

        }
        ShoppingCartListView.CountListener count = new ShoppingCartListView.CountListener(rowView, cartItemsModels.get(position).getBranchId(), cartItemsModels.get(position).getProductModelId());
        etQuantity.addTextChangedListener(count);
        minusBtn.setOnClickListener(count);
        plusBtn.setOnClickListener(count);
        deleteBtn.setOnClickListener(count);

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
            }
            catch (OutOfMemoryError e) {

                Util.getInstance().showSnackBarToast(context,"Unable to load Profile Picture due no response from the image source");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            catch (Exception e) {

                Util.getInstance().showSnackBarToast(context,"Unable to load Profile Picture due to network connectivity loss");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(final Bitmap result) {
            Activity activity = context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bmImage.setImageBitmap(result);
                }
            });
        }
    }


    private class CountListener implements View.OnClickListener, TextWatcher {
        int count;
        EditText etQuantity;
        ImageButton minusBtn;
        ImageButton plusBtn;
        ImageButton deleteBtn;
//        Button addToCartBtn;
//        Button saveEditBtn;
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
            deleteBtn = (ImageButton) itemView.findViewById(R.id.btn_delete_cart_item) ;
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
                case R.id.btn_delete_cart_item:
                    saveItem();
                    listener.deleteCartItem(savedCardItem);
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

            etQuantity.setText(count+"");
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
            oldQtyValue = count;
        }
    }

    public interface ShoppingCartListViewListener {
        void deleteCartItem(CartItemsModel cartItemsModel);
        void editCartItem(CartItemsModel cartItemsModel);
    }
}
