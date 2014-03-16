package com.lejoying.wxgs.activity.view.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BaseActivity;

public class CircleMenu {

	static final int STATUS_CREATING = 0x3b;
	static final int STATUS_SHOW = 0x23;
	static final int STATUS_HIDE = 0x27;
	static final int STATUS_SHOW_DRAG = 0x2b;
	static final int STATUS_SHOW_ANIM = 0x33;

	public static final int LOCATION_TOP = 0x1d;
	public static final int LOCATION_BOTTOM = 0x1f;
	static final int LOCATION_CENTER = 0x1e;

	int mStatus;
	int mLocation, mBeforeLocation;

	int mScreenWidth, mScreenHeight, mPreviewWidth, mPreviewHeight,
			mStatusBarHeight;

	// circle disk display scale on top or bottom,less than 1
	final float mDiskDisplayScale = 0.37f;
	float mDiskCommonDisplayHeight;

	Point mCurrentDiskCenter;
	Point mDiskLocationOnTop, mDiskLocationOnCenter, mDiskLocationOnBottom;
	Point mDiskCenterOnTop, mDiskCenterOnCenter, mDiskCenterOnBottom;

	int mControlDiskWidth, mControlDiskHeight, mItemDiskWidth, mItemDiskHeight;
	int mControlDiskRadius, mItemDiskRadius;

	WindowManager mWindowManager;
	LayoutInflater mInflater;

	View mMenuContent;
	View mControlDisk;
	View mItemDisk;

	TextView mBackView;
	TextView mPageName;
	TextView mAppNmae;

	LayoutParams mMenuContentParams;

	View mTouchSpace;
	LayoutParams mTouchSpaceParams;

	List<List<View>> mMenuItemList;

	GestureDetector mTouchSpaceDetector;

	boolean isCreated;

	boolean isLock;
	boolean isShowBack;
	String pageName = "";

	MenuItemClickListener mMenuItemClickListener;

	Context mContext;

	Queue<AnimOperation> mAnimationOperationQueue = new LinkedList<AnimOperation>();

	private CircleMenu(Context context) {
		if (isCreated) {
			return;
		}
		mStatus = STATUS_CREATING;
		this.mContext = context.getApplicationContext();
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		mScreenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
		initView();
		initEvent();
		
		new BackThread().start();
	}

	synchronized void waitForBack() throws InterruptedException {
		wait();
	}

	synchronized void back() {
		notify();
	}

	class BackThread extends Thread {
		@Override
		public void run() {
			Instrumentation inst = new Instrumentation();
			while (true) {
				try {
					waitForBack();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			}
		}
	}

	void initView() {

		mMenuContentParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE,
				LayoutParams.FLAG_NOT_TOUCHABLE
						| LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSPARENT);

		mTouchSpaceParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);

		mTouchSpaceParams.gravity = mMenuContentParams.gravity = Gravity.TOP
				| Gravity.LEFT;

		mMenuContent = mInflater.inflate(R.layout.widget_circlemenu, null);
		mItemDisk = mMenuContent.findViewById(R.id.rl_controldiskout);
		mControlDisk = mMenuContent.findViewById(R.id.controlDisk);
		mBackView = (TextView) mControlDisk.findViewById(R.id.backview);
		mPageName = (TextView) mControlDisk.findViewById(R.id.pagename);
		mAppNmae = (TextView) mControlDisk.findViewById(R.id.appname);

		mTouchSpace = new View(mContext);
		mTouchSpace.setVisibility(View.GONE);

		initMenuItems();

		mWindowManager.addView(mTouchSpace, mTouchSpaceParams);
		mWindowManager.addView(mMenuContent, mMenuContentParams);

		AsyncTask<Integer, Integer, Boolean> asyncTask = new AsyncTask<Integer, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				while (mMenuContent.getWidth() == 0
						&& mItemDisk.getWidth() == 0)
					;
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				int[] location = new int[2];
				mMenuContent.getLocationOnScreen(location);
				mStatusBarHeight = location[1];

				mControlDiskWidth = mControlDisk.getWidth();
				mControlDiskHeight = mControlDisk.getHeight();
				mItemDiskWidth = mItemDisk.getWidth();
				mItemDiskHeight = mItemDisk.getHeight();

				mPreviewWidth = mMenuContent.getWidth();
				mPreviewHeight = mMenuContent.getHeight();

				mControlDiskRadius = mControlDiskWidth / 2;
				mItemDiskRadius = mItemDiskWidth / 2;

				boolean flag = true;
				for (int i = 0; i < mMenuItemList.size(); i++) {
					List<View> views = mMenuItemList.get(i);
					if (flag && i != 0) {
						flag = false;
					}
					initMenuItem(views, flag);
				}

