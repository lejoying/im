package com.open.welinks.model;

import android.app.Application;
import android.content.Intent;

import com.open.welinks.service.ExceptionService;

public class MainApplication extends Application {

	public static Data data;
	public Parser parser;

	public TaskManageHolder taskManageHolder;

	@Override
	public void onCreate() {
		super.onCreate();

		taskManageHolder = TaskManageHolder.getInstance();
		taskManageHolder.isInitialized = false;
		taskManageHolder.initialize(this);

		Parser parser = Parser.getInstance();
		parser.initialize(this);

		ExceptionService service = new ExceptionService();
		Intent intent = new Intent(getApplicationContext(), ExceptionService.class);
		startService(intent);
		ExceptionHandler handler = ExceptionHandler.getInstance();
		handler.init(getApplicationContext(), service);
		Thread.setDefaultUncaughtExceptionHandler(handler);
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());

	}

	@Override
	public void onLowMemory() {
		data = parser.parse();
		super.onLowMemory();
	}

}
