package com.open.welinks.model;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.MyLog;
import com.open.welinks.oss.DownloadFileList;
import com.open.welinks.oss.UploadMultipartList;
import com.open.welinks.view.ViewManage;

public class TaskManageHolder {

	public String tag = "TaskManageHolder";
	public MyLog log = new MyLog(tag, true);

	public TaskManager taskManager;
	public FileHandler fileHandler;
	public MultipartUploader multipartUploader;

	public AudioHandler audioHandler;

	public ImageLoader imageLoader;

	public ViewManage viewManage;

	public UploadMultipartList uploadMultipartList;
	public DownloadFileList downloadFileList;

	public LBSHandler lbsHandler;

	public static TaskManageHolder taskManageHolder;

	public static TaskManageHolder getInstance() {
		if (taskManageHolder == null) {
			taskManageHolder = new TaskManageHolder();
		}
		return taskManageHolder;
	}

	public TaskManageHolder() {
	}

	public boolean isInitialized = false;

	public void initialize() {
		if (this.isInitialized == false) {
			this.isInitialized = true;
			this.taskManager = TaskManager.getInstance();
			this.taskManager.initialize();
			this.taskManager.startLoop();
			this.fileHandler = FileHandler.getInstance();
			this.fileHandler.initialize();
			this.fileHandler.startLoop();
			this.multipartUploader = MultipartUploader.getInstance();
			this.audioHandler = AudioHandler.getInstance();
			this.imageLoader = ImageLoader.getInstance();
			this.viewManage = ViewManage.getInstance();
			this.uploadMultipartList = UploadMultipartList.getInstance();
			this.lbsHandler = LBSHandler.getInstance();
			this.downloadFileList = DownloadFileList.getInstance();
		}
	}
}
