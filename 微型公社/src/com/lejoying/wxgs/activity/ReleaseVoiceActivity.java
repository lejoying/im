package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class ReleaseVoiceActivity extends Activity implements OnClickListener,OnTouchListener {

	LayoutInflater mInflater;

	View rl_back, rl_send, rl_sync;
	GestureDetector backViewDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_voice);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initData();
		initLayout();
		initEvent();

	}

	void initData() {

	}

	void initLayout() {
		rl_back = findViewById(R.id.rl_back);
		rl_send = findViewById(R.id.rl_send);
		rl_sync = findViewById(R.id.rl_sync);
	}

	void initEvent() {
		rl_back.setOnTouchListener(this);
		rl_send.setOnClickListener(this);
		rl_sync.setOnClickListener(this);
		backViewDetector = new GestureDetector(
				ReleaseVoiceActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						finish();
						return true;
					}
				});
	}

	void Send() {

	}

	void Sync() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_send:
			Send();
			break;
		case R.id.rl_sync:
			Sync();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.rl_back:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_back.setBackgroundColor(Color.argb(143, 0, 0,
						0));
				break;
			case MotionEvent.ACTION_UP:
				rl_back
						.setBackgroundColor(Color.argb(0, 0, 0, 0));
				break;
			}
			break;
		}
		return backViewDetector.onTouchEvent(event);
	}

}
