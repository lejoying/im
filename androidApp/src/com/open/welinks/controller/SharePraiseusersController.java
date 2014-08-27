package com.open.welinks.controller;

import java.util.ArrayList;

import com.open.welinks.model.Data;
import com.open.welinks.view.SharePraiseusersView;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class SharePraiseusersController {

	public Data data = Data.getInstance();
	public String tag = "SharePraiseusersController";

	public Context context;
	public SharePraiseusersView thisView;
	public SharePraiseusersController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;

	public ArrayList<String> praiseusersList;

	public SharePraiseusersController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		thisController = this;
	}

	public void onCreate() {
		praiseusersList = data.tempData.praiseusersList;
		data.tempData.praiseusersList = null;
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
	}
}
