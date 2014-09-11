package com.open.welinks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.service.PushService;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog;

public class SettingActivity extends Activity implements OnClickListener {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public RelativeLayout backView;
	public RelativeLayout exitCurrentUserView;

	public TextView titleContentView;

	public View about, disclaimer, opinion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		backView = (RelativeLayout) findViewById(R.id.backView);
		exitCurrentUserView = (RelativeLayout) findViewById(R.id.rl_exitCurrentUser);
		titleContentView = (TextView) findViewById(R.id.backTitleView);
		titleContentView.setText("设置");
		about = findViewById(R.id.about);
		disclaimer = findViewById(R.id.disclaimer);
		opinion = findViewById(R.id.opinion);
		initEvent();
	}

	private void initEvent() {
		exitCurrentUserView.setOnClickListener(this);
		about.setOnClickListener(this);
		disclaimer.setOnClickListener(this);
		opinion.setOnClickListener(this);
		setOnTouch(exitCurrentUserView);
		backView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.backView) {
			// view.playSoundEffect(SoundEffectConstants.CLICK);
			finish();
		} else if (id == R.id.rl_exitCurrentUser) {
			logOut();
		} else if (id == R.id.about) {
			Intent intent = new Intent(SettingActivity.this, StatementActivity.class);
			intent.putExtra("type", "about");
			startActivity(intent);
		} else if (id == R.id.disclaimer) {
			Intent intent = new Intent(SettingActivity.this, StatementActivity.class);
			intent.putExtra("type", "disclaimer");
			startActivity(intent);
		} else if (id == R.id.opinion) {
			startActivity(new Intent(SettingActivity.this, FeedBackActivity.class));
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
				data = parser.check();
				data.userInformation.currentUser.accessKey = "";
				data.userInformation.isModified = true;
				setResult(Activity.RESULT_OK);
				finish();
			}
		}).show();

	}
}
