package com.open.welinks.view;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.TouchImageView;
import com.open.lib.TouchTextView;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.ShareSubController;
import com.open.welinks.customView.ControlProgress;
import com.open.welinks.customView.ScrollListBody;
import com.open.welinks.customView.ScrollListBody.ScrollListItemBody;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.Comment;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.SubData.ShareContent;
import com.open.welinks.model.SubData.ShareContent.ShareContentItem;
import com.open.welinks.utils.DateUtil;

public class ShareSubView {

	public Data data = Data.getInstance();

	public String tag = "ShareSubView";

	public MyLog log = new MyLog(tag, true);

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	// share
	public RelativeLayout shareView;

	public ViewGroup shareMessageView;
	public ListBody1 shareMessageListBody;

	public RelativeLayout shareTitleView;
	public ImageView leftImageButton;
	public RelativeLayout shareTopMenuGroupNameParent;
	public TextView shareTopMenuGroupName;

	// group
	// public PopupWindow groupPopWindow;
	// pop layout
	public TouchView groupDialogView;

	public TouchView groupsDialogContent;

	public ListBody1 groupListBody;

	// share top Bar child view
	public View groupMembersView;
	public ViewGroup groupMembersListContentView;
	public ImageView releaseShareView;

	public ImageView groupCoverView;
	public ImageView groupHeadView;

	public View groupManageView;
	public View groupsManageButtons;
	public View groupListButtonView;
	public View createGroupButtonView;
	public View findMoreGroupButtonView;

	public int shareImageHeight;

	public float imageHeightScale = 0.5686505598114319f;

	public float panelScale = 1.010845986984816f;

	public float converScale = 0.5277777777777778f;

