package com.open.welinks.controller;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.View.OnLongClickListener;

import com.open.welinks.ChatActivity;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
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
				Log.d(tag, "onclick");
				Friend friend = null;
				if ((friend = (Friend) view.getTag(R.id.friendsContainer)) != null) {
					Intent intent = new Intent(thisView.mainView.thisActivity,
							ChatActivity.class);
					intent.putExtra("id", friend.phone);
					intent.putExtra("type", "point");
					thisView.mainView.thisActivity.startActivity(intent);
				}

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
