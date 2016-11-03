package com.uprise.ordering.base;

import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.model.ShopOnMapModel;

import java.util.List;

/**
 * Created by cicciolina on 11/3/16.
 */

public interface MapLocationListener {
    void onFocusChanged(boolean isFocused);
    void onLatLngChanged(LatLng latLng);
    List<ShopOnMapModel> getShopsLocation();
}
