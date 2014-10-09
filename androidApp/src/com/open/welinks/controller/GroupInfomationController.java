package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.GroupMemberManageActivity;
import com.open.welinks.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog;
import com.open.welinks.view.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.view.GroupInfomationView;
import com.open.welinks.view.ViewManage;

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

	public ViewManage viewManage = ViewManage.getInstance();

	public Gson gson = new Gson();

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
				// thisView.showGroupMembers();
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
					Alert.createDialog(thisActivity).setTitle("您确定要删除并退出该群组？").setOnConfirmClickListener(new OnDialogClickListener() {

						@Override
						public void onClick(AlertInputDialog dialog) {
							thisActivity.finish();
							resetShareGroup();
						}
					}).show();
				} else if (view.equals(thisView.groupMemberControlView)) {
					Intent intent = new Intent(thisActivity, GroupMemberManageActivity.class);// GroupMemberManageActivity
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
				} else if (view.getTag(R.id.tag_class) != null) {
					String tag_class = (String) view.getTag(R.id.tag_class);
					if ("head_click".equals(tag_class)) {
						String phone = (String) view.getTag(R.id.tag_first);
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, phone);
						thisView.businessCardPopView.showUserCardDialogView();
					}
				}
			}
		};
		thisView.showGroupMembers();
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

	public void resetShareGroup() {
		parser.check();
		String gid = data.localStatus.localData.currentSelectedGroup;
		data.relationship.groups.remove(gid);
		data.localStatus.localData.currentSelectedGroup = "";
		viewManage.shareSubView.setGroupsDialogContent();
		if (data.relationship.groups.size() != 0) {
			data.localStatus.localData.currentSelectedGroup = data.relationship.groups.get(0);
			viewManage.shareSubView.shareTopMenuGroupName.setText(data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup).name);
		} else {
			viewManage.shareSubView.shareTopMenuGroupName.setText("暂无群组");
		}
		viewManage.shareSubView.shareMessageListBody.y = 0;
		data.relationship.isModified = true;
		viewManage.shareSubView.showShareMessages();
		List<String> subtractMembers = new ArrayList<String>();
		subtractMembers.add(data.userInformation.currentUser.phone);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		String subtractMembersStr = gson.toJson(subtractMembers);
		modifyGroupMembers(API.GROUP_REMOVEMEMBERS, subtractMembersStr, responseHandlers.removeMembersCallBack);
	}

	public void modifyGroupMembers(String url, String membersContentString, RequestCallBack<String> callBack) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", currentGroup.gid + "");
		params.addBodyParameter("members", membersContentString);

		httpUtils.send(HttpMethod.POST, url, params, callBack);
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

	public void onResume() {
		thisView.businessCardPopView.dismissUserCardDialogView();
	}
}
