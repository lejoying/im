package com.open.lib;

import android.util.Log;

public class MyLog {

	public static boolean isGlobalTurnOn = true;

	public boolean isTurnOn = true;
	public String tag = null;

	public MyLog(String tag, boolean isTurnOn) {
		this.tag = tag;
		this.isTurnOn = isTurnOn;
	}

	public void v(String message) {
		this.v(this.tag, message);
	}

	public void d(String message) {
		this.d(this.tag, message);
	}

	public void i(String message) {
		this.i(this.tag, message);
	}

	public void w(String message) {
		this.w(this.tag, message);
	}

	public void e(String message) {
		this.e(this.tag, message);
	}

	public void v(String tag, String message) {
		if (isTurnOn && isGlobalTurnOn) {
			Log.v(tag, message);
		}
	}

	public void d(String tag, String message) {
		if (isTurnOn && isGlobalTurnOn) {
			Log.d(tag, message);
		}
	}

	public void i(String tag, String message) {
		if (isTurnOn && isGlobalTurnOn) {
			Log.i(tag, message);
		}
	}

	public void w(String tag, String message) {
		if (isTurnOn && isGlobalTurnOn) {
			Log.w(tag, message);
		}
	}

	public void e(String tag, String message) {
		if (isTurnOn && isGlobalTurnOn) {
			Log.e(tag, message);
		}
	}

}
