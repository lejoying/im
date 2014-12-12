package com.open.welinks.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.MyLog;
import com.open.welinks.NewChatActivity;
import com.open.welinks.R;
import com.open.welinks.WebViewActivity;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.NewChatController;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Event.EventMessage;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandlers;
import com.open.welinks.model.SubData.CardMessageContent;
import com.open.welinks.model.SubData.LocationMessageContent;
import com.open.welinks.model.SubData.MessageShareContent;
import com.open.welinks.model.SubData.VoiceMessageContent;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.ExpressionUtil;
import com.open.welinks.utils.MyGson;

public class NewChatView {

	public String tag = "NewChatView";
	public MyLog log = new MyLog(tag, true);

	public NewChatView thisView;
	public NewChatController thisController;
	public NewChatActivity thisActivity;

	public View currentVoiceView, chatContentHeaderView, chatBottomLayout, chatLayout, backView, chatMenuLayout, textLayout, voiceLayout, chatAddLayout, takePhoto, ablum, location, voicePop;
	public RelativeLayout rightContainer;
	public TextView titleText, chatSend, voicePopTime, voicePopPrompt;
	public ListView chatContent;
	public ImageView chatAdd, chatSmily, chatRecord, titleImage, chatMenuBackground, voicePopImage;
	public EditText chatInput;
	public GridView chatMenu;
	public MapView locationMapView;
	public ChatFaceView faceLayout;

	public SmallBusinessCardPopView businessCardPopView;

	public ChatAdapter mChatAdapter;
	public ChatMenuAdapter mChatMenuAdapter;

	public Animation chatContentAddInRotateAnimation, chatContentAddOutRotateAnimation, chatContentAddInTranslateAnimation, chatContentAddOutTranslateAnimation, chatContentSamilyInTranslateAnimation, chatContentSamilyOutTranslateAnimation, menuInTranslateAnimation, menuInAlphaAnimation, menuOutTranslateAnimation, samilyInTranslateAnimation, samilyOutTranslateAnimation, recoredInAlphAnimation, recoredOutAlphAnimation, addInTranslateAnimation, addOutTranslateAnimation;

	private DisplayImageOptions locationOptions;

	public Timer voiceTimer;
	public int voiceTimerTimes;

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
		chatAddLayout = thisActivity.findViewById(R.id.chatAddLayout);
		takePhoto = thisActivity.findViewById(R.id.takePhoto);
		ablum = thisActivity.findViewById(R.id.ablum);
		location = thisActivity.findViewById(R.id.location);
		voicePop = thisActivity.findViewById(R.id.voicePop);
		chatLayout = thisActivity.findViewById(R.id.chatLayout);
		chatBottomLayout = thisActivity.findViewById(R.id.chatBottomLayout);
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
		locationMapView = (MapView) thisActivity.findViewById(R.id.locationMapView);

		businessCardPopView = new SmallBusinessCardPopView(thisActivity, thisActivity.findViewById(R.id.chatMainView));

		chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_record));
		chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));

		titleImage = new ImageView(thisActivity);
		titleImage.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_arrow_down));
		rightContainer.addView(titleImage);

		locationOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.chat_location_searching).showImageForEmptyUri(R.drawable.chat_location_searching).showImageOnFail(R.drawable.default_user_head).displayer(new RoundedBitmapDisplayer((int) BaseDataUtils.dpToPx(30))).build();

		mChatMenuAdapter = new ChatMenuAdapter();
		chatMenu.setAdapter(mChatMenuAdapter);

		thisController.mAMap = locationMapView.getMap();
		thisController.mAMap.getUiSettings().setZoomControlsEnabled(false);

		initAnimations();

	}

	private void initAnimations() {
		menuInTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_in);
		menuOutTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_out);
		menuInAlphaAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_in_alpha);
		samilyInTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_samily_in);
		samilyOutTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_samily_out);
		recoredInAlphAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_chat_alpha_in);
		recoredOutAlphAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_chat_alpha_out);
		addInTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_add_in);
		addOutTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_add_out);
		chatContentAddInRotateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_add_rotate_in);
		chatContentAddOutRotateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_add_rotate_out);

		// TODO init animation

		chatContentAddInTranslateAnimation = new TranslateAnimation(0, 0, BaseDataUtils.dpToPx(101), 0);
		chatContentAddOutTranslateAnimation = new TranslateAnimation(0, 0, -BaseDataUtils.dpToPx(101), 0);
		chatContentSamilyInTranslateAnimation = new TranslateAnimation(0, 0, 0, -BaseDataUtils.dpToPx(200));
		chatContentSamilyOutTranslateAnimation = new TranslateAnimation(0, 0, BaseDataUtils.dpToPx(200), 0);
		chatContentAddInTranslateAnimation.setDuration(250);
		chatContentAddOutTranslateAnimation.setDuration(250);
		chatContentSamilyInTranslateAnimation.setDuration(2000);
		chatContentSamilyOutTranslateAnimation.setDuration(2000);

	}

	public void fillData() {
		if (thisController.data.localStatus.localData.notSentMessagesMap != null) {
			String content = thisController.data.localStatus.localData.notSentMessagesMap.remove(thisController.type + thisController.key);
			if (content != null) {
				thisView.chatInput.setText(content);
			}
		}
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
			log.e("current:::" + thisController.data.relationship.groupsMap.get(key).members.size());
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
		chatContent.addHeaderView(initHeaderView());
		chatContent.setAdapter(mChatAdapter);
		if (thisController.showChatCounts >= thisView.mChatAdapter.messages.size()) {
			thisView.chatContentHeaderView.setVisibility(View.GONE);
		} else {
			thisView.chatContentHeaderView.setVisibility(View.VISIBLE);
		}
		changeChatList();
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
			chatContent.setSelection(chatContent.getBottom());
		}

		@Override
		public int getCount() {
			// int size;
			// if (messages.size() > thisController.showChatCounts) {
			// size = thisController.showChatCounts;
			// } else {
			// size = messages.size();
			// }
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			// int id;
			// if (messages.size() > thisController.showChatCounts) {
			// id = messages.size() - thisController.showChatCounts + position;
			// } else {
			// id = position;
			// }
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			Message message = messages.get((int) getItemId(position));
			Message message2 = null;
			Friend friend;
			if ((int) getItemId(position) != 0) {
				message2 = messages.get((int) getItemId(position) - 1);
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChatHolder holder = null;
			Friend friend = null;
			int type = getItemViewType(position);
			Message message = messages.get((int) getItemId(position));
			if (message != null) {
				friend = thisController.data.relationship.friendsMap.get(message.phone);
			}
			String messageHead = "";
			if (convertView == null) {
				holder = new ChatHolder();
				if (type == TYPE_SELF) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_send, null);
				} else if (type == TYPE_SELF_FIRST) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_send, null);
				} else if (type == TYPE_OTHER_MALE) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
				} else if (type == TYPE_OTHER_MALE_FIRST) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
				} else if (type == TYPE_OTHER_FEMALE) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
				} else if (type == TYPE_OTHER_FEMALE_FIRST) {
					convertView = thisActivity.mInflater.inflate(R.layout.f_new_chat_item_receive, null);
				} else if (type == TYPE_EVENT) {
					convertView = thisActivity.mInflater.inflate(R.layout.chat_item_event, null);
				}

				if (type == TYPE_EVENT) {
					holder.character = (TextView) convertView.findViewById(R.id.character);
				} else {
					holder.chatLayout = convertView.findViewById(R.id.chatLayout);
					holder.voice = convertView.findViewById(R.id.voice);
					holder.imagesLayout = convertView.findViewById(R.id.imagesLayout);
					holder.cardLayout = convertView.findViewById(R.id.cardLayout);
					holder.share = convertView.findViewById(R.id.share);
					holder.image = (ImageView) convertView.findViewById(R.id.image);
					holder.images = (ImageView) convertView.findViewById(R.id.images);
					holder.head = (ImageView) convertView.findViewById(R.id.head);
					holder.status = (ImageView) convertView.findViewById(R.id.status);
					holder.shareImage = (ImageView) convertView.findViewById(R.id.shareImage);
					holder.locationImage = (ImageView) convertView.findViewById(R.id.locationImage);
					holder.cardTitleImage = (ImageView) convertView.findViewById(R.id.cardTitleImage);
					holder.cardHead = (ImageView) convertView.findViewById(R.id.cardHead);
					holder.time = (TextView) convertView.findViewById(R.id.time);
					holder.character = (TextView) convertView.findViewById(R.id.character);
					holder.voicetime = (TextView) convertView.findViewById(R.id.voicetime);
					holder.imagesCount = (TextView) convertView.findViewById(R.id.imagesCount);
					holder.shareTitle = (TextView) convertView.findViewById(R.id.shareTitle);
					holder.shareText = (TextView) convertView.findViewById(R.id.shareText);
					holder.cardTitleText = (TextView) convertView.findViewById(R.id.cardTitleText);
					holder.cardName = (TextView) convertView.findViewById(R.id.cardName);
					holder.cardMainBusiness = (TextView) convertView.findViewById(R.id.cardMainBusiness);
					holder.gif = (GifImageView) convertView.findViewById(R.id.gif);
					holder.voiceGif = (ImageView) convertView.findViewById(R.id.voiceGif);
				}
				if (type == TYPE_SELF) {
					holder.chatLayout.setBackgroundResource(R.drawable.myself_chat_order_bg);
				} else if (type == TYPE_SELF_FIRST) {
					holder.chatLayout.setBackgroundResource(R.drawable.myself_chat_bg);
				} else if (type == TYPE_OTHER_MALE) {
					holder.chatLayout.setBackgroundResource(R.drawable.male_chat_from_order_bg);
				} else if (type == TYPE_OTHER_MALE_FIRST) {
					holder.chatLayout.setBackgroundResource(R.drawable.male_chat_from_bg);
				} else if (type == TYPE_OTHER_FEMALE) {
					holder.chatLayout.setBackgroundResource(R.drawable.female_chat_from_order_bg);
				} else if (type == TYPE_OTHER_FEMALE_FIRST) {
					holder.chatLayout.setBackgroundResource(R.drawable.female_chat_from_bg);
				}
				convertView.setTag(holder);
			} else {
				// Log.e("NewChatView", type + "::::::::::::::::::::::::::::::" + message.content);
				holder = (ChatHolder) convertView.getTag();
			}

			if (type == TYPE_EVENT) {
				MyGson gson = new MyGson();
				EventMessage event = gson.fromJson(message.content, EventMessage.class);
				String content = DataHandlers.switchChatMessageEvent(event);
				holder.character.setText(content);
			} else {
				if (type == TYPE_SELF_FIRST) {
					messageHead = currentUser.head;
				} else if (type == TYPE_OTHER_MALE_FIRST || type == TYPE_OTHER_FEMALE_FIRST) {
					messageHead = friend.head;
				}
				if (!"".equals(messageHead)) {
					thisController.fileHandlers.getHeadImage(messageHead, holder.head, thisController.viewManage.options40);
					holder.head.setTag(R.id.tag_first, "head");
					holder.head.setTag(R.id.tag_second, message.phone);
					holder.head.setOnClickListener(thisController.mOnClickListener);
				} else {
					holder.head.setVisibility(View.GONE);
				}

				String contentType = message.contentType;
				if (type == TYPE_SELF) {
					holder.status.setVisibility(View.VISIBLE);
					if ("sending".equals(message.status)) {
						long currentLong = System.currentTimeMillis();
						long cha = (currentLong - Long.valueOf(message.time)) / 1000;
						if (cha > 60) {
							message.status = "failed";
							this.notifyDataSetChanged();
						} else {
							holder.status.setImageResource(R.drawable.message_send);
						}
					} else if ("sent".equals(message.status)) {
						holder.status.setVisibility(View.GONE);
					} else if ("failed".equals(message.status)) {
						holder.status.setImageResource(R.drawable.message_resend);
						holder.status.setTag(R.id.tag_first, "resend");
						holder.status.setTag(R.id.tag_second, position);
						holder.status.setOnClickListener(thisController.mOnClickListener);
					} else {
						holder.status.setImageResource(R.drawable.message_failed);
						holder.status.setVisibility(View.GONE);
					}
					// TODO resend current message
					holder.status.setVisibility(View.GONE);
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
					holder.locationImage.setVisibility(View.GONE);
					holder.cardLayout.setVisibility(View.GONE);
					holder.character.setText(message.content);
					holder.character.setAutoLinkMask(Linkify.WEB_URLS);
					holder.character.setMovementMethod(LinkMovementMethod.getInstance());
					URLSpan[] urls = holder.character.getUrls();
					String contentString = message.content;
					SpannableString spannableString = ExpressionUtil.getExpressionString(thisActivity, contentString, Constant.FACEREGX);
					SpannableStringBuilder style = new SpannableStringBuilder(spannableString);
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
					holder.locationImage.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.cardLayout.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);

					VoiceMessageContent messageContent = null;
					try {
						messageContent = thisController.gson.fromJson(message.content, VoiceMessageContent.class);
					} catch (Exception e) {
						Log.e("NewChatView", message.content);
					}
					if (messageContent == null) {
						holder.voicetime.setText("数据结构错误");
					} else {
						holder.voicetime.setText(messageContent.time + "s");
						thisController.audiohandlers.prepareVoice(messageContent.fileName, Integer.valueOf(messageContent.recordReadSize));
						holder.voice.setTag(R.id.tag_first, contentType);
						holder.voice.setTag(R.id.tag_second, messageContent.fileName);
						holder.voice.setTag(R.id.tag_third, messageContent.recordReadSize);
						holder.voice.setTag(R.id.tag_fourth, type);
						holder.voice.setOnClickListener(thisController.mOnClickListener);
					}
				} else if (contentType.equals("image")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.GONE);
					holder.cardLayout.setVisibility(View.GONE);

					List<String> images = null;
					try {
						images = thisController.gson.fromJson(message.content, new TypeToken<List<String>>() {
						}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (images == null) {
						holder.character.setVisibility(View.VISIBLE);
						holder.character.setText("数据结构错误");
						holder.image.setVisibility(View.GONE);
					} else {
						LinearLayout.LayoutParams imagePrams = new LinearLayout.LayoutParams((int) BaseDataUtils.dpToPx(178), (int) BaseDataUtils.dpToPx(146));
						imagePrams.setMargins((int) BaseDataUtils.dpToPx(10), (int) BaseDataUtils.dpToPx(10), (int) BaseDataUtils.dpToPx(10), (int) BaseDataUtils.dpToPx(10));
						String image = images.get(0);
						if (images.size() == 1) {
							holder.imagesLayout.setVisibility(View.GONE);
							holder.image.setVisibility(View.VISIBLE);
							holder.image.setTag(R.id.tag_first, contentType);
							holder.image.setTag(R.id.tag_second, images);
							holder.image.setOnClickListener(thisController.mOnClickListener);
							holder.image.setLayoutParams(imagePrams);
							thisController.fileHandlers.getThumbleImage(image, holder.image, (int) BaseDataUtils.dpToPx(178), (int) BaseDataUtils.dpToPx(146), thisController.viewManage.options30, thisController.fileHandlers.THUMBLE_TYEP_CHAT, null);
						} else {
							holder.image.setVisibility(View.GONE);
							holder.imagesLayout.setVisibility(View.VISIBLE);
							holder.imagesCount.setText(String.valueOf(images.size()));
							holder.imagesLayout.setTag(R.id.tag_first, contentType);
							holder.imagesLayout.setTag(R.id.tag_second, images);
							holder.imagesLayout.setOnClickListener(thisController.mOnClickListener);
							holder.imagesLayout.setLayoutParams(imagePrams);
							thisController.fileHandlers.getThumbleImage(image, holder.images, (int) BaseDataUtils.dpToPx(178), (int) BaseDataUtils.dpToPx(146), thisController.viewManage.options, thisController.fileHandlers.THUMBLE_TYEP_CHAT, null);
						}
					}
				} else if (contentType.equals("gif")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.GONE);
					holder.cardLayout.setVisibility(View.GONE);
					holder.gif.setVisibility(View.VISIBLE);
					LinearLayout.LayoutParams layoutParams = (LayoutParams) holder.gif.getLayoutParams();
					layoutParams.width = (int) (thisController.data.baseData.density / 1.5f * 120);
					layoutParams.height = (int) (thisController.data.baseData.density / 1.5f * 120);
					thisController.fileHandlers.getGifImage(message.content, holder.gif);
				} else if (contentType.equals("share")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.GONE);
					holder.cardLayout.setVisibility(View.GONE);
					holder.share.setVisibility(View.VISIBLE);

					MessageShareContent messageContent = thisController.gson.fromJson(message.content, MessageShareContent.class);
					holder.shareText.setText(messageContent.text);
					if (messageContent.image != null && !"".equals(messageContent.image)) {
						thisController.fileHandlers.getThumbleImage(messageContent.image, holder.shareImage, (int) BaseDataUtils.dpToPx(50), (int) BaseDataUtils.dpToPx(50), thisController.viewManage.options40, thisController.fileHandlers.THUMBLE_TYEP_CHAT, null);
					} else {
						thisController.imageLoader.displayImage("drawable://" + R.drawable.icon, holder.shareImage, thisController.viewManage.options40);
					}
					holder.share.setTag(R.id.tag_first, contentType);
					holder.share.setTag(R.id.tag_second, messageContent.gid);
					holder.share.setTag(R.id.tag_third, messageContent.gsid);
					holder.share.setTag(R.id.tag_fourth, messageContent.sid);
					holder.share.setOnClickListener(thisController.mOnClickListener);
				} else if (contentType.equals("location")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.cardLayout.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.VISIBLE);
					if (!"".equals(message.content)) {
						LocationMessageContent messageContent = thisController.gson.fromJson(message.content, LocationMessageContent.class);
						if (messageContent != null) {
							if ("".equals(messageContent.imageFileName)) {
								thisController.imageLoader.displayImage("drawable://" + R.drawable.chat_location_searching, holder.shareImage, thisController.viewManage.options);
							} else {
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) BaseDataUtils.dpToPx(150), (int) BaseDataUtils.dpToPx(100));
								thisController.fileHandlers.getImage(messageContent.imageFileName, holder.locationImage, params, DownloadFile.TYPE_IMAGE, locationOptions);
								holder.locationImage.setTag(R.id.tag_first, contentType);
								holder.locationImage.setTag(R.id.tag_second, messageContent.address);
								holder.locationImage.setTag(R.id.tag_third, messageContent.latitude);
								holder.locationImage.setTag(R.id.tag_fourth, messageContent.longitude);
								holder.locationImage.setTag(R.id.tag_fifth, params);
								holder.locationImage.setOnClickListener(thisController.mOnClickListener);
							}
						}
					} else {
						thisController.imageLoader.displayImage("drawable://" + R.drawable.chat_location_searching, holder.shareImage, thisController.viewManage.options);
					}
				} else if (contentType.equals("card")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.GONE);
					holder.cardLayout.setVisibility(View.VISIBLE);

					CardMessageContent messageContent = thisController.gson.fromJson(message.content, CardMessageContent.class);
					if ("point".equals(messageContent.type)) {
						holder.cardTitleText.setText(thisActivity.getString(R.string.personCard));
						holder.cardTitleImage.setImageResource(R.drawable.person_card_icon);
					} else if ("group".equals(messageContent.type)) {
						holder.cardTitleText.setText(thisActivity.getString(R.string.groupCard));
						holder.cardTitleImage.setImageResource(R.drawable.group_card_icon);
					}
					holder.cardName.setText(messageContent.name);
					holder.cardMainBusiness.setText(BaseDataUtils.generateMainBusiness(messageContent.type, messageContent.mainBusiness));
					thisController.fileHandlers.getHeadImage(messageContent.head, holder.cardHead, thisController.viewManage.options40);
					holder.cardLayout.setTag(R.id.tag_first, contentType);
					holder.cardLayout.setTag(R.id.tag_second, messageContent.type);
					holder.cardLayout.setTag(R.id.tag_third, messageContent.key);
					holder.cardLayout.setOnClickListener(thisController.mOnClickListener);
				}
			}
			return convertView;
		}

		class ChatHolder {
			View voice, share, chatLayout, imagesLayout, cardLayout;
			ImageView voiceIcon, image, head, status, images, shareImage, locationImage, cardTitleImage, cardHead, voiceGif;
			TextView time, character, voicetime, imagesCount, shareTitle, shareText, cardTitleText, cardName, cardMainBusiness;
			GifImageView gif;
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
				// menuString.add(thisActivity.getString(R.string.groupAlbum));
				// menuString.add(thisActivity.getString(R.string.closeNotice));
				// menuString.add(thisActivity.getString(R.string.share));
				menuString.add(thisActivity.getString(R.string.sendCard));
				// menuString.add(thisActivity.getString(R.string.tureOff));
				menuString.add(thisActivity.getString(R.string.setting));
				menuImage.add(R.drawable.chat_menu_item_details);
				menuImage.add(R.drawable.chat_menu_item_members);
				// menuImage.add(R.drawable.chat_menu_item_albums);
				// menuImage.add(R.drawable.chat_menu_item_tips_on);
				// menuImage.add(R.drawable.chat_menu_item_share);
				menuImage.add(R.drawable.chat_menu_item_send);
				// menuImage.add(R.drawable.chat_menu_item_light);
				menuImage.add(R.drawable.chat_menu_item_settings);
			} else {
				menuString.add(thisActivity.getString(R.string.personalAlbum));
				menuString.add(thisActivity.getString(R.string.personalDetails));
				// menuString.add(thisActivity.getString(R.string.closeNotice));
				menuString.add(thisActivity.getString(R.string.sendCard));
				menuImage.add(R.drawable.chat_menu_item_albums);
				menuImage.add(R.drawable.chat_menu_item_details);
				// menuImage.add(R.drawable.chat_menu_item_tips_on);
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
			convertView.setTag(menuString.get(position));
			if (position == 0 && thisController.type.equals("point")) {
				convertView.setVisibility(View.INVISIBLE);
			}
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

	public void changeVoice(final View view) {
		if (this.currentVoiceView != null && this.currentVoiceView.equals(view)) {
			if (thisController.audiohandlers.isPlaying()) {
				thisController.postHandler(thisController.HANDLER_CHAT_STOPPLAY);
				thisView.showVoiceMoive(false);
				thisController.audiohandlers.stopPlay();
			} else {
				thisController.postHandler(thisController.HANDLER_CHAT_STARTPLAY);
				thisController.audiohandlers.prepareVoice((String) view.getTag(R.id.tag_second), Integer.valueOf((String) view.getTag(R.id.tag_third)), true);
				thisView.showVoiceMoive(true);
			}
		} else {
			if (thisController.audiohandlers.isPlaying()) {
				thisController.handler.post(new Runnable() {
					@Override
					public void run() {
						thisView.currentVoiceView.findViewById(R.id.voiceGif).setVisibility(View.INVISIBLE);
						thisView.showVoiceMoive(false);
						thisController.continuePlay = true;
						thisView.currentVoiceView = view;
						thisController.audiohandlers.stopPlay();
					}
				});
			} else {
				thisController.postHandler(thisController.HANDLER_CHAT_STARTPLAY);
				thisController.audiohandlers.prepareVoice((String) view.getTag(R.id.tag_second), Integer.valueOf((String) view.getTag(R.id.tag_third)), true);
				this.currentVoiceView = view;
				thisView.showVoiceMoive(true);
			}
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
		if (this.chatMenuLayout.getVisibility() == View.VISIBLE)
			thisView.titleImage.performClick();
	}

	public void changeChatMenu() {
		if (this.chatMenuLayout.getVisibility() == View.GONE) {
			this.titleImage.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_arrow_up));
			this.chatMenuLayout.startAnimation(menuInTranslateAnimation);
			this.chatMenuLayout.setVisibility(View.VISIBLE);
			this.chatMenuBackground.startAnimation(menuInAlphaAnimation);
			this.chatMenuBackground.setVisibility(View.VISIBLE);
			if (thisController.inputManager.isActive(chatInput))
				thisController.inputManager.hide(chatInput);
			if (this.faceLayout.getVisibility() == View.VISIBLE)
				this.chatSmily.performClick();
			if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
				this.chatAdd.performClick();
			}
		} else {
			this.titleImage.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_arrow_down));
			this.chatMenuLayout.startAnimation(menuOutTranslateAnimation);
			this.chatMenuLayout.setVisibility(View.GONE);
			this.chatMenuBackground.setVisibility(View.GONE);
		}
	}

	public void changeChatRecord() {
		if (this.textLayout.getVisibility() == View.VISIBLE) {
			if (this.faceLayout.getVisibility() == View.VISIBLE)
				this.chatSmily.performClick();
			if (thisController.inputManager.isActive(chatInput))
				thisController.inputManager.hide(chatInput);
			this.textLayout.startAnimation(recoredOutAlphAnimation);
			this.textLayout.setVisibility(View.GONE);
			this.voiceLayout.setVisibility(View.VISIBLE);
			this.voiceLayout.startAnimation(recoredInAlphAnimation);
			this.chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_keyboard));
		} else if (this.voiceLayout.getVisibility() == View.VISIBLE) {
			this.voiceLayout.startAnimation(recoredOutAlphAnimation);
			this.voiceLayout.setVisibility(View.GONE);
			this.textLayout.setVisibility(View.VISIBLE);
			this.textLayout.startAnimation(recoredInAlphAnimation);
			this.chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_record));
			this.chatInput.requestFocus();
		}
		if (this.chatAddLayout.getVisibility() == View.VISIBLE)
			this.chatAdd.performClick();
		if (this.chatMenuLayout.getVisibility() == View.VISIBLE)
			this.titleImage.performClick();
	}

	public void changeChatAdd() {
		// TODO animation
		if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
			this.chatAdd.startAnimation(chatContentAddOutRotateAnimation);
			// this.chatBottomLayout.startAnimation(addOutTranslateAnimation);
			// this.chatContent.startAnimation(chatContentAddOutTranslateAnimation);
		} else {
			this.chatAdd.startAnimation(chatContentAddInRotateAnimation);
			if (thisController.inputManager.isActive(chatInput))
				thisController.inputManager.hide(chatInput);
			if (this.faceLayout.getVisibility() == View.VISIBLE)
				this.chatSmily.performClick();
			if (this.chatMenuLayout.getVisibility() == View.VISIBLE)
				this.titleImage.performClick();
			// this.chatContent.getLayoutParams().height = (int) (thisController.data.baseData.appHeight - BaseDataUtils.dpToPx(106));
			// this.chatBottomLayout.startAnimation(addInTranslateAnimation);
			// this.chatContent.startAnimation(chatContentAddInTranslateAnimation);
		}
	}

	public void changeChatSmily() {
		// TODO animation
		if (this.faceLayout.getVisibility() == View.VISIBLE) {
			faceLayout.setVisibility(View.GONE);
			// chatBottomLayout.startAnimation(samilyOutTranslateAnimation);
		} else {
			if (thisController.inputManager.isActive(chatInput))
				thisController.inputManager.hide(chatInput);
			if (this.chatAddLayout.getVisibility() == View.VISIBLE)
				this.chatAdd.performClick();
			if (this.chatMenuLayout.getVisibility() == View.VISIBLE)
				thisView.titleImage.performClick();
			faceLayout.setVisibility(View.VISIBLE);
			changeChatList();
			// chatBottomLayout.startAnimation(samilyInTranslateAnimation);
		}
	}

	public void changeChatInput() {
		if (this.faceLayout.getVisibility() == View.VISIBLE) {
			this.chatSmily.performClick();
		}
		if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
			this.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));
			this.chatAddLayout.setVisibility(View.GONE);
		}
		if (this.faceLayout.getVisibility() == View.VISIBLE) {
			this.faceLayout.setVisibility(View.GONE);
		}
		changeChatList();
	}

	public void changeChatList() {
		new Thread() {
			public void run() {
				try {
					sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (thisController.chatContentRunnable == null) {
					thisController.chatContentRunnable = new Runnable() {
						@Override
						public void run() {
							chatContent.setSelection(chatContent.getBottom());
						}
					};

				}
				thisController.handler.post(thisController.chatContentRunnable);
			};
		}.start();
	}

	public void showVoiceMoive(boolean weather) {
		if (currentVoiceView != null) {
			if (weather) {
				if (currentVoiceView != null) {
					int i = (Integer) currentVoiceView.getTag(R.id.tag_fourth), drawableId = 0;
					if (i == mChatAdapter.TYPE_SELF || i == mChatAdapter.TYPE_SELF_FIRST) {
						drawableId = R.drawable.personal_chat_sound1;
					} else {
						drawableId = R.drawable.others_chat_sound1;
					}
					final int id = drawableId;
					thisController.handler.post(new Runnable() {
						@Override
						public void run() {
							if (id != 0) {
								((ImageView) currentVoiceView.findViewById(R.id.voiceGif)).setImageResource(id);
							}
						}
					});
				}
				voiceTimer = new Timer();
				voiceTimerTimes = 0;
				voiceTimer.schedule(new VoiceMoiveTimerTask(), new Date(), 500);
			} else {
				if (voiceTimer != null)
					voiceTimer.cancel();
				voiceTimerTimes = 0;
				voiceTimer = null;
			}
		}
	}

	private View initHeaderView() {
		chatContentHeaderView = thisActivity.mInflater.inflate(R.layout.chat_list_header_view, null);
		chatContentHeaderView.setOnClickListener(thisController.mOnClickListener);
		return chatContentHeaderView;
	}

	private class VoiceMoiveTimerTask extends TimerTask {

		@Override
		public void run() {
			if (currentVoiceView != null) {
				int i = (Integer) currentVoiceView.getTag(R.id.tag_fourth), drawableId = 0;
				if (i == mChatAdapter.TYPE_SELF || i == mChatAdapter.TYPE_SELF_FIRST) {
					if (voiceTimerTimes % 3 == 0) {
						drawableId = R.drawable.personal_chat_sound1;
					} else if (voiceTimerTimes % 3 == 1) {
						drawableId = R.drawable.personal_chat_sound2;
					} else if (voiceTimerTimes % 3 == 2) {
						drawableId = R.drawable.personal_chat_sound3;
					}
				} else {
					if (voiceTimerTimes % 3 == 0) {
						drawableId = R.drawable.others_chat_sound1;
					} else if (voiceTimerTimes % 3 == 1) {
						drawableId = R.drawable.others_chat_sound2;
					} else if (voiceTimerTimes % 3 == 2) {
						drawableId = R.drawable.others_chat_sound3;
					}
				}
				final int id = drawableId;
				thisController.handler.post(new Runnable() {
					@Override
					public void run() {
						if (id != 0) {
							((ImageView) currentVoiceView.findViewById(R.id.voiceGif)).setImageResource(id);
						}
					}
				});
				voiceTimerTimes++;
			}

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