				mDiskCommonDisplayHeight = mControlDiskHeight
						* mDiskDisplayScale;

				mDiskLocationOnTop = new Point();
				mDiskLocationOnCenter = new Point();
				mDiskLocationOnBottom = new Point();
				mDiskCenterOnTop = new Point();
				mDiskCenterOnCenter = new Point();
				mDiskCenterOnBottom = new Point();

				mDiskLocationOnTop.x = mDiskLocationOnCenter.x = mDiskLocationOnBottom.x = mPreviewWidth
						/ 2 - mControlDiskRadius;
				mDiskLocationOnTop.y = (int) (mDiskCommonDisplayHeight - mControlDiskHeight);
				mDiskLocationOnCenter.y = mPreviewHeight / 2
						- mControlDiskRadius;
				mDiskLocationOnBottom.y = (int) (mPreviewHeight - mDiskCommonDisplayHeight);

				mDiskCenterOnTop.x = mDiskCenterOnCenter.x = mDiskCenterOnBottom.x = mPreviewWidth / 2;
				mDiskCenterOnTop.y = (int) (mDiskCommonDisplayHeight
						- mControlDiskRadius + mStatusBarHeight);
				mDiskCenterOnCenter.y = mPreviewHeight / 2 + mStatusBarHeight;
				mDiskCenterOnBottom.y = (int) (mPreviewHeight
						- mDiskCommonDisplayHeight + mControlDiskRadius + mStatusBarHeight);

				mStatus = STATUS_HIDE;
				isCreated = true;

