package com.lejoying.mc.data;

import java.util.ArrayList;
import java.util.Hashtable;

import android.os.Bundle;

public class App {
	private static App app;

	private App() {
		initData();
		initConfig();
	}

	public static App getInstance() {
		if (app == null) {
			app = new App();
		}
		return app;
	}

	public void cleanData() {
		if (app != null) {
			initData();
		}
	}

	public void cleanConfig() {
		if (app != null) {
			initConfig();
		}
	}

	void initConfig() {
		config = new StaticConfig();
	}

	void initData() {
		data = new StaticData();
		data.user = new User();
		data.circles = new ArrayList<Circle>();
		data.friends = new Hashtable<String, Friend>();
		data.lastChatFriends = new ArrayList<String>();
		data.newFriends = new ArrayList<Friend>();
	}

	public boolean isDataChanged;

	public StaticData data;
	public StaticConfig config;

	// temp data
	public Bundle registerBundle;
	public Friend tempFriend;
	public final int SHOW_SELF = 1;
	public final int SHOW_FRIEND = 2;
	public final int SHOW_TEMPFRIEND = 3;
	public int businessCardStatus;
}
