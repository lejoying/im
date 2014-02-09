package com.lejoying.mc;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.LoginUsePassFragment;

public class LoginActivity extends BaseFragmentActivity {

	App app = App.getInstance();

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
		return R.drawable.snow_d_blur;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public static Timer timer;
	public static String remainPhone;
	public static int remainTime;

	public static boolean setRemain(String phone) {
		if (phone.equals(remainPhone) && remainTime != 0) {
			return false;
		}
		remainPhone = phone;
		remainTime = 0;
		return true;
	}

	public static void startRemain() {
		if (remainTime == 0) {
			remainTime = 60;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					remainTime--;
					if (remainTime == 0) {
						timer.cancel();
					}
				}
			}, 1000, 1000);
		}
	}

	public static void stopRemain() {
		if(timer!=null){
			timer.cancel();
		}
		remainTime = 0;
	}

}
