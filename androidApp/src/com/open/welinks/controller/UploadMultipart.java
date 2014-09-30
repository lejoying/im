package com.open.welinks.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.entity.ByteArrayEntity;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.view.View;

import com.aliyun.android.oss.Base64;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.http.client.entity.InputStreamUploadEntity;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.lib.MyLog;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.Debug1View.TransportingList.TransportingItem;

public class UploadMultipart {

	public Data data = Data.getInstance();

	public static String tag = "UploadMultipart";

	public MyLog log = new MyLog(tag, true);

	public static HttpClient httpClient = HttpClient.getInstance();

	public Gson gson = new Gson();

	public String BUCKETNAME = "wxgs";// welinkstest
	public String OSSACCESSKEYID = "dpZe5yUof6KSJ8RM";
	public String ACCESSKEYSECRET = "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG";

	public String OSS_HOST_URL = "http://images2.we-links.com/";// http://images5.we-links.com/
	public String OSS_DIRECTORY = "temp/";// multipart

	public OnUploadLoadingListener uploadLoadingListener;

	public UploadMultipart instance;

	public View view;

	public String path; // example:"/sdcard/test/test001.jpg"
	public String contentType = null;// example: "image/jpg" "image/png"

	public static int UPLOAD_TYPE_IMAGE = 0x01;
	public static int UPLOAD_TYPE_VOICE = 0x02;
	public static int UPLOAD_TYPE_HEAD = 0x03;
	public static int UPLOAD_TYPE_BACKGROUND = 0x04;

	public static int UPLOAD_DEFAULT = 0x00;
	public static int UPLOAD_EMPTY = 0x01;
	public static int UPLOAD_INIT = 0x02;
	public static int UPLOAD_INITSUCCESS = 0x03;
	public static int UPLOAD_INITFAILED = 0x04;
	public static int UPLOAD_LOADING = 0x05;
	public static int UPLOAD_SUCCESS = 0x06;
	public static int UPLOAD_FAILED = 0x07;
	public static int UPLOAD_CANCLE = 0x08;

	public static int isUploadStatus = UPLOAD_DEFAULT;

	public InitiateMultipartUploadResult initiateMultipartUploadResult;
	public List<Part> parts = new ArrayList<Part>();
	public byte[] bytes;
	public String fileName = "";

	public int addExpires = 600;
	public int partSize = 256000;
	public int partCount = 0;
	public int partSuccessCount = 0;

	public CompleteMultipartUploadResult completeMultipartUploadResult;

	public HttpHandler<String> httpHandler;

	public TransportingItem transportingItem;

	public int currentUploadType = 0;

	public UploadMultipart(String path, int type) {
		this.currentUploadType = type;
		checkOSSFile();
		this.path = path;
	}

	public UploadMultipart(String path, String contentType, int type) {
		this.currentUploadType = type;
		checkOSSFile();
		this.path = path;
		this.contentType = contentType;
	}

	public UploadMultipart(String path, String fileName, byte[] bytes, int type) {
		this.currentUploadType = type;
		checkOSSFile();
		this.path = path;
		this.fileName = OSS_DIRECTORY + fileName;
		this.bytes = bytes;
	}

	public void checkOSSFile() {
		if (currentUploadType == UPLOAD_TYPE_BACKGROUND) {
			OSS_DIRECTORY = "backgrounds/";
		} else if (currentUploadType == UPLOAD_TYPE_HEAD) {
			OSS_DIRECTORY = "heads/";
		} else if (currentUploadType == UPLOAD_TYPE_IMAGE) {
			OSS_DIRECTORY = "images/";
		} else if (currentUploadType == UPLOAD_TYPE_VOICE) {
			OSS_DIRECTORY = "voices/";
		} else if (currentUploadType == 0) {
			OSS_DIRECTORY = "temp/";
		}
	}

	public void startUpload() {
		instance = this;
		if (!"".equals(fileName)) {
			checkFileExists();
		} else {
			initiateMultipartUploadParams();
		}
	}

	public void initiateMultipartUploadParams() {
		SHA1 sha1 = new SHA1();
		String sha1FileName = "";
		String suffixName = path.substring(path.lastIndexOf("."));
		// suffixName = suffixName.toLowerCase(Locale.getDefault());

		if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
			suffixName = ".osj";
		} else if (suffixName.equals(".png")) {
			suffixName = ".osp";
		}

