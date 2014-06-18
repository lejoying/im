package com.lejoying.autosendsms;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
import android.os.IBinder;
import android.telephony.SmsManager;

import com.lejoying.autosendsms.handler.NetworkHandler;
import com.lejoying.autosendsms.handler.NetworkHandler.NetConnection;
import com.lejoying.autosendsms.handler.NetworkHandler.Response;
import com.lejoying.autosendsms.handler.NetworkHandler.ResponseHandler;
import com.lejoying.autosendsms.handler.NetworkHandler.Settings;
import com.lejoying.autosendsms.handler.StreamParser;

public class SMSService extends Service {

	public static final String ACTION = "com.lejoying.autosendsms.smsstatuschanged";

	SmsManager smsManager;
	PendingIntent sentPI;
	PendingIntent deliverPI;
	BroadcastReceiver sentReceiver;
	BroadcastReceiver deliveredReceiver;

	NetworkHandler networkHandler;
	ResponseHandler responseHandler;
	SMSSender smsSender;

	NetConnection netConnection;

	Status status;

	String ip = "112.126.71.180";
	String port = "8074";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		status = new Status();

		networkHandler = new NetworkHandler(1);
		responseHandler = new ResponseHandler(1);
		smsSender = new SMSSender();

		status.isStartSend = smsSender.isStartSend();

		resetConnection();
		networkHandler.connection(netConnection);
		smsSender.startSend();

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
					status.successCount++;
					status.nowSending = "wait...";
					sendStatusChanged();
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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String operation = intent.getStringExtra("operation");
			if (operation != null && !operation.equals("")) {
				if (operation.equals("start")) {
					if (netConnection == null || netConnection.isDisconnected()) {
						resetConnection();
						networkHandler.connection(netConnection);
					}
					ip = intent.getStringExtra("ip");
					port = intent.getStringExtra("port");
					status.isConnection = true;
					sendStatusChanged();
				} else if (operation.equals("stop")) {
					if (netConnection != null) {
						netConnection.disConnection();
						netConnection = null;
					}
					status.isConnection = false;
					sendStatusChanged();
				} else if (operation.equals("startSend")) {
					smsSender.startSend();
					status.isStartSend = true;
					sendStatusChanged();
				} else if (operation.equals("stopSend")) {
					smsSender.stopSend();
					status.isStartSend = true;
					sendStatusChanged();
				} else if (operation.equals("clear")) {
					smsSender.clear();
				}
			}
		}
		status.isConnection = netConnection != null ? !netConnection
				.isDisconnected() : false;
		status.isStartSend = smsSender.isStartSend();
		sendStatusChanged();
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	void resetConnection() {
		netConnection = new NetConnection() {

			@Override
			protected void failed(int failedType, int responseCode) {
				switch (failedType) {
				case FAILED_TIMEOUT:

					break;

				default:
					synchronized (this) {
						try {
							wait(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				}
			}

			@Override
			protected void success(InputStream is,
					final HttpURLConnection httpURLConnection) {
				responseHandler.exclude(new Response(is) {
					@Override
					public void handleResponse(InputStream is) {
						JSONObject jObject = StreamParser.parseToJSONObject(is);
						httpURLConnection.disconnect();
						try {
							String phone = jObject.getString("phone");
							String message = jObject.getString("message");
							smsSender.sendSMS(phone, message);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = "http://" + ip + ":" + port + "/api2/sms/event";
				settings.timeout = 30000;
				String sessionID = String.valueOf(new Date().getTime());
				Map<String, String> params = new HashMap<String, String>();
				params.put("sessionID", sessionID);
				settings.params = params;
				settings.circulating = true;
			}
		};
	}

	public void sendStatusChanged() {
		Intent broadcast = new Intent(ACTION);
		broadcast.putExtra("isConnection", status.isConnection);
		broadcast.putExtra("isStartSend", status.isStartSend);
		broadcast.putExtra("queueSize", status.queueSize);
		broadcast.putExtra("nowSending", status.nowSending);
		broadcast.putExtra("successCount", status.successCount);
		broadcast.putExtra("ip", ip);
		broadcast.putExtra("port", port);
		sendBroadcast(broadcast);
	}

	public static class Status {
		boolean isConnection;
		boolean isStartSend;
		int queueSize;
		String nowSending = "wait...";
		int successCount;
	}

	public class SMSSender {

		public Queue<SMS> mSMSQueue;

		boolean startSend;

		WorkThread workThread;

		public SMSSender() {
			mSMSQueue = new LinkedList<SMSService.SMSSender.SMS>();
			workThread = new WorkThread();
			workThread.start();
			status.isStartSend = startSend;
		}

		public synchronized void stopSend() {
			startSend = false;
			status.isStartSend = startSend;
			notify();
		}

		public void startSend() {
			startSend = true;
			status.isStartSend = startSend;
		}

		public boolean isStartSend() {
			return startSend;
		}

		public synchronized void clear() {
			mSMSQueue.clear();
			status.queueSize = mSMSQueue.size();
			sendStatusChanged();
		}

		public synchronized void sendSMS(String phone, String message) {
			mSMSQueue.offer(new SMS(phone, message));
			status.queueSize = mSMSQueue.size();
			sendStatusChanged();
			if (startSend) {
				notify();
			}
		}

		synchronized SMS getSMS() throws InterruptedException {
			if (!startSend || mSMSQueue.size() == 0) {
				wait();
			}
			return mSMSQueue.poll();
		}

		class SMS {
			String phone;
			String text;

			public SMS() {
				// TODO Auto-generated constructor stub
			}

			public SMS(String phone, String text) {
				this.phone = phone;
				this.text = text;
			}

		}

		class WorkThread extends Thread {
			boolean interrupt;

			@Override
			public void run() {
				while (!interrupt) {
					SMS sms = null;
					try {
						while ((sms = getSMS()) == null)
							;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (sms.phone != null && !sms.phone.equals("")
							&& sms.text != null && !sms.text.equals("")) {
						status.queueSize = mSMSQueue.size();
						status.nowSending = sms.phone;
						sendStatusChanged();
						smsManager.sendTextMessage(sms.phone, null, sms.text,
								sentPI, deliverPI);
					}
				}
			}
		}
	}
}
