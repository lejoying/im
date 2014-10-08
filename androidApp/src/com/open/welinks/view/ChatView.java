package com.open.welinks.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.open.welinks.model.LBSHandlers;
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

		initSmallBusinessCardDialog();
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

		ArrayList<Message> messages;
		User user = data.userInformation.currentUser;

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
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = messages.get(position);
			ChatHolder chatHolder = null;
			int type = message.type;
			// if (convertView == null) {
			chatHolder = new ChatHolder();
			if (message.sendType.equals("point")) {
				if (message.phone.equals(user.phone)) {
					convertView = mInflater.inflate(R.layout.chat_item_send, null);
				} else if (type == Constant.MESSAGE_TYPE_RECEIVE) {
					convertView = mInflater.inflate(R.layout.chat_item_receive, null);
				}
			} else if (message.sendType.equals("group")) {
				if (message.phone.equals(user.phone)) {
					convertView = mInflater.inflate(R.layout.chat_item_send, null);
				} else if (type == Constant.MESSAGE_TYPE_RECEIVE) {
					convertView = mInflater.inflate(R.layout.chat_item_receive, null);
				}
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
			// } else {
			// chatHolder = (ChatHolder) convertView.getTag();
			// }
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
				if (type == Constant.MESSAGE_TYPE_SEND) {
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
				thisController.setImageThumbnail(messageContent.image, chatHolder.share_image, 50, 50);

				chatHolder.share.setTag(R.id.tag_second, messageContent.gid);
				chatHolder.share.setTag(R.id.tag_third, messageContent.gsid);
				chatHolder.share.setOnClickListener(thisController.mOnClickListener);

			}
			chatHolder.time.setText(DateUtil.getChatMessageListTime(Long.valueOf(message.time)));
			String fileName = "";
			String phone = "";
			User user = data.userInformation.currentUser;
			if (message.phone.equals(user.phone)) {
				fileName = user.head;
				phone = user.phone;
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
		userCardMainView = mInflater.inflate(R.layout.view_dialog_small_businesscard, null);
		optionTwoView = (LinearLayout) userCardMainView.findViewById(R.id.optionTwo);
		userNickNameView = (TextView) userCardMainView.findViewById(R.id.userNickName);
		userAgeView = (TextView) userCardMainView.findViewById(R.id.userAge);
		distanceView = (TextView) userCardMainView.findViewById(R.id.userDistance);
		lastLoginTimeView = (TextView) userCardMainView.findViewById(R.id.lastLoginTime);
		userBusinessContainer = (RelativeLayout) userCardMainView.findViewById(R.id.userBusinessView);
		int height = (int) (displayMetrics.heightPixels * 0.5f - 50 * displayMetrics.density) + getStatusBarHeight(thisActivity);
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

	LBSHandlers lbsHandlers = LBSHandlers.getInstance();

	public void setSmallBusinessCardContent(String phone, String head, String nickName, String age, String longitude, String latitude) {
		User user = data.userInformation.currentUser;
		goInfomationView.setTag(R.id.tag_first, phone);
		goChatView.setTag(R.id.tag_first, phone);
		singleButtonView.setTag(R.id.tag_first, phone);
		fileHandlers.getHeadImage(head, userHeadView, smallBusinessCardOptions);
		userNickNameView.setText(nickName);
		userAgeView.setText(age + "");
		distanceView.setText(lbsHandlers.pointDistance(user.longitude, user.latitude, longitude, latitude) + "km");
		lastLoginTimeView.setText("0小时前");
		if (user.phone.equals(phone)) {
			optionTwoView.setVisibility(View.GONE);
			singleButtonView.setVisibility(View.VISIBLE);
			cardStatusView.setText("自己");
			singleButtonView.setTag(R.id.tag_second, "point");
		} else {
			if (data.relationship.friends.contains(phone)) {
				optionTwoView.setVisibility(View.GONE);
				singleButtonView.setVisibility(View.VISIBLE);
				cardStatusView.setText("已是好友");
				singleButtonView.setTag(R.id.tag_second, "point");
				singleButtonView.setTag(R.id.tag_third, false);
			} else {
				optionTwoView.setVisibility(View.GONE);
				singleButtonView.setVisibility(View.VISIBLE);
				cardStatusView.setText("不是好友");
				singleButtonView.setTag(R.id.tag_second, "point");
				singleButtonView.setTag(R.id.tag_third, true);
				data.tempData.tempFriend = data.relationship.friendsMap.get(phone);
			}
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
}
