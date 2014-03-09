package com.lejoying.wxgs.activity;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;

import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.view.widget.CircleMenu;

public abstract class BaseActivity extends Activity {

	public abstract void initView();

	public abstract void initEvent();

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
