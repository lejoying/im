package com.open.welinks.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.NewChatActivity;
import com.open.welinks.R;
import com.open.welinks.WebViewActivity;
import com.open.welinks.controller.NewChatController;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.DataHandlers;
import com.open.welinks.model.Data.Event.EventMessage;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.SubData.MessageShareContent;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MyGson;
import com.open.welinks.view.ChatView.ChatAdapter;
import com.open.welinks.view.ChatView.ChatAdapter.ChatHolder;

public class NewChatView {

	public NewChatView thisView;
	public NewChatController thisController;
	public NewChatActivity thisActivity;

	public View currentVoiceView, backView, chatMenuLayout, textLayout, voiceLayout, chatAddLayout, takePhoto, ablum, location, voicePop;
	public RelativeLayout rightContainer;
	public TextView titleText, chatSend, voicePopTime, voicePopPrompt;
	public ListView chatContent;
	public ImageView chatAdd, chatSmily, chatRecord, titleImage, chatMenuBackground, voicePopImage;
	public EditText chatInput;
	public GridView chatMenu;
	public ChatFaceView faceLayout;

	public SmallBusinessCardPopView businessCardPopView;

	public ChatAdapter mChatAdapter;
	public ChatMenuAdapter mChatMenuAdapter;

	private Animation inTranslateAnimation, inAlphaAnimation, outTranslateAnimation;

	private DisplayImageOptions headOptions;

	public NewChatView(NewChatActivity activity) {
		thisView = this;
		thisActivity = activity;
	}

