package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SendVoiceActivity extends BaseActivity implements OnClickListener {
	BackgroundView mBackground;
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;

	int height, width, dip;
	float density;

	RelativeLayout sendvoice_rl_navigation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_send_voice);
		mInflater = getLayoutInflater();
		getWindow().setBackgroundDrawableResource(R.drawable.square_background);
		initData();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	protected void onResume() {
		CircleMenu.hide();
		super.onResume();
	}

	public void initData() {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}

	public void initEvent() {
		// TODO Auto-generated method stub
		final TextView sendvoice_tv = (TextView) findViewById(R.id.sendvoice_tv);
		View sendvoice_button = findViewById(R.id.sendvoice_button);
		View sendvoice_iv_commit = findViewById(R.id.sendvoice_iv_commit);
		View sendvoice_iv_del = findViewById(R.id.sendvoice_iv_del);
		sendvoice_rl_navigation = (RelativeLayout) findViewById(R.id.sendvoice_rl_navigation);

		RelativeLayout.LayoutParams buttonrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonrelativeParams.width = width / 3 * 2;
		// buttonrelativeParams.height=60;
		buttonrelativeParams.topMargin = 20;
		buttonrelativeParams.bottomMargin = 20;
		buttonrelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		sendvoice_button.setLayoutParams(buttonrelativeParams);

		RelativeLayout.LayoutParams tvrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvrelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		sendvoice_tv.setLayoutParams(tvrelativeParams);

		RelativeLayout.LayoutParams commitrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		commitrelativeParams.addRule(RelativeLayout.LEFT_OF,
				R.id.sendvoice_button);
		commitrelativeParams.rightMargin = width / 25;
		commitrelativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
		sendvoice_iv_commit.setLayoutParams(commitrelativeParams);

		RelativeLayout.LayoutParams delrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		delrelativeParams.addRule(RelativeLayout.RIGHT_OF,
				R.id.sendvoice_button);
		delrelativeParams.leftMargin = width / 25;
		delrelativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
		sendvoice_iv_del.setLayoutParams(delrelativeParams);

		sendvoice_iv_commit.setOnClickListener(this);
		sendvoice_iv_del.setOnClickListener(this);
		sendvoice_rl_navigation.setOnClickListener(this);

		sendvoice_button.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sendvoice_tv.setText("放开停止");
					break;
				case MotionEvent.ACTION_UP:
					sendvoice_tv.setText("开始录音");
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sendvoice_iv_commit:

			break;
		case R.id.sendvoice_iv_del:

			break;
		case R.id.sendvoice_rl_navigation:
 
			break;
		default:
			break;
		}
	}

}
