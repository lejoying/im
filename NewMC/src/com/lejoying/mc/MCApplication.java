package com.lejoying.mc;

import android.app.Application;

import com.lejoying.mc.data.App;

public class MCApplication extends Application {
	App app = App.getInstance();

	@Override
	public void onCreate() {
		super.onCreate();
		app.initialize(this);
	}

}
