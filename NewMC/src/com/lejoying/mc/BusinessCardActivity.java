package com.lejoying.mc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.mc.utils.CircleMenu;

public class BusinessCardActivity extends Activity {

	private static final int SCROLL = 0x51;

	// DEFINITION view
	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_spacing3;
	private RelativeLayout rl_show;
	private TextView textView4;
	private ScrollView sv_content;

	// DEFINITION object
	private Handler handler;
	private boolean stopSend;

	private int startHight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscard);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				int what = msg.what;
				switch (what) {
				case SCROLL:
					if (sv_content.getScrollY() > 10) {
						textView4.setMaxLines(100);
					}
					if (sv_content.getScrollY() < 10) {
						textView4.setMaxLines(3);
					}
					break;
				}
				super.handleMessage(msg);
			}
		};
		initMenu();
	}

	public void initMenu() {
		CircleMenu circleMenu = new CircleMenu(this);
		circleMenu.showMenu(CircleMenu.SHOW_TOP, null, true);
	}

	public void initView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		tv_spacing2 = (TextView) findViewById(R.id.tv_spacing2);
		rl_show = (RelativeLayout) findViewById(R.id.rl_show);
		tv_spacing.setHeight((int) (dm.heightPixels - rl_show.getHeight()
				- statusBarHeight - tv_spacing2.getHeight()));

		tv_spacing3 = (TextView) findViewById(R.id.tv_spacing3);
		tv_spacing3.setHeight((int) (dm.heightPixels * 0.2));

		textView4 = (TextView) findViewById(R.id.tv_mainbusiness);

		sv_content = (ScrollView) findViewById(R.id.sv_content);

		sv_content.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				stopSend = true;
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (stopSend) {
							handler.sendEmptyMessage(SCROLL);
							int start = sv_content.getScrollY();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							int stop = sv_content.getScrollY();
							if (start == stop) {
								stopSend = false;
							}
						}

						super.run();
					}

				}.start();
				return false;
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		tv_spacing = (TextView) findViewById(R.id.tv_spacing);
		if (startHight == 0) {
			startHight = tv_spacing.getHeight();
		}
		if (tv_spacing.getHeight() == startHight) {
			initView();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void back(View v) {
		finish();
	}

}
