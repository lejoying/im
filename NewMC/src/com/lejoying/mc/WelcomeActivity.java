package com.lejoying.mc;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.Settings;

public class WelcomeActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	long start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app.context = this;
		setContentView(R.layout._welcome);
		app.sDcardDataResolver.readConfig();
		start = new Date().getTime();
		if (app.config.lastLoginPhone.equals("")) {
			startToLogin();
		} else {
			app.sDcardDataResolver.readData(new UIModification() {
				@Override
				public void modifyUI() {
					System.out.println(app.config.lastLoginPhone);
					checkData();
				}
			});
		}
	}

	void checkData() {
		if (app.config.lastLoginPhone.equals("none")
				|| app.data.user.accessKey == null) {
			startToLogin();
		} else {
			final Bundle params = new Bundle();
			params.putString("phone", app.data.user.phone);
			params.putString("accessKey", app.data.user.accessKey);
			params.putString("target", app.data.user.phone);

			MCNetTools.ajax(new AjaxAdapter() {

				@Override
				public void setParams(Settings settings) {
					settings.url = API.ACCOUNT_GET;
					settings.params = params;
				}

				@Override
				public void onSuccess(JSONObject jData) {
					try {
						final JSONObject jUser = jData.getJSONObject("account");
						app.dataHandler.modifyData(new Modification() {
							public void modify(Data data) {
								app.mJSONHandler.updateUser(jUser, data);
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
				public void timeout() {
					startToMain();
				}
			});
		}
	}

	void startToLogin() {
		new Thread() {
			public void run() {
				long end = new Date().getTime();
				if (end - start < 1000) {
					try {
						Thread.sleep(1000 - end + start);
						Intent intent = new Intent(WelcomeActivity.this,
								LoginActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Intent intent = new Intent(WelcomeActivity.this,
							LoginActivity.class);
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
						Intent intent = new Intent(WelcomeActivity.this,
								MainActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Intent intent = new Intent(WelcomeActivity.this,
							MainActivity.class);
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

	@Override
	protected void onResume() {
		super.onResume();
		app.context = this;
	}

}
