package com.uprise.ordering.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.util.Util;

/**
 * Created by cicciolina on 11/3/16.
 */

public class LocationTrackingBase {

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LatLng latLng = null;
    private String location = null; //used when no latlng is found
    private LocationTrackingBaseListener locationTrackingBaseListener;

    public LocationTrackingBase(LocationTrackingBaseListener locationTrackingBaseListener) {
        this.locationTrackingBaseListener = locationTrackingBaseListener;
        init();
    }

    public interface LocationTrackingBaseListener {
        Context getIContext();
        Activity getIActivity();
        GoogleApiClient.ConnectionCallbacks getConnectionCallbacks();
        GoogleApiClient.OnConnectionFailedListener getOnConnectionFailedListener();
        void setLocation(LatLng latlng);
    }

    protected void init() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(getConnectionCallbacks())
                    .addOnConnectionFailedListener(getOnConnectionFailedListener())
                    .addApi(LocationServices.API)
//                    .addScope(Plus.SCOPE_PLUS_LOGIN)
//                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                    .build();
        }
    }

    private Context getContext() {return locationTrackingBaseListener.getIContext();}

    private Activity getActivity() {return locationTrackingBaseListener.getIActivity();}

    private GoogleApiClient.ConnectionCallbacks getConnectionCallbacks() {return locationTrackingBaseListener.getConnectionCallbacks();}

    private GoogleApiClient.OnConnectionFailedListener getOnConnectionFailedListener() {return locationTrackingBaseListener.getOnConnectionFailedListener();}


    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean canGetLocation(){
        return latLng != null && (latLng.latitude != 0 && latLng.longitude != 0);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLocation() {
        LatLng latLng = null;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Util.requestPermission(getActivity());
            return null;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null) {
            latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            locationTrackingBaseListener.setLocation(latLng);
        }
        return latLng;
    }
}
