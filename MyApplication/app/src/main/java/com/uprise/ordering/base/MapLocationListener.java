package com.uprise.ordering.base;

import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.model.LocationDetailsModel;

/**
 * Created by cicciolina on 11/3/16.
 */

public interface MapLocationListener {
    void onLatLngChanged(LatLng latLng);
    void address(LocationDetailsModel shopOnMapModel);
    boolean isOnShopNowPage();
//    List<LocationDetailsModel> getLocationsModel();


}
