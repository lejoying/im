package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;

import com.open.welinks.R;
import com.open.welinks.controller.ChatController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MCImageUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	public RelativeLayout selectedface;
	public RelativeLayout selectpicture;
	public RelativeLayout makeaudio;
	public ImageView more_selected;

	public ChatAdapter mChatAdapter;

	Bitmap bitmap;

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

		backview = (RelativeLayout) thisActivity.findViewById(R.id.backview);
		name = (TextView) thisActivity.findViewById(R.id.name);
		infomation = (ImageView) thisActivity.findViewById(R.id.infomation);
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

		bitmap = BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
	}

	public void showChatViews() {
		String type = thisController.type, key = thisController.key;
		ArrayList<Message> messages = null;
		if ("group".equals(type)) {
			messages = data.messages.groupMessageMap.get(key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				data.messages.groupMessageMap.put(key, messages);
			}
		} else if ("point".equals(type)) {
			messages = data.messages.friendMessageMap.get(key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				data.messages.friendMessageMap.put(key, messages);
			}
			Friend friend = data.relationship.friendsMap.get(key);
			if (friend == null || friend.head.equals("")) {
				infomation.setImageBitmap(bitmap);
			} else {

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
			if (type == Message.MESSAGE_TYPE_SEND) {
				convertView = mInflater.inflate(R.layout.f_chat_item_send, null);
			} else if (type == Message.MESSAGE_TYPE_RECEIVE) {
				convertView = mInflater.inflate(R.layout.f_chat_item_receive, null);
			}
			chatHolder.time = (TextView) convertView.findViewById(R.id.time);
			chatHolder.character = (TextView) convertView.findViewById(R.id.character);
			chatHolder.image = (RelativeLayout) convertView.findViewById(R.id.image);
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
				chatHolder.image.removeAllViews();
				List<String> images = thisController.getImageFromJson(message.content);
				for (int i = 0; i < images.size(); i++) {
					String image = images.get(i);
					ImageView child = new ImageView(thisActivity);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (178 * displayMetrics.density + 0.5f), (int) (106 * displayMetrics.density + 0.5f));
					if (i > 0) {
						params.topMargin = i * (int) (116 * displayMetrics.density + 0.5f);
					}
					chatHolder.image.addView(child, params);
					child.setTag(R.id.tag_first, i);
					child.setTag(R.id.tag_second, images);
					child.setOnClickListener(thisController.mOnClickListener);
					thisController.setImageThumbnail(image, child);
				}
			} else if ("voice".equals(contentType)) {
				chatHolder.character.setVisibility(View.GONE);
				chatHolder.image.setVisibility(View.GONE);
				chatHolder.voice.setVisibility(View.VISIBLE);
				Bitmap bitmap = BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.chat_item_voice);
				if (type == Message.MESSAGE_TYPE_SEND) {
					Matrix mMatrix = new Matrix();
					mMatrix.setRotate(180);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
				}
				chatHolder.voice_icon.setImageBitmap(bitmap);
				chatHolder.voicetime.setText("");
			}
			chatHolder.time.setText(DateUtil.getChatMessageListTime(Long.valueOf(message.time)));
			chatHolder.head.setImageBitmap(bitmap);
			return convertView;
		}

		class ChatHolder {
			public RelativeLayout voice, image;
			public TextView time, character, voicetime;
			public ImageView voice_icon, head;
		}

	}
}
