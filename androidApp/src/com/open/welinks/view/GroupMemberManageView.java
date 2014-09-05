package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.welinks.R;
import com.open.welinks.controller.GroupMemberManageController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.utils.MCImageUtils;

public class GroupMemberManageView {

	public Data data = Data.getInstance();
	public String tag = "GroupMemberManageView";

	public Context context;
	public GroupMemberManageView thisView;
	public GroupMemberManageController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;

	// top bar
	public LinearLayout backView;
	public TextView groupMemberCountView;
	public TextView confirmButtonView;

	// main content
	public GridView groupMemberGridView;

	public Bitmap defaultBitmapHead;

	public int MANAGE_COMMON = 0x00;
	public int MANAGE_SUBTRACT = 0x01;

	public int isSubtract = MANAGE_COMMON;

	public GroupMembersAdapter groupMembersAdapter;

	public GroupMemberManageView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisView = this;
	}

	public void initView() {
		thisActivity.setContentView(R.layout.activity_group_member_manage);

		backView = (LinearLayout) thisActivity.findViewById(R.id.backView);
		groupMemberCountView = (TextView) thisActivity.findViewById(R.id.groupMemberCount);
		confirmButtonView = (TextView) thisActivity.findViewById(R.id.confirmButton);

		groupMemberGridView = (GridView) thisActivity.findViewById(R.id.gridView_groupmembers);

		mInflater = thisActivity.getLayoutInflater();
		// default head
		Resources resources = thisActivity.getResources();
		defaultBitmapHead = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		defaultBitmapHead = MCImageUtils.getCircleBitmap(defaultBitmapHead, true, 5, Color.WHITE);
	}

	public void showCurrentGroupMembers() {
		groupMembersAdapter = new GroupMembersAdapter();
		this.groupMemberGridView.setAdapter(groupMembersAdapter);
	}

	public List<String> subtractMembers = new ArrayList<String>();

	public class GroupMembersAdapter extends BaseAdapter {

		public List<String> members = new ArrayList<String>();
		public Map<String, Friend> friendsMap;

		public GroupMembersAdapter() {
			members.clear();
			members.addAll(thisController.currentGroup.members);
			friendsMap = data.relationship.friendsMap;
			groupMemberCountView.setText("成员管理 ( " + members.size() + "人 )");
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			groupMemberCountView.setText("成员管理 ( " + members.size() + "人 )");
		}

		@Override
		public int getCount() {
			if (isSubtract == MANAGE_COMMON) {
				return members.size() + 2;
			} else {
				return members.size();
			}
		}

		@Override
		public Object getItem(int position) {
			return members.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageHolder imageHolder = null;
			if (convertView == null) {
				imageHolder = new ImageHolder();
				convertView = mInflater.inflate(R.layout.activity_group_members_item, null);
				imageHolder.imageContent = (ImageView) convertView.findViewById(R.id.iv_image);
				imageHolder.imageContentStatus = (ImageView) convertView.findViewById(R.id.iv_imageContentStatus);
				imageHolder.nickNameView = (TextView) convertView.findViewById(R.id.tv_username);
				convertView.setTag(imageHolder);
			} else {
				imageHolder = (ImageHolder) convertView.getTag();
			}
			if (isSubtract == MANAGE_SUBTRACT) {
				imageHolder.imageContentStatus.setVisibility(View.VISIBLE);
			} else {
				if (imageHolder.imageContentStatus.getVisibility() == View.VISIBLE)
					imageHolder.imageContentStatus.setVisibility(View.GONE);
			}
			final ImageHolder imageHolder0 = imageHolder;
			if (position < members.size() + 2 - 2) {
				final Friend friend = friendsMap.get(members.get(position));
				String nickName = "";
				if (friend.nickName.length() >= 4) {
					nickName = friend.nickName.substring(0, 4);
				} else {
					nickName = friend.nickName;
				}
				imageHolder.nickNameView.setText(nickName);
				imageHolder0.imageContent.setImageBitmap(defaultBitmapHead);

				convertView.setTag(R.id.iv_image, "subtractonclick#" + friend.phone);
				convertView.setOnClickListener(thisController.mOnClickListener);
			} else {
				if (isSubtract == MANAGE_COMMON) {
					imageHolder.nickNameView.setText("");
					if (position == members.size() + 2 - 2) {
						imageHolder0.imageContent.setImageResource(R.drawable.subtract_icon);
						imageHolder0.imageContent.setTag("managesubtract#");
						imageHolder0.imageContent.setOnClickListener(thisController.mOnClickListener);
					} else {
						imageHolder0.imageContent.setImageResource(R.drawable.add_icon);
						imageHolder0.imageContent.setTag("invitafriendgroup#");
						imageHolder0.imageContent.setOnClickListener(thisController.mOnClickListener);
					}
				}
			}

			return convertView;
		}
	}

	class ImageHolder {
		ImageView imageContent;
		ImageView imageContentStatus;

		TextView nickNameView;
	}
}
