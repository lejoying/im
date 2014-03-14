package com.lejoying.wxgs.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;

public class MainActivity extends BaseActivity {

	MainApplication app = MainApplication.getMainApplication();

	public static final String TAG = "MainActivity";

	public static final String MODE_LOGIN = "login";
	public static final String MODE_MAIN = "main";

	String mode = "";

	FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_main);
		mFragmentManager = getSupportFragmentManager();

		switchMode();

		super.onCreate(savedInstanceState);
	}

	void switchMode() {
		if (app.data.user.phone.equals("")
				|| app.data.user.accessKey.equals("")) {
			mode = MODE_LOGIN;
		} else {
			mode = MODE_MAIN;
		}
	}

}
