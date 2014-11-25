package com.open.welinks.model;

import com.open.lib.MyLog;

public class FileTransferQueue {

	public String tag = "FileTransferQueue";
	public MyLog log = new MyLog(tag, true);

	public static FileTransferQueue fileTransferQueue;

	public static FileTransferQueue getInstance() {
		if (fileTransferQueue == null) {
			fileTransferQueue = new FileTransferQueue();
		}
		return fileTransferQueue;
	}
}
