package com.lejoying.wxgs.activity.mode.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;

public class ReleaseOptionFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	private View mContent;
	View imageandtext, voice, vote, activity, commodity, service;
	View rl_imageandtext, rl_voice, rl_vote, rl_activity, rl_commodity,
			rl_service;

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
		mContent = mInflater.inflate(R.layout.activity_release_option, null);
		initData();
		initLayout();
		initEvent();

		return mContent;
	}

	void initLayout() {
		imageandtext = mContent.findViewById(R.id.reloption_rl_imageandtext);
		voice = mContent.findViewById(R.id.reloption_rl_voice);
		vote = mContent.findViewById(R.id.reloption_rl_vote);
		activity = mContent.findViewById(R.id.reloption_rl_activity);
		commodity = mContent.findViewById(R.id.reloption_rl_commodity);
		service = mContent.findViewById(R.id.reloption_rl_service);
		rl_imageandtext = mContent.findViewById(R.id.rl_imageandtext);
		rl_voice = mContent.findViewById(R.id.rl_voice);
		rl_vote = mContent.findViewById(R.id.rl_vote);
		rl_activity = mContent.findViewById(R.id.rl_activity);
		rl_commodity = mContent.findViewById(R.id.rl_commodity);
		rl_service = mContent.findViewById(R.id.rl_service);
	}

	void initEvent() {
		imageandtext.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {
						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							mMainModeManager
									.showNext(mMainModeManager.mReleaseImageAndTextFragment);
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_imageandtext
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_imageandtext
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		voice.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							mMainModeManager
									.showNext(mMainModeManager.mReleaseVoiceFragment);
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_voice.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_voice.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		vote.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							mMainModeManager
									.showNext(mMainModeManager.mReleaseVoteFragment);
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_vote.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_vote.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		activity.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_activity
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_activity
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		commodity.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_commodity
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_commodity
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		service.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_service
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_service
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
	}

	void initData() {

	}

}
