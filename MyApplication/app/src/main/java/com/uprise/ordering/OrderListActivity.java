package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uprise.ordering.model.OrderModel;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.OrderListView;

public class OrderListActivity extends AppCompatActivity {

    private ListView orderList;
    private ArrayAdapter<OrderModel> orderModelArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        getSupportActionBar().setTitle("My Orders");
        orderList = (ListView) findViewById(R.id.list_orders);

        orderModelArrayAdapter = new OrderListView(OrderListActivity.this,
                Util.getInstance().generateOrders(OrderListActivity.this));

        orderList.setAdapter(orderModelArrayAdapter);
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderListActivity.this, OrderDetailsActivity.class);
                intent.putExtra("orderModel", orderModelArrayAdapter.getItem(position));
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(OrderListActivity.this, MainActivity.class));
                break;
        }
        return true;
    }
}