				super.onPostExecute(result);
			}

		};

		asyncTask.execute();

	}

	void initMenuItems() {
		List<MenuItemEntity> l1MenuItems = new ArrayList<MenuItemEntity>();
		l1MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item1,
				mContext.getString(R.string.circleitem1_1)));
		l1MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item2,
				mContext.getString(R.string.circleitem1_2)));
		l1MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item3,
				mContext.getString(R.string.circleitem1_3)));
		l1MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item4,
				mContext.getString(R.string.circleitem1_4)));

		List<MenuItemEntity> l2MenuItems = new ArrayList<MenuItemEntity>();
		l2MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item2_1,
				mContext.getString(R.string.circleitem2_1)));
		l2MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item2_2,
				mContext.getString(R.string.circleitem2_2)));
		l2MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item2_3,
				mContext.getString(R.string.circleitem2_3)));
		l2MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item2_4,
				mContext.getString(R.string.circleitem2_4)));
		l2MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item2_5,
				mContext.getString(R.string.circleitem2_5)));
		l2MenuItems.add(new MenuItemEntity(R.drawable.circlemenu_item2_6,
				mContext.getString(R.string.circleitem2_6)));

		mMenuItemList = new ArrayList<List<View>>();
		mMenuItemList.add(addMenuItemView(l1MenuItems));
		mMenuItemList.add(addMenuItemView(l2MenuItems));
	}

	List<View> addMenuItemView(List<MenuItemEntity> entities) {
		ViewGroup itemGroup = (ViewGroup) mItemDisk;
		List<View> views = new ArrayList<View>();
		for (MenuItemEntity entity : entities) {
			ViewGroup menuItemView = (ViewGroup) mInflater.inflate(
					R.layout.widget_circlemenu_item, null);
			((TextView) menuItemView.findViewById(R.id.menu_item_text))
					.setText(entity.text);
			((ImageView) menuItemView.findViewById(R.id.menu_item_icon))
					.setImageResource(entity.imageID);
			menuItemView.setVisibility(View.INVISIBLE);
			views.add(menuItemView);
			itemGroup.addView(menuItemView);
		}
		return views;
	}

	void initMenuItem(List<View> menuList, boolean show) {
		ViewGroup itemGroup = (ViewGroup) mItemDisk;
		if (menuList.size() == 6 || menuList.size() == 4) {

			int sideWidth = ((mItemDiskRadius - mControlDiskRadius) / 2 + mControlDiskRadius) / 2;
			int sideHeight = (int) (((mItemDiskRadius - mControlDiskRadius) / 2 + mControlDiskRadius) * 0.866);

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
						marginLeft = mItemDiskRadius - sideWidth - itemWidth
								/ 2;
						marginTop = mItemDiskRadius - sideHeight - itemHeight
								/ 2;
					} else if (menuList.size() == 4) {
						marginLeft = (2 * mItemDiskRadius - itemWidth) / 2;
						marginTop = (mItemDiskRadius - mControlDiskRadius - itemHeight) / 2;
					}
				} else if (i == 1) {
					if (menuList.size() == 6) {
						marginLeft = (mItemDiskRadius - mControlDiskRadius) / 2
								- itemWidth / 2;
						marginTop = (2 * mItemDiskRadius - itemHeight) / 2;
					} else if (menuList.size() == 4) {
						marginLeft = (mItemDiskRadius - mControlDiskRadius - itemWidth) / 2;
						marginTop = mItemDiskRadius - itemHeight / 2;
					}
				} else if (i == 2) {
					if (menuList.size() == 6) {
						marginLeft = mItemDiskRadius - sideWidth - itemWidth
								/ 2;
						marginTop = mItemDiskRadius + sideHeight - itemHeight
								/ 2;
					} else if (menuList.size() == 4) {
						marginLeft = (2 * mItemDiskRadius - itemWidth) / 2;
						marginTop = (3 * mItemDiskRadius + mControlDiskRadius - itemHeight) / 2;
					}
				} else if (i == 3) {
					if (menuList.size() == 6) {
						marginLeft = mItemDiskRadius + sideWidth - itemWidth
								/ 2;
						marginTop = mItemDiskRadius + sideHeight - itemHeight
								/ 2;
					} else if (menuList.size() == 4) {
						marginLeft = (3 * mItemDiskRadius + mControlDiskRadius - itemWidth) / 2;
						marginTop = mItemDiskRadius - itemHeight / 2;
					}
				} else if (i == 4) {
					marginLeft = (mItemDiskRadius - mControlDiskRadius) / 2
							- itemWidth / 2 + mItemDiskRadius
							+ mControlDiskRadius;
					marginTop = (2 * mItemDiskRadius - itemHeight) / 2;
				} else if (i == 5) {
					marginLeft = mItemDiskRadius + sideWidth - itemWidth / 2;
					marginTop = mItemDiskRadius - sideHeight - itemHeight / 2;
				}
				params.setMargins(marginLeft, marginTop, 0, 0);
				itemGroup.addView(menuList.get(i), params);
			}
		}
	}

	boolean mTouchDownFlag;

	void initEvent() {
		mTouchSpace.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTouchSpaceDetector.onTouchEvent(event);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_UP:
					mTouchDownFlag = false;
					if (mStatus == STATUS_SHOW_DRAG) {
						if (mControlDisk.getTop() <= mPreviewHeight / 10) {
							addShowOperation(LOCATION_TOP);
						} else if (mControlDisk.getTop() > mPreviewHeight / 10
								&& mControlDisk.getTop() < mPreviewHeight
										- mControlDiskHeight - mPreviewHeight
										/ 10) {
							addShowOperation(LOCATION_CENTER);
						} else if (mControlDisk.getTop() >= mPreviewHeight
								- mControlDiskRadius * 2 - mPreviewHeight / 10) {
							addShowOperation(LOCATION_BOTTOM);
						}
					}
					break;
				default:
					break;
				}
				return true;
			}
		});

		mTouchSpaceDetector = new GestureDetector(mContext,
				new GestureDetector.SimpleOnGestureListener() {

					int t, l;

					RelativeLayout.LayoutParams diskParams;

					@Override
					public boolean onDown(MotionEvent e) {
						mTouchDownFlag = false;
						switch (mStatus & mLocation) {
						case STATUS_SHOW & LOCATION_CENTER:
							mTouchDownFlag = true;
							break;
						case STATUS_SHOW & LOCATION_TOP:
						case STATUS_SHOW & LOCATION_BOTTOM:
							mTouchDownFlag = isInCircle(e.getRawX(),
									e.getRawY(), mCurrentDiskCenter,
									mControlDiskRadius);
							break;
						default:
							break;
						}
						l = mControlDisk.getLeft();
						t = mControlDisk.getTop();
						diskParams = (RelativeLayout.LayoutParams) mControlDisk
								.getLayoutParams();
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (mTouchDownFlag) {
							switch (mLocation) {
							case LOCATION_TOP:
								if (isInCircle(e.getRawX(), e.getRawY(),
										mCurrentDiskCenter, mControlDiskRadius)) {
									if (!isShowBack) {
										mBeforeLocation = mLocation;
										mAppNmae.setVisibility(View.INVISIBLE);
										addShowOperation(LOCATION_CENTER);
									} else if (isShowBack) {
										back();
									}
								}
								break;
							case LOCATION_CENTER:
								if (isInCircle(e.getRawX(), e.getRawY(),
										mDiskCenterOnCenter, mControlDiskRadius)) {
									cancelMenu();
								} else if (isInCircle(e.getRawX(), e.getRawY(),
										mDiskCenterOnCenter, mItemDiskRadius)) {
									float clickX = e.getRawX();
									float clickY = e.getRawY();
									float tan = (clickX - mDiskCenterOnCenter.x)
											/ ((clickY - mDiskCenterOnCenter.y) != 0 ? (clickY - mDiskCenterOnCenter.y)
													: 0.1f);
									int item = -1;
									if (mMenuItemList.get(mCurrentMenuIndex)
											.size() == 4) {
										if (clickX < mDiskCenterOnCenter.x) {
											if (tan >= 0 && tan < 1f) {
												item = 1;
											}
											if (tan >= 1f || tan < -1f) {
												item = 2;
											}
											if (tan > -1f && tan < 0) {
												item = 3;
											}
										} else {
											if (tan >= 0 && tan < 1f) {
												item = 3;
											}
											if (tan >= 1f || tan < -1f) {
												item = 4;
												setItemDeskIndex(
														mCurrentMenuIndex,
														++mCurrentMenuIndex);
											}
											if (tan > -1f && tan < 0) {
												item = 1;
											}
										}
									} else if (mMenuItemList.get(
											mCurrentMenuIndex).size() == 6) {
										if (clickX < mDiskCenterOnCenter.x) {
											if (tan >= 0 && tan < 1.732f) {
												item = 1;
											}
											if (tan >= 1.732f || tan < -1.732f) {
												item = 2;
											}
											if (tan > -1.732f && tan < 0) {
												item = 3;
											}
										} else {
											if (tan >= 0 && tan < 1.732f) {
												item = 4;
											}
											if (tan >= 1.732f || tan < -1.732f) {
												item = 5;
											}
											if (tan > -1.732f && tan < 0) {
												item = 6;
											}
										}
									}
									System.out.println(item + mCurrentMenuIndex
											* 10);
								} else {
									addShowOperation(mBeforeLocation);
								}
								break;
							case LOCATION_BOTTOM:
								if (isInCircle(e.getRawX(), e.getRawY(),
										mCurrentDiskCenter, mControlDiskRadius)) {
									mBeforeLocation = mLocation;
									mPageName.setVisibility(View.INVISIBLE);
									mBackView.setVisibility(View.INVISIBLE);
									addShowOperation(LOCATION_CENTER);
								}
								break;
							default:
								break;
							}
						}
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						if (mTouchDownFlag && !isLock) {
							switch (mLocation) {
							case LOCATION_TOP:
							case LOCATION_BOTTOM:
								mBeforeLocation = mLocation;
								addShowOperation(LOCATION_CENTER);
								break;

							default:
								break;
							}
						}
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						if (mTouchDownFlag && !isLock) {
							mAppNmae.setVisibility(View.INVISIBLE);
							mPageName.setVisibility(View.INVISIBLE);
							mBackView.setVisibility(View.INVISIBLE);
							switch (mLocation) {
							case LOCATION_TOP:
							case LOCATION_BOTTOM:
								mBeforeLocation = mLocation;
								mStatus = STATUS_SHOW_DRAG;
								break;
							default:
								break;
							}
							if (mStatus == STATUS_SHOW_DRAG) {
								int moveX = (int) (e2.getRawX() - e1.getRawX());
								int moveY = (int) (e2.getRawY() - e1.getRawY());
								diskParams.leftMargin = l + moveX;
								diskParams.topMargin = t + moveY;
								diskParams.rightMargin = -mControlDiskWidth;
								diskParams.bottomMargin = -mControlDiskHeight;
								mControlDisk.setLayoutParams(diskParams);
							}
						}
						return true;
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (mTouchDownFlag && !isLock) {
							switch (mLocation) {
							case LOCATION_TOP:
								if (velocityY > 600
										&& mControlDisk.getTop() < mPreviewHeight
												- mControlDiskHeight
												- mPreviewHeight / 10) {
									addShowOperation(LOCATION_CENTER);
								}
								break;
							case LOCATION_CENTER:
								if (velocityY > 600) {
									addShowOperation(LOCATION_BOTTOM);
								} else if (velocityY < -600) {
									addShowOperation(LOCATION_TOP);
								}
								break;
							case LOCATION_BOTTOM:
								if (velocityY < -600
										&& mControlDisk.getTop() > mPreviewHeight / 10) {
									addShowOperation(LOCATION_CENTER);
								}
								break;
							default:
								break;
							}
						}
						return true;
					}
				});

		mTouchSpace.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
					if (mCurrentMenuIndex != 0) {
						setItemDeskIndex(mCurrentMenuIndex, --mCurrentMenuIndex);
					} else {
						addShowOperation(mBeforeLocation);
					}
				}
				return true;
			}
		});

	}

	public boolean isInCircle(float clickX, float clickY, Point center,
			int radius) {
		return center == null ? false : Math.pow(clickX - center.x, 2)
				+ Math.pow(clickY - center.y, 2) < Math.pow(radius, 2);
	}

	boolean isWaitingForCreate;

	Animation mShowFromTop;

	void changePageName(String pageName) {
		this.pageName = pageName;
		mPageName.setText(pageName);
	}

	Animation mShowFromBottom;

	void setMenuLocation(final int location) {
		mMenuContent.clearAnimation();
		switch (mStatus) {
		case STATUS_CREATING:
			if (!isWaitingForCreate) {
				isWaitingForCreate = true;
				AsyncTask<Integer, Integer, Boolean> waitCreating = new AsyncTask<Integer, Integer, Boolean>() {
					int location;

					@Override
					protected Boolean doInBackground(Integer... params) {
						location = params[0];
						while (mStatus == STATUS_CREATING)
							;
						return true;
					}

					@Override
					protected void onPostExecute(Boolean result) {
						setMenuLocation(location);
						isWaitingForCreate = false;
					}
				};
				waitCreating.execute(location);
				return;
			}
			break;
		case STATUS_SHOW:
			if (location == mLocation) {
				if (mBackView.getVisibility() == View.VISIBLE
						&& isShowBack == false) {
					addHideOperation(true);
					addShowOperation(location);
					excludeEnd();
				} else {
					excludeEnd();
				}
				return;
			} else if ((location == LOCATION_TOP && mLocation == LOCATION_BOTTOM)
					|| (location == LOCATION_BOTTOM && mLocation == LOCATION_TOP)) {
				addHideOperation(true);
				addShowOperation(location);
				return;
			}
			break;
		}

		mBackView.setVisibility(View.INVISIBLE);
		mAppNmae.setVisibility(View.INVISIBLE);
		mPageName.setVisibility(View.INVISIBLE);

		switch (location) {
		case LOCATION_TOP:
			switch (mStatus) {
			case STATUS_HIDE:
				if (mShowFromTop == null) {
					mShowFromTop = new TranslateAnimation(0, 0,
							-mDiskCommonDisplayHeight, 0);
					mShowFromTop.setDuration(100);
					mShowFromTop.setAnimationListener(new AnimationAdapter() {
						@Override
						public void onAnimationEnd(Animation animation) {
							mStatus = STATUS_SHOW;
							if (isShowBack) {
								mBackView.setVisibility(View.VISIBLE);
							} else {
								mAppNmae.setVisibility(View.VISIBLE);
							}
							excludeEnd();
						}
					});
				}
				mLocation = location;
				mStatus = STATUS_SHOW_ANIM;
				mCurrentDiskCenter = mDiskCenterOnTop;

				mTouchSpaceParams.width = mControlDiskWidth;
				mTouchSpaceParams.height = (int) mDiskCommonDisplayHeight;
				mTouchSpaceParams.gravity = Gravity.TOP
						| Gravity.CENTER_HORIZONTAL;
				mTouchSpaceParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				mWindowManager.updateViewLayout(mTouchSpace, mTouchSpaceParams);
				mTouchSpace.setVisibility(View.VISIBLE);

				setControlDiskLocation(location);

				mItemDisk.setVisibility(View.INVISIBLE);
				mMenuContent.setVisibility(View.VISIBLE);
				mControlDisk.startAnimation(mShowFromTop);
				break;
			default:
				TranslateAnimation translateToTopAnimation = new TranslateAnimation(
						mControlDisk.getLeft() - mDiskLocationOnTop.x, 0,
						mControlDisk.getTop() - mDiskLocationOnTop.y, 0);
				translateToTopAnimation.setDuration(150);
				translateToTopAnimation
						.setAnimationListener(new AnimationAdapter() {
							@Override
							public void onAnimationEnd(Animation animation) {
								mStatus = STATUS_SHOW;
								if (isShowBack) {
									mBackView.setVisibility(View.VISIBLE);

								} else {
									mAppNmae.setVisibility(View.VISIBLE);
								}

								excludeEnd();
							}
						});

				mLocation = location;
				mStatus = STATUS_SHOW_ANIM;
				mCurrentDiskCenter = mDiskCenterOnTop;

				mTouchSpaceParams.width = mControlDiskRadius * 2;
				mTouchSpaceParams.height = (int) mDiskCommonDisplayHeight;
				mTouchSpaceParams.gravity = Gravity.TOP
						| Gravity.CENTER_HORIZONTAL;
				mTouchSpaceParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				mWindowManager.updateViewLayout(mTouchSpace, mTouchSpaceParams);
				mTouchSpace.setVisibility(View.VISIBLE);

				setControlDiskLocation(location);

				mCurrentDiskCenter = mDiskCenterOnTop;
				mItemDisk.setVisibility(View.INVISIBLE);

				mControlDisk.startAnimation(translateToTopAnimation);
				break;
			}
			break;
		case LOCATION_CENTER:
			switch (mStatus) {
			case STATUS_HIDE:
				mStatus = STATUS_SHOW;
				mTouchSpace.setVisibility(View.VISIBLE);
				mMenuContent.setVisibility(View.VISIBLE);
				mCurrentDiskCenter = mDiskCenterOnCenter;
				excludeEnd();
				break;
			default:
				TranslateAnimation translateToCenterAnimation = new TranslateAnimation(
						mControlDisk.getLeft() - mDiskLocationOnCenter.x, 0,
						mControlDisk.getTop() - mDiskLocationOnCenter.y, 0);
				translateToCenterAnimation.setDuration(150);
				translateToCenterAnimation
						.setAnimationListener(new AnimationAdapter() {

							@Override
							public void onAnimationEnd(Animation animation) {
								for (View v : mMenuItemList
										.get(mCurrentMenuIndex)) {
									v.setVisibility(View.GONE);
								}
								for (View v : mMenuItemList.get(0)) {
									v.setVisibility(View.VISIBLE);
								}
								mCurrentMenuIndex = 0;
								ScaleAnimation scaleanimation = new ScaleAnimation(
										0.5f, 1, 0.5f, 1,
										Animation.RELATIVE_TO_SELF, 0.5f,
										Animation.RELATIVE_TO_SELF, 0.5f);
								scaleanimation.setDuration(80);
								mItemDisk.setVisibility(View.VISIBLE);
								mStatus = STATUS_SHOW;
								mItemDisk.startAnimation(scaleanimation);
								excludeEnd();
							}
						});

				mLocation = location;
				mStatus = STATUS_SHOW_ANIM;

				mTouchSpaceParams.width = mTouchSpaceParams.height = LayoutParams.MATCH_PARENT;
				mTouchSpaceParams.gravity = Gravity.TOP | Gravity.LEFT;
				mTouchSpaceParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
				mWindowManager.updateViewLayout(mTouchSpace, mTouchSpaceParams);
				mTouchSpace.setVisibility(View.VISIBLE);

				setControlDiskLocation(location);

				mCurrentDiskCenter = mDiskCenterOnCenter;
				mControlDisk.startAnimation(translateToCenterAnimation);
				break;
			}
			break;
		case LOCATION_BOTTOM:

			switch (mStatus) {
			case STATUS_HIDE:

				if (mShowFromBottom == null) {
					mShowFromBottom = new TranslateAnimation(0, 0,
							mDiskCommonDisplayHeight, 0);
					mShowFromBottom.setDuration(100);
					mShowFromBottom
							.setAnimationListener(new AnimationAdapter() {

								@Override
								public void onAnimationEnd(Animation animation) {
									mStatus = STATUS_SHOW;
									mPageName.setVisibility(View.VISIBLE);
									excludeEnd();
								}
							});
				}

				mLocation = location;
				mStatus = STATUS_SHOW_ANIM;
				mCurrentDiskCenter = mDiskCenterOnBottom;

				mTouchSpaceParams.width = mControlDiskWidth;
				mTouchSpaceParams.height = (int) mDiskCommonDisplayHeight;
				mTouchSpaceParams.gravity = Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL;
				mTouchSpaceParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				mWindowManager.updateViewLayout(mTouchSpace, mTouchSpaceParams);
				mTouchSpace.setVisibility(View.VISIBLE);

				setControlDiskLocation(location);

				mItemDisk.setVisibility(View.INVISIBLE);
				mMenuContent.setVisibility(View.VISIBLE);

				mControlDisk.startAnimation(mShowFromBottom);
				break;

			default:

				TranslateAnimation translateToBottomAnimation = new TranslateAnimation(
						mControlDisk.getLeft() - mDiskLocationOnBottom.x, 0,
						mControlDisk.getTop() - mDiskLocationOnBottom.y, 0);
				translateToBottomAnimation.setDuration(150);
				translateToBottomAnimation
						.setAnimationListener(new AnimationAdapter() {

							@Override
							public void onAnimationEnd(Animation animation) {
								mStatus = STATUS_SHOW;
								mPageName.setVisibility(View.VISIBLE);
								excludeEnd();
							}
						});
				mLocation = location;
				mStatus = STATUS_SHOW_ANIM;
				mCurrentDiskCenter = mDiskCenterOnBottom;

				mTouchSpaceParams.width = mControlDiskWidth;
				mTouchSpaceParams.height = (int) mDiskCommonDisplayHeight;
				mTouchSpaceParams.gravity = Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL;
				mTouchSpaceParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				mWindowManager.updateViewLayout(mTouchSpace, mTouchSpaceParams);
				mTouchSpace.setVisibility(View.VISIBLE);

				setControlDiskLocation(location);

				mItemDisk.setVisibility(View.INVISIBLE);
				mControlDisk.startAnimation(translateToBottomAnimation);
				break;
			}

			break;
		default:
			excludeEnd();
			break;
		}

	}

	int mCurrentMenuIndex;

	void setItemDeskIndex(final int now, final int next) {
		mStatus = STATUS_SHOW_ANIM;
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
				mItemDisk.startAnimation(scaleanimation);
			}
		});
		mItemDisk.startAnimation(scaleanimation);
	}

	void setControlDiskLocation(int location) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mControlDisk
				.getLayoutParams();
		params.rightMargin = -mControlDiskWidth;
		params.bottomMargin = -mControlDiskHeight;
		switch (location) {
		case LOCATION_TOP:
			params.leftMargin = mDiskLocationOnTop.x;
			params.topMargin = mDiskLocationOnTop.y;
			mControlDisk.setLayoutParams(params);
			break;
		case LOCATION_CENTER:
			params.leftMargin = mDiskLocationOnCenter.x;
			params.topMargin = mDiskLocationOnCenter.y;
			mControlDisk.setLayoutParams(params);
			break;
		case LOCATION_BOTTOM:
			params.leftMargin = mDiskLocationOnBottom.x;
			params.topMargin = mDiskLocationOnBottom.y;
			mControlDisk.setLayoutParams(params);
			break;
		default:
			break;
		}
	}

	boolean cancelMenu() {
		boolean flag = false;
		if (mStatus == STATUS_SHOW && mLocation == LOCATION_CENTER) {
			if (mCurrentMenuIndex == 0) {
				addShowOperation(mBeforeLocation);
				flag = true;
			} else {
				setItemDeskIndex(mCurrentMenuIndex, --mCurrentMenuIndex);
			}
		}
		return flag;
	}

	Animation mHideMenuFromTop;
	Animation mHideMenuFromBottom;

	void hideMenu() {
		mMenuContent.clearAnimation();
		if (isCreated && mStatus != STATUS_HIDE) {
			switch (mLocation) {
			case LOCATION_TOP:
				if (mHideMenuFromTop == null) {
					mHideMenuFromTop = new TranslateAnimation(0, 0, 0,
							-mDiskCommonDisplayHeight);
					mHideMenuFromTop.setDuration(80);
					mHideMenuFromTop
							.setAnimationListener(new AnimationAdapter() {

								@Override
								public void onAnimationEnd(Animation animation) {
									mMenuContent.setVisibility(View.INVISIBLE);
									mStatus = STATUS_HIDE;
									excludeEnd();
								}
							});
				}
				mStatus = STATUS_SHOW_ANIM;
				mTouchSpace.setVisibility(View.GONE);
				mControlDisk.startAnimation(mHideMenuFromTop);
				break;
			case LOCATION_CENTER:
				mStatus = STATUS_HIDE;
				mTouchSpace.setVisibility(View.GONE);
				mMenuContent.setVisibility(View.INVISIBLE);
				excludeEnd();
				break;
			case LOCATION_BOTTOM:
				if (mHideMenuFromBottom == null) {
					mHideMenuFromBottom = new TranslateAnimation(0, 0, 0,
							mDiskCommonDisplayHeight);
					mHideMenuFromBottom.setDuration(80);
					mHideMenuFromBottom
							.setAnimationListener(new AnimationAdapter() {
								@Override
								public void onAnimationEnd(Animation animation) {
									mMenuContent.setVisibility(View.INVISIBLE);
									mStatus = STATUS_HIDE;
									excludeEnd();
								}
							});
				}
				mStatus = STATUS_SHOW_ANIM;
				mTouchSpace.setVisibility(View.GONE);
				mControlDisk.startAnimation(mHideMenuFromBottom);
				break;
			default:
				excludeEnd();
				break;
			}
		} else {
			excludeEnd();
		}
	}

	void addShowOperation(int location) {
		mAnimationOperationQueue.offer(new AnimOperation(AnimOperation.SHOW,
				location));
		exclude();
	}

	void addHideOperation(boolean immediately) {
		if (!immediately) {
			mAnimationOperationQueue
					.offer(new AnimOperation(AnimOperation.HIDE));
			exclude();
		} else {
			mAnimationOperationQueue.clear();
			mMenuContent.clearAnimation();
			mAnimationOperationQueue
					.offer(new AnimOperation(AnimOperation.HIDE));
			excludeEnd();
		}
	}

	boolean waitAnimationEnd;

	void exclude() {
		if (mAnimationOperationQueue.size() != 0 && !waitAnimationEnd) {
			waitAnimationEnd = true;
			AnimOperation animOperation = mAnimationOperationQueue.poll();
			if (animOperation != null) {
				switch (animOperation.operation) {
				case AnimOperation.SHOW:
					setMenuLocation(animOperation.location);
					break;
				case AnimOperation.HIDE:
					hideMenu();
					break;
				default:
					excludeEnd();
					break;
				}
			} else {
				excludeEnd();
			}
		}
	}

	void excludeEnd() {
		waitAnimationEnd = false;
		exclude();
	}

	class AnimOperation {
		public static final int SHOW = 1;
		public static final int HIDE = 2;
		public int operation;
		public int location;

		public AnimOperation() {
			// TODO Auto-generated constructor stub
		}

		public AnimOperation(int operation) {
			this.operation = operation;
		}

		public AnimOperation(int operation, int location) {
			this.operation = operation;
			this.location = location;
		}
	}

	class MenuItemEntity {
		public int imageID;
		public String text;

		public MenuItemEntity(int imageID, String text) {
			this.imageID = imageID;
			this.text = text;
		}
	}

	static abstract class AnimationAdapter implements AnimationListener {

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}
	}

	public interface MenuItemClickListener {
		public void onItemClick(int itemPosition);
	}

	// static method

	static CircleMenu mCircleMenu;

	static BaseActivity mCurrentActivity;

	public static void create(Context context) {
		if (mCircleMenu == null) {
			mCircleMenu = new CircleMenu(context);
		}
	}

	public static final void show(BaseActivity activity) {
		int location = LOCATION_TOP;
		if (mCircleMenu != null) {
			location = mCircleMenu.mLocation != 0 ? mCircleMenu.mLocation
					: LOCATION_TOP;
		}
		show(activity, location);
	}

	public static final void show(BaseActivity activity, int location) {
		if (mCircleMenu != null && activity != null) {
			if (mCircleMenu.isLeave && activity.equals(mCurrentActivity)) {
				location = mCircleMenu.mLocation != 0 ? mCircleMenu.mLocation
						: LOCATION_TOP;
				mCircleMenu.isLeave = false;
			} else {
				mCircleMenu.isLock = false;
				mCircleMenu.isShowBack = false;
			}
			mCurrentActivity = activity;
			mCircleMenu.addShowOperation(location);
		} else if (activity != null && mCircleMenu == null) {
			create(activity);
			show(activity, location);
		}
	}

	public static final void showBack(BaseActivity activity) {
		if (mCircleMenu != null) {
			mCurrentActivity = activity;
			if (mCircleMenu.mStatus != STATUS_HIDE && !mCircleMenu.isLock
					&& !mCircleMenu.isShowBack) {
				mCircleMenu.addHideOperation(true);
			}
			mCircleMenu.isLock = true;
			mCircleMenu.isShowBack = true;
			mCircleMenu.addShowOperation(LOCATION_TOP);
		} else if (activity != null && mCircleMenu == null) {
			System.out.println("wait create");
			create(activity);
			showBack(activity);
		}
	}

	public static final void hide() {
		if (mCircleMenu != null) {
			mCircleMenu.addHideOperation(false);
		}
	}

	boolean isLeave;

	public static final void hideImmediately(boolean isLeave) {
		if (mCircleMenu != null) {
			mCircleMenu.isLeave = isLeave;
			mCircleMenu.addHideOperation(true);
		}
	}

	public static void setOnMenuItemClickListener(
			MenuItemClickListener itemClickListener) {
		if (mCircleMenu != null) {
			mCircleMenu.mMenuItemClickListener = itemClickListener;
		}
	}

	public static void setPageName(String pageName) {
		if (mCircleMenu != null) {
			mCircleMenu.changePageName(pageName);
		}
	}

	public static void destroy() {
		if (mCircleMenu != null) {
			CircleMenu waitGC = mCircleMenu;
			CircleMenu.mCurrentActivity = null;
			mCircleMenu = null;
			waitGC.mContext = null;
			waitGC.mWindowManager.removeView(waitGC.mMenuContent);
			waitGC.mWindowManager.removeView(waitGC.mTouchSpace);
			waitGC = null;
			System.gc();
		}
	}

}
