package com.aliyun.android.oss;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public class OSSHttpTool {

	public static final String ETAG = "ETag";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CACHE_CONTROL = "Cache-control";
	public static final String SERVER = "Server";
	public static final String EXPIRES = "Expires";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String HOST = "Host";
	public static final String LAST_MODIFIED = "Last-Modified";
	public static final String DATE = "Date";
	public static final String AUTHORIZATION = "Authorization";
	public static final String X_OSS_FILE_GROUP = "x-oss-file-group";
	public static final String X_OSS_ACL = "x-oss-acl";
	public static final String RANGE = "Range";
	public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	public static final String IF_MATCH = "If-Match";
	public static final String IF_NONE_MATCH = "If-None-Match";
	public static final String X_OSS_META_COMPRESS = "x-oss-meta-compress";
	public static final String X_OSS_META_ENCRYPT = "x-oss-meta-encrypt";

	public static final String PREFIX = "prefix";
	public static final String MAX_KEYS = "max-keys";
	public static final String MARKER = "marker";
	public static final String KEY_MARKER = "key-marker";
	public static final String DELIMITER = "delimiter";
	public static final String MAX_UPLOADS = "max-uploads";
	public static final String UPLOAD_ID_MARKER = "upload-id-marker";
	public static final String MAX_PARTS = "max-parts";
	public static final String PART_NUMBER_MARKER = "part-number-marker";
	public static final String ACL = "acl";
	public static final String GROUP = "group";
	public static final String UPLOAD_ID = "uploadId";
	public static final String PART_NUMBER = "partNumber";
	public static final String UPLOADS = "uploads";
	public static final String RESPONSE_CONTENT_TYPE = "response-content-type";
	public static final String RESPONSE_CONTENT_LANGUAGE = "response-content-language";
	public static final String RESPONSE_EXPIRES = "response-expires";
	public static final String RESPONSE_CACHE_CONTROL = "response-cache-control";
	public static final String RESPONSE_CONTENT_DISPOSITION = "response-content-disposition";
	public static final String RESPONSE_CONTENT_ENCODING = "response-content-encoding";
	private Boolean isAcl = null;

	private Boolean isGroup = Boolean.valueOf(false);

	private String uploadId = null;

	private Integer partNumber = null;

	private Boolean isUploads = Boolean.valueOf(false);

	private String contentType = null;

	private String contentLanguage = null;

	private String expires = null;

	private String cacheControl = null;

	private String contentDisposition = null;

	private String contentEncoding = null;

	public String generateCanonicalizedResource(String baseResource) {
		if (this.isAcl != null) {
			baseResource = appendParameter(baseResource, "acl");
		}
		if (this.isGroup.booleanValue()) {
			baseResource = appendParameter(baseResource, "group");
		}
		if (this.partNumber != null) {
			baseResource = appendParameterPair(baseResource, "partNumber",
					this.partNumber.toString());
		}
		if (this.cacheControl != null) {
			baseResource = appendParameterPair(baseResource,
					"response-cache-control", this.cacheControl);
		}
		if (this.contentDisposition != null) {
			baseResource = appendParameterPair(baseResource,
					"response-content-disposition", this.contentDisposition);
		}
		if (this.contentEncoding != null) {
			baseResource = appendParameterPair(baseResource,
					"response-content-encoding", this.contentEncoding);
		}
		if (this.contentLanguage != null) {
			baseResource = appendParameterPair(baseResource,
					"response-content-language", this.contentLanguage);
		}
		if (this.contentType != null) {
			baseResource = appendParameterPair(baseResource,
					"response-content-type", this.contentType);
		}
		if (this.expires != null) {
			baseResource = appendParameterPair(baseResource,
					"response-expires", this.expires);
		}
		if (this.uploadId != null) {
			baseResource = appendParameterPair(baseResource, "uploadId",
					this.uploadId);
		}
		if (this.isUploads.booleanValue()) {
			baseResource = appendParameter(baseResource, "uploads");
		}

		return baseResource;
	}

	public static byte[] getBytesFromIS(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b = 0;
		while ((b = is.read()) != -1)
			baos.write(b);
		return baos.toByteArray();
	}

	public static String appendParameterPair(String baseUri, String paramKey,
			String paramValue) {
		if ((Helper.isEmptyString(paramValue))
				|| (Helper.isEmptyString(paramKey))) {
			return baseUri;
		}
		if (baseUri.contains("?"))
			baseUri = baseUri + "&" + paramKey + "=" + paramValue;
		else {
			baseUri = baseUri + "?" + paramKey + "=" + paramValue;
		}
		return baseUri;
	}

	public static String appendParameter(String baseUri, String param) {
		if (Helper.isEmptyString(param)) {
			return baseUri;
		}
		if (baseUri.contains("?"))
			baseUri = baseUri + "&" + param;
		else {
			baseUri = baseUri + "?" + param;
		}
		return baseUri;
	}

	public static ObjectMetaData getObjectMetadataFromResponse(
			HttpResponse response) throws ParseException {
		Header[] headers = response.getAllHeaders();
		ObjectMetaData meta = new ObjectMetaData();
		for (Header h : headers) {
			if (h.getName().equalsIgnoreCase("Content-Length"))
				meta.setContentLength(h.getValue());
			else if (h.getName().equalsIgnoreCase("Content-Type"))
				meta.setContentType(h.getValue());
			else if (h.getName().equalsIgnoreCase("Content-Encoding"))
				meta.setContentEncoding(h.getValue());
			else if (h.getName().equalsIgnoreCase("Content-Disposition"))
				meta.setContentDisposition(h.getValue());
			else if (h.getName().equalsIgnoreCase("Cache-control"))
				meta.setCacheControl(h.getValue());
			else if (h.getName().equalsIgnoreCase("Expires"))
				meta.setExpirationTime(Helper.getGMTDateFromString(h.getValue()));
			else if (h.getName().equalsIgnoreCase("Date"))
				meta.setDate(Helper.getGMTDateFromString(h.getValue()));
			else if (h.getName().equalsIgnoreCase("Last-Modified"))
				meta.setLastModified(Helper.getGMTDateFromString(h.getValue()));
			else if (h.getName().equalsIgnoreCase("Server"))
				meta.setServer(h.getValue());
			else if (h.getName().equals("ETag"))
				meta.seteTag(h.getValue());
			else if (h.getName().startsWith("x-oss-meta-")) {
				meta.addCustomAttr(h.getName(), h.getValue());
			}
		}
		return meta;
	}

	public static void addHttpRequestHeader(HttpRequestBase request,
			String key, String value) {
		if ((!Helper.isEmptyString(key)) && (!Helper.isEmptyString(value)))
			request.setHeader(key, value);
	}

	public static String generateAuthorization(String accessId,
			String accessKey, String content) {
		String signature = null;
		try {
			signature = Helper.getHmacSha1Signature(content, accessKey);
		} catch (Exception e) {
			Log.i("authorization", e.toString());
		}
		return "OSS " + accessId + ":" + signature;
	}

	public static String generateAuthorization(String accessId,
			String accessKey, String httpMethod, String md5, String type,
			String date, String ossHeaders, String resource) {
		String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date
				+ "\n" + ossHeaders + resource;
		Log.d("content", content);
		return generateAuthorization(accessId, accessKey, content);
	}

	public static String generateCanonicalizedHeader(Map<String, String> headers) {
		String ossHeader = "";
		List<String> list = new ArrayList<String>();
		list.addAll(headers.keySet());
		Collections.sort(list);

		String post = "";
		for (String s : list) {
			if (s.equals(post))
				ossHeader = ossHeader + "," + (String) headers.get(s);
			else {
				ossHeader = ossHeader + "\n" + s + ":"
						+ (String) headers.get(s);
			}
			post = s;
		}

		if (!Helper.isEmptyString(ossHeader)) {
			ossHeader = ossHeader.trim();
			ossHeader = ossHeader + "\n";
		}
		return ossHeader;
	}

	public Boolean getIsAcl() {
		return this.isAcl;
	}

	public void setIsAcl(Boolean isAcl) {
		this.isAcl = isAcl;
	}

	public Boolean getGroup() {
		return this.isGroup;
	}

	public void setGroup(Boolean group) {
		this.isGroup = group;
	}

	public String getUploadId() {
		return this.uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public Integer getPartNumber() {
		return this.partNumber;
	}

	public void setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
	}

	public Boolean getIsUploads() {
		return this.isUploads;
	}

	public void setIsUploads(Boolean isUploads) {
		this.isUploads = isUploads;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentLanguage() {
		return this.contentLanguage;
	}

	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}

	public String getExpires() {
		return this.expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getCacheControl() {
		return this.cacheControl;
	}

	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	public String getContentDisposition() {
		return this.contentDisposition;
	}

	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	public String getContentEncoding() {
		return this.contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
}