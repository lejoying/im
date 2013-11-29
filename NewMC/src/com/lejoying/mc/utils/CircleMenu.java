package com.lejoying.mc.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.adapter.AnimationAdapter;
import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.listener.OldCircleMenuItemClickListener;

public class CircleMenu {

	public final static String CIRCLE_MORE = "¸ü¶à";
	public final static String CIRCLE_BACK = "·µ»Ø";

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
	private TextView tv_back;
	private TextView tv_pagename;

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
	private boolean initMenu;

	private List<List<View>> allMenuItemList;
	private List<List<MenuEntity>> menuEntityList;

	private boolean lock;
	private boolean noItem;

	private int nowShowMenuIndex;

	private ViewGroup contentView;

	private OldCircleMenuItemClickListener circleMenuItemClickListener;

	public CircleMenu(Activity activity) {
		this.activity = activity;
		this.handler = MCTools.handler;
	}

	public void setCircleMenuItemClickListener(
			OldCircleMenuItemClickListener circleMenuItemClickListener) {
		this.circleMenuItemClickListener = circleMenuItemClickListener;
	}

	public boolean isShow() {
		boolean flag = false;
		if (status != SHOW_TOP && status != SHOW_BOTTOM) {
			flag = true;
		}
		return flag;
	}

	public void cancelMenu() {
		if (nowShowMenuIndex == 0) {
			back(oldWhere != 0 ? oldWhere : SHOW_TOP, null);
		} else {
			showBack();
		}
	}

	public void showMenu(final int showWhere, final List<MenuEntity> menuList,
			boolean lock) {
		if (init) {
			return;
		}
		init = true;
		this.lock = lock;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rl_control = (RelativeLayout) inflater.inflate(R.layout.circlemenu,
				null);
		rl_control.setVisibility(View.INVISIBLE);

		iv_controldisk = (ImageView) rl_control
				.findViewById(R.id.iv_controldisk);
		rl_controldiskout = (RelativeLayout) rl_control
				.findViewById(R.id.rl_controldiskout);

		tv_back = (TextView) rl_control.findViewById(R.id.tv_back);
		tv_pagename = (TextView) rl_control.findViewById(R.id.tv_pagename);

		contentView = getContentView(activity);
		if (contentView != null) {
			contentView.addView(rl_control, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		} else {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			activity.addContentView(rl_control, params);
		}

		allMenuItemList = new ArrayList<List<View>>();
		menuEntityList = new ArrayList<List<MenuEntity>>();

		addMore(menuList);

		ToTry.tryDoing(10, 1000, new ToTryAdapter() {

			@Override
			public boolean isSuccess() {
				return rl_control.getHeight() != 0;
			}

			@Override
			public void successed(long time) {
				rl_control.setVisibility(View.VISIBLE);
				initData(showWhere);
				initMenu = true;
				for (List<MenuEntity> list : menuEntityList) {
					if (allMenuItemList.size() == 0) {
						addMore(list, true);
					} else {
						addMore(list, false);
					}
				}
			}

		});
	}

	private void showBack() {
		if (nowShowMenuIndex - 1 >= 0) {
			showMenu(nowShowMenuIndex, nowShowMenuIndex -= 1);
		}
	}

	private void showNext() {
		if (nowShowMenuIndex + 1 < allMenuItemList.size()) {
			showMenu(nowShowMenuIndex, nowShowMenuIndex += 1);
		}
	}

	private void showMenu(final int now, final int next) {
		ScaleAnimation scaleanimation = new ScaleAnimation(1, 0.3f, 1, 0.3f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleanimation.setDuration(40);
		scaleanimation.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				for (View v : allMenuItemList.get(now)) {
					v.setVisibility(View.GONE);
				}
				for (View v : allMenuItemList.get(next)) {
					v.setVisibility(View.VISIBLE);
				}
				ScaleAnimation scaleanimation = new ScaleAnimation(0.5f, 1,
						0.5f, 1, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleanimation.setDuration(80);
				rl_controldiskout.startAnimation(scaleanimation);
			}
		});
		rl_controldiskout.startAnimation(scaleanimation);
	}

	public void addMore(List<MenuEntity> menuList) {
		if (initMenu == true) {
			return;
		}
		if (menuList == null || menuList.size() == 0 && menuList.size() != 4
				&& menuList.size() != 6 && menuEntityList.size() != 0) {
			noItem = true;
			return;
		} else {
			noItem = false;
		}
		menuEntityList.add(menuList);
	}

