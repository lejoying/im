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

public class CircleMenu_Backup {
	public final static int SHOW_TOP = 0x0011;
	public final static int SHOW_BOTTOM = 0x0012;
	private final int SHOW_CENTER = 0x0013;

	private final int STATUS_TOP = 0x0014;
	private final int STATUS_BOTTOM = 0x0015;
	private final int STATUS_CENTER = 0x0016;
	private final int STATUS_ANIMATION = 0x0017;
	private final int STATUS_DRAG = 0x0018;
	private int status;

	private int initX;
	private int initY;

	private float clickX;
	private float clickY;

	private int scrollX;
	private int scrollY;

	private int ivX;
	private int ivY;

	private Activity activity;
	private LayoutInflater inflater;
	private RelativeLayout rl_control;
	private ImageView iv_controldisk;
	private ImageView iv_controldiskout;

	private int where;
	private int oldWhere;

	private boolean initClick;

	private Handler handler;

	private GestureDetector gd;
	
	public CircleMenu_Backup(Activity activity) {
		this.activity = activity;
		this.handler = MCTools.handler;
	}

	public void showMenu(final int showWhere) {
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
		initClick = true;
		new Thread() {
			@Override
			public void run() {
				while (initClick) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(rl_control.getHeight());
					if (rl_control.getHeight() != 0) {
						initClick = false;
						handler.post(new Runnable() {

							@Override
							public void run() {
								rl_control.scrollTo(0,
										rl_control.getHeight() / 2);
								rl_control.setVisibility(View.VISIBLE);
								show(showWhere);
							}
						});
					}
				}
			}

		}.start();
	}

	private void initMenuItem() {

	}

	private void initMenuLocation(int showWhere) {
		if (showWhere == SHOW_TOP) {
			scrollX = initX;
			scrollY = initY;
			rl_control.scrollTo(scrollX, scrollY);
			status = STATUS_TOP;
		}
		if (showWhere == SHOW_BOTTOM) {
			scrollX = initX;
			scrollY = -initY;
			rl_control.scrollTo(scrollX, scrollY);
			status = STATUS_BOTTOM;
		}
		if (showWhere == SHOW_CENTER) {
			scrollX = 0;
			scrollY = 0;
			rl_control.scrollTo(scrollX, scrollY);
			status = STATUS_CENTER;
		}
	}
	
	private void show(final int showWhere) {
		initMenuItem();
		where = showWhere;
		initX = rl_control.getScrollX();
		initY = rl_control.getScrollY();

		iv_controldisk = (ImageView) rl_control
				.findViewById(R.id.iv_controldisk);
		iv_controldiskout = (ImageView) rl_control
				.findViewById(R.id.iv_controldiskout);

		initMenuLocation(showWhere);

		gd = new GestureDetector(activity,
				new OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						int[] location = new int[2];
						iv_controldisk.getLocationInWindow(location);
						int height = iv_controldisk.getHeight();
						int width = iv_controldisk.getWidth();

						ivX = location[0];
						ivY = location[1];

						int circleX = ivX + width / 2;
						int circleY = ivY + height / 2;
						int radius = width / 2;
						if (status == STATUS_CENTER) {
							initClick = false;
							if ((clickX - circleX) * (clickX - circleX)
									+ (clickY - circleY) * (clickY - circleY) < radius
									* radius) {
								back(oldWhere);
							}
						}
						return true;
					}

					@Override
					public void onShowPress(MotionEvent e) {

					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						if ((status == STATUS_BOTTOM || status == STATUS_TOP)
								&& initClick) {
							status = STATUS_DRAG;
						}
						if (status == STATUS_DRAG) {
							int moveX = (int) (clickX - e2.getX());
							int moveY = (int) (clickY - e2.getY());
							rl_control.scrollTo((int) (scrollX + moveX),
									(int) (scrollY + moveY));
						}
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (where == SHOW_TOP && initClick) {
							if (velocityY > 3000) {
								showCircle();
							}
						}
						if (where == SHOW_BOTTOM && initClick) {
							if (velocityY < -3000) {
								showCircle();
							}
						}

						if (status == STATUS_CENTER && initClick) {
							if (velocityY > 3000) {
								back(SHOW_BOTTOM);
							}
							if (velocityY < -3000) {
								back(SHOW_TOP);
							}
						}
						return true;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						boolean flag = false;
						if (status == STATUS_TOP || status == STATUS_CENTER
								|| status == STATUS_BOTTOM) {
							clickX = e.getX();
							clickY = e.getY();
							int[] location = new int[2];
							iv_controldisk.getLocationInWindow(location);
							int height = iv_controldisk.getHeight();
							int width = iv_controldisk.getWidth();

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
						}
						if (status != STATUS_ANIMATION) {
							initClick = true;
						} else {
							initClick = false;
							flag = true;
						}
						return flag;
					}
				});
		rl_control.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (status == STATUS_DRAG) {
						float leaveY = event.getY();
						if (leaveY > initY * 3 / 2) {
							back(SHOW_BOTTOM);
						} else if (leaveY < initY / 2) {
							back(SHOW_TOP);
						} else {
							showCircle();
						}
					}
				}
				return gd.onTouchEvent(event);
			}
		});
	}

	private void showCircle() {
		oldWhere = where;
		status = STATUS_ANIMATION;
		if (where == SHOW_TOP) {
			ivY = ivY + initY;
		}
		if (where == SHOW_BOTTOM) {
			ivY = ivY - initY;
		}
		where = SHOW_CENTER;
		int[] nowlocation = new int[2];
		iv_controldisk.getLocationInWindow(nowlocation);

		TranslateAnimation ta = new TranslateAnimation(0, ivX - nowlocation[0],
				0, ivY - nowlocation[1]);

		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				iv_controldisk.clearAnimation();
				initMenuLocation(where);
				ScaleAnimation scaleanimation = new ScaleAnimation(0.5f, 1,
						0.5f, 1, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleanimation.setDuration(80);
				iv_controldiskout.setVisibility(View.VISIBLE);
				iv_controldiskout.startAnimation(scaleanimation);
			}
		});
		ta.setDuration(200);
		iv_controldisk.startAnimation(ta);

	}

	private void back(int toWhere) {
		status = STATUS_ANIMATION;
		if (toWhere == SHOW_BOTTOM) {
			if (where == SHOW_TOP) {
				ivY = ivY + initY * 2;
			}
			if (where == SHOW_CENTER) {
				ivY = ivY + initY;
			}
			where = SHOW_BOTTOM;
		}
		if (toWhere == SHOW_TOP) {
			if (where == SHOW_BOTTOM) {
				ivY = ivY - initY * 2;
			}
			if (where == SHOW_CENTER) {
				ivY = ivY - initY;
			}
			where = SHOW_TOP;
		}
		if (iv_controldiskout.getVisibility() == View.INVISIBLE) {
			circleOutBack();
		} else {
			ScaleAnimation scaleanimation = new ScaleAnimation(1, 0.3f, 1,
					0.3f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			scaleanimation.setDuration(100);
			scaleanimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					iv_controldiskout.setVisibility(View.INVISIBLE);
					circleOutBack();
				}
			});
			iv_controldiskout.startAnimation(scaleanimation);
		}
	}

	private void circleOutBack() {
		int[] nowlocation = new int[2];
		iv_controldisk.getLocationInWindow(nowlocation);
		TranslateAnimation ta = new TranslateAnimation(0, ivX - nowlocation[0],
				0, ivY - nowlocation[1]);

		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				iv_controldisk.clearAnimation();
				initMenuLocation(where);

			}
		});
		ta.setDuration(200);
		iv_controldisk.startAnimation(ta);
	}

	public ViewGroup getContentView(Activity activity) {
		ViewGroup v = (ViewGroup) activity.getWindow().getDecorView()
				.findViewById(android.R.id.content);
		return (ViewGroup) v.getChildAt(0);
	}
}
