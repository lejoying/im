package com.open.welinks.model;

import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.SystemClock;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.open.lib.MyLog;
import com.open.lib.ResponseHandler;

public class TaskManager {

	public String tag = "TaskManager";
	public MyLog log = new MyLog(tag, true);

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public static TaskManager instance;

	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}

	public void startLoop() {
		new Thread(this.taskRunnable).start();
		postHandler();
	}

	public boolean isIntialized = false;

	public void initialize() {
		if (this.isIntialized == false) {
			this.taskRunnable = new TaskRunnable();
			this.taskQueue = new LinkedBlockingQueue<Task>();
			this.requestQueue = new MyLinkedListQueue<Task>();
			this.requestQueue.currentRunnable = this.taskRunnable;
			this.responseQueue = new LinkedBlockingQueue<Task>();
			this.mHandler = new Handler();
			this.isIntialized = true;
		}
	}

	public LinkedBlockingQueue<Task> taskQueue;

	public void pushTask(Task task) {
		long currentTime = SystemClock.uptimeMillis();
		task.startTime = currentTime;
		task.modifyData();
		task.status.state = task.status.DataModified;
		task.modifyView();
		task.status.state = task.status.ViewModified;
		taskQueue.offer(task);
		requestQueue.offerE(task);
	}

	// public LinkedBlockingQueue<Task> requestQueue = new LinkedBlockingQueue<Task>();

	public MyLinkedListQueue<Task> requestQueue;
	public TaskRunnable taskRunnable;

	class TaskRunnable implements Runnable {
		@Override
		public void run() {
			while (requestQueue.isRunning) {
				try {
					Task task = requestQueue.takeE();
					if (task == null) {
						log.e("break");
					} else {
						if (task.status.state == task.status.ViewModified) {
							if (task.myFileList != null) {
								for (MyFile myFile : task.myFileList) {
									myFile.task = task;
									taskManageHolder.fileHandler.pushMyFile(myFile);
								}
								task.status.state = task.status.FilesUploading;
								// task.resolveLocalFiles();
								// task.status.state = task.status.localFilesResolved;
								// task.uploadFiles();

								// task.status.state = task.status.FilesUploaded;
							} else {
								task.status.state = task.status.FilesUploaded;
							}
						}

						if (task.status.state == task.status.FilesUploaded) {
							log.e("task.status.state == task.status.FilesUploaded");
							MyResponseHandler responseHandler = new MyResponseHandler();
							responseHandler.task = task;
							task.sendRequest();
							HttpUtils httpUtils = new HttpUtils();
							httpUtils.send(task.mHttpMethod, task.API, task.params, responseHandler);
							task.status.state = task.status.RequestSending;
						}
					}
				} catch (Exception e) {
					log.e("TaskThread@" + "异常");
					// StackTraceElement ste = new Throwable().getStackTrace()[1];
					// log.e("Exception@" + ste.getLineNumber());
				}
			}
		}
	}

	void onMyFileUploaded(MyFile uploadedMyFile) {
		log.e("onMyFileUploaded");
		Task task = uploadedMyFile.task;
		if (task.myFileList == null) {
			return;
		}
		task.uploadeFileCount++;
		boolean isUploaded = true;
		if (task.uploadeFileCount >= task.myFileList.size()) {
			for (MyFile myFile : task.myFileList) {
				if (myFile.status.state != myFile.status.Completed) {
					isUploaded = false;
					break;
				}
			}
		} else {
			isUploaded = false;
		}
		log.e(task.uploadeFileCount + "--" + task.myFileList.size() + "--" + isUploaded);
		if (isUploaded) {
			task.status.state = task.status.FilesUploaded;
			requestQueue.offerE(task);
		}
	}

	public class MyResponseHandler extends ResponseHandler<String> {
		public Task task;

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			if (task != null) {
				log.e("MyResponseHandler onSuccess");
				task.status.state = task.status.ResponseReceiving;
				boolean needResolveResponse = task.onResponseReceived(responseInfo);
				task.status.state = task.status.ResponseReceived;
				if (needResolveResponse) {
					responseQueue.offer(task);
					postHandler();
				} else {
					logTask(task);
					task.status.state = task.status.Done;
				}
			}
		};
	};

	public LinkedBlockingQueue<Task> responseQueue;
	public Handler mHandler;
	public boolean handlerIsRunning = false;

	public void postHandler() {
		if (handlerIsRunning == false) {
			mHandler.post(mTaskRunnable);
		}
	}

	public Runnable mTaskRunnable = new Runnable() {
		@Override
		public void run() {
			log.e("mTaskRunnable Runnable");
			Task task;
			try {
				task = responseQueue.poll();
				if (task != null) {
					handlerIsRunning = true;
					task.updateData();
					task.status.state = task.status.DataUpdated;
					task.updateView();
					task.status.state = task.status.ViewUpdated;
					logTask(task);
					task.status.state = task.status.Done;

					mHandler.post(mTaskRunnable);
				} else {
					handlerIsRunning = false;
				}

			} catch (Exception e) {
				StackTraceElement ste = new Throwable().getStackTrace()[1];
				log.e("Exception@" + ste.getLineNumber());
			}
		}
	};

	void logTask(Task task) {

	}
}
