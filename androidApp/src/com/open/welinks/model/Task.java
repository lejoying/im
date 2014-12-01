package com.open.welinks.model;

import java.util.List;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public abstract class Task {
	public Status status = new Status();

	public class Status {
		public int Created = 1, DataModified = 2, ViewModified = 3, localFilesResolved = 4, FilesUploading = 5, FilesUploaded = 6;
		public int RequestSending = 11, RequestSent = 12, ResponseReceiving = 13, ResponseReceived = 14;
		public int DataUpdated = 21, ViewUpdated = 22;
		public int Failed = 31, discarded = 32, Done = 33;
		public int state = Created;
	}

	public long startTime;
	public long endTime;
	public float progress = 0;// 0~100

	public void onProgress() {

	}

	public void modifyData() {// 主UI线程

	}

	public void modifyView() {// 主UI线程

	}

	public List<MyFile> myFileList;
	public int uploadeFileCount = 0;

	public int currentResolveFileCount = 0;
	public int resolveFileTotal = 0;

	public void onLocalFilesResolved() {// 子线程

	}

	public void uploadFiles() {// 子线程

	}

	public String API;
	public RequestParams params = new RequestParams();
	public HttpMethod mHttpMethod = HttpMethod.POST;

	abstract public void sendRequest(); // 子线程

	abstract public boolean onResponseReceived(ResponseInfo<String> responseInfo);// 主UI线程

	public void updateData() {// 主UI线程

	}

	public void updateView() {// 主UI线程
	}

}
