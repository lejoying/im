package com.lejoying.mc;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.fragment.LoginUsePassFragment;

public class LoginActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
	}

	@Override
	public Fragment setFirstPreview() {
		return new LoginUsePassFragment();
	}

	@Override
	protected int setBackground() {
		return R.drawable.card_background;
	}

}
