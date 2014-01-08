package com.lejoying.mc;

import java.net.HttpURLConnection;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.StaticData;
import com.lejoying.mc.data.handler.DataHandler1.Modification;
import com.lejoying.mc.data.handler.DataHandler2;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;
import com.lejoying.utils.HttpTools;

public class WelcomeActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	long start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout._welcome);
		app.dataHandler.sendMessage(app.dataHandler.DATA_HANDLER_GETCONFIGANDDATA, app.dataHandler.DOSYNC, this);
		start = new Date().getTime();
		if (app.config.lastLoginPhone.equals("none") || app.data.user.accessKey == null) {
			startToLogin();
		} else {
			Bundle params = new Bundle();
			params.putString("phone", app.data.user.phone);
			params.putString("accessKey", app.data.user.accessKey);
			params.putString("target", app.data.user.phone);

			MCNetTools.ajax(this, API.ACCOUNT_GET, params, HttpTools.SEND_POST, 5000, new ResponseListener() {

				@Override
				public void success(JSONObject data) {
					try {
						final JSONObject jUser = data.getJSONObject("account");
						app.dataHandler1.modifyData(new Modification() {
							public void modify(StaticData data) {
								DataHandler2 dataHandler2 = new DataHandler2();
								dataHandler2.updateUser(jUser, data);
							}
						});
						startToMain();
					} catch (JSONException e) {
						startToLogin();
					}
				}

				@Override
				public void noInternet() {
					startToMain();
				}

				@Override
				public void failed() {
					startToMain();
				}

				@Override
				public void connectionCreated(HttpURLConnection httpURLConnection) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

	void startToLogin() {
		app.dataHandler.sendEmptyMessage(app.dataHandler.DATA_HANDLER_CLEANDATA);
		new Thread() {
			public void run() {
				long end = new Date().getTime();
				if (end - start < 1000) {
					try {
						Thread.sleep(1000 - end + start);
						Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
					startActivity(intent);
					WelcomeActivity.this.finish();
				}
			};
		}.start();
	}

	void startToMain() {

		new Thread() {
			public void run() {
				long end = new Date().getTime();
				if (end - start < 1000) {
					try {
						Thread.sleep(1000 - end + start);
						Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
					startActivity(intent);
					WelcomeActivity.this.finish();
				}
			};
		}.start();
	}

	@Override
	public Fragment setFirstPreview() {
		return null;
	}

	@Override
	protected int setBackground() {
		return R.drawable.app_start;
	}

}
