package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.welinks.ClassificationRecommendationActivity;
import com.open.welinks.MainActivity1;
import com.open.welinks.NearbyActivity;
import com.open.welinks.NearbyReleationActivity;
import com.open.welinks.R;
import com.open.welinks.ShareListActivity;
import com.open.welinks.ShareSectionActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.view.SquareSubView;

public class SquareSubController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public String tag = "SquareSubController";
	public MyLog log = new MyLog(tag, true);

	public SquareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;

	public OnDownloadListener downloadListener;

	public Gson gson = new Gson();

	public SquareSubController(MainController mainController) {
		thisActivity = mainController.thisActivity;
		this.mainController = mainController;
	}

	public View onTouchDownView;

	public boolean isTouchDown = false;

	public void initializeListeners() {
		downloadListener = new OnDownloadListener() {

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onSuccess(final DownloadFile instance, int status) {
				DisplayImageOptions options = thisView.options;
				boolean flag = true;
				if (instance.view.getTag() != null) {
					try {
						String tag = (String) instance.view.getTag();
						if ("head".equals(tag)) {
							options = taskManageHolder.viewManage.options40;
						} else if ("conver".equals(tag)) {
							flag = false;
						}
					} catch (Exception e) {
					}
				}
				if (flag) {
					taskManageHolder.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							Log.e(tag, "---------------failed");
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
							instance.view.setLayoutParams(params);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							if (instance.view.getTag() != null) {
								// fileHandlers.bitmaps.put(imageUri, loadedImage);
							}
						}
					});
				} else {
					taskManageHolder.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view);
				}
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				if (instance.view.getTag() != null) {
					if ("image".equals(instance.view.getTag().toString())) {
						Log.e(tag, "---------------failure:" + instance.view.getTag().toString());
					}
				}
			}
		};
		this.mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					if (isTouchDown) {
						return false;
					}
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("SelectBody")) {
						onTouchDownView = view;
						isTouchDown = true;
						log.e("mOnTouchListener onTouch");
					}
				}
				return false;
			}
		};

		mOnClickListener = new MyOnClickListener() {
			@Override
			public void onClickEffective(View view) {
				if (view.equals(thisView.titleNameView)) {
					Intent intent = new Intent(thisActivity, NearbyActivity.class);
					intent.putExtra("type", "newest");
					thisActivity.startActivity(intent);
					thisActivity.finish();
				} else if (view.getTag(R.id.tag_class) != null) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("SelectBody")) {
						int id = (Integer) view.getTag(R.id.tag_first);
						if (id == R.drawable.sidebar_icon_discover_normal) {
							Intent intent = new Intent(thisActivity, MainActivity1.class);
							intent.putExtra("type", "newest");
							thisActivity.startActivity(intent);
						} else if (id == R.drawable.sidebar_icon_days_normal) {
							Intent intent = new Intent(thisActivity, ShareSectionActivity.class);
							intent.putExtra("key", "91");
							thisActivity.startActivity(intent);
						} else if (id == R.drawable.sidebar_icon_group_normal) {
							Intent intent = new Intent(thisActivity, NearbyReleationActivity.class);
							intent.putExtra("type", "group");
							thisActivity.startActivity(intent);
						} else if (id == R.drawable.sidebar_icon_category_normal) {
							Intent intent = new Intent(thisActivity, ClassificationRecommendationActivity.class);
							thisActivity.startActivity(intent);
						} else if (id == R.drawable.sidebar_icon_test_normal) {
							Intent intent = new Intent(thisActivity, NearbyReleationActivity.class);
							intent.putExtra("type", "account");
							thisActivity.startActivity(intent);
						} else if (id == R.drawable.sidebar_icon_beauty_normal) {
							Intent intent = new Intent(thisActivity, ShareListActivity.class);
							intent.putExtra("key", data.userInformation.currentUser.phone);
							thisActivity.startActivity(intent);
						}
					}
				}
			}
		};
	}

	public void setTitle(AMapLocation mAmapLocation) {
		thisView.titleNameView.setText("广场");
	}

	public void bindEvent() {
		thisView.titleNameView.setOnClickListener(this.mOnClickListener);
	}

	public void onSingleTapUp(MotionEvent event) {
		log.e("onSingleTapUp");
		if (this.onTouchDownView != null) {
			String view_class = (String) this.onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("SelectBody")) {
				int id = (Integer) this.onTouchDownView.getTag(R.id.tag_first);
				if (id == R.drawable.sidebar_icon_discover_normal) {
					onTouchDownView.performClick();
				} else if (id == R.drawable.sidebar_icon_days_normal) {
					onTouchDownView.performClick();
				} else if (id == R.drawable.sidebar_icon_group_normal) {
					onTouchDownView.performClick();
				} else if (id == R.drawable.sidebar_icon_category_normal) {
					onTouchDownView.performClick();
				} else if (id == R.drawable.sidebar_icon_test_normal) {
					onTouchDownView.performClick();
				} else if (id == R.drawable.sidebar_icon_beauty_normal) {
					onTouchDownView.performClick();
				}
			}
			onTouchDownView = null;
		}
		isTouchDown = false;
	}

	public void onScroll() {
		// log.e("onScroll");
		if (onTouchDownView != null) {
		}
		// isTouchDown = false;
		onTouchDownView = null;
		isTouchDown = false;
	}

	public void onDestroy() {
	}
}
