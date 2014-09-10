package com.open.welinks.utils;

public class DistanceUtils {

	public static String getDistance(int distance) {
		if (distance < 1000) {
			return distance + "m";
		} else {
			return Math.round(distance / 1000 / 1.0) + "km";
		}
	}
}
