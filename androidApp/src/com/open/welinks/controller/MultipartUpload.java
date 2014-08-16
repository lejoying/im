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

import android.util.Log;

import com.aliyun.android.oss.Base64;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.entity.Part;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;

public class MultipartUpload {

	public static String tag = "MultipartUpload";

	public static HttpClient httpClient = HttpClient.getInstance();

	public Gson gson = new Gson();

	public String BUCKETNAME = "welinkstest";
	public String OSSACCESSKEYID = "dpZe5yUof6KSJ8RM";
	public String ACCESSKEYSECRET = "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG";

	public String OSS_HOST_URL = "http://images5.we-links.com/";

	public UploadLoadingListener uploadLoadingListener;

	public static MultipartUpload instance;

	public String path; // example:"sdcard/test/test001.jpg"
	public String contentType = null;// example: "image/jpg" "image/png"

	public static int UPLOAD_DEFAULT = 0x00;
	public static int UPLOAD_LOADING = 0x01;
	public static int UPLOAD_SUCCESS = 0x02;
	public static int UPLOAD_EMPTY = 0x03;
	public static int UPLOAD_FAILED = 0x04;
	public static int UPLOAD_INITFAILED = 0x05;

	public int isUploadStatus = UPLOAD_DEFAULT;

	public InitiateMultipartUploadResult initiateMultipartUploadResult;
	public List<Part> parts = new ArrayList<Part>();
	public byte[] bytes;
	public String fileName = "";

	public int addExpires = 600;
	public int partSize = 256000;
	public int partCount = 0;
	public int partSuccessCount = 0;

	public MultipartUpload(String path) {
		instance = this;
		this.path = path;
		initiateMultipartupload();
	}

	public MultipartUpload(String path, String contentType) {
		instance = this;
		this.path = path;
		this.contentType = contentType;
		initiateMultipartupload();
	}

	public void initiateMultipartupload() {
		SHA1 sha1 = new SHA1();
		String sha1FileName = "";
		String suffixName = path.substring(path.lastIndexOf("."));

		long expires = (new Date().getTime() / 1000) + addExpires;

		File file = new File(path);

		try {
			this.bytes = StreamParser.parseToByteArray(new FileInputStream(file));
			sha1FileName = sha1.getDigestOfString(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			isUploadStatus = UPLOAD_EMPTY;
		}
		if (this.bytes == null) {
			isUploadStatus = UPLOAD_EMPTY;
			return;
		}

		this.fileName = sha1FileName + suffixName;

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
		httpUtils.send(HttpMethod.POST, url, params, initUpload);
	}

	public ResponseHandler initUpload = httpClient.new ResponseHandler() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				initiateMultipartUploadResult = parseXml(responseInfo.result);
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

		Log.e(tag, "partCount:" + partCount);

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
			parts.add(part);
			params.setBodyEntity(new ByteArrayEntity(byte0));
			UploadResponseHandler uploadResponseHandler = new UploadResponseHandler();
			uploadResponseHandler.partID = partID;
			httpUtils.send(HttpMethod.PUT, url, params, uploadResponseHandler);
		}
	}

	class UploadResponseHandler extends ResponseHandler {

		UploadResponseHandler() {
			httpClient.super();
		}

		public int partID = 0;
		Part part;

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			instance.partSuccessCount++;
			part.setStatus(true);
			uploadLoadingListener.loading(instance.partSuccessCount / partCount, UPLOAD_SUCCESS);
			if (instance.partSuccessCount == instance.partCount) {
				instance.completeMultipartUpload();
			}
		};

		@Override
		public void onFailure(HttpException error, String msg) {
			Log.e(tag, error + "-----************---" + msg);
			instance.isUploadStatus = UPLOAD_FAILED;
			part.setStatus(false);
			uploadPart(partID);
			uploadLoadingListener.loading(instance.partSuccessCount / partCount, UPLOAD_FAILED);
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

	public static ResponseHandler completeUpload = httpClient.new ResponseHandler() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			CompleteMultipartUploadResult completeMultipartUploadResult = parseXmlCompleteResult(responseInfo.result);
			instance.isUploadStatus = UPLOAD_SUCCESS;
			Log.e(tag, completeMultipartUploadResult.location + "---" + completeMultipartUploadResult.bucket + "---" + completeMultipartUploadResult.key + "---" + completeMultipartUploadResult.eTag);
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			instance.isUploadStatus = UPLOAD_FAILED;
		};
	};

	public interface UploadLoadingListener {
		public void loading(int precent, int status);
	}

	public void setUploadLoadingListener(UploadLoadingListener listener) {
		this.uploadLoadingListener = listener;
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

	public static CompleteMultipartUploadResult parseXmlCompleteResult(String resultXml) {
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
