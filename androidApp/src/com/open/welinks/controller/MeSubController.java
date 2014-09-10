package com.open.welinks.controller;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.BusinessCardActivity;
import com.open.welinks.DynamicListActivity;
import com.open.welinks.FindMoreActivity;
import com.open.welinks.R;
import com.open.welinks.SettingActivity;
import com.open.welinks.model.Data;
import com.open.welinks.view.MeSubView;

public class MeSubController {

	public Data data = Data.getInstance();
	public String tag = "MeSubController";
	public MeSubView thisView;

	public MainController mainController;

	public OnTouchListener onTouchListener;
	public OnClickListener onClickListener;

	public MeSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {

		onTouchListener = new OnTouchListener() {

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
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.myBusiness)) {
					Intent intent = new Intent(mainController.thisActivity, BusinessCardActivity.class);
					intent.putExtra("type", "point");
					intent.putExtra("key", data.userInformation.currentUser.phone);
					mainController.thisActivity.startActivity(intent);
				} else if (view.equals(thisView.mySetting)) {
					mainController.thisActivity.startActivityForResult((new Intent(mainController.thisActivity, SettingActivity.class)), R.id.tag_first);
				} else if (view.equals(thisView.dynamicListView)) {
					Intent intent = new Intent(mainController.thisActivity, DynamicListActivity.class);
					mainController.thisActivity.startActivity(intent);
				} else if (view.equals(thisView.moreFriendView)) {
					Intent intent = new Intent(mainController.thisActivity, FindMoreActivity.class);
					mainController.thisActivity.startActivity(intent);
				}
			}
		};
	}

	public void bindEvent() {
		thisView.mRootView.setOnTouchListener(onTouchListener);
		thisView.myBusiness.setOnClickListener(onClickListener);
		thisView.mySetting.setOnClickListener(onClickListener);
		thisView.dynamicListView.setOnClickListener(onClickListener);
		thisView.moreFriendView.setOnClickListener(onClickListener);
	}

}
