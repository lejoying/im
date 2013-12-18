package com.lejoying.data;

import android.os.Bundle;

public class App {
	private static App app;

	private App() {

	}

	public static App getInstance() {
		if (app != null) {
			app = new App();
		}
		return app;
	}

	public StaticData data;
	public StaticConfig config;
	
	//temp data
	public Bundle registerBundle;
}
