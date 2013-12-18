package com.lejoying.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

public class LocationTools {

	public static double[] getLocation(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String providerName = lm.getBestProvider(criteria, true);

		if (providerName != null) {
			Location location = lm.getLastKnownLocation(providerName);
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			return new double[] { longitude, latitude };
		} else {
			Toast.makeText(context, "can't find location",
					Toast.LENGTH_SHORT).show();
		}

		return null;
	}
}
