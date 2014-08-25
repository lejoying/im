package com.open.welinks.controller;

import android.view.View;
import android.view.View.OnClickListener;

import android.view.View.OnLongClickListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.FriendsSubView;

public class FriendsSubController {

	public String tag = "UserIntimateController";

	public Data data = Data.getInstance();

	public FriendsSubView thisView;
	public FriendsSubController thisController;
	public OnLongClickListener onLongClickListener;

	public OnClickListener mOnClickListener;

	public MainController mainController;

	public FriendsSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

			}
		};
		onLongClickListener = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				thisView.showCircleSettingDialog(view);
				return true;
			}
		};
	}

	public void bindEvent() {

	}

}
