package com.open.welinks.controller;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.SharePraiseusersView;

public class SharePraiseusersController {

	public Data data = Data.getInstance();
	public String tag = "SharePraiseusersController";

	public Context context;
	public SharePraiseusersView thisView;
	public SharePraiseusersController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;

	public List<String> praiseusersList;

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
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("user".equals(type)) {
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent("point", content);
						thisView.businessCardPopView.showUserCardDialogView();
					}
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
	}

	public void onResume() {
		thisView.businessCardPopView.dismissUserCardDialogView();
	}
}
