package com.app.ivoke.helpers;

import com.app.ivoke.objects.UserIvoke;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class LocationHelper {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20;
    private static final long MIN_TIME_BW_UPDATES = DateTimeHelper.getMilisecondsFromMinutes(1); 
    
    public static LocationHelper.Listener getLocationListener(Context pContext)
    {
        Location location = null;
        Listener listener = new Listener();

        LocationManager locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);

         // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
            Log.d("Network", "Network");
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        
        if (isGPSEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
        
        if(location!=null)
           listener.onLocationChanged(location);
        
        listener.setLocationManager(locationManager);
        
        return listener;
    }

    public static void getRequestLocation(Context pContext, LocationHelper.Listener pListener)
    {
        Location location = null;

        LocationManager locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);

         // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, pListener);
            Log.d("Network", "Network");
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        
        if (isGPSEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, pListener);
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
        
        if(location!=null)
            pListener.onLocationChanged(location);
        
        pListener.setLocationManager(locationManager);
    
    }

    public static Intent getIntentGpsSettings()
    {
         Intent intent = new Intent(
                 Settings.ACTION_LOCATION_SOURCE_SETTINGS);
         return intent;
    }

    public static class Listener implements LocationListener
    {
        UserIvoke user;

        Location currentLocation;
        Location oldLocation;
        boolean  isChanged;
        boolean  isEnabled;

        public LocationManager manager;

        @Override
        public void onLocationChanged(Location location) {
            new DebugHelper("LocationHelper.Listener").method("onLocationChanged").par("location", location);

            if(currentLocation!=null)
               oldLocation     = new Location(currentLocation);
            else
               oldLocation     = new Location(location);

            currentLocation = location;

            if(user!=null)
               user.setLocalization(getCurrentLatLng());
        }

        public void listenerForUser(UserIvoke pUser)
        {
            user = pUser;
            if(currentLocation!=null)
               user.setLocalization(getCurrentLatLng());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            isEnabled = true;
        }

        @Override
        public void onProviderDisabled(String provider) {
            isEnabled = false;
        }

        public boolean isChangedLocation()
        {
            return oldLocation != currentLocation;
        }

        public Location getCurrentLocation()
        {
            return currentLocation;
        }

        public LatLng getCurrentLatLng()
        {
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        public Location getOldLocation()
        {
            return oldLocation;
        }

        public void setLocationManager(LocationManager pLocationManager)
        {
            manager = pLocationManager;
        }

        public LocationManager getManager()
        {
            return manager;
        }

    }

}
