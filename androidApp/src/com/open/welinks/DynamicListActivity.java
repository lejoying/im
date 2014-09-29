package com.open.welinks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.HttpClient;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Event.EventMessage;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.LBSHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.view.ThreeChoicesView;
import com.open.welinks.view.ThreeChoicesView.OnItemClickListener;
import com.open.welinks.view.ViewManage;

public class DynamicListActivity extends Activity {

	public String tag = "DynamicListActivity";

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public RelativeLayout backView;
	public TextView backTitleView;
	public RelativeLayout rightContainerView;

	public ListView squareEventContainer;
	public ListView groupEventContainer;
	public ListView userEventContainer;

	public OnClickListener mOnClickListener;

	public LayoutInflater mInflater;

	// public UserEventListAdapter squareEventListAdapter;
	public GroupEventListAdapter groupEventListAdapter;
	public UserEventListAdapter userEventListAdapter;

	public ViewManage viewManage = ViewManage.getInstance();

	public List<String> groupEventMessages = new ArrayList<String>();
	public List<String> userEventMessages = new ArrayList<String>();
	public Map<String, Friend> friendsMap;
	public Gson gson = new Gson();

	// public Bitmap bitmap;
	public int selectType = 3;
	public ThreeChoicesView threeChoicesView;

	public DisplayImageOptions headOptions;
	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public LBSHandlers lbsHandlers = LBSHandlers.getInstance();
	public DisplayMetrics displayMetrics;

	public View maxView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		selectType = getIntent().getIntExtra("type", 3);

