package com.lejoying.utils;

import android.util.Base64;

public class BASE64 {

	public static String decode(String baseStr) {
		byte[] b = Base64.decode(baseStr.getBytes(), Base64.DEFAULT);
		return new String(b);
	}

	public static String encode(String str) {
		return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
	}
}
