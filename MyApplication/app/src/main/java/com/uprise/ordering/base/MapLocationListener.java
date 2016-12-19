package com.uprise.ordering.base;

import com.google.android.gms.maps.model.LatLng;
import com.uprise.ordering.model.BranchModel;

/**
 * Created by cicciolina on 11/3/16.
 */

public interface MapLocationListener {
    void onLatLngChanged(LatLng latLng);
    void address(BranchModel branchModel);
    boolean isOnShopNowPage();
//    List<LocationDetailsModel> getLocationsModel();


}
