package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.open.lib.MyLog;
import com.open.welinks.ClassificationRecommendationActivity;
import com.open.welinks.MainActivity1;
import com.open.welinks.NearbyReleationActivity;
import com.open.welinks.R;
import com.open.welinks.NearbyActivity;
import com.open.welinks.ShareListActivity;
import com.open.welinks.ShareSectionActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.view.SquareSubView;

public class SquareSubController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "SquareSubController";
	public MyLog log = new MyLog(tag, true);

	public SquareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;

	public Gson gson = new Gson();

	public SquareSubController(MainController mainController) {
		thisActivity = mainController.thisActivity;
		this.mainController = mainController;
	}

	public View onTouchDownView;

	public boolean isTouchDown = false;

	public void initializeListeners() {
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
		// if (mAmapLocation.getProvince() != null) {
		// thisView.titleNameView.setText(mAmapLocation.getProvince() + mAmapLocation.getCity());
		// } else {
		thisView.titleNameView.setText("广场");
		// }
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
		log.e("onScroll");
		if (onTouchDownView != null) {
		}
		// isTouchDown = false;
		onTouchDownView = null;
		isTouchDown = false;
	}

	public void onDestroy() {

	}
}
