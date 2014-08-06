package com.lejoying.wxgs.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.os.StrictMode;

import com.lejoying.wxgs.activity.BaseActivity;
import com.lejoying.wxgs.app.data.Configuration;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler;
import com.lejoying.wxgs.app.handler.AsyncHandler;
import com.lejoying.wxgs.app.handler.EventHandler;
import com.lejoying.wxgs.app.handler.LocationHandler;
import com.lejoying.wxgs.app.handler.NetworkHandler;
import com.lejoying.wxgs.app.handler.OSSFileHandler;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.utils.SHA1;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MainApplication extends Application implements
		Thread.UncaughtExceptionHandler {

	public static final String APP_DATA_PARSINGISCOMPLETE = "com.lejoying.wxgs.app.parsingiscomplete";

	static MainApplication mMainApplication;

	public Data data;
	public Configuration config;

	public Handler UIHandler;
	public DataHandler dataHandler;
	public NetworkHandler networkHandler;
	public OSSFileHandler fileHandler;
	public EventHandler eventHandler;
	public LocationHandler locationHandler;
	public AsyncHandler asyncHandler;

	public static String currentTAG;
	public static BaseActivity currentActivity;

	public SHA1 mSHA1;

	public File sdcardAppFolder;
	public File sdcardImageFolder;
	public File sdcardVoiceFolder;
	public File sdcardHeadImageFolder;
	public File sdcardBackImageFolder;
	public File sdcardThumbnailFolder;
	public File sdcardQRcodeFolder;

	public String sdcardStatus = "none";// "exist"
	public String networkStatus = "none";// "WIFI"|"mobile"

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
		fileHandler = new OSSFileHandler();
		fileHandler.initialize(this);
		eventHandler = new EventHandler();
		eventHandler.initialize(this);
		locationHandler = new LocationHandler();
		locationHandler.initialize(this);
		asyncHandler = new AsyncHandler();
		asyncHandler.initialized(10, UIHandler);

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
		if (data == null) {
			data = new Data();
		}
		// if (config == null) {
		// config = new Configuration();
		// }
		// data = new Data();
		// if (!config.lastLoginPhone.equals("")) {
		// dataHandler.exclude(new Modification() {
		// @Override
		// public void modifyData(Data data) {
		// try {
		// Data localData = (Data) StreamParser
		// .parseToObject(openFileInput(config.lastLoginPhone));
		// if (localData != null) {
		// data.user = localData.user;
		// data.circles = localData.circles;
		// data.circlesMap = localData.circlesMap;
		// data.friends = localData.friends;
		// data.groups = localData.groups;
		// data.currentSquare = localData.currentSquare;
		// data.squareFlags = localData.squareFlags;
		// data.squareMessages = localData.squareMessages;
		// data.squareMessagesClassify = localData.squareMessagesClassify;
		// data.squareCollects = localData.squareCollects;
		// data.squareMessagesMap = localData.squareMessagesMap;
		// data.groupsMap = localData.groupsMap;
		// data.groupFriends = localData.groupFriends;
		// data.lastChatFriends = localData.lastChatFriends;
		// data.newFriends = localData.newFriends;
		// }
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// sendBroadcast(new Intent(APP_DATA_PARSINGISCOMPLETE));
		// }
		// });
		// } else {
		// sendBroadcast(new Intent(APP_DATA_PARSINGISCOMPLETE));
		// }

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			sdcardStatus = "exist";
		} else {
			sdcardStatus = "none";
		}

		if (sdcardStatus == "exist") {
			sdcardAppFolder = new File(
					Environment.getExternalStorageDirectory(), "lejoying");
			if (!sdcardAppFolder.exists()) {
				sdcardAppFolder.mkdirs();
			}
			sdcardImageFolder = new File(sdcardAppFolder, "image");
			if (!sdcardImageFolder.exists()) {
				sdcardImageFolder.mkdirs();
			}
			sdcardVoiceFolder = new File(sdcardAppFolder, "voice");
			if (!sdcardVoiceFolder.exists()) {
				sdcardVoiceFolder.mkdirs();
			}
			sdcardHeadImageFolder = new File(sdcardImageFolder, "head");
			if (!sdcardHeadImageFolder.exists()) {
				sdcardHeadImageFolder.mkdir();
			}
			sdcardBackImageFolder = new File(sdcardImageFolder, "background");
			if (!sdcardBackImageFolder.exists()) {
				sdcardBackImageFolder.mkdir();
			}
			sdcardThumbnailFolder = new File(sdcardImageFolder, "thumbnail");
			if (!sdcardThumbnailFolder.exists()) {
				sdcardThumbnailFolder.mkdir();
			}
			sdcardQRcodeFolder = new File(sdcardImageFolder, "qrcode");
			if (!sdcardQRcodeFolder.exists()) {
				sdcardQRcodeFolder.mkdir();
			}
		}
		if (Config.DEVELOPER_MODE
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}
		initImageLoader(getApplicationContext());
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
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

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "wxgsbugs.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			String ex = arg1.getStackTrace().toString();
			byte[] buffer = ex.getBytes();
			try {
				fileOutputStream.write(buffer, 0, buffer.length);
			} catch (IOException e) {
				// e.printStackTrace();
			} finally {
				try {
					fileOutputStream.flush();
				} catch (IOException e) {
				}
				try {
					fileOutputStream.close();
				} catch (IOException e) {
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}