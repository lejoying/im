package com.lejoying.wxgs.app;

import java.io.FileNotFoundException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Process;

import com.lejoying.wxgs.activity.BaseActivity;
import com.lejoying.wxgs.app.data.Configuration;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.utils.SHA1;

public class MainApplication extends Application {

	public static final String APP_DATA_PARSINGISCOMPLETE = "com.lejoying.wxgs.app.parsingiscomplete";

	static MainApplication mMainApplication;

	public Data data;
	public Configuration config;

	public Handler UIHandler;
	public DataHandler dataHandler;
	public NetworkHandler networkHandler;

	public static String currentTAG;
	public static BaseActivity currentActivity;

	public SHA1 mSHA1;

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

		// initialize handler
		UIHandler = new Handler();
		dataHandler = new DataHandler();
		dataHandler.initialize(this);
		networkHandler = new NetworkHandler(5);

		// initialize tool
		mSHA1 = new SHA1();

		// initialize data and configuration
		try {
			config = (Configuration) StreamParser
					.parseToObject(openFileInput("config"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (config == null) {
			config = new Configuration();
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

}