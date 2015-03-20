package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.model.UpdateManager;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.view.NearbyView;

public class NearbyActivity extends Activity {

	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

	public TaskManageHolder taskManageHolder;

	public Parser parser = Parser.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		parser.initialize(this);
		parser.check();

		taskManageHolder = TaskManageHolder.getInstance();
		taskManageHolder.initialize();
		initImageLoader(getApplicationContext());
		BaseDataUtils.initBaseData(this);
		Constant.init();
		UpdateManager manager = new UpdateManager(this);
		manager.checkUpdate();

		thisActivity = this;
		thisView = new NearbyView(thisActivity);
		thisController = new NearbyController(thisActivity);

		thisView.thisController = thisController;
		thisController.thisView = thisView;

		thisController.onCreate();
		thisView.initView();
		thisView.mapView.onCreate(savedInstanceState);
		thisController.initData();
		thisView.fillData();
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		thisController.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		thisView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		thisView.onPause();
		super.onPause();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return thisController.onKeyUp(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		thisView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		thisView.onDestroy();
		super.onDestroy();
	}
}
