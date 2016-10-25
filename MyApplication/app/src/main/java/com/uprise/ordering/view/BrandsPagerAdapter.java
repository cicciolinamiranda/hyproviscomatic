package com.uprise.ordering.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;

import java.text.DecimalFormat;
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
    private EditText etQuantity;
    private Button addToCartBtn;
    private int oldQtyValue;
    private BrandsPagerAdapter.BrandsAdapterListener listener;
    private Button saveEditBtn;
    private int productPosition;
    private CartItemsModel savedCardItem;

    public BrandsPagerAdapter(Context context, List<BrandModel> web, BrandsPagerAdapter.BrandsAdapterListener listener, int productPosition ) {
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resources = context.getResources();
        this.web = web;
        this.listener = listener;
        this.productPosition = productPosition;
        savedCardItem = new CartItemsModel();
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
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        tvBrandPrice.setText(decimalFormat.format(web.get(position).getPrice())+" Php");
        addToCartBtn = (Button) itemView.findViewById(R.id.btn_add_to_cart);
        addToCartBtn.setVisibility(View.GONE);
        etQuantity = (EditText) itemView.findViewById(R.id.et_brand_qty);
        minusBtn = (ImageButton) itemView.findViewById(R.id.btn_minus_brand_qty);
        plusBtn = (ImageButton)  itemView.findViewById(R.id.btn_plus_brand_qty);
        saveEditBtn = (Button)   itemView.findViewById(R.id.btn_save_edit_brand_item);
        BrandsPagerAdapter.CountListener count = new BrandsPagerAdapter.CountListener(itemView, position, productPosition);
        saveEditBtn.setOnClickListener(count);
        etQuantity.addTextChangedListener(count);
        minusBtn.setOnClickListener(count);
        plusBtn.setOnClickListener(count);
        addToCartBtn.setOnClickListener(count);

        container.addView(itemView);

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
        EditText etQuantity;
        ImageButton minusBtn;
        ImageButton plusBtn;
        Button addToCartBtn;
        Button saveEditBtn;
        int brandPosition;
        int productPosition;
        String beforeTextChanged;
        View itemView;
        public CountListener(View itemView, int brandPosition, int productPosition) {
            this.count = 0;
            this.itemView = itemView;
            etQuantity = (EditText) itemView.findViewById(R.id.et_brand_qty);
            minusBtn = (ImageButton) itemView.findViewById(R.id.btn_minus_brand_qty);
            plusBtn = (ImageButton)  itemView.findViewById(R.id.btn_plus_brand_qty);
            addToCartBtn = (Button) itemView.findViewById(R.id.btn_add_to_cart);
            saveEditBtn = (Button) itemView.findViewById(R.id.btn_save_edit_brand_item);
            this.brandPosition = brandPosition;
            this.productPosition = productPosition;
        }
        @Override
        public void onClick(View view) {

            String qtyStr = etQuantity.getText().toString();
            if(!qtyStr.isEmpty()) count = Integer.parseInt(qtyStr);

            switch(view.getId()) {
                case R.id.btn_minus_brand_qty:
                    if(count > 0) count--;
                    break;
                case R.id.btn_plus_brand_qty:
                    count++;
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
            saveEditBtn.setVisibility(View.GONE);
            if(count > 0) {
                minusBtn.setVisibility(View.VISIBLE);
                addToCartBtn.setVisibility(View.VISIBLE);
            }

            if(!addToCartBtn.isEnabled() && oldQtyValue != count && count > 0) saveEditBtn.setVisibility(View.VISIBLE);

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
            savedCardItem.setBranchIndex(brandPosition);
            savedCardItem.setProductIndex(productPosition);
            savedCardItem.setCartItemsView(itemView);
            addToCartBtn.setEnabled(false);
            saveEditBtn.setVisibility(View.GONE);
            oldQtyValue = count;
            addToCartBtn.setText(resources.getString(R.string.added_to_cart));
        }
    }
}
