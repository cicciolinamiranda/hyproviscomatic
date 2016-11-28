package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.OrderModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.OrderListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderListActivity extends BaseAuthenticatedActivity implements RestCallServices.RestServiceListener {

    private ListView orderList;
    private ArrayAdapter<OrderModel> orderModelArrayAdapter;
    private String username;
    private LinearLayout llNoRecords;
    private RelativeLayout rlShopCartLoader;
    private ArrayList<OrderModel> orderModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        sqlDatabaseHelper = new SqlDatabaseHelper(OrderListActivity.this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        orderList = (ListView) findViewById(R.id.list_orders);
        llNoRecords = (LinearLayout) findViewById(R.id.ll_order_list_no_records);
        rlShopCartLoader = (RelativeLayout) findViewById(R.id.rl_order_list_loading_layout);

        restCallServices = new RestCallServices(this);
        orderModels = new ArrayList<>();
        if(loginModel != null) restCallServices.getPurchaseList(OrderListActivity.this,
                this, loginModel);

        showLoader();

    }

    private void showLoader() {
        orderList.setVisibility(View.GONE);
        llNoRecords.setVisibility(View.GONE);
        rlShopCartLoader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        orderList.setVisibility(View.VISIBLE);
        llNoRecords.setVisibility(View.GONE);
        rlShopCartLoader.setVisibility(View.GONE);
    }

    private void showNoRecords() {
            orderList.setVisibility(View.GONE);
            llNoRecords.setVisibility(View.VISIBLE);
            rlShopCartLoader.setVisibility(View.GONE);
    }

    private void populateList() {

        if(orderModels != null && !orderModels.isEmpty()) {
            orderModelArrayAdapter = new OrderListView(OrderListActivity.this,
                    orderModels);

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

    @Override
    public int getResultCode() {
        return 0;
    }

    @Override
    public void onSuccess(RestCalls callType, String string) {
        hideLoader();
        try {
//            JSONObject jsnobject = new JSONObject(string);
            JSONArray jsonArray = new JSONArray(string);

//            if(jsnobject != null) {
//                jsnobject = jsnobject.getJSONObject("results");
//            }

            if(jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject items = jsonArray.getJSONObject(i);
                    if(items != null) {
                        orderModels.add(Util.getInstance().generateOrderModelFromJson(items));
                    }
                }
                populateList();
            }

        } catch (JSONException e) {
            showNoRecords();
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        showNoRecords();
    }
}
