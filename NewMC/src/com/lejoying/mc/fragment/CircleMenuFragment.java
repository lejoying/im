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
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.adapter.AnimationAdapter;
import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.entity.MenuEntity;
import com.lejoying.mc.utils.ToTry;
import com.lejoying.mc.view.CircleMenuView;
import com.lejoying.mc.view.CircleMenuView.SizeChangedListener;

public class CircleMenuFragment extends BaseFragment {

	private int mWhere;
	private int mOldWhere;
	private final int WHERE_TOP = 0x01;
	private final int WHERE_CENTER = 0x02;
	private final int WHERE_BOTTOM = 0x03;

	private int mStatus;
	private final int STATUS_SHOW = 0x04;
	private final int STATUS_HIDE = 0x05;
	private final int STATUS_DRAG = 0x06;
	private final int STATUS_ANIMATION = 0x07;

	private CircleMenuView mCircleMenu;
	private View mDisk;
	private View mDiskOut;
	private TextView mView_back;
	private TextView mView_pageName;
	private TextView mView_appName;

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
	private boolean mLock;
	private boolean mShowBack;
	private boolean mIsCreated;

	private List<List<View>> mMenuItemList;
	private int mMenuIndex;

	private GestureDetector mGestureDetector;

	private CircleMenuListener mCircleMenuListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCircleMenu = (CircleMenuView) inflater.inflate(R.layout.f_circlemenu,
				null);
		mDisk = mCircleMenu.findViewById(R.id.rl_controldisk);
		mDiskOut = mCircleMenu.findViewById(R.id.rl_controldiskout);
		mView_back = (TextView) mCircleMenu.findViewById(R.id.tv_back);
		mView_pageName = (TextView) mCircleMenu.findViewById(R.id.tv_pagename);
		mView_appName = (TextView) mCircleMenu.findViewById(R.id.tv_app);
		mCircleMenu.setVisibility(View.INVISIBLE);
		initMenu(inflater);
		return mCircleMenu;
	}

	public void setPageName(String pageName) {
		mView_pageName.setText(pageName);
	}

	public void showToTop(final boolean lock, final boolean showBack) {
		if (mCircleMenu == null) {
			return;
		}
		this.mLock = lock;
		this.mShowBack = showBack;

		mView_pageName.setVisibility(View.INVISIBLE);

		if (mStatus == STATUS_HIDE) {
			mStatus = STATUS_SHOW;
			setLocation(WHERE_TOP);
			TranslateAnimation animation = new TranslateAnimation(0, 0,
					-mDiskRadius, 0);
			animation.setDuration(120);
			mCircleMenu.setVisibility(View.VISIBLE);
			mDisk.startAnimation(animation);
		} else if (mStatus == STATUS_SHOW && mWhere == WHERE_TOP) {
			if (showBack) {
				mView_back.setVisibility(View.VISIBLE);
				mView_appName.setVisibility(View.INVISIBLE);
			} else {
				mView_back.setVisibility(View.INVISIBLE);
				mView_appName.setVisibility(View.VISIBLE);
			}
		} else if (mWhere == WHERE_BOTTOM) {

			hideCircleMenu(new AnimationAdapter() {
				@Override
				public void onAnimationEnd(Animation animation) {
					showToTop(lock, showBack);
				}
			});
		}
	}

	public void showToBottom() {
		this.mLock = false;
		if (mStatus == STATUS_HIDE) {
			mStatus = STATUS_SHOW;
			setLocation(WHERE_BOTTOM);
			TranslateAnimation animation = new TranslateAnimation(0, 0,
					mDiskRadius, 0);
			animation.setDuration(150);
			mCircleMenu.setVisibility(View.VISIBLE);
			mDisk.startAnimation(animation);
		} else if (mWhere == WHERE_TOP) {
			hideCircleMenu(new AnimationAdapter() {
				@Override
				public void onAnimationEnd(Animation animation) {
					showToBottom();
				}
			});
		}
	}

	public void hideCircleMenu(final AnimationAdapter adapter) {
		if (mCircleMenu == null) {
			return;
		}
		if (mStatus != STATUS_HIDE) {
			mStatus = STATUS_HIDE;
			float toXDelta = 0;
			float toYDelta = 0;
			switch (mWhere) {
			case WHERE_TOP:
				toYDelta = -mDiskRadius;
				break;
			case WHERE_BOTTOM:
				toYDelta = mDiskRadius;
				break;
			default:
				break;
			}
			TranslateAnimation animation = new TranslateAnimation(0, toXDelta,
					0, toYDelta);
			animation.setDuration(80);
			animation.setAnimationListener(new AnimationAdapter() {
				@Override
				public void onAnimationEnd(Animation animation) {
					mCircleMenu.setVisibility(View.INVISIBLE);
					if (adapter != null) {
						adapter.onAnimationEnd(animation);
					}
				}
			});
			mDisk.startAnimation(animation);
		}
	}

	private void initMenu(final LayoutInflater inflater) {
		mDiskLocation = new int[2];
		mCircleMenu.setSizeChangedListener(new SizeChangedListener() {
			@Override
			public void sizeChanged(int w, int h, int oldw, int oldh) {
				if (oldw != 0 && oldh != 0) {
					mMenuHeight = h;
					mDistance = mMenuHeight / 2 + mDiskHeight / 8;
					mDiskLocation[1] = mDiskLocation[1] - (oldh - h) / 2;
					if (h <= mDiskOutHeight) {
						mLock = true;
					} else {
						mLock = false;
					}
					setLocation(mWhere);
				}
			}
		});
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
				mDisk.getLocationOnScreen(mDiskLocation);
				mDiskCenter = new Point();
				initMenu(WHERE_TOP);
				initMenuItem(inflater);
			}

		});
	}

	private void initMenu(int where) {
		mStatus = STATUS_HIDE;

		setLocation(where);

		mCircleMenu.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP && mInitClick) {
					float leaveY = event.getRawY();
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
						if (mStatus != STATUS_SHOW || !mInitClick) {
							return false;
						}
						float clickX = e.getRawX();
						float clickY = e.getRawY();
						if (isInCircle(clickX, clickY, mDiskRadius)) {
							if (mWhere == WHERE_CENTER) {
								cancelMenu();
							} else if (mWhere == WHERE_TOP) {
								if (mShowBack) {
									getActivity().getSupportFragmentManager()
											.popBackStack();
								} else if (!mLock) {
									mView_appName.setVisibility(View.INVISIBLE);
									showCircle();
								}
							} else if (mWhere == WHERE_BOTTOM) {
								showCircle();
								mView_pageName.setVisibility(View.INVISIBLE);
								mView_appName.setVisibility(View.INVISIBLE);
							}
						} else if (mWhere == WHERE_CENTER) {
							if (isInCircle(clickX, clickY, mDiskOutRadius)) {
								float tan = (clickX - mDiskCenter.x)
										/ ((clickY - mDiskCenter.y) != 0 ? (clickY - mDiskCenter.y)
												: 0.1f);
								int item = -1;
								if (mMenuItemList.get(mMenuIndex).size() == 4) {
									if (clickX < mDiskCenter.x) {
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
								} else if (mMenuItemList.get(mMenuIndex).size() == 6) {
									if (clickX < mDiskCenter.x) {
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
								}
								onItemClick(item + mMenuIndex * 10);
							} else {
								// cancelMenu();
								back(mOldWhere, null);
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
						if (mLock) {
							return false;
						}
						if (mInitClick) {
							mView_appName.setVisibility(View.INVISIBLE);
							mView_pageName.setVisibility(View.INVISIBLE);
							mView_back.setVisibility(View.INVISIBLE);
						}
						if ((mWhere == WHERE_BOTTOM || mWhere == WHERE_TOP)
								&& mInitClick) {
							mStatus = STATUS_DRAG;
						}
						if (mStatus == STATUS_DRAG && mInitClick) {
							int moveX = (int) (e1.getRawX() - e2.getRawX());
							int moveY = (int) (mScrollY + e1.getRawY() - e2
									.getRawY());
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
						if (mLock) {
							return false;
						}
						float leaveY = e2.getRawY();
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

							float clickX = e.getRawX();
							float clickY = e.getRawY();

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

		final ViewGroup itemGroup = (ViewGroup) mDiskOut;

		mMenuItemList.add(inflaterMenuItemView(inflater, menuEntitys1,
				itemGroup));
		mMenuItemList.add(inflaterMenuItemView(inflater, menuEntitys2,
				itemGroup));

		ToTry.tryDoing(10, 500, new ToTryAdapter() {
			boolean first = true;

			@Override
			public boolean isSuccess() {
				List<View> view = mMenuItemList.get(mMenuItemList.size() - 1);
				View last = view.get(view.size() - 1);
				return last.getWidth() != 0 && last.getHeight() != 0;
			}

			@Override
			public void successed(long time) {
				for (List<View> views : mMenuItemList) {
					initMenuItem(views, itemGroup, first);
					first = false;
				}
				mIsCreated = true;
				if (mCircleMenuListener != null) {
					mCircleMenuListener.onCreated();
				}
			}
		});
	}

	private void initMenuItem(List<View> menuList, ViewGroup itemGroup,
			boolean show) {
		if (menuList.size() == 6 || menuList.size() == 4) {

			int sideWidth = ((mDiskOutRadius - mDiskRadius) / 2 + mDiskRadius) / 2;
			int sideHeight = (int) (((mDiskOutRadius - mDiskRadius) / 2 + mDiskRadius) * 0.866);

			for (int i = 0; i < menuList.size(); i++) {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				int marginLeft = 0;
				int marginTop = 0;

				int itemWidth = menuList.get(i).getWidth();
				int itemHeight = menuList.get(i).getHeight();
				itemGroup.removeView(menuList.get(i));
				if (show) {
					menuList.get(i).setVisibility(View.VISIBLE);
				}
				if (i == 0) {
					if (menuList.size() == 6) {
						marginLeft = mDiskOutRadius - sideWidth - itemWidth / 2;
						marginTop = mDiskOutRadius - sideHeight - itemHeight
								/ 2;
					} else if (menuList.size() == 4) {
						marginLeft = (2 * mDiskOutRadius - itemWidth) / 2;
						marginTop = (mDiskOutRadius - mDiskRadius - itemHeight) / 2;
					}
				} else if (i == 1) {
					if (menuList.size() == 6) {
						marginLeft = (mDiskOutRadius - mDiskRadius) / 2
								- itemWidth / 2;
						marginTop = (2 * mDiskOutRadius - itemHeight) / 2;
					} else if (menuList.size() == 4) {
						marginLeft = (mDiskOutRadius - mDiskRadius - itemWidth) / 2;
						marginTop = mDiskOutRadius - itemHeight / 2;
					}
				} else if (i == 2) {
					if (menuList.size() == 6) {
						marginLeft = mDiskOutRadius - sideWidth - itemWidth / 2;
						marginTop = mDiskOutRadius + sideHeight - itemHeight
								/ 2;
					} else if (menuList.size() == 4) {
						marginLeft = (2 * mDiskOutRadius - itemWidth) / 2;
						marginTop = (3 * mDiskOutRadius + mDiskRadius - itemHeight) / 2;
					}
				} else if (i == 3) {
					if (menuList.size() == 6) {
						marginLeft = mDiskOutRadius + sideWidth - itemWidth / 2;
						marginTop = mDiskOutRadius + sideHeight - itemHeight
								/ 2;
					} else if (menuList.size() == 4) {
						marginLeft = (3 * mDiskOutRadius + mDiskRadius - itemWidth) / 2;
						marginTop = mDiskOutRadius - itemHeight / 2;
					}
				} else if (i == 4) {
					marginLeft = (mDiskOutRadius - mDiskRadius) / 2 - itemWidth
							/ 2 + mDiskOutRadius + mDiskRadius;
					marginTop = (2 * mDiskOutRadius - itemHeight) / 2;
				} else if (i == 5) {
					marginLeft = mDiskOutRadius + sideWidth - itemWidth / 2;
					marginTop = mDiskOutRadius - sideHeight - itemHeight / 2;
				}
				params.setMargins(marginLeft, marginTop, 0, 0);
				itemGroup.addView(menuList.get(i), params);
			}
		}
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

	private void showBack() {
		if (mMenuIndex - 1 >= 0) {
			showMenu(mMenuIndex, mMenuIndex -= 1);
		}
	}

	private void showNext() {
		if (mMenuIndex + 1 < mMenuItemList.size()) {
			showMenu(mMenuIndex, mMenuIndex += 1);
		}
	}

	private void showMenu(final int now, final int next) {
		mStatus = STATUS_ANIMATION;
		ScaleAnimation scaleanimation = new ScaleAnimation(1, 0.3f, 1, 0.3f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleanimation.setDuration(40);
		scaleanimation.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				for (View v : mMenuItemList.get(now)) {
					v.setVisibility(View.GONE);
				}
				for (View v : mMenuItemList.get(next)) {
					v.setVisibility(View.VISIBLE);
				}
				ScaleAnimation scaleanimation = new ScaleAnimation(0.5f, 1,
						0.5f, 1, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleanimation.setDuration(80);
				scaleanimation.setAnimationListener(new AnimationAdapter() {
					@Override
					public void onAnimationEnd(Animation animation) {
						mStatus = STATUS_SHOW;
					}
				});
				mDiskOut.startAnimation(scaleanimation);
			}
		});
		mDiskOut.startAnimation(scaleanimation);
	}

	private void setLocation(int where) {
		if (where == 0) {
			return;
		}
		mWhere = where;
		switch (where) {
		case WHERE_TOP:
			mScrollY = mDistance;
			mView_back.setVisibility(View.INVISIBLE);
			mView_appName.setVisibility(View.INVISIBLE);
			if (mShowBack) {
				mView_back.setVisibility(View.VISIBLE);
			} else {
				mView_appName.setVisibility(View.VISIBLE);
			}
			break;
		case WHERE_CENTER:
			mScrollY = 0;
			break;
		case WHERE_BOTTOM:
			mScrollY = -mDistance;
			mView_pageName.setVisibility(View.VISIBLE);
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

	public boolean cancelMenu() {
		boolean flag = false;
		if (mStatus == STATUS_SHOW && mWhere == WHERE_CENTER) {
			if (mMenuIndex == 0) {
				back(mOldWhere, null);
			} else {
				showBack();
			}
			flag = true;
		}
		return flag;
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
		if (mMenuIndex != 0) {
			for (View v : mMenuItemList.get(mMenuIndex)) {
				v.setVisibility(View.GONE);
			}
			mMenuIndex = 0;
			for (View v : mMenuItemList.get(mMenuIndex)) {
				v.setVisibility(View.VISIBLE);
			}
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
		ta.setDuration(150);
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

	public void onItemClick(int item) {
		switch (item) {
		case 1:
			back(mOldWhere, new CircleDiskAnimationEnd() {
				@Override
				public void outAnimationEnd() {
					mMCFragmentManager.relpaceToContent(new FriendsFragment(),
							false);
				}

				@Override
				public void diskAnimationEnd() {

				}
			});
			break;
		case 2:
			back(mOldWhere, new CircleDiskAnimationEnd() {
				@Override
				public void outAnimationEnd() {
					mMCFragmentManager.relpaceToContent(new MessageFragment(),
							false);
				}

				@Override
				public void diskAnimationEnd() {

				}
			});
			break;
		case 3:

			break;
		case 4:
			showNext();
			break;
		case 11:
			back(WHERE_TOP, new CircleDiskAnimationEnd() {

				@Override
				public void outAnimationEnd() {
					mMCFragmentManager.relpaceToContent(
							new ScanQRCodeFragment(), true);
				}

				@Override
				public void diskAnimationEnd() {

				}
			});
			break;
		case 12:

			break;
		case 13:

			break;
		case 14:
			showBack();
			break;
		case 15:

			break;
		case 16:

			break;

		default:
			break;
		}
	}

	public boolean isCreated() {
		return mIsCreated;
	}

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCircleMenuListener(CircleMenuListener circleMenuListener) {
		this.mCircleMenuListener = circleMenuListener;
	}

	public interface CircleMenuListener {
		public void onCreated();
	}
}
