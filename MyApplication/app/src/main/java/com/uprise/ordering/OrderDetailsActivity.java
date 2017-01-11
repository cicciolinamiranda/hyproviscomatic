package com.uprise.ordering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.uprise.ordering.model.OrderItemsModel;
import com.uprise.ordering.model.OrderModel;
import com.uprise.ordering.model.ProductModel;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.OrderItemsListView;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    private ListView lvOrderItemsList;
    private TextView tvEstimatedTotal;
    private TextView tvNetTotal;
    private TextView tvDiscount;
    private ArrayAdapter<OrderItemsModel> cartItemsModelArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        lvOrderItemsList = (ListView) findViewById(R.id.list_order_items);
        tvEstimatedTotal = (TextView) findViewById(R.id.tv_order_item__estimated_total_value);
        tvDiscount = (TextView) findViewById(R.id.tv_order_item__estimated_discount_value);
        tvNetTotal = (TextView) findViewById(R.id.tv_order_item_net_total_value);
        OrderModel orderModel = getIntent().getParcelableExtra("orderModel");
        //TODO: rest call
        ArrayList<ProductModel> productModels = Util.getInstance().generateProductModels();
        cartItemsModelArrayAdapter = new OrderItemsListView(OrderDetailsActivity.this,
                orderModel.getOrderItemsModels(),
                productModels);
        lvOrderItemsList.setAdapter(cartItemsModelArrayAdapter);
        tvEstimatedTotal.setText(String.format("%.2f", orderModel.getTotalAmount())+" Php");
        double computedDiscountPercentage = orderModel.getTotalAmount() * (orderModel.getDiscount()/100);
        double netTotal = orderModel.getTotalAmount() - computedDiscountPercentage;
        tvNetTotal.setText(String.format("%.2f", netTotal)+" Php");
        tvDiscount.setText(String.format("%.2f", computedDiscountPercentage)+" Php");
        getSupportActionBar().setTitle("Order# "+orderModel.getOrderId());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
