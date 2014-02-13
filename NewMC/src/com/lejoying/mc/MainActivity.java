package com.lejoying.mc;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.service.PushService;

public class MainActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	public static MainActivity instance;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
		Intent service = new Intent(this, PushService.class);
		service.putExtra("objective", "start");
		startService(service);
		instance = this;
	}

	@Override
	public Fragment setFirstPreview() {
		return new FriendsFragment();
	}

	@Override
	protected int setBackground() {
		// TODO Auto-generated method stub
		return R.drawable.cloudy_d_blur;
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("pause");
		app.sDcardDataResolver.saveToSDcard();
	}

	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}

	@Override
	public void finish() {
		Intent service = new Intent(this, PushService.class);
		service.putExtra("objective", "stop");
		startService(service);
		super.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}
}
