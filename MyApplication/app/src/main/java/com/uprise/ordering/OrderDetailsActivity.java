package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.model.OrderItemsModel;
import com.uprise.ordering.model.OrderModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.OrderItemsListView;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    private ListView lvOrderItemsList;
    private TextView tvEstimatedTotal;
//    private TextView tvNetTotal;
    private TextView tvDiscount;
    private TextView tvShippingFee;
    private TextView tvModeOfPaymentLabel;
    private ArrayAdapter<OrderItemsModel> cartItemsModelArrayAdapter;
    private LinearLayout llSubmitModeOfPayment;
    private OrderModel updatedPaymentOrderModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.asset_nav_back);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText(getString(R.string.label_my_orders));

        llSubmitModeOfPayment = (LinearLayout) findViewById(R.id.ll_order_item_submit_mode_of_payment);
        lvOrderItemsList = (ListView) findViewById(R.id.list_order_items);
        tvEstimatedTotal = (TextView) findViewById(R.id.tv_order_item__estimated_total_value);
        tvDiscount = (TextView) findViewById(R.id.tv_order_item__estimated_discount_value);
//        tvNetTotal = (TextView) findViewById(R.id.tv_order_item_net_total_value);
        tvShippingFee = (TextView) findViewById(R.id.tv_order_item__estimated_shipping_free_value);
        tvModeOfPaymentLabel = (TextView) findViewById(R.id.tv_proceed_to_checkout);
        tvModeOfPaymentLabel.setTextColor(getResources().getColor(R.color.light));
        final OrderModel orderModel = getIntent().getParcelableExtra("orderModel");
        ArrayList<ProductModel> productModels = Util.getInstance().generateProductModels();
        cartItemsModelArrayAdapter = new OrderItemsListView(OrderDetailsActivity.this,
                orderModel.getOrderItemsModels(),
                productModels);
        lvOrderItemsList.setAdapter(cartItemsModelArrayAdapter);
        tvEstimatedTotal.setText(String.format("%.2f", orderModel.getTotalAmount())+" Php");
        double computedDiscountPercentage = orderModel.getDiscount();
        double shippingFreeDouble = orderModel.getShippingFee();
//        double netTotal = orderModel.getTotalAmount();
//        tvNetTotal.setText(String.format("%.2f", netTotal)+" Php");
        tvDiscount.setText(String.format("%.2f", computedDiscountPercentage)+" Php");
        tvShippingFee.setText(String.format("%.2f", shippingFreeDouble)+" Php");
        getSupportActionBar().setTitle("Order# "+orderModel.getOrderId());

        displayModeOfPaymentButton(orderModel);

        llSubmitModeOfPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderModel != null && orderModel.getOrderStatus() != null &&
                        orderModel.getOrderStatus().equalsIgnoreCase(ApplicationConstants.PURCHASE_STATUS.keySet().toArray()[2].toString())) {
                    Intent proofOfPaymentIntent = new Intent(OrderDetailsActivity.this, ProofOfPaymentActivity.class);
                    proofOfPaymentIntent.putExtra("orderModel", orderModel);
                    startActivityForResult(proofOfPaymentIntent, ApplicationConstants.RESULT_FROM_SUBMIT_PROOF_OF_PAYMENT);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == ApplicationConstants.RESULT_FROM_SUBMIT_PROOF_OF_PAYMENT && data != null) {
            updatedPaymentOrderModel = data.getParcelableExtra("orderModel");
            displayModeOfPaymentButton(updatedPaymentOrderModel);
        }
    }

    private void displayModeOfPaymentButton(OrderModel orderModel) {
        if(orderModel != null && orderModel.getOrderStatus() != null) {
            if (orderModel.getOrderStatus().equalsIgnoreCase(ApplicationConstants.PURCHASE_STATUS.keySet().toArray()[2].toString())) {
                llSubmitModeOfPayment.setBackgroundColor(getResources().getColor(R.color.buttons_color));
                tvModeOfPaymentLabel.setText(getString(R.string.btn_submit_proof_of_payment));
            } else {
                llSubmitModeOfPayment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvModeOfPaymentLabel.setText(ApplicationConstants.PURCHASE_STATUS.get(orderModel.getOrderStatus()));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if(updatedPaymentOrderModel != null) {
                    setResult(ApplicationConstants.RESULT_FROM_SUBMIT_PROOF_OF_PAYMENT);
                }
                finish();
                break;
        }
        return true;
    }

}