	private void addMore(List<MenuEntity> menuList, final boolean first) {
		final List<View> menuItemList = new ArrayList<View>();
		for (MenuEntity entity : menuList) {
			RelativeLayout rl_menuitem = (RelativeLayout) inflater.inflate(
					R.layout.circlemenu_item, null);
			ImageView iv_image = (ImageView) rl_menuitem
					.findViewById(R.id.iv_image);
			TextView tv_text = (TextView) rl_menuitem
					.findViewById(R.id.tv_text);

			Drawable d = activity.getResources().getDrawable(
					entity.getImageID());
			iv_image.setImageDrawable(d);
			tv_text.setText(entity.getText());

			menuItemList.add(rl_menuitem);
			rl_menuitem.setVisibility(View.INVISIBLE);

			rl_controldiskout.addView(rl_menuitem);
		}
		allMenuItemList.add(menuItemList);

		if (first) {
			initMenu = true;
		}
		new Thread() {
			boolean addMore = true;

			@Override
			public void run() {
				while (addMore) {
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (menuItemList.get(menuItemList.size() - 1).getHeight() != 0) {
						addMore = false;
						handler.post(new Runnable() {
							@Override
							public void run() {
								initMenu(menuItemList, first);
							}
						});
					}
				}
			}
		}.start();

	}

