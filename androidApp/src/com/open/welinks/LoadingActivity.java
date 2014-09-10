package com.open.welinks;

import com.open.welinks.controller.LoginController;
import com.open.welinks.controller.TestHttpLongPull;
import com.open.welinks.controller.TestMultipartUpload;
import com.open.welinks.model.Data;
import com.open.welinks.view.LoginView;
import com.open.welinks.view.LoginView.Status;
import com.open.welinks.view.ViewManage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LoadingActivity extends Activity {
	public Data data = Data.getInstance();
	public String tag = "LoginActivity";

	public Context context;
	public LoginView thisView;
	public LoginController thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thisActivity = this;
		thisActivity.setContentView(R.layout.activity_login);
		// // linkViewController();
		//
		// if (thisView.status == Status.welcome) {
		// thisView.status = Status.welcome;
		// } else {
		// thisView.status = Status.start;
		// }
	}

	@Override
	public void onResume() {
		super.onResume();
		// thisController.onResume();
		data.localStatus.thisActivityName = "LoadingActivity";
		startMain();

	}

	public void startMain() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startActivity(new Intent(thisActivity, MainActivity.class));
				thisActivity.finish();
			}
		}).start();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new LoginView(thisActivity);
		this.thisController = new LoginController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		viewManager.loginView = this.thisView;

		thisView.initView();
		thisController.onCreate();
		thisController.initializeListeners();
		thisController.bindEvent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_debug_1, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.debug1_1) {
			Log.d(tag, "debug1.1");
			startActivity(new Intent(LoadingActivity.this, Debug1Activity.class));
		} else if (item.getItemId() == R.id.debug1_0) {
			Log.d(tag, "debug1.1");
			startActivity(new Intent(LoadingActivity.this, Debug1Activity.class));
		} else if (item.getItemId() == R.id.csubmenu2_1) {
			Log.d(tag, "csubmenu2_1");
			startActivity(new Intent(LoadingActivity.this, ImagesDirectoryActivity.class));
		} else if (item.getItemId() == R.id.csubmenu2_2) {
			Log.d(tag, "csubmenu2_2");
			startActivity(new Intent(LoadingActivity.this, TestMultipartUpload.class));
		} else if (item.getItemId() == R.id.csubmenu2_3) {
			Log.d(tag, "csubmenu2_3");
			startActivity(new Intent(LoadingActivity.this, TestHttpLongPull.class));
		} else if (item.getItemId() == R.id.csubmenu2_4) {
			Log.d(tag, "csubmenu2_4");
			startActivity(new Intent(LoadingActivity.this, DownloadOssFileActivity.class));
		} else if (item.getItemId() == R.id.debug1_2) {
			thisView.showCircleSettingDialog();
		} else if (item.getItemId() == R.id.debug1_4) {
			thisView.showInputDialog();
		} else if (item.getItemId() == R.id.debug1_3) {
			Log.d(tag, "debug1_3");
			startActivity(new Intent(LoadingActivity.this, MainActivity.class));
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		// thisController.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return thisController.onKeyDown(keyCode, event);
	}

}