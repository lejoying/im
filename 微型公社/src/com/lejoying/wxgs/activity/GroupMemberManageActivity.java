package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.InviteOrSelectedFriendActivity.InvitaFriendAdapter;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class GroupMemberManageActivity extends Activity implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();

	static int MANAGE_COMMON = 0x00;
	static int MANAGE_SUBTRACT = 0x01;

	int isSubtract = MANAGE_COMMON;

	LayoutInflater mInflater;

	float height, width, dip;
	float density;

	LinearLayout backView;
	TextView commitCompleteView;

	TextView backTextView;

	GridView groupMembersView;

	Group currentGroup;

	List<String> groupMembers = new ArrayList<String>();
	ArrayList<String> subtractMembers = new ArrayList<String>();

	GroupMembersAdapter groupMembersAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String gid = getIntent().getStringExtra("gid");
		if (gid == null || "".equals(gid)
				|| app.data.groupsMap.get(gid) == null) {
			return;
		}
		currentGroup = app.data.groupsMap.get(gid);
		groupMembers.addAll(currentGroup.members);
		mInflater = this.getLayoutInflater();
		setContentView(R.layout.activity_groupmembersmanage);
		backView = (LinearLayout) findViewById(R.id.ll_groupmanagebackview);
		commitCompleteView = (TextView) findViewById(R.id.tv_commit);
		groupMembersView = (GridView) findViewById(R.id.gridView_groupmembers);
		backTextView = (TextView) findViewById(R.id.tv_backText);
		backTextView.setText("成员管理  (" + groupMembers.size() + " )");
		initData();
		groupMembersAdapter = new GroupMembersAdapter();
		groupMembersView.setAdapter(groupMembersAdapter);
		backView.setOnClickListener(this);
		commitCompleteView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_groupmanagebackview:
			subtractMembers.clear();
			finish();
			break;
		case R.id.tv_commit:
			isSubtract = MANAGE_COMMON;
			groupMembersAdapter.notifyDataSetChanged();
			commitCompleteView.setVisibility(View.GONE);
			modifyGroupMembers(subtractMembers);
			break;
		default:
			break;
		}
	}

	private void initData() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}

	class GroupMembersAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (isSubtract == MANAGE_COMMON) {
				return groupMembers.size() + 2;
			} else {
				return groupMembers.size();
			}
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImageHolder imageHolder = null;
			if (convertView == null) {
				imageHolder = new ImageHolder();
				convertView = mInflater.inflate(
						R.layout.activity_groupmembers_item, null);
				imageHolder.imageContent = (ImageView) convertView
						.findViewById(R.id.iv_image);
				imageHolder.imageContentStatus = (ImageView) convertView
						.findViewById(R.id.iv_imageContentStatus);
				imageHolder.nickNameView = (TextView) convertView
						.findViewById(R.id.tv_username);
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
			if (position < groupMembers.size() + 2 - 2) {
				final Friend friend = app.data.groupFriends.get(groupMembers
						.get(position));
				imageHolder.nickNameView.setText(friend.nickName);
				app.fileHandler.getHeadImage(friend.phone, friend.sex,
						new FileResult() {

							@Override
							public void onResult(String where, Bitmap bitmap) {
								imageHolder0.imageContent
										.setImageBitmap(bitmap);
							}
						});
				if (isSubtract == MANAGE_SUBTRACT) {
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							subtractMembers.add(friend.phone);
							groupMembers.remove(position);
							groupMembersAdapter.notifyDataSetChanged();
						}
					});
				}
			} else {
				if (isSubtract == MANAGE_COMMON) {
					imageHolder.nickNameView.setText("");
					if (position == groupMembers.size() + 2 - 2) {
						imageHolder0.imageContent
								.setImageResource(R.drawable.subtract_icon);
						imageHolder0.imageContent
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										isSubtract = MANAGE_SUBTRACT;
										groupMembersAdapter
												.notifyDataSetChanged();
										commitCompleteView
												.setVisibility(View.VISIBLE);
									}
								});
					} else {
						imageHolder0.imageContent
								.setImageResource(R.drawable.add_icon);
						imageHolder0.imageContent
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										Intent intent = new Intent(
												GroupMemberManageActivity.this,
												InviteOrSelectedFriendActivity.class);
										startActivity(intent);
									}
								});
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

	public float dp2px(float px) {
		float dp = density * px + 0.5f;
		return dp;
	}

	void modifyGroupMembers(List<String> members) {
		final JSONArray membersArray = new JSONArray();
		for (int i = 0; i < members.size(); i++) {
			membersArray.put(members.get(i));
		}
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_REMOVEMEMBERS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", currentGroup.gid + "");
				params.put("members", membersArray.toString());
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				DataUtil.getGroups(new GetDataListener() {

					@Override
					public void getSuccess() {
						groupMembers.clear();
						subtractMembers.clear();
						groupMembers.addAll(currentGroup.members);
						backTextView.setText("成员管理  (" + groupMembers.size()
								+ " )");
						groupMembersAdapter.notifyDataSetChanged();
					}
				});
			}
		});
	}
}
