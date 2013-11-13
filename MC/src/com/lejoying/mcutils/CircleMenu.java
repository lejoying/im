package com.lejoying.mcutils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lejoying.mc.R;

public class CircleMenu {
	public final static int SHOW_TOP = 0x0011;
	public final static int SHOW_BOTTOM = 0x0012;
	private final int SHOW_CENTER = 0x0013;
	private final int STATUS_ANIMATION = 0x0014;
	private final int STATUS_DRAG = 0x0015;
	private int status;

	private Handler handler;

	private Activity activity;
	private LayoutInflater inflater;
	private RelativeLayout rl_control;
	private ImageView iv_controldisk;
	private RelativeLayout rl_controldiskout;

	private GestureDetector gd;

	private int initWidth;
	private int initHeight;
	private float initIvDiskX;
	private float initIvDiskY;
	private float initRlOutX;
	private float initRlOutY;

	private int scrollY;

	private boolean init;

	public CircleMenu(Activity activity) {
		this.activity = activity;
		this.handler = MCTools.handler;
	}

	public void showMenu(final int showWhere) {
		if (init) {
			return;
		}
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rl_control = (RelativeLayout) inflater.inflate(R.layout.circlemenu,
				null);
		rl_control.setVisibility(View.INVISIBLE);

		ViewGroup contentView = getContentView(activity);
		if (contentView != null) {
			contentView.addView(rl_control,
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
		} else {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			activity.addContentView(rl_control, params);
		}
		new Thread() {
			@Override
			public void run() {
				while (!init) {
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (rl_control.getHeight() != 0) {
						init = true;
						handler.post(new Runnable() {
							@Override
							public void run() {
								rl_control.setVisibility(View.VISIBLE);
								initData(showWhere);
							}
						});
					}
				}
			}

		}.start();
	}

	private void initData(int showWhere) {
		iv_controldisk = (ImageView) rl_control
				.findViewById(R.id.iv_controldisk);
		rl_controldiskout = (RelativeLayout) rl_control
				.findViewById(R.id.rl_controldiskout);
		initWidth = rl_control.getWidth();
		initHeight = rl_control.getHeight();
		int[] location = new int[2];
		iv_controldisk.getLocationInWindow(location);
		initIvDiskX = location[0];
		initIvDiskY = location[1];
		rl_controldiskout.getLocationInWindow(location);
		initRlOutX = location[0];
		initRlOutY = location[1];

		setLocation(showWhere);

		gd = new GestureDetector(activity, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {

				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {

				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {

				return true;
			}
		});

		rl_control.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					
				}
				return gd.onTouchEvent(event);
			}
		});
		
	};

	private void setLocation(int showWhere) {
		if (showWhere == SHOW_TOP) {
			scrollY = initHeight / 2;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_TOP;
		}
		if (showWhere == SHOW_BOTTOM) {
			scrollY = -initHeight / 2;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_BOTTOM;
		}
		if (showWhere == SHOW_CENTER) {
			scrollY = 0;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_CENTER;
		}
	}

	public ViewGroup getContentView(Activity activity) {
		ViewGroup v = (ViewGroup) activity.getWindow().getDecorView()
				.findViewById(android.R.id.content);
		return (ViewGroup) v.getChildAt(0);
	}
}
