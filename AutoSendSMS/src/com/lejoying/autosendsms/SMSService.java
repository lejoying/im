package com.lejoying.autosendsms;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.lejoying.utils.Ajax;
import com.lejoying.utils.Ajax.AjaxInterface;
import com.lejoying.utils.Ajax.Settings;
import com.lejoying.utils.HttpTools;

public class SMSService extends Service {

	public static final String ACTION = "SMSSERVICE";

	SmsManager smsManager;
	PendingIntent sentPI;
	PendingIntent deliverPI;
	BroadcastReceiver sentReceiver;
	BroadcastReceiver deliveredReceiver;

	NetworkStatusReceiver networkStatusReceiver;

	public static boolean isStart;
	public static boolean isStartSend = true;
	boolean isSending;
	boolean isWaitForInternet;

	long currentConnectionCode;
	HttpURLConnection currentConnection;

	Queue<SMSEntity> mSMSQueue = new LinkedList<SMSEntity>();
	SMSEntity nowSendingEntity;
	long sentCount;

	Handler handler;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		handler = new Handler();

		smsManager = SmsManager.getDefault();
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		Intent sentIntent;
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
		Intent deliverIntent;

		smsManager = SmsManager.getDefault();
		sentIntent = new Intent(SENT_SMS_ACTION);
		sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
		sentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					// System.out.println("RESULT_OK");
					sentCount++;
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// System.out.println("RESULT_ERROR_GENERIC_FAILURE");
					handleOver();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					// System.out.println("RESULT_ERROR_RADIO_OFF");
					handleOver();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					// System.out.println("RESULT_ERROR_NULL_PDU");
					handleOver();
					break;
				}
				handleOver();
			}
		};
		registerReceiver(sentReceiver, new IntentFilter(SENT_SMS_ACTION));

		deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		deliverPI = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
		deliveredReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

			}
		};

		registerReceiver(deliveredReceiver, new IntentFilter(
				DELIVERED_SMS_ACTION));

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		networkStatusReceiver = new NetworkStatusReceiver();
		registerReceiver(networkStatusReceiver, intentFilter);
		super.onCreate();
	}

	public void sendSMS(SMSEntity smsEntity) {
		mSMSQueue.offer(smsEntity);
		sendSMSBroadcast();
		handleSMSQueue();
	}

	public void handleSMSQueue() {
		if (!isStartSend) {
			return;
		}
		if (isSending || mSMSQueue.size() == 0) {
			return;
		}
		isSending = true;
		new Thread() {
			public void run() {
				SMSEntity smsEntity = mSMSQueue.poll();
				nowSendingEntity = smsEntity;
				sendSMSBroadcast();
				if (smsEntity.phone != null && !smsEntity.phone.equals("")
						&& smsEntity.text != null && !smsEntity.text.equals("")) {
					smsManager.sendTextMessage(smsEntity.phone, null,
							smsEntity.text, sentPI, deliverPI);
				} else {
					handleOver();
					// TODO SEND SMS IS FAILED
				}

			}
		}.start();
	}

	public void sendSMSBroadcast() {
		Intent intent = new Intent();
		intent.setAction(ACTION);
		if (nowSendingEntity != null) {
			intent.putExtra("nowSending", nowSendingEntity.phone);
		} else {
			intent.putExtra("nowSending", "(wait)");
		}
		intent.putExtra("queueCount", mSMSQueue.size());
		intent.putExtra("sentCount", sentCount);
		sendBroadcast(intent);
	}

	public void handleOver() {
		isSending = false;
		nowSendingEntity = null;
		sendSMSBroadcast();
		handleSMSQueue();
	}

	public SMSEntity generateSMSEntityFromJSON(JSONObject jSMSEntity) {
		SMSEntity smsEntity = new SMSEntity();
		try {
			smsEntity.phone = jSMSEntity.getString("phone");
			smsEntity.text = jSMSEntity.getString("message");
		} catch (JSONException e) {
		}
		return smsEntity;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String operation = intent.getStringExtra("operation");
			if (operation != null) {
				if (operation.equals("start")) {
					startLongAjax();
				} else if (operation.equals("stop")) {
					stopLongAjax();
				} else if (operation.equals("stopSend")) {
					isStartSend = false;
				} else if (operation.equals("startSend")) {
					if (!isStartSend) {
						isStartSend = true;
						handleSMSQueue();
					}
				} else if (operation.equals("clearQueue")) {
					cleanQueue();
				}
			} else {
				sendSMSBroadcast();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	Bundle params;

	public void startLongAjax() {
		if (!isStart) {
			isStart = true;
			isWaitForInternet = false;
			currentConnectionCode = new Date().getTime();
			longAjax(currentConnectionCode);
		}
	}

	public void stopLongAjax() {
		if (isStart) {
			isStart = false;
			if (currentConnection != null) {
				currentConnection.disconnect();
			}
		}
	}

	public void longAjax(final long code) {
		if (code != currentConnectionCode) {
			return;
		}
		if (params == null) {
			String sessionID = String.valueOf(new Date().getTime());
			params = new Bundle();
			params.putString("sessionID", sessionID);
		}
		Ajax.ajax(this, new AjaxInterface() {
			@Override
			public void setParams(Settings settings) {
				settings.url = "http://115.28.51.197:8074/api2/sms/event";
				settings.timeout = 30000;
				settings.params = params;
				settings.method = HttpTools.SEND_POST;
			}

			@Override
			public void onSuccess(JSONObject jData) {
				if (isStart) {
					longAjax(code);
				}
				sendSMS(generateSMSEntityFromJSON(jData));
			}

			@Override
			public void failed() {
				if (isStart) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					longAjax(code);
				}
			}

			@Override
			public void noInternet() {
				if (isStart) {
					isWaitForInternet = true;
					stopLongAjax();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SMSService.this,
								getString(R.string.nointernet),
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void timeout() {
				if (isStart) {
					longAjax(code);
				}
			}

			@Override
			public void connectionCreated(HttpURLConnection httpURLConnection) {
				currentConnection = httpURLConnection;
			}

		});
	}

	public void cleanQueue() {
		mSMSQueue.clear();
		sendSMSBroadcast();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(sentReceiver);
		unregisterReceiver(deliveredReceiver);
		unregisterReceiver(networkStatusReceiver);
		super.onDestroy();
	}

	public class NetworkStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (HttpTools.hasNetwork(context)) {
				if (isWaitForInternet) {
					startLongAjax();
				}
				sendSMSBroadcast();
			}
		}
	}

}
