package com.lejoying.mc;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.utils.RSA;
import com.lejoying.utils.RSA.RSAKeyPair;

public class WelcomeActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout._welcome);

		//

		// 2a86f29ae643a0aacd5f909945ffcd6b3c4b0b4c26c67ea6b93d66f13933d929
		// 3061aa780e43ca8dd100374ffe506f2e02f08a25b3324e441e3e73d660e28af5

		String modulus = "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659";

		String publicExponent = "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841";

		String privateExponent = "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1";

		RSA rsa = new RSA();
		
		RSAKeyPair privateKey = rsa.RSAKey(rsa.RSAKeyStr(privateExponent, privateExponent, modulus));

		System.out.println("createË½Ô¿³É¹¦");
		
		// Intent intent = new Intent(this, LoginActivity.class);
		// startActivity(intent);

	}

	public byte[] deoren(int MODE, Key key, byte[] data) throws Exception {
		Cipher ci = Cipher.getInstance("RSA");
		ci.init(MODE, key);
		return ci.doFinal(data);
	}

	public static byte[] decrypt(PrivateKey privateKey, byte[] data)
			throws Exception {

		Cipher ci = Cipher.getInstance("RSA");

		ci.init(Cipher.DECRYPT_MODE, privateKey);

		return ci.doFinal(data);

	}

	public static byte[] encrypt(PublicKey publicKey, byte[] data)
			throws Exception {

		Cipher ci = Cipher.getInstance("RSA");

		ci.init(Cipher.ENCRYPT_MODE, publicKey);

		return ci.doFinal(data);

	}

	public PublicKey getPublicKey(String modulus, String publicExponent)
			throws Exception {

		BigInteger m = new BigInteger(modulus, 16);

		BigInteger e = new BigInteger(publicExponent, 16);

		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		return publicKey;

	}

	public PrivateKey getPrivateKey(String modulus, String privateExponent)
			throws Exception {

		BigInteger m = new BigInteger(modulus, 16);

		BigInteger e = new BigInteger(privateExponent, 16);

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
		hexString = hexString.toUpperCase();
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
	};

	class KeyEntity {
		private String module;
		private String exponent;

		public KeyEntity(String key) {
			int first = key.indexOf("#");
			int second = key.indexOf("#", first + 1);
			this.exponent = key.substring(0, first);
			this.module = key.substring(second + 1);
			System.out.println(exponent);
			System.out.println(module);
		}

		public String getModule() {
			return module;
		}

		public String getExponent() {
			return exponent;
		}

	}

	@Override
	public Fragment setFirstPreview() {
		return null;
	}

	@Override
	protected int setBackground() {
		return R.drawable.app_start;
	}

}
