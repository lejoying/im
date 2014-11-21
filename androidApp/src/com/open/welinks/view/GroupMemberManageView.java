package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
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
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.FileHandlers;

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

	public int MANAGE_COMMON = 0x00;
	public int MANAGE_SUBTRACT = 0x01;
	public int MANAGE_INIT = 0x02;

	public int isSubtract = MANAGE_INIT;

	public GroupMembersAdapter groupMembersAdapter;

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayMetrics displayMetrics;

	public LinearLayout alreadyListContainer;

	public View maxView;

	public GroupMemberManageView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisView = this;
	}

	public SmallBusinessCardPopView businessCardPopView;

	public void initView() {

		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		thisActivity.setContentView(R.layout.activity_group_member_manage);
		maxView = thisActivity.findViewById(R.id.maxView);
		alreadyListContainer = (LinearLayout) thisActivity.findViewById(R.id.alreadyListContainer);
		backView = (LinearLayout) thisActivity.findViewById(R.id.backView);
		groupMemberCountView = (TextView) thisActivity.findViewById(R.id.groupMemberCount);
		confirmButtonView = (TextView) thisActivity.findViewById(R.id.confirmButton);

		groupMemberGridView = (GridView) thisActivity.findViewById(R.id.gridView_groupmembers);

		mInflater = thisActivity.getLayoutInflater();

		businessCardPopView = new SmallBusinessCardPopView(thisActivity, maxView);
	}

	public void showCurrentGroupMembers() {
		groupMembersAdapter = new GroupMembersAdapter();
		this.groupMemberGridView.setAdapter(groupMembersAdapter);
	}

	public void showAlreayList() {
		int width = (int) (40 * displayMetrics.density);
		int spacing = (int) (5 * displayMetrics.density);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
		layoutParams.setMargins(spacing, spacing, spacing, spacing);
		alreadyListContainer.removeAllViews();
		for (int i = 0; i < subtractMembers.size(); i++) {
			String key = subtractMembers.get(i);
			Friend friend = data.relationship.friendsMap.get(key);
			ImageView imageView = new ImageView(thisActivity);
			imageView.setTag(R.id.tag_class, "already_friend");
			imageView.setTag(R.id.tag_first, friend.phone);
			imageView.setOnClickListener(thisController.mOnClickListener);
			alreadyListContainer.addView(imageView, layoutParams);
			fileHandlers.getHeadImage(friend.head, imageView, viewManage.options60);
		}
	}

	ViewManage viewManage = ViewManage.getInstance();

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
			} else if (isSubtract == MANAGE_SUBTRACT) {
				return members.size();
			} else if (isSubtract == MANAGE_INIT) {
				return members.size();
			}
			return 0;
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
				fileHandlers.getHeadImage(friend.head, imageHolder0.imageContent, viewManage.options60);

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
