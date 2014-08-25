package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.R;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.view.ShareSubView;

public class ShareSubController {

	public Data data = Data.getInstance();
	public String tag = "ShareSubController";
	public ShareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchBackColorListener;

	public ShareSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.shareTopMenuGroupNameParent)) {
					thisView.showGroupsDialog();
				} else if (view.equals(thisView.groupDialogView)) {
					thisView.dismissGroupDialog();
				} else if (view.equals(thisView.releaseShareView)) {
					thisView.showReleaseShareDialogView();
				} else if (view.equals(thisView.releaseShareDialogView)) {
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageTextButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					thisActivity.startActivity(intent);
					thisView.dismissReleaseShareDialogView();
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("GroupDialogContentItem".equals(type)) {
						data.localStatus.localData.currentSelectedGroup = content;
						thisView.dismissGroupDialog();
						Group group = data.relationship.groupsMap.get(content);
						TextView shareTopMenuGroupName = (TextView) view.getTag(R.id.shareTopMenuGroupName);
						shareTopMenuGroupName.setText(group.name);
						// thisView.shareSubView.
					}
				}
			}
		};
		onTouchBackColorListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (view == thisView.releaseImageTextButton) {
					int motionEvent = event.getAction();
					if (motionEvent == MotionEvent.ACTION_DOWN) {
						view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					} else if (motionEvent == MotionEvent.ACTION_UP) {
						view.setBackgroundColor(Color.parseColor("#00000000"));
					}
				}
				return false;
			}
		};
	}

	public void bindEvent() {
		thisView.shareTopMenuGroupNameParent.setOnClickListener(mOnClickListener);
		thisView.groupDialogView.setOnClickListener(mOnClickListener);
	}

	public void getUserCurrentAllGroup() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", "lejoying");

		httpUtils.send(HttpMethod.POST, "http://www.we-links.com/api2/group/getgroupmembers", params, responseHandlers.getGroupMembers);
	}

}
