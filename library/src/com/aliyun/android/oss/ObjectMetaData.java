package com.aliyun.android.oss;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ObjectMetaData {
	private String cacheControl = null;

	private String contentDisposition = null;

	private String contentEncoding = null;

	private String contentLength = null;

	private String contentType = null;

	private String eTag = null;

	private Date expirationTime = null;

	private Date lastModified = null;

	private String server = null;

	private Date date = null;

	private Map<String, String> attrs;

	public ObjectMetaData() {
		this.attrs = new HashMap<String, String>();
	}

	public Map<String, String> getAttrs() {
		return this.attrs;
	}

	protected void addAttr(String key, String value) {
		if (this.attrs.containsKey(key)) {
			value = (String) this.attrs.get(key) + ":" + value;
		}
		this.attrs.put(key, value);
	}

	public void addCustomAttr(String key, String value) {
		if (key.startsWith("x-oss-meta-"))
			addAttr(key, value);
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

	public String getContentLength() {
		return this.contentLength;
	}

	public void setContentLength(String contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String geteTag() {
		return this.eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}

	public Date getExpirationTime() {
		return this.expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getServer() {
		return this.server;
	}

	public void setServer(String server) {
		this.server = server;
	}
}