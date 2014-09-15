package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.R;
import com.open.welinks.controller.ChatController;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.DateUtil;

public class ChatView {
	public Data data = Data.getInstance();
	public String tag = "ChatView";

	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;

	public ChatController thisController;
	public Activity thisActivity;
	public Context context;
	public ChatView thisView;

	public RelativeLayout backview;
	public TextView name;
	public ImageView infomation;
	public ListView chat_content;
	public RelativeLayout chat_bottom_bar;
	public ImageView send;
	public ImageView more;
	public EditText input;
	public RelativeLayout chat_bottom_bar_selected;
	public RelativeLayout infomation_layout;
	public RelativeLayout selectedface;
	public RelativeLayout selectpicture;
	public RelativeLayout makeaudio;
	public ImageView more_selected;

	public ChatAdapter mChatAdapter;

	// public Bitmap bitmap;

	public DisplayImageOptions headOptions;
	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public ChatView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		context = thisActivity;
		thisView = this;
	}

	public void initViews() {

		mInflater = thisActivity.getLayoutInflater();
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		thisActivity.setContentView(R.layout.activity_chat);

		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		backview = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		name = (TextView) thisActivity.findViewById(R.id.backTitleView);
		infomation_layout = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		// infomation = (ImageView) thisActivity.findViewById(R.id.infomation);
		infomation = new ImageView(context);
		infomation.setBackgroundResource(R.drawable.share_to_group_icon);
		RelativeLayout.LayoutParams infomationParams = new RelativeLayout.LayoutParams((int) (40 * displayMetrics.density), (int) (40 * displayMetrics.density));
		infomationParams.addRule(RelativeLayout.CENTER_VERTICAL);
		infomation_layout.addView(infomation, infomationParams);

		chat_content = (ListView) thisActivity.findViewById(R.id.chat_content);
		chat_bottom_bar = (RelativeLayout) thisActivity.findViewById(R.id.chat_bottom_bar);
		send = (ImageView) thisActivity.findViewById(R.id.send);
		more = (ImageView) thisActivity.findViewById(R.id.more);
		input = (EditText) thisActivity.findViewById(R.id.input);
		chat_bottom_bar_selected = (RelativeLayout) thisActivity.findViewById(R.id.chat_bottom_bar_selected);
		selectedface = (RelativeLayout) thisActivity.findViewById(R.id.selectedface);
		selectpicture = (RelativeLayout) thisActivity.findViewById(R.id.selectpicture);
		makeaudio = (RelativeLayout) thisActivity.findViewById(R.id.makeaudio);
		more_selected = (ImageView) thisActivity.findViewById(R.id.more_selected);

		// bitmap = BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.face_man);
		// bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
	}

	public void showChatViews() {
		String type = thisController.type, key = thisController.key;
		ArrayList<Message> messages = null;
		if ("group".equals(type)) {
			messages = data.messages.groupMessageMap.get("g" + key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				data.messages.groupMessageMap.put("g" + key, messages);
			}
			Group group = data.relationship.groupsMap.get(key);
			if (group != null) {
				name.setText(group.name + "(" + group.members.size() + ")");
			} else {
				name.setText("Group");
			}
		} else if ("point".equals(type)) {
			messages = data.messages.friendMessageMap.get("p" + key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				data.messages.friendMessageMap.put("p" + key, messages);
			}
			Friend friend = data.relationship.friendsMap.get(key);
			if (friend != null) {
				fileHandlers.getHeadImage(friend.head, infomation, headOptions);
				// if (friend.head.equals("Head") || friend.head.equals("")) {
				// infomation.setImageBitmap(bitmap);
				// } else {
				// thisController.setHeadImage(friend.head, infomation);
				// }
				if ("".equals(friend.alias)) {
					name.setText(friend.nickName);
				} else {
					name.setText(friend.alias);
				}
			} else {
				name.setText("Name");
				fileHandlers.getHeadImage("", infomation, headOptions);
				// infomation.setImageBitmap(bitmap);
			}
		}
		mChatAdapter = new ChatAdapter(messages);
		chat_content.setAdapter(mChatAdapter);
		chat_content.setSelection(mChatAdapter.getCount());
	}

	public class ChatAdapter extends BaseAdapter {

		ArrayList<Message> messages;

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			chat_content.setSelection(this.getCount());
		}

		public ChatAdapter(ArrayList<Message> messages) {
			this.messages = messages;
		}

		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = messages.get(position);
			ChatHolder chatHolder = null;
			int type = message.type;
			// if (convertView == null) {
			chatHolder = new ChatHolder();
			if (type == Constant.MESSAGE_TYPE_SEND) {
				convertView = mInflater.inflate(R.layout.f_chat_item_send, null);
			} else if (type == Constant.MESSAGE_TYPE_RECEIVE) {
				convertView = mInflater.inflate(R.layout.f_chat_item_receive, null);
			}
			chatHolder.time = (TextView) convertView.findViewById(R.id.time);
			chatHolder.character = (TextView) convertView.findViewById(R.id.character);
			chatHolder.image = (ImageView) convertView.findViewById(R.id.image);
			chatHolder.images_layout = convertView.findViewById(R.id.images_layout);
			chatHolder.images = (ImageView) convertView.findViewById(R.id.images);
			chatHolder.images_count = (TextView) convertView.findViewById(R.id.images_count);
			chatHolder.voice = (RelativeLayout) convertView.findViewById(R.id.voice);
			chatHolder.voicetime = (TextView) convertView.findViewById(R.id.voicetime);
			chatHolder.voice_icon = (ImageView) convertView.findViewById(R.id.voice_icon);
			chatHolder.head = (ImageView) convertView.findViewById(R.id.head);
			convertView.setTag(chatHolder);
			// } else {
			// chatHolder = (ChatHolder) convertView.getTag();
			// }
			String contentType = message.contentType;
			if ("text".equals(contentType)) {
				chatHolder.character.setVisibility(View.VISIBLE);
				chatHolder.image.setVisibility(View.GONE);
				chatHolder.voice.setVisibility(View.GONE);
				chatHolder.character.setText(message.content);
			} else if ("image".equals(contentType)) {
				chatHolder.character.setVisibility(View.GONE);
				chatHolder.image.setVisibility(View.VISIBLE);
				chatHolder.voice.setVisibility(View.GONE);
				List<String> images = thisController.getImageFromJson(message.content);
				String image = images.get(0);
				if (images.size() == 1) {
					chatHolder.images_layout.setVisibility(View.GONE);
					chatHolder.image.setVisibility(View.VISIBLE);
					chatHolder.image.setTag(R.id.tag_first, images);
					thisController.setImageThumbnail(image, chatHolder.image);
					chatHolder.image.setOnClickListener(thisController.mOnClickListener);
				} else {
					chatHolder.image.setVisibility(View.GONE);
					chatHolder.images_layout.setVisibility(View.VISIBLE);
					chatHolder.images_count.setText(String.valueOf(images.size()));
					chatHolder.images_layout.setTag(R.id.tag_first, images);
					thisController.setImageThumbnail(image, chatHolder.images);
					chatHolder.images_layout.setOnClickListener(thisController.mOnClickListener);
				}
				// for (int i = 0; i < images.size(); i++) {
				// String image = images.get(i);
				// ImageView child = new ImageView(thisActivity);
				// RelativeLayout.LayoutParams params = new
				// RelativeLayout.LayoutParams((int) (178 *
				// displayMetrics.density + 0.5f), (int) (106 *
				// displayMetrics.density + 0.5f));
				// if (i > 0) {
				// params.topMargin = i * (int) (116 * displayMetrics.density +
				// 0.5f);
				// }
				// chatHolder.image.addView(child, params);
				// child.setTag(R.id.tag_first, i);
				// child.setTag(R.id.tag_second, images);
				// child.setOnClickListener(thisController.mOnClickListener);
				//
				// }
			} else if ("voice".equals(contentType)) {
				chatHolder.character.setVisibility(View.GONE);
				chatHolder.image.setVisibility(View.GONE);
				chatHolder.voice.setVisibility(View.VISIBLE);
				Bitmap bitmap = BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.chat_item_voice);
				if (type == Constant.MESSAGE_TYPE_SEND) {
					Matrix mMatrix = new Matrix();
					mMatrix.setRotate(180);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
				}
				chatHolder.voice_icon.setImageBitmap(bitmap);
				chatHolder.voicetime.setText("");
			}
			chatHolder.time.setText(DateUtil.getChatMessageListTime(Long.valueOf(message.time)));
			String fileName = "";
			User user = data.userInformation.currentUser;
			if (message.phone.equals(user.phone)) {
				fileName = user.head;
			} else {
				Friend friend = data.relationship.friendsMap.get(message.phone);
				if (friend != null) {
					fileName = friend.head;
				}
			}
			fileHandlers.getHeadImage(fileName, chatHolder.head, headOptions);

			// chatHolder.head.setImageBitmap(bitmap);
			return convertView;
		}

		class ChatHolder {
			public View images_layout;
			public RelativeLayout voice;
			public TextView time, character, voicetime, images_count;
			public ImageView voice_icon, head, image, images;
		}

	}
}
