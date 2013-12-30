package com.lejoying.mc.data;

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
	public Friend nowChatFriend;
}
