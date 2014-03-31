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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity implements OnClickListener {
	private TextView loginpg;
	private EditText rgNumber1, rgNumber2, rgPasswd;
	private Button next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		SysApplication.getInstance().addActivity(this);
		getWindow().setBackgroundDrawableResource(R.drawable.login_bk);
		loginpg = (TextView) findViewById(R.id.loginpg);
		rgNumber1 = (EditText) findViewById(R.id.rgNumber1);
		rgNumber2 = (EditText) findViewById(R.id.rgNumber2);
		rgPasswd = (EditText) findViewById(R.id.rgPasswd);
		next = (Button) findViewById(R.id.next);

		loginpg.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		rgNumber1.setBackgroundColor(Color.WHITE);
		rgNumber2.setBackgroundColor(Color.WHITE);
		rgPasswd.setBackgroundColor(Color.WHITE);

		loginpg.setOnClickListener(this);
		next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.loginpg:
			startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
			finish();
			break;

		case R.id.next:
			startActivity(new Intent(RegisterActivity.this, CheckActivity.class));
			finish();
			break;
		}
	}

}
