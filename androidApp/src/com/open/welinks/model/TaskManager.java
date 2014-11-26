package com.open.welinks.model;

import java.util.Queue;

import android.os.Handler;
import android.os.SystemClock;

public class TaskManager {

	public static TaskManager instance;

	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	public void startLoop(){
		new TaskThread().start();
		mHandler.post(mTaskRunnable);
	}

	public Queue<Task> taskQueue;

	public void pushTask(Task task) {
		long currentTime = SystemClock.uptimeMillis();
		task.startTime = currentTime;
		task.modifyData();
		task.status.state = task.status.DataModified;
		task.modifyView();
		task.status.state = task.status.ViewModified;
	}
	
	class TaskThread extends Thread {
		@Override
		public void run() {
			while (true) {
				
				
				
			}
		}
	}
	
	public Handler mHandler;
	public Runnable mTaskRunnable = new Runnable() {
		@Override
		public void run() {
			
			
			
			mHandler.post(mTaskRunnable);
		}
	};

}
