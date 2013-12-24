package com.lejoying.mc;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.utils.MCDataTools;

public class MainActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
	}

	@Override
	public Fragment setFirstPreview() {
		return new FriendsFragment();
	}

	@Override
	protected int setBackground() {
		// TODO Auto-generated method stub
		return R.drawable.card_background;
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("pause");
		MCDataTools.saveData(this);
	}

}
