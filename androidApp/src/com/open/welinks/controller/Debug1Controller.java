package com.open.welinks.controller;

import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.view.Debug1View;
import com.open.welinks.view.Debug1View.ControlProgress;
import com.open.welinks.view.Debug1View.Status;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

public class Debug1Controller {
	public Data data = Data.getInstance();
	public String tag = "LoginController";

	public Runnable animationRunnable;

	public Context context;
	public Debug1View thisView;
	public Debug1Controller thisController;
	public Activity thisActivity;

	public Debug1Controller(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initializeListeners() {

	}

	public void bindEvent() {
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			thisActivity.finish();
		}
		boolean flag = false;
		return flag;
	}

	int targetPercentage = 30;
	int index = 0;

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.debug2_1) {
			Log.d(tag, "Debug1Activity debug2_1");
			thisView.titleControlProgress.moveTo(targetPercentage);
			targetPercentage = (targetPercentage + 30) % 100;
		}
		if (item.getItemId() == R.id.debug2_2) {
			Log.d(tag, "Debug1Activity debug2_2");

			ControlProgress controlProgress = thisView.transportingList.transportingItems.get(index).controlProgress;
			if (controlProgress.percentage > 50) {
				controlProgress.moveTo(0);
			} else {
				controlProgress.moveTo(100);
			}
			index = (index + 1) % 10;
		}

		if (item.getItemId() == R.id.debug2_3) {
			Log.d(tag, "Debug1Activity debug2_3");
			thisView.titleControlProgress.setTo(targetPercentage);
			targetPercentage = (targetPercentage + 30) % 100;
		}
		return true;
	}

}
