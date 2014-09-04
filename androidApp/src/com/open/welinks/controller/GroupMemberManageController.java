package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.open.welinks.InviteFriendActivity;
import com.open.welinks.R;
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

	public int REQUESTCODE_INVITEFRIEND = 0x01;

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
				} else if (view.getTag(R.id.iv_image) != null) {
					String tagContent = (String) view.getTag(R.id.iv_image);
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if (thisView.isSubtract == thisView.MANAGE_SUBTRACT) {
						if (data.userInformation.currentUser.phone.equals(content)) {
							// Alert.showMessage("不能把自己移出群组");
							Toast.makeText(thisActivity, "不能把自己移出群组", Toast.LENGTH_SHORT).show();
						} else {
							thisView.subtractMembers.add(content);
							thisView.groupMembersAdapter.members.remove(content);
							thisView.groupMembersAdapter.notifyDataSetChanged();
						}
					} else if (thisView.isSubtract == thisView.MANAGE_COMMON) {
						Toast.makeText(thisActivity, "好友资料", Toast.LENGTH_SHORT).show();
					}
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("invitafriendroup".equals(type)) {
						Intent intent = new Intent(thisActivity, InviteFriendActivity.class);
						intent.putExtra("type", InviteFriendActivity.INVITA_FRIEND_GROUP);
						// intent.putExtra("gid", currentGroup.gid + "");
						thisActivity.startActivityForResult(intent, REQUESTCODE_INVITEFRIEND);
					} else if ("managesubtract".equals(type)) {
						thisView.isSubtract = thisView.MANAGE_SUBTRACT;
						thisView.groupMembersAdapter.notifyDataSetChanged();
						thisView.confirmButtonView.setVisibility(View.VISIBLE);
					} else if ("subtractonclick".equals(type)) {
						Toast.makeText(thisActivity, "不能ssssssss自己移出群组", Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.confirmButtonView.setOnClickListener(mOnClickListener);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == REQUESTCODE_INVITEFRIEND && resultCode == Activity.RESULT_OK) {
			
		}
	}
}
