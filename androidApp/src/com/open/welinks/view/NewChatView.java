package com.open.welinks.view;

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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.NewChatActivity;
import com.open.welinks.R;
import com.open.welinks.WebViewActivity;
import com.open.welinks.controller.DownloadFile;
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
import com.open.welinks.model.SubData.LocationMessageContent;
import com.open.welinks.model.SubData.MessageShareContent;
import com.open.welinks.model.SubData.VoiceMessageContent;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.ExpressionUtil;
import com.open.welinks.utils.MyGson;

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
	public MapView locationMapView;
	public ChatFaceView faceLayout;

	public SmallBusinessCardPopView businessCardPopView;

	public ChatAdapter mChatAdapter;
	public ChatMenuAdapter mChatMenuAdapter;

	private Animation inTranslateAnimation, inAlphaAnimation, outTranslateAnimation;

	private DisplayImageOptions headOptions, locationOptions;

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
		locationMapView = (MapView) thisActivity.findViewById(R.id.locationMapView);

		businessCardPopView = new SmallBusinessCardPopView(thisActivity, thisActivity.findViewById(R.id.chatMainView));

		chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_record));
		chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));

		titleImage = new ImageView(thisActivity);
		titleImage.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_arrow_down));
		rightContainer.addView(titleImage);

		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();
		locationOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.chat_location_searching).showImageForEmptyUri(R.drawable.chat_location_searching).showImageOnFail(R.drawable.default_user_head).displayer(new RoundedBitmapDisplayer((int) BaseDataUtils.dpToPx(30))).build();

		mChatMenuAdapter = new ChatMenuAdapter();
		chatMenu.setAdapter(mChatMenuAdapter);

		inTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_in);
		outTranslateAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_out);
		inAlphaAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.chat_menu_in_alpha);

		locationMapView.onCreate(thisActivity.savedInstanceState);
		thisController.mAMap = locationMapView.getMap();
		thisController.mAMap.getUiSettings().setZoomControlsEnabled(false);

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
					holder.share = convertView.findViewById(R.id.share);
					holder.image = (ImageView) convertView.findViewById(R.id.image);
					holder.images = (ImageView) convertView.findViewById(R.id.images);
					holder.head = (ImageView) convertView.findViewById(R.id.head);
					holder.status = (ImageView) convertView.findViewById(R.id.status);
					holder.shareImage = (ImageView) convertView.findViewById(R.id.shareImage);
					holder.locationImage = (ImageView) convertView.findViewById(R.id.locationImage);
					holder.time = (TextView) convertView.findViewById(R.id.time);
					holder.character = (TextView) convertView.findViewById(R.id.character);
					holder.voicetime = (TextView) convertView.findViewById(R.id.voicetime);
					holder.imagesCount = (TextView) convertView.findViewById(R.id.imagesCount);
					holder.shareTitle = (TextView) convertView.findViewById(R.id.shareTitle);
					holder.shareText = (TextView) convertView.findViewById(R.id.shareText);
					holder.gif = (GifImageView) convertView.findViewById(R.id.gif);
					holder.voiceGif = (GifImageView) convertView.findViewById(R.id.voiceGif);
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
					thisController.fileHandlers.getHeadImage(messageHead, holder.head, headOptions);
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
					SpannableString spannableString = ExpressionUtil.getExpressionString(thisActivity, style.toString(), Constant.FACEREGX);
					holder.character.setText(spannableString);
				} else if (contentType.equals("voice")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.VISIBLE);
					holder.image.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
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
					VoiceMessageContent messageContent = thisController.gson.fromJson(message.content, VoiceMessageContent.class);
					if (messageContent == null) {
						holder.voicetime.setText("数据结构错误");
					} else {
						holder.voicetime.setText(messageContent.time + "s");
						thisController.audiohandlers.prepareVoice(messageContent.fileName, Integer.valueOf(messageContent.recordReadSize));
						holder.voice.setTag(R.id.tag_first, contentType);
						holder.voice.setTag(R.id.tag_second, messageContent.fileName);
						holder.voice.setTag(R.id.tag_third, messageContent.recordReadSize);
						holder.voice.setOnClickListener(thisController.mOnClickListener);
					}
				} else if (contentType.equals("image")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.GONE);

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
					holder.locationImage.setVisibility(View.GONE);
					holder.gif.setVisibility(View.VISIBLE);
					thisController.fileHandlers.getGifImage(message.content, holder.gif);
				} else if (contentType.equals("share")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.GONE);
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
				} else if (contentType.equals("location")) {
					holder.character.setVisibility(View.GONE);
					holder.voice.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
					holder.imagesLayout.setVisibility(View.GONE);
					holder.share.setVisibility(View.GONE);
					holder.gif.setVisibility(View.GONE);
					holder.locationImage.setVisibility(View.VISIBLE);
					if (!"".equals(message.content)) {
						LocationMessageContent messageContent = thisController.gson.fromJson(message.content, LocationMessageContent.class);
						if (messageContent != null) {
							if ("".equals(messageContent.imageFileName)) {
								holder.locationImage.setImageResource(R.drawable.chat_location_searching);
							} else {
								thisController.fileHandlers.getImage(messageContent.imageFileName, holder.locationImage, new LinearLayout.LayoutParams((int) BaseDataUtils.dpToPx(150), (int) BaseDataUtils.dpToPx(100)), DownloadFile.TYPE_IMAGE, locationOptions);
							}
							holder.locationImage.setTag(R.id.tag_first, contentType);
							holder.locationImage.setTag(R.id.tag_second, messageContent.address);
							holder.locationImage.setTag(R.id.tag_third, messageContent.latitude);
							holder.locationImage.setTag(R.id.tag_four, messageContent.longitude);
							holder.locationImage.setOnClickListener(thisController.mOnClickListener);
						}
					} else {
						holder.locationImage.setImageResource(R.drawable.chat_location_searching);
					}
				}
			}
			return convertView;
		}

		class ChatHolder {
			View voice, share, chatLayout, imagesLayout;
			ImageView voiceIcon, image, head, status, images, shareImage, locationImage;
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
			convertView.setTag(menuString.get(position));
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
				thisController.audiohandlers.startPlay((String) view.getTag(R.id.tag_second), (String) view.getTag(R.id.tag_third));
			}
		} else {
			if (thisController.audiohandlers.isPlaying()) {
				thisController.audiohandlers.stopPlay();
				// ((ImageView) thisView.currentView.findViewById(R.id.voiceIcon)).setImageResource(R.drawable.icon_play_off);
			}
			this.currentVoiceView = view;
			// ((ImageView) thisView.currentView.findViewById(R.id.voiceIcon)).setImageResource(R.drawable.icon_play_on);
			thisController.audiohandlers.startPlay((String) view.getTag(R.id.tag_second), (String) view.getTag(R.id.tag_third));
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
			this.chatMenuLayout.startAnimation(inTranslateAnimation);
			this.chatMenuLayout.setVisibility(View.VISIBLE);
			this.chatMenuBackground.startAnimation(inAlphaAnimation);
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
			this.chatMenuLayout.startAnimation(outTranslateAnimation);
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
			this.textLayout.setVisibility(View.GONE);
			this.voiceLayout.setVisibility(View.VISIBLE);
			this.chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_keyboard));
		} else if (this.voiceLayout.getVisibility() == View.VISIBLE) {
			this.textLayout.setVisibility(View.VISIBLE);
			this.voiceLayout.setVisibility(View.GONE);
			this.chatRecord.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_record));
			this.chatInput.requestFocus();
		}
		if (this.chatAddLayout.getVisibility() == View.VISIBLE)
			this.chatAdd.performClick();
		if (this.chatMenuLayout.getVisibility() == View.VISIBLE)
			thisView.titleImage.performClick();
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
			if (this.faceLayout.getVisibility() == View.VISIBLE)
				this.chatSmily.performClick();
			if (this.chatMenuLayout.getVisibility() == View.VISIBLE)
				thisView.titleImage.performClick();
			this.chatAddLayout.setVisibility(View.VISIBLE);
			changeChatList();
		}
	}

	public void changeChatSmily() {
		if (this.faceLayout.getVisibility() == View.VISIBLE) {
			this.faceLayout.setVisibility(View.GONE);
		} else {
			if (thisController.inputManager.isActive(chatInput))
				thisController.inputManager.hide(chatInput);

			if (this.chatAddLayout.getVisibility() == View.VISIBLE) {
				this.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));
				this.chatAddLayout.setVisibility(View.GONE);
			}
			if (this.chatMenuLayout.getVisibility() == View.VISIBLE)
				thisView.titleImage.performClick();
			this.faceLayout.setVisibility(View.VISIBLE);
			changeChatList();
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
					sleep(100);
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
