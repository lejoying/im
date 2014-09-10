package com.open.welinks;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.view.ViewManage;

public class DynamicListActivity extends Activity {

	public String tag = "DynamicListActivity";

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public RelativeLayout backView;
	public TextView backTitleView;

	public ListView eventContainer;

	public OnClickListener mOnClickListener;

	public LayoutInflater mInflater;

	public EventListAdapter eventListAdapter;

	public ViewManage viewManage = ViewManage.getInstance();

	public List<Message> userEventMessages;
	public Map<String, Friend> friendsMap;
	public Gson gson = new Gson();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewManage.dynamicListActivity = this;
		initView();
		initializeListeners();
		bindEvent();
		initData();
		showEventList();
		getRequareAddFriendList();
	}

	private void initData() {
		parser.check();
		userEventMessages = data.event.userEvents;
		friendsMap = data.relationship.friendsMap;
	}

	private void showEventList() {
		eventListAdapter = new EventListAdapter();
		eventContainer.setAdapter(eventListAdapter);
	}

	public Bitmap bitmap;

	public void initView() {
		mInflater = this.getLayoutInflater();

		Resources resources = getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		setContentView(R.layout.activity_dynamiclist);
		backView = (RelativeLayout) findViewById(R.id.backView);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		backTitleView.setText("动态列表");

		eventContainer = (ListView) findViewById(R.id.eventContainer);
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				}
			}
		};
	}

	public void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
	}

	public class EventListAdapter extends BaseAdapter {

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
			Friend friend;
			Message message = userEventMessages.get(position);
			UserEvent event;
			if ("relation_newfriend".equals(message.contentType)) {
				event = gson.fromJson(message.content, UserEvent.class);
				friend = friendsMap.get(event.phone);
				holder.eventContentView.setText(friend.nickName + "  请求加你为好友!");
				if (event.status.equals("waiting")) {
					holder.eventOperationView.setVisibility(View.VISIBLE);
					holder.processedView.setVisibility(View.GONE);
				} else if (event.status.equals("success")) {
					holder.eventOperationView.setVisibility(View.GONE);
					holder.processedView.setVisibility(View.VISIBLE);
				}
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
}