	@SuppressLint("HandlerLeak")
	public void initViews() {
		thisActivity.setContentView(R.layout.activity_new_chat);
		backView = thisActivity.findViewById(R.id.backView);
		chatMenuLayout = thisActivity.findViewById(R.id.chatMenuLayout);
		textLayout = thisActivity.findViewById(R.id.textLayout);
		voiceLayout = thisActivity.findViewById(R.id.voiceLayout);
		chatAddLayout = thisActivity.findViewById(R.id.chatSmilyLayout);
		takePhoto = thisActivity.findViewById(R.id.takePhoto);
		ablum = thisActivity.findViewById(R.id.ablum);
		location = thisActivity.findViewById(R.id.location);
		voicePop = thisActivity.findViewById(R.id.voicePop);
		rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		titleText = (TextView) thisActivity.findViewById(R.id.titleText);
		chatSend = (TextView) thisActivity.findViewById(R.id.chatSend);
		voicePopTime = (TextView) thisActivity.findViewById(R.id.voicePopTime);
		voicePopPrompt = (TextView) thisActivity.findViewById(R.id.voicePopPrompt);
		chatContent = (ListView) thisActivity.findViewById(R.id.chatContent);
		chatAdd = (ImageView) thisActivity.findViewById(R.id.chatAdd);
		chatSmily = (ImageView) thisActivity.findViewById(R.id.chatSmily);
		chatRecord = (ImageView) thisActivity.findViewById(R.id.chatRecord);
		chatMenuBackground = (ImageView) thisActivity.findViewById(R.id.chatMenuBackground);
		voicePopImage = (ImageView) thisActivity.findViewById(R.id.voicePopImage);
		chatInput = (EditText) thisActivity.findViewById(R.id.chatInput);
		chatMenu = (GridView) thisActivity.findViewById(R.id.chatMenu);
		faceLayout = (ChatFaceView) thisActivity.findViewById(R.id.faceLayout);

		businessCardPopView = new SmallBusinessCardPopView(thisActivity, thisActivity.findViewById(R.id.chatMainView));

		chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_record));
		chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));

		titleImage = new ImageView(thisActivity);
		titleImage.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_arrow_down));
		rightContainer.addView(titleImage);

		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		mChatMenuAdapter = new ChatMenuAdapter();
		chatMenu.setAdapter(mChatMenuAdapter);

		inTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_in);
		outTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_out);
		inAlphaAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_in_alpha);

	}

	public void fillData() {
		String type = thisController.type, key = thisController.key;
		ArrayList<Message> messages = null;
		thisController.parser.check();
		if ("group".equals(type)) {
			messages = thisController.data.messages.groupMessageMap.get("g" + key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				thisController.data.messages.groupMessageMap.put("g" + key, messages);
			}
			Group group = thisController.data.relationship.groupsMap.get(key);
			if (group != null) {
				group.notReadMessagesCount = 0;
				titleText.setText(group.name + "(" + group.members.size() + ")");
			} else {
				titleText.setText("Group");
			}
		} else if ("point".equals(type)) {
			messages = thisController.data.messages.friendMessageMap.get("p" + key);
			if (messages == null) {
				messages = new ArrayList<Data.Messages.Message>();
				thisController.data.messages.friendMessageMap.put("p" + key, messages);
			}
			Friend friend = thisController.data.relationship.friendsMap.get(key);
			if (friend != null) {
				friend.notReadMessagesCount = 0;
				if ("".equals(friend.alias)) {
					titleText.setText(friend.nickName);
				} else {
					titleText.setText(friend.alias);
				}
			} else {
				titleText.setText("Name");
			}
		}
		thisController.data.relationship.isModified = true;
		mChatAdapter = new ChatAdapter(messages);
		chatContent.setAdapter(mChatAdapter);
		chatContent.setSelection(mChatAdapter.getCount());
	}

	public class ChatAdapter extends BaseAdapter {
		public ArrayList<Message> messages;
		public User currentUser = thisController.data.userInformation.currentUser;
		public int TYPECOUNT = 8;
		public int TYPE_SELF = 0x01;
		public int TYPE_SELF_FIRST = 0x02;
		public int TYPE_OTHER_MALE = 0x03;
		public int TYPE_OTHER_FEMALE = 0x04;
		public int TYPE_OTHER_MALE_FIRST = 0x05;
		public int TYPE_OTHER_FEMALE_FIRST = 0x06;
		public int TYPE_EVENT = 0x07;

		public ChatAdapter(ArrayList<Message> messages) {
			this.messages = messages;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			chatContent.setSelection(messages.size() - 1);
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
			Message message2 = null;
			Friend friend;
			if (position != 0) {
				message2 = messages.get(position - 1);
			}
			if ("event".equals(message.sendType)) {
				return TYPE_EVENT;
			} else {
				if (message.phone.equals(currentUser.phone)) {
					if (message2 != null && message2.phone != null && message2.phone.equals(message.phone)) {
						return TYPE_SELF;
					} else {
						return TYPE_SELF_FIRST;
					}
				} else {
					friend = thisController.data.relationship.friendsMap.get(message.phone);
					if (friend.sex.equals("male") || friend.sex.equals("男")) {
						if (message2 != null && message2.phone != null && message2.phone.equals(message.phone)) {
							return TYPE_OTHER_MALE;
						} else {
							return TYPE_OTHER_MALE_FIRST;
						}
					} else {
						if (message2 != null && message2.phone != null && message2.phone.equals(message.phone)) {
							return TYPE_OTHER_FEMALE;
						} else {
							return TYPE_OTHER_FEMALE_FIRST;
						}
					}
				}
			}
		}

		@Override
		public int getViewTypeCount() {
			return TYPECOUNT;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChatHolder holder = null;
			Friend friend = null;
			int type = getItemViewType(position);
			Message message = messages.get(position);
			if (message != null) {
				friend = thisController.data.relationship.friendsMap.get(message.phone);
			}
			int backgroundDrawableId = 0;
			String messageHead = "";
			if (convertView == null) {
				holder = new ChatHolder();
				if (type == TYPE_SELF) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_send, null);
					backgroundDrawableId = R.drawable.myself_chat_order_bg;
					messageHead = "";
				} else if (type == TYPE_SELF_FIRST) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_send, null);
					backgroundDrawableId = R.drawable.myself_chat_bg;
					messageHead = currentUser.head;
				} else if (type == TYPE_OTHER_MALE) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
					backgroundDrawableId = R.drawable.male_chat_from_order_bg;
					messageHead = "";
				} else if (type == TYPE_OTHER_MALE_FIRST) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
					backgroundDrawableId = R.drawable.male_chat_from_bg;
					messageHead = friend.head;
				} else if (type == TYPE_OTHER_FEMALE) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
					backgroundDrawableId = R.drawable.female_chat_from_order_bg;
					messageHead = "";
				} else if (type == TYPE_OTHER_FEMALE_FIRST) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
					backgroundDrawableId = R.drawable.female_chat_from_bg;
					messageHead = friend.head;
				} else if (type == TYPE_EVENT) {
					convertView = thisActivity.mInflater.inflate(R.layout.chat_item_event, null);
				}

				if (type == TYPE_EVENT) {
					holder.character = (TextView) convertView.findViewById(R.id.character);
				} else {
					holder.chatLayout = convertView.findViewById(R.id.chatLayout);
					holder.voice = convertView.findViewById(R.id.voice);
					holder.imagesLayout = convertView.findViewById(R.id.imagesLayout);
					holder.share = convertView.findViewById(R.id.share);
					holder.image = (ImageView) convertView.findViewById(R.id.image);
					holder.images = (ImageView) convertView.findViewById(R.id.images);
					holder.head = (ImageView) convertView.findViewById(R.id.head);
					holder.status = (ImageView) convertView.findViewById(R.id.status);
					holder.shareImage = (ImageView) convertView.findViewById(R.id.shareImage);
					holder.time = (TextView) convertView.findViewById(R.id.time);
					holder.character = (TextView) convertView.findViewById(R.id.character);
					holder.voicetime = (TextView) convertView.findViewById(R.id.voicetime);
					holder.imagesCount = (TextView) convertView.findViewById(R.id.imagesCount);
					holder.shareTitle = (TextView) convertView.findViewById(R.id.shareTitle);
					holder.shareText = (TextView) convertView.findViewById(R.id.shareText);
					holder.gif = (GifImageView) convertView.findViewById(R.id.gif);
					holder.voiceGif = (GifImageView) convertView.findViewById(R.id.voiceGif);
				}
				convertView.setTag(holder);
			} else {
				holder = (ChatHolder) convertView.getTag();
			}

			if (type == TYPE_EVENT) {
				MyGson gson = new MyGson();
				EventMessage event = gson.fromJson(message.content, EventMessage.class);
				String content = DataHandlers.switchChatMessageEvent(event);
				holder.character.setText(content);
			} else {
				if (!"".equals(messageHead)) {
					thisController.fileHandlers.getHeadImage(messageHead, holder.head, headOptions);
					holder.head.setTag(R.id.tag_first, "head");
					holder.head.setTag(R.id.tag_second, message.phone);
					holder.head.setOnClickListener(thisController.mOnClickListener);
				} else {
					holder.head.setVisibility(View.GONE);
				}
				holder.chatLayout.setBackgroundResource(backgroundDrawableId);
				String contentType = message.contentType;
				if (type == TYPE_SELF) {
					holder.status.setVisibility(View.VISIBLE);
					if ("sending".equals(message.status)) {
						long currentLong = System.currentTimeMillis();
						long cha = (currentLong - Long.valueOf(message.time)) / 1000;
						if (cha > 60) {
							message.status = "failed";
							holder.status.setImageResource(R.drawable.message_resend);
							holder.status.setTag(R.id.tag_class, "resend_message");
							holder.status.setTag(R.id.tag_first, position);
							holder.status.setOnClickListener(thisController.mOnClickListener);
						} else {
							holder.status.setImageResource(R.drawable.message_send);
						}
					} else if ("sent".equals(message.status)) {
						holder.status.setVisibility(View.GONE);
					} else if ("failed".equals(message.status)) {
						holder.status.setImageResource(R.drawable.message_resend);
						holder.status.setTag(R.id.tag_class, "resend_message");
						holder.status.setTag(R.id.tag_first, position);
						holder.status.setOnClickListener(thisController.mOnClickListener);
					} else {
						holder.status.setImageResource(R.drawable.message_failed);
						holder.status.setVisibility(View.GONE);
					}
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
							holder.time.setText(currentTime);
						} else {
							holder.time.setText("");
						}
					}
				} else {
					String time = "";
					if ("event".equals(message.sendType)) {
						time = thisController.gson.fromJson(message.content, EventMessage.class).time;
					} else {
						time = message.time;
					}
					holder.time.setText(DateUtil.getChatMessageListTime(Long.valueOf(time)));
				}

				if (contentType.equals("text")) {
					holder.character.setVisibility(View.VISIBLE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.character.setText(message.content);
					holder.character.setAutoLinkMask(Linkify.WEB_URLS);
					holder.character.setMovementMethod(LinkMovementMethod.getInstance());
					URLSpan[] urls = holder.character.getUrls();
					String contentString = message.content;
					SpannableStringBuilder style = new SpannableStringBuilder(message.content);
					Map<String, Integer> positionMap = new HashMap<String, Integer>();
					if (urls.length > 0) {
						for (int i = 0; i < urls.length; i++) {
							String str = urls[i].getURL();
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
							}
						}
					}
					holder.character.setText(style);
				} else if (contentType.equals("voice")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.VISIBLE);
					holder.image.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);

					String[] infomation = message.content.split("@");
					String time = infomation[1], fileName = infomation[0];
					holder.voicetime.setText(time + "s");
					GifDrawable gifDrawable = null;
					try {
						if (type == TYPE_SELF || type == TYPE_SELF_FIRST) {
							gifDrawable = new GifDrawable(thisActivity.getResources(), R.drawable.chat_send_voice);
						} else {
							gifDrawable = new GifDrawable(thisActivity.getResources(), R.drawable.chat_receive_voice);
						}
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (gifDrawable != null) {
						holder.voiceGif.setImageDrawable(gifDrawable);
						gifDrawable.stop();
					}
					thisController.audiohandlers.prepareVoice(fileName);
					holder.voice.setTag(R.id.tag_first, contentType);
					holder.voice.setTag(R.id.tag_second, fileName);
					holder.voice.setOnClickListener(thisController.mOnClickListener);
				} else if (contentType.equals("image")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);

					List<String> images = null;
					try {
						images = thisController.gson.fromJson(message.content, ArrayList.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (images == null) {
						holder.character.setVisibility(View.VISIBLE);
						holder.character.setText("数据结构错误");
						holder.image.setVisibility(View.GONE);
					}
					String image = images.get(0);
					if (images.size() == 1) {
						holder.imagesLayout.setVisibility(View.GONE);
						holder.image.setVisibility(View.VISIBLE);
						holder.image.setTag(R.id.tag_first, contentType);
						holder.image.setTag(R.id.tag_second, images);
						holder.image.setOnClickListener(thisController.mOnClickListener);
						thisController.fileHandlers.getThumbleImage(image, holder.image, (int) BaseDataUtils.dpToPx(178), (int) BaseDataUtils.dpToPx(106), thisController.viewManage.options, thisController.fileHandlers.THUMBLE_TYEP_CHAT, null);
					} else {
						holder.image.setVisibility(View.GONE);
						holder.imagesLayout.setVisibility(View.VISIBLE);
						holder.imagesCount.setText(String.valueOf(images.size()));
						holder.imagesLayout.setTag(R.id.tag_first, contentType);
						holder.imagesLayout.setTag(R.id.tag_second, images);
						holder.imagesLayout.setOnClickListener(thisController.mOnClickListener);
						thisController.fileHandlers.getThumbleImage(image, holder.images, (int) BaseDataUtils.dpToPx(178), (int) BaseDataUtils.dpToPx(106), thisController.viewManage.options, thisController.fileHandlers.THUMBLE_TYEP_CHAT, null);
					}
				} else if (contentType.equals("gif")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.gif.setVisibility(View.VISIBLE);
					thisController.fileHandlers.getGifImage(message.content, holder.gif);
				} else if (contentType.equals("share")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.share.setVisibility(View.VISIBLE);

					MessageShareContent messageContent = thisController.gson.fromJson(message.content, MessageShareContent.class);
					holder.shareText.setText(messageContent.text);
					if (messageContent.image != null && !"".equals(messageContent.image)) {
						thisController.fileHandlers.getThumbleImage(messageContent.image, holder.shareImage, (int) BaseDataUtils.dpToPx(50), (int) BaseDataUtils.dpToPx(50), thisController.viewManage.options, thisController.fileHandlers.THUMBLE_TYEP_CHAT, null);
					} else {
						thisController.imageLoader.displayImage("drawable://" + R.drawable.icon, holder.shareImage, thisController.viewManage.options);
					}
					holder.share.setTag(R.id.tag_first, contentType);
					holder.share.setTag(R.id.tag_second, messageContent.gid);
					holder.share.setTag(R.id.tag_third, messageContent.gsid);
					holder.share.setTag(R.id.tag_four, messageContent.sid);
					holder.share.setOnClickListener(thisController.mOnClickListener);
				}
			}
			return convertView;
		}

		class ChatHolder {
			View voice, share, chatLayout, imagesLayout;
			ImageView voiceIcon, image, head, status, images, shareImage;
			TextView time, character, voicetime, imagesCount, shareTitle, shareText;
			GifImageView gif, voiceGif;
		}

	}

	@SuppressLint("ViewHolder")
	public class ChatMenuAdapter extends BaseAdapter {
		public List<String> menuString;
		public List<Integer> menuImage;

		public ChatMenuAdapter() {
			menuString = new ArrayList<String>();
			menuImage = new ArrayList<Integer>();
			if (thisController.type.equals("group")) {
				menuString.add(thisActivity.getString(R.string.groupDetails));
				menuString.add(thisActivity.getString(R.string.groupMembers));
				menuString.add(thisActivity.getString(R.string.groupAlbum));
				menuString.add(thisActivity.getString(R.string.closeNotice));
				menuString.add(thisActivity.getString(R.string.share));
				menuString.add(thisActivity.getString(R.string.sendCard));
				menuString.add(thisActivity.getString(R.string.tureOff));
				menuString.add(thisActivity.getString(R.string.setting));
				menuImage.add(R.drawable.chat_menu_item_details);
				menuImage.add(R.drawable.chat_menu_item_members);
				menuImage.add(R.drawable.chat_menu_item_albums);
				menuImage.add(R.drawable.chat_menu_item_tips_on);
				menuImage.add(R.drawable.chat_menu_item_share);
				menuImage.add(R.drawable.chat_menu_item_send);
				menuImage.add(R.drawable.chat_menu_item_light);
				menuImage.add(R.drawable.chat_menu_item_settings);
			} else {
				menuString.add(thisActivity.getString(R.string.personalDetails));
				menuString.add(thisActivity.getString(R.string.personalAlbum));
				menuString.add(thisActivity.getString(R.string.closeNotice));
				menuString.add(thisActivity.getString(R.string.sendCard));
				menuImage.add(R.drawable.chat_menu_item_details);
				menuImage.add(R.drawable.chat_menu_item_albums);
				menuImage.add(R.drawable.chat_menu_item_tips_on);
				menuImage.add(R.drawable.chat_menu_item_send);
			}
		}

		@Override
		public int getCount() {
			return menuString.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = thisActivity.mInflater.inflate(R.layout.chat_menu_item, null);
			ImageView image = (ImageView) convertView.findViewById(R.id.item_image);
			TextView text = (TextView) convertView.findViewById(R.id.item_text);
			image.setImageResource(menuImage.get(position));
			text.setText(menuString.get(position));
			return convertView;
		}

	}

	public void changeVoice() {
		thisController.handler.post(new Runnable() {
			@Override
			public void run() {
				String time = thisView.voicePopTime.getText().toString(), seconds = thisActivity.getText(R.string.seconds).toString();
				if (seconds.equals(time)) {
					thisView.voicePopTime.setText("0" + seconds);
				} else {
					thisView.voicePopTime.setText((Integer.valueOf(time.substring(0, time.lastIndexOf(seconds))) + 1) + seconds);
				}

			}
		});
	}

	public void changeVoice(View view) {
		if (this.currentVoiceView != null && this.currentVoiceView.equals(view)) {
			if (thisController.audiohandlers.isPlaying()) {
				// ((ImageView) thisView.currentView.findViewById(R.id.voiceIcon)).setImageResource(R.drawable.icon_play_off);
				thisController.audiohandlers.stopPlay();
			} else {
				// ((ImageView) thisView.currentView.findViewById(R.id.voiceIcon)).setImageResource(R.drawable.icon_play_on);
				thisController.audiohandlers.startPlay((String) view.getTag(R.id.tag_second));
			}
		} else {
			if (thisController.audiohandlers.isPlaying()) {
				thisController.audiohandlers.stopPlay();
				// ((ImageView) thisView.currentView.findViewById(R.id.voiceIcon)).setImageResource(R.drawable.icon_play_off);
			}
			this.currentVoiceView = view;
			// ((ImageView) thisView.currentView.findViewById(R.id.voiceIcon)).setImageResource(R.drawable.icon_play_on);
			thisController.audiohandlers.startPlay((String) view.getTag(R.id.tag_second));
		}
	}

	public void changeVoice(final int resourceId) {
		thisController.handler.post(new Runnable() {
			@Override
			public void run() {
				thisView.voicePopImage.setImageResource(resourceId);

			}
		});
	}

	public void changeVoice(boolean weather) {
		if (weather) {
			this.voicePopPrompt.setText(thisActivity.getString(R.string.slideFingers));
		} else {
			this.voicePopImage.setImageResource(R.drawable.image_chat_voice_cancel);
			this.voicePopPrompt.setText(thisActivity.getString(R.string.loosenFingers));
		}
	}

	public void changeChatMenu() {
		if (this.chatMenuLayout.getVisibility() == View.GONE) {
			this.titleImage.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_arrow_up));
			this.chatMenuLayout.startAnimation(inTranslateAnimation);
			this.chatMenuLayout.setVisibility(View.VISIBLE);
			this.chatMenuBackground.startAnimation(inAlphaAnimation);
			this.chatMenuBackground.setVisibility(View.VISIBLE);
		} else {
			this.titleImage.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_arrow_down));
			this.chatMenuLayout.startAnimation(outTranslateAnimation);
			this.chatMenuLayout.setVisibility(View.GONE);
			this.chatMenuBackground.setVisibility(View.GONE);
		}
	}

	public void changeChatRecord() {
		if (this.textLayout.getVisibility() == View.VISIBLE) {
			this.textLayout.setVisibility(View.GONE);
			this.voiceLayout.setVisibility(View.VISIBLE);
			this.chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_keyboard));
			if (this.faceLayout.getVisibility() == View.VISIBLE) {
				this.faceLayout.setVisibility(View.GONE);
			}
			if (thisController.inputManager.isActive(chatInput)) {
				thisController.inputManager.hide(chatInput);
			}
		} else if (this.voiceLayout.getVisibility() == View.VISIBLE) {
			this.textLayout.setVisibility(View.VISIBLE);
			this.voiceLayout.setVisibility(View.GONE);
			this.chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_record));
			this.chatInput.requestFocus();
		}
		if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
			this.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));
			this.chatAddLayout.setVisibility(View.GONE);
		}
	}

	public void changeChatAdd() {
		if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
			this.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));
			this.chatAddLayout.setVisibility(View.GONE);
		} else {
			this.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_return));
			if (thisController.inputManager.isActive(chatInput)) {
				thisController.inputManager.hide(chatInput);
			}
			if (this.faceLayout.getVisibility() == View.VISIBLE) {
				this.faceLayout.setVisibility(View.GONE);
			}
			this.chatAddLayout.setVisibility(View.VISIBLE);
		}
	}

	public void changeChatSmily() {
		if (this.faceLayout.getVisibility() == View.VISIBLE) {
			this.faceLayout.setVisibility(View.GONE);
		} else {
			if (thisController.inputManager.isActive(chatInput)) {
				thisController.inputManager.hide(chatInput);
			}
			if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
				this.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));
				this.chatAddLayout.setVisibility(View.GONE);
			}
			this.faceLayout.setVisibility(View.VISIBLE);
		}

	}

	public void changeChatInput() {
		if (this.faceLayout.getVisibility() == View.VISIBLE) {
			this.faceLayout.setVisibility(View.GONE);
		}
		if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
			this.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));
			this.chatAddLayout.setVisibility(View.GONE);
		}
		if (this.faceLayout.getVisibility() == View.VISIBLE) {
			this.faceLayout.setVisibility(View.GONE);
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
