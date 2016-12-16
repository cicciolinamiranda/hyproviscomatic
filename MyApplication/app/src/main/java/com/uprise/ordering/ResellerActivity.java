package com.uprise.ordering;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.base.LocationTrackingBase;
import com.uprise.ordering.base.MapLocationListener;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.fragment.BranchFragment;
import com.uprise.ordering.fragment.MapLocationFragment;
import com.uprise.ordering.model.LocationDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class ResellerActivity extends LandingSubPageBaseActivity implements MapLocationListener, SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener, LocationTrackingBase.LocationTrackingBaseListener
    {

        private MapLocationFragment mapLocationFragment;
        private BranchFragment branchFragment;
        private SearchView searchShopsView;
        protected SimpleCursorAdapter shopsAdapter;
        private List<LocationDetailsModel> shopOnMapModelList;
        private List<LocationDetailsModel> matchedResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseller);
//        mapLocationFragment = (MapLocationFragment) getSupportFragmentManager().findFragmentById(R.id.frag_reseller);
//        mapLocationFragment.setOnFocusChangedListener(this);

        branchFragment = (BranchFragment) getSupportFragmentManager().findFragmentById(R.id.frag_reseller);

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
            setSelectedShopAddress(position);
            return false;
        }

        @Override
        public boolean onSuggestionClick(int position) {
            setSelectedShopAddress(position);
            return false;
        }

        @Override
        public void onLatLngChanged(LatLng latLng) {

        }

        @Override
        public void address(LocationDetailsModel shopOnMapModel) {
            shopOnMapModelList.add(shopOnMapModel);
        }

        @Override
        public boolean isOnShopNowPage() {
            return true;
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.main, menu);
            searchShopsView = (SearchView) menu.findItem(R.id.search_shop).getActionView();
            menu.findItem(R.id.search_shop).setVisible(false);
            searchShopsView.setQueryHint(getString(R.string.shops_search_hint));
            searchShopsView.setOnQueryTextListener(this);
            searchShopsView.setOnSuggestionListener(this);
            searchShopsView.setSuggestionsAdapter(shopsAdapter);
            searchShopsView.setIconifiedByDefault(false);

            //TODO: when fragment is change to MapFragment, please change this to true
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
                    LocationDetailsModel shopOnMapModel = data.getParcelableExtra("shopOnMapModel");
                    mapLocationFragment.setLocation(shopOnMapModel.getLocation());
                    break;
            }
        }

        private boolean setSelectedShopAddress(int position) {
            mapLocationFragment.setLocation(matchedResults.get(position).getLocation());
            return false;
        }

        @Override
        public Context getIContext() {
            return this;
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
            LocationDetailsModel shopOnMapModel = getIntent().getParcelableExtra("shopOnMapModel");
            if(shopOnMapModel != null) {
                mapLocationFragment.setLocation(shopOnMapModel.getLocation());
            }
            else {
                mapLocationFragment.setLocation(latlng);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return false;
        }
    }
