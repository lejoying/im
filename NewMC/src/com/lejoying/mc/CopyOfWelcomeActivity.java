package com.lejoying.mc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.entity.User;

public class CopyOfWelcomeActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout._welcome);
		// Intent service = new Intent(this, MainService.class);
		// startService(service);

		// var pbkeyStr0 = RSA
		// .RSAKeyStr(
		// "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841",
		// "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841",
		// "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659");
		// var pbkey0 = RSA.RSAKey(pbkeyStr0);
		//
		// var pvkeyStr0 = RSA
		// .RSAKeyStr(
		// "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
		// "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
		// "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659");
		// var pvkey0 = RSA.RSAKey(pvkeyStr0);

		InputStream is = null;
		ObjectInputStream ois = null;
		User user = null;

		try {
			is = this.openFileInput("123");
			ois = new ObjectInputStream(is);
			user = (User) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// System.out.println(user.getAccessKey());

		// accessKey:---8a0154431e54ef33f36f1ea778b9a5896ff5f4fc

		// 03ba1e543d94fa5d3ea3b5ba2163d7019a5ae5556018b29f72ae25dbc7932285
		// 011b37b8758ece8ca082094587794d121ae232f48fce58b3fc89b71c460233b9

		String pvKey = "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1"
				+ "#10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1"
				+ "#3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659";
		String pbKey = "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841"
				+ "#5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841"
				+ "#3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659";

		String modulus = "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659";

		String publicExponent = "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841";

		String privateExponent = "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1";

		String mi = "03ba1e543d94fa5d3ea3b5ba2163d7019a5ae5556018b29f72ae25dbc7932285 "
				+ "011b37b8758ece8ca082094587794d121ae232f48fce58b3fc89b71c460233b9";

		String[] mis = mi.split(" ");
		try {
			Cipher cipher = Cipher.getInstance("RSA");// Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE,
					getPublicKey(modulus, publicExponent));
			for (String m : mis) {
				byte[] deBytes = cipher.doFinal(m.getBytes());
				System.out.println(new String(deBytes, "UTF-8"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// User user = MCDataTools.getLoginedUser(this);
		// if (user != null && user.getPhone() != null
		// && user.getAccessKey() != null) {
		// Bundle params = new Bundle();
		// params.putString("phone", user.getPhone());
		// params.putString("accessKey", user.getAccessKey());
		// params.putString("target", user.getPhone());
		// startNetwork(API.ACCOUNT_GET, params, false,
		// new NetworkStatusListener() {
		// @Override
		// public void onReceive(int STATUS, String log) {
		// switch (STATUS) {
		// case NetworkService.STATUS_SUCCESS:
		// startToMain();
		// break;
		// case NetworkService.STATUS_UNSUCCESS:
		//
		// break;
		// case NetworkService.STATUS_NOINTERNET:
		//
		// break;
		// case NetworkService.STATUS_FAILED:
		//
		// break;
		//
		// default:
		// break;
		// }
		// }
		// });
		// } else {
		// startToLogin();
		// }
		//
		// startToLogin();
	}

	private void startToLogin() {
		Intent intent = new Intent(CopyOfWelcomeActivity.this, LoginActivity.class);
		CopyOfWelcomeActivity.this.startActivity(intent);
		CopyOfWelcomeActivity.this.finish();
	}

	private void startToMain() {
		Intent intent = new Intent(CopyOfWelcomeActivity.this, MainActivity.class);
		CopyOfWelcomeActivity.this.startActivity(intent);
		CopyOfWelcomeActivity.this.finish();
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
