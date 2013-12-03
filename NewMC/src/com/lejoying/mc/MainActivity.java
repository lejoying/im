package com.lejoying.mc;

import com.lejoying.mc.fragment.MessageFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MainActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
	}

	@Override
	public Fragment setFirstPreview() {
		return new MessageFragment();
	}

	@Override
	protected int setBackground() {
		// TODO Auto-generated method stub
		return R.drawable.card_background;
	}

}
