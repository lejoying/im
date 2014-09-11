package com.open.welinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseEventHandlers.GroupEvent;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MCImageUtils;
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

	public List<Message> groupEventMessages = new ArrayList<Message>();
	public List<Message> userEventMessages = new ArrayList<Message>();
	public Map<String, Friend> friendsMap;
	public Gson gson = new Gson();

	public Bitmap bitmap;
	public int selectType = 3;
	public ThreeChoicesView threeChoicesView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewManage.dynamicListActivity = this;
		friendsMap = data.relationship.friendsMap;
		initView();
		initializeListeners();
		bindEvent();
		initData();
		showEventList();
		changData(selectType);
		getRequareAddFriendList();
	}

	public void initData() {
		parser.check();
		userEventMessages.clear();
		List<Message> userEventMessages0 = data.event.userEvents;
		for (int i = userEventMessages0.size() - 1; i >= 0; i--) {
			Message message0 = userEventMessages0.get(i);
			if ("account_dataupdate".equals(message0.contentType) || "relation_newfriend".equals(message0.contentType) || "relation_addfriend".equals(message0.contentType)) {
				Message message2 = data.event.userEventsMap.get(message0.gid);
				if (message2 != null) {
					message0 = message2;
				}
				userEventMessages.add(message0);
			}
		}
		List<Message> groupEventMessages0 = data.event.groupEvents;
		for (int i = groupEventMessages0.size() - 1; i >= 0; i--) {
			groupEventMessages.add(groupEventMessages0.get(i));
		}
	}

	public void showEventList() {
		if (selectType == 1) {

		} else if (selectType == 2) {
			if (groupEventListAdapter == null) {
				groupEventListAdapter = new GroupEventListAdapter();
				groupEventContainer.setAdapter(groupEventListAdapter);
			}
		} else if (selectType == 3) {
			if (userEventListAdapter == null) {
				userEventListAdapter = new UserEventListAdapter();
				userEventContainer.setAdapter(userEventListAdapter);
			}
		}
	}

	public void initView() {
		mInflater = this.getLayoutInflater();

		Resources resources = getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		setContentView(R.layout.activity_dynamiclist);
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
	}

	public OnItemClickListener mOnItemClickListener;

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
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
	}

	public class GroupEventListAdapter extends BaseAdapter {

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

			holder.headView.setImageBitmap(bitmap);
			Message message = groupEventMessages.get(position);
			GroupEvent event = gson.fromJson(message.content, GroupEvent.class);
			String nickName = event.phone;
			Friend friend = data.relationship.friendsMap.get(nickName);
			if (friend != null) {
				nickName = friend.nickName;
			}
			Group group = data.relationship.groupsMap.get(event.gid + "");
			String groupName = event.gid;
			if (group != null) {
				groupName = group.name;
			}
			String contentType = message.contentType;
			holder.timeView.setText(DateUtil.getTime(Long.valueOf(event.time)));
			if ("group_addmembers".equals(contentType)) {
				content = nickName + " 邀请了" + event.members.size() + "个好友到 " + groupName + " 群组中.";
			} else if ("group_removemembers".equals(contentType)) {
				content = nickName + " 从" + groupName + " 移除了" + event.members.size() + "个好友.";
			} else if ("group_dataupdate".equals(contentType)) {
				content = nickName + " 更新了 " + groupName + " 的资料信息.";
			}
			holder.eventContentView.setText(content);
			return convertView;
		}
	}

	public class UserEventListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			initData();
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

			String content = "";
			String nickName = "";
			UserEvent event;
			try {
				Message message = userEventMessages.get(position);
				if ("relation_newfriend".equals(message.contentType)) {
					Message messageLocal = data.event.userEventsMap.get(message.gid);
					if (messageLocal != null) {
						message = messageLocal;
					}
					event = gson.fromJson(message.content, UserEvent.class);
					friend = friendsMap.get(event.phone);
					if (event.content != null) {
						content = event.content;
					}
					if (friend != null) {
						nickName = friend.nickName;
					} else {
						nickName = event.phone;
					}
					holder.timeView.setText(DateUtil.getTime(Long.valueOf(event.time)));
					holder.eventContentView.setText(nickName + "  请求加你为好友!验证信息:" + content);
					if (event.status.equals("waiting")) {
						holder.eventOperationView.setVisibility(View.VISIBLE);
						holder.processedView.setVisibility(View.GONE);
						final UserEvent event0 = event;
						final Message message0 = message;
						holder.agreeButtonView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								holder0.eventOperationView.setVisibility(View.GONE);
								holder0.processedView.setVisibility(View.VISIBLE);
								holder0.processedView.setText("已添加");
								// modify local data
								parser.check();
								Message messageLocal = data.event.userEventsMap.get(message0.gid);
								UserEvent event = gson.fromJson(messageLocal.content, UserEvent.class);
								event.status = "success";
								messageLocal.content = gson.toJson(event);
								agreeAddFriend(event.phone);
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
								event0.status = "ignore";
								parser.check();
								Message messageLocal = data.event.userEventsMap.get(message0.gid);
								messageLocal.content = gson.toJson(event0);
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
					}
				} else if ("relation_addfriend".equals(message.contentType)) {
					Message message2 = null;
					Message message0 = data.event.userEventsMap.get(message.gid);
					if (message0 != null) {
						message2 = message0;
					}
					event = gson.fromJson(message2.content, UserEvent.class);
					holder.eventOperationView.setVisibility(View.GONE);
					holder.processedView.setVisibility(View.VISIBLE);
					friend = friendsMap.get(event.phone);
					if (event.content != null) {
						content = event.content;
					}
					if (friend != null) {
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
				} else if ("account_dataupdate".equals(message.contentType)) {
					event = gson.fromJson(message.content, UserEvent.class);
					holder.timeView.setText(DateUtil.getTime(Long.valueOf(event.time)));
					holder.eventContentView.setText("更新个人资料");
					holder.eventOperationView.setVisibility(View.GONE);
					holder.processedView.setVisibility(View.GONE);
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			holder.headView.setImageBitmap(bitmap);
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
