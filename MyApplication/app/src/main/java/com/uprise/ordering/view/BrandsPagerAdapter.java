package com.uprise.ordering.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;
import com.uprise.ordering.shared.LoginSharedPref;
import com.uprise.ordering.util.Util;

import java.io.InputStream;
import java.util.List;

/**
 * Created by cicciolina on 10/24/16.
 */

public class BrandsPagerAdapter extends PagerAdapter {

    private final List<BrandModel> web;
    private Context mContext;
    private final Resources resources;
    private LayoutInflater mLayoutInflater;
    private ImageButton plusBtn;
    private ImageButton minusBtn;
    private TextView etQuantity;
    private ImageView itemImage;
    private Button addToCartBtn;
    private int oldQtyValue;
    private BrandsPagerAdapter.BrandsAdapterListener listener;
    private Button saveEditBtn;
    private String productId;
    private CartItemsModel savedCardItem;
    private List<CartItemsModel> cartItemsModelList;
    private LinearLayout llBrandPrice;
    private LinearLayout llBrandQty;
    private LinearLayout llQtyButtons;
    private LinearLayout llTransacBtn;

    public BrandsPagerAdapter(Context context, List<BrandModel> web, List<CartItemsModel> cartItemsModelList, BrandsPagerAdapter.BrandsAdapterListener listener, String productId) {
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resources = context.getResources();
        this.web = web;
        this.listener = listener;
        this.productId = productId;
//        this.productPosition = productPosition;
        savedCardItem = new CartItemsModel();
        this.cartItemsModelList = cartItemsModelList;

    }

    @Override
    public int getCount() {
        return web.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.custom_brand_list, container, false);
        TextView tvBrandName =(TextView) itemView.findViewById(R.id.tv_brand_name);
        tvBrandName.setText(web.get(position).getBrandName());
        TextView tvBrandPrice =(TextView) itemView.findViewById(R.id.tv_brand_price);
//        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        tvBrandPrice.setText(String.format("%.2f", web.get(position).getPrice())+" Php");
        itemImage = (ImageView) itemView.findViewById(R.id.iv_brand_image);
        new LoadImageAsyncTask(itemImage).execute(web.get(position).getBrandPhotoUrl());
        addToCartBtn = (Button) itemView.findViewById(R.id.btn_add_to_cart);
        addToCartBtn.setVisibility(View.GONE);
        etQuantity = (TextView) itemView.findViewById(R.id.tv_brand_qty);
        minusBtn = (ImageButton) itemView.findViewById(R.id.btn_minus_brand_qty);
        plusBtn = (ImageButton)  itemView.findViewById(R.id.btn_plus_brand_qty);
        saveEditBtn = (Button)   itemView.findViewById(R.id.btn_save_edit_brand_item);
        BrandsPagerAdapter.CountListener count = new BrandsPagerAdapter.CountListener(itemView, web.get(position).getId(), productId);


        LoginSharedPref loginSharedPref = new LoginSharedPref();
        llBrandPrice = (LinearLayout) itemView.findViewById(R.id.ll_brand_price);
        llBrandQty = (LinearLayout) itemView.findViewById(R.id.ll_brand_qty);
        llQtyButtons = (LinearLayout) itemView.findViewById(R.id.ll_item_qty_buttons);
        llTransacBtn = (LinearLayout) itemView.findViewById(R.id.ll_transac_buttons);
        if(!loginSharedPref.isLoggedIn(mContext)) {
            llBrandPrice.setVisibility(View.INVISIBLE);
            llBrandQty.setVisibility(View.INVISIBLE);
            llQtyButtons.setVisibility(View.INVISIBLE);
            llTransacBtn.setVisibility(View.INVISIBLE);
        }

        saveEditBtn.setOnClickListener(count);
        etQuantity.addTextChangedListener(count);
        minusBtn.setOnClickListener(count);
        plusBtn.setOnClickListener(count);
        addToCartBtn.setOnClickListener(count);

//        List<CartItemsModel> cartItemsModelList = sharedPreferences.loadCartItems(mContext, loginSharedPref.getUsername(mContext));
        if(cartItemsModelList != null && !cartItemsModelList.isEmpty()) {
            for (CartItemsModel cartItemsModel : cartItemsModelList) {
                if (cartItemsModel.getBranchId().equalsIgnoreCase(web.get(position).getId())
                        && cartItemsModel.getProductModelId().equalsIgnoreCase(productId)) {
                    addToCartBtn.setEnabled(false);
                    addToCartBtn.setText(resources.getString(R.string.added_to_cart));
                    saveEditBtn.setVisibility(View.GONE);
                    etQuantity.setText(cartItemsModel.getQuantity()+"");
                }
                oldQtyValue = cartItemsModel.getQuantity();
            }
        }
        container.addView(itemView);
//        oldQtyValue = sharedPreferences.
        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public interface BrandsAdapterListener {
        void addToCart(CartItemsModel cartItemsModel);
        void editCartItem(CartItemsModel cartItemsModel);
    }

    private class CountListener implements View.OnClickListener, TextWatcher {
        int count;
        TextView etQuantity;
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
            etQuantity = (TextView) itemView.findViewById(R.id.tv_brand_qty);
            minusBtn = (ImageButton) itemView.findViewById(R.id.btn_minus_brand_qty);
            plusBtn = (ImageButton)  itemView.findViewById(R.id.btn_plus_brand_qty);
            addToCartBtn = (Button) itemView.findViewById(R.id.btn_add_to_cart);
            saveEditBtn = (Button) itemView.findViewById(R.id.btn_save_edit_brand_item);
            this.brandId = brandId;
            this.productId = productId;
        }
        @Override
        public void onClick(View view) {

            String qtyStr = etQuantity.getText().toString();
            if(!qtyStr.isEmpty()) count = Integer.parseInt(qtyStr);
            saveEditBtn.setVisibility(View.GONE);
            switch(view.getId()) {
                case R.id.btn_minus_brand_qty:
                    if(count > 0) count--;
                    if(!addToCartBtn.isEnabled() && oldQtyValue != count && count > 0) {
                        saveEditBtn.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.btn_plus_brand_qty:
                    count++;
                    if(!addToCartBtn.isEnabled() && oldQtyValue != count && count > 0)

                    {
                        saveEditBtn.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.btn_add_to_cart:
                    saveItem();
                    listener.addToCart(savedCardItem);
                    break;
                case R.id.btn_save_edit_brand_item:
                    saveItem();
                    listener.editCartItem(savedCardItem);
                    break;
            }

            isCountZero();

            if(this.etQuantity != null) {
                this.etQuantity.setText(count+"");
            }

        }


        private void isCountZero() {
            minusBtn.setVisibility(View.GONE);
            addToCartBtn.setVisibility(View.GONE);
            if(count > 0) {
                minusBtn.setVisibility(View.VISIBLE);
                addToCartBtn.setVisibility(View.VISIBLE);
            }

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
            addToCartBtn.setEnabled(false);
            saveEditBtn.setVisibility(View.GONE);
            addToCartBtn.setText(resources.getString(R.string.added_to_cart));
        }
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
            } catch (OutOfMemoryError e) {

                Util.getInstance().showSnackBarToast(mContext,"Unable to load Profile Picture due no response from the image source");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            catch (Exception e) {

                Util.getInstance().showSnackBarToast(mContext,"Unable to load Profile Picture due to network connectivity loss");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(final Bitmap result) {

            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bmImage.setImageBitmap(result);
                }
            });

        }


    }
}
