package com.lejoying.wxgs.activity.mode.fragment;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BigHeadFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	View mContent;
	LayoutInflater mInflater;

	private ImageView bigHead;
	public String userHead;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = inflater.inflate(R.layout.f_bighead, null);
//		mContent.setBackgroundDrawable(new BitmapDrawable(
//				app.fileHandler.bitmaps
//						.get(app.data.user.userBackground)));
		bigHead = (ImageView) mContent.findViewById(R.id.iv_bighead);
		app.fileHandler.getImage(userHead, new FileResult() {
			@Override
			public void onResult(String where) {
//				System.out.println(app.fileHandler.bitmaps.get(userHead)
//						+ "------" + bigHead);
				bigHead.setImageBitmap(app.fileHandler.bitmaps.get(userHead));
			}
		});
		mContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMainModeManager.back();
			}
		});
		return mContent;

	}

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}
}
