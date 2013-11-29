package com.lejoying.mc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WelcomeActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

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
	public boolean createCircleMenu() {
		return false;
	}

	@Override
	public Fragment setFirstPreview() {
		return null;
	}

}
