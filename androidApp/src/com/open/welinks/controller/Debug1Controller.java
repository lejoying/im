package com.open.welinks.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.entity.ByteArrayEntity;

import com.aliyun.android.oss.Base64;
import com.aliyun.android.oss.Helper;
import com.aliyun.android.oss.OSSHttpTool;
import com.aliyun.android.oss.ObjectMetaData;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.Debug1View;
import com.open.welinks.view.Debug1View.ControlProgress;
import com.open.welinks.view.Debug1View.Status;
import com.open.lib.TestHttp;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

public class Debug1Controller {
	public Data data = Data.getInstance();
	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	public String tag = "Debug1Controller";

	public Runnable animationRunnable;

	public Context context;
	public Debug1View thisView;
	public Debug1Controller thisController;
	public Activity thisActivity;

	public ArrayList<HashMap<String, String>> imagesSource = new ArrayList<HashMap<String, String>>();;

	public Debug1Controller(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initializeListeners() {

	}

	public void bindEvent() {
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;

		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> imagesSource = (ArrayList<HashMap<String, String>>) thisActivity.getIntent().getSerializableExtra("images");
		if (imagesSource != null) {
			this.imagesSource = imagesSource;
		}
	}

	public void testPutObject() {
		System.out.println("start config params");
		RequestParams params = new RequestParams();
		params.addHeader("Content-Type", "image/png");

		params.addHeader("Host", "images5.we-links.com");// welinkstest.oss-cn-beijing.aliyuncs.com
		params.addHeader("Date", getGMTDate());

		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "test0.png");

		String md5 = null;
		try {
			byte[] bytes = StreamParser.parseToByteArray(new FileInputStream(file));

			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes, 0, bytes.length);
			BigInteger bigInt = new BigInteger(1, digest.digest());
			md5 = bigInt.toString(16).toLowerCase(Locale.getDefault());

			params.addHeader("Content-Length", bytes.length + "");
			params.addHeader("Content-Md5", md5);

			params.setBodyEntity(new ByteArrayEntity(bytes));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		String value = "PUT" + "\n" + md5 + "\n" + "image/png" + "\n" + getGMTDate() + "\n" + "";
		try {
			params.addHeader("Authorization", "OSS " + "dpZe5yUof6KSJ8RM" + ":" + getHmacSha1Signature(value, "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG"));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		HttpUtils http = new HttpUtils();
		http.configSoTimeout(50000);

		String url2 = "http://images5.we-links.com/test0.png";
		try {
			System.out.println("start http send" + getHmacSha1Signature(value, "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG"));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		http.send(HttpRequest.HttpMethod.PUT, url2, params, responseHandlers.upload);
	}

	public void checkImage(String fileName, String value) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("accessKey", "lejoying");
		params.addBodyParameter("phone", "15120088197");
		params.addBodyParameter("filename", fileName);
		params.addBodyParameter("notencryptedcontent", value);

		HttpUtils http = new HttpUtils();
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		String url2 = "http://192.168.1.91/image/checkfile";
		http.send(HttpRequest.HttpMethod.POST, url2, params, responseHandlers.checkFile);
	}

	public static String getHmacSha1Signature(String value, String key) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] keyBytes = key.getBytes();
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);

		byte[] rawHmac = mac.doFinal(value.getBytes());
		return new String(Base64.encode(rawHmac));
	}

	public static String getGMTDate() {
		return getGMTDate(new Date());
	}

	public static String getGMTDate(Date date) {
		if (date == null) {
			return null;
		}
		DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			thisActivity.finish();
		}
		boolean flag = false;
		return flag;
	}

	int targetPercentage = 30;
	int index = 0;
	TestHttp testHttp = new TestHttp();

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.debug2_1) {
			Log.d(tag, "Debug1Activity debug2_1");
			thisView.titleControlProgress.moveTo(targetPercentage);
			targetPercentage = (targetPercentage + 30) % 100;
		}
		if (item.getItemId() == R.id.debug2_2) {
			Log.d(tag, "Debug1Activity debug2_2");

			ControlProgress controlProgress = thisView.transportingList.transportingItems.get(index).controlProgress;
			if (controlProgress.percentage > 50) {
				controlProgress.moveTo(0);
			} else {
				controlProgress.moveTo(100);
			}
			index = (index + 1) % 10;
		}

		if (item.getItemId() == R.id.debug2_3) {
			Log.d(tag, "Debug1Activity debug2_3");
			thisView.titleControlProgress.setTo(targetPercentage);
			targetPercentage = (targetPercentage + 30) % 100;

			for (int i = 0; i < 10; i++) {
				testHttp.test1();
			}

		}

		if (item.getItemId() == R.id.debug2_5) {
			Log.d(tag, "Debug1Activity debug2_5");
//			uploadImage();
			downloadImage();
		}

		return true;
	}

	/******************** upload image **************************/
	public void uploadImage() {
		SHA1 sha1 = new SHA1();
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		String OSS_END_POINT = "http://images5.we-links.com";

		String OSS_HOST = "oss-cn-beijing.aliyuncs.com";

		String contentType = "image/jpg";

		String endFileName = "jpg";

		String BACKETNAME = "welinkstest";
		String ACCESSKEYID = "dpZe5yUof6KSJ8RM";
		String ACCESSKEYSECRET = "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG";

		OSSHttpTool httpTool = new OSSHttpTool();

		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "test1/test002.jpg");

		String sha1FileName = "default";
		byte[] bytes = null;
		try {
			bytes = StreamParser.parseToByteArray(new FileInputStream(file));
			sha1FileName = sha1.getDigestOfString(bytes);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String resource = httpTool.generateCanonicalizedResource("/" + BACKETNAME + "/" + sha1FileName + "." + endFileName);
		String requestUri = OSS_END_POINT + resource;

		String dateStr = Helper.getGMTDate();
		String authorization = OSSHttpTool.generateAuthorization(ACCESSKEYID, ACCESSKEYSECRET, "PUT", "", contentType, dateStr, "", resource);

		params.addHeader("Authorization", authorization);
		params.addHeader("Date", dateStr);
		params.addHeader("Host", OSS_HOST);
		params.addHeader("Content-Type", contentType);
		// params.addHeader("Content-Length", "" + bytes.length);

		if ((bytes != null) && (bytes.length > 0)) {
			params.setBodyEntity(new ByteArrayEntity(bytes));
		}

		http.send(HttpRequest.HttpMethod.PUT, requestUri, params, responseHandlers.upload);
	}

	public void downloadImage() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		String requestUri = "http://images5.we-links.com/63727A37A9CF78022B408316DA17EAC6D5B519B9.jpg";
		http.send(HttpRequest.HttpMethod.GET, requestUri, params, responseHandlers.upload);

	}

}