		viewManage.dynamicListActivity = this;
		friendsMap = data.relationship.friendsMap;
		initView();
		initializeListeners();
		bindEvent();
		initData("all");
		showEventList();
		changData(selectType);
		getRequareAddFriendList();
	}

	@Override
	public void finish() {
		viewManage.postNotifyView("MessagesSubView");
		viewManage.postNotifyView("MeSubView");
		super.finish();
	}

	public void initData(String type) {
		parser.check();
		if ("user".equals(type) || "all".equals(type)) {
			userEventMessages.clear();
			List<String> userEventMessages0 = data.event.userEvents;
			if (userEventMessages0 != null && data.event.userEventsMap != null) {
				for (int i = userEventMessages0.size() - 1; i >= 0; i--) {
					String key = userEventMessages0.get(i);
					EventMessage message0 = data.event.userEventsMap.get(key);
					if ("account_dataupdate".equals(message0.type) || "relation_newfriend".equals(message0.type) || "relation_addfriend".equals(message0.type)) {
						userEventMessages.add(message0.eid);
					}
				}
			} else {
				Log.e(tag, userEventMessages0 + "---" + data.event.userEventsMap);
			}
		}

		if ("group".equals(type) || "all".equals(type)) {
			groupEventMessages.clear();
			List<String> groupEventMessages0 = data.event.groupEvents;
			for (int i = groupEventMessages0.size() - 1; i >= 0; i--) {
				String key = groupEventMessages0.get(i);
				EventMessage event = data.event.groupEventsMap.get(key);
				if (event != null) {
					groupEventMessages.add(key);
				}
			}
		}
	}

	public void showEventList() {
		if (selectType == 1) {
			// TODO square event
		} else if (selectType == 2) {
			if (groupEventListAdapter == null) {
				groupEventListAdapter = new GroupEventListAdapter();
				groupEventContainer.setAdapter(groupEventListAdapter);
				parser.check();
				data.event.groupNotReadMessage = false;
				data.event.isModified = true;
			}
		} else if (selectType == 3) {
			if (userEventListAdapter == null) {
				userEventListAdapter = new UserEventListAdapter();
				userEventContainer.setAdapter(userEventListAdapter);
				parser.check();
				data.event.userNotReadMessage = false;
				data.event.isModified = true;
			}
		}
	}

	public void initView() {
		mInflater = this.getLayoutInflater();
		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		setContentView(R.layout.activity_dynamiclist);
		maxView = findViewById(R.id.maxView);
		backView = (RelativeLayout) findViewById(R.id.backView);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		backTitleView.setText("动态列表");
		rightContainerView = (RelativeLayout) findViewById(R.id.rightContainer);

		squareEventContainer = (ListView) findViewById(R.id.squareEventContainer);
		groupEventContainer = (ListView) findViewById(R.id.groupEventContainer);
		userEventContainer = (ListView) findViewById(R.id.userEventContainer);

		threeChoicesView = new ThreeChoicesView(this, selectType);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightContainerView.addView(threeChoicesView, params);

		initSmallBusinessCardDialog();
	}

	public OnItemClickListener mOnItemClickListener;

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(userCardMainView)) {
					dismissUserCardDialogView();
				} else if (view.equals(goChatView)) {
					Intent intent = new Intent(DynamicListActivity.this, ChatActivity.class);
					intent.putExtra("id", (String) view.getTag(R.id.tag_first));
					intent.putExtra("type", (String) view.getTag(R.id.tag_second));
					startActivityForResult(intent, R.id.tag_second);
					dismissUserCardDialogView();
				} else if (view.equals(goInfomationView)) {
					Intent intent = new Intent(DynamicListActivity.this, BusinessCardActivity.class);
					intent.putExtra("key", (String) view.getTag(R.id.tag_first));
					intent.putExtra("type", (String) view.getTag(R.id.tag_second));
					startActivity(intent);
					dismissUserCardDialogView();
				} else if (view.equals(singleButtonView)) {
					Intent intent = new Intent(DynamicListActivity.this, BusinessCardActivity.class);
					String key = (String) view.getTag(R.id.tag_first);
					String type = (String) view.getTag(R.id.tag_second);
					intent.putExtra("key", key);
					intent.putExtra("type", type);
					intent.putExtra("isTemp", true);
					if ("point".equals(type)) {
						data.tempData.tempFriend = data.relationship.friendsMap.get(key);
					} else if ("group".equals(type)) {
						data.tempData.tempGroup = data.relationship.groupsMap.get(key);
					}
					startActivity(intent);
					dismissUserCardDialogView();
				} else if (view.getTag(R.id.tag_class) != null) {
					String tag_class = (String) view.getTag(R.id.tag_class);
					if ("event_group".equals(tag_class)) {
						String key = (String) view.getTag(R.id.tag_first);
						parser.check();
						boolean isExists = data.relationship.groups.contains(key);
						if (isExists) {
							Group group = data.relationship.groupsMap.get(key);
							setSmallBusinessCardContent(group.gid + "", group.icon, group.name, "", group.longitude, group.latitude, TYPE_CARD_GROUP, isExists);
						} else {
							Group group = data.relationship.groupsMap.get(key);
							if (group != null) {
								setSmallBusinessCardContent(group.gid + "", group.icon, group.name, "", group.longitude, group.latitude, TYPE_CARD_GROUP, isExists);
							} else {
								setSmallBusinessCardContent(key, "", "", "", "0", "0", TYPE_CARD_GROUP, isExists);
							}
							scanGroupCard(key);
						}
						showUserCardDialogView();
					} else if ("event_user".equals(tag_class)) {
						String key = (String) view.getTag(R.id.tag_first);
						parser.check();
						User user = data.userInformation.currentUser;
						if (user.phone.equals(key)) {
							setSmallBusinessCardContent(user.phone + "", user.head, user.nickName, "", user.longitude, user.latitude, TYPE_CARD_FRIEND, true);
						} else {
							boolean isExists = data.relationship.friends.contains(key);
							if (isExists) {
								Friend friend = data.relationship.friendsMap.get(key);
								setSmallBusinessCardContent(friend.phone + "", friend.head, friend.nickName, "", friend.longitude, friend.latitude, TYPE_CARD_FRIEND, true);
							} else {
								Friend friend = data.relationship.friendsMap.get(key);
								if (friend != null) {
									setSmallBusinessCardContent(friend.phone + "", friend.head, friend.nickName, "", friend.longitude, friend.latitude, TYPE_CARD_FRIEND, false);
								} else {
									setSmallBusinessCardContent(key, "", "", "", "0", "0", TYPE_CARD_FRIEND, false);
								}
							}
							scanUserCard(key);
						}
						showUserCardDialogView();
					}
				}
			}
		};
		mOnItemClickListener = threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {
				selectType = position;
				changData(selectType);
				showEventList();
			}
		};
	}

	private void changData(int selectType) {
		if (selectType == 1) {
			squareEventContainer.setVisibility(View.VISIBLE);
			groupEventContainer.setVisibility(View.GONE);
			userEventContainer.setVisibility(View.GONE);
		} else if (selectType == 2) {
			squareEventContainer.setVisibility(View.GONE);
			groupEventContainer.setVisibility(View.VISIBLE);
			userEventContainer.setVisibility(View.GONE);
		} else if (selectType == 3) {
			squareEventContainer.setVisibility(View.GONE);
			groupEventContainer.setVisibility(View.GONE);
			userEventContainer.setVisibility(View.VISIBLE);
		}

	}

	public void bindEvent() {
		threeChoicesView.setOnItemClickListener(mOnItemClickListener);
		this.backView.setOnClickListener(mOnClickListener);
		this.userCardMainView.setOnClickListener(mOnClickListener);
	}

	public class GroupEventListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			initData("group");
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return groupEventMessages.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			EventHolder holder = null;
			if (convertView == null) {
				holder = new EventHolder();
				convertView = mInflater.inflate(R.layout.activity_dynamiclist_item, null);
				holder.headView = (ImageView) convertView.findViewById(R.id.headImage);
				holder.eventContentView = (TextView) convertView.findViewById(R.id.eventContent);
				holder.timeView = (TextView) convertView.findViewById(R.id.eventTime);
				holder.eventOperationView = (LinearLayout) convertView.findViewById(R.id.eventOperation);
				holder.agreeButtonView = (TextView) convertView.findViewById(R.id.agreeButton);
				holder.ignoreButtonView = (TextView) convertView.findViewById(R.id.ignoreButton);
				holder.processedView = (TextView) convertView.findViewById(R.id.processed);
				convertView.setTag(holder);
			} else {
				holder = (EventHolder) convertView.getTag();
			}
			holder.eventOperationView.setVisibility(View.GONE);
			holder.processedView.setVisibility(View.GONE);
			String content = "";

			String headFileName = "";
			// holder.headView.setImageBitmap(bitmap);

			String key = groupEventMessages.get(position);
			EventMessage event = data.event.groupEventsMap.get(key);
			if (event == null) {
				holder.timeView.setText("");
				holder.eventContentView.setText("");
				return convertView;
			}
			String nickName = event.phone;
			Friend friend = data.relationship.friendsMap.get(nickName);
			if (friend != null) {
				headFileName = friend.phone;
				nickName = friend.nickName;
				if (friend.phone.equals(data.userInformation.currentUser.phone)) {
					nickName = "您";
				}
			}
			final Group group = data.relationship.groupsMap.get(event.gid + "");
			String groupName = event.gid;
			// final boolean isExists = data.relationship.groups.contains(event.gid);
			if (group != null) {
				headFileName = group.icon;
				groupName = group.name;
			}
			String contentType = event.type;
			holder.timeView.setText(DateUtil.getTime(Long.valueOf(event.time)));
			if ("group_addmembers".equals(contentType)) {
				content = "【" + nickName + "】 邀请了" + event.content + "个好友到 【" + groupName + "】 群组中.";
			} else if ("group_removemembers".equals(contentType)) {
				content = "【" + nickName + "】 从【" + groupName + "】 移除了" + event.content + "个好友.";
			} else if ("group_dataupdate".equals(contentType)) {
				content = "【" + nickName + "】 更新了 【" + groupName + "】 的资料信息.";
			} else if ("group_create".equals(contentType)) {
				content = "【" + nickName + "】创建了新的群组:【" + groupName + "】.";
			} else if ("group_addme".equals(contentType)) {
				content = "【" + nickName + "】把你从添加到群组：【" + groupName + "】.";
			} else if ("group_removeme".equals(contentType)) {
				content = "【" + nickName + "】退出了【" + groupName + "】群组.";
			}
			holder.eventContentView.setText(content);

			fileHandlers.getHeadImage(headFileName, holder.headView, headOptions);
			convertView.setTag(R.id.tag_class, "event_group");
			convertView.setTag(R.id.tag_first, event.gid);
			convertView.setOnClickListener(mOnClickListener);
			return convertView;
		}
	}

	public class UserEventListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			initData("user");
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return userEventMessages.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			EventHolder holder = null;
			if (convertView == null) {
				holder = new EventHolder();
				convertView = mInflater.inflate(R.layout.activity_dynamiclist_item, null);
				holder.headView = (ImageView) convertView.findViewById(R.id.headImage);
				holder.eventContentView = (TextView) convertView.findViewById(R.id.eventContent);
				holder.timeView = (TextView) convertView.findViewById(R.id.eventTime);
				holder.eventOperationView = (LinearLayout) convertView.findViewById(R.id.eventOperation);
				holder.agreeButtonView = (TextView) convertView.findViewById(R.id.agreeButton);
				holder.ignoreButtonView = (TextView) convertView.findViewById(R.id.ignoreButton);
				holder.processedView = (TextView) convertView.findViewById(R.id.processed);
				convertView.setTag(holder);
			} else {
				holder = (EventHolder) convertView.getTag();
			}
			final EventHolder holder0 = holder;
			Friend friend;

			String headFileName = "";
			String content = "";
			String nickName = "";
			try {
				String key = userEventMessages.get(position);
				EventMessage event = data.event.userEventsMap.get(key);
				if ("relation_newfriend".equals(event.type)) {
					friend = friendsMap.get(event.phone);
					if (event.content != null) {
						content = event.content;
					}
					if (friend != null) {
						headFileName = friend.head;
						nickName = friend.nickName;
					} else {
						nickName = event.phone;
					}
					holder.timeView.setText(DateUtil.getTime(Long.valueOf(event.time)));
					holder.eventContentView.setText("【" + nickName + "】  请求加你为好友!验证信息:" + content);
					if (event.status.equals("waiting")) {
						holder.eventOperationView.setVisibility(View.VISIBLE);
						holder.processedView.setVisibility(View.GONE);
						final EventMessage event0 = event;
						holder.agreeButtonView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								holder0.eventOperationView.setVisibility(View.GONE);
								holder0.processedView.setVisibility(View.VISIBLE);
								holder0.processedView.setText("已添加");
								// modify local data
								parser.check();
								event0.status = "success";
								agreeAddFriend(event0.phone);
								data.event.isModified = true;
							}
						});
						holder.ignoreButtonView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								holder0.eventOperationView.setVisibility(View.GONE);
								holder0.processedView.setVisibility(View.VISIBLE);
								holder0.processedView.setText("已处理");
								// modify local data
								parser.check();
								event0.status = "ignore";
								data.event.isModified = true;
							}
						});
					} else if (event.status.equals("success")) {
						holder.eventOperationView.setVisibility(View.GONE);
						holder.processedView.setVisibility(View.VISIBLE);
						holder.processedView.setText("已添加");
					} else if (event.status.equals("ignore")) {
						holder.eventOperationView.setVisibility(View.GONE);
						holder.processedView.setVisibility(View.VISIBLE);
						holder.processedView.setText("已处理");
					} else {
						holder0.eventOperationView.setVisibility(View.GONE);
						holder0.processedView.setVisibility(View.GONE);
					}
				} else if ("relation_addfriend".equals(event.type)) {
					holder.eventOperationView.setVisibility(View.GONE);
					holder.processedView.setVisibility(View.VISIBLE);
					friend = friendsMap.get(event.phoneTo);
					if (event.content != null) {
						content = event.content;
					}
					if (friend != null) {
						headFileName = friend.head;
						nickName = friend.nickName;
					} else {
						nickName = event.phone;
					}
					holder.timeView.setText(DateUtil.getTime(Long.valueOf(event.time)));
					holder.eventContentView.setText("您请求加" + nickName + "为好友!验证信息:" + content);
					if (event.status.equals("waiting")) {
						holder.processedView.setText("等待验证");
					} else if (event.status.equals("success")) {
						holder.processedView.setText("已添加");
					}
					try {
						if (data.relationship.circlesMap.get("8888888").friends.contains(event.phoneTo)) {
							event.status = "success";
							data.event.isModified = true;
							holder.processedView.setText("已添加");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("account_dataupdate".equals(event.type)) {
					headFileName = data.userInformation.currentUser.head;
					holder.timeView.setText(DateUtil.getTime(Long.valueOf(event.time)));
					holder.eventContentView.setText("更新个人资料");
					holder.eventOperationView.setVisibility(View.GONE);
					holder.processedView.setVisibility(View.GONE);
				}
				convertView.setTag(R.id.tag_class, "event_user");
				convertView.setTag(R.id.tag_first, event.phone);
				convertView.setOnClickListener(mOnClickListener);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			// holder.headView.setImageBitmap(bitmap);
			fileHandlers.getHeadImage(headFileName, holder.headView, headOptions);
			return convertView;
		}
	}

	public class UserEvent {
		public String type;
		public String phone;
		public String time;
		public String status;
		public String content;
	}

	public class EventHolder {
		public ImageView headView;
		public TextView eventContentView;
		public TextView timeView;
		public LinearLayout eventOperationView;
		public TextView agreeButtonView;
		public TextView ignoreButtonView;
		public TextView processedView;
	}

	// small businesscard
	public DisplayImageOptions smallBusinessCardOptions;
	public View userCardMainView;
	public PopupWindow userCardPopWindow;
	public RelativeLayout userBusinessContainer;
	public TextView goInfomationView;
	public TextView goChatView;
	public ImageView userHeadView;
	public TextView userNickNameView;
	public TextView userAgeView;
	public TextView distanceView;
	public TextView lastLoginTimeView;
	public LinearLayout optionTwoView;
	public TextView singleButtonView;
	public TextView cardStatusView;

	@SuppressWarnings("deprecation")
	public void initSmallBusinessCardDialog() {
		userCardMainView = mInflater.inflate(R.layout.account_info_pop, null);
		optionTwoView = (LinearLayout) userCardMainView.findViewById(R.id.optionTwo);
		userNickNameView = (TextView) userCardMainView.findViewById(R.id.userNickName);
		userAgeView = (TextView) userCardMainView.findViewById(R.id.userAge);
		distanceView = (TextView) userCardMainView.findViewById(R.id.userDistance);
		lastLoginTimeView = (TextView) userCardMainView.findViewById(R.id.lastLoginTime);
		userBusinessContainer = (RelativeLayout) userCardMainView.findViewById(R.id.userBusinessView);
		int height = (int) (displayMetrics.heightPixels * 0.5f - 50 * displayMetrics.density) + getStatusBarHeight(this);
		userBusinessContainer.getLayoutParams().height = height;
		goInfomationView = (TextView) userCardMainView.findViewById(R.id.goInfomation);
		goChatView = (TextView) userCardMainView.findViewById(R.id.goChat);
		singleButtonView = (TextView) userCardMainView.findViewById(R.id.singleButton);
		cardStatusView = (TextView) userCardMainView.findViewById(R.id.cardStatus);
		// singleButtonView.setVisibility(View.GONE);
		userHeadView = (ImageView) userCardMainView.findViewById(R.id.userHead);
		userHeadView.getLayoutParams().height = height;
		userCardPopWindow = new PopupWindow(userCardMainView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		userCardPopWindow.setBackgroundDrawable(new BitmapDrawable());
		smallBusinessCardOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(10)).build();
	}

	public int TYPE_CARD_FRIEND = 0x01;
	public int TYPE_CARD_GROUP = 0x02;

	public void setSmallBusinessCardContent(String key, String head, String nickName, String age, String longitude, String latitude, int type, boolean isRelationship) {
		User user = data.userInformation.currentUser;
		String cardStatus = "";
		goInfomationView.setTag(R.id.tag_class, type);
		goInfomationView.setTag(R.id.tag_first, key);
		if (isRelationship) {
			goChatView.setTag(R.id.tag_class, type);
			goChatView.setTag(R.id.tag_first, key);
			goInfomationView.setOnClickListener(mOnClickListener);
			goChatView.setOnClickListener(mOnClickListener);
			singleButtonView.setVisibility(View.GONE);
			if (type == TYPE_CARD_FRIEND) {
				cardStatus = "已是好友";
				goChatView.setTag(R.id.tag_second, "point");
				// goInfomationView.setTag(R.id.tag_third, true);
				goInfomationView.setTag(R.id.tag_second, "point");
				// goInfomationView.setTag(R.id.tag_third, true);
			} else if (type == TYPE_CARD_GROUP) {
				cardStatus = "已加入群组";
				goChatView.setTag(R.id.tag_second, "group");
				goInfomationView.setTag(R.id.tag_second, "group");
			}
			if (user.phone.equals(key)) {
				singleButtonView.setTag(R.id.tag_class, type);
				singleButtonView.setTag(R.id.tag_first, key);
				singleButtonView.setTag(R.id.tag_second, "point");
				optionTwoView.setVisibility(View.GONE);
				singleButtonView.setVisibility(View.VISIBLE);
				singleButtonView.setOnClickListener(mOnClickListener);
			}
		} else {
			singleButtonView.setTag(R.id.tag_class, type);
			singleButtonView.setTag(R.id.tag_first, key);
			singleButtonView.setOnClickListener(mOnClickListener);
			optionTwoView.setVisibility(View.GONE);
			singleButtonView.setVisibility(View.VISIBLE);
			if (type == TYPE_CARD_FRIEND) {
				cardStatus = "不是好友";
				singleButtonView.setTag(R.id.tag_second, "point");
			} else if (type == TYPE_CARD_GROUP) {
				cardStatus = "未加入群组";
				singleButtonView.setTag(R.id.tag_second, "group");
			}
		}
		cardStatusView.setText(cardStatus);
		fileHandlers.getHeadImage(head, userHeadView, smallBusinessCardOptions);
		userNickNameView.setText(nickName);
		distanceView.setText(lbsHandlers.pointDistance(user.longitude, user.latitude, longitude, latitude) + "km");
		if (type == TYPE_CARD_GROUP) {
			lastLoginTimeView.setVisibility(View.GONE);
			userAgeView.setVisibility(View.GONE);
		} else {
			lastLoginTimeView.setText("0小时前");
			userAgeView.setText(age + "");
		}
	}

	public void showUserCardDialogView() {
		if (userCardPopWindow != null && !userCardPopWindow.isShowing()) {
			userCardPopWindow.showAtLocation(maxView, Gravity.CENTER, 0, 0);
		}
	}

	public void dismissUserCardDialogView() {
		if (userCardPopWindow != null && userCardPopWindow.isShowing()) {
			userCardPopWindow.dismiss();
		}
	}

	public HttpClient httpClient = HttpClient.getInstance();

	public void scanGroupCard(final String gid) {
		Log.e("Coolspan-DynamicList", gid);
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("type", "group");

		httpUtils.send(HttpMethod.POST, API.GROUP_GET, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public Group group;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("获取群组信息成功".equals(response.提示信息)) {
					Group group = response.group;
					if (group != null) {
						parser.check();
						String gid = group.gid + "";
						Group group0 = data.relationship.groupsMap.get(gid);
						boolean flag = data.relationship.groups.contains(gid);
						if (group0 != null) {
							group0.icon = group.icon;
							group0.name = group.name;
							group0.longitude = group.longitude;
							group0.latitude = group.latitude;
							group0.description = group.description;
							group0.background = group.background;
						} else {
							data.relationship.groupsMap.put(gid, group);
						}
						data.relationship.isModified = true;
						int type = (Integer) goInfomationView.getTag(R.id.tag_class);
						if (type == TYPE_CARD_GROUP) {
							String key = (String) goInfomationView.getTag(R.id.tag_first);
							if (gid.equals(key)) {
								setSmallBusinessCardContent(group.gid + "", group.icon, group.name, "", group.longitude, group.latitude, TYPE_CARD_GROUP, flag);
							}
						}
					}
				}
			};
		});
	}

	public void scanUserCard(String phone) {
		Log.e("Coolspan-DynamicList", phone);
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("target", "[\"" + phone + "\"]");

		httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public List<Friend> accounts;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("获取用户信息成功".equals(response.提示信息)) {
					Friend friend = response.accounts.get(0);
					if (friend != null) {
						parser.check();
						Friend friend0 = data.relationship.friendsMap.get(friend.phone);
						boolean flag = data.relationship.friends.contains(friend.phone);
						if (friend0 != null) {
							friend0.sex = friend.sex;
							friend0.nickName = friend.nickName;
							friend0.mainBusiness = friend.mainBusiness;
							friend0.head = friend.head;
							friend0.longitude = friend.longitude;
							friend0.latitude = friend.latitude;
							friend0.userBackground = friend.userBackground;
							friend0.lastlogintime = friend.lastlogintime;
						} else {
							data.relationship.friendsMap.put(friend.phone, friend);
						}
						data.relationship.isModified = true;
						int type = (Integer) goInfomationView.getTag(R.id.tag_class);
						if (type == TYPE_CARD_FRIEND) {
							String key = (String) goInfomationView.getTag(R.id.tag_first);
							if (friend.phone.equals(key)) {
								setSmallBusinessCardContent(friend.phone + "", friend.head, friend.nickName, "", friend.longitude, friend.latitude, TYPE_CARD_FRIEND, flag);
							}
						}
					}
				}
			};
		});
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public void getRequareAddFriendList() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);

		httpUtils.send(HttpMethod.POST, API.RELATION_GETASKFRIENDS, params, responseHandlers.getaskfriendsCallBack);
	}

	public void agreeAddFriend(String phoneTo) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("phoneask", phoneTo);
		params.addBodyParameter("status", "true");

		httpUtils.send(HttpMethod.POST, API.RELATION_ADDFRIENDAGREE, params, responseHandlers.addFriendAgreeCallBack);
	}
}
