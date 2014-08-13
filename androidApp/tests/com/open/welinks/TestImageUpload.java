package com.open.welinks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.entity.ByteArrayEntity;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.aliyun.android.oss.Helper;
import com.aliyun.android.oss.OSSHttpTool;
import com.aliyun.android.oss.ObjectMetaData;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;

public class TestImageUpload extends AndroidTestCase {

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	/******************** check image **************************/
	public void checkImage(String fileName, String value) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("accessKey", "lejoying");
		params.addBodyParameter("phone", "15120088197");
		params.addBodyParameter("filename", fileName);
		params.addBodyParameter("notencryptedcontent", value);

		HttpUtils http = new HttpUtils();
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		String url2 = "http://192.168.1.91/image/checkfile";
		http.send(HttpRequest.HttpMethod.POST, url2, params,
				responseHandlers.checkFile);
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

		ObjectMetaData objectMetaData = new ObjectMetaData();
		objectMetaData.setContentType(contentType);
		OSSHttpTool httpTool = new OSSHttpTool();

		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "test1/test.jpg");

		String sha1FileName = "default";
		byte[] bytes = null;
		try {
			bytes = StreamParser.parseToByteArray(new FileInputStream(file));
			sha1FileName = sha1.getDigestOfString(bytes);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String resource = httpTool.generateCanonicalizedResource("/"
				+ BACKETNAME + "/" + sha1FileName + "." + endFileName);
		String requestUri = OSS_END_POINT + resource;

		String dateStr = Helper.getGMTDate();
		String xossHeader = OSSHttpTool
				.generateCanonicalizedHeader(objectMetaData.getAttrs());
		String authorization = OSSHttpTool.generateAuthorization(ACCESSKEYID,
				ACCESSKEYSECRET, "PUT", "", objectMetaData.getContentType(),
				dateStr, xossHeader, resource);

		params.addHeader("Authorization", authorization);
		params.addHeader("Date", dateStr);
		params.addHeader("Host", OSS_HOST);
		params.addHeader("Content-Type", contentType);

		for (Map.Entry entry : objectMetaData.getAttrs().entrySet()) {
			addHttpRequestHeader(params, (String) entry.getKey(),
					(String) entry.getValue());
		}

		if ((bytes != null) && (bytes.length > 0)) {
			params.setBodyEntity(new ByteArrayEntity(bytes));
		}

		http.send(HttpRequest.HttpMethod.PUT, requestUri, params,
				responseHandlers.upload);
	}

	public static void addHttpRequestHeader(RequestParams request, String key,
			String value) {
		if ((!Helper.isEmptyString(key)) && (!Helper.isEmptyString(value)))
			request.setHeader(key, value);
	}

}
