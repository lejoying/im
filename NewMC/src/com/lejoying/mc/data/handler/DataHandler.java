package com.lejoying.mc.data.handler;

import java.util.LinkedList;
import java.util.Queue;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;

public class DataHandler {

	App app;

	public void initialize(App app) {
		this.app = app;
	}

	public void modifyData(Modification modification) {
		modifyData(modification, null);
	}

	public void modifyData(Modification modification,
			UIModification mUIModification) {
		Operation operation = new Operation();
		operation.mModification = modification;
		operation.mUIModification = mUIModification;
		mQueue.offer(operation);
		handleData();
	}

	boolean isHandling = false;

	private void handleData() {
		if (mQueue.size() == 0 || isHandling) {
			return;
		}
		isHandling = true;
		final Operation operation = mQueue.poll();

		new Thread() {
			public void run() {
				app.isDataChanged = true;
				handleOperation(operation);
				isHandling = false;
				handleData();
			}
		}.start();
	}

	private void handleOperation(final Operation operation) {
		app.isDataChanged = true;
		operation.mModification.modify(app.data);
		if (operation.mUIModification != null) {
			app.mUIThreadHandler.post(new Runnable() {
				public void run() {
					operation.mUIModification.modifyUI();
				}
			});
		}
	}

	private class Operation {
		public Modification mModification;
		public UIModification mUIModification;
	}

	Queue<Operation> mQueue = new LinkedList<Operation>();

	public interface Modification {
		public void modify(Data data);
	}

	public interface UIModification {
		public void modifyUI();
	}
}
