package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.open.welinks.InviteFriendActivity;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.view.GroupMemberManageView;

public class GroupMemberManageController {

	public Data data = Data.getInstance();
	public String tag = "GroupMemberManageController";

	public Context context;
	public GroupMemberManageView thisView;
	public GroupMemberManageController thisController;
	public Activity thisActivity;

	public Group currentGroup;

	public OnClickListener mOnClickListener;

	public GroupMemberManageController(Activity thisActivity) {
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
				thisView.showCurrentGroupMembers();
			}
		} else {
			thisActivity.finish();
		}
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.confirmButtonView)) {
					thisView.isSubtract = thisView.MANAGE_COMMON;
					thisView.groupMembersAdapter.notifyDataSetChanged();
					thisView.confirmButtonView.setVisibility(View.GONE);
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					// String content = tagContent.substring(index + 1);
					if ("invitafriendroup".equals(type)) {
						Intent intent = new Intent(thisActivity, InviteFriendActivity.class);
						intent.putExtra("type", InviteFriendActivity.INVITA_FRIEND_GROUP);
						intent.putExtra("gid", currentGroup.gid + "");
						thisActivity.startActivityForResult(intent, InviteFriendActivity.INVITA_FRIEND_GROUP);
					} else if ("managesubtract".equals(type)) {
						thisView.isSubtract = thisView.MANAGE_SUBTRACT;
						thisView.groupMembersAdapter.notifyDataSetChanged();
						thisView.confirmButtonView.setVisibility(View.VISIBLE);
					}
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.confirmButtonView.setOnClickListener(mOnClickListener);
	}
}
