package com.uprise.ordering.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.BaseAuthenticatedActivity;
import com.uprise.ordering.constant.ApplicationConstants;

/**
 * Created by cicciolina on 11/3/16.
 */

public abstract class LocationTrackingActivity extends BaseAuthenticatedActivity implements LocationTrackingBase.LocationTrackingBaseListener {

    protected LocationTrackingBase locationTrackingBase;

//    @Override
//    protected void onResume() {
//        locationTrackingBase = new LocationTrackingBase(this);
//        locationTrackingBase.getGoogleApiClient().connect();
//        super.onResume();
//    }



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

    public abstract void setLocation(LatLng latlng);

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        locationTrackingBase.setLatLng(locationTrackingBase.getLocation());
    }

    protected LatLng getLocation() {
        return locationTrackingBase.getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        super.onConnectionSuspended(i);
        locationTrackingBase.setLastLocation(null);
        locationTrackingBase.setLatLng(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //super.onConnectionFailed(connectionResult);
        if(connectionResult.getErrorMessage() != null) Log.i(ApplicationConstants.APP_CODE, connectionResult.getErrorMessage());
        locationTrackingBase.setLastLocation(null);
        locationTrackingBase.setLatLng(null);
    }
}
