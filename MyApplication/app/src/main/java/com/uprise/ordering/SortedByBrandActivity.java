package com.uprise.ordering;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.uprise.ordering.model.LoginModel;
import com.uprise.ordering.model.SorterBrandModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.SortedBrandNamesList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SortedByBrandActivity extends AppCompatActivity implements RestCallServices.RestServiceListener {

    private LinearLayout llNoBrandsAvail;
    private LinearLayout llBrandNames;
    private List<SorterBrandModel> sorterBrandModelList;
    private ArrayAdapter<SorterBrandModel> sorterBrandModelArrayAdapter;
    private RelativeLayout loadingLayout;
    private MenuItem previousMenu;
    private MenuItem nextMenu;
    private String nextUrl;
    private String prevUrl;
    private ListView lvBrandNames;
    private RestCallServices restCallServices;
    private LoginModel loginModel;
    private SqlDatabaseHelper sqlDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorted_by_brand);

        llNoBrandsAvail = (LinearLayout) findViewById(R.id.ll_brand_no_records);
        llBrandNames = (LinearLayout) findViewById(R.id.ll_brand_names);
        lvBrandNames = (ListView) findViewById(R.id.list_brand_names);
        loadingLayout = (RelativeLayout) findViewById(R.id.rl_brand_list_loading_layout);
        sorterBrandModelList = new ArrayList<>();
        restCallServices = new RestCallServices(SortedByBrandActivity.this);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        loginModel = new LoginModel();
        sqlDatabaseHelper = new SqlDatabaseHelper(SortedByBrandActivity.this);
        loginModel = sqlDatabaseHelper.getLoginCredentials();

        final String brandEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_brand_names);
        if(loginModel != null && loginModel.getToken() != null &&
                Util.getInstance().isNetworkAvailable(this)) {
            restCallServices.getBrandNames(SortedByBrandActivity.this, this, loginModel.getToken(), brandEndpoint);
        } else {
            llNoBrandsAvail.setVisibility(View.VISIBLE);
            llBrandNames.setVisibility(View.GONE);
             loadingLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent nextIntent = new Intent(SortedByBrandActivity.this, MainActivity.class);

                finish();
                break;


            case R.id.menu_orderlist_prev:
                llBrandNames.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                sorterBrandModelList = new ArrayList<>();
//                restCallServices.getBranch(ExistingBranchActivity.this, this, loginModel.getToken(), prevUrl);
                break;
            case R.id.menu_orderlist_next:
                llBrandNames.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                sorterBrandModelList = new ArrayList<>();
//                restCallServices.getBranch(ExistingBranchActivity.this, this,loginModel.getToken(), nextUrl);
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
        nextMenu.setVisible(false);
        previousMenu.setVisible(false);

        try {
            JSONObject jsnobject = new JSONObject(string);
            JSONArray jsonArray = new JSONArray();
            if(jsnobject != null) {
                jsonArray = jsnobject.getJSONArray("results");
            }

            if(jsnobject.getString("next") != null && !jsnobject.getString("next").isEmpty() && !jsnobject.getString("next").contentEquals("null"))  {

                nextUrl = jsnobject.getString("next");
                nextMenu.setVisible(true);
            }

            if(jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i) != null) {

                        sorterBrandModelList.add(Util.getInstance().generateBrandNamesFromJson(jsonArray.getJSONObject(i)));
                    }

                }

            }

            checkIfTheresData();
            if(sorterBrandModelList != null && !sorterBrandModelList.isEmpty()) {
                populateList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            checkIfTheresData();
        }
    }

    private void populateList() {
        sorterBrandModelArrayAdapter = new SortedBrandNamesList(SortedByBrandActivity.this, sorterBrandModelList);
        sorterBrandModelArrayAdapter.notifyDataSetChanged();
        lvBrandNames.setAdapter(sorterBrandModelArrayAdapter);
        lvBrandNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent nextIntent;
                if(loginModel != null && loginModel.getToken() != null) {
                    nextIntent = new Intent(SortedByBrandActivity.this, MainActivity.class);
                    nextIntent.putExtra("sorterBrandModel", sorterBrandModelList.get(position));
                } else {
                    nextIntent = new Intent(SortedByBrandActivity.this, DistributorShopActivity.class);
                    nextIntent.putExtra("sorterBrandModel", sorterBrandModelList.get(position));

                }

                startActivity(nextIntent);
                finish();
            }
        });
    }

    @Override
    public void onFailure(RestCalls callType, String string) {
        Util.getInstance().showDialog(this, string, this.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        checkIfTheresData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.pagination_menu, menu);
        previousMenu = menu.findItem(R.id.menu_orderlist_prev);
        previousMenu.setVisible(false);
        nextMenu = menu.findItem(R.id.menu_orderlist_next);
        nextMenu.setVisible(false);

        return true;
    }

    private void checkIfTheresData() {
        if(sorterBrandModelList != null && !sorterBrandModelList.isEmpty()) {
            llNoBrandsAvail.setVisibility(View.GONE);
            llBrandNames.setVisibility(View.VISIBLE);
        } else {
            llNoBrandsAvail.setVisibility(View.VISIBLE);
            llBrandNames.setVisibility(View.GONE);
        }
        loadingLayout.setVisibility(View.GONE);
    }

}
