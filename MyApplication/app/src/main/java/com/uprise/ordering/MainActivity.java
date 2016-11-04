package com.uprise.ordering;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.base.LocationTrackingActivity;
import com.uprise.ordering.base.MapLocationListener;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.fragment.MapLocationFragment;
import com.uprise.ordering.model.ShopOnMapModel;
import com.uprise.ordering.shared.LoginSharedPref;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends LocationTrackingActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MapLocationListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private MapLocationFragment mapLocationFragment;
    private LoginSharedPref loginSharedPref;
    private View headerLayout;
    private SearchView searchShopsView;
    protected SimpleCursorAdapter shopsAdapter;
    private List<ShopOnMapModel> shopOnMapModelList;
    private List<ShopOnMapModel> matchedResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setTitle("Shops");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView tvEmail = (TextView) headerLayout.findViewById(R.id.tv_nav_header_username);

        loginSharedPref = new LoginSharedPref();
        if(!loginSharedPref.isLoggedIn(MainActivity.this)) {
            startActivity(new Intent(MainActivity.this, LandingActivity.class));
            finish();
        } else {
            tvEmail.setText(loginSharedPref.getUsername(MainActivity.this).toString());

            mapLocationFragment = new MapLocationFragment();
            mapLocationFragment.setOnFocusChangedListener(this);


            FragmentTransaction tx =  getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content_frame, mapLocationFragment);
            tx.commit();
        }

        shopOnMapModelList = new ArrayList<>();
        final String[] from = new String[] {"addresses"};
        final int[] to = new int[] {android.R.id.text1};
        shopsAdapter = new SimpleCursorAdapter(this,
                R.layout.custom_suggested_addresses,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()) {

            case R.id.nav_shop_now:
                finish();
                startActivity(new Intent(MainActivity.this, ProductsActivity.class));
                break;
            case R.id.nav_shopping_cart:
                finish();
                startActivity(new Intent(MainActivity.this, ShoppingCartActivity.class));
                break;
            case R.id.nav_notifications:
                finish();
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                break;
            case R.id.nav_branch:
                finish();
                startActivity(new Intent(MainActivity.this, ExistingBranch.class));
                break;
            case R.id.nav_orders:
                finish();
                startActivity(new Intent(MainActivity.this, OrderListActivity.class));
                break;
            case R.id.nav_logout:

                if(loginSharedPref.isLoggedIn(MainActivity.this)) {
                    loginSharedPref.logOut(MainActivity.this);
                    finish();
                    startActivity(new Intent(MainActivity.this, LandingActivity.class));
                }
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);


    }

    @Override
    public Context getIContext() {
        return MainActivity.this;
    }

    @Override
    public Activity getIActivity() {
        return this;
    }

    @Override
    public GoogleApiClient.ConnectionCallbacks getConnectionCallbacks() {
        return this;
    }

    @Override
    public GoogleApiClient.OnConnectionFailedListener getOnConnectionFailedListener() {
        return this;
    }

    @Override
    public void setLocation(LatLng latlng) {

        ShopOnMapModel shopOnMapModel = getIntent().getParcelableExtra("shopOnMapModel");
        if(shopOnMapModel != null) {
            mapLocationFragment.setLocation(shopOnMapModel.getLocation());
        }
        else {
            mapLocationFragment.setLocation(latlng);
        }
    }


    @Override
    public void onFocusChanged(boolean isFocused) {

    }

    @Override
    public void onLatLngChanged(LatLng latLng) {

    }

    @Override
    public void address(ShopOnMapModel shopOnMapModel) {
        shopOnMapModelList.add(shopOnMapModel);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main, menu);
        searchShopsView = (SearchView) menu.findItem(R.id.search_shop).getActionView();
        searchShopsView.setQueryHint(getString(R.string.shops_search_hint));
        searchShopsView.setOnQueryTextListener(this);
        searchShopsView.setOnSuggestionListener(this);
        searchShopsView.setSuggestionsAdapter(shopsAdapter);
        searchShopsView.setIconifiedByDefault(false);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(null != shopOnMapModelList) populateAdapter(newText);
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {

//        mapLocationFragment.setLocation(matchedResults.get(position).getLocation());
        setSelectedShopAddress(position);
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {

//        mapLocationFragment.setLocation(matchedResults.get(position).getLocation());
        setSelectedShopAddress(position);
        return false;
    }

    private void populateAdapter(String query) {
        ArrayList<String> addresses = new ArrayList<>();
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "addresses" });
        matchedResults = new ArrayList<>();
//        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "ssidName" });
//        Collections.sort(wifiScanner.getSearchResults(), new Comparator<ScanResult>() {
//            public int compare(ScanResult o1, ScanResult o2) {
//                return o1.SSID.compareToIgnoreCase(o2.SSID);
//            }
//        });

        mainLoop:
        for (int i = 0; i < shopOnMapModelList.size(); i++) {

            if(shopOnMapModelList.get(i).getAddress().toLowerCase().indexOf(query.toLowerCase()) >= 0) {

                addresses.add(shopOnMapModelList.get(i).getAddress());
                matchedResults.add(shopOnMapModelList.get(i));
                c.addRow(new Object[] {i, shopOnMapModelList.get(i).getAddress()
                        +" | lat: "+ shopOnMapModelList.get(i).getLocation().latitude
                        +" | long: "+ shopOnMapModelList.get(i).getLocation().longitude});
            }
        }

        if(addresses.isEmpty()) {
            c.addRow(new Object[] {0, "No Location found"});
        }
        shopsAdapter.changeCursor(c);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ApplicationConstants.RESULT_FROM_SEARCH_SELECTED_ADDRESS:
                ShopOnMapModel shopOnMapModel = data.getParcelableExtra("shopOnMapModel");
                mapLocationFragment.setLocation(shopOnMapModel.getLocation());
                break;
        }
    }

    private boolean setSelectedShopAddress(int position) {
        Intent intent = getIntent();
        intent.putExtra("shopOnMapModel", matchedResults.get(position));
        finish();
        startActivity(intent);
        return false;
    }
}
