package com.uprise.ordering;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.uprise.ordering.base.LocationTrackingBase;
import com.uprise.ordering.base.LocationTrackingBase.LocationTrackingBaseListener;
import com.uprise.ordering.base.MapLocationListener;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.fragment.MapLocationFragment;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.LocationDetailsModel;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.view.PlaceAutocompleteAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchAddressActivity extends AppCompatActivity implements MapLocationListener,
        LocationTrackingBaseListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnClickListener,
        AdapterView.OnItemClickListener{

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1  ;

    private MapLocationFragment mapLocationFragment;
    private LinearLayout llSearchAddress;
    private TextView tvSearchAddress;
    private ImageView ivSearch;
    protected LocationTrackingBase locationTrackingBase;
    private LocationDetailsModel locationDetailsModel;

    private PlaceAutocompleteAdapter mAdapter;

    private boolean isFromSuggestion;

    private LatLngBounds PHILIPPINES = new LatLngBounds(new LatLng(4.6145711,119.6272661), new LatLng(19.966096,124.173694));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.asset_nav_back);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText(getString(R.string.title_activity_search_address));
        mapLocationFragment = (MapLocationFragment) getSupportFragmentManager().findFragmentById(R.id.search_address_map_frag);
        mapLocationFragment.setOnFocusChangedListener(this);
        locationDetailsModel = new LocationDetailsModel();

        llSearchAddress = (LinearLayout) findViewById(R.id.ll_search_address);
        llSearchAddress.setOnClickListener(this);
        tvSearchAddress = (TextView) findViewById(R.id.tv_search_address_value);
        ivSearch = (ImageView) findViewById(R.id.img_search_address);
        ivSearch.setColorFilter(getResources().getColor(R.color.grey_500));
        // Retrieve the AutoCompleteTextView that will display Place suggestions.
//        mAutocompleteView = (AutoCompleteTextView)
//                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
//        mAutocompleteView.setOnItemClickListener(this);

        // Retrieve the TextViews that will display details and attributions of the selected place.
//        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
//        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

//        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
//        // the entire world.
//        mAdapter = new PlaceAutocompleteAdapter(this, locationTrackingBase.getGoogleApiClient(), PHILIPPINES,
//                null);
//        mAutocompleteView.setAdapter(mAdapter);


    }

    @Override
    public void onLatLngChanged(LatLng latLng) {
        if(locationDetailsModel == null) locationDetailsModel = new LocationDetailsModel();
        //TODO: address
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());



        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if(addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

//                locationDetailsModel.setAddress(address);
                locationDetailsModel.setLocation(latLng);

                if(!isFromSuggestion) {
                    locationDetailsModel.setAddress(address);
//                    mAutocompleteView.setText(locationDetailsModel.getAddress());
                    tvSearchAddress.setText(locationDetailsModel.getAddress());

                }
            }

            isFromSuggestion = false;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void address(BranchModel branchModel) {
        locationDetailsModel = new LocationDetailsModel();
        locationDetailsModel.setAddress(branchModel.getAddress());

        locationDetailsModel.setLocation(Util.convertStrToLocation(branchModel.getLat(), branchModel.getLng()));
//        mAutocompleteView.setText(locationDetailsModel.getAddress());
        tvSearchAddress.setText(locationDetailsModel.getAddress());
    }

//    @Override
//    public void address(LocationDetailsModel shopOnMapModel) {
//
//        locationDetailsModel = new LocationDetailsModel();
//        locationDetailsModel.setAddress(shopOnMapModel.getAddress());
//        locationDetailsModel.setLocation(shopOnMapModel.getLocation());
////        mAutocompleteView.setText(locationDetailsModel.getAddress());
//        tvSearchAddress.setText(locationDetailsModel.getAddress());
//    }

    @Override
    public boolean isOnShopNowPage() {
        return false;
    }

