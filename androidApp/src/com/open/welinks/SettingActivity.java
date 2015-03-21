package com.open.welinks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;

public class SettingActivity extends Activity implements OnClickListener {

	public String tag = "SettingActivity";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public RelativeLayout backView;
	public RelativeLayout exitCurrentUserView;

	public TextView titleContentView;

	public View aboutView, disclaimerView, opinionView;

	public View maxView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
	}

	private void initView() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		XDISTANCE_MIN = displayMetrics.widthPixels / 2;
		setContentView(R.layout.activity_setting);
		backView = (RelativeLayout) findViewById(R.id.backView);
		exitCurrentUserView = (RelativeLayout) findViewById(R.id.rl_exitCurrentUser);
		titleContentView = (TextView) findViewById(R.id.backTitleView);
		titleContentView.setText("设置");
		aboutView = findViewById(R.id.about);
		disclaimerView = findViewById(R.id.disclaimer);
		opinionView = findViewById(R.id.opinion);
		maxView = findViewById(R.id.maxView);
	}

	private void initEvent() {
		exitCurrentUserView.setOnClickListener(this);
		aboutView.setOnClickListener(this);
		disclaimerView.setOnClickListener(this);
		opinionView.setOnClickListener(this);
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

	public void logOut() {
		Alert.createDialog(this).setTitle("退出登录后您将接收不到任何消息，确定要退出登录吗？").setOnConfirmClickListener(new AlertInputDialog.OnDialogClickListener() {
			@Override
			public void onClick(AlertInputDialog dialog) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		}).show();

	}

	public float xDown;
	public float xMove;
	public float xUp;
	public VelocityTracker mVelocityTracker;
	private int XSPEED_MIN = 200;

	private int XDISTANCE_MIN = 500;

	public int MIN_DISTANCE = 150;

	public float tempX;

	boolean isFinish = false;

	public boolean isMove = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		createVelocityTracker(event);

		int id = event.getAction();

		if (id == MotionEvent.ACTION_DOWN) {
			isMove = false;
			xDown = event.getRawX();
			log.e("Down:" + xDown);
		} else if (id == MotionEvent.ACTION_MOVE) {
			isMove = true;
			xMove = event.getRawX();
			int deltaX = (int) (tempX - xMove);
			tempX = xMove;
			int distanceX = (int) (xMove - xDown);
			if (distanceX > 0) {
				int xSpeed = getScrollVelocity();
				maxView.scrollBy(deltaX, 0);
				if ((distanceX > MIN_DISTANCE && xSpeed > XSPEED_MIN) || distanceX > XDISTANCE_MIN) {
					isFinish = true;
				} else {
					isFinish = false;
				}
				log.e("Move:" + xMove);
			}
		} else if (id == MotionEvent.ACTION_UP) {
			xUp = event.getRawX();
			if (!isFinish) {
				int delta = maxView.getScrollX();
				maxView.scrollBy(-delta, 0);
			} else {
				finish();
			}
			isFinish = false;
			tempX = 0;
		}
		return true;
	}

	public View touchView;

	@SuppressLint("Recycle")
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}
}
