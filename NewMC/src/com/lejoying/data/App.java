package com.lejoying.data;

import java.util.ArrayList;
import java.util.Hashtable;

import android.os.Bundle;

public class App {
	private static App app;

	private App() {
		// init config
		config = new StaticConfig();
		// init data
		data = new StaticData();
		data.user = new User();
		data.circles = new ArrayList<Circle>();
		data.friends = new Hashtable<String, Friend>();
		data.lastChatFriends = new ArrayList<String>();
		data.notReadCountMap = new Hashtable<String, Integer>();
	}

	public static App getInstance() {
		if (app == null) {
			app = new App();
		}
		return app;
	}

	public StaticData data;
	public StaticConfig config;

	// temp data
	public Bundle registerBundle;
}
