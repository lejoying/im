package com.open.welinks.model;

import com.open.lib.MyLog;

public class TaskManageHolder {

	public String tag = "TaskManageHolder";
	public MyLog log = new MyLog(tag, true);

	public TaskManager taskManager;
	public FileHandler fileHandler;
	public MultipartUploader multipartUploader;

	public static TaskManageHolder taskManageHolder;

	public static TaskManageHolder getInstance() {
		if (taskManageHolder == null) {
			taskManageHolder = new TaskManageHolder();
		}
		return taskManageHolder;
	}

	public boolean isInitialized = false;

	public void initialize() {
		if (this.isInitialized == false) {
			this.taskManager = TaskManager.getInstance();
			this.taskManager.initialize();
			this.taskManager.startLoop();
			this.fileHandler = FileHandler.getInstance();
			this.fileHandler.initialize();
			this.fileHandler.startLoop();
			this.multipartUploader = MultipartUploader.getInstance();
			this.isInitialized = true;
		}
	}
}
