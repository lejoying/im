package com.lejoying.autosendsms;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lejoying.utils.Ajax;
import com.lejoying.utils.Ajax.AjaxInterface;
import com.lejoying.utils.Ajax.Settings;
import com.lejoying.utils.HttpTools;

public class MainActivity extends Activity implements OnClickListener {

	static final String path = "";

	Button btn_start;
	Button btn_stop;
	TextView tv_list;
	TextView tv_send;

	SmsManager smsManager;

	String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	Intent sentIntent;
	PendingIntent sentPI;

	String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	Intent deliverIntent;
	PendingIntent deliverPI;

	List<String> phones = new ArrayList<String>();
	Map<String, String> phoneText = new HashMap<String, String>();
	List<String> successed = new ArrayList<String>();
	List<String> unsuccess = new ArrayList<String>();

	HttpURLConnection currentConnection;

	Handler handler;

	boolean isStart;

	Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		handler = new Handler();
	}

	public void initView() {
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		tv_list = (TextView) findViewById(R.id.tv_list);
		tv_send = (TextView) findViewById(R.id.tv_send);
		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		stop();
		smsManager = SmsManager.getDefault();
		sentIntent = new Intent(SENT_SMS_ACTION);
		sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					if (toast != null) {
						toast.cancel();
					}
					toast = Toast.makeText(MainActivity.this, "短信发送成功",
							Toast.LENGTH_SHORT);
					toast.show();
					Set<String> set = _intent.getExtras().keySet();
					System.out.println(set.size());
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));

		deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		deliverPI = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				if (toast != null) {
					toast.cancel();
				}
				toast = Toast.makeText(MainActivity.this, "收信人已经成功接收",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}, new IntentFilter(DELIVERED_SMS_ACTION));

	}

	public void sendSMS(String phone, String text) {
		smsManager.sendTextMessage(phone, null, text, sentPI, deliverPI);
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
		default:
			break;
		}
	}

	public void start() {
		isStart = true;
		btn_start.setClickable(false);
		btn_start.setTextColor(Color.GRAY);
		btn_stop.setClickable(true);
		btn_stop.setTextColor(Color.BLACK);
		startListener();
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(MainActivity.this, "开始", Toast.LENGTH_SHORT);
		toast.show();
	}

	public void stop() {
		isStart = false;
		btn_start.setClickable(true);
		btn_start.setTextColor(Color.BLACK);
		btn_stop.setClickable(false);
		btn_stop.setTextColor(Color.GRAY);
		if (currentConnection != null) {
			currentConnection.disconnect();
		}
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(MainActivity.this, "停止", Toast.LENGTH_SHORT);
		toast.show();
	}

	public void startListener() {
		Ajax.ajax(this, new AjaxListenerImpl());
		System.out.println("重新连接");
	}

	@Override
	public void finish() {
		stop();
		super.finish();
	}

	class AjaxListenerImpl implements AjaxInterface {
		@Override
		public void setParams(Settings settings) {
			settings.url = "http://115.28.51.197:8074/api2/session/event";
			Bundle params = new Bundle();
			params.putString("text", "nihao");
			settings.params = params;
			settings.timeout = 30000;
			settings.method = HttpTools.SEND_POST;
		}

		@Override
		public void onSuccess(JSONObject jData) {
			if (isStart) {
				startListener();
			}
			System.out.println(jData);
		}

		@Override
		public void failed() {
			System.out.println("连接失败");
			if (isStart) {
				startListener();
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (toast != null) {
						toast.cancel();
					}
					toast = Toast.makeText(MainActivity.this, "连接失败",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}

		@Override
		public void noInternet() {
			System.out.println("没有网络连接");
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (toast != null) {
						toast.cancel();
					}
					toast = Toast.makeText(MainActivity.this, "没有网络连接",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}

		@Override
		public void timeout() {
			System.out.println("超时了");
			if (isStart) {
				startListener();
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (toast != null) {
						toast.cancel();
					}
					toast = Toast.makeText(MainActivity.this, "超时了",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}

		@Override
		public void connectionCreated(HttpURLConnection httpURLConnection) {
			currentConnection = httpURLConnection;
			sendNotify();
		}
	}

	public void sendNotify() {
		Ajax.ajax(this, new AjaxInterface() {

			@Override
			public void timeout() {
				// TODO Auto-generated method stub

			}

			@Override
			public void setParams(Settings settings) {
				settings.url = "http://115.28.51.197:8074/api2/session/notify";
				Bundle params = new Bundle();
				params.putString("text", "nihao");
				settings.params = params;
			}

			@Override
			public void onSuccess(JSONObject jData) {
				System.out.println(jData);
			}

			@Override
			public void noInternet() {
				// TODO Auto-generated method stub

			}

			@Override
			public void failed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void connectionCreated(HttpURLConnection httpURLConnection) {
				// TODO Auto-generated method stub

			}
		});
	}
}
