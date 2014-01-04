package com.lejoying.mc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.FriendsFragment;

public class MainActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	public static MainActivity instance;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
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
		app.dataHandler.sendMessage(app.dataHandler.HANDLER_SAVECONFIGANDDATA,
				app.dataHandler.DOSYNC, this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = true;
		// if (keyCode == KeyEvent.KEYCODE_BACK) {
		// if (app.mark.equals(app.friendsFragment)
		// || app.mark.equals(app.ShareFragment)) {
		// moveTaskToBack(false);
		// flag = false;
		// }
		// }
		return flag && super.onKeyDown(keyCode, event);
	}
}
