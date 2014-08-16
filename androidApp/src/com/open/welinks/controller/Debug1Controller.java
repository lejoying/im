package com.open.welinks.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.entity.ByteArrayEntity;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.aliyun.android.oss.Helper;
import com.aliyun.android.oss.OSSHttpTool;
import com.aliyun.android.oss.task.PutObjectTask;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.entity.InputStreamUploadEntity;
import com.open.lib.TestHttp;
import com.open.welinks.R;
import com.open.welinks.controller.UploadMultipart.UploadLoadingListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.Debug1View;
import com.open.welinks.view.Debug1View.ControlProgress;
import com.open.welinks.view.Debug1View.Status;
import com.open.welinks.view.Debug1View.TransportingList;
import com.open.welinks.view.Debug1View.TransportingList.TransportingItem;

public class Debug1Controller {
	public Data data = Data.getInstance();
	public static ResponseHandlers responseHandlers = ResponseHandlers
			.getInstance();
	public static String tag = "Debug1Controller";

	public Runnable animationRunnable;

	public Context context;
	public Debug1View thisView;
	public Debug1Controller thisController;
	public Activity thisActivity;

	public UploadMultipartList uploadMultipartList = UploadMultipartList
			.getInstance();

	public UploadLoadingListener uploadLoadingListener;

	public OnLongClickListener onLongClickListener;

	public ArrayList<HashMap<String, String>> imagesSource = new ArrayList<HashMap<String, String>>();

