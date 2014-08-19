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

import org.apache.http.Header;
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

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.aliyun.android.oss.Base64;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;

public class TestMultipartUpload extends Activity {

	public static String tag = "TestMultipartUpload";

	public static HttpClient httpClient = HttpClient.getInstance();

	public Gson gson = new Gson();

	static TestMultipartUpload instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		Log.e(tag, "-----onCreate-----");
		initiateMultipartupload();
	}

	public InitiateMultipartUploadResult initiateMultipartUploadResult;
	List<Part> parts = new ArrayList<Part>();
	public byte[] bytes;
	public String fileName;
	String BUCKETNAME = "wxgs-data";
	String OSSAccessKeyId = "dpZe5yUof6KSJ8RM";
	String ACCESSKEYSECRET = "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG";
	
	public void initiateMultipartupload() {


		long expires = (new Date().getTime() / 1000) + 600;

		SHA1 sha1 = new SHA1();
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "test1/test002.jpg");// "test1/test002.jpg"

		// byte[] bytes = null;
		String sha1FileName = "";
		try {
			bytes = StreamParser.parseToByteArray(new FileInputStream(file));
			sha1FileName = sha1.getDigestOfString(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		fileName = sha1FileName + ".jpg";

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

		String url = "http://images7.we-links.com/" + fileName + "?uploads";

		params.addQueryStringParameter("OSSAccessKeyId", OSSAccessKeyId);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		Log.e(fileName, OSSAccessKeyId + "---" + expires + "---" + signature + "---" + fileName);
		httpUtils.send(HttpMethod.POST, url, params, initUpload);
	}

	public ResponseHandler<String> initUpload = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.statusCode + "-------initUpload success");
			try {
				initiateMultipartUploadResult = parseXml(responseInfo.result);
				Log.e(tag, initiateMultipartUploadResult.bucket + "---" + initiateMultipartUploadResult.key + "---" + initiateMultipartUploadResult.uploadId);
				Header[] headers = responseInfo.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					Log.e(tag, "reply upload: " + headers[i]);
				}
				uploadParts();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	public static ResponseHandler<String> inittingUpload = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.result + "-------inittingUpload success");
			instance.currentCount++;
			if (instance.currentCount == instance.partCount) {
				Log.e(tag, "-------------------------------");
				instance.completeMultipartUpload();
			}
			// Header[] headers = responseInfo.getAllHeaders();
			// for (int i = 0; i < headers.length; i++) {
			// Log.e(tag, "reply upload: " + headers[i]);
			//
			// }
		};
	};
	public static ResponseHandler<String> completeUpload = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.result + "-------completeUpload success");
			Header[] headers = responseInfo.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				Log.e(tag, "reply upload: " + headers[i]);
			}
			CompleteMultipartUploadResult completeMultipartUploadResult = parseXmlCompleteResult(responseInfo.result);
			Log.e(tag, completeMultipartUploadResult.location + "---" + completeMultipartUploadResult.bucket + "---" + completeMultipartUploadResult.key + "---" + completeMultipartUploadResult.eTag);
		};
	};

	int partSize = 512000;
	int partCount = 0;
	int currentCount = 0;

	public void uploadParts() {
		currentCount = 0;
		partCount = (int) Math.ceil((double) bytes.length / (double) partSize);

		Log.e(tag, "partCount:" + partCount);


		for (int i = 0; i < partCount; i++) {
			int partID = i + 1;
			long expires = (new Date().getTime() / 1000) + 600;

			String postContent = "PUT\n\n\n" + expires + "\n/" + BUCKETNAME + "/" + fileName + "?partNumber=" + (i + 1) + "&uploadId=" + initiateMultipartUploadResult.uploadId;
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

			String url = "http://images7.we-links.com/" + fileName + "?partNumber=" + (i + 1) + "&uploadId=" + initiateMultipartUploadResult.uploadId;

			params.addQueryStringParameter("OSSAccessKeyId", OSSAccessKeyId);
			params.addQueryStringParameter("Expires", expires + "");
			params.addQueryStringParameter("Signature", signature);

			Log.e(fileName, OSSAccessKeyId + "---" + expires + "---" + signature + "---" + fileName);

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
			}
			httpUtils.send(HttpMethod.PUT, url, params, inittingUpload);
		}
	}

	public void completeMultipartUpload() {
		long expires = (new Date().getTime() / 1000) + 600;

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

		String url = "http://images7.we-links.com/" + fileName + "?uploadId=" + initiateMultipartUploadResult.uploadId;

		params.addQueryStringParameter("OSSAccessKeyId", OSSAccessKeyId);
		params.addQueryStringParameter("Expires", expires + "");
		params.addQueryStringParameter("Signature", signature);
		params.setBodyEntity(new ByteArrayEntity(writeXml(parts).getBytes()));

		httpUtils.send(HttpMethod.POST, url, params, completeUpload);
	}

	public static String getHmacSha1Signature(String value, String key) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] keyBytes = key.getBytes();
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);

		byte[] rawHmac = mac.doFinal(value.getBytes());
		return new String(Base64.encode(rawHmac));
	}

	class Part {
		public int partNumber;
		public String eTag;

		public Part(int partNumber, String eTag) {
			this.partNumber = partNumber;
			this.eTag = eTag;
		}
	}

	public String writeXml(List<Part> parts) {
		// List<Part> parts = new ArrayList<Part>();
		// parts.add(new Part(1, "D49418A40A16133A1782D900B352E26F"));
		// parts.add(new Part(2, "7817E2EB42CA1530DFDE67B38757DD9D"));

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
			Log.e(tag, stringWriter.toString() + "-----");
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
