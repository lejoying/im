package com.lejoying.mc.data.handler;

import java.util.ArrayList;

import android.os.Handler;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.StaticData;

public class DataHandler1 {
	App app;
//	public StaticData data;

	public Handler mUIThreadHandler;

	public void initailize(App app) {
		this.app = app;
//		this.data = app.data;
		this.mUIThreadHandler = new Handler();
	}

	public void modifyData(Modification modification) {
		modifyData(modification, null);
	}

	public void modifyData(Modification modification, UIModification mUIModification) {
		Operation operation = new Operation();
		operation.mModification = modification;
		operation.mUIModification = mUIModification;
		mQueue.add(operation);
		handleData();
	}

	boolean isHandling = false;

	private void handleData() {
		if (mQueue.size() == 0 || isHandling) {
			return;
		}
		isHandling = true;
		final Operation operation = mQueue.get(0);
		mQueue.remove(0);

		new Thread() {
			public void run() {
				handleOperation(operation);
				isHandling = false;
				handleData();
			}
		}.start();
	}
	
	void handleOperation(final Operation operation){
		operation.mModification.modify(this.app.data);
		if(operation.mUIModification!=null){
			mUIThreadHandler.post(new Runnable() {
				public void run() {
					operation.mUIModification.modifyUI();
				}
			});
		}
	}

	public class Operation {
		public Modification mModification;
		public UIModification mUIModification;
	}

	ArrayList<Operation> mQueue = new ArrayList<Operation>();

	public interface Modification {
		public void modify(StaticData data);
	}

	public interface UIModification {
		public void modifyUI();
	}
}
