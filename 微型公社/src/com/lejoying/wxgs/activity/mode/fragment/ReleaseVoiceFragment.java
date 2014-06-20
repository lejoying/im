package com.lejoying.wxgs.activity.mode.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;

public class ReleaseVoiceFragment extends BaseFragment {
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	private View mContent;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = mInflater.inflate(R.layout.release_voice, null);
		initData();
		initLayout();
		initEvent();

		return mContent;
	}

	void initData() {

	}

	void initLayout() {

	}

	void initEvent() {

	}
}
