package com.open.welinks.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.FriendsSortListActivity;
import com.open.welinks.InviteFriendActivity;
import com.open.welinks.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.ResponseHandlers;
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

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public Gson gson = new Gson();

	public GroupMemberManageController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisController = this;
	}

	public void onCreate() {
		String gid = thisActivity.getIntent().getStringExtra("gid");
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
					thisController.currentGroup.members.removeAll(thisView.subtractMembers);
					modifyGroupMembers(API.GROUP_REMOVEMEMBERS, gson.toJson(thisView.subtractMembers), responseHandlers.removeMembersCallBack);
					thisView.subtractMembers.clear();
				} else if (view.getTag(R.id.iv_image) != null) {
					String tagContent = (String) view.getTag(R.id.iv_image);
					int index = tagContent.lastIndexOf("#");
					// String type = tagContent.substring(0, index);
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
					// String content = tagContent.substring(index + 1);
					if ("invitafriendgroup".equals(type)) {
						Intent intent = new Intent(thisActivity, FriendsSortListActivity.class);
						intent.putExtra("type", InviteFriendActivity.INVITA_FRIEND_GROUP);
						intent.putExtra("gid", currentGroup.gid + "");
						thisActivity.startActivityForResult(intent, REQUESTCODE_INVITEFRIEND);
					} else if ("managesubtract".equals(type)) {
						thisView.isSubtract = thisView.MANAGE_SUBTRACT;
						thisView.groupMembersAdapter.notifyDataSetChanged();
						thisView.confirmButtonView.setVisibility(View.VISIBLE);
					} else if ("subtractonclick".equals(type)) {
						Toast.makeText(thisActivity, "不能把自己移出群组", Toast.LENGTH_SHORT).show();
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
			ArrayList<String> inviteFriends = data2.getStringArrayListExtra("invitaFriends");
			Log.e(tag, inviteFriends.toString());
			if (inviteFriends.size() > 0) {
				Log.e(tag, inviteFriends.toString() + "-----add new friend");
				thisController.currentGroup.members.addAll(inviteFriends);
				thisView.groupMembersAdapter.members.addAll(inviteFriends);
				modifyGroupMembers(API.GROUP_ADDMEMBERS, gson.toJson(inviteFriends), responseHandlers.addMembersCallBack);
				Log.e(tag, thisView.groupMembersAdapter.members.toString() + "----");
				// thisView.groupMembersAdapter.notifyDataSetChanged();
				thisView.groupMembersAdapter = thisView.new GroupMembersAdapter();
				thisView.groupMemberGridView.setAdapter(thisView.groupMembersAdapter);
			}
		}
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
}
