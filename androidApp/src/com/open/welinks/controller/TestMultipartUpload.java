package com.open.welinks.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.entity.ByteArrayEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;

public class TestMultipartUpload extends Activity {

	public static String tag = "TestMultipartUpload";

	public static HttpClient httpClient = HttpClient.getInstance();

	public Gson gson = new Gson();

	InitiateMultipartUploadResult initiateMultipartUploadResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkImage();
		Log.e(tag, "onCreate-----");
	}

	public void checkImage() {
		SHA1 sha1 = new SHA1();
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "ss.jpg");// "test1/test002.jpg"

		byte[] bytes = null;
		String sha1FileName = "";
		try {
			bytes = StreamParser.parseToByteArray(new FileInputStream(file));
			sha1FileName = sha1.getDigestOfString(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String endFileName = "jpg";

		RequestParams params = new RequestParams();
		params.addBodyParameter("accessKey", "lejoying");
		params.addBodyParameter("phone", "151");
		params.addBodyParameter("filename", sha1FileName + "." + endFileName);

		HttpUtils http = new HttpUtils();

		String url2 = "http://192.168.1.91/image/checkfile2";
		http.send(HttpRequest.HttpMethod.POST, url2, params, checkFile);
	}

	public void initiateMultipartupload(String OSSAccessKeyId, long expires,
			String signature, String fileName) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();

		String url = "http://images5.we-links.com/" + fileName + "?uploads";

		params.addQueryStringParameter("OSSAccessKeyId", OSSAccessKeyId);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		Log.e(fileName, OSSAccessKeyId + "---" + expires + "---" + signature
				+ "---" + fileName);
		httpUtils.send(HttpMethod.POST, url, params, initUpload);
	}

	public ResponseHandler checkFile = httpClient.new ResponseHandler() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String filename;
			public boolean exists;
			public String signature;
			public long expires;
			public String OSSAccessKeyId;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result,
					Response.class);
			Log.e(tag, responseInfo.result + "-------checkFile success");
			if (response.提示信息.equals("查找成功")) {
				if (response.exists) {

				} else if (!response.exists) {
					initiateMultipartupload(response.OSSAccessKeyId,
							response.expires, response.signature,
							response.filename);
				}
			}
		};
	};
	public ResponseHandler initUpload = httpClient.new ResponseHandler() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.statusCode + "-------initUpload success");
			try {
				initiateMultipartUploadResult = parseXml(responseInfo.result);
				Log.e(tag, initiateMultipartUploadResult.bucket + "---"
						+ initiateMultipartUploadResult.key + "---"
						+ initiateMultipartUploadResult.uploadId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};
	public static ResponseHandler inittingUpload = httpClient.new ResponseHandler() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.result + "-------inittingUpload success");
		};
	};

	public void uploadFile() {
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "ss.jpg");// "test1/test002.jpg"

		byte[] bytes = null;
		try {
			bytes = StreamParser.parseToByteArray(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int partCount = bytes.length / 128000;

		for (int i = 0; i < partCount; i++) {

		}
	}

	public static void uploadImage(String signature, String fileName,
			long expires, String OSSAccessKeyId) {
		// SHA1 sha1 = new SHA1();
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		String OSS_END_POINT = "http://images5.we-links.com";

		String OSS_HOST = "oss-cn-beijing.aliyuncs.com";

		// String contentType = "image/jpg";

		String FolderNAME = "test";
		// String ACCESSKEYID = "dpZe5yUof6KSJ8RM";

		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "ss.jpg");// "test1/test002.jpg"

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
		// Log.e("data", dateStr + "-----date");
		// params.addHeader("Authorization", "OSS " + ACCESSKEYID + ":" +
		// signature);
		// params.addHeader("Date", dateStr);
		params.addHeader("Host", OSS_HOST);
		// params.addHeader("Content-Type", contentType);
		params.addQueryStringParameter("OSSAccessKeyId", OSSAccessKeyId);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		// params.addHeader("Content-Length", "" + bytes.length);

		if ((bytes != null) && (bytes.length > 0)) {
			params.setBodyEntity(new ByteArrayEntity(bytes));
		}

		http.send(HttpRequest.HttpMethod.PUT, requestUri, params,
				inittingUpload);
	}

	class InitiateMultipartUploadResult {
		public String bucket;
		public String key;
		String uploadId;
	}

	public InitiateMultipartUploadResult parseXml(String resultXml)
			throws Exception {
		InputStream is = new ByteArrayInputStream(resultXml.getBytes("UTF-8"));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		Element rootElement = doc.getDocumentElement();
		InitiateMultipartUploadResult initiateMultipartUploadResult = new InitiateMultipartUploadResult();
		Node item = rootElement;
		NodeList properties = item.getChildNodes();
		for (int j = 0; j < properties.getLength(); j++) {
			Node property = properties.item(j);
			String nodeName = property.getNodeName();
			if (nodeName.equals("Bucket")) {
				initiateMultipartUploadResult.bucket = property.getFirstChild()
						.getNodeValue();
			} else if (nodeName.equals("Key")) {
				initiateMultipartUploadResult.key = property.getFirstChild()
						.getNodeValue();
			} else if (nodeName.equals("UploadId")) {
				initiateMultipartUploadResult.uploadId = property
						.getFirstChild().getNodeValue();
			}
		}
		return initiateMultipartUploadResult;
	}
}
