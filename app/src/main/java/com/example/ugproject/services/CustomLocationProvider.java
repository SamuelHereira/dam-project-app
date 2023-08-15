package com.example.ugproject.services;

import android.location.Location;

public class CustomLocationProvider {

    private static CustomLocationProvider instance;
    private Location location;
    private LocationCallback locationCallback;

    private CustomLocationProvider() {
    }

    public static synchronized CustomLocationProvider getInstance() {
        if (instance == null) {
            instance = new CustomLocationProvider();
        }
        return instance;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (locationCallback != null) {
            locationCallback.onLocationUpdated(location);
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocationCallback(LocationCallback callback) {
        this.locationCallback = callback;
    }
}
