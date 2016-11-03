package com.uprise.ordering.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uprise.ordering.R;
import com.uprise.ordering.base.LocationTrackingBase;
import com.uprise.ordering.base.MapLocationListener;
import com.uprise.ordering.model.ShopOnMapModel;

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
        GoogleApiClient.OnConnectionFailedListener {

    private static final String MARKER_TITLE = "My Current Location";
    private GoogleMap mMap;
    private Marker marker;
//    private ArrayList<ShopOnMapModel> shopsOnMap;
    private LocationTrackingBase locationTrackingBase;
    private LatLng currPoint;
    private SharedPreferences sharedPreferences;
    private MapLocationListener listener;
    private MapView mapView;
    private View v;
    private int markerSize = 150;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_map_location, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setOnFocusChangeListener(this);
        currPoint = null;
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

        marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(MARKER_TITLE));
        marker.setVisible(true);
            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraChangeListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
        saveLatLng();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
         listener.onFocusChanged(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        listener.onFocusChanged(false);
        saveLatLng();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        currPoint = new LatLng( 0.0f
                , 0.0f);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {
            mMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_store_black_48dp);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap storeMarker = Bitmap.createScaledBitmap(b, markerSize, markerSize, false);

        for(ShopOnMapModel shopOnMap: listener.getShopsLocation()) {
            mMap.addMarker(new MarkerOptions().position(shopOnMap.getLocation())
                .icon(BitmapDescriptorFactory.fromBitmap(storeMarker))
                    .title(shopOnMap.getTitle()));

        }


        marker = mMap.addMarker(new MarkerOptions().position(currPoint)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("My Location"));

        marker.setVisible(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        listener.onFocusChanged(hasFocus);
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
        if (!isCurrPointExisting()) {
            onMapClick(latlng);

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void saveLatLng() {
        listener.onLatLngChanged(marker.getPosition());

        currPoint = marker.getPosition();
        locationTrackingBase.setLatLng(currPoint);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isCurrPointExisting() {
        return (currPoint != null) && (currPoint.latitude != 0 && currPoint.longitude != 0) && (marker != null);
    }

}
