package com.open.welinks.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.ResponseHandler;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.Base64;

public class MultipartUploader {

	public Data data = Data.getInstance();
	public static String tag = "MultipartUploader";

	public static MultipartUploader instance;

	public static MultipartUploader getInstance() {
		if (instance == null) {
			instance = new MultipartUploader();
		}
		return instance;
	}

	public FileHandler fileHandler;
	public Gson gson = new Gson();

	void initialize() {
		fileHandler = FileHandler.getInstance();
	}

	public void checkFileExists(MyFile myFile) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("fileName", myFile.fileName);

		CheckFileExists checkFileExists = new CheckFileExists();
		checkFileExists.myFile = myFile;
		httpUtils.send(HttpMethod.POST, API.IMAGE_CHECKFILEEXIST, params, checkFileExists);
	}

	public class CheckFileExists extends ResponseHandler<String> {
		public MyFile myFile;

		class Response {
			public String 提示信息;
			public String 失败原因;
			public String fileName;
			public boolean exists;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("查找成功")) {
				myFile.isExists = response.exists;
				fileHandler.onCheckFile(myFile);
			} else {
				myFile.status.state = myFile.status.Failed;
			}
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			myFile.status.state = myFile.status.Exception;
		};
	};
	
	
	public int addExpires = 600;
	public String BUCKETNAME = "wxgs";// welinkstest
	public String OSSACCESSKEYID = "dpZe5yUof6KSJ8RM";
	public String ACCESSKEYSECRET = "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG";
	public String OSS_HOST_URL = "http://images2.we-links.com/";// http://images5.we-links.com/
	
	public void initiateUpLoad(MyFile myFile) {
		long expires = (new Date().getTime() / 1000) + addExpires;
		String postContent = "POST\n\n\n" + expires + "\n/" + BUCKETNAME + "/" + myFile.fileName + "?uploads";
		String signature = "";
		try {
			signature = getHmacSha1Signature(postContent, ACCESSKEYSECRET);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();

		String url = OSS_HOST_URL + myFile.fileName + "?uploads";

		// if (contentType != null) {
		// params.addHeader("Content-Type", contentType);
		// }

		params.addQueryStringParameter("OSSAccessKeyId", OSSACCESSKEYID);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		
		InitUpload initUpload = new InitUpload();
		initUpload.myFile = myFile;
		httpUtils.send(HttpMethod.POST, url, params, initUpload);
	}

	public class InitUpload extends ResponseHandler<String>{
		MyFile myFile;
		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				  parseXml(responseInfo.result, myFile);
				  fileHandler.onInitiateUpLoad(myFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			myFile.status.state = myFile.status.Exception;
		};
	};
	

	public void  parseXml(String resultXml, MyFile myFile) throws Exception {
		InputStream is = new ByteArrayInputStream(resultXml.getBytes("UTF-8"));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		Element rootElement = doc.getDocumentElement();
		Node item = rootElement;
		NodeList properties = item.getChildNodes();
		for (int j = 0; j < properties.getLength(); j++) {
			Node property = properties.item(j);
			String nodeName = property.getNodeName();
			if (nodeName.equals("Bucket")) {
				myFile.bucket = property.getFirstChild().getNodeValue();
			} else if (nodeName.equals("Key")) {
				myFile.key = property.getFirstChild().getNodeValue();
			} else if (nodeName.equals("UploadId")) {
				myFile.uploadId = property.getFirstChild().getNodeValue();
			}
		}
	}
	public static String getHmacSha1Signature(String value, String key) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] keyBytes = key.getBytes();
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);

		byte[] rawHmac = mac.doFinal(value.getBytes());
		return new String(Base64.encode(rawHmac));
	}

}
