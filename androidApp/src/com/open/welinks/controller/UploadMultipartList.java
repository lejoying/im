package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadMultipartList {

	public String tag = "UploadMultipartList";

	public ArrayList<String> uploadMultipartFilesList = new ArrayList<String>();
	public HashMap<String, UploadMultipart> uploadMultipartFilesMap = new HashMap<String, UploadMultipart>();

	public static UploadMultipartList uploadMultipartList;

	public static UploadMultipartList getInstance() {
		if (uploadMultipartList == null) {
			uploadMultipartList = new UploadMultipartList();
		}
		return uploadMultipartList;
	}

	public void addMultipart(UploadMultipart multipart) {
		this.uploadMultipartFilesList.add(multipart.path);
		this.uploadMultipartFilesMap.put(multipart.path, multipart);
		multipart.startUpload();
	}

	public void addMultipart(List<UploadMultipart> multiparts) {
		for (int i = 0; i < multiparts.size(); i++) {
			UploadMultipart multipart = multiparts.get(i);
			this.uploadMultipartFilesList.add(multipart.path);
			this.uploadMultipartFilesMap.put(multipart.path, multipart);
			multipart.startUpload();
		}
	}

	public void cancleMultipart(String path) {
		UploadMultipart multipart = uploadMultipartFilesMap.get(path);
		if (multipart != null) {
			multipart.cancalMultipartUpload();
		}
	}
}
