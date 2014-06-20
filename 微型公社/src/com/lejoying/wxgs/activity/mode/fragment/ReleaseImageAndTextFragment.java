package com.lejoying.wxgs.activity.mode.fragment;

import java.lang.reflect.Field;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;

public class ReleaseImageAndTextFragment extends BaseFragment {
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	private View mContent;

	int height, width, dip;
	float density;
	View sl_content,bottom_bar;
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
		mContent = mInflater.inflate(R.layout.release_imageandtext, null);
		initData();
		initLayout();
		initEvent();

		return mContent;
	}

	void initData() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}

	void initLayout() {
		bottom_bar=mContent.findViewById(R.id.bottom_bar);
		
		sl_content=mContent.findViewById(R.id.sl_content);
		LayoutParams params=sl_content.getLayoutParams();
		params.height=height-MainActivity.statusBarHeight-(int)(157*density+0.5f);
		sl_content.setLayoutParams(params);
	}

	void initEvent() {

	}
}
