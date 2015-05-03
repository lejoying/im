package com.open.welinks.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
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

		initImageLoader(this);

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
		super.onLowMemory();
		// data = parser.parse();
	}

	public static void initImageLoader(Context context) {
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024)
					.tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
			ImageLoader.getInstance().init(config);
		}
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
}
