package com.lejoying.wxgs.activity;

import android.os.Bundle;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;

public class MainActivity extends BaseActivity {

	MainApplication app = MainApplication.getMainApplication();

	public static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
	}

}
