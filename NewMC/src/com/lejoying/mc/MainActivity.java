package com.lejoying.mc;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.FriendsFragment;

public class MainActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	public static MainActivity instance;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		app.context = this;
		setContentView(R.layout._main);
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
	protected void onResume() {
		super.onResume();
		app.context = this;
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}
}
