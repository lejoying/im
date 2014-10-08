package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.open.welinks.controller.LoginController;
import com.open.welinks.controller.TestMultipartUpload;
import com.open.welinks.model.Data;
import com.open.welinks.view.LoginView;
import com.open.welinks.view.LoginView.Status;
import com.open.welinks.view.ViewManage;

public class LoginActivity extends Activity {
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

		linkViewController();

		if (thisView.status == Status.welcome) {
			thisView.status = Status.welcome;
		} else {
			thisView.status = Status.start;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		thisController.onResume();
		data.localStatus.thisActivityName = "LoginActivity";
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
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.menu_debug_1, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.debug1_1) {
			Log.d(tag, "debug1.1");
			startActivity(new Intent(LoginActivity.this, Debug1Activity.class));
		} else if (item.getItemId() == R.id.debug1_0) {
			Log.d(tag, "debug1.1");
			startActivity(new Intent(LoginActivity.this, Debug1Activity.class));
		} else if (item.getItemId() == R.id.csubmenu2_1) {
			Log.d(tag, "csubmenu2_1");
			startActivity(new Intent(LoginActivity.this, ImagesDirectoryActivity.class));
		} else if (item.getItemId() == R.id.csubmenu2_2) {
			Log.d(tag, "csubmenu2_2");
			startActivity(new Intent(LoginActivity.this, TestMultipartUpload.class));
		} else if (item.getItemId() == R.id.csubmenu2_3) {
			Log.d(tag, "csubmenu2_3");
			// startActivity(new Intent(LoginActivity.this, TestHttpLongPull.class));
		} else if (item.getItemId() == R.id.csubmenu2_4) {
			Log.d(tag, "csubmenu2_4");
			startActivity(new Intent(LoginActivity.this, DownloadOssFileActivity.class));
		} else if (item.getItemId() == R.id.debug1_2) {
			thisView.showCircleSettingDialog();
		} else if (item.getItemId() == R.id.debug1_4) {
			thisView.showInputDialog();
		} else if (item.getItemId() == R.id.debug1_3) {
			Log.d(tag, "debug1_3");
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		thisController.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		viewManager.loginView = null;
		thisController.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return thisController.onKeyDown(keyCode, event);
	}

}
