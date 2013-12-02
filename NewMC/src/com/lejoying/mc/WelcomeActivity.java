package com.lejoying.mc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.service.SMSService;

public class WelcomeActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout._welcome);

		startService(new Intent(this, SMSService.class));

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
					Intent intent = new Intent(WelcomeActivity.this,
							MainActivity.class);
					WelcomeActivity.this.startActivity(intent);
					WelcomeActivity.this.finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
