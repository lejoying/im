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

	public FileHandler fileHandlers;

	public static TaskManager instance;
	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}

	public void startLoop() {
		fileHandlers = FileHandler.getInstance();
		new TaskThread().start();
		postHandler();

	}

	public LinkedBlockingQueue<Task> taskQueue = new LinkedBlockingQueue<Task>();

	public void pushTask(Task task) {
		long currentTime = SystemClock.uptimeMillis();
		task.startTime = currentTime;
		task.modifyData();
		task.status.state = task.status.DataModified;
		task.modifyView();
		task.status.state = task.status.ViewModified;

		taskQueue.offer(task);
		requestQueue.offer(task);
	}

	public LinkedBlockingQueue<Task> requestQueue = new LinkedBlockingQueue<Task>();


	class TaskThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Task task = requestQueue.take();
					if (task.status.state == task.status.ViewModified) {
						if (task.myFileList != null) {
							for (MyFile myFile : task.myFileList) {
								myFile.task = task;
								fileHandlers.pushMyFile(myFile);
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
						MyResponseHandler responseHandler = new MyResponseHandler();
						responseHandler.task = task;
						task.sendRequest();
						HttpUtils httpUtils = new HttpUtils();
						httpUtils.send(task.mHttpMethod, task.API, task.params, responseHandler);
						task.status.state = task.status.RequestSending;

					}
				} catch (Exception e) {
					StackTraceElement ste = new Throwable().getStackTrace()[1];
				}
			}
		}
	}

	void onMyFileUploaded(MyFile uploadedMyFile) {
		Task task = uploadedMyFile.task;
		if (task.myFileList == null) {
			return;
		}
		task.uploadeFileCount++;
		boolean isUploaded = true;
		if (task.uploadeFileCount >= task.myFileList.size()) {
			for (MyFile myFile : task.myFileList) {
				if (myFile.status.state != myFile.status.Uploaded) {
					isUploaded=false;
					break;
				}
			}
		}else{
			isUploaded=false;
		}
		
		if(isUploaded){
			task.status.state = task.status.FilesUploaded;
			requestQueue.offer(task);
		}
	}

	public class MyResponseHandler extends ResponseHandler<String> {
		public Task task;

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			if (task != null) {
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

	public LinkedBlockingQueue<Task> responseQueue = new LinkedBlockingQueue<Task>();
	public Handler mHandler = new Handler();
	public boolean handlerIsRunning = false;

	public void postHandler() {
		if (handlerIsRunning == false) {
			mHandler.post(mTaskRunnable);
		}
	}

	public Runnable mTaskRunnable = new Runnable() {
		@Override
		public void run() {
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
			}

		}
	};

	void logTask(Task task) {

	}
}
