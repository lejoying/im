package com.open.welinks.controller;

import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.BusinessCardActivity;
import com.open.welinks.DynamicListActivity;
import com.open.welinks.FindMoreActivity;
import com.open.welinks.R;
import com.open.welinks.SettingActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.model.Data;
import com.open.welinks.view.MeSubView;
import android.view.ViewGroup;

public class MeSubController {

	public Data data = Data.getInstance();
	public String tag = "MeSubController";
	public MeSubView thisView;

	public MainController mainController;

	public OnTouchListener onTouchListener1;
	public OnClickListener onClickListener;
	public OnTouchListener onTouchListener;

	public View onTouchDownView;

	public boolean isTouchDown = false;

	public MeSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {

		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					String view_class = (String) view.getTag(R.id.tag_class);
					// if (view_class.equals("share_view")) {
					onTouchDownView = view;
					try {
						((ViewGroup) onTouchDownView).getChildAt(0).setVisibility(View.VISIBLE);
					} catch (Exception e) {
					}
					isTouchDown = true;
					// }
					Log.i(tag, "ACTION_DOWN---" + view_class);
				}
				return false;
			}
		};
		onTouchListener1 = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					thisView.mMePageAppIconScaleSpring.setEndValue(1);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					thisView.mMePageAppIconScaleSpring.setEndValue(0);
				}
				return true;
			}
		};
		onClickListener = new MyOnClickListener() {

			public void onClickEffective(View view) {
				if (view.equals(thisView.myBusiness)) {
					Intent intent = new Intent(mainController.thisActivity, BusinessCardActivity.class);
					intent.putExtra("type", "point");
					intent.putExtra("key", data.userInformation.currentUser.phone);
					mainController.thisActivity.startActivity(intent);
				} else if (view.equals(thisView.mySetting)) {
					mainController.thisActivity.startActivityForResult((new Intent(mainController.thisActivity, SettingActivity.class)), R.id.tag_first);
				} else if (view.equals(thisView.dynamicListView)) {
					Intent intent = new Intent(mainController.thisActivity, DynamicListActivity.class);
					if (data.event.userNotReadMessage) {
						intent.putExtra("type", 2);
					} else if (data.event.groupNotReadMessage) {
						intent.putExtra("type", 3);
					}
					mainController.thisActivity.startActivity(intent);
				} else if (view.equals(thisView.moreFriendView)) {
					Intent intent = new Intent(mainController.thisActivity, FindMoreActivity.class);
					mainController.thisActivity.startActivity(intent);
				}
			}
		};
	}

	public void bindEvent() {
		thisView.mRootView.setOnTouchListener(onTouchListener1);
		thisView.myBusiness.setOnClickListener(onClickListener);
		thisView.mySetting.setOnClickListener(onClickListener);
		thisView.dynamicListView.setOnClickListener(onClickListener);
		thisView.moreFriendView.setOnClickListener(onClickListener);
		thisView.myBusiness.setOnTouchListener(onTouchListener);
		thisView.mySetting.setOnTouchListener(onTouchListener);
		thisView.dynamicListView.setOnTouchListener(onTouchListener);
		thisView.moreFriendView.setOnTouchListener(onTouchListener);
	}

	public void onSingleTapUp(MotionEvent event) {
		if (onTouchDownView != null) {
			// String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			// if (view_class.equals("share_view")) {
			try {
				((ViewGroup) onTouchDownView).getChildAt(0).setVisibility(View.INVISIBLE);
			} catch (Exception e) {
			}
			onTouchDownView.performClick();
			// }
			onTouchDownView = null;
		}
		isTouchDown = false;
	}

	public void onScroll() {
		try {
			((ViewGroup) onTouchDownView).getChildAt(0).setVisibility(View.INVISIBLE);
		} catch (Exception e) {
		}
		onTouchDownView = null;
	}
}
