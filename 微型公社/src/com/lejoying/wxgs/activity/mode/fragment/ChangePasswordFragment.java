package com.lejoying.wxgs.activity.mode.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;

public class ChangePasswordFragment extends BaseFragment {
	View mContent;
	LayoutInflater mInflater;
	
	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	
	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = inflater.inflate(R.layout.f_changepwd, null);
		
		
		return mContent;
	}
	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}
}
