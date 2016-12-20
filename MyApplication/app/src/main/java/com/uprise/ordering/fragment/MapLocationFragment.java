package com.uprise.ordering.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uprise.ordering.LandingActivity;
import com.uprise.ordering.R;
import com.uprise.ordering.base.LocationTrackingBase;
import com.uprise.ordering.base.MapLocationListener;
import com.uprise.ordering.constant.ApplicationConstants;
import com.uprise.ordering.database.SqlDatabaseHelper;
import com.uprise.ordering.model.BranchModel;
import com.uprise.ordering.model.LoginModel;
import com.uprise.ordering.rest.RestCalls;
import com.uprise.ordering.rest.service.RestCallServices;
import com.uprise.ordering.util.Util;
import com.uprise.ordering.util.WifiScanManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cicciolina on 11/3/16.
 */

public class MapLocationFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerDragListener,
         GoogleMap.OnCameraChangeListener,
        View.OnFocusChangeListener,
        LocationTrackingBase.LocationTrackingBaseListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RestCallServices.RestServiceListener{

    private static final String MARKER_TITLE = "My Current Location";
    private GoogleMap mMap;
    private Marker marker;
//    private ArrayList<ShopOnMapModel> shopsOnMap;
    private LocationTrackingBase locationTrackingBase;
    private LatLng currPoint;
//    private LoginSharedPref loginSharedPref;
    private SqlDatabaseHelper sqlDatabaseHelper;
    private LoginModel loginModel;
    private MapLocationListener listener;
    private MapView mapView;
    private View v;
    private int markerSize = 150;
    private String nextUrl;

    /** Location name purposes **/
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    private String mAddressOutput;
    private boolean mAddressRequested;

//    private AddressResultReceiver mResultReceiver;
    private RestCallServices restCallServices;
    private ArrayList<BranchModel> shopOnMapModels;
    private RelativeLayout rlLoadingLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_map_location, container, false);
//        loginSharedPref = new LoginSharedPref();
        restCallServices = new RestCallServices(getContext());

        sqlDatabaseHelper = new SqlDatabaseHelper(getContext());
        loginModel = sqlDatabaseHelper.getLoginCredentials();
        rlLoadingLayout = (RelativeLayout) v.findViewById(R.id.rl_frag_map_loading_layout);


        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setOnFocusChangeListener(this);
        currPoint = null;

        mAddressOutput = "";
        final String branchEndpoint = getResources().getString(R.string.endpoint_server)
                + getResources().getString(R.string.endpoint_get_branch);
//        if(loginModel != null && loginModel.getUsername() != null) {
            restCallServices.getResellers(getContext(), this, branchEndpoint);
//        }

        updateValuesFromBundle(savedInstanceState);

        LocationManager lm = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        try
        {
            lm.addGpsStatusListener(new android.location.GpsStatus.Listener() {
                public void onGpsStatusChanged(int event) {
                    switch (event) {
                        case GpsStatus.GPS_EVENT_STARTED:
                            MapLocationFragment.this.getCurrPoint();
                            break;
                        case GpsStatus.GPS_EVENT_STOPPED:
                            WifiScanManager.getInstance().checkGPS(MapLocationFragment.this.getActivity());
                            break;
                    }
                }
            });}
        catch (SecurityException s){
            s.printStackTrace();
            Log.d(ApplicationConstants.APP_CODE, "permission onError:" + s.getMessage());
        }
        return v;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("Camera postion change" + "", cameraPosition + "");
        marker.setPosition(cameraPosition.target);
        saveLatLng();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        marker.remove();

//        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_store_black_48dp);
//        Bitmap b = bitmapdraw.getBitmap();
//        Bitmap storeMarker = Bitmap.createScaledBitmap(b, markerSize, markerSize, false);
//
//        if(loginModel != null && loginModel.getUsername() != null) {
//            restCallServices.getBranch(getContext(), this, loginModel.getToken());
//        }
//
//        if(listener.isOnShopNowPage()  && shopOnMapModels != null && shopOnMapModels.size() > 0) {
//            for (LocationDetailsModel shopOnMap : shopOnMapModels) {
//                Marker shopMaker = mMap.addMarker(new MarkerOptions().position(shopOnMap.getLocation())
//                        .icon(BitmapDescriptorFactory.fromBitmap(storeMarker))
//                        .title(shopOnMap.getAddress()));
//                shopMaker.setVisible(true);
//                startIntentService(shopOnMap);
//
//            }
//        }

        marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(MARKER_TITLE));
        marker.setVisible(true);
        if(listener.isOnShopNowPage()) marker.setVisible(false);

            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraChangeListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14.0f));
        saveLatLng();

