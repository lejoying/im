package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class ReleaseVoiceActivity extends Activity implements OnClickListener {

	LayoutInflater mInflater;

	View rl_back, rl_send, rl_sync;

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
		rl_back.setOnClickListener(this);
		rl_send.setOnClickListener(this);
		rl_sync.setOnClickListener(this);
	}

	void Send() {

	}

	void Sync() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;
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

}
