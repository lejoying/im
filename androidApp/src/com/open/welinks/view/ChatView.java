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
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.SubData.MessageShareContent;
import com.open.welinks.utils.DateUtil;

public class ChatView {
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ChatView";

	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;

	public ChatController thisController;
	public Activity thisActivity;
	public Context context;
	public ChatView thisView;

	public RelativeLayout backView;
	public TextView backNameView;
	public ImageView infomation;
	public ListView chatContentListView;
	public RelativeLayout chat_bottom_bar;
	public ImageView sendMessageView;
	public ImageView moreOptions;
	public EditText inputMessageContentView;
	public RelativeLayout chat_bottom_bar_selected;
	public RelativeLayout infomation_layout;
	public RelativeLayout selectedFaceview;
	public RelativeLayout selectPictureView;
	public RelativeLayout makeAudioView;
	public ImageView moreSelectedView;

	public ChatAdapter mChatAdapter;

	public int imageWidth;
	public int imageHeight;

	public View maxView;

	public DisplayImageOptions headOptions;
	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public ChatView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		context = thisActivity;
		thisView = this;
	}

	public SmallBusinessCardPopView businessCardPopView;

	public void initViews() {

		mInflater = thisActivity.getLayoutInflater();
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		imageWidth = (int) (178 * thisView.displayMetrics.density);
		imageHeight = (int) (106 * thisView.displayMetrics.density);

		thisActivity.setContentView(R.layout.activity_chat);

		maxView = thisActivity.findViewById(R.id.maxView);

		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		backNameView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		infomation_layout = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		// infomation = (ImageView) thisActivity.findViewById(R.id.infomation);
		infomation = new ImageView(context);
		infomation.setImageResource(R.drawable.share_to_group_icon);
		RelativeLayout.LayoutParams infomationParams = new RelativeLayout.LayoutParams((int) (40 * displayMetrics.density), (int) (40 * displayMetrics.density));
		infomationParams.addRule(RelativeLayout.CENTER_VERTICAL);
		infomation_layout.addView(infomation, infomationParams);

		chatContentListView = (ListView) thisActivity.findViewById(R.id.chat_content);
		chat_bottom_bar = (RelativeLayout) thisActivity.findViewById(R.id.chat_bottom_bar);
		sendMessageView = (ImageView) thisActivity.findViewById(R.id.send);
		moreOptions = (ImageView) thisActivity.findViewById(R.id.more);
		inputMessageContentView = (EditText) thisActivity.findViewById(R.id.input);
		chat_bottom_bar_selected = (RelativeLayout) thisActivity.findViewById(R.id.chat_bottom_bar_selected);
		selectedFaceview = (RelativeLayout) thisActivity.findViewById(R.id.selectedface);
		selectPictureView = (RelativeLayout) thisActivity.findViewById(R.id.selectpicture);
		makeAudioView = (RelativeLayout) thisActivity.findViewById(R.id.makeaudio);
		moreSelectedView = (ImageView) thisActivity.findViewById(R.id.more_selected);

		businessCardPopView = new SmallBusinessCardPopView(thisActivity, maxView);
	}

	public void showChatViews() {
		String type = thisController.type, key = thisController.key;
		ArrayList<Message> messages = null;
		parser.check();
		if ("group".equals(type)) {
			messages = data.messages.groupMessageMap.get("g" + key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				data.messages.groupMessageMap.put("g" + key, messages);
			}
			Group group = data.relationship.groupsMap.get(key);
			if (group != null) {
				group.notReadMessagesCount = 0;
				backNameView.setText(group.name + "(" + group.members.size() + ")");
			} else {
				backNameView.setText("Group");
			}
		} else if ("point".equals(type)) {
			messages = data.messages.friendMessageMap.get("p" + key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				data.messages.friendMessageMap.put("p" + key, messages);
			}
			Friend friend = data.relationship.friendsMap.get(key);
			if (friend != null) {
				friend.notReadMessagesCount = 0;
				fileHandlers.getHeadImage(friend.head, infomation, headOptions);
				if ("".equals(friend.alias)) {
					backNameView.setText(friend.nickName);
				} else {
					backNameView.setText(friend.alias);
				}
			} else {
				backNameView.setText("Name");
				fileHandlers.getHeadImage("", infomation, headOptions);
			}
		}
		data.relationship.isModified = true;
		mChatAdapter = new ChatAdapter(messages);
		chatContentListView.setAdapter(mChatAdapter);
		chatContentListView.setSelection(mChatAdapter.getCount());
	}

	public class ChatAdapter extends BaseAdapter {

		public ArrayList<Message> messages;
		public User currentUser = data.userInformation.currentUser;

		public int TYPECOUNT = 3;
		public int TYPE_SELF = 0x01;
		public int TYPE_OTHER = 0x02;

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			chatContentListView.setSelection(this.getCount());
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
		public int getItemViewType(int position) {
			Message message = messages.get(position);
			if (message.phone.equals(currentUser.phone)) {
				return TYPE_SELF;
			} else {
				return TYPE_OTHER;
			}
		}

		@Override
		public int getViewTypeCount() {
			return TYPECOUNT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = messages.get(position);
			ChatHolder chatHolder = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				chatHolder = new ChatHolder();
				if (type == TYPE_SELF) {
					convertView = mInflater.inflate(R.layout.chat_item_send, null);
				} else if (type == TYPE_OTHER) {
					convertView = mInflater.inflate(R.layout.chat_item_receive, null);
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
				chatHolder.share = convertView.findViewById(R.id.share);
				chatHolder.share_text = (TextView) convertView.findViewById(R.id.share_text);
				chatHolder.share_image = (ImageView) convertView.findViewById(R.id.share_image);

				convertView.setTag(chatHolder);
			} else {
				chatHolder = (ChatHolder) convertView.getTag();
			}

			String contentType = message.contentType;

			if ("text".equals(contentType)) {
				chatHolder.character.setVisibility(View.VISIBLE);
				chatHolder.image.setVisibility(View.GONE);
				chatHolder.voice.setVisibility(View.GONE);
				chatHolder.share.setVisibility(View.GONE);
				chatHolder.character.setText(message.content);
			} else if ("image".equals(contentType)) {
				chatHolder.character.setVisibility(View.GONE);
				chatHolder.image.setVisibility(View.VISIBLE);
				chatHolder.voice.setVisibility(View.GONE);
				chatHolder.share.setVisibility(View.GONE);
				List<String> images = thisController.getImageFromJson(message.content);
				String image = images.get(0);
				if (images.size() == 1) {
					chatHolder.images_layout.setVisibility(View.GONE);
					chatHolder.image.setVisibility(View.VISIBLE);
					chatHolder.image.setTag(R.id.tag_first, images);
					thisController.setImageThumbnail(image, chatHolder.image, 178, 106);
					chatHolder.image.setOnClickListener(thisController.mOnClickListener);
				} else {
					chatHolder.image.setVisibility(View.GONE);
					chatHolder.images_layout.setVisibility(View.VISIBLE);
					chatHolder.images_count.setText(String.valueOf(images.size()));
					chatHolder.images_layout.setTag(R.id.tag_first, images);
					thisController.setImageThumbnail(image, chatHolder.images, 178, 106);
					chatHolder.images_layout.setOnClickListener(thisController.mOnClickListener);
				}
			} else if ("voice".equals(contentType)) {
				chatHolder.character.setVisibility(View.GONE);
				chatHolder.image.setVisibility(View.GONE);
				chatHolder.share.setVisibility(View.GONE);
				chatHolder.voice.setVisibility(View.VISIBLE);
				Bitmap bitmap = BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.chat_item_voice);
				if (type == TYPE_SELF) {
					Matrix mMatrix = new Matrix();
					mMatrix.setRotate(180);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
				}
				chatHolder.voice_icon.setImageBitmap(bitmap);
				chatHolder.voicetime.setText("");
			} else if ("share".equals(contentType)) {
				chatHolder.character.setVisibility(View.GONE);
				chatHolder.image.setVisibility(View.GONE);
				chatHolder.share.setVisibility(View.VISIBLE);
				chatHolder.voice.setVisibility(View.GONE);

				MessageShareContent messageContent = thisController.gson.fromJson(message.content, MessageShareContent.class);
				chatHolder.share_text.setText(messageContent.text);
				if (messageContent.image != null && !"".equals(messageContent.image)) {
					thisController.setImageThumbnail(messageContent.image, chatHolder.share_image, 50, 50);
				} else {
					thisController.imageLoader.displayImage("drawable://" + R.drawable.icon, chatHolder.share_image, thisController.options);
				}
				chatHolder.share.setTag(R.id.tag_second, messageContent.gid);
				chatHolder.share.setTag(R.id.tag_third, messageContent.gsid);
				chatHolder.share.setOnClickListener(thisController.mOnClickListener);
			}
			chatHolder.time.setText(DateUtil.getChatMessageListTime(Long.valueOf(message.time)));
			String fileName = "";
			String phone = "";
			if (message.phone.equals(currentUser.phone)) {
				fileName = currentUser.head;
				phone = currentUser.phone;
			} else {
				Friend friend = data.relationship.friendsMap.get(message.phone);
				if (friend != null) {
					fileName = friend.head;
					phone = friend.phone;
				}
			}
			fileHandlers.getHeadImage(fileName, chatHolder.head, headOptions);

			chatHolder.head.setTag(R.id.tag_class, "head_click");
			chatHolder.head.setTag(R.id.tag_first, phone);
			chatHolder.head.setOnClickListener(thisController.mOnClickListener);

			return convertView;
		}

		class ChatHolder {
			public View images_layout, share;
			public RelativeLayout voice;
			public TextView time, character, voicetime, images_count, share_text, share_title;
			public ImageView voice_icon, head, image, images, share_image;
		}
	}
}