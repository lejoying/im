package com.open.welinks.model;

import com.lidroid.xutils.http.ResponseInfo;

public abstract class Task {
	public Status status = new Status();

	public class Status {
		public int Created = 1, DataModified = 2, ViewModified = 3, localFileResolved = 4, FileUploading = 5, FileUploaded = 6;
		public int RequestSending = 11, RequestSent = 12, ResponceReceiving = 13, ResponceReceived = 14;
		public int DataUpdated = 21, ViewUpdated = 22;
		public int Failed = 31, discarded = 32, Done = 33;
		public int state = Created;
	}

	public long startTime;	
	public long endTime;
	public float progress=0;//0~100
	public void onProgress(){
		
	}

	public void modifyData() {//主UI线程

	}

	public void modifyView() {//主UI线程

	}

	public void resolveLocalFiles() {//子线程

	}	

	abstract public void sendRequest(); //子线程


	abstract public void onResponceReceived(ResponseInfo<String> responseInfo);//主UI线程


	public void updateData() {//主UI线程

	}

	public void updateView() {//主UI线程
	}
}
