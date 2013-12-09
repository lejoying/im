package com.lejoying.utils;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Locale;

import javax.crypto.Cipher;

import com.lejoying.mc.WelcomeActivity;

public class RSAUtils {

	public static String decrypt(Key rsaKey, String data) throws Exception {
		Cipher ci = Cipher.getInstance("RSA");
		ci.init(Cipher.DECRYPT_MODE, rsaKey);
		String datas[] = data.split(" ");
		String ming = "";
		for (String d : datas) {
			ming += reversal(new String(ci.doFinal(hexStringToBytes(d))));
		}
		return ming;

	}

	public static String encrypt(Key rsaKey, String data) throws Exception {
		Cipher ci = Cipher.getInstance("RSA");
		ci.init(Cipher.ENCRYPT_MODE, rsaKey);
		RSAPrivateKey r = (RSAPrivateKey) rsaKey;
		String hexModulus = bytesToHexString(r.getModulus().toByteArray());
		int chunkSize = (hexModulus.length() % 4 == 0 ? hexModulus.length() / 4 - 1
				: hexModulus.length() / 4) * 2;
		int count = data.length() % chunkSize == 0 ? data.length() / chunkSize
				: data.length() / chunkSize + 1;
		String mi = "";
		for (int i = 0; i < count; i++) {
			mi += bytesToHexString(ci.doFinal(reversal(
					data.substring(i * chunkSize, (i + 1) * chunkSize > data
							.length() ? data.length() : chunkSize * (i + 1)))
					.getBytes()));
			if (i != count - 1) {
				mi += " ";
			}
		}
		return mi;

	}

	public static PublicKey getPublicKey(String hexModulus,
			String hexPublicExponent) throws Exception {

		BigInteger m = new BigInteger(hexModulus, 16);

		BigInteger e = new BigInteger(hexPublicExponent, 16);

		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		return publicKey;

	}

	public static PrivateKey getPrivateKey(String hexModulus,
			String hexPrivateExponent) throws Exception {

		BigInteger m = new BigInteger(hexModulus, 16);

		BigInteger e = new BigInteger(hexPrivateExponent, 16);

		RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		return privateKey;

	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase(Locale.getDefault());
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	private static String reversal(String s) {
		String result = "";
		char[] cs = s.toCharArray();
		for (int i = cs.length - 1; i > -1; i--) {
			result += String.valueOf(cs[i]);
		}
		return result;
	}

}

class KeyEntity {
	private String module;
	private String exponent;

	public KeyEntity(String key) {
		int first = key.indexOf("#");
		int second = key.indexOf("#", first + 1);
		this.exponent = key.substring(0, first);
		this.module = key.substring(second + 1);
	}

	public String getModule() {
		return module;
	}

	public String getExponent() {
		return exponent;
	}

}