		File file = new File(path);

		try {
			this.bytes = StreamParser.parseToByteArray(new FileInputStream(file));
			sha1FileName = sha1.getDigestOfString(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			isUploadStatus = UPLOAD_EMPTY;
			// uploadLoadingListener.loading(0, UPLOAD_EMPTY);
		}
		if (this.bytes == null) {
			isUploadStatus = UPLOAD_EMPTY;
			uploadLoadingListener.onLoading(instance, 0, 0, UPLOAD_EMPTY);
			return;
		}

		this.fileName = OSS_DIRECTORY + sha1FileName + suffixName;
		checkFileExists();
	}

	public void checkFileExists() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("fileName", fileName);

		httpUtils.send(HttpMethod.POST, API.IMAGE_CHECKFILEEXIST, params, checkFileExists);
	}

	public ResponseHandler<String> checkFileExists = httpClient.new ResponseHandler<String>() {

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
				if (response.fileName.equals(fileName) && response.exists == true) {
					isUploadStatus = UPLOAD_SUCCESS;
					if (uploadLoadingListener != null) {
						uploadLoadingListener.onSuccess(instance, 0);
					}
					log.e("上传成功**********************服务器已经存在");
				} else {
					initiateMultipartupload();
				}
			} else {
				log.e("上传失败**********************" + response.失败原因);
			}
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			isUploadStatus = UPLOAD_INITFAILED;
			uploadLoadingListener.onLoading(instance, 0, 0, UPLOAD_FAILED);
			initiateMultipartupload();
		};
	};

	public void initiateMultipartupload() {
		isUploadStatus = UPLOAD_INIT;
		long expires = (new Date().getTime() / 1000) + addExpires;
		String postContent = "POST\n\n\n" + expires + "\n/" + BUCKETNAME + "/" + fileName + "?uploads";
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

		String url = OSS_HOST_URL + fileName + "?uploads";

		if (contentType != null) {
			params.addHeader("Content-Type", contentType);
		}

		params.addQueryStringParameter("OSSAccessKeyId", OSSACCESSKEYID);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		httpHandler = httpUtils.send(HttpMethod.POST, url, params, initUpload);
	}

	public ResponseHandler<String> initUpload = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				initiateMultipartUploadResult = parseXml(responseInfo.result);
				isUploadStatus = UPLOAD_INITSUCCESS;
				uploadParts();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			isUploadStatus = UPLOAD_INITFAILED;
		};
	};

	public void uploadParts() {
		isUploadStatus = UPLOAD_LOADING;
		partSuccessCount = 0;
		partCount = (int) Math.ceil((double) bytes.length / (double) partSize);

		// log.e("partCount:" + partCount);

		for (int i = 0; i < partCount; i++) {
			int partID = i + 1;
			uploadPart(partID);
		}
	}

	public void uploadPart(final int partID) {
		long expires = (new Date().getTime() / 1000) + addExpires;

		String postContent = "PUT\n\n\n" + expires + "\n/" + BUCKETNAME + "/" + fileName + "?partNumber=" + partID + "&uploadId=" + initiateMultipartUploadResult.uploadId;
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

		String url = OSS_HOST_URL + fileName + "?partNumber=" + partID + "&uploadId=" + initiateMultipartUploadResult.uploadId;

		params.addQueryStringParameter("OSSAccessKeyId", OSSACCESSKEYID);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);

		if ((bytes != null) && (bytes.length > 0)) {

			int length = partSize;

			int start = (partID - 1) * partSize;
			if (partID * partSize > bytes.length) {
				length = bytes.length - start;
			}

			byte[] byte0 = new byte[length];

			for (int j = 0; j < length; j++) {
				byte0[j] = bytes[start + j];
			}
			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			digest.update(byte0, 0, byte0.length);
			BigInteger bigInt = new BigInteger(1, digest.digest());
			String eTag = bigInt.toString(16).toUpperCase(Locale.getDefault());
			if (eTag.length() < 32) {
				for (int h = 0; h < 32 - eTag.length(); h++) {
					eTag = "0" + eTag;
				}
			}
			Part part = new Part(partID, eTag);
			part.setStatus(Part.PART_INIT);
			if (!parts.contains(part)) {
				parts.add(part);
			}
			params.setBodyEntity(new InputStreamUploadEntity(new ByteArrayInputStream(byte0), byte0.length));
			params.setBodyEntity(new ByteArrayEntity(byte0));
			UploadResponseHandler uploadResponseHandler = new UploadResponseHandler();
			uploadResponseHandler.partID = partID;
			uploadResponseHandler.part = part;
			part.uploadResponseHandler = uploadResponseHandler;
			httpUtils.send(HttpMethod.PUT, url, params, uploadResponseHandler);
		}
	}

	public class TimeLine {

		public long start = 0; // 0
		public long startConnect = 0; // 1

		public long startSend = 0; // 2
		public long sent = 0; // 3

		public long startReceive = 0; // 4
		public long received = 0; // 5
	}

	public TimeLine time = new TimeLine();
	public int uploadPrecent;

	class UploadResponseHandler extends ResponseHandler<String> {

		UploadResponseHandler() {
			httpClient.super();
		}

		public int partID = 0;
		Part part;

		@Override
		public void onStart() {
			if (partID == 1) {
				time.start = System.currentTimeMillis();
			}
			part.setStatus(Part.PART_LOADING);
		};

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			super.onLoading(total, current, isUploading);
			time.received = System.currentTimeMillis();
			// log.e(total + "--*****---" + current + "_+_+_+_+_+_+" + isUploading);
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			partSuccessCount++;
			part.setStatus(Part.PART_SUCCESS);
			uploadPrecent = (int) ((((double) partSuccessCount / (double) partCount)) * 100);
			uploadLoadingListener.onLoading(instance, uploadPrecent, (time.received - time.start), UPLOAD_SUCCESS);
			// log.e(partSuccessCount + "-----" + partCount + "------" +
			// part.eTag);
			if (partSuccessCount == partCount) {
				time.received = System.currentTimeMillis();
				completeMultipartUpload();
			}
		};

		@Override
		public void onFailure(HttpException error, String msg) {
			// log.e( error + "-----************---" + msg);
			// instance.isUploadStatus = UPLOAD_FAILED;
			part.setStatus(Part.PART_FAILED);
			if (isUploadStatus != UPLOAD_CANCLE) {
				uploadPart(partID);
			}
			// uploadLoadingListener.loading(
			// instance.partSuccessCount / partCount, UPLOAD_FAILED);
		};
	};

	public void completeMultipartUpload() {
		long expires = (new Date().getTime() / 1000) + addExpires;

		String postContent = "POST\n\n\n" + expires + "\n/" + BUCKETNAME + "/" + fileName + "?uploadId=" + initiateMultipartUploadResult.uploadId;
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

		String url = OSS_HOST_URL + fileName + "?uploadId=" + initiateMultipartUploadResult.uploadId;

		params.addQueryStringParameter("OSSAccessKeyId", OSSACCESSKEYID);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		params.setBodyEntity(new ByteArrayEntity(writeXml(parts).getBytes()));

		httpUtils.send(HttpMethod.POST, url, params, completeUpload);
	}

	public ResponseHandler<String> completeUpload = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			completeMultipartUploadResult = parseXmlCompleteResult(responseInfo.result);
			isUploadStatus = UPLOAD_SUCCESS;
			uploadLoadingListener.onSuccess(instance, (int) (time.received - time.start));
			log.e("上传成功**********************");
			uploadFileName();
			// log.e(completeMultipartUploadResult.location + "---" +
			// completeMultipartUploadResult.bucket + "---" +
			// completeMultipartUploadResult.key + "---" +
			// completeMultipartUploadResult.eTag);
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			isUploadStatus = UPLOAD_FAILED;
		};
	};

	public void uploadFileName() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("fileName", fileName);

		httpUtils.send(HttpMethod.POST, API.IMAGE_UPLOADFILENAME, params, uploadFileName);
	}

	public ResponseHandler<String> uploadFileName = httpClient.new ResponseHandler<String>() {

		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("上传成功")) {
				log.e("上传文件名到服务器成功");
			} else {
				log.e("上传文件名到服务器失败**********************" + response.失败原因);
			}
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			isUploadStatus = UPLOAD_INITFAILED;
			uploadLoadingListener.onLoading(instance, 0, 0, UPLOAD_FAILED);
			initiateMultipartupload();
		};
	};

	public void cancalMultipartUpload() {
		if (httpHandler != null) {
			httpHandler.cancel();
			isUploadStatus = UPLOAD_CANCLE;
		}
	}

	public void setUploadLoadingListener(OnUploadLoadingListener listener) {
		this.uploadLoadingListener = listener;
	}

	public class Part {
		public int partNumber;
		public String eTag;
		public int status = PART_DEFAULT;

		public UploadResponseHandler uploadResponseHandler;

		public static final int PART_DEFAULT = 0x10;
		public static final int PART_INIT = 0x11;
		public static final int PART_LOADING = 0x12;
		public static final int PART_SUCCESS = 0x13;
		public static final int PART_FAILED = 0x14;

		public Part() {
			this.partNumber = 0;
			this.eTag = "";
			status = PART_DEFAULT;
		}

		public Part(int partNumber, String eTag) {
			this.partNumber = partNumber;
			this.eTag = eTag;
			status = PART_DEFAULT;
		}

		public void setPartNumber(int partNumber) {
			this.partNumber = partNumber;
		}

		public void seteTag(String eTag) {
			this.eTag = eTag;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		@Override
		public boolean equals(Object o) {
			boolean flag = false;
			if (o != null) {
				try {
					Part c = (Part) o;
					if (partNumber == c.partNumber && eTag.equals(c.eTag)) {
						flag = true;
					}
				} catch (Exception e) {
					flag = false;
				}
			}
			return flag;
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

	public String writeXml(List<Part> parts) {
		StringWriter stringWriter = new StringWriter();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();// Xml.newSerializer();
			xmlSerializer.setOutput(stringWriter);
			xmlSerializer.startDocument("utf-8", true);
			xmlSerializer.startTag(null, "CompleteMultipartUpload");
			for (Part part : parts) {
				xmlSerializer.startTag(null, "Part");
				xmlSerializer.startTag(null, "PartNumber");
				xmlSerializer.text(part.partNumber + "");
				xmlSerializer.endTag(null, "PartNumber");
				xmlSerializer.startTag(null, "ETag");
				xmlSerializer.text("\"" + part.eTag + "\"");
				xmlSerializer.endTag(null, "ETag");
				xmlSerializer.endTag(null, "Part");
			}
			xmlSerializer.endTag(null, "CompleteMultipartUpload");
			xmlSerializer.endDocument();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
	}

	class CompleteMultipartUploadResult {
		public String location;
		public String bucket;
		public String key;
		public String eTag;
	}

	public CompleteMultipartUploadResult parseXmlCompleteResult(String resultXml) {
		CompleteMultipartUploadResult completeMultipartUploadResult = null;
		try {
			InputStream is = new ByteArrayInputStream(resultXml.getBytes("UTF-8"));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			Element rootElement = doc.getDocumentElement();
			completeMultipartUploadResult = instance.new CompleteMultipartUploadResult();
			Node item = rootElement;
			NodeList properties = item.getChildNodes();
			for (int j = 0; j < properties.getLength(); j++) {
				Node property = properties.item(j);
				String nodeName = property.getNodeName();
				if (nodeName.equals("Bucket")) {
					completeMultipartUploadResult.bucket = property.getFirstChild().getNodeValue();
				} else if (nodeName.equals("Key")) {
					completeMultipartUploadResult.key = property.getFirstChild().getNodeValue();
				} else if (nodeName.equals("ETag")) {
					completeMultipartUploadResult.eTag = property.getFirstChild().getNodeValue();
				} else if (nodeName.equals("Location")) {
					completeMultipartUploadResult.location = property.getFirstChild().getNodeValue();
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return completeMultipartUploadResult;
	}

	class InitiateMultipartUploadResult {
		public String bucket;
		public String key;
		String uploadId;
	}

	public InitiateMultipartUploadResult parseXml(String resultXml) throws Exception {
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
				initiateMultipartUploadResult.bucket = property.getFirstChild().getNodeValue();
			} else if (nodeName.equals("Key")) {
				initiateMultipartUploadResult.key = property.getFirstChild().getNodeValue();
			} else if (nodeName.equals("UploadId")) {
				initiateMultipartUploadResult.uploadId = property.getFirstChild().getNodeValue();
			}
		}
		return initiateMultipartUploadResult;
	}
}