	private void initMenu(List<View> menuList, boolean show) {
		if (!noItem) {
			if (menuList.size() == 6 || menuList.size() == 4) {

				int sideWidth = ((outRadius - diskRadius) / 2 + diskRadius) / 2;
				int sideHeight = (int) (((outRadius - diskRadius) / 2 + diskRadius) * 0.866);

				for (int i = 0; i < menuList.size(); i++) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					int marginLeft = 0;
					int marginTop = 0;

					int itemWidth = menuList.get(i).getWidth();
					int itemHeight = menuList.get(i).getHeight();
					rl_controldiskout.removeView(menuList.get(i));
					if (show) {
						menuList.get(i).setVisibility(View.VISIBLE);
					}
					if (i == 0) {
						if (menuList.size() == 6) {
							marginLeft = outRadius - sideWidth - itemWidth / 2;
							marginTop = outRadius - sideHeight - itemHeight / 2;
						} else if (menuList.size() == 4) {
							marginLeft = (outWidth - itemWidth) / 2;
							marginTop = (outRadius - diskRadius - itemHeight) / 2;
						}
					} else if (i == 1) {
						if (menuList.size() == 6) {
							marginLeft = (outRadius - diskRadius) / 2
									- itemWidth / 2;
							marginTop = (outHeight - itemHeight) / 2;
						} else if (menuList.size() == 4) {
							marginLeft = (outRadius - diskRadius - itemWidth) / 2;
							marginTop = outHeight / 2 - itemHeight / 2;
						}
					} else if (i == 2) {
						if (menuList.size() == 6) {
							marginLeft = outRadius - sideWidth - itemWidth / 2;
							marginTop = outRadius + sideHeight - itemHeight / 2;
						} else if (menuList.size() == 4) {
							marginLeft = (outWidth - itemWidth) / 2;
							marginTop = (3 * outRadius + diskRadius - itemHeight) / 2;
						}
					} else if (i == 3) {
						if (menuList.size() == 6) {
							marginLeft = outRadius + sideWidth - itemWidth / 2;
							marginTop = outRadius + sideHeight - itemHeight / 2;
						} else if (menuList.size() == 4) {
							marginLeft = (3 * outRadius + diskRadius - itemWidth) / 2;
							marginTop = outHeight / 2 - itemHeight / 2;
						}
					} else if (i == 4) {
						marginLeft = (outRadius - diskRadius) / 2 - itemWidth
								/ 2 + outRadius + diskRadius;
						marginTop = (outHeight - itemHeight) / 2;
					} else if (i == 5) {
						marginLeft = outRadius + sideWidth - itemWidth / 2;
						marginTop = outRadius - sideHeight - itemHeight / 2;
					}
					params.setMargins(marginLeft, marginTop, 0, 0);
					rl_controldiskout.addView(menuList.get(i), params);
				}
			}
		}
	}

	private void initData(int showWhere) {
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

		gd = new GestureDetector(activity, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				int circleX = ivX + diskRadius;
				int circleY = ivY + diskRadius;

				if ((clickX - circleX) * (clickX - circleX)
						+ (clickY - circleY) * (clickY - circleY) < diskRadius
						* diskRadius) {
					initClick = false;
					if (status == SHOW_CENTER) {
						cancelMenu();
					} else if (status == SHOW_TOP) {
						activity.finish();
					} else if (status == SHOW_BOTTOM) {
						showCircle();
						tv_pagename.setVisibility(View.INVISIBLE);
					}
				} else if (status == SHOW_CENTER && !noItem) {
					initClick = false;
					int radiusOut = rl_controldiskout.getWidth() / 2;
					if ((clickX - circleX) * (clickX - circleX)
							+ (clickY - circleY) * (clickY - circleY) < radiusOut
							* radiusOut) {
						float tan = (clickX - circleX)
								/ ((clickY - circleY) != 0 ? (clickY - circleY)
										: 0.1f);
						if (allMenuItemList.get(nowShowMenuIndex).size() == 4) {
							int item = -1;
							if (clickX < circleX) {
								if (tan >= 0 && tan < 1f) {
									// System.out.println(1);
									item = 1;
								}
								if (tan >= 1f || tan < -1f) {
									// System.out.println(2);
									item = 2;
								}
								if (tan > -1f && tan < 0) {
									// System.out.println(3);
									item = 3;
								}
							} else {
								if (tan >= 0 && tan < 1f) {
									// System.out.println(3);
									item = 3;
								}
								if (tan >= 1f || tan < -1f) {
									// System.out.println(4);
									item = 4;
								}
								if (tan > -1f && tan < 0) {
									// System.out.println(1);
									item = 1;
								}
							}
							handlerMenuItemClick(item);
						} else if (allMenuItemList.get(nowShowMenuIndex).size() == 6) {
							int item = -1;
							if (clickX < circleX) {
								if (tan >= 0 && tan < 1.732f) {
									// System.out.println(1);
									item = 1;
								}
								if (tan >= 1.732f || tan < -1.732f) {
									// System.out.println(2);
									item = 2;
								}
								if (tan > -1.732f && tan < 0) {
									// System.out.println(3);
									item = 3;
								}
							} else {
								if (tan >= 0 && tan < 1.732f) {
									// System.out.println(4);
									item = 4;
								}
								if (tan >= 1.732f || tan < -1.732f) {
									// System.out.println(5);
									item = 5;
								}
								if (tan > -1.732f && tan < 0) {
									// System.out.println(6);
									item = 6;
								}
							}
							handlerMenuItemClick(item);
						}
					} else {
						// cancelMenu();
						back(oldWhere, null);
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
				if (status != SHOW_TOP && initClick) {
					tv_back.setVisibility(View.INVISIBLE);
				}
				if (status != SHOW_BOTTOM && initClick) {
					tv_pagename.setVisibility(View.INVISIBLE);
				}
				if ((status == SHOW_BOTTOM || status == SHOW_TOP) && initClick
						&& !lock) {
					status = STATUS_DRAG;
				}
				if (status == STATUS_DRAG && !lock) {
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
				if (status != SHOW_TOP && initClick) {
					tv_back.setVisibility(View.INVISIBLE);
				}
				if (status != SHOW_BOTTOM && initClick) {
					tv_pagename.setVisibility(View.INVISIBLE);
				}
				if (where == SHOW_TOP && initClick && !lock) {
					if (velocityY > 2000) {
						showCircle();
					}
				}
				if (where == SHOW_BOTTOM && initClick && !lock) {
					if (velocityY < -2000) {
						showCircle();
					}
				}

				if (status == SHOW_CENTER && initClick && !lock) {
					if (velocityY > 2000) {
						back(SHOW_BOTTOM, null);
					}
					if (velocityY < -2000) {
						back(SHOW_TOP, null);
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
				if (event.getAction() == MotionEvent.ACTION_UP && !lock) {
					if (status == STATUS_DRAG) {
						float leaveY = event.getY();
						if (leaveY > initHeight * 3 / 4) {
							back(SHOW_BOTTOM, null);
						} else if (leaveY < initHeight / 4) {
							back(SHOW_TOP, null);
						} else {
							showCircle();
						}
					}
				}
				return gd.onTouchEvent(event);
			}
		});

	};

	private void handlerMenuItemClick(final int item) {
		if (item != -1) {
			View clickView = allMenuItemList.get(nowShowMenuIndex)
					.get(item - 1);
			final ImageView iv_image = (ImageView) clickView
					.findViewById(R.id.iv_image);
			final TextView tv_text = (TextView) clickView
					.findViewById(R.id.tv_text);
			if (tv_text.getText().toString().equals(CIRCLE_MORE)) {
				showNext();
				return;
			}
			if (tv_text.getText().toString().equals(CIRCLE_BACK)) {
				showBack();
				return;
			}
			if (circleMenuItemClickListener != null) {
				final int temp = nowShowMenuIndex * 10;
				back(oldWhere != 0 ? oldWhere : SHOW_TOP,
						new CircleDiskAnimationEnd() {
							@Override
							public void diskAnimationEnd() {
								circleMenuItemClickListener.onItemClick(item
										+ temp, iv_image, tv_text);
							}

							@Override
							public void outAnimationEnd() {
								// TODO Auto-generated
								// method stub

							}

						});
			}
		}
	}

	private void setLocation(int showWhere) {
		if (showWhere == SHOW_TOP) {
			scrollY = initHeight / 2 + diskHeight / 8;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_TOP;
			tv_back.setVisibility(View.VISIBLE);
		}
		if (showWhere == SHOW_BOTTOM) {
			scrollY = -initHeight / 2 - diskHeight / 8;
			rl_control.scrollTo(0, scrollY);
			status = SHOW_BOTTOM;
			tv_pagename.setVisibility(View.VISIBLE);
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

		ta.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_controldisk.clearAnimation();
				setLocation(where);
				if (!noItem) {
					ScaleAnimation scaleanimation = new ScaleAnimation(0.5f, 1,
							0.5f, 1, Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					scaleanimation.setDuration(80);
					rl_controldiskout.setVisibility(View.VISIBLE);
					rl_controldiskout.startAnimation(scaleanimation);
				}
			}
		});
		ta.setDuration(200);
		iv_controldisk.startAnimation(ta);

	}

	private void back(int toWhere, final CircleDiskAnimationEnd animationEnd) {
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
			circleDiskBack(animationEnd);
		} else {
			ScaleAnimation scaleanimation = new ScaleAnimation(1, 0.3f, 1,
					0.3f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			scaleanimation.setDuration(100);
			scaleanimation.setAnimationListener(new AnimationAdapter() {
				@Override
				public void onAnimationEnd(Animation animation) {
					rl_controldiskout.setVisibility(View.INVISIBLE);
					if (animationEnd != null) {
						animationEnd.outAnimationEnd();
					}
					if (!noItem) {
						circleDiskBack(animationEnd);
					}
				}
			});
			rl_controldiskout.startAnimation(scaleanimation);
		}
	}

	private void circleDiskBack(final CircleDiskAnimationEnd animationEnd) {
		if (nowShowMenuIndex != 0) {
			for (View v : allMenuItemList.get(nowShowMenuIndex)) {
				v.setVisibility(View.GONE);
			}
			nowShowMenuIndex = 0;
			for (View v : allMenuItemList.get(nowShowMenuIndex)) {
				v.setVisibility(View.VISIBLE);
			}
		}
		int[] nowlocation = new int[2];
		iv_controldisk.getLocationInWindow(nowlocation);
		TranslateAnimation ta = new TranslateAnimation(0, ivX - nowlocation[0],
				0, ivY - nowlocation[1]);

		ta.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_controldisk.clearAnimation();
				setLocation(where);
				if (animationEnd != null) {
					animationEnd.diskAnimationEnd();
				}
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

	public void hideMenu() {
		init = false;
		if (status == SHOW_TOP) {
			Animation animation = AnimationUtils.loadAnimation(activity,
					R.anim.tran_out_top);
			animation.setAnimationListener(new AnimationAdapter() {
				@Override
				public void onAnimationEnd(Animation animation) {
					rl_control.setVisibility(View.GONE);
					rl_control.clearAnimation();
				};
			});
			rl_control.startAnimation(animation);
		} else if (status == SHOW_BOTTOM) {
			Animation animation = AnimationUtils.loadAnimation(activity,
					R.anim.tran_out_bottom);
			animation.setAnimationListener(new AnimationAdapter() {
				@Override
				public void onAnimationEnd(Animation arg0) {
					rl_control.setVisibility(View.GONE);
					rl_control.clearAnimation();
				}
			});
			rl_control.startAnimation(animation);
		} else if (status == SHOW_CENTER) {
			rl_control.setVisibility(View.GONE);
		}

	}

	private interface CircleDiskAnimationEnd {
		public void diskAnimationEnd();

		public void outAnimationEnd();
	}

	public int getMarginTop() {
		return diskHeight / 8 * 3;
	}

}
