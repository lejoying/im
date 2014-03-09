package com.lejoying.wxgs.handler;

import com.lejoying.wxgs.app.MainApplication;

public class FileHandler {
	MainApplication app;

	public static final int WORKTHREADCOUNT_MIN = 1;
	public static final int WORKTHREADCOUNT_MAX = 10;

	public int mWorkThreadCount;

	public FileHandler() {

	}

	public void initialize(MainApplication app, int workThreadCount) {
		this.app = app;
	}

	class FileHandlerWorkThread extends Thread {

		public int id;

		boolean interrupt;

		public FileHandlerWorkThread(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			while (!interrupt) {

			}
		}
	}
}
