package com.lejoying.mc.data;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;

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

	private App() {
		initData();
		initConfig();
		dataHandler = new DataHandler(this);
		if (Environment.getExternalStorageDirectory() != null) {
			sdcardAppFolder = new File(
					Environment.getExternalStorageDirectory(), "lejoying");
			if (!sdcardAppFolder.exists()) {
				sdcardAppFolder.mkdirs();
			}
			sdcardImageFolder = new File(sdcardAppFolder, "image");
			if (!sdcardImageFolder.exists()) {
				sdcardImageFolder.mkdirs();
			}
		}
	}

	public static App getInstance() {
		if (app == null) {
			app = new App();
		}
		return app;
	}

	public void initConfig() {
		config = new StaticConfig();
	}

	public void initData() {
		data = new StaticData();
	}

	public boolean isDataChanged;

	public StaticData data;
	public StaticConfig config;

	public DataHandler dataHandler;
	// temp data
	public Bundle registerBundle;
	public Friend tempFriend;
	public final int SHOW_SELF = 1;
	public final int SHOW_FRIEND = 2;
	public final int SHOW_TEMPFRIEND = 3;
	public int businessCardStatus;
	public Friend nowChatFriend;
	public Map<String, Bitmap> heads = new Hashtable<String, Bitmap>();
	public File sdcardAppFolder;
	public File sdcardImageFolder;
}
