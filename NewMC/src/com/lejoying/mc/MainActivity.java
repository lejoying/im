package com.lejoying.mc;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.fragment.LoginUsePassFragment;

public class MainActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
	}

	@Override
	public boolean createCircleMenu() {
		return true;
	}

	@Override
	public Fragment setFirstPreview() {
		return new LoginUsePassFragment();
	}
	
}
