package com.open.welinks.controller;

import android.view.View;
import android.view.View.OnLongClickListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.FriendsSubView;

public class FriendsSubController {

	public Data data = Data.getInstance();

	public FriendsSubView thisView;
	public FriendsSubController thisController;
	public OnLongClickListener onLongClickListener;
	
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
