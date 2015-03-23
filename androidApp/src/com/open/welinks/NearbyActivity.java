package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.open.lib.MyLog;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.DataHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.model.UpdateManager;
import com.open.welinks.service.ConnectionChangeReceiver;
import com.open.welinks.service.PushService;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.view.NearbyView;

public class NearbyActivity extends Activity {

	public String tag = "NearbyActivity";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();

	public static NearbyActivity instance;
	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

	public TaskManageHolder taskManageHolder;

	public Parser parser = Parser.getInstance();

	public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public ConnectionChangeReceiver connectionChangeReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;

		parser.initialize(this);
		parser.check();

		startPushService();

		taskManageHolder = TaskManageHolder.getInstance();
		taskManageHolder.initialize();
		taskManageHolder.viewManage.initialize(this);
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

		connectionChangeReceiver = new ConnectionChangeReceiver();
		IntentFilter filter = new IntentFilter(CONNECTIVITY_ACTION);
		thisActivity.registerReceiver(connectionChangeReceiver, filter);

		thisController.onCreate();
		thisView.initView();
		thisView.mapView.onCreate(savedInstanceState);
		thisController.initData();
		thisView.fillData();
	}

	public void startPushService() {
		Intent service = new Intent(this, PushService.class);
		PushService.isRunning = false;
		Log.e(tag, "* startPushService *check data");

		data = parser.check();

		service.putExtra("phone", data.userInformation.currentUser.phone);
		service.putExtra("accessKey", data.userInformation.currentUser.accessKey);
		service.putExtra("operation", true);
		startService(service);
	}

	public void exitApplication() {
		data = parser.check();
		data.userInformation.currentUser.phone = "";
		data.userInformation.currentUser.accessKey = "";
		data.userInformation.isModified = true;
		parser.save();
		thisActivity.stopService(new Intent(thisActivity, PushService.class));
		if (this.connectionChangeReceiver != null) {
			thisActivity.unregisterReceiver(this.connectionChangeReceiver);
			connectionChangeReceiver = null;
		}
		DataHandler.clearData();
		thisActivity.finish();
		thisActivity.startActivity(new Intent(thisActivity, LoginActivity.class));
	}

	public static void initImageLoader(Context context) {
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
			ImageLoader.getInstance().init(config);
		}
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
		parser.save();
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
	public void finish() {
		super.finish();
		if (this.connectionChangeReceiver != null) {
			thisActivity.unregisterReceiver(this.connectionChangeReceiver);
			connectionChangeReceiver = null;
		}
	}

	@Override
	protected void onDestroy() {
		thisView.onDestroy();
		super.onDestroy();
	}
}
