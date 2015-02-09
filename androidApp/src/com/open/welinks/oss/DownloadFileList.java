package com.open.welinks.oss;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadFileList {

	public String tag = "DownloadFileList";

	public static DownloadFileList downloadFileList;

	public ArrayList<String> downloadFiles = new ArrayList<String>();
	public HashMap<String, DownloadFile> downloadFilesMap = new HashMap<String, DownloadFile>();

	public static DownloadFileList getInstance() {
		if (downloadFileList == null) {
			downloadFileList = new DownloadFileList();
		}
		return downloadFileList;
	}

	public void addDownloadFile(DownloadFile downloadFile) {
		downloadFiles.add(downloadFile.path);
		downloadFilesMap.put(downloadFile.path, downloadFile);
		downloadFile.startDownload();
	}

	public void cancleDownloadFile(String url) {
		DownloadFile downloadFile = downloadFilesMap.get(url);
		if (downloadFile != null) {
			downloadFile.httpHandler.cancel(true);
		}
	}
}
