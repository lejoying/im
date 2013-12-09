package com.lejoying.mc;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import com.lejoying.utils.RSA;
import com.lejoying.utils.RSAUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WelcomeActivity extends BaseFragmentActivity {

	public static String hexModulus = "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659";

	public static String hexPublicExponent = "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841";

	public static String hexPrivateExponent = "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout._welcome);

		//

		// 2a86f29ae643a0aacd5f909945ffcd6b3c4b0b4c26c67ea6b93d66f13933d929
		// 3061aa780e43ca8dd100374ffe506f2e02f08a25b3324e441e3e73d660e28af5

		// ea8a27d11758a0f429d3dcdf54e1c6c22ab13779

		// 3404067baa441e0a9ee0401acca55ca2ad47f61def36097889e96e980b496a53
		// 077791f16e4b8322ec557cabead4deca78b140529961462aaf6489aa1fcb43bd

		String ming = "ea8a27d11758a0f429d3dcdf54e1c6c22ab13779";
		String mi = "0a6c3de8362299fbf8ac6925a2b0d5a264739fc56ad7957a9502c7ea3f30552b 3b85359ef5611964734aec01e0ee718ab6adcbb661e38bd14019dd41ae3178c5";

		String key = "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841#5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841#3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659";

		// 解密成功

		try {
			String ming1 = RSAUtils.decrypt(
					RSAUtils.getPublicKey(hexModulus, hexPublicExponent), mi);
			System.out.println(ming1);
		} catch (Exception e) {
			System.out.println("解密失败");
			e.printStackTrace();
		}
		try {
			String mi1 = RSAUtils.encrypt(
					RSAUtils.getPrivateKey(hexModulus, hexPrivateExponent),
					ming);
			System.out.println(new String(mi1));
		} catch (Exception e) {
			System.out.println("加密失败");
			e.printStackTrace();
		}

		// Intent intent = new Intent(this, LoginActivity.class);
		// startActivity(intent);

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
