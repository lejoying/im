package com.lejoying.mc;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.CircleMenuFragment;
import com.lejoying.mc.fragment.LoginUseCodeFragment;
import com.lejoying.mc.fragment.LoginUsePassFragment;
import com.lejoying.mc.fragment.RegisterCodeFragment;
import com.lejoying.mc.fragment.RegisterPassFragment;
import com.lejoying.mc.fragment.RegisterPhoneFragment;

public class LoginActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	public CircleMenuFragment circleMenuFragment;
	public LoginUsePassFragment loginUsePassFragment;
	public LoginUseCodeFragment loginUseCodeFragment;
	public RegisterPhoneFragment registerPhoneFragment;
	public RegisterCodeFragment registerCodeFragment;
	public RegisterPassFragment registerPassFragment;

	FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._login);
		fragmentManager = getSupportFragmentManager();
		circleMenuFragment = (CircleMenuFragment) fragmentManager
				.findFragmentById(R.id.circleMenu);

		if (loginUsePassFragment == null) {
			loginUsePassFragment = new LoginUsePassFragment();
		}
		fragmentManager.beginTransaction()
				.add(R.id.fl_content, loginUsePassFragment).commit();
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
		if (timer != null) {
			timer.cancel();
		}
		remainTime = 0;
	}

}
