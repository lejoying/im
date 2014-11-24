package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.WebViewActivity;
import com.open.welinks.controller.ChatController;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Event.EventMessage;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandlers;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.SubData.MessageShareContent;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MyGson;

public class ChatView {
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ChatView";
	public MyLog log = new MyLog(tag, true);

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

		imageWidth = (int) (178 * thisView.displayMetrics.density + 0.5f);
		imageHeight = (int) (106 * thisView.displayMetrics.density + 0.5f);

		thisActivity.setContentView(R.layout.activity_chat);

		maxView = thisActivity.findViewById(R.id.maxView);

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
				fileHandlers.getHeadImage(friend.head, infomation, viewManage.headOptions40);
				if ("".equals(friend.alias)) {
					backNameView.setText(friend.nickName);
				} else {
					backNameView.setText(friend.alias);
				}
			} else {
				backNameView.setText("Name");
				fileHandlers.getHeadImage("", infomation, viewManage.headOptions40);
			}
		}
		data.relationship.isModified = true;
		mChatAdapter = new ChatAdapter(messages);
		chatContentListView.setAdapter(mChatAdapter);
		chatContentListView.setSelection(mChatAdapter.getCount());
	}

	ViewManage viewManage = ViewManage.getInstance();

	public class ChatAdapter extends BaseAdapter {

		public ArrayList<Message> messages;
		public User currentUser = data.userInformation.currentUser;

		public int TYPECOUNT = 4;
		public int TYPE_SELF = 0x01;
		public int TYPE_OTHER = 0x02;
		public int TYPE_EVENT = 0x03;

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
			if ("event".equals(message.sendType)) {
				return TYPE_EVENT;
			} else {
				if (message.phone.equals(currentUser.phone)) {
					return TYPE_SELF;
				} else {
					return TYPE_OTHER;
				}
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
				} else if (type == TYPE_EVENT) {
					convertView = mInflater.inflate(R.layout.chat_item_event, null);
				}

				if (type == TYPE_EVENT) {
					chatHolder.character = (TextView) convertView.findViewById(R.id.character);
				} else {
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
					if (type == TYPE_SELF) {
						chatHolder.message_status = (ImageView) convertView.findViewById(R.id.message_status);
					}
				}

				convertView.setTag(chatHolder);
			} else {
				chatHolder = (ChatHolder) convertView.getTag();
			}
			if (type == TYPE_EVENT) {
				MyGson gson = new MyGson();
				EventMessage event = gson.fromJson(message.content, EventMessage.class);
				String content = DataHandlers.switchChatMessageEvent(event);
				chatHolder.character.setText(content);
			} else {
				String contentType = message.contentType;
				if (type == TYPE_SELF) {
					chatHolder.message_status.setVisibility(View.VISIBLE);
					if ("sending".equals(message.status)) {
						long currentLong = System.currentTimeMillis();
						long cha = (currentLong - Long.valueOf(message.time)) / 1000;
						if (cha > 60) {
							message.status = "failed";
							chatHolder.message_status.setImageResource(R.drawable.message_resend);
							chatHolder.message_status.setTag(R.id.tag_class, "resend_message");
							chatHolder.message_status.setTag(R.id.tag_first, position);
							chatHolder.message_status.setOnClickListener(thisController.mOnClickListener);
						} else {
							chatHolder.message_status.setImageResource(R.drawable.message_send);
						}
					} else if ("sent".equals(message.status)) {
						chatHolder.message_status.setVisibility(View.GONE);
					} else if ("failed".equals(message.status)) {
						chatHolder.message_status.setImageResource(R.drawable.message_resend);
						chatHolder.message_status.setTag(R.id.tag_class, "resend_message");
						chatHolder.message_status.setTag(R.id.tag_first, position);
						chatHolder.message_status.setOnClickListener(thisController.mOnClickListener);
					} else {
						chatHolder.message_status.setImageResource(R.drawable.message_failed);
						chatHolder.message_status.setVisibility(View.GONE);
					}
				}
				// TODO
				// log.e(contentType);
				if ("text".equals(contentType)) {
					chatHolder.character.setVisibility(View.VISIBLE);
					chatHolder.image.setVisibility(View.GONE);
					chatHolder.voice.setVisibility(View.GONE);
					chatHolder.share.setVisibility(View.GONE);
					chatHolder.images_layout.setVisibility(View.GONE);
					chatHolder.character.setText(message.content);
					chatHolder.character.setAutoLinkMask(Linkify.WEB_URLS);
					chatHolder.character.setMovementMethod(LinkMovementMethod.getInstance());
					URLSpan[] urls = chatHolder.character.getUrls();
					String contentString = message.content;
					SpannableStringBuilder style = new SpannableStringBuilder(message.content);
					Map<String, Integer> positionMap = new HashMap<String, Integer>();
					if (urls.length > 0) {
						Log.e(tag, "Url length:" + urls.length);
						for (int i = 0; i < urls.length; i++) {
							String str = urls[i].getURL();
							Log.e(tag, "Url content:" + str);
							int start = 0;
							int end = 0;
							if (positionMap.get(str) == null) {
								start = contentString.indexOf(str);
								end = start + str.length();
							} else {
								start = positionMap.get(str);
								start = contentString.indexOf(str, start);
								end = start + str.length();
							}
							MyURLSpan myURLSpan = new MyURLSpan(str);
							if (start == -1 || end > contentString.length()) {
								continue;
							} else {
								positionMap.put(str, end);
								style.setSpan(myURLSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
								// style.setSpan(new ForegroundColorSpan(Color.MAGENTA), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
						}
					}
					chatHolder.character.setText(style);
				} else if ("image".equals(contentType)) {
					chatHolder.character.setVisibility(View.GONE);
					chatHolder.image.setVisibility(View.VISIBLE);
					chatHolder.voice.setVisibility(View.GONE);
					chatHolder.share.setVisibility(View.GONE);
					List<String> images = null;
					try {
						images = thisController.getImageFromJson(message.content);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (images == null) {
						chatHolder.character.setVisibility(View.VISIBLE);
						chatHolder.character.setText("数据结构错误");
						chatHolder.images_layout.setVisibility(View.GONE);
						chatHolder.image.setVisibility(View.GONE);
						return convertView;
					}
					String image = images.get(0);
					// log.e(image);
					if (images.size() == 1) {
						chatHolder.images_layout.setVisibility(View.GONE);
						chatHolder.image.setVisibility(View.VISIBLE);
						chatHolder.image.setTag(R.id.tag_first, images);
						// chatHolder.image.setImageResource(R.drawable.login_background_1);
						// thisController.setImageThumbnail(image, chatHolder.image, 178, 106);
						RelativeLayout.LayoutParams params = (LayoutParams) chatHolder.image.getLayoutParams();
						params.height = imageHeight;
						params.width = imageWidth;
						fileHandlers.getThumbleImage(image, chatHolder.image, (int) imageWidth / 2, (int) imageHeight / 2, thisController.options, fileHandlers.THUMBLE_TYEP_CHAT, null);
						chatHolder.image.setOnClickListener(thisController.mOnClickListener);
					} else {
						chatHolder.image.setVisibility(View.GONE);
						chatHolder.images_layout.setVisibility(View.VISIBLE);
						chatHolder.images_count.setText(String.valueOf(images.size()));
						chatHolder.images_layout.setTag(R.id.tag_first, images);
						// thisController.setImageThumbnail(image, chatHolder.images, 178, 106);
						FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) chatHolder.images.getLayoutParams();
						params.height = (int) (imageHeight + 40 * displayMetrics.density + 0.5f);
						params.width = imageWidth;
						fileHandlers.getThumbleImage(image, chatHolder.images, (int) (178 * displayMetrics.density + 0.5f) / 2, (int) (params.height) / 2, thisController.options, fileHandlers.THUMBLE_TYEP_CHAT, null);
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
					if (chatHolder.message_status != null) {
						chatHolder.message_status.setVisibility(View.GONE);
					}
					chatHolder.character.setVisibility(View.GONE);
					chatHolder.image.setVisibility(View.GONE);
					chatHolder.share.setVisibility(View.VISIBLE);
					chatHolder.voice.setVisibility(View.GONE);

					MessageShareContent messageContent = thisController.gson.fromJson(message.content, MessageShareContent.class);
					chatHolder.share_text.setText(messageContent.text);
					if (messageContent.image != null && !"".equals(messageContent.image)) {
						fileHandlers.getThumbleImage(messageContent.image, chatHolder.share_image, (int) (50 * displayMetrics.density + 0.5f) / 2, (int) (50 * thisView.displayMetrics.density + 0.5f) / 2, thisController.options, fileHandlers.THUMBLE_TYEP_CHAT, null);
						// thisController.setImageThumbnail(messageContent.image, chatHolder.share_image, 50, 50);
					} else {
						thisController.imageLoader.displayImage("drawable://" + R.drawable.icon, chatHolder.share_image, thisController.options);
					}
					chatHolder.share.setTag(R.id.tag_second, messageContent.gid);
					chatHolder.share.setTag(R.id.tag_third, messageContent.gsid);
					chatHolder.share.setOnClickListener(thisController.mOnClickListener);
				}
				if (position != 0) {
					Message beforeMessage = messages.get(position - 1);
					String beforeTime = "";
					if ("event".equals(beforeMessage.sendType)) {
						beforeTime = thisController.gson.fromJson(beforeMessage.content, EventMessage.class).time;
					} else {
						beforeTime = beforeMessage.time;
					}
					String time = "";
					if ("event".equals(message.sendType)) {
						time = thisController.gson.fromJson(beforeMessage.content, EventMessage.class).time;
					} else {
						time = message.time;
					}
					if (beforeTime != null && time != null) {
						String beroreTime = DateUtil.getChatMessageListTime(Long.valueOf(beforeTime));
						String currentTime = DateUtil.getChatMessageListTime(Long.valueOf(time));
						if (!beroreTime.equals(currentTime)) {
							chatHolder.time.setText(currentTime);
						} else {
							chatHolder.time.setText("");
						}
					}
				} else {
					String time = "";
					if ("event".equals(message.sendType)) {
						time = thisController.gson.fromJson(message.content, EventMessage.class).time;
					} else {
						time = message.time;
					}
					chatHolder.time.setText(DateUtil.getChatMessageListTime(Long.valueOf(time)));
				}
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
				fileHandlers.getHeadImage(fileName, chatHolder.head, viewManage.headOptions40);

				chatHolder.head.setTag(R.id.tag_class, "head_click");
				chatHolder.head.setTag(R.id.tag_first, phone);
				chatHolder.head.setOnClickListener(thisController.mOnClickListener);
			}

			return convertView;
		}

		class ChatHolder {
			public View images_layout, share;
			public RelativeLayout voice;
			public TextView time, character, voicetime, images_count, share_text, share_title;
			public ImageView voice_icon, head, image, images, share_image, message_status;
		}
	}

	private class MyURLSpan extends ClickableSpan {

		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			Intent intent = new Intent(thisActivity, WebViewActivity.class);
			intent.putExtra("url", mUrl);
			thisActivity.startActivity(intent);
		}
	}
}