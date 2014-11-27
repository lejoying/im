package com.open.welinks.model;

public class MyFile {

	public class Status {
		public int Created = 0, Queueing = 1, LocalStored = 2, Checking = 3, Uploading = 4, Uploaded = 5;
		public int Failed = 6, Exception = 7;
		public int state = Created;
	}

	public Status status = new Status();

	public class Type {
		public int Image = 0, Other = 1;
		public int type = Image;
	}

	public Type type = new Type();

	public long startTime;
	public long endTime;
	public float progress = 0;// 0~100

	public void onProgress() {

	}

	public String path = "";
	public String fileName = "";
	public long length;
	public String suffixName;
	public byte[] bytes;
	public boolean isCompression = true;
	public boolean isExists = false;
	public String shaStr;
	
	public String bucket;
	public String key;
	public String uploadId;

	public Task task;

}