//    @Override
//    public void onSearchTextChanged(String oldQuery, String newQuery) {
//        openAutocompleteActivity();
//    }

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
        locationDetailsModel = getIntent().getParcelableExtra("locationDetailsModel");
        if(locationDetailsModel != null) {
            mapLocationFragment.setLocation(locationDetailsModel.getLocation());
            tvSearchAddress.setText(locationDetailsModel.getAddress());
        }
        else {
            mapLocationFragment.setLocation(latlng);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationTrackingBase.setLatLng(locationTrackingBase.getLocation());
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, locationTrackingBase.getGoogleApiClient(), PHILIPPINES,
                null);
//        mAutocompleteView.setAdapter(mAdapter);
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationTrackingBase.setLastLocation(null);
        locationTrackingBase.setLatLng(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.getErrorMessage() != null) Log.i(ApplicationConstants.APP_CODE, connectionResult.getErrorMessage());
        locationTrackingBase.setLastLocation(null);
        locationTrackingBase.setLatLng(null);

        Log.e(ApplicationConstants.APP_CODE, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    protected void onStart() {
        if(locationTrackingBase == null) {
            locationTrackingBase = new LocationTrackingBase(this);
        }
        super.onStart();
        locationTrackingBase.getGoogleApiClient().connect();
    }

    protected void onStop() {
        //
        super.onStop();
        locationTrackingBase.getGoogleApiClient().disconnect();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.search_address_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_select_address:

                if(locationDetailsModel != null && locationDetailsModel.getAddress() != null) {
                Intent result = new Intent(SearchAddressActivity.this, AddBranchActivity.class);
                       result.putExtra("locationDetailsModel", locationDetailsModel);
                     setResult(ApplicationConstants.REQUEST_CODE_ADD_BRANCH_LAT_LNG, result);
                        finish();
                } else {
                    Util.getInstance().showDialog(this, "Please pin the marker to the map again.", this.getString(R.string.action_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }

            break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
        final AutocompletePrediction item = mAdapter.getItem(position);
        final String placeId = item.getPlaceId();
        final CharSequence primaryText = item.getPrimaryText(null);

        Log.i(ApplicationConstants.APP_CODE, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(locationTrackingBase.getGoogleApiClient(), placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

//        Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
//                Toast.LENGTH_SHORT).show();
//        Log.i(ApplicationConstants.APP_CODE, "Called getPlaceById to get Place details for " + placeId);
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(ApplicationConstants.APP_CODE, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            isFromSuggestion = true;
            locationDetailsModel.setAddress(place.getName().toString());
            locationDetailsModel.setLocation(place.getLatLng());
            mapLocationFragment.onMapClick(place.getLatLng());
//            mAutocompleteView.setText(locationDetailsModel.getAddress());
//            Intent tvSearchAddressIntent = getIntent();
//            tvSearchAddressIntent.putExtra("locationDetailsModel", locationDetailsModel );
//            finish();
//            startActivity(tvSearchAddressIntent);

            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            Log.i(ApplicationConstants.APP_CODE, "Place details received: " + place.getName());

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(ApplicationConstants.APP_CODE, res.getString(R.string.place_details, name, address));
        return Html.fromHtml(res.getString(R.string.place_details, name, address));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ll_search_address:
                openAutocompleteActivity();
                break;
        }
    }
    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(ApplicationConstants.APP_CODE, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(ApplicationConstants.APP_CODE, "Place Selected: " + place.getName());

                // Format the place's details and display them in the TextView.
                tvSearchAddress.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));

                locationDetailsModel.setAddress(place.getName().toString());
                locationDetailsModel.setLocation(place.getLatLng());
                mapLocationFragment.onMapClick(place.getLatLng());

                // Display attributions if required.
//                CharSequence attributions = place.getAttributions();
//                if (!TextUtils.isEmpty(attributions)) {
//                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
//                } else {
//                    mPlaceAttribution.setText("");
//                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(ApplicationConstants.APP_CODE, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

}
