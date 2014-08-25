package com.open.welinks.controller;

import android.view.View;
import android.view.View.OnClickListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.MessagesSubView;

public class MessagesSubController {

	public Data data = Data.getInstance();
	public String tag = "UserIntimateController";
	public MessagesSubView thisView;
	public MessagesSubController thisconController;

	public OnClickListener mOnClickListener;

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

			}
		};
	}

	public void bindEvent() {
	}

}
