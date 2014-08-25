package com.open.welinks.controller;

import android.view.View;
import android.view.View.OnLongClickListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.FriendsSubView;

public class FriendsSubController {

	public Data data = Data.getInstance();
	public String tag = "FriendsSubController";

	public FriendsSubView thisView;
	public FriendsSubController thisController;
	public OnLongClickListener onLongClickListener;
	
	public MainController mainController;

	public FriendsSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
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
