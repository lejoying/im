package com.open.welinks;

import com.open.welinks.service.PushService;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * SettingActivity 2014-7-29 下午1:42:13
 * 
 * @author 乔晓松 qiaoxiaosong@lejoying.com
 */
public class SettingActivity extends Activity implements OnClickListener {

	ImageView backView;
	RelativeLayout exitCurrentUserView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		backView = (ImageView) findViewById(R.id.iv_setttingBack);
		exitCurrentUserView = (RelativeLayout) findViewById(R.id.rl_exitCurrentUser);
		initEvent();
	}

	private void initEvent() {
		exitCurrentUserView.setOnClickListener(this);
		setOnTouch(exitCurrentUserView);
		backView.setOnClickListener(this);
		backView.setOnTouchListener(new OnTouchListener() {
			GestureDetector backviewDetector = new GestureDetector(SettingActivity.this, new GestureDetector.SimpleOnGestureListener() {

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

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					backView.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					backView.setBackgroundColor(Color.argb(0, 0, 0, 0));
					break;
				}
				return backviewDetector.onTouchEvent(event);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_setttingBack:
			finish();
			break;
		case R.id.rl_exitCurrentUser:
			logOut();
			break;
		default:
			break;
		}
	}

	void setOnTouch(final View view) {
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					view.setBackgroundColor(Color.parseColor("#38ffffff"));
					break;
				}
				return false;
			}
		});
	}

	public void logOut() {
		Alert.createDialog(this).setTitle("退出登录后您将接收不到任何消息，确定要退出登录吗？").setOnConfirmClickListener(new AlertInputDialog.OnDialogClickListener() {
			@Override
			public void onClick(AlertInputDialog dialog) {
				Intent service = new Intent(SettingActivity.this, PushService.class);
				service.putExtra("operation", false);
				startService(service);
				finish();
			}
		}).show();

	}
}
