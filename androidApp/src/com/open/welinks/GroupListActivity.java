package com.open.welinks;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.view.ThreeChoicesView;
import com.open.welinks.view.ViewManage;
import com.open.welinks.view.Alert.AlertInputDialog;

public class GroupListActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "GroupListActivity";
	public MyLog log = new MyLog(tag, false);

	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public TextView backTitileView;
	public TextView titleView;
	public RelativeLayout rightContainer;

	public TextView createGroupButton;

	public ListView groupListContainer;

	public ThreeChoicesView threeChoicesView;

	public DisplayMetrics displayMetrics;

	public OnClickListener mOnClickListener;
	public OnItemClickListener mOnItemClickListener;
	public com.open.welinks.view.ThreeChoicesView.OnItemClickListener mOnThreeChoiceItemClickListener;

	public List<String> groups;
	public String[] friends;
	public Map<String, Group> groupsMap;
	public Map<String, Friend> friendsMap;
	public GroupListAdapter groupListAdapter;

	public Bitmap bitmap;

	public ViewManage viewManage = ViewManage.getInstance();

	public Status status;

	private enum Status {
		square, friend, share_group, message_group, list_group
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewManage.groupListActivity = this;
		String type = getIntent().getStringExtra("type");
		if ("list_group".equals(type)) {
			this.status = Status.list_group;
		} else if ("share".equals(type)) {
			this.status = Status.square;
		} else if ("message".equals(type)) {
			this.status = Status.friend;
		}
		initView();
		initializeListeners();
		bindEvent();
		initData();
	}

	private void initData() {
		friends = new String[] {};
		parser.check();
		if (status == Status.friend) {
			friendsMap = data.relationship.friendsMap;
			Set<String> friends = new HashSet<String>();
			for (String circles : data.relationship.circles) {
				friends.addAll(data.relationship.circlesMap.get(circles).friends);
			}
			if (data.relationship.circlesMap.get("8888888") != null) {
				friends.addAll(data.relationship.circlesMap.get("8888888").friends);
			}
			this.friends = friends.toArray(this.friends);
		} else {
			if (status == Status.square) {
				groups = data.relationship.squares;
			} else {
				groups = data.relationship.groups;
			}
			groupsMap = data.relationship.groupsMap;

		}
		groupListAdapter = new GroupListAdapter();
		groupListContainer.setAdapter(groupListAdapter);
	}

	private void initView() {
		displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		mInflater = this.getLayoutInflater();

		Resources resources = getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		setContentView(R.layout.activity_group_list);
		this.backView = (RelativeLayout) findViewById(R.id.backView);
		this.backTitileView = (TextView) findViewById(R.id.backTitleView);
		this.titleView = (TextView) findViewById(R.id.titleContent);
		this.rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);

		this.groupListContainer = (ListView) findViewById(R.id.groupListContainer);
		this.createGroupButton = new TextView(this);
		this.threeChoicesView = new ThreeChoicesView(this);
		this.threeChoicesView.setTwoChoice();

		if (status == Status.list_group) {
			this.backTitileView.setText("群组列表");
			int dp_5 = (int) (5 * displayMetrics.density);
			this.createGroupButton.setGravity(Gravity.CENTER);
			this.createGroupButton.setTextColor(Color.WHITE);
			this.createGroupButton.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
			this.createGroupButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			this.createGroupButton.setText("创建群组");
			this.createGroupButton.setBackgroundResource(R.drawable.textview_bg);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			this.rightContainer.addView(this.createGroupButton, layoutParams);
		} else {
			this.backTitileView.setText("分享");
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (status == Status.friend) {
				this.threeChoicesView.setButtonOneText("全部好友");
				this.threeChoicesView.setButtonThreeText("群组");
			} else if (status == Status.square) {
				this.threeChoicesView.setButtonOneText("广场");
				this.threeChoicesView.setButtonThreeText("群组");
			}
			this.rightContainer.addView(this.threeChoicesView, layoutParams);
		}
	}

	private void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(createGroupButton)) {
					Intent intent = new Intent(GroupListActivity.this, CreateGroupStartActivity.class);
					startActivity(intent);
				} else if (view.equals(backView)) {
					finish();
				}
			}
		};

		mOnThreeChoiceItemClickListener = this.threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {
				if (position == 1) {
					if (status == Status.share_group) {
						status = Status.square;
					} else if (status == Status.message_group) {
						status = Status.friend;
					}
				} else if (position == 3) {
					if (status == Status.friend) {
						status = Status.message_group;
					} else if (status == Status.square) {
						status = Status.share_group;
					}
				}
				groupListAdapter.notifyDataSetChanged();
			}
		};

		mOnItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if (status == Status.list_group) {
					Intent intent = new Intent(GroupListActivity.this, BusinessCardActivity.class);
					intent.putExtra("type", "group");
					intent.putExtra("key", groups.get(position));
					startActivity(intent);
				} else {
					Alert.createDialog(GroupListActivity.this).setTitle(getDialogTitle(position)).setOnConfirmClickListener(new OnDialogClickListener() {

						@Override
						public void onClick(AlertInputDialog dialog) {
							Intent intent = new Intent();
							if (status == Status.friend) {
								intent.putExtra("key", friendsMap.get(friends[position]).phone);
								intent.putExtra("type", "message");
								intent.putExtra("sendType", "point");
							} else {
								intent.putExtra("key", String.valueOf(groupsMap.get(groups.get(position)).gid));
								if (status == Status.share_group || status == Status.square) {
									intent.putExtra("type", "share");
								} else if (status == Status.message_group) {
									intent.putExtra("type", "message");
									intent.putExtra("sendType", "group");
								}
							}
							setResult(Activity.RESULT_OK, intent);
							finish();
						}
					}).show();
				}
			}
		};
	}

	private void bindEvent() {
		this.createGroupButton.setOnClickListener(mOnClickListener);
		this.backView.setOnClickListener(mOnClickListener);
		this.groupListContainer.setOnItemClickListener(mOnItemClickListener);
		this.threeChoicesView.setOnItemClickListener(mOnThreeChoiceItemClickListener);
	}

	public String getDialogTitle(int position) {
		String title = "是否分享";
		if (status == Status.friend) {
			title += "给好友：【" + friendsMap.get(friends[position]).nickName + "】?";
		} else if (status == Status.message_group) {
			title += "给群组：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.square) {
			title += "到广场：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.share_group) {
			title += "到群组：【" + groupsMap.get(groups.get(position)).name + "】?";
		}
		return title;
	}

	public class GroupListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			if (status == Status.friend) {
				friendsMap = data.relationship.friendsMap;
			} else {
				if (status == Status.square) {
					groups = data.relationship.squares;
				} else {
					groups = data.relationship.groups;
				}
				groupsMap = data.relationship.groupsMap;
			}
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			int size = 0;
			if (status == Status.friend) {
				size = friends.length;
			} else {
				size = groups.size();
			}
			return size;
		}

		@Override
		public Object getItem(int position) {
			Object item = null;
			if (status == Status.friend) {
				item = friends[position];
			} else {
				item = groups.get(position);
			}
			return item;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GroupHolder holder = null;
			if (convertView == null) {
				holder = new GroupHolder();
				convertView = mInflater.inflate(R.layout.activity_group_list_item, null);
				holder.headView = (ImageView) convertView.findViewById(R.id.head);
				holder.nameView = (TextView) convertView.findViewById(R.id.title);
				holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			if (status == Status.friend) {
				Friend friend = friendsMap.get(friends[position]);
				holder.headView.setImageBitmap(bitmap);
				if (friend.alias.equals("")) {
					holder.nameView.setText(friend.nickName);
				} else {
					holder.nameView.setText(friend.alias + "(" + friend.nickName + ")");
				}
				holder.descriptionView.setText(friend.mainBusiness);
			} else {
				Group group = groupsMap.get(groups.get(position));
				holder.headView.setImageBitmap(bitmap);
				holder.nameView.setText(group.name);
				holder.descriptionView.setText(group.description);
			}
			return convertView;
		}
	}

	public class GroupHolder {
		public ImageView headView;
		public TextView nameView;
		public TextView descriptionView;
	}
}
