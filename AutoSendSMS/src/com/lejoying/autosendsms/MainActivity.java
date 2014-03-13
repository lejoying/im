package com.lejoying.autosendsms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	Button btn_start;
	Button btn_stop;
	Button btn_startSend;
	Button btn_stopSend;
	Button btn_clearqueue;
	TextView tv_nowsending;
	TextView tv_list;
	TextView tv_send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		startService(new Intent(this, SMSService.class));

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public void initView() {
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_startSend = (Button) findViewById(R.id.btn_startsend);
		btn_stopSend = (Button) findViewById(R.id.btn_stopsend);
		btn_clearqueue = (Button) findViewById(R.id.btn_clearqueue);
		tv_nowsending = (TextView) findViewById(R.id.tv_nowsending);
		tv_list = (TextView) findViewById(R.id.tv_list);
		tv_send = (TextView) findViewById(R.id.tv_send);
		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_startSend.setOnClickListener(this);
		btn_stopSend.setOnClickListener(this);
		btn_clearqueue.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start:
			start();
			break;
		case R.id.btn_stop:
			stop();
			break;
		case R.id.btn_startsend:
			startSend();
			break;
		case R.id.btn_stopsend:
			stopSend();
			break;
		case R.id.btn_clearqueue:
			clearQueue();
			break;
		default:
			break;
		}
	}
}
