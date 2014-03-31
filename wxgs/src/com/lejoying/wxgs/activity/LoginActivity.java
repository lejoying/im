package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener {
	private EditText phoneN, passW;
	private Button submit;
	private TextView register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		SysApplication.getInstance().addActivity(this);
		getWindow().setBackgroundDrawableResource(R.drawable.login_bk);
		register = (TextView) findViewById(R.id.register);
		phoneN = (EditText) findViewById(R.id.phoneNumber);
		passW = (EditText) findViewById(R.id.passWord);
		submit = (Button) findViewById(R.id.submit);

		register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		phoneN.setBackgroundColor(Color.WHITE);
		passW.setBackgroundColor(Color.WHITE);

		submit.setOnClickListener(this);
		register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.register:
			startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			finish();
			break;

		case R.id.submit:
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			finish();
			break;
		}
	}

}