	public Debug1Controller(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initializeListeners() {
		uploadLoadingListener = new UploadLoadingListener() {

			@Override
			public void loading(UploadMultipart instance, int precent,
					int status) {
				instance.controlProgress.moveTo(precent);
			}
		};
		onLongClickListener = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String path = (String) v.getTag();
				uploadMultipartList.cancleMultipart(path);
				return false;
			}
		};
	}

	public void bindEvent() {
		ArrayList<TransportingItem> transportingList = thisView.transportingList.transportingItems;
		for (int i = 0; i < transportingList.size(); i++) {
			transportingList.get(i).transportingItemView
					.setOnLongClickListener(onLongClickListener);
			transportingList.get(i).uploadMultipart
					.setUploadLoadingListener(uploadLoadingListener);
		}
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;

		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> imagesSource = (ArrayList<HashMap<String, String>>) thisActivity
				.getIntent().getSerializableExtra("images");
		if (imagesSource != null) {
			this.imagesSource = imagesSource;
		}
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

			ControlProgress controlProgress = thisView.transportingList.transportingItems
					.get(index).controlProgress;
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
			// uploadImage();
			// downloadImage();
			checkImage();
		}

		if (item.getItemId() == R.id.debug2_6) {
			Log.d(tag, "Debug1Activity debug2_6");
			// uploadImage();
			// downloadImage();
			// uploadImageHeadAnth();
			OSSPutObject();
		}

		return true;
	}

	public UploadMultipart uploadFile(String path, View transportingItemView) {
		UploadMultipart uploadMultipart = new UploadMultipart(path);
		uploadMultipartList.addMultipart(uploadMultipart);
		// uploadMultipart.setUploadLoadingListener(uploadLoadingListener);
		return uploadMultipart;
	}

	/******************** upload image **************************/
	public static void uploadImageWithInputStreamUploadEntity(String signature,
			String fileName, long expires, String OSSAccessKeyId) {
		// SHA1 sha1 = new SHA1();
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		String OSS_END_POINT = "http://images7.we-links.com";

		String OSS_HOST = "oss-cn-hangzhou.aliyuncs.com";

		String contentType = "image/jpg";

		String FolderNAME = "test";
		String ACCESSKEYID = "dpZe5yUof6KSJ8RM";

		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "test1/test003.jpg");// "test1/test002.jpg"

		long fileLength = file.length();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String resource = "/" + FolderNAME + "/" + fileName;
		String requestUri = OSS_END_POINT + resource;

		params.addHeader("Date", dateStr);
		params.addHeader("Host", OSS_HOST);
		params.addHeader("Content-Type", contentType);
		params.addQueryStringParameter("OSSAccessKeyId", OSSAccessKeyId);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);

		if (fileLength > 0) {
			params.setBodyEntity(new InputStreamUploadEntity(inputStream,
					fileLength));
		}
		Log.d(tag, "uploadImageWithInputStreamUploadEntity");
		http.send(HttpRequest.HttpMethod.PUT, requestUri, params,
				responseHandlers.upload);
	}

	public void uploadImageHeadAnth() {
		SHA1 sha1 = new SHA1();
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		String OSS_END_POINT = "http://images7.we-links.com";

		String OSS_HOST = "oss-cn-Hangzhou.aliyuncs.com";

		String contentType = "image/jpg";

		String endFileName = "jpg";

		String BACKETNAME = "test";
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
		String dateStr = Helper.getGMTDate();
		String content = "PUT\n\nimage/jpg\n" + dateStr + "\n/wxgs-data/test/"
				+ sha1FileName + ".jpg";
		String requestUri = OSS_END_POINT + "/test/" + sha1FileName + ".jpg";

		String signature = "";
		try {
			signature = Helper.getHmacSha1Signature(content, ACCESSKEYSECRET);
		} catch (InvalidKeyException e) {
		} catch (NoSuchAlgorithmException e) {
		}

		params.addHeader("Authorization", "OSS dpZe5yUof6KSJ8RM:" + signature);
		params.addHeader("Date", dateStr);
		params.addHeader("Host", OSS_HOST);
		params.addHeader("Content-Type", contentType);
		// params.addHeader("Content-Length", "" + bytes.length);

		if ((bytes != null) && (bytes.length > 0)) {
			params.setBodyEntity(new ByteArrayEntity(bytes));
		}

		http.send(HttpRequest.HttpMethod.PUT, requestUri, params,
				responseHandlers.upload);
	}

	public void OSSPutObject() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				SHA1 sha1 = new SHA1();

				File sdFile = Environment.getExternalStorageDirectory();
				File file = new File(sdFile, "qwe.jpg");

				String sha1FileName = "default.jpg";
				byte[] bytes = null;
				try {
					bytes = StreamParser.parseToByteArray(new FileInputStream(
							file));
					sha1FileName = sha1.getDigestOfString(bytes);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				PutObjectTask putObjectTask = new PutObjectTask("wxgs",
						"images/" + sha1FileName + ".jpg", "image/jpg");
				putObjectTask.initKey("dpZe5yUof6KSJ8RM",
						"UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG");
				putObjectTask.setData(bytes);
				String result = putObjectTask.getResult();
				System.out.println(result + "---");

				// Log.e(tag, "oss putobject reply:---" + result);
			}
		}).start();
	}

	public static void uploadImageWithByteArrayEntity(String signature,
			String fileName, long expires, String OSSAccessKeyId) {
		// SHA1 sha1 = new SHA1();
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		String OSS_END_POINT = "http://images7.we-links.com";

		String OSS_HOST = "oss-cn-hangzhou.aliyuncs.com";

		String contentType = "image/jpg";

		String FolderNAME = "test";
		String ACCESSKEYID = "dpZe5yUof6KSJ8RM";

		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "test1/test002.jpg");// "test1/test002.jpg"

		byte[] bytes = null;
		try {
			bytes = StreamParser.parseToByteArray(new FileInputStream(file));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//
		String resource = "/" + FolderNAME + "/" + fileName;
		String requestUri = OSS_END_POINT + resource;
		//
		// String dateStr = DateUtil.getGMTDate();
		// String authorization = "";
		//
		// String signatureContent = "PUT" + "\n" + "" + "\n" + contentType +
		// "\n"
		// + dateStr + "\n" + "" + resource;
		Log.e("data", dateStr + "-----date");
		// params.addHeader("Authorization", "OSS " + ACCESSKEYID + ":" +
		// signature);
		params.addHeader("Date", dateStr);
		params.addHeader("Host", OSS_HOST);
		params.addHeader("Content-Type", contentType);
		params.addQueryStringParameter("OSSAccessKeyId", OSSAccessKeyId);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		// params.addHeader("Content-Length", "" + bytes.length);

		if ((bytes != null) && (bytes.length > 0)) {
			params.setBodyEntity(new ByteArrayEntity(bytes));
		}

		http.send(HttpRequest.HttpMethod.PUT, requestUri, params,
				responseHandlers.upload);
	}

	static String dateStr;

	public void checkImage() {
		SHA1 sha1 = new SHA1();
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "test1/test002.jpg");// "test1/test002.jpg"

		byte[] bytes = null;
		String sha1FileName = "";
		try {
			bytes = StreamParser.parseToByteArray(new FileInputStream(file));
			sha1FileName = sha1.getDigestOfString(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// String contentType = "image/jpg";

		String endFileName = "jpg";
		// String BACKETNAME = "welinkstest";
		// String resource = "/" + BACKETNAME + "/" + sha1FileName + "." +
		// endFileName;

		dateStr = DateUtil.getGMTDate();
		// String signatureContent = "PUT" + "\n" + "" + "\n" + contentType +
		// "\n" + dateStr + "\n" + "" + resource;

		RequestParams params = new RequestParams();
		params.addBodyParameter("accessKey", "lejoying");
		params.addBodyParameter("phone", "15120088197");
		params.addBodyParameter("filename", sha1FileName + "." + endFileName);
		// params.addBodyParameter("signaturecontent", signatureContent);

		HttpUtils http = new HttpUtils();
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		String url2 = "http://192.168.1.91/image/checkfile";
		http.send(HttpRequest.HttpMethod.POST, url2, params,
				responseHandlers.checkFile);
	}

	public void downloadImage() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		String requestUri = "http://images5.we-links.com/63727A37A9CF78022B408316DA17EAC6D5B519B9.jpg";
		http.send(HttpRequest.HttpMethod.GET, requestUri, params,
				responseHandlers.upload);

	}

}
