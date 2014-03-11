package com.lejoying.wxgs.activity;

import android.os.Bundle;

import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.service.PushService;

public class MainActivity extends BaseActivity {

	MainApplication app = MainApplication.getMainApplication();

	public static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initView();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		CircleMenu.show(this);
		CircleMenu.setPageName("密友圈");
		super.onResume();
	}

	@Override
	public void finish() {
		CircleMenu.hideImmediately(false);
		super.finish();
	}

	public void initView() {
		// TODO Auto-generated method stub
	}

	public void initEvent() {
		// TODO Auto-generated method stub
	}

}
