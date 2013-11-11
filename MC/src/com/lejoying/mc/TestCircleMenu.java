package com.lejoying.mc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TestCircleMenu extends Activity {

	private RelativeLayout rl_circleMenu;
	private ImageView iv_control;

	private float clickX;
	private float clickY;

	private float scrollX;
	private float scrollY;

	private int ivX;
	private int ivY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circlemenu_old);
		rl_circleMenu = (RelativeLayout) findViewById(R.id.rl_circleMenu);
		iv_control = (ImageView) findViewById(R.id.iv_control);
		rl_circleMenu.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean flag = false;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					clickX = event.getX();
					clickY = event.getY();

					scrollX = rl_circleMenu.getScrollX();
					scrollY = rl_circleMenu.getScrollY();

					int[] location = new int[2];
					iv_control.getLocationInWindow(location);
					int height = iv_control.getHeight();
					int width = iv_control.getWidth();

					ivX = location[0];
					ivY = location[1];

					int circleX = ivX + width / 2;
					int circleY = ivY + height / 2;
					int radius = width / 2;
					if ((clickX - circleX) * (clickX - circleX)
							+ (clickY - circleY) * (clickY - circleY) < radius
							* radius) {
						flag = true;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) (clickX - event.getX());
					int moveY = (int) (clickY - event.getY());

					rl_circleMenu.scrollTo((int) (scrollX + moveX),
							(int) (scrollY + moveY));
					break;
				case MotionEvent.ACTION_UP:
					float leaveX = event.getX();
					float leaveY = event.getY();
					if (leaveY < scrollY / 3) {
						int[] location2 = new int[2];
						iv_control.getLocationInWindow(location2);
						TranslateAnimation ta = new TranslateAnimation(0, ivX
								- location2[0], 0, ivY - location2[1]);
						ta.setDuration(250);
						ta.setFillAfter(true);
						ta.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation arg0) {
								rl_circleMenu.scrollTo((int) scrollX,
										(int) scrollY);
								iv_control.clearAnimation();
							}
						});
						iv_control.startAnimation(ta);

					} else {
						System.out.println("go");
					}

					break;
				default:
					break;
				}

				return flag;
			}
		});
	}

}
