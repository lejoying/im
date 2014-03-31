package com.lejoying.wxgs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;

public class SetActivity extends Activity implements OnClickListener {
	private ImageView set_back;
	private Button set_logout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		SysApplication.getInstance().addActivity(this);
		set_back = (ImageView) findViewById(R.id.set_back);
		set_logout = (Button) findViewById(R.id.set_logout);

		set_back.setOnClickListener(this);
		set_logout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.set_back:
			finish();
			break;

		case R.id.set_logout:
			Intent intent = new Intent(SetActivity.this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			SysApplication.getInstance().exit();
			break;
		}
	}

}
