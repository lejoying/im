package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.HashMap;


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

	public void cancleMultipart(String path) {
		UploadMultipart multipart = uploadMultipartFilesMap.get(path);
		if (multipart != null) {
			multipart.cancalMultipartUpload();
		}
	}
}
