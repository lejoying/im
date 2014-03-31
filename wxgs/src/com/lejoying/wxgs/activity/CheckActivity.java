package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CheckActivity extends Activity implements OnClickListener {
	private EditText check;
	private Button confirm;
	private TextView loginpg2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_check);
		SysApplication.getInstance().addActivity(this);
		getWindow().setBackgroundDrawableResource(R.drawable.login_bk);
		check = (EditText) findViewById(R.id.check);
		confirm = (Button) findViewById(R.id.confirm);
		loginpg2 = (TextView) findViewById(R.id.loginpg2);

		loginpg2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		check.setBackgroundColor(Color.WHITE);

		loginpg2.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.loginpg2:
			startActivity(new Intent(CheckActivity.this, LoginActivity.class));
			finish();
			break;

		case R.id.confirm:
			startActivity(new Intent(CheckActivity.this, MainActivity.class));
			finish();
			break;
		}
	}

}
