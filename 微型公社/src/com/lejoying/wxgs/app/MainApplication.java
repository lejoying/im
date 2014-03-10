package com.lejoying.wxgs.app;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

import com.lejoying.wxgs.activity.BaseActivity;
import com.lejoying.wxgs.app.data.Config;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.handler.DataHandler;
import com.lejoying.wxgs.handler.NetworkHandler;
import com.lejoying.wxgs.view.widget.CircleMenu;

public class MainApplication extends Application {

	public static final String APP_START = "com.lejoying.wxgs.app.start";

	static MainApplication mMainApplication;

	public Data data;
	public Config config;

	public Handler UIHandler;
	public DataHandler dataHandler;
	public NetworkHandler networkHandler;

	public static String currentTAG;
	public static BaseActivity currentActivity;

	public static MainApplication getMainApplication() {
		return mMainApplication;
	}

	@Override
	public void onCreate() {
		if (isInMainProcess()) {
			initMainApplication();
		}
		super.onCreate();
	}

	public void initMainApplication() {
		mMainApplication = this;
		// init circle menu
		CircleMenu.create(this);

		// init handler
		UIHandler = new Handler();
		dataHandler = new DataHandler();
		dataHandler.initialize(this);
		networkHandler = new NetworkHandler();
		networkHandler.initialize(this, 5);

		// init data and config
		data = new Data();
		config = new Config();
	}

	public boolean isInMainProcess() {
		return isInProgress(getPackageName());
	}

	public boolean isInProgress(String packageName) {
		if (packageName == null || packageName.equals("")) {
			return false;
		}
		ActivityManager mActivityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager
				.getRunningAppProcesses();
		if (runningAppProcessInfos != null) {
			int pid = Process.myPid();
			for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfos) {
				if (appProcess.pid == pid) {
					return packageName.equals(appProcess.processName);
				}
			}
		}
		return false;
	}

}
