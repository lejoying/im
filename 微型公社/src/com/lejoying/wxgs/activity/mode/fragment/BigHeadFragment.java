package com.lejoying.wxgs.activity.mode.fragment;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mInflater = inflater;
		mContent = inflater.inflate(R.layout.f_bighead, null);
		bigHead = (ImageView) mContent.findViewById(R.id.iv_bighead);
		final String headFileName = app.data.user.head;
		app.fileHandler.getImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				System.out.println(app.fileHandler.bitmaps.get(headFileName)
						+ "------" + bigHead);
//				bigHead.setImageBitmap(app.fileHandler.bitmaps
//						.get(headFileName));
			}
		});
		mContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMainModeManager.back();
			}
		});
		return super.onCreateView(inflater, container, savedInstanceState);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
