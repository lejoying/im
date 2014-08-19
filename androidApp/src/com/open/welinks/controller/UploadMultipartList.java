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

	public void addMultipart(UploadMultipart multipartUpload) {
		this.uploadMultipartFilesList.add(multipartUpload.path);
		this.uploadMultipartFilesMap.put(multipartUpload.path, multipartUpload);
		multipartUpload.startUpload();
	}

	public void cancleMultipart(String path) {
		UploadMultipart multipart = uploadMultipartFilesMap.get(path);
		if (multipart != null) {
			multipart.cancalMultipartUpload();
		}
	}
}
