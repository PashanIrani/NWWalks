package ca.bcit.comp37171.nwwalks;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by pashan on 2018-01-17.
 */

public class Place {

    private LatLng location;
    private String name;
    private String distanceFromCurrentLocation;
    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setLocation(double lat, double lng) {
        this.location = new LatLng(lat, lng);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistanceFromCurrentLocation() {
        return distanceFromCurrentLocation;
    }

    public void setDistanceFromCurrentLocation(String distanceFromCurrentLocation) {
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
    }

    @Override
    public String toString() {
        return "Place{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", distanceFromCurrentLocation='" + distanceFromCurrentLocation + '\'' +
                '}';
    }
}
