package com.example.devicesignaturegenerator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

class MyLocationListener implements LocationListener {
	Context mycontext = null;

	public MyLocationListener(Context context) {
		// TODO Auto-generated constructor stub
		mycontext = context;
	}

	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		Log.d("Location changed",
				loc.getLatitude() + ", " + loc.getLongitude());
		String longitude = "Longitude: " + loc.getLongitude();
        Log.v("long=", longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v("lat=", latitude);
		String cityName = null;
		Geocoder gcd = new Geocoder(mycontext, Locale.getDefault());
		List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                loc.getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            cityName = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
            + cityName;
		Log.d("Location=", s);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}