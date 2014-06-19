package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.R.layout;
import com.lejoying.wxgs.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class ReleaseOptionActivity extends Activity implements OnTouchListener {

	LayoutInflater mInflater;
	View imageandtext, voice, vote, activity, commodity, service;
	View rl_imageandtext, rl_voice, rl_vote, rl_activity, rl_commodity,
			rl_service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_release_option);
		mInflater = getLayoutInflater();
		initData();
		initLayout();
		initEvent();
	}

	void initLayout() {
		imageandtext = findViewById(R.id.reloption_rl_imageandtext);
		voice = findViewById(R.id.reloption_rl_voice);
		vote = findViewById(R.id.reloption_rl_vote);
		activity = findViewById(R.id.reloption_rl_activity);
		commodity = findViewById(R.id.reloption_rl_commodity);
		service = findViewById(R.id.reloption_rl_service);
		rl_imageandtext = findViewById(R.id.rl_imageandtext);
		rl_voice = findViewById(R.id.rl_voice);
		rl_vote = findViewById(R.id.rl_vote);
		rl_activity = findViewById(R.id.rl_activity);
		rl_commodity = findViewById(R.id.rl_commodity);
		rl_service = findViewById(R.id.rl_service);
	}

	void initEvent() {
		imageandtext.setOnTouchListener(this);
		voice.setOnTouchListener(this);
		vote.setOnTouchListener(this);
		activity.setOnTouchListener(this);
		commodity.setOnTouchListener(this);
		service.setOnTouchListener(this);
	}

	void initData() {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.reloption_rl_imageandtext:
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
			break;
		case R.id.reloption_rl_voice:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_voice.setBackgroundResource(R.drawable.reloption_bk_sel);
				break;
			case MotionEvent.ACTION_UP:
				rl_voice.setBackgroundResource(R.drawable.reloption_bk_nor);
				break;
			}
			break;
		case R.id.reloption_rl_vote:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_vote.setBackgroundResource(R.drawable.reloption_bk_sel);
				break;
			case MotionEvent.ACTION_UP:
				rl_vote.setBackgroundResource(R.drawable.reloption_bk_nor);
				break;
			}
			break;
		case R.id.reloption_rl_activity:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_activity.setBackgroundResource(R.drawable.reloption_bk_sel);
				break;
			case MotionEvent.ACTION_UP:
				rl_activity.setBackgroundResource(R.drawable.reloption_bk_nor);
				break;
			}
			break;
		case R.id.reloption_rl_commodity:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_commodity.setBackgroundResource(R.drawable.reloption_bk_sel);
				break;
			case MotionEvent.ACTION_UP:
				rl_commodity.setBackgroundResource(R.drawable.reloption_bk_nor);
				break;
			}
			break;
		case R.id.reloption_rl_service:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_service.setBackgroundResource(R.drawable.reloption_bk_sel);
				break;
			case MotionEvent.ACTION_UP:
				rl_service.setBackgroundResource(R.drawable.reloption_bk_nor);
				break;
			}
			break;
		default:
			break;
		}
		return true;
	}

}
