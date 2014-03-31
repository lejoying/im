package com.lejoying.wxgs.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class SendActivity extends Activity implements OnClickListener {
	private Button send;
	private ImageView back;
	private EditText send_contant;
	private CheckBox checkbox1,checkbox2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		SysApplication.getInstance().addActivity(this);
		send = (Button) findViewById(R.id.send);
		back = (ImageView) findViewById(R.id.send_Back);
		send_contant = (EditText) findViewById(R.id.send_content);
		checkbox1=(CheckBox) findViewById(R.id.checkBox1);
		checkbox2=(CheckBox) findViewById(R.id.checkBox2);
		send.setOnClickListener(this);
		back.setOnClickListener(this);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 1000);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.send:
			startActivity(new Intent(SendActivity.this, MainActivity.class));
			finish();
			break;

		case R.id.send_Back:
			finish();
			break;
		}
	}

}
