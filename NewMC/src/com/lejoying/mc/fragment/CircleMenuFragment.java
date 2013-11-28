package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.adapter.AnimationAdapter;
import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.utils.MenuEntity;
import com.lejoying.mc.utils.ToTry;

public class CircleMenuFragment extends BaseFragment {

	private int mWhere;
	private int mOldWhere;
	private final int WHERE_TOP = 0x01;
	private final int WHERE_CENTER = 0x02;
	private final int WHERE_BOTTOM = 0x03;

	private int mStatus;
	private final int STATUS_SHOW = 0x04;
	private final int STATUS_DRAG = 0x05;
	private final int STATUS_ANIMATION = 0x06;

	private View mCircleMenu;
	private View mDisk;
	private View mDiskOut;
	private TextView mView_back;
	private TextView mView_pageName;

	private int mMenuHeight;
	private int mDiskHeight;
	private int mDiskOutHeight;
	private Point mDiskCenter;
	private int mDiskRadius;
	private int mDiskOutRadius;

	private int mDiskLocation[];

	private int mDistance;
	private int mScrollY;

	private boolean mInitClick;

	private List<List<View>> mMenuItemList;
	private int mMenuIndex;

	private GestureDetector mGestureDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCircleMenu = inflater.inflate(R.layout.f_circlemenu, null);
		mDisk = mCircleMenu.findViewById(R.id.iv_controldisk);
		mDiskOut = mCircleMenu.findViewById(R.id.rl_controldiskout);
		mView_back = (TextView) mCircleMenu.findViewById(R.id.tv_back);
		mView_pageName = (TextView) mCircleMenu.findViewById(R.id.tv_pagename);
		mCircleMenu.setVisibility(View.INVISIBLE);
		initMenu();
		initMenuItem(inflater);
		return mCircleMenu;
	}

	private void initMenu() {
		ToTry.tryDoing(10, 500, new ToTryAdapter() {
			@Override
			public boolean isSuccess() {
				return mCircleMenu.getHeight() != 0 && mDisk.getHeight() != 0
						&& mDiskOut.getHeight() != 0;
			}

			@Override
			public void successed(long time) {
				mMenuHeight = mCircleMenu.getHeight();
				mDiskHeight = mDisk.getHeight();
				mDiskOutHeight = mDiskOut.getHeight();
				mDiskRadius = mDiskHeight / 2;
				mDiskOutRadius = mDiskOutHeight / 2;
				mDistance = mMenuHeight / 2 + mDiskHeight / 8;
				mDiskLocation = new int[2];
				mDisk.getLocationInWindow(mDiskLocation);
				mDiskCenter = new Point();
				initMenu(WHERE_TOP);
			}

		});
	}

	private void initMenu(int where) {
		mStatus = STATUS_SHOW;

		setLocation(where);

		mCircleMenu.setVisibility(View.VISIBLE);

		mCircleMenu.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP && mInitClick) {
					float leaveY = event.getY();
					if (mStatus == STATUS_DRAG) {
						if (leaveY > mMenuHeight * 3 / 4) {
							back(WHERE_BOTTOM, null);
						} else if (leaveY < mMenuHeight / 4) {
							back(WHERE_TOP, null);
						} else {
							showCircle();
						}
					}
				}
				return mGestureDetector.onTouchEvent(event);
			}
		});

		mGestureDetector = new GestureDetector(getActivity(),
				new OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (isInCircle(e.getX(), e.getY(), mDiskRadius)) {
							mInitClick = false;
							if (mWhere == WHERE_CENTER) {
								cancelMenu();
							} else if (mWhere == WHERE_TOP) {

							} else if (mWhere == WHERE_BOTTOM) {
								showCircle();
								mView_pageName.setVisibility(View.INVISIBLE);
							}
						}
						return true;
					}

					@Override
					public void onShowPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {

						if ((mWhere == WHERE_BOTTOM || mWhere == WHERE_TOP)
								&& mInitClick) {
							mStatus = STATUS_DRAG;
						}
						if (mStatus == STATUS_DRAG && mInitClick) {
							int moveX = (int) (e1.getX() - e2.getX());
							int moveY = (int) (mScrollY + e1.getY() - e2.getY());
							if (moveY > mDistance || moveY < -mDistance) {
								moveY = mScrollY;
							}
							mCircleMenu.scrollTo(moveX, moveY);
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
						float leaveY = e2.getY();
						if (leaveY < mMenuHeight * 5 / 6
								&& leaveY > mMenuHeight / 6) {
							if (mWhere != WHERE_TOP && mInitClick) {
								mView_back.setVisibility(View.INVISIBLE);
							}
							if (mWhere != WHERE_BOTTOM && mInitClick) {
								mView_pageName.setVisibility(View.INVISIBLE);
							}
							if (mWhere == WHERE_TOP && mInitClick) {
								if (velocityY > 2000) {
									showCircle();
								}
							}
							if (mWhere == WHERE_BOTTOM && mInitClick) {
								if (velocityY < -2000) {
									showCircle();
								}
							}

							if (mWhere == WHERE_CENTER && mInitClick) {
								if (velocityY > 2000) {
									back(WHERE_BOTTOM, null);
								}
								if (velocityY < -2000) {
									back(WHERE_TOP, null);
								}
							}
						}
						return true;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						boolean flag = false;
						if (mStatus == STATUS_SHOW) {
							switch (mWhere) {
							case WHERE_TOP:
								mDiskCenter.set(mDiskLocation[0] + mDiskRadius,
										mDiskLocation[1] - mDistance
												+ mDiskRadius);
								break;
							case WHERE_CENTER:
								mDiskCenter.set(mDiskLocation[0] + mDiskRadius,
										mDiskLocation[1] + mDiskRadius);
								break;
							case WHERE_BOTTOM:
								mDiskCenter.set(mDiskLocation[0] + mDiskRadius,
										mDiskLocation[1] + mDistance
												+ mDiskRadius);
								break;
							}

							float clickX = e.getX();
							float clickY = e.getY();

							if (isInCircle(clickX, clickY, mDiskRadius)) {
								flag = true;
							}
							mInitClick = true;
						} else {
							mInitClick = false;
							flag = false;
						}

						if (mWhere == WHERE_CENTER) {
							flag = true;
						}

						return flag;
					}
				});
	}

	private void initMenuItem(LayoutInflater inflater) {
		mMenuIndex = 0;
		mMenuItemList = new ArrayList<List<View>>();
		List<MenuEntity> menuEntitys1 = new ArrayList<MenuEntity>();
		menuEntitys1.add(new MenuEntity(R.drawable.test_menu_item1, "密友圈"));
		menuEntitys1.add(new MenuEntity(R.drawable.test_menu_item2, "消息"));
		menuEntitys1.add(new MenuEntity(R.drawable.test_menu_item3, "分享"));
		menuEntitys1.add(new MenuEntity(R.drawable.test_menu_item4, "更多"));
		List<MenuEntity> menuEntitys2 = new ArrayList<MenuEntity>();
		menuEntitys2.add(new MenuEntity(R.drawable.test_menu_item1, "扫一扫"));
		menuEntitys2.add(new MenuEntity(R.drawable.test_menu_item2, "我的名片"));
		menuEntitys2.add(new MenuEntity(R.drawable.test_menu_item3, "社区服务"));
		menuEntitys2.add(new MenuEntity(R.drawable.test_menu_item4, "返回"));
		menuEntitys2.add(new MenuEntity(R.drawable.test_menu_item5, "订单"));
		menuEntitys2.add(new MenuEntity(R.drawable.test_menu_item6, "资金账户"));

		ViewGroup itemGroup = (ViewGroup) mDiskOut;

		mMenuItemList.add(inflaterMenuItemView(inflater, menuEntitys1,
				itemGroup));
		mMenuItemList.add(inflaterMenuItemView(inflater, menuEntitys2,
				itemGroup));

		ToTry.tryDoing(10, 500, new ToTryAdapter() {
			@Override
			public boolean isSuccess() {
				return true;
			}

			@Override
			public void successed(long time) {
				super.successed(time);
			}
		});
	}

	private List<View> inflaterMenuItemView(LayoutInflater inflater,
			List<MenuEntity> entities, ViewGroup itemGroup) {
		List<View> views = new ArrayList<View>();
		for (MenuEntity entity : entities) {
			ViewGroup menuItemView = (ViewGroup) inflater.inflate(
					R.layout.f_circlemenu_item, null);
			((TextView) menuItemView.findViewById(R.id.tv_text)).setText(entity
					.getText());
			((ImageView) menuItemView.findViewById(R.id.iv_image))
					.setImageResource(entity.getImageID());
			menuItemView.setVisibility(View.INVISIBLE);
			views.add(menuItemView);
			itemGroup.addView(menuItemView);
		}
		return views;
	}

	private void setLocation(int where) {
		mWhere = where;
		switch (where) {
		case WHERE_TOP:
			mScrollY = mDistance;
			break;
		case WHERE_CENTER:
			mScrollY = 0;
			break;
		case WHERE_BOTTOM:
			mScrollY = -mDistance;
			break;
		default:
			break;
		}
		mCircleMenu.scrollTo(0, mScrollY);
	}

	private void showCircle() {
		mOldWhere = mWhere;
		mStatus = STATUS_ANIMATION;
		int[] nowLocation = new int[2];
		mDisk.getLocationInWindow(nowLocation);

		TranslateAnimation ta = new TranslateAnimation(0, mDiskLocation[0]
				- nowLocation[0], 0, mDiskLocation[1] - nowLocation[1]);

		ta.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				mDisk.clearAnimation();
				setLocation(WHERE_CENTER);
				ScaleAnimation scaleanimation = new ScaleAnimation(0.5f, 1,
						0.5f, 1, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleanimation.setDuration(80);
				mDiskOut.setVisibility(View.VISIBLE);
				mDiskOut.startAnimation(scaleanimation);
				mStatus = STATUS_SHOW;
			}
		});
		ta.setDuration(200);
		mDisk.startAnimation(ta);

	}

	private void cancelMenu() {
		back(mOldWhere, null);
	}

	private void back(final int toWhere,
			final CircleDiskAnimationEnd animationEnd) {
		mStatus = STATUS_ANIMATION;
		if (mDiskOut.getVisibility() == View.INVISIBLE) {
			backDisk(toWhere, animationEnd);
		} else {
			ScaleAnimation scaleanimation = new ScaleAnimation(1, 0.3f, 1,
					0.3f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			scaleanimation.setDuration(100);
			scaleanimation.setAnimationListener(new AnimationAdapter() {
				@Override
				public void onAnimationEnd(Animation animation) {
					mDiskOut.clearAnimation();
					mDiskOut.setVisibility(View.INVISIBLE);
					if (animationEnd != null) {
						animationEnd.outAnimationEnd();
					}
					backDisk(toWhere, animationEnd);
				}
			});
			mDiskOut.startAnimation(scaleanimation);
		}
	}

	private void backDisk(final int toWhere,
			final CircleDiskAnimationEnd animationEnd) {
		int[] nowLocation = new int[2];
		mDisk.getLocationInWindow(nowLocation);
		int toX = mDiskLocation[0];
		int toY = 0;
		switch (toWhere) {
		case WHERE_TOP:
			toY = mDiskLocation[1] - mDistance;
			break;
		case WHERE_BOTTOM:
			toY = mDiskLocation[1] + mDistance;
			break;
		}
		TranslateAnimation ta = new TranslateAnimation(0, toX - nowLocation[0],
				0, toY - nowLocation[1]);

		ta.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				mDisk.clearAnimation();
				setLocation(toWhere);
				if (animationEnd != null) {
					animationEnd.diskAnimationEnd();
				}
				mStatus = STATUS_SHOW;
			}
		});
		ta.setDuration(200);
		mDisk.startAnimation(ta);
	}

	private boolean isInCircle(float clickX, float clickY, int radius) {
		return (clickX - mDiskCenter.x) * (clickX - mDiskCenter.x)
				+ (clickY - mDiskCenter.y) * (clickY - mDiskCenter.y) < radius
				* radius;
	}

	private interface CircleDiskAnimationEnd {
		public void diskAnimationEnd();

		public void outAnimationEnd();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
