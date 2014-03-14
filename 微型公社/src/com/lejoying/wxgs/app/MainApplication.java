package com.lejoying.wxgs.app;

import java.io.FileNotFoundException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Process;

import com.lejoying.wxgs.activity.BaseActivity;
import com.lejoying.wxgs.app.data.Config;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler;
import com.lejoying.wxgs.app.parser.StreamParser;

public class MainApplication extends Application {

	public static final String APP_DATA_PARSINGISCOMPLETE = "com.lejoying.wxgs.app.parsingiscomplete";

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
		if (isInMainProcess() && mMainApplication == null) {
			initMainApplication();
		}
		super.onCreate();
	}

	public void initMainApplication() {
		mMainApplication = this;

		// init handler
		UIHandler = new Handler();
		dataHandler = new DataHandler();
		dataHandler.initialize(this);
		networkHandler = new NetworkHandler(5);

		// init data and config
		try {
			config = (Config) StreamParser
					.parseToObject(openFileInput("config"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (config == null) {
			config = new Config();
		}
		data = new Data();
		if (!config.lastLoginPhone.equals("")) {
			dataHandler.exclude(new Modification() {
				@Override
				public void modifyData(Data data) {
					try {
						Data localData = (Data) StreamParser
								.parseToObject(openFileInput(config.lastLoginPhone));
						if (localData != null) {
							data.user = localData.user;
							data.circles = localData.circles;
							data.friends = localData.friends;
							data.groups = localData.groups;
							data.groupFriends = localData.groupFriends;
							data.lastChatFriends = localData.lastChatFriends;
							data.newFriends = localData.newFriends;
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendBroadcast(new Intent(APP_DATA_PARSINGISCOMPLETE));
				}
			});
		} else {
			sendBroadcast(new Intent(APP_DATA_PARSINGISCOMPLETE));
		}
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

	// override

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		// super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerComponentCallbacks(ComponentCallbacks callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterComponentCallbacks(ComponentCallbacks callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerOnProvideAssistDataListener(
			OnProvideAssistDataListener callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterOnProvideAssistDataListener(
			OnProvideAssistDataListener callback) {
		// TODO Auto-generated method stub
	}

}
