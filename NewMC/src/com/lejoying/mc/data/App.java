package com.lejoying.mc.data;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.lejoying.mc.data.handler.DataHandler;
import com.lejoying.mc.data.handler.EventHandler;
import com.lejoying.mc.data.handler.FileHandler;
import com.lejoying.mc.data.handler.JSONHandler;
import com.lejoying.mc.data.handler.SDcardDataResolver;
import com.lejoying.mc.data.handler.ServerHandler;
import com.lejoying.utils.SHA1;

public class App {
	private static App app;

	public String addFriendFragment = "addFriendFragment";
	public String businessCardFragment = "businessCardFragment";
	public String chatFragment = "chatFragment";
	public String friendNotFoundFragment = "friendNotFoundFragment";
	public String friendsFragment = "friendsFragment";
	public String loginUserCodeFragment = "loginUserCodeFragment";
	public String modifyFragment = "modifyFragment";
	public String newFriendsFragment = "newFriendsFragment";
	public String registerCodeFragment = "registerCodeFragment";
	public String registerPassFragment = "registerPassFragment";
	public String registerPhoneFragment = "registerPhoneFragment";
	public String scanQRCodeFragment = "scanQRCodeFragment";
	public String searchFriendFragment = "searchFriendFragment";
	public String ShareFragment = "ShareFragment";

	public String mark;

	public String networkStatus = "none";// "WIFI"|"mobile"
	public String sdcardStatus = "none";// "exist"

	public SHA1 sha1;
	private App() {
		initData();
		initConfig();
		initHandler();
		initSDCard();
		sha1 = new SHA1();
	}

	public static App getInstance() {
		if (app == null) {
			app = new App();
		}
		return app;
	}

	public void initConfig() {
		config = new Config();
	}

	public Data data;

	public void initData() {
		data = new Data();
	}

	public DataHandler dataHandler;
	public EventHandler eventHandler;
	public JSONHandler mJSONHandler;
	public ServerHandler serverHandler;
	public FileHandler fileHandler;
	public SDcardDataResolver sDcardDataResolver;

	public Handler mUIThreadHandler;

	public void initHandler() {

		mUIThreadHandler = new Handler();
		dataHandler = new DataHandler();
		dataHandler.initailize(this);

		eventHandler = new EventHandler();
		eventHandler.initailize(this);

		mJSONHandler = new JSONHandler();
		mJSONHandler.initailize(this);

		serverHandler = new ServerHandler();
		serverHandler.initailize(this);

		fileHandler = new FileHandler();
		fileHandler.initailize(this);

		sDcardDataResolver = new SDcardDataResolver();
		sDcardDataResolver.initailize(this);
	}



	public File sdcardAppFolder;
	public File sdcardImageFolder;
	public File sdcardHeadImageFolder;
	public void initSDCard() {
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
			sdcardHeadImageFolder = new File(sdcardImageFolder, "head");
			if (!sdcardHeadImageFolder.exists()) {
				sdcardHeadImageFolder.mkdir();
			}
		}
	}

	public boolean isDataChanged;

	public Config config;

	public Context context;

	public final int SHOW_SELF = 1;
	public final int SHOW_FRIEND = 2;
	public final int SHOW_TEMPFRIEND = 3;
	public int businessCardStatus;
}
