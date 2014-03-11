package com.lejoying.wxgs.handler;

import java.util.LinkedList;
import java.util.Queue;

import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.Data;

public class DataHandler {

	MainApplication app;

	DataHandlerWorkThread mWorkThread;

	Queue<Modification> mQueue;

	public DataHandler() {

	}

	public void initialize(MainApplication app) {
		this.app = app;
		mQueue = new LinkedList<DataHandler.Modification>();
		mWorkThread = new DataHandlerWorkThread();
		mWorkThread.start();
	}

	public synchronized void exclude(Modification modification) {
		mQueue.offer(modification);
		notify();
	}

	public synchronized Modification getExclude() throws InterruptedException {
		if (mQueue.size() == 0) {
			wait();
		}
		return mQueue.poll();
	}

	public static abstract class Modification implements Runnable {
		public abstract void modifyData(Data data);

		public void modifyUI() {
			// TODO Auto-generated method stub
		}

		@Override
		public final void run() {
			modifyUI();
		}
	}

	class DataHandlerWorkThread extends Thread {
		boolean interrupt;

		@Override
		public void run() {
			while (!interrupt) {
				Modification modification = null;
				try {
					while ((modification = getExclude()) == null)
						;
				} catch (InterruptedException e) {
					return;
				}
				modification.modifyData(app.data);
				app.UIHandler.post(modification);
			}
		}
	}

}
