package com.lejoying.wxgs.activity.mode;

import android.support.v4.app.FragmentManager;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.fragment.LoginUseCodeFragment;
import com.lejoying.wxgs.activity.mode.fragment.LoginUsePassFragment;
import com.lejoying.wxgs.activity.mode.fragment.RegisterCodeFragment;
import com.lejoying.wxgs.activity.mode.fragment.RegisterPassFragment;
import com.lejoying.wxgs.activity.mode.fragment.RegisterPhoneFragment;

public class LoginModeManager extends BaseModeManager {

	boolean isInit;

	FragmentManager mFragmentManager;

	int mContentID = R.id.fragmentContent;

	public LoginUsePassFragment mLoginUsePassFragment;
	public LoginUseCodeFragment mLoginUseCodeFragment;
	public RegisterPhoneFragment mRegisterPhoneFragment;
	public RegisterCodeFragment mRegisterCodeFragment;
	public RegisterPassFragment mRegisterPassFragment;

	public LoginModeManager(MainActivity activity) {
		super(activity);
		mFragmentManager = activity.getSupportFragmentManager();
	}

	@Override
	public void initialize() {
		if (!isInit) {
			isInit = true;
			mLoginUsePassFragment = new LoginUsePassFragment();
			mLoginUsePassFragment.setMode(this);
			mLoginUseCodeFragment = new LoginUseCodeFragment();
			mLoginUseCodeFragment.setMode(this);
			mRegisterPhoneFragment = new RegisterPhoneFragment();
			mRegisterPhoneFragment.setMode(this);
			mRegisterCodeFragment = new RegisterCodeFragment();
			mRegisterCodeFragment.setMode(this);
			mRegisterPassFragment = new RegisterPassFragment();
			mRegisterPassFragment.setMode(this);
		}
	}

	@Override
	public void release() {
		isInit = false;
		super.release();
	}

}
