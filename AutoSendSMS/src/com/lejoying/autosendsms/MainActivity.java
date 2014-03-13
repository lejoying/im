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

	StatusReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SMSService.ACTION);
		mReceiver = new StatusReceiver();
		registerReceiver(mReceiver, intentFilter);

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
			Intent serviceStart = new Intent(this, SMSService.class);
			serviceStart.putExtra("operation", "start");
			startService(serviceStart);
			start();
			break;
		case R.id.btn_stop:
			Intent serviceStop = new Intent(this, SMSService.class);
			serviceStop.putExtra("operation", "stop");
			startService(serviceStop);
			stop();
			break;
		case R.id.btn_startsend:
			Intent serviceStartSend = new Intent(this, SMSService.class);
			serviceStartSend.putExtra("operation", "startSend");
			startService(serviceStartSend);
			startSend();
			break;
		case R.id.btn_stopsend:
			Intent serviceStopSend = new Intent(this, SMSService.class);
			serviceStopSend.putExtra("operation", "stopSend");
			startService(serviceStopSend);
			stopSend();
			break;
		case R.id.btn_clearqueue:
			Intent serviceClear = new Intent(this, SMSService.class);
			serviceClear.putExtra("operation", "clear");
			startService(serviceClear);
			break;
		default:
			break;
		}
	}

	public void start() {
		btn_start.setClickable(false);
		btn_start.setTextColor(Color.GRAY);
		btn_stop.setClickable(true);
		btn_stop.setTextColor(Color.BLACK);
	}

	public void stop() {
		btn_start.setClickable(true);
		btn_start.setTextColor(Color.BLACK);
		btn_stop.setClickable(false);
		btn_stop.setTextColor(Color.GRAY);
	}

	public void startSend() {
		btn_startSend.setClickable(false);
		btn_startSend.setTextColor(Color.GRAY);
		btn_stopSend.setClickable(true);
		btn_stopSend.setTextColor(Color.BLACK);
	}

	public void stopSend() {
		btn_startSend.setClickable(true);
		btn_startSend.setTextColor(Color.BLACK);
		btn_stopSend.setClickable(false);
		btn_stopSend.setTextColor(Color.GRAY);
	}

	class StatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			parseStatus(intent);
		}

		void parseStatus(Intent intent) {
			if (intent.getBooleanExtra("isConnection", false)) {
				start();
			} else {
				stop();
			}
			if (intent.getBooleanExtra("isStartSend", false)) {
				startSend();
			} else {
				stopSend();
			}

			String nowSending = intent.getStringExtra("nowSending");

			if (nowSending != null) {
				tv_nowsending.setText("正在发送到：" + nowSending);
			}

			int queueCount = intent.getIntExtra("queueSize", 0);
			tv_list.setText("队列中条数：" + String.valueOf(queueCount));

			int successCount = intent.getIntExtra("successCount", 0);
			tv_send.setText("已发送条数：" + String.valueOf(successCount));

		}
	}

}
