package com.lejoying.mc;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.lejoying.mc.api.AccountManager;

public class LoadingActivity extends Activity {

	public static LoadingActivity instance;
	public AccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		instance = this;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void finish() {
		super.finish();
		instance = null;
	}
}
