package com.open.welinks.model;

import java.util.ArrayList;
import java.util.List;

public class MyFile {

	public class Status {
		public int Created = 1, Queueing = 2, LocalStored = 3;
		public int Checking = 11, Checked = 12;
		public int Initializing = 21, Initialized = 22;
		public int Uploading = 31, Uploaded = 32;
		public int Completing = 41, Completed = 42;
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

	public int partSuccessCount = 0;
	public int partCount = 0;
	public List<Part> parts = new ArrayList<Part>();

	public class Part {
		public int partNumber;
		public String eTag;

		public int PART_DEFAULT = 0x10;
		public int PART_INIT = 0x11;
		public int PART_LOADING = 0x12;
		public int PART_SUCCESS = 0x13;
		public int PART_FAILED = 0x14;

		public int status = PART_DEFAULT;

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
}
