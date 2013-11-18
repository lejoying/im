package com.lejoying.mcutils;

import java.util.ArrayList;
import java.util.List;

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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

	private int ivX;
	private int ivY;
	private float clickX;
	private float clickY;

	private int diskWidth;
	private int diskHeight;
	private int diskRadius;
	private int outWidth;
	private int outHeight;
	private int outRadius;

	private int where;
	private int oldWhere;

	private boolean init;
	private boolean initClick;

	private List<View> menuItemList;

	public CircleMenu(Activity activity) {
		this.activity = activity;
		this.handler = MCTools.handler;
	}

	public void showMenu(final int showWhere, final List<MenuEntity> menuList) {
		if (init || menuList == null) {
			return;
		}
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rl_control = (RelativeLayout) inflater.inflate(R.layout.circlemenu,
				null);
		rl_control.setVisibility(View.INVISIBLE);

		iv_controldisk = (ImageView) rl_control
				.findViewById(R.id.iv_controldisk);
		rl_controldiskout = (RelativeLayout) rl_control
				.findViewById(R.id.rl_controldiskout);

		menuItemList = new ArrayList<View>();
		for (MenuEntity entity : menuList) {
			RelativeLayout rl_menuitem = (RelativeLayout) inflater.inflate(
					R.layout.circlemenu_item, null);
			ImageView iv_image = (ImageView) rl_menuitem
					.findViewById(R.id.iv_image);
			TextView tv_text = (TextView) rl_menuitem
					.findViewById(R.id.tv_text);

			// iv_image.setImageDrawable(activity.getResources().getDrawable(
			// entity.getImageID()));
			tv_text.setText(entity.getText());

			menuItemList.add(rl_menuitem);

			rl_menuitem.setVisibility(View.INVISIBLE);
			rl_controldiskout.addView(rl_menuitem);
		}

		ViewGroup contentView = getContentView(activity);
		if (contentView != null) {
			contentView.addView(rl_control,
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		} else {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
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
								initData(showWhere, menuList);
							}
						});
					}
				}
			}

		}.start();
	}

	private void initData(int showWhere, final List<MenuEntity> menuList) {
		where = showWhere;
		initWidth = rl_control.getWidth();
		initHeight = rl_control.getHeight();
		int[] location = new int[2];
		iv_controldisk.getLocationInWindow(location);
		initIvDiskX = location[0];
		initIvDiskY = location[1];
		diskWidth = iv_controldisk.getWidth();
		diskHeight = iv_controldisk.getHeight();
		diskRadius = diskWidth / 2;

		rl_controldiskout.getLocationInWindow(location);
		initRlOutX = location[0];
		initRlOutY = location[1];
		outWidth = rl_controldiskout.getWidth();
		outHeight = rl_controldiskout.getHeight();
		outRadius = outWidth / 2;

		setLocation(showWhere);

		if (menuList.size() == 6) {

			int sideWidth = ((outRadius - diskRadius) / 2 + diskRadius) / 2;
			int sideHeight = (int) (((outRadius - diskRadius) / 2 + diskRadius) * 0.866);

			for (int i = 0; i < menuItemList.size(); i++) {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				int marginLeft = 0;
				int marginTop = 0;
				if (i == 0) {
					int itemWidth = menuItemList.get(i).getWidth();
					int itemHeight = menuItemList.get(i).getHeight();
					rl_controldiskout.removeView(menuItemList.get(i));
					menuItemList.get(i).setVisibility(View.VISIBLE);

					marginLeft = outRadius - sideWidth - itemWidth / 2;
					marginTop = outRadius - sideHeight - itemHeight / 2;

				}
				if (i == 1) {
					int itemWidth = menuItemList.get(i).getWidth();
					int itemHeight = menuItemList.get(i).getHeight();
					rl_controldiskout.removeView(menuItemList.get(i));
					menuItemList.get(i).setVisibility(View.VISIBLE);

					marginLeft = (outRadius - diskRadius) / 2 - itemWidth / 2;
					marginTop = outHeight / 2 - itemHeight / 2;
				}
				if (i == 2) {
					int itemWidth = menuItemList.get(i).getWidth();
					int itemHeight = menuItemList.get(i).getHeight();
					rl_controldiskout.removeView(menuItemList.get(i));
					menuItemList.get(i).setVisibility(View.VISIBLE);

					marginLeft = outRadius - sideWidth - itemWidth / 2;
					marginTop = outRadius + sideHeight - itemHeight / 2;
				}
				if (i == 3) {
					int itemWidth = menuItemList.get(i).getWidth();
					int itemHeight = menuItemList.get(i).getHeight();
					rl_controldiskout.removeView(menuItemList.get(i));
					menuItemList.get(i).setVisibility(View.VISIBLE);

					marginLeft = outRadius + sideWidth - itemWidth / 2;
					marginTop = outRadius + sideHeight - itemHeight / 2;
				}
				if (i == 4) {
					int itemWidth = menuItemList.get(i).getWidth();
					int itemHeight = menuItemList.get(i).getHeight();
					rl_controldiskout.removeView(menuItemList.get(i));
					menuItemList.get(i).setVisibility(View.VISIBLE);

					marginLeft = (outRadius - diskRadius) / 2 - itemWidth / 2
							+ outRadius + diskRadius;
					marginTop = outHeight / 2 - itemHeight / 2;
				}
				if (i == 5) {
					int itemWidth = menuItemList.get(i).getWidth();
					int itemHeight = menuItemList.get(i).getHeight();
					rl_controldiskout.removeView(menuItemList.get(i));
					menuItemList.get(i).setVisibility(View.VISIBLE);

					marginLeft = outRadius + sideWidth - itemWidth / 2;
					marginTop = outRadius - sideHeight - itemHeight / 2;
				}
				params.setMargins(marginLeft, marginTop, 0, 0);
				rl_controldiskout.addView(menuItemList.get(i), params);
			}
		}

		gd = new GestureDetector(activity, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				int circleX = ivX + diskRadius;
				int circleY = ivY + diskRadius;
				if (status == SHOW_CENTER) {
					initClick = false;
					int radiusOut = rl_controldiskout.getWidth() / 2;
					if ((clickX - circleX) * (clickX - circleX)
							+ (clickY - circleY) * (clickY - circleY) < diskRadius
							* diskRadius) {
						back(oldWhere);
					} else if ((clickX - circleX) * (clickX - circleX)
							+ (clickY - circleY) * (clickY - circleY) < radiusOut
							* radiusOut) {
						float tan = (clickX - circleX)
								/ ((clickY - circleY) != 0 ? (clickY - circleY)
										: 0.1f);
						if (menuList.size() == 4) {
							if (clickX < circleX) {
								if (tan >= 0 && tan < 1f) {
									System.out.println(1);
								}
								if (tan >= 1f || tan < -1f) {
									System.out.println(2);
								}
								if (tan > -1f && tan < 0) {
									System.out.println(3);
								}
							} else {
								if (tan >= 0 && tan < 1f) {
									System.out.println(3);
								}
								if (tan >= 1f || tan < -1f) {
									System.out.println(4);
								}
								if (tan > -1f && tan < 0) {
									System.out.println(1);
								}
							}
						}
						if (menuList.size() == 6) {
							if (clickX < circleX) {
								if (tan >= 0 && tan < 1.732f) {
									System.out.println(1);
								}
								if (tan >= 1.732f || tan < -1.732f) {
									System.out.println(2);
								}
								if (tan > -1.732f && tan < 0) {
									System.out.println(3);
								}
							} else {
								if (tan >= 0 && tan < 1.732f) {
									System.out.println(4);
								}
								if (tan >= 1.732f || tan < -1.732f) {
									System.out.println(5);
								}
								if (tan > -1.732f && tan < 0) {
									System.out.println(6);
								}
							}
						}
					} else {
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
				if ((status == SHOW_BOTTOM || status == SHOW_TOP) && initClick) {
					status = STATUS_DRAG;
				}
				if (status == STATUS_DRAG) {
					int moveX = (int) (clickX - e2.getX());
					int moveY = (int) (clickY - e2.getY());
					rl_control.scrollTo(moveX, scrollY + moveY);
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

				if (status == SHOW_CENTER && initClick) {
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
				if (status == SHOW_TOP || status == SHOW_CENTER
						|| status == SHOW_BOTTOM) {
					clickX = e.getX();
					clickY = e.getY();

					if (status == SHOW_TOP) {
						ivX = (int) initIvDiskX;
						ivY = (int) initIvDiskY - initHeight / 2;
					} else if (status == SHOW_CENTER) {
						ivX = (int) initIvDiskX;
						ivY = (int) initIvDiskY;
					} else if (status == SHOW_BOTTOM) {
						ivX = (int) initIvDiskX;
						ivY = (int) initIvDiskY + initHeight / 2;
					}

					int circleX = ivX + diskRadius;
					int circleY = ivY + diskRadius;
					if ((clickX - circleX) * (clickX - circleX)
							+ (clickY - circleY) * (clickY - circleY) < diskRadius
							* diskRadius) {
						flag = true;
					}
				}
				if (status != STATUS_ANIMATION) {
					initClick = true;
				} else {
					initClick = false;
					flag = true;
				}

				if (status == SHOW_CENTER) {
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
						if (leaveY > initHeight * 3 / 4) {
							back(SHOW_BOTTOM);
						} else if (leaveY < initHeight / 4) {
							back(SHOW_TOP);
						} else {
							showCircle();
						}
					}
				}
				return gd.onTouchEvent(event);
			}
		});

	};

	private void setLocation(int showWhere) {
		if (showWhere == SHOW_TOP) {
			scrollY = initHeight / 2 + diskHeight / 8;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_TOP;
		}
		if (showWhere == SHOW_BOTTOM) {
			scrollY = -initHeight / 2 - diskHeight / 8;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_BOTTOM;
		}
		if (showWhere == SHOW_CENTER) {
			scrollY = 0;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_CENTER;
		}
	}

	private void showCircle() {
		oldWhere = where;
		status = STATUS_ANIMATION;
		if (where == SHOW_TOP) {
			ivY = ivY + initHeight / 2;
		}
		if (where == SHOW_BOTTOM) {
			ivY = ivY - initHeight / 2;
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
				setLocation(where);
				ScaleAnimation scaleanimation = new ScaleAnimation(0.5f, 1,
						0.5f, 1, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleanimation.setDuration(80);
				rl_controldiskout.setVisibility(View.VISIBLE);
				rl_controldiskout.startAnimation(scaleanimation);
			}
		});
		ta.setDuration(200);
		iv_controldisk.startAnimation(ta);

	}

	private void back(int toWhere) {
		status = STATUS_ANIMATION;
		if (toWhere == SHOW_BOTTOM) {
			if (where == SHOW_TOP) {
				ivY = ivY + initHeight / 2 * 2;
			}
			if (where == SHOW_CENTER) {
				ivY = ivY + initHeight / 2;
			}
			where = SHOW_BOTTOM;
		}
		if (toWhere == SHOW_TOP) {
			if (where == SHOW_BOTTOM) {
				ivY = ivY - initHeight / 2 * 2;
			}
			if (where == SHOW_CENTER) {
				ivY = ivY - initHeight / 2;
			}
			where = SHOW_TOP;
		}
		if (rl_controldiskout.getVisibility() == View.INVISIBLE) {
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
					rl_controldiskout.setVisibility(View.INVISIBLE);
					circleOutBack();
				}
			});
			rl_controldiskout.startAnimation(scaleanimation);
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
				setLocation(where);

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
