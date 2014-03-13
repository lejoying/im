package com.lejoying.autosendsms;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.lejoying.utils.NetworkHandler;
import com.lejoying.utils.NetworkHandler.NetConnection;
import com.lejoying.utils.NetworkHandler.Settings;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.telephony.SmsManager;

public class SMSService extends Service {

	SmsManager smsManager;
	PendingIntent sentPI;
	PendingIntent deliverPI;
	BroadcastReceiver sentReceiver;
	BroadcastReceiver deliveredReceiver;

	NetworkHandler networkHandler;
	NetConnection netConnection;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		networkHandler = new NetworkHandler(1);
		netConnection = new NetConnection() {

			@Override
			protected void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void settings(Settings settings) {
				settings.url = "http://115.28.51.197:8074/api2/sms/event";
				settings.timeout = 30000;
				String sessionID = String.valueOf(new Date().getTime());
				Map<String, String> params = new HashMap<String, String>();
				params.put("sessionID", sessionID);
				settings.params = params;
				settings.circulating = true;
			}
		};

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
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// System.out.println("RESULT_ERROR_GENERIC_FAILURE");
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					// System.out.println("RESULT_ERROR_RADIO_OFF");
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					// System.out.println("RESULT_ERROR_NULL_PDU");
					break;
				}
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
		super.onCreate();
	}

	public void sendSMS(String phone, String text) {
		if (phone != null && !phone.equals("") && text != null
				&& !text.equals("")) {
			smsManager.sendTextMessage(phone, null, text, sentPI, deliverPI);
		}
	}
}
