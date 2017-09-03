package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
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
import com.uprise.ordering.util.InputFilterMinMax;
import com.uprise.ordering.util.Util;

import java.io.InputStream;
import java.util.List;

/**
 * Created by cicciolina on 12/10/16.
 */

public class BrandBasedShoppingCartListView extends ArrayAdapter<CartItemsModel> {

    private List<CartItemsModel> cartItemsModels;
    private Activity context;
    private Resources resources;
    private ImageButton plusBtn;
    private ImageButton minusBtn;
    private ImageButton deleteBtn;
    private EditText etQuantity;
    private ImageView itemImage;
    private View rowView;
    private TextView tvBrandName;
    private TextView tvBrandPrice;
    private TextView tvProductName;
    private CartItemsModel savedCardItem;

    private BrandBasedShoppingCartListView.ShoppingCartListViewListener listener;

    private List<BrandModel> brandModels;


    public  BrandBasedShoppingCartListView(Activity context,
                                List<CartItemsModel> cartItemsModels,
                                List<BrandModel> brandModels, BrandBasedShoppingCartListView.ShoppingCartListViewListener listener) {
        super(context, R.layout.custom_shopping_cart_list, cartItemsModels);

        this.context = context;
        this.cartItemsModels = cartItemsModels;
        this.resources = context.getResources();
        this.brandModels = brandModels;
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
        etQuantity.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "10000")});
        minusBtn = (ImageButton) rowView.findViewById(R.id.btn_minus_brand_qty);
        plusBtn = (ImageButton)  rowView.findViewById(R.id.btn_plus_brand_qty);
        deleteBtn = (ImageButton) rowView.findViewById(R.id.btn_delete_cart_item);

        if(Util.getInstance().isBrandsAndCartItemsNotEmpty(brandModels, cartItemsModels)) {

//            ProductModel matchedProductModel = Util.getInstance().getMatchedProductModel(cartItemsModels.get(position), productModels);
            BrandModel matchedBrandModel = Util.getInstance().getMatchedBrandModel(cartItemsModels.get(position), brandModels);

            if(matchedBrandModel != null && matchedBrandModel.getProducts() != null &&
                    !matchedBrandModel.getProducts().isEmpty()) {
                ProductModel matchedProductModel = Util.getInstance().getMatchedProductModel(cartItemsModels.get(position), matchedBrandModel.getProducts(), matchedBrandModel.getId());
//                BrandModel matchedBrandModel = Util.getInstance().getMatchedBrandModel(cartItemsModels.get(position), matchedProductModel.getBrands(), matchedProductModel.getId());
                if(matchedProductModel != null) {
                    tvProductName.setText(matchedProductModel.getName());
                    tvBrandName.setText(matchedBrandModel.getBrandName());
                    tvBrandPrice.setText(String.format("%.2f",matchedProductModel.getPrice()) + " Php");
                    if(matchedProductModel.getProductPhotoUrl() != null && !matchedProductModel.getProductPhotoUrl().isEmpty()) {

                        //TODO: Since no storage, response is Base64
//                            new ImageDownloaderTask(itemImage).execute(matchedBrandModel.getBrandPhotoUrl());

                        String replacedBase64 = matchedProductModel.getProductPhotoUrl().replace("data:image/jpeg;base64,","");
                        if(replacedBase64.contains("data:image/png;base64,")) {
                            replacedBase64 = replacedBase64.replace("data:image/png;base64,","");
                        }
                        byte[] decodedString = Base64.decode(replacedBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        itemImage.setImageBitmap(decodedByte);
                    } /** new ShoppingCartListView.LoadImageAsyncTask(itemImage).execute(matchedBrandModel.getBrandPhotoUrl()); **/
                    etQuantity.setText(cartItemsModels.get(position).getQuantity()+"");
                    minusBtn.setVisibility(View.GONE);
                    if(cartItemsModels.get(position).getQuantity()>1) {
                        minusBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
        BrandBasedShoppingCartListView.CountListener count = new BrandBasedShoppingCartListView.CountListener(rowView, cartItemsModels.get(position).getBrandId(), cartItemsModels.get(position).getProductModelId(),
                cartItemsModels.get(position).getAttributeId());
        minusBtn.setOnClickListener(count);
        etQuantity.addTextChangedListener(count);
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


    private class CountListener implements View.OnClickListener, TextWatcher{
        int count;
        EditText etQuantity;
        ImageButton minusBtn;
        ImageButton plusBtn;
        ImageButton deleteBtn;
        String brandId;
        String productId;
        String attributeId;
        View itemView;
        public CountListener(View itemView, String brandId, String productId, String attributeId) {
            this.count = 0;
            this.itemView = itemView;
            etQuantity = (EditText) itemView.findViewById(R.id.et_brand_qty);
            minusBtn = (ImageButton) itemView.findViewById(R.id.btn_minus_brand_qty);
            plusBtn = (ImageButton)  itemView.findViewById(R.id.btn_plus_brand_qty);
            deleteBtn = (ImageButton) itemView.findViewById(R.id.btn_delete_cart_item) ;
            this.brandId = brandId;
            this.productId = productId;
            this.attributeId = attributeId;

        }
        @Override
        public void onClick(View view) {

            String qtyStr = etQuantity.getText().toString();
            if(!qtyStr.isEmpty()) count = Integer.parseInt(qtyStr);

            switch(view.getId()) {
                case R.id.btn_minus_brand_qty:
                    if(count > 1) count--;
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
            minusBtn.setVisibility(View.VISIBLE);
            if(count <= 1) {
                minusBtn.setVisibility(View.GONE);
            }

//            etQuantity.setText(count+"");
        }

        private void saveItem() {
            savedCardItem.setQuantity(count);
            savedCardItem.setBrandId(brandId);
            savedCardItem.setProductModelId(productId);
            savedCardItem.setAttributeId(attributeId);
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!etQuantity.getText().toString().isEmpty()) {

                if(etQuantity.getText().toString().equalsIgnoreCase("0")) etQuantity.setText("1");
                try {
                    count = Integer.parseInt(etQuantity.getText().toString());
                }catch (NumberFormatException e) {
                    count = 0;
                }
                isCountZero();

                if(count > 0) {
                    saveItem();
                    listener.editCartItem(savedCardItem);
                }
            }

        }
    }

    public interface ShoppingCartListViewListener {
        void deleteCartItem(CartItemsModel cartItemsModel);
        void editCartItem(CartItemsModel cartItemsModel);
    }
}
