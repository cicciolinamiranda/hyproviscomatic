package com.uprise.ordering.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.OrderItemsModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.util.Util;

import java.io.InputStream;
import java.util.List;

/**
 * Created by cicciolina on 11/3/16.
 */

public class OrderItemsListView extends ArrayAdapter<OrderItemsModel> {

//    private List<CartItemsModel> cartItemsModels;
    private List<OrderItemsModel> orderItemsModels;
    private Activity context;
    private Resources resources;
    private List<ProductModel> productModels;
    private ImageButton plusBtn;
    private ImageButton minusBtn;
    private ImageButton deleteBtn;
    private TextView etQuantity;
    private ImageView itemImage;
    private View rowView;
    private TextView tvBrandName;
    private TextView tvBrandPrice;
    private TextView tvProductName;

    public OrderItemsListView (Activity context,
                               List<OrderItemsModel> orderItemsModels,
                               List<ProductModel> productModels) {
        super(context, R.layout.custom_shopping_cart_list, orderItemsModels);
        this.context = context;
//        this.cartItemsModels = cartItemsModels;
        this.orderItemsModels = orderItemsModels;
        this.resources = context.getResources();
        this.productModels = productModels;
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
        etQuantity = (TextView) rowView.findViewById(R.id.tv_brand_qty);
        minusBtn = (ImageButton) rowView.findViewById(R.id.btn_minus_brand_qty);
        plusBtn = (ImageButton)  rowView.findViewById(R.id.btn_plus_brand_qty);
        deleteBtn = (ImageButton) rowView.findViewById(R.id.btn_delete_cart_item);

        minusBtn.setVisibility(View.GONE);
        plusBtn.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.GONE);
                    tvProductName.setText(orderItemsModels.get(position).getProductName());
                    tvBrandName.setText(orderItemsModels.get(position).getBrandName());
                    tvBrandPrice.setText(String.format("%.2f",orderItemsModels.get(position).getPrice()) + " Php");
//                    new OrderItemsListView.LoadImageAsyncTask(itemImage).execute(orderItemsModels.get(position).getPhotoUrl());
                    etQuantity.setText(orderItemsModels.get(position).getQuantity()+"");
                    etQuantity.setTextColor(resources.getColor(R.color.black));
//        if(Util.getInstance().isProductsAndCartItemsNotEmpty(productModels, OrderItemsModel)) {
//
//            ProductModel matchedProductModel = Util.getInstance().getMatchedProductModel(cartItemsModels.get(position), productModels);
//
//            if(matchedProductModel != null &&
//                    !matchedProductModel.getBrands().isEmpty()) {
//                BrandModel matchedBrandModel = Util.getInstance().getMatchedBrandModel(cartItemsModels.get(position), matchedProductModel.getBrands(), matchedProductModel.getId());
//                if(matchedBrandModel != null) {
//                    tvProductName.setText(matchedProductModel.getName());
//                    tvBrandName.setText(matchedBrandModel.getBrandName().toString());
//                    tvBrandPrice.setText(String.format("%.2f",matchedBrandModel.getPrice()) + " Php");
//                    new OrderItemsListView.LoadImageAsyncTask(itemImage).execute(matchedBrandModel.getBrandPhotoUrl());
//                    etQuantity.setText(cartItemsModels.get(position).getQuantity()+"");
//                    etQuantity.setTextColor(resources.getColor(R.color.black));
//                }
//            }
//
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
}
