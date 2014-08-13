package com.aliyun.android.oss;

import android.annotation.SuppressLint;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Helper {
	public static String getGMTDate() {
		return getGMTDate(new Date());
	}

	public static String getGMTDate(Date date) {
		if (date == null) {
			return null;
		}
		DateFormat dateFormat = new SimpleDateFormat(
				"E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	public static Date getGMTDateFromString(String dateStr)
			throws ParseException {
		return new Date();
	}

	public static String getHmacSha1Signature(String value, String key)
			throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] keyBytes = key.getBytes();
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);

		byte[] rawHmac = mac.doFinal(value.getBytes());
		return new String(Base64.encode(rawHmac));
	}

	public static boolean isEmptyString(String str) {
		return (str == null) || (str.equals(""));
	}

	public static String inputStream2String(InputStream inputStream)
			throws Exception {
		if (inputStream == null) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = inputStream.read(buffer)) >= 0) {
			outputStream.write(buffer, 0, count);
		}
		String convertedBuffer = new String(outputStream.toByteArray());
		outputStream.close();
		return convertedBuffer;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(String dateString)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.S'Z'");
		return sdf.parse(dateString);
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(String format, String dateString)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(dateString);
	}

	public static String formatPath(String path) {
		if (isEmptyString(path)) {
			return "";
		}
		return path + "/";
	}
}