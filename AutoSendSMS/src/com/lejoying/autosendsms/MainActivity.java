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
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	Button btn_start;
	Button btn_stop;
	Button btn_startSend;
	Button btn_stopSend;
	TextView tv_nowsending;
	TextView tv_list;
	TextView tv_send;

	SMSStatusReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SMSService.ACTION);
		mReceiver = new SMSStatusReceiver();
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
		tv_nowsending = (TextView) findViewById(R.id.tv_nowsending);
		tv_list = (TextView) findViewById(R.id.tv_list);
		tv_send = (TextView) findViewById(R.id.tv_send);
		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_startSend.setOnClickListener(this);
		btn_stopSend.setOnClickListener(this);

		if (SMSService.isStart) {
			start();
		} else {
			stop();
		}

		if (SMSService.isStartSend) {
			startSend();
		} else {
			stopSend();
		}
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
		default:
			break;
		}
	}

	public void start() {
		btn_start.setClickable(false);
		btn_start.setTextColor(Color.GRAY);
		btn_stop.setClickable(true);
		btn_stop.setTextColor(Color.BLACK);
		Intent service = new Intent(this, SMSService.class);
		service.putExtra("operation", "start");
		startService(service);
	}

	public void stop() {
		btn_start.setClickable(true);
		btn_start.setTextColor(Color.BLACK);
		btn_stop.setClickable(false);
		btn_stop.setTextColor(Color.GRAY);
		Intent service = new Intent(this, SMSService.class);
		service.putExtra("operation", "stop");
		startService(service);
	}

	public void startSend() {
		btn_startSend.setClickable(false);
		btn_startSend.setTextColor(Color.GRAY);
		btn_stopSend.setClickable(true);
		btn_stopSend.setTextColor(Color.BLACK);
		Intent service = new Intent(this, SMSService.class);
		service.putExtra("operation", "startSend");
		startService(service);
	}

	public void stopSend() {
		btn_startSend.setClickable(true);
		btn_startSend.setTextColor(Color.BLACK);
		btn_stopSend.setClickable(false);
		btn_stopSend.setTextColor(Color.GRAY);
		Intent service = new Intent(this, SMSService.class);
		service.putExtra("operation", "stopSend");
		startService(service);
	}

	class SMSStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String nowSending = intent.getStringExtra("nowSending");
			int queueCount = intent.getIntExtra("queueCount", 0);
			long sentCount = intent.getLongExtra("sentCount", 0);

			tv_nowsending.setText(getString(R.string.sendingto) + nowSending);
			tv_list.setText(getString(R.string.tv_list) + queueCount);
			tv_send.setText(getString(R.string.tv_send) + sentCount);
		}

	}

}
