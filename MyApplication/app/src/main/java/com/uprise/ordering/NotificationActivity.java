package com.uprise.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.NotificationsModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.NotificationsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationActivity extends BaseAuthenticatedActivity implements RestCallServices.RestServiceListener {

    private ArrayAdapter<NotificationsModel> notificationsModelArrayAdapter;
    private ListView lvNotificationsList;
    private ArrayList<NotificationsModel> notificationsModelArrayList;
    private LinearLayout llNoRecords;
    private RelativeLayout rlShopCartLoader;
    private MenuItem previousMenu;
    private MenuItem nextMenu;
    private String nextUrl;
    private String prevUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText(getString(R.string.label_notifications));
        lvNotificationsList = (ListView) findViewById(R.id.list_notifications);
        llNoRecords = (LinearLayout) findViewById(R.id.ll_order_list_no_records);
        rlShopCartLoader = (RelativeLayout) findViewById(R.id.rl_order_list_loading_layout);

        sqlDatabaseHelper = new SqlDatabaseHelper(NotificationActivity.this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        restCallServices = new RestCallServices(this);
        final String notifEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_notifications);
        if(loginModel != null &&
        Util.getInstance().isNetworkAvailable(this)) {
            restCallServices.getNotifications(NotificationActivity.this,
                    this, loginModel, notifEndpoint);
            notificationsModelArrayList = new ArrayList<>();
            rlShopCartLoader.setVisibility(View.VISIBLE);
            showLoader();
        } else {
            showNoRecords();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(NotificationActivity.this, MainActivity.class));
                break;
            case R.id.menu_orderlist_prev:
                rlShopCartLoader.setVisibility(View.VISIBLE);
                notificationsModelArrayList = new ArrayList<>();
                restCallServices.getNotifications(NotificationActivity.this, this, loginModel, prevUrl);
                break;
            case R.id.menu_orderlist_next:
                rlShopCartLoader.setVisibility(View.VISIBLE);
                notificationsModelArrayList = new ArrayList<>();
                restCallServices.getNotifications(NotificationActivity.this, this, loginModel, nextUrl);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case ApplicationConstants.RESULT_FROM_DELETING_NOTIF_MSG:

                //TODO: to be replaced by rest call api
                notificationsModelArrayAdapter.clear();
                lvNotificationsList.setAdapter(notificationsModelArrayAdapter);
                break;
        }
    }

    private void showLoader() {
        lvNotificationsList.setVisibility(View.GONE);
        llNoRecords.setVisibility(View.GONE);
        rlShopCartLoader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        lvNotificationsList.setVisibility(View.VISIBLE);
        llNoRecords.setVisibility(View.GONE);
        rlShopCartLoader.setVisibility(View.GONE);
    }

    private void showNoRecords() {
        lvNotificationsList.setVisibility(View.GONE);
        llNoRecords.setVisibility(View.VISIBLE);
        rlShopCartLoader.setVisibility(View.GONE);
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
                        notificationsModelArrayList.add(Util.getInstance().generateNotificationsFromJson(items));
                    }
                }
                populateList();
            }

        } catch (JSONException e) {
            showNoRecords();
            e.printStackTrace();
        }
    }

    private void populateList() {

        if(notificationsModelArrayList != null && !notificationsModelArrayList.isEmpty()) {
            notificationsModelArrayAdapter = new NotificationsList(NotificationActivity.this, notificationsModelArrayList);
            notificationsModelArrayAdapter.notifyDataSetChanged();
            lvNotificationsList.setAdapter(notificationsModelArrayAdapter);
            registerForContextMenu(lvNotificationsList);
        } else {
            showNoRecords();
        }
    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        showNoRecords();
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
