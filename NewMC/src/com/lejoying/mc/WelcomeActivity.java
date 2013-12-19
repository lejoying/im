package com.lejoying.mc;

import com.lejoying.mc.utils.MCDataTools;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WelcomeActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout._welcome);

		MCDataTools.getData(this);

		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);

		this.finish();
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
