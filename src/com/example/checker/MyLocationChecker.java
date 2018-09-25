package com.example.checker;

import android.location.Location;

public class MyLocationChecker {
	public boolean checkLocationWithinRadius(Location deviceLocation,
			Location accessPointLocation) {

		float[] dist = new float[1];
		if (deviceLocation != null && accessPointLocation != null) {
			Location.distanceBetween(deviceLocation.getLatitude(),
					deviceLocation.getLongitude(),
					accessPointLocation.getLatitude(),
					accessPointLocation.getLongitude(), dist);
			if (dist[0] / 500 > 1) {
				// If device Location is outside 500m radius area
				return false;
			} else
				return true;
		}
		return false;
	}
}
