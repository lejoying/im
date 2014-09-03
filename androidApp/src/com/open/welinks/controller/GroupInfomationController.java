package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.open.welinks.BusinessCardActivity;
import com.open.welinks.GroupMemberManageActivity;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.view.GroupInfomationView;

public class GroupInfomationController {

	public Data data = Data.getInstance();
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

		String gid = data.localStatus.localData.currentSelectedGroup;
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
					thisActivity.startActivity(intent);
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.groupBusinessCardView.setOnClickListener(mOnClickListener);
		thisView.exit2DeleteGroupView.setOnClickListener(mOnClickListener);
		thisView.groupMemberControlView.setOnClickListener(mOnClickListener);
		thisView.seekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
	}
}
