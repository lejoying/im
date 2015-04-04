package com.open.welinks;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.open.lib.MyLog;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.LocalStatus.LocalData;
import com.open.welinks.model.Data.UserInformation;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.model.UpdateManager;
import com.open.welinks.service.ConnectionChangeReceiver;
import com.open.welinks.service.PushService;
import com.open.welinks.view.NearbyView;

public class NearbyActivity extends Activity {

	public String tag = "NearbyActivity";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();

	public static NearbyActivity instance;
	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public Parser parser = Parser.getInstance();

	public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public ConnectionChangeReceiver connectionChangeReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;

		thisActivity = this;
		thisView = new NearbyView(thisActivity);
		thisController = new NearbyController(thisActivity);

		thisView.thisController = thisController;
		thisController.thisView = thisView;

		if (data.userInformation == null) {
			String userInformationStr = parser.getFromRootForder("userInformation.js");
			data.userInformation = parser.gson.fromJson(userInformationStr, UserInformation.class);
		}
		try {
			User currentUser = data.userInformation.currentUser;
			if (!"".equals(currentUser.phone) && !"".equals(currentUser.accessKey)) {

				if (data.localStatus.localData == null) {
					String localDataStr = parser.getFromUserForder(currentUser.phone, "localData.js");
					if (localDataStr == null || "".equals(localDataStr)) {
						data.localStatus.localData = data.localStatus.new LocalData();
					} else {
						data.localStatus.localData = parser.gson.fromJson(localDataStr, LocalData.class);
					}
					if (data.localStatus.localData == null) {
						data.localStatus.localData = data.localStatus.new LocalData();
					}
				}
			} else {
				data.localStatus.localData = data.localStatus.new LocalData();
			}
		} catch (Exception e) {
			data.localStatus.localData = data.localStatus.new LocalData();
		}

		thisController.onCreate(true);
		thisView.initView();
		thisView.mapView.onCreate(savedInstanceState);
		thisController.initializeListeners();
		thisController.initData();
		thisController.bindEvent();
		thisView.fillData();

		taskManageHolder.dataHandler.preparingData();

		startPushService();

		UpdateManager manager = new UpdateManager(this);
		manager.checkUpdate();

		connectionChangeReceiver = new ConnectionChangeReceiver();
		IntentFilter filter = new IntentFilter(CONNECTIVITY_ACTION);
		thisActivity.registerReceiver(connectionChangeReceiver, filter);
	}

	public void startPushService() {
		try {
			if (data.userInformation == null) {
				String userInformationStr = parser.getFromRootForder("userInformation.js");
				data.userInformation = parser.gson.fromJson(userInformationStr, UserInformation.class);
			}
			User currentUser = data.userInformation.currentUser;
			if (!"".equals(currentUser.phone) && !"".equals(currentUser.accessKey)) {
				Intent service = new Intent(this, PushService.class);
				PushService.isRunning = false;
				Log.e(tag, "* startPushService *check data");

				data = parser.check();

				service.putExtra("phone", currentUser.phone);
				service.putExtra("accessKey", currentUser.accessKey);
				service.putExtra("operation", true);
				startService(service);
			}
		} catch (Exception e) {
		}
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
		// thisActivity.finish();
		// thisActivity.startActivity(new Intent(thisActivity, LoginActivity.class));
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