	public int panelHeight;
	public int panelWidth;

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 9);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();
	public Parser parser = Parser.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	// first share View Animation true or false
	public boolean isShowFirstMessageAnimation = false;

	public ShareSubView(MainView mainView) {
		this.mainView = mainView;
		viewManage.shareSubView = this;
	}

	public SmallBusinessCardPopView businessCardPopView;

	public View releaseChannelContainer;
	public View releaseChannelView;

	public TextView roomTextView;

	public void initViews() {
		this.shareView = mainView.shareView;
		this.displayMetrics = mainView.displayMetrics;

		shareImageHeight = (int) (this.displayMetrics.widthPixels * imageHeightScale);
		panelHeight = (int) (this.displayMetrics.widthPixels * panelScale);

		shareMessageView = (ViewGroup) shareView.findViewById(R.id.groupShareMessageContent);

		shareMessageListBody = new ListBody1();
		shareMessageListBody.initialize(displayMetrics, shareMessageView);

		shareTitleView = (RelativeLayout) shareView.findViewById(R.id.title_share);
		shareTitleView.setTag(R.id.tag_class, "title_share");
		leftImageButton = (ImageView) shareView.findViewById(R.id.leftImageButton);
		shareTopMenuGroupNameParent = (RelativeLayout) shareView.findViewById(R.id.shareTopMenuGroupNameParent);
		shareTopMenuGroupName = (TextView) shareView.findViewById(R.id.shareTopMenuGroupName);

		this.groupMembersView = mainView.mInflater.inflate(R.layout.share_group_members_show, null);
		groupMembersListContentView = (ViewGroup) this.groupMembersView.findViewById(R.id.groupMembersListContent);
		groupMembersListContentView.setTag(R.id.tag_class, "group_members");
		releaseShareView = (TouchImageView) this.groupMembersView.findViewById(R.id.releaseShare);
		releaseShareView.setTag(R.id.tag_class, "share_release");

		roomTextView = (TextView) this.groupMembersView.findViewById(R.id.roomTime);

		groupCoverView = (TouchImageView) this.groupMembersView.findViewById(R.id.groupCover);
		groupCoverView.setTag(R.id.tag_class, "group_head");
		groupHeadView = (ImageView) this.groupMembersView.findViewById(R.id.group_head);
		groupHeadView.setTag(R.id.tag_class, "group_head");

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		// myScrollImageBody = new MyScrollImageBody();
		// myScrollImageBody.initialize(groupMembersListContentView);

		releaseChannelView = mainView.mInflater.inflate(R.layout.view_release_channels, null);
		releaseChannelContainer = releaseChannelView.findViewById(R.id.releaseChannelContainer);

		mImageFile = fileHandlers.sdcardHeadImageFolder;
		if (!mImageFile.exists())
			mImageFile.mkdirs();

		thisController.getUserCurrentAllGroup();

		showShareMessages();

		initReleaseShareDialogView();

		initializationGroupsDialog();

		businessCardPopView = new SmallBusinessCardPopView(mainView.thisActivity, mainView.main_container);

		showReleaseChannel();
	}

	public void getCurrentGroupShareMessages() {
		thisController.getCurrentGroupShareMessages();
	}

	public void setConver() {
		final Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		File file = new File(fileHandlers.sdcardBackImageFolder, group.conver);
		if (group.conver == null || "".equals(group.conver)) {
			imageLoader.displayImage("drawable://" + R.drawable.tempicon, groupCoverView);
			return;
		}
		final String path = file.getAbsolutePath();
		if (file.exists()) {
			imageLoader.displayImage("file://" + path, groupCoverView, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					downloadConver(group.conver, path);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				}
			});
		} else {
			if (group.conver != null) {
				downloadConver(group.conver, path);
			} else {
				imageLoader.displayImage("drawable://" + R.drawable.tempicon, groupCoverView);
			}
		}
	}

	public void downloadConver(String converName, String path) {
		groupCoverView.setTag("conver");
		String url = API.DOMAIN_COMMONIMAGE + "backgrounds/" + converName;
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = groupCoverView;
		downloadFile.setDownloadFileListener(thisController.downloadListener);
		downloadFileList.addDownloadFile(downloadFile);
	}

	public Group currentGroup;

	public void showRoomTime() {
		if (thisController.reflashStatus.state == thisController.reflashStatus.Reflashing) {
			roomTextView.setText("正在获取数据");
		} else if (thisController.reflashStatus.state == thisController.reflashStatus.Failed) {
			roomTextView.setText("刷新数据失败");
		} else {
			if (currentGroup != null) {
				parser.check();
				Share share = data.shares.shareMap.get(currentGroup.gid + "");
				if (share != null) {
					roomTextView.setText("上次刷新:" + DateUtil.getChatMessageListTime(share.updateTime));
				} else {
					roomTextView.setText("");
				}
			} else {
				roomTextView.setText("");
			}
		}
	}

	public ScrollListBody releaseChannelListBody;
	public boolean isShowChannel = true;

	public void showReleaseChannel() {
		// init
		releaseChannelListBody = new ScrollListBody();
		releaseChannelListBody.initialize(displayMetrics, releaseChannelContainer);
		// make
		String[] options = new String[] { "文本", "相册", "图文", "投票", "海报", "活动" };
		for (int i = 0; i < options.length; i++) {
			String key = options[i];
			// TODO
			ChannelBody body = new ChannelBody(releaseChannelListBody);
			body.initialize();
			body.setContent(key);

			int width = (int) (displayMetrics.density * 120);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
			body.itemHeight = width;

			this.releaseChannelListBody.containerView.addView(body.cardView, layoutParams);
			body.x = releaseChannelListBody.height;
			body.cardView.setX(body.x);
			body.cardView.setY(0);

			releaseChannelListBody.height = releaseChannelListBody.height + width;
		}
		this.releaseChannelListBody.containerHeight = (int) (this.displayMetrics.heightPixels - ViewManage.getStatusBarHeight(mainView.thisActivity));
		// FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels);
		// this.mainView.main_container.addView(releaseChannelView, params);
	}

	public class ChannelBody extends ScrollListItemBody {

		ChannelBody(ScrollListBody listBody) {
			listBody.super();
		}

		public View cardView;
		public TouchImageView imageView;
		public TouchTextView textView;

		public View initialize() {
			this.cardView = mainView.mInflater.inflate(R.layout.view_release_channel, null);
			this.imageView = (TouchImageView) cardView.findViewById(R.id.image);
			this.textView = (TouchTextView) cardView.findViewById(R.id.text);
			this.itemHeight = displayMetrics.density * 120;
			this.itemWidth = displayMetrics.density * 120;
			return this.cardView;
		}

		public void setContent(String text) {
			this.textView.setText(text);
			int backGround = R.drawable.reloption_bk_nor;
			int icon = R.drawable.comm_ico_menu_basic;
			this.imageView.setBackgroundResource(backGround);
			this.imageView.setImageResource(icon);
		}
	}

	public void showShareMessages() {
		data = parser.check();

		showGroupMembers();
		if (data.relationship.groups == null || data.localStatus.localData == null) {
			log.e("return groups or localData");
			return;
		}
		boolean flag0 = data.relationship.groups.contains(data.localStatus.localData.currentSelectedGroup);
		if (!flag0) {
			if (data.relationship.groups.size() == 0) {
				data.localStatus.localData.currentSelectedGroup = "";
				data.relationship.isModified = true;
			} else {
				data.localStatus.localData.currentSelectedGroup = data.relationship.groups.get(0);
			}
		}

		Share share = data.shares.shareMap.get(data.localStatus.localData.currentSelectedGroup);
		boolean flag = data.relationship.groups.contains(data.localStatus.localData.currentSelectedGroup);
		SharesMessageBody sharesMessageBody0 = null;
		if (flag) {
			sharesMessageBody0 = (SharesMessageBody) shareMessageListBody.listItemBodiesMap.get("message#" + "topBar");
			this.shareMessageListBody.listItemsSequence.clear();
			this.shareMessageListBody.containerView.removeAllViews();
			shareMessageView.removeAllViews();
			log.e("clear share list body.");
			this.shareMessageListBody.height = 0;
			if (sharesMessageBody0 == null) {
				sharesMessageBody0 = new SharesMessageBody(this.shareMessageListBody);
				sharesMessageBody0.initialize(-1);
				sharesMessageBody0.itemHeight = (280 - 48) * displayMetrics.density;
			}
			sharesMessageBody0.setContent(null, "", "", "", 0);
			this.shareMessageListBody.listItemsSequence.add("message#" + "topBar");
			this.shareMessageListBody.listItemBodiesMap.put("message#" + "topBar", sharesMessageBody0);
			RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (250 * displayMetrics.density));
			sharesMessageBody0.y = -48 * displayMetrics.density;
			sharesMessageBody0.cardView.setY(sharesMessageBody0.y);
			// sharesMessageBody0.cardView.setX(0);
			this.shareMessageListBody.height = this.shareMessageListBody.height + (215 - 48) * displayMetrics.density;
			this.shareMessageListBody.containerView.addView(sharesMessageBody0.cardView, layoutParams0);
		} else {
			this.shareMessageListBody.listItemsSequence.clear();
			this.shareMessageListBody.containerView.removeAllViews();
			shareMessageView.removeAllViews();
			this.shareMessageListBody.height = 0;
			log.e("clear share list body.");
			return;
		}
		if (!flag || share == null) {
			return;
		}
		// set conver
		// TODO conver setting
		currentGroup = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		fileHandlers.getHeadImage(currentGroup.icon, this.groupHeadView, viewManage.options56);
		if (currentGroup.conver != null && !currentGroup.conver.equals("")) {
			setConver();
		} else {
			imageLoader.displayImage("drawable://" + R.drawable.tempicon, groupCoverView);
		}

		showRoomTime();

		List<String> sharesOrder = share.shareMessagesOrder;
		Map<String, ShareMessage> sharesMap = share.shareMessagesMap;
		ShareMessage lastShareMessage = null;
		// int timeBarCount = 0;
		for (int i = 0; i < sharesOrder.size(); i++) {
			String key = sharesOrder.get(i);
			ShareMessage shareMessage = null;
			shareMessage = sharesMap.get(key);
			if (shareMessage == null) {
				continue;
			}
			if (!shareMessage.type.equals("imagetext")) {
				continue;
			}
			SharesMessageBody sharesMessageBody = null;
			// .........................
			String lastTime;
			if (lastShareMessage == null) {
				lastTime = "";
			} else {
				lastTime = DateUtil.formatYearMonthDay(lastShareMessage.time);
			}
			String nowTime = DateUtil.formatYearMonthDay(shareMessage.time);
			if (!nowTime.equals(lastTime)) {
				sharesMessageBody0 = new SharesMessageBody(this.shareMessageListBody);
				sharesMessageBody0.initialize(-2);
				sharesMessageBody0.setContent(shareMessage, "", "", "", 0);
				this.shareMessageListBody.listItemsSequence.add("message#" + "timeBar" + shareMessage.time);
				this.shareMessageListBody.listItemBodiesMap.put("message#" + "timeBar" + shareMessage.time, sharesMessageBody0);
				RelativeLayout.LayoutParams layoutParams_2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (40 * displayMetrics.density));
				sharesMessageBody0.y = this.shareMessageListBody.height;
				sharesMessageBody0.cardView.setY(sharesMessageBody0.y);
				sharesMessageBody0.cardView.setX(10 * displayMetrics.density);
				this.shareMessageListBody.height = this.shareMessageListBody.height + 50 * displayMetrics.density;
				this.shareMessageListBody.containerView.addView(sharesMessageBody0.cardView, layoutParams_2);
				// i--;
				lastShareMessage = shareMessage;
				// continue;
			}
			// .........................
			// log.e("message#---" + shareMessage.gsid);
			String keyName = "message#" + shareMessage.gsid;
			if (this.shareMessageListBody.listItemsSequence.contains(keyName)) {
				continue;
			}
			// Data duplication problem
			if (this.shareMessageListBody.listItemsSequence.contains(keyName)) {
				// continue;
			}
			boolean isExists = false;
			if (this.shareMessageListBody.listItemBodiesMap.get(keyName) != null) {
				sharesMessageBody = (SharesMessageBody) this.shareMessageListBody.listItemBodiesMap.get(keyName);
				isExists = true;
			} else {
				sharesMessageBody = new SharesMessageBody(this.shareMessageListBody);
				sharesMessageBody.initialize(i);
				this.shareMessageListBody.listItemBodiesMap.put(keyName, sharesMessageBody);
			}
			Friend friend = data.relationship.friendsMap.get(shareMessage.phone);
			this.shareMessageListBody.listItemsSequence.add(keyName);
			String fileName = "";
			if (friend != null) {
				fileName = friend.head;
			}
			int height10dp = (int) (10 * displayMetrics.density + 0.5f);
			int totalHeight = 0;
			if (!isExists) {
				ShareContent shareContent = gson.fromJson("{shareContentItems:" + shareMessage.content + "}", ShareContent.class);
				String textContent = "";
				String imageContent = "";
				List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
				B: for (int j = 0; j < shareContentItems.size(); j++) {
					ShareContentItem shareContentItem = shareContentItems.get(j);
					if (shareContentItem.type.equals("image")) {
						imageContent = shareContentItem.detail;
						if (!"".equals(textContent))
							break B;
					} else if (shareContentItem.type.equals("text")) {
						textContent = shareContentItem.detail;
						if (!"".equals(imageContent))
							break B;
					}
				}

				totalHeight = (int) (360 * displayMetrics.density + 0.5f);
				sharesMessageBody.shareTextContentView.setText(textContent);
				int widthMeasureSpec = 0, heightMeasureSpec = 0;
				sharesMessageBody.shareTextContentView.measure(widthMeasureSpec, heightMeasureSpec);

				float textSize = sharesMessageBody.shareTextContentView.getTextSize();
				int lineTextCount = (int) ((displayMetrics.widthPixels - 40 * displayMetrics.density) / textSize);
				int lineCount = sharesMessageBody.shareTextContentView.getLineCount();
				// String content = textContent;
				if (lineCount >= 5) {
					lineCount = 4;
					String firstContent = textContent.substring(0, 4 * lineTextCount);
					String[] n = firstContent.getBytes().toString().split("\n");
					int subPosition = 4 * lineTextCount;
					for (String str : n) {
						if (str.length() % lineTextCount != 0) {
							subPosition -= (lineTextCount - (str.length() % lineTextCount));
							subPosition += 2;
						} else {
							if (str.length() == 0) {
								subPosition -= lineTextCount;
							}
							subPosition += 2;
						}
					}
					// log.e(n.length + "--");
					String endContent = textContent.substring(subPosition);
					if (textContent.indexOf("1、") == 0) {
						log.e(textContent.substring(0, subPosition));
					}
					int indexN = endContent.indexOf("\n");
					int indexRN = endContent.indexOf("\r\n");
					if (indexN != -1 && indexRN != -1) {
						if (indexN > indexRN) {
							textContent = textContent.substring(0, subPosition) + endContent.substring(0, indexRN);
							lineCount += ((indexRN % lineTextCount) == 0 ? (indexRN / lineTextCount) : (indexRN / lineTextCount) + 1);
						} else {
							textContent = textContent.substring(0, subPosition) + endContent.substring(0, indexN);
							lineCount += ((indexN % lineTextCount) == 0 ? (indexN / lineTextCount) : (indexN / lineTextCount) + 1);
						}
					} else if (indexN != -1) {
						textContent = textContent.substring(0, subPosition) + endContent.substring(0, indexN);
						lineCount += ((indexN % lineTextCount) == 0 ? (indexN / lineTextCount) : (indexN / lineTextCount) + 1);
					} else if (indexRN != -1) {
						textContent = textContent.substring(0, subPosition) + endContent.substring(0, indexRN);
						lineCount += ((indexRN % lineTextCount) == 0 ? (indexRN / lineTextCount) : (indexRN / lineTextCount) + 1);
					} else {
						lineCount = sharesMessageBody.shareTextContentView.getLineCount();
					}
					if (indexRN != -1 || indexN != -1) {
						sharesMessageBody.shareTextContentView.setText(textContent);
						sharesMessageBody.shareTextContentView.measure(widthMeasureSpec, heightMeasureSpec);
						lineCount = sharesMessageBody.shareTextContentView.getLineCount();
					}
				}
				int lineHeight = sharesMessageBody.shareTextContentView.getLineHeight();
				if ("".equals(imageContent)) {
					totalHeight = (int) (70 * displayMetrics.density + 0.5f) + lineCount * lineHeight;
				} else if ("".equals(textContent)) {
					totalHeight = (int) (60 * displayMetrics.density + 0.5f + shareImageHeight);
				} else {
					totalHeight = (int) (75 * displayMetrics.density + 0.5f + shareImageHeight) + lineCount * lineHeight;
				}
				sharesMessageBody.setContent(shareMessage, fileName, imageContent, textContent, totalHeight);
			} else {
				sharesMessageBody.setContent(sharesMessageBody.message, sharesMessageBody.fileName, sharesMessageBody.imageContent, sharesMessageBody.textContent, sharesMessageBody.totalHeight);
				totalHeight = sharesMessageBody.totalHeight;
			}

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (displayMetrics.widthPixels - height10dp * 2), totalHeight);
			sharesMessageBody.y = this.shareMessageListBody.height;
			sharesMessageBody.cardView.setY(sharesMessageBody.y);
			sharesMessageBody.cardView.setX(height10dp);
			// sharesMessageBody.cardView.setX(0);
			// Why the object cache access to cheap 10dp view position
			if (isExists) {
				// sharesMessageBody.cardView.setX(10 * displayMetrics.density);
			}
			sharesMessageBody.itemHeight = totalHeight + height10dp;
			this.shareMessageListBody.height = this.shareMessageListBody.height + totalHeight + height10dp;
			this.shareMessageListBody.containerView.addView(sharesMessageBody.cardView, layoutParams);
			if (i == 0 && isShowFirstMessageAnimation) {
				shareMessageRootView = sharesMessageBody.cardView;
				dialogSpring.addListener(dialogSpringListener);
				dialogSpring.setCurrentValue(0);
				dialogSpring.setEndValue(1);
			}

			sharesMessageBody.cardView.setTag(R.id.tag_class, "share_view");
			sharesMessageBody.cardView.setTag("ShareMessageDetail#" + shareMessage.gsid);
			sharesMessageBody.cardView.setOnClickListener(thisController.mOnClickListener);
			sharesMessageBody.cardView.setOnTouchListener(thisController.mOnTouchListener);
		}

		this.isShowFirstMessageAnimation = false;

		this.shareMessageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
		if (this.shareMessageListBody.height < this.shareMessageListBody.containerHeight) {
			this.shareMessageListBody.y = 0;
		}
		this.shareMessageListBody.setChildrenPosition();
	}

	public class SharesMessageBody extends MyListItemBody {

		SharesMessageBody(ListBody1 listBody) {
			listBody.super();
		}

		public ViewGroup cardView = null;

		public ImageView headView;
		public TextView nickNameView;
		public TextView releaseTimeView;
		public TextView shareTextContentView;
		public ImageView shareImageContentView;
		public TextView sharePraiseNumberView;
		public ImageView sharePraiseIconView;
		public TextView shareCommentNumberView;
		public ImageView shareCommentIconView;
		public TextView shareStatusView;

		public TextView messageTimeView;

		public View background_share_item;

		public DownloadFile downloadFile = null;

		public ShareMessage message;

		public String fileName;

		public int totalHeight;
		public String imageContent;
		public String textContent;

		public int i;

		public ControlProgress controlProgress;
		public View controlProgressView;

		public View initialize(int i) {
			this.i = i;
			if (i == -1) {
				this.cardView = (ViewGroup) groupMembersView;
				// groupMembersListContentView = (RelativeLayout)
				// this.cardView.findViewById(R.id.groupMembersListContent);
				// releaseShareView = (ImageView)
				// this.cardView.findViewById(R.id.releaseShare);
			} else if (i == -2) {
				this.cardView = (ViewGroup) mainView.mInflater.inflate(R.layout.share_message_item_title, null);
				this.messageTimeView = (TextView) this.cardView.findViewById(R.id.releaseMessageTime);
			} else {
				this.cardView = (ViewGroup) mainView.mInflater.inflate(R.layout.share_message_item, null);
				this.headView = (ImageView) this.cardView.findViewById(R.id.share_head);
				this.nickNameView = (TextView) this.cardView.findViewById(R.id.share_nickName);
				this.releaseTimeView = (TextView) this.cardView.findViewById(R.id.share_releaseTime);
				this.shareTextContentView = (TextView) this.cardView.findViewById(R.id.share_textContent);
				this.shareImageContentView = (ImageView) this.cardView.findViewById(R.id.share_imageContent);
				this.sharePraiseNumberView = (TextView) this.cardView.findViewById(R.id.share_praise);
				this.sharePraiseIconView = (ImageView) this.cardView.findViewById(R.id.share_praise_icon);
				this.shareCommentNumberView = (TextView) this.cardView.findViewById(R.id.share_comment);
				this.shareCommentIconView = (ImageView) this.cardView.findViewById(R.id.share_comment_icon);

				this.shareStatusView = (TextView) this.cardView.findViewById(R.id.share_status);

				this.background_share_item = this.cardView.findViewById(R.id.background_share_item);

				// TODO
				// progress bar
				this.controlProgressView = this.cardView.findViewById(R.id.list_item_progress_container);

				this.shareTextContentView.setTextColor(Color.parseColor("#99000000"));

				int textWidth = (int) (displayMetrics.widthPixels - 40 * displayMetrics.density);
				this.shareTextContentView.setWidth(textWidth);
			}
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(ShareMessage shareMessage, String fileName, String imageContent, String textContent, int totalHeight) {
			data = parser.check();
			if (i == -1) {
				// showGroupMembers(groupMembersListContentView);
				groupMembersListContentView.setOnClickListener(thisController.mOnClickListener);
				groupMembersListContentView.setOnTouchListener(thisController.mOnTouchListener);
				releaseShareView.setOnClickListener(thisController.mOnClickListener);
				releaseShareView.setOnTouchListener(thisController.mOnTouchListener);

				groupHeadView.setOnClickListener(thisController.mOnClickListener);
				groupHeadView.setOnTouchListener(thisController.mOnTouchListener);
				groupCoverView.setOnClickListener(thisController.mOnClickListener);
				groupCoverView.setOnTouchListener(thisController.mOnTouchListener);
			} else if (i == -2) {
				this.messageTimeView.setText(DateUtil.formatYearMonthDay(shareMessage.time));
			} else {
				this.controlProgressView.setVisibility(View.GONE);
				if (shareMessage != null) {
					if (shareMessage.status != null) {
						if ("sending".equals(shareMessage.status)) {
							this.controlProgressView.setVisibility(View.VISIBLE);
							this.controlProgress = new ControlProgress();
							this.controlProgress.initialize(this.controlProgressView, displayMetrics);
							// this.controlProgress.moveTo(70);
						}
					}
				}
				this.message = shareMessage;
				this.fileName = fileName;
				this.totalHeight = totalHeight;
				this.imageContent = imageContent;
				this.textContent = textContent;

				if (shareMessage.status != null) {
					if ("sending".equals(shareMessage.status)) {
						shareStatusView.setText("发送中...");
						shareStatusView.setVisibility(View.VISIBLE);
					} else if ("failed".equals(shareMessage.status)) {
						shareStatusView.setText("发布失败");
						shareStatusView.setVisibility(View.VISIBLE);
					}
				}
				fileHandlers.getHeadImage(fileName, this.headView, viewManage.headOptions40);
				if (data.relationship.friendsMap.get(shareMessage.phone) == null) {
					this.nickNameView.setText(shareMessage.phone);
				} else {
					this.nickNameView.setText(data.relationship.friendsMap.get(shareMessage.phone).nickName);
				}
				this.headView.setTag("ShareMessage#" + shareMessage.phone);
				this.headView.setTag(R.id.tag_class, "share_head");
				this.headView.setOnClickListener(thisController.mOnClickListener);
				this.headView.setOnTouchListener(thisController.mOnTouchListener);
				this.releaseTimeView.setText(DateUtil.formatHourMinute(shareMessage.time));
				if (imageContent == null || textContent == null) {
					ShareContent shareContent = gson.fromJson("{shareContentItems:" + shareMessage.content + "}", ShareContent.class);
					textContent = "";
					imageContent = "";
					List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
					for (int i = 0; i < shareContentItems.size(); i++) {
						ShareContentItem shareContentItem = shareContentItems.get(i);
						if (shareContentItem.type.equals("image")) {
							imageContent = shareContentItem.detail;
							if (!"".equals(textContent))
								break;
						} else if (shareContentItem.type.equals("text")) {
							textContent = shareContentItem.detail;
							if (!"".equals(imageContent))
								break;
						}
					}
				}

				this.shareTextContentView.setText(textContent);

				if ("".equals(imageContent)) {
					// totalHeight = (int) (65 * displayMetrics.density + 0.5f) + lineCount * this.shareTextContentView.getLineHeight();
					RelativeLayout.LayoutParams params2 = (android.widget.RelativeLayout.LayoutParams) shareTextContentView.getLayoutParams();
					params2.topMargin = (int) (1 * displayMetrics.density + 0.5f);
					// this.shareTextContentView.setLines(5);
				} else if ("".equals(textContent)) {
					// this.shareTextContentView.setLines(4);
					// totalHeight = (int) (60 * displayMetrics.density + 0.5f + shareImageHeight);
				} else {
					// totalHeight = (int) (75 * displayMetrics.density + 0.5f + shareImageHeight) + lineCount * this.shareTextContentView.getLineHeight();
				}

				// shareImageContentView.setBackgroundResource(R.drawable.account_pop_black_background);

				FrameLayout.LayoutParams params = (LayoutParams) background_share_item.getLayoutParams();
				params.height = totalHeight;
				// this.shareTextContentView.setLines(lineCount);
				// this.shareTextContentView.setText(content);
				// if (content.indexOf("合伙人") == 0) {
				// log.e(this.shareTextContentView.getLineCount() + "----lines:" + lineCount);
				// }

				// File file = new File(fileHandlers.sdcardThumbnailFolder, imageContent);
				final int showImageWidth = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density + 120);
				final int showImageHeight = shareImageHeight;// (int)
																// (displayMetrics.density
																// * 200 +
																// 0.5f);
				RelativeLayout.LayoutParams shareImageParams = new RelativeLayout.LayoutParams(showImageWidth, showImageHeight);
				// int margin = (int) ((int) displayMetrics.density * 1 + 0.5f);
				shareImageContentView.setLayoutParams(shareImageParams);
				fileHandlers.getThumbleImage(imageContent, shareImageContentView, showImageWidth / 2, showImageHeight / 2, options, fileHandlers.THUMBLE_TYEP_GROUP, null);
				this.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
				this.shareCommentNumberView.setText(shareMessage.comments.size() + "");
				String userPhone = data.userInformation.currentUser.phone;
				if (shareMessage.praiseusers.contains(userPhone)) {
					this.sharePraiseIconView.setImageResource(R.drawable.praised_icon);
				} else {
					this.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
				}
				this.sharePraiseIconView.setTag("SharePraise#" + shareMessage.gsid);
				this.sharePraiseIconView.setTag(R.id.tag_class, "share_praise");
				this.sharePraiseIconView.setOnClickListener(thisController.mOnClickListener);
				this.sharePraiseIconView.setOnTouchListener(thisController.mOnTouchListener);
				List<Comment> comments = shareMessage.comments;
				this.shareCommentIconView.setImageResource(R.drawable.comment_icon);
				for (int i = 0; i < comments.size(); i++) {
					Comment comment = comments.get(i);
					if (comment.phone.equals(userPhone)) {
						this.shareCommentIconView.setImageResource(R.drawable.commented_icon);
						break;
					}
				}
			}
		}
	}

	public PopupWindow releaseSharePopWindow;

	public View releaseShareDialogView;
	public HorizontalScrollView dialogMainContentView;

	public TouchView releaseTextButton;
	public TouchView releaseAlbumButton;
	public TouchView releaseImageViewButton;

	public ShareSubController thisController;

	@SuppressWarnings("deprecation")
	public void initReleaseShareDialogView() {
		releaseShareDialogView = mainView.mInflater.inflate(R.layout.share_release_type_dialog, null);
		dialogMainContentView = (HorizontalScrollView) releaseShareDialogView.findViewById(R.id.dialogMainContent);
		releaseTextButton = (TouchView) releaseShareDialogView.findViewById(R.id.releaseTextShareButton);
		releaseTextButton.isIntercept = true;
		releaseAlbumButton = (TouchView) releaseShareDialogView.findViewById(R.id.releaseAlbumShareButton);
		releaseAlbumButton.isIntercept = true;
		releaseImageViewButton = (TouchView) releaseShareDialogView.findViewById(R.id.releaseImageTextShareButton);
		releaseImageViewButton.isIntercept = true;

		releaseTextButton.setOnClickListener(thisController.mOnClickListener);
		releaseAlbumButton.setOnClickListener(thisController.mOnClickListener);
		releaseImageViewButton.setOnClickListener(thisController.mOnClickListener);
		dialogMainContentView.setOnClickListener(thisController.mOnClickListener);
		releaseShareDialogView.setOnClickListener(thisController.mOnClickListener);
		// releaseVoiceTextButton.setOnClickListener(thisController.mOnClickListener);
		// releaseVoteButton.setOnClickListener(thisController.mOnClickListener);

		releaseSharePopWindow = new PopupWindow(releaseShareDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		releaseSharePopWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	public void showReleaseShareDialogView() {
		if (releaseSharePopWindow != null && !releaseSharePopWindow.isShowing()) {
			releaseSharePopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);
		}
	}

	public void dismissReleaseShareDialogView() {
		if (releaseSharePopWindow != null && releaseSharePopWindow.isShowing()) {
			releaseSharePopWindow.dismiss();
		}
	}

	public ViewGroup pop_out_background1;
	public ViewGroup pop_out_background2;

	public void initializationGroupsDialog() {
		groupDialogView = (TouchView) mainView.mInflater.inflate(R.layout.share_group_select_dialog, null, false);
		// groupDialogView.isIntercept = true;

		groupDialogView.setTag(R.id.tag_class, "group_view");
		// groupPopWindow = new PopupWindow(groupDialogView,
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		// groupPopWindow.setBackgroundDrawable(new BitmapDrawable());
		// groupPopWindow.setOutsideTouchable(true);

		pop_out_background1 = (ViewGroup) groupDialogView.findViewById(R.id.pop_out_background1);
		pop_out_background2 = (ViewGroup) groupDialogView.findViewById(R.id.pop_out_background2);

		groupManageView = groupDialogView.findViewById(R.id.groups_manage);
		groupManageView.setTag(R.id.tag_class, "group_setting");
		groupListButtonView = groupDialogView.findViewById(R.id.groupListButton);
		createGroupButtonView = groupDialogView.findViewById(R.id.createGroupButton);
		findMoreGroupButtonView = groupDialogView.findViewById(R.id.findMoreButton);
		groupsManageButtons = groupDialogView.findViewById(R.id.groups_manage_buttons);

		TouchView mainContentView = (TouchView) groupDialogView;
		groupsDialogContent = (TouchView) groupDialogView.findViewById(R.id.groupsContent);

		panelWidth = (int) (displayMetrics.widthPixels * 0.7578125f);
		panelHeight = (int) (displayMetrics.heightPixels * 0.7578125f);

		TouchView.LayoutParams mainContentParams = new TouchView.LayoutParams(panelWidth, panelHeight);

		mainContentView.setLayoutParams(mainContentParams);
		groupListBody = new ListBody1();
		groupListBody.initialize(displayMetrics, groupsDialogContent);
		setGroupsDialogContent();
		// groupsDialogContent.setOnClickListener(thisController.mOnClickListener);
		// groupsDialogContent.setOnTouchListener(thisController.mOnTouchListener);
	}

	public boolean isShowGroupDialog = false;

	public void showGroupsDialog() {
		if (!isShowGroupDialog) {
			if (data.relationship.groups.size() == 0) {
				if (groupsManageButtons.getVisibility() == View.GONE) {
					groupsManageButtons.setVisibility(View.VISIBLE);
				}
			} else {
				if (groupsManageButtons.getVisibility() == View.VISIBLE) {
					groupsManageButtons.setVisibility(View.GONE);
				}
			}
			groupListBody.active();
			shareMessageListBody.inActive();
			mainView.mainPagerBody.inActive();
			// groupPopWindow.showAtLocation(mainView.main_container,
			// Gravity.CENTER, 0, 0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			mainView.main_container.addView(this.groupDialogView, layoutParams);
			isShowGroupDialog = true;
		}
	}

	public void dismissGroupDialog() {
		if (isShowGroupDialog) {
			groupListBody.inActive();
			shareMessageListBody.active();
			mainView.mainPagerBody.active();
			// groupPopWindow.dismiss();
			mainView.main_container.removeView(this.groupDialogView);
			isShowGroupDialog = false;
		}
	}

	public void setGroupsDialogContent() {
		data = parser.check();
		if (data.relationship.groups == null || data.localStatus.localData == null) {
			log.e("return groups or localData");
			return;
		}
		boolean flag = data.relationship.groups.contains(data.localStatus.localData.currentSelectedGroup);
		if (!flag) {
			if (data.relationship.groups.size() == 0) {
				data.localStatus.localData.currentSelectedGroup = "";
				data.relationship.isModified = true;
			} else {
				data.localStatus.localData.currentSelectedGroup = data.relationship.groups.get(0);
			}
		}
		Group group0 = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		if (group0 != null) {
			this.shareTopMenuGroupName.setText(group0.name);
		} else {
			this.shareTopMenuGroupName.setText("暂无房间");
		}

		List<String> groups = data.relationship.groups;
		Map<String, Group> groupsMap = data.relationship.groupsMap;
		groupsDialogContent.removeAllViews();
		this.groupListBody.height = 0;
		groupListBody.listItemsSequence.clear();
		for (int i = 0; i < groups.size(); i++) {
			// boolean a = groups.get(i) == "1765";
			// log.e(a + "--------" + groups.get(i) + "---" +
			// groupsMap.get("1765"));
			Group group = groupsMap.get(groups.get(i));
			if (group == null) {
				continue;
			}
			String key = "group#" + group.gid + "_" + group.name;
			GroupDialogItem groupDialogItem;
			View view = null;
			if (groupListBody.listItemBodiesMap.get(key) != null) {
				groupDialogItem = (GroupDialogItem) groupListBody.listItemBodiesMap.get(key);
				view = groupDialogItem.cardView;
			} else {
				groupDialogItem = new GroupDialogItem(this.groupListBody);
				view = groupDialogItem.initialize();
				groupListBody.listItemBodiesMap.put(key, groupDialogItem);
			}
			groupListBody.listItemsSequence.add(key);
			groupDialogItem.setContent(group);
			// groupDialogItem.setViewLayout();

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 60), (int) (60 * displayMetrics.density));
			groupDialogItem.y = this.groupListBody.height;
			groupDialogItem.cardView.setY(groupDialogItem.y);
			groupDialogItem.cardView.setX(0);
			this.groupListBody.height = this.groupListBody.height + 60 * displayMetrics.density;
			this.groupListBody.containerView.addView(groupDialogItem.cardView, layoutParams);

			// onclick
			view.setTag("GroupDialogContentItem#" + group.gid);
			view.setTag(R.id.shareTopMenuGroupName, shareTopMenuGroupName);
			// listener
			view.setTag(R.id.tag_class, "group_view");
			view.setTag(R.id.tag_first, group);
			view.setOnClickListener(thisController.mOnClickListener);
			view.setOnTouchListener(thisController.mOnTouchListener);

			Log.v(tag, "this.friendListBody.height: " + this.groupListBody.height + "    circleBody.y:  " + groupDialogItem.y);
		}
		this.groupListBody.containerHeight = (int) (displayMetrics.heightPixels * 0.6578125f);

	}

	public void modifyCurrentShowGroup() {
		List<String> listItemsSequence = groupListBody.listItemsSequence;
		Map<String, MyListItemBody> listItemsSequenceMap = groupListBody.listItemBodiesMap;
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			GroupDialogItem body = (GroupDialogItem) listItemsSequenceMap.get(key);
			body.setViewLayout();
		}
	}

	public class GroupDialogItem extends MyListItemBody {
		GroupDialogItem(ListBody1 listBody) {
			listBody.super();
		}

		public View cardView;

		public ImageView groupIconView;
		public TextView groupNameView;
		public ImageView groupSelectedStatusView;

		public ImageView gripCardBackground;

		public Group group;

		public View initialize() {
			this.cardView = mainView.mInflater.inflate(R.layout.share_group_select_dialog_item, null);
			this.groupIconView = (ImageView) this.cardView.findViewById(R.id.groupIcon);
			this.groupNameView = (TextView) this.cardView.findViewById(R.id.groupName);
			this.groupSelectedStatusView = (ImageView) this.cardView.findViewById(R.id.groupSelectedStatus);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Group group) {
			data = parser.check();
			this.group = group;
			fileHandlers.getHeadImage(group.icon, this.groupIconView, viewManage.headOptions40);
			this.groupNameView.setText(group.name);
			if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
				this.groupSelectedStatusView.setVisibility(View.VISIBLE);
			} else {
				this.groupSelectedStatusView.setVisibility(View.GONE);
			}
			this.itemHeight = 60 * displayMetrics.density;
		}

		public void setViewLayout() {
			data = parser.check();
			if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
				this.groupSelectedStatusView.setVisibility(View.VISIBLE);
				this.groupNameView.setText(group.name);
			} else {
				this.groupSelectedStatusView.setVisibility(View.GONE);
			}
		}
	}

	// public MyScrollImageBody myScrollImageBody;
	public int width;
	public File mImageFile;

	public void showGroupMembers() {
		data = parser.check();
		groupMembersListContentView.removeAllViews();
		if (data.relationship.groupsMap == null || data.localStatus.localData == null) {
			return;
		}
		Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		if (group != null) {
			shareTopMenuGroupName.setText(group.name);
		} else {
			shareTopMenuGroupName.setText("暂无房间");
			return;
		}

		width = (int) (displayMetrics.density * 40);
	}

	public void onResume() {
	}

	public View shareMessageRootView;

	private class DialogShowSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) spring.getCurrentValue();
			shareMessageRootView.setScaleX(mappedValue);
			shareMessageRootView.setScaleY(mappedValue);
		}
	}
}
