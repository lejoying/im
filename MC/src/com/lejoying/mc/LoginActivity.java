package com.lejoying.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// 启动activity时自动弹出软键盘
		 getWindow().setSoftInputMode(
		 WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public void login(View v) {
		Intent intent = new Intent(this,MessagesActivity.class);
		startActivity(intent);
		finish();
	}

	public void register(View v) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

}
