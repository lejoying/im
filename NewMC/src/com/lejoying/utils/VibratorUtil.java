package com.lejoying.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class VibratorUtil {

	public static final long[] VIBRATE_COMMON = new long[] { 0, 120, 70, 120 };

	public static void Vibrate(Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void Vibrate(Context context, long[] pattern, boolean isRepeat) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

	public static void CommonVibrate(Context context) {
		Vibrate(context, VIBRATE_COMMON, false);
	}
}
