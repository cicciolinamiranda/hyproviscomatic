package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
    private MenuItem previousMenu;
    private MenuItem nextMenu;
    private String nextUrl;
    private String prevUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        sqlDatabaseHelper = new SqlDatabaseHelper(OrderListActivity.this);
        getSupportActionBar().setTitle(getResources().getString(R.string.label_my_orders));
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        orderList = (ListView) findViewById(R.id.list_orders);
        llNoRecords = (LinearLayout) findViewById(R.id.ll_order_list_no_records);
        rlShopCartLoader = (RelativeLayout) findViewById(R.id.rl_order_list_loading_layout);

        restCallServices = new RestCallServices(this);
        orderModels = new ArrayList<>();
        final String purchaseEndpoint = getResources().getString(R.string.endpoint_server)
        + getResources().getString(R.string.endpoint_get_purchase);
        if(loginModel != null &&
                Util.getInstance().isNetworkAvailable(this)) {
            restCallServices.getPurchaseList(OrderListActivity.this,
                    this, loginModel, purchaseEndpoint);

            showLoader();
        } else {
            showNoRecords();
        }

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
        } else {
            showNoRecords();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(OrderListActivity.this, MainActivity.class));
                break;
            case R.id.menu_orderlist_prev:
                orderModels.clear();
                restCallServices.getPurchaseList(OrderListActivity.this, this, loginModel, prevUrl);
                break;
            case R.id.menu_orderlist_next:
                orderModels.clear();
                restCallServices.getPurchaseList(OrderListActivity.this, this, loginModel, nextUrl);
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
        previousMenu.setVisible(false);
        nextMenu.setVisible(false);
        try {

            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = new JSONArray();
            if(jsonObject.getString("results") != null) {
                jsonArray = new JSONArray(jsonObject.getString("results"));
            }

            if(jsonObject.getString("next") != null && !jsonObject.getString("next").isEmpty() && !jsonObject.getString("next").contentEquals("null"))  {

                nextUrl = jsonObject.getString("next");
                nextMenu.setVisible(true);
            }
            if(jsonObject.getString("previous") != null && !jsonObject.getString("previous").isEmpty()
                    && !jsonObject.getString("previous").contentEquals("null")) {
                prevUrl = jsonObject.getString("previous");
                previousMenu.setVisible(true);
            }

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
        if(orderModels.size() <=0) showNoRecords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.pagination_menu, menu);
        previousMenu = menu.findItem(R.id.menu_orderlist_prev);
        previousMenu.setVisible(false);
        nextMenu = menu.findItem(R.id.menu_orderlist_next);
        nextMenu.setVisible(false);
        return true;
    }
}
