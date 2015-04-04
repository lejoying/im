package com.open.welinks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.open.welinks.model.Data;
import com.open.welinks.view.ViewManage;

public class LoadingActivity extends Activity {
	public Data data = Data.getInstance();
	public String tag = "LoginActivity";

	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thisActivity = this;
		thisActivity.setContentView(R.layout.activity_login);
	}

	@Override
	public void onResume() {
		super.onResume();
		data.localStatus.thisActivityName = "LoadingActivity";
		startMain();
	}

	public void startMain() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(thisActivity, NearbyActivity.class);
				intent.putExtra("type", "newest");
				startActivity(intent);
				thisActivity.finish();
			}
		}).start();
	}
}
