package com.open.welinks.controller;

import com.open.welinks.model.Data;
import com.open.welinks.view.InviteFriendView;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class InviteFriendController {

	public Data data = Data.getInstance();
	public String tag = "InviteFriendController";

	public Context context;
	public InviteFriendView thisView;
	public InviteFriendController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;

	public InviteFriendController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisController = this;
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.confirmButtonView)) {

				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.confirmButtonView.setOnClickListener(mOnClickListener);
	}
}