//        for(ShopOnMapModel shopOnMap: getShopsLocation()) {
//            startIntentService(shopOnMap);
//        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
//        listener.onFocusChanged(false);
          saveLatLng();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(listener.isOnShopNowPage()) {
            mapView.setVisibility(View.GONE);
            rlLoadingLayout.setVisibility(View.VISIBLE);
        }

        mMap = googleMap;
        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        currPoint = new LatLng( 0.0f
                , 0.0f);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currPoint, 10.0f));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            MapsInitializer.initialize(this.getActivity());

            marker = mMap.addMarker(new MarkerOptions().position(currPoint)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("My Location"));
            marker.setVisible(true);
            if (listener.isOnShopNowPage()) marker.setVisible(false);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerDragListener(this);

            if (mapView != null &&
                    mapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                View zoomButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("1"));
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams locLayoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                // position on right bottom
                locLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                locLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                locLayoutParams.setMargins(0, 0, 35, 450);

//
//            RelativeLayout.LayoutParams zoomLayoutParams = (RelativeLayout.LayoutParams)
//                    zoomButton.getLayoutParams();
//            // position on right bottom
//            zoomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//            zoomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);


            }
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
//        listener.onFocusChanged(hasFocus);
    }

    @Override
    public Context getIContext() {
        return getContext();
    }

    @Override
    public Activity getIActivity() {
        return getActivity();
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
//        if (!isCurrPointExisting()) {
            if(latlng != null) onMapClick(latlng);

//        }
    }

    public LatLng getCurrPoint() {
        return currPoint;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mAddressRequested = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        locationTrackingBase = new LocationTrackingBase(this);
        locationTrackingBase.getGoogleApiClient().connect();
        super.onStart();
    }

    public void setOnFocusChangedListener(MapLocationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        restCallServices = null;
        rlLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void saveLatLng() {

        currPoint = marker.getPosition();
        locationTrackingBase.setLatLng(currPoint);
        listener.onLatLngChanged(currPoint);
//        if(listener.isOnShopNowPage()  && shopOnMapModels != null && shopOnMapModels.size() > 0) {
//            for (LocationDetailsModel shopOnMap : shopOnMapModels) {
////                startIntentService(shopOnMap);
//            }
//
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isCurrPointExisting() {
        return (currPoint != null) && (currPoint.latitude != 0 && currPoint.longitude != 0) && (marker != null);
    }

//    private List<LocationDetailsModel> getShopsLocation() {
////        ArrayList<LocationDetailsModel> shopOnMapModels =
//
//
////        //TODO replace with REST api call
//
//        if(shopOnMapModels != null && shopOnMapModels.size() > 0) {
//            for (int i = 0; i < Util.latLngs.size(); i++) {
//                LocationDetailsModel shopOnMapModel = new LocationDetailsModel();
//                LatLng latLng = Util.latLngs.get(i);
////            shopOnMapModel.setTitle("Location "+i);
//                shopOnMapModel.setLocation(latLng);
//                shopOnMapModels.add(shopOnMapModel);
//            }
//        }
//        return shopOnMapModels;
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save whether the address has been requested.
        outState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        outState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(outState);
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
//                displayAddressOutput();
            }
        }
    }

    @Override
    public int getResultCode() {
        return 0;
    }


    @Override
    public void onSuccess(RestCalls callType, String string) {

        if(listener.isOnShopNowPage() && isVisible()) {
            mapView.setVisibility(View.VISIBLE);
            rlLoadingLayout.setVisibility(View.GONE);
            shopOnMapModels = new ArrayList<>();
            try {
                JSONObject jsnobject = new JSONObject(string);
                JSONArray jsonArray = new JSONArray();
                if (jsnobject != null) {
                    jsonArray = jsnobject.getJSONArray("results");
                }

                if(jsnobject.getString("next") != null && !jsnobject.getString("next").isEmpty() && !jsnobject.getString("next").contentEquals("null"))  {

                    nextUrl = jsnobject.getString("next");
                }

                if (jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i) != null) {
                            shopOnMapModels.add(Util.getInstance().generateBranchModelFromJson(jsonArray.getJSONObject(i)));
                        }

                    }

                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_store_black_48dp);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap storeMarker = Bitmap.createScaledBitmap(b, markerSize, markerSize, false);


                    if (listener.isOnShopNowPage() && shopOnMapModels != null && shopOnMapModels.size() > 0) {
                        for (BranchModel shopOnMap : shopOnMapModels) {
                            LatLng location = Util.convertStrToLocation(shopOnMap.getLat(), shopOnMap.getLng());
                            Marker shopMaker = mMap.addMarker(new MarkerOptions().position(location)
                                    .icon(BitmapDescriptorFactory.fromBitmap(storeMarker))
                                    .title("Branch Name: "+shopOnMap.getName())
                                    .snippet("Address: "+shopOnMap.getAddress()+"\n "+"Contact Number: "+shopOnMap.getContactNum()));
                            shopMaker.setVisible(true);
//                            startIntentService(shopOnMap);
                            listener.address(shopOnMap);
                        }
                    }

                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View v = getActivity().getLayoutInflater().inflate(R.layout.marker, null);
                            TextView title= (TextView) v.findViewById(R.id.title);
                            TextView info= (TextView) v.findViewById(R.id.info);

                            title.setText(marker.getTitle());
                            info.setText(marker.getSnippet());

                            return v;
                        }
                    });

                    if(jsnobject.getString("next") != null && !jsnobject.getString("next").isEmpty() && !jsnobject.getString("next").contentEquals("null"))  {
                        nextUrl = jsnobject.getString("next");
                        restCallServices.getResellers(getActivity(), this, nextUrl);
                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    private LocationDetailsModel generateBranchModelFromJson(JSONObject jsonObject) {
//        LocationDetailsModel shopsLocation = new LocationDetailsModel();
//
//        try {
//            if(jsonObject.getString("lat") != null && jsonObject.getString("lng") != null) {
//                double lat = 0;
//                double lng = 0;
//                if(!jsonObject.getString("lat").isEmpty()) lat = Double.parseDouble(jsonObject.getString("lat"));
//                if(!jsonObject.getString("lng").isEmpty()) lng = Double.parseDouble(jsonObject.getString("lng"));
//                shopsLocation.setLocation(new LatLng(lat, lng));
//            }
//
//            if(jsonObject.getString("address") != null) shopsLocation.setAddress(jsonObject.getString("address"));
//            return shopsLocation;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    @Override
    public void onFailure(RestCalls callType, String string) {

        mapView.setVisibility(View.VISIBLE);
        rlLoadingLayout.setVisibility(View.GONE);
        if(string.contentEquals("Session is already expired")) {
            Util.getInstance().showSnackBarToast(getContext(), string);
            sqlDatabaseHelper.logOut(loginModel);
            getActivity().finish();
            getContext().startActivity(new Intent(getActivity(), LandingActivity.class));
        } else {
            Util.getInstance().showSnackBarToast(getContext(), getContext().getString(R.string.unable_to_retrieve_branch));
        }

    }

//    private class AddressResultReceiver extends ResultReceiver {
//        public AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        /**
//         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
//         */
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//
////            // Display the address string or an error message sent from the intent service.
////            mAddressOutput = resultData.getString(EndpointConstants.RESULT_DATA_KEY);
////            displayAddressOutput();
////
////            // Reset. Enable the Fetch Address button and stop showing the progress bar.
////            mAddressRequested = false;
////            listener.address((LocationDetailsModel) resultData.getParcelable(ApplicationConstants.RESULT_DATA_KEY));
//
//        }
//    }
//
//    protected void startIntentService(LocationDetailsModel shopOnMapModel) {
//        // Create an intent for passing to the intent service responsible for fetching the address.
//        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
//        mResultReceiver = new AddressResultReceiver(new Handler());
//        // Pass the result receiver as an extra to the service.
//        intent.putExtra(ApplicationConstants.RECEIVER, mResultReceiver);
//
//        // Pass the location data as an extra to the service.
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//
////            Location loc = new Location(ApplicationConstants.APP_CODE);
////            loc.setLatitude(shopOnMap.getLocation().latitude);
////            loc.setLongitude(shopOnMap.getLocation().longitude);
//            intent.putExtra(ApplicationConstants.LOCATION_DATA_EXTRA, shopOnMapModel);
//
//            // Start the service. If the service isn't already running, it is instantiated and started
//            // (creating a process for it if needed); if it is running then it remains running. The
//            // service kills itself automatically once all intents are processed.
//            getActivity().startService(intent);
//
//
//    }

}
