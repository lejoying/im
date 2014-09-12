package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.GroupMemberManageActivity;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.view.GroupInfomationView;

public class GroupInfomationController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "GroupInfomationController";

	public Context context;
	public GroupInfomationView thisView;
	public GroupInfomationController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;
	public OnSeekBarChangeListener mOnSeekBarChangeListener;

	public Group currentGroup;

	public GroupInfomationController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisController = this;
	}

	public void onCreate() {

		// String currentSelectedGroup = data.localStatus.localData.currentSelectedGroup;
		String gid = thisActivity.getIntent().getStringExtra("gid");
		if (gid != null && !"".equals(gid)) {
			currentGroup = data.relationship.groupsMap.get(gid);
			if (currentGroup == null) {
				thisActivity.finish();
			} else {
				thisView.showGroupMembers();
			}
		} else {
			thisActivity.finish();
		}
	}

	public void initializeListeners() {
		mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (seekBar.getProgress() > 50) {
					seekBar.setProgress(100);
				} else {
					seekBar.setProgress(0);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		};
		mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.groupBusinessCardView)) {
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("type", "group");
					intent.putExtra("key", currentGroup.gid + "");
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.exit2DeleteGroupView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.groupMemberControlView)) {
					Intent intent = new Intent(thisActivity, GroupMemberManageActivity.class);
					intent.putExtra("gid", currentGroup.gid + "");
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.groupNameLayoutView)) {
					thisView.dialogContentView.setVisibility(View.VISIBLE);
					thisView.dialogTitleView.setText("请输入群组名称");
					String groupName = currentGroup.name;
					thisView.dialogEditView.setText(groupName);
					thisView.dialogEditView.setSelection(groupName.length());
					thisView.dialogEditView.requestFocus();
					thisView.inputMethodManager.showSoftInput(thisView.dialogEditView, InputMethodManager.SHOW_FORCED);
				} else if (view.equals(thisView.dialogCancleView)) {
					if (thisView.inputMethodManager.isActive()) {
						thisView.inputMethodManager.hideSoftInputFromWindow(thisView.dialogEditView.getWindowToken(), 0);
					}
					thisView.dialogContentView.setVisibility(View.GONE);
				} else if (view.equals(thisView.dialogConfirmView)) {
					String groupName = thisView.dialogEditView.getText().toString().trim();
					if ("".equals(groupName)) {
						return;
					}

					// modify local data
					parser.check();
					currentGroup = data.relationship.groupsMap.get(currentGroup.gid + "");
					currentGroup.name = groupName;
					data.relationship.isModified = true;
					thisView.groupName2View.setText(groupName);
					thisView.groupNameView.setText(groupName);

					if (thisView.inputMethodManager.isActive()) {
						thisView.inputMethodManager.hideSoftInputFromWindow(thisView.dialogEditView.getWindowToken(), 0);
					}
					thisView.dialogContentView.setVisibility(View.GONE);
					// modify server data
					modifyGroupName(groupName);
				}
			}
		};
	}

	public void bindEvent() {
		thisView.dialogConfirmView.setOnClickListener(mOnClickListener);
		thisView.dialogCancleView.setOnClickListener(mOnClickListener);
		thisView.groupNameLayoutView.setOnClickListener(mOnClickListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.groupBusinessCardView.setOnClickListener(mOnClickListener);
		thisView.exit2DeleteGroupView.setOnClickListener(mOnClickListener);
		thisView.groupMemberControlView.setOnClickListener(mOnClickListener);
		thisView.seekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
	}

	public void onBackPressed() {
		if (thisView.dialogContentView.getVisibility() == View.VISIBLE) {
			thisView.dialogContentView.setVisibility(View.GONE);
		} else {
			thisActivity.finish();
		}
	}

	public void modifyGroupName(String groupName) {

		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", currentGroup.gid + "");
		params.addBodyParameter("name", groupName);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFY, params, responseHandlers.group_modify);
	}
}
