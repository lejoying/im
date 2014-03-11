package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;

import com.lejoying.wxgs.activity.page.BasePage;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;

public abstract class BaseActivity extends Activity {

	ActivityManager mActivityManager;

	public List<BasePage> mBackStack = new ArrayList<BasePage>();

	public BasePage mContentPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivityManager = (ActivityManager) (getSystemService(Context.ACTIVITY_SERVICE));
		super.onCreate(savedInstanceState);
	}

	public void addToBackStack(BasePage basePage) {
		if (basePage != null) {
			mBackStack.add(basePage);
			basePage.hide(BasePage.ANIMATION_DIRECTION_TOP);
		}
	}

	public void changeContentPage(BasePage basePage, int direction) {
		if (basePage != null) {
			basePage.show(direction);
			mContentPage = basePage;
		}
	}

	@Override
	protected void onResume() {
		MainApplication.currentActivity = this;
		super.onResume();
	}

	@Override
	public void finish() {
		if (mBackStack.size() != 0) {
		} else {
			super.finish();
		}
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
