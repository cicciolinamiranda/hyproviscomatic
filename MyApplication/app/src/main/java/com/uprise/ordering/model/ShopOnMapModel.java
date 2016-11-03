package com.uprise.ordering.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cicciolina on 11/3/16.
 */

public class ShopOnMapModel {

    private String title;
    private LatLng location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
