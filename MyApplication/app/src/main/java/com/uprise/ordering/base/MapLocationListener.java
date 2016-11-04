package com.uprise.ordering.base;

import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.model.ShopOnMapModel;

/**
 * Created by cicciolina on 11/3/16.
 */

public interface MapLocationListener {
    void onFocusChanged(boolean isFocused);
    void onLatLngChanged(LatLng latLng);
    void address(ShopOnMapModel shopOnMapModel);


}
