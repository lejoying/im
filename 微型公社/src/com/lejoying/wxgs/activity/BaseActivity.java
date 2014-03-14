package com.lejoying.wxgs.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;

public abstract class BaseActivity extends FragmentActivity {

	ActivityManager mActivityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivityManager = (ActivityManager) (getSystemService(Context.ACTIVITY_SERVICE));
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		MainApplication.currentActivity = this;
		Alert.recover();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (isLeave()) {
			onLeave();
		}
		super.onPause();
	}

	public void onLeave() {
		CircleMenu.hideImmediately(true);
		Alert.hide();
	}

	public boolean isLeave() {
		boolean flag = false;
		List<RunningTaskInfo> runningTaskInfos = mActivityManager
				.getRunningTasks(1);

		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			flag = !f.getPackageName().equals(getPackageName());
		}
		return flag;
	}

}
