package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.TouchImageView;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.SquareSubController;
import com.open.welinks.customView.ControlProgress;
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

public class SquareSubView {

	public Data data = Data.getInstance();

	public String tag = "SquareSubView";

	public SquareSubController thisController;

	public MyLog log = new MyLog(tag, true);

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	// share
	public RelativeLayout squareView;

	public ViewGroup squareMessageView;
	public ListBody1 squareMessageListBody;

	public RelativeLayout shareTitleView;
	public ImageView leftImageButton;
	public RelativeLayout squareTopMenuGroupNameParent;
	public TextView squareTopMenuSquareName;

	// group
	// public PopupWindow groupPopWindow;
	// pop layout
	public TouchView squareDialogView;

	public ViewGroup groupsDialogContent;

	public ListBody1 squaresListBody;

	// share top Bar child view
	// public View groupMembersView;
	// public RelativeLayout groupMembersListContentView;
	// public ImageView releaseShareView;

	public View groupManageView;
	public View groupsManageButtons;
	public View groupListButtonView;
	public View createGroupButtonView;
	public View findMoreGroupButtonView;

	public int shareImageHeight;

	public float imageHeightScale = 0.5686505598114319f;

	public float panelScale = 1.010845986984816f;

	public int panelHeight;
	public int panelWidth;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();
	public Parser parser = Parser.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public boolean isShowFirstMessageAnimation = false;

	public LayoutInflater mInflater;

	public int showImageWidth;
	public int showImageHeight;

	// share top Bar child view
	public View groupMembersView;
	public ViewGroup groupMembersListContentView;
	public ImageView releaseShareView;

	public ImageView groupCoverView;
	public ImageView groupHeadView;

	public Group currentSquare;

	public SquareSubView(MainView mainView) {
		this.mainView = mainView;
		viewManage.squareSubView = this;
	}

	TextView titleName;
	public SmallBusinessCardPopView businessCardPopView;

	public ControlProgress controlProgress;
	public View controlProgressView;

	public void initViews() {

		this.squareView = mainView.squareView;
		this.displayMetrics = mainView.displayMetrics;

		this.showImageWidth = displayMetrics.widthPixels;
		this.showImageHeight = (int) (115 * displayMetrics.density + 0.5f);

		this.mInflater = mainView.mInflater;

		shareImageHeight = (int) (this.displayMetrics.widthPixels * imageHeightScale);
		panelHeight = (int) (this.displayMetrics.widthPixels * panelScale);

		squareMessageView = (ViewGroup) squareView.findViewById(R.id.squareContainer);

		squareMessageListBody = new ListBody1();
		squareMessageListBody.initialize(displayMetrics, squareMessageView);

		shareTitleView = (RelativeLayout) squareView.findViewById(R.id.title_square);
		shareTitleView.setTag(R.id.tag_class, "title_share");
		leftImageButton = (ImageView) squareView.findViewById(R.id.leftImageButton);
		squareTopMenuGroupNameParent = (RelativeLayout) squareView.findViewById(R.id.shareTopMenuGroupNameParent);
		squareTopMenuSquareName = (TextView) squareView.findViewById(R.id.shareTopMenuSquareName);

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		data = parser.check();

		try {
			currentSquare = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedSquare);
			if (currentSquare != null) {
				this.squareTopMenuSquareName.setText(currentSquare.name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// square cover layout
		this.groupMembersView = mainView.mInflater.inflate(R.layout.share_group_members_show, null);
		titleName = (TextView) this.groupMembersView.findViewById(R.id.share_praise);
		titleName.setText("微型社区广播");
		groupMembersListContentView = (ViewGroup) this.groupMembersView.findViewById(R.id.groupMembersListContent);
		groupMembersListContentView.setTag(R.id.tag_class, "group_members");
		releaseShareView = (TouchImageView) this.groupMembersView.findViewById(R.id.releaseShare);
		releaseShareView.setTag(R.id.tag_class, "share_release");

		groupCoverView = (TouchImageView) this.groupMembersView.findViewById(R.id.groupCover);
		groupCoverView.setTag(R.id.tag_class, "group_head");
		groupHeadView = (ImageView) this.groupMembersView.findViewById(R.id.group_head);
		groupHeadView.setTag(R.id.tag_class, "group_head");
		bigHeadOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(56)).build();

		businessCardPopView = new SmallBusinessCardPopView(mainView.thisActivity, mainView.main_container);

		// progress
		this.controlProgressView = squareView.findViewById(R.id.title_control_progress_container);
		this.controlProgress = new ControlProgress();
		this.controlProgress.initialize(this.controlProgressView, displayMetrics);
		this.controlProgress.moveTo(0);

		initData();
		showSquareMessages(true);
		initReleaseShareDialogView();
		initializationSquaresDialog();
	}

	int screenHeight;
	int screenWidth;
	public int width1;

	public float imageHeightScale1 = 0.7586206896551724f;
	public int imageHeight;

	private void initData() {
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;
		width = (int) (screenWidth - (displayMetrics.density * 20 + 0.5f));
		imageHeight = (int) (width * imageHeightScale);
		width1 = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density - 0.5f);
	}

	public void setConver() {
		final Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedSquare);
		File file = new File(fileHandlers.sdcardBackImageFolder, group.conver);
		final String path = file.getAbsolutePath();
		// log.e(file.exists() + "---exists" + group.conver);
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
				imageLoader.displayImage("drawable://" + R.drawable.login_background_1, groupCoverView);
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

	public DisplayImageOptions bigHeadOptions;

	public void showSquareMessages(boolean flag) {
		showSquareMessages2(flag);
	}

	public void showSquareMessages1() {

		this.squareMessageListBody.listItemsSequence.clear();
		this.squareMessageListBody.containerView.removeAllViews();
		this.squareMessageListBody.height = 10 * displayMetrics.density;

		data = parser.check();
		if (data.shares.shareMap == null || data.localStatus.localData == null) {
			return;
		}
		Share share = data.shares.shareMap.get(data.localStatus.localData.currentSelectedSquare);
		if (share == null)
			return;

		SharesMessageBody sharesMessageBody0 = null;
		sharesMessageBody0 = (SharesMessageBody) squareMessageListBody.listItemBodiesMap.get("message#" + "topBar");
		this.squareMessageListBody.listItemsSequence.clear();
		this.squareMessageListBody.containerView.removeAllViews();
		// shareMessageView.removeAllViews();
		log.e("clear square list body.");
		this.squareMessageListBody.height = 0;
		if (sharesMessageBody0 == null) {
			sharesMessageBody0 = new SharesMessageBody(this.squareMessageListBody);
			sharesMessageBody0.initialize(-1);
			sharesMessageBody0.itemHeight = (280 - 48) * displayMetrics.density;
		}
		sharesMessageBody0.setContent(null, "");
		this.squareMessageListBody.listItemsSequence.add("message#" + "topBar");
		this.squareMessageListBody.listItemBodiesMap.put("message#" + "topBar", sharesMessageBody0);
		RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (250 * displayMetrics.density));
		sharesMessageBody0.y = -48 * displayMetrics.density;
		sharesMessageBody0.cardView.setY(sharesMessageBody0.y);
		// sharesMessageBody0.cardView.setX(0);
		this.squareMessageListBody.height = this.squareMessageListBody.height + (215) * displayMetrics.density;// 215 - 48
		this.squareMessageListBody.containerView.addView(sharesMessageBody0.cardView, layoutParams0);

		Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedSquare);
		if (group.conver != null && !group.conver.equals("")) {
			setConver();
		} else {
			imageLoader.displayImage("drawable://" + R.drawable.login_background_1, groupCoverView);
		}

		fileHandlers.getHeadImage(group.icon, this.groupHeadView, bigHeadOptions);
		titleName.setText(group.name);

		List<String> sharesOrder = share.shareMessagesOrder;
		Map<String, ShareMessage> sharesMap = share.shareMessagesMap;
		for (int i = 0; i < sharesOrder.size(); i++) {
			String key = sharesOrder.get(i);
			ShareMessage shareMessage = null;
			shareMessage = sharesMap.get(key);
			if (!shareMessage.type.equals("imagetext")) {
				continue;
			}
			SharesMessageBody sharesMessageBody = null;

			String keyName = "message#" + shareMessage.gsid;
			if (this.squareMessageListBody.listItemBodiesMap.get(keyName) != null) {
				sharesMessageBody = (SharesMessageBody) this.squareMessageListBody.listItemBodiesMap.get(keyName);
			} else {
				sharesMessageBody = new SharesMessageBody(this.squareMessageListBody);
				sharesMessageBody.initialize(i);
				this.squareMessageListBody.listItemBodiesMap.put(keyName, sharesMessageBody);
			}
			// Friend friend =
			// data.relationship.friendsMap.get(shareMessage.phone);
			this.squareMessageListBody.listItemsSequence.add(keyName);

			Friend friend = data.relationship.friendsMap.get(shareMessage.phone);
			String fileName = "";
			if (friend != null) {
				fileName = friend.head;
			}
			sharesMessageBody.setContent(shareMessage, fileName);

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (210 * displayMetrics.density));
			sharesMessageBody.y = this.squareMessageListBody.height;
			sharesMessageBody.cardView.setY(sharesMessageBody.y);
			sharesMessageBody.cardView.setX(displayMetrics.density * 10);
			sharesMessageBody.itemHeight = 210 * displayMetrics.density;
			this.squareMessageListBody.height = this.squareMessageListBody.height + 210 * displayMetrics.density;

			this.squareMessageListBody.containerView.addView(sharesMessageBody.cardView, layoutParams);

			sharesMessageBody.cardView.setTag(R.id.tag_class, "share_view");
			sharesMessageBody.cardView.setTag("ShareMessageDetail#" + shareMessage.gsid);
			sharesMessageBody.cardView.setOnClickListener(thisController.mOnClickListener);
			sharesMessageBody.cardView.setOnTouchListener(thisController.mOnTouchListener);
		}

		this.squareMessageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
		squareMessageListBody.setChildrenPosition();
	}

	public class SharesMessageBody extends MyListItemBody {

		SharesMessageBody(ListBody1 listBody) {
			listBody.super();
		}

		public View cardView;

		public ImageView headView;
		public TextView nickNameView;
		public TextView releaseTimeView;
		public TextView shareTextContentView;
		public ImageView shareImageContentView;
		public TextView sharePraiseNumberView;
		public ImageView sharePraiseIconView;
		public TextView shareCommentNumberView;
		public ImageView shareCommentIconView;

		public DownloadFile downloadFile = null;

		public ShareMessage message;

		public String fileName;

		public int i;
		public String option;

		public View initialize(int i) {
			this.i = i;
			if (i == -1) {
				this.cardView = (ViewGroup) groupMembersView;
			} else {
				this.cardView = mInflater.inflate(R.layout.square_message_item_left, null);

				this.headView = (ImageView) this.cardView.findViewById(R.id.share_head);
				this.nickNameView = (TextView) this.cardView.findViewById(R.id.share_nickName);
				this.releaseTimeView = (TextView) this.cardView.findViewById(R.id.share_releaseTime);
				this.shareTextContentView = (TextView) this.cardView.findViewById(R.id.share_textContent);
				this.shareImageContentView = (ImageView) this.cardView.findViewById(R.id.share_imageContent);
				this.sharePraiseNumberView = (TextView) this.cardView.findViewById(R.id.share_praise);
				this.sharePraiseIconView = (ImageView) this.cardView.findViewById(R.id.share_praise_icon);
				this.shareCommentNumberView = (TextView) this.cardView.findViewById(R.id.share_comment);
				this.shareCommentIconView = (ImageView) this.cardView.findViewById(R.id.share_comment_icon);

				this.itemHeight = 350 * displayMetrics.density;
			}
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(ShareMessage shareMessage, String fileName) {
			if (i == -1) {
				releaseShareView.setOnClickListener(thisController.mOnClickListener);
				releaseShareView.setOnTouchListener(thisController.mOnTouchListener);
				groupHeadView.setOnClickListener(thisController.mOnClickListener);
				groupHeadView.setOnTouchListener(thisController.mOnTouchListener);
				groupCoverView.setOnClickListener(thisController.mOnClickListener);
				groupCoverView.setOnTouchListener(thisController.mOnTouchListener);
			} else {
				data = parser.check();

				this.message = shareMessage;
				this.fileName = fileName;
				fileHandlers.getHeadImage(fileName, this.headView, headOptions);
				if (data.relationship.friendsMap.get(shareMessage.phone) == null) {
					this.nickNameView.setText(shareMessage.phone);
				} else {
					this.nickNameView.setText(data.relationship.friendsMap.get(shareMessage.phone).nickName);
				}
				this.releaseTimeView.setText(DateUtil.formatHourMinute(shareMessage.time));
				ShareContent shareContent = gson.fromJson("{shareContentItems:" + shareMessage.content + "}", ShareContent.class);
				String textContent = "";
				String imageContent = "";
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

				this.shareTextContentView.setText(textContent);
				File file = new File(fileHandlers.sdcardThumbnailFolder, imageContent);
				final int showImageWidth = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density + 120);
				final int showImageHeight = (int) (displayMetrics.density * 136);

				FrameLayout.LayoutParams shareImageParams = new FrameLayout.LayoutParams(showImageWidth, showImageHeight);
				// int margin = (int) ((int) displayMetrics.density * 1 + 0.5f);
				shareImageContentView.setLayoutParams(shareImageParams);
				if (!imageContent.equals("")) {
					final String url = API.DOMAIN_OSS_THUMBNAIL + "images/" + imageContent + "@" + showImageWidth / 2 + "w_" + showImageHeight / 2 + "h_1c_1e_100q";
					final String path = file.getAbsolutePath();
					if (file.exists()) {
						imageLoader.displayImage("file://" + path, shareImageContentView, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								downloadFile = new DownloadFile(url, path);
								downloadFile.view = shareImageContentView;
								downloadFile.view.setTag("image");
								downloadFile.setDownloadFileListener(thisController.downloadListener);
								downloadFileList.addDownloadFile(downloadFile);
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								int height = showImageHeight;
								FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(showImageWidth, height);
								shareImageContentView.setLayoutParams(params);
							}
						});
					} else {
						File file2 = new File(fileHandlers.sdcardImageFolder, imageContent);
						final String path2 = file2.getAbsolutePath();
						if (file2.exists()) {
							imageLoader.displayImage("file://" + path2, shareImageContentView, options);
						}
						downloadFile = new DownloadFile(url, path);
						downloadFile.view = shareImageContentView;
						downloadFile.view.setTag("image");
						downloadFile.setDownloadFileListener(thisController.downloadListener);
						downloadFileList.addDownloadFile(downloadFile);
					}
				}

				this.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
				this.shareCommentNumberView.setText(shareMessage.comments.size() + "");
				String userPhone = data.userInformation.currentUser.phone;
				if (shareMessage.praiseusers.contains(userPhone)) {
					this.sharePraiseIconView.setImageResource(R.drawable.praised_icon);
				} else {
					this.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
				}
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

	ArrayList<String> notFriends = new ArrayList<String>();

	public void showSquareMessages2(boolean flag) {
		notFriends.clear();
		// flag = true;
		// this.squareMessageListBody.listItemsSequence.clear();
		// this.squareMessageListBody.containerView.removeAllViews();
		// this.squareMessageListBody.height = 10 * displayMetrics.density;

		data = parser.check();
		if (data.shares.shareMap == null || data.localStatus.localData == null) {
			log.e("return shareMap or localData");
			return;
		}
		Share share = data.shares.shareMap.get(data.localStatus.localData.currentSelectedSquare);
		if (share == null) {
			log.e("return square share");
			return;
		}

		SharesMessageBody1 sharesMessageBody0 = null;
		sharesMessageBody0 = (SharesMessageBody1) squareMessageListBody.listItemBodiesMap.get("message#" + "topBar");
		this.squareMessageListBody.listItemsSequence.clear();
		this.squareMessageListBody.containerView.removeAllViews();
		// shareMessageView.removeAllViews();
		log.e("clear square list body.");

		this.squareMessageListBody.height = 0;
		if (flag) {
			this.squareMessageListBody.y = 0;
		}
		boolean isNewTitle = false;
		if (sharesMessageBody0 == null) {
			isNewTitle = true;
			sharesMessageBody0 = new SharesMessageBody1(this.squareMessageListBody);
			sharesMessageBody0.initialize(-1);
			sharesMessageBody0.itemHeight = (280 - 48) * displayMetrics.density;
		}
		sharesMessageBody0.setContent(null, "", null, null, null, isNewTitle);
		this.squareMessageListBody.listItemsSequence.add("message#" + "topBar");
		this.squareMessageListBody.listItemBodiesMap.put("message#" + "topBar", sharesMessageBody0);
		RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (250 * displayMetrics.density));
		sharesMessageBody0.y = -48 * displayMetrics.density;
		sharesMessageBody0.cardView.setY(sharesMessageBody0.y);
		// sharesMessageBody0.cardView.setX(0);
		this.squareMessageListBody.height = this.squareMessageListBody.height + (215) * displayMetrics.density;// 215 - 48
		this.squareMessageListBody.containerView.addView(sharesMessageBody0.cardView, layoutParams0);

		// imageLoader.displayImage("drawable://" + R.drawable.login_background_1, groupCoverView);
		Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedSquare);
		if (group.conver != null && !group.conver.equals("")) {
			setConver();
		} else {
			imageLoader.displayImage("drawable://" + R.drawable.login_background_1, groupCoverView);
		}

		titleName.setText(group.name);
		fileHandlers.getHeadImage(group.icon, this.groupHeadView, bigHeadOptions);

		List<String> sharesOrder = share.shareMessagesOrder;
		Map<String, ShareMessage> sharesMap = share.shareMessagesMap;
		// float total = sharesOrder.size() + 1;
		// this.controlProgress.setTo(0);
		// this.controlProgress.moveTo((int) ((1 / total) * 100));
		A: for (int i = 0; i < sharesOrder.size(); i++) {// sharesOrder.size()
			String key = sharesOrder.get(i);
			ShareMessage shareMessage = null;
			shareMessage = sharesMap.get(key);
			if (shareMessage == null || !"imagetext".equals(shareMessage.type)) {
				continue A;
			}
			SharesMessageBody1 sharesMessageBody = null;

			String keyName = "message#" + shareMessage.gsid;
			if (this.squareMessageListBody.listItemsSequence.contains(keyName)) {
				continue A;
			}
			boolean isNew = false;
			if (this.squareMessageListBody.listItemBodiesMap.get(keyName) != null) {
				sharesMessageBody = (SharesMessageBody1) this.squareMessageListBody.listItemBodiesMap.get(keyName);
			} else {
				isNew = true;
				sharesMessageBody = new SharesMessageBody1(this.squareMessageListBody);
				sharesMessageBody.initialize(i);
				this.squareMessageListBody.listItemBodiesMap.put(keyName, sharesMessageBody);
			}

			Friend friend = data.relationship.friendsMap.get(shareMessage.phone);
			String fileName = "";
			if (friend != null) {
				fileName = friend.head;
			}

			boolean isExistsImage = false;
			int cHeight = imageHeight;
			ShareContent shareContent = gson.fromJson("{shareContentItems:" + shareMessage.content + "}", ShareContent.class);
			List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
			String textContent = "";
			String imageContent = "";
			B: for (int j = 0; j < shareContentItems.size(); j++) {
				ShareContentItem shareContentItem = shareContentItems.get(j);
				if (shareContentItem.type.equals("image")) {
					imageContent = shareContentItem.detail;
					isExistsImage = true;
					if (!"".equals(textContent))
						break B;
				} else if (shareContentItem.type.equals("text")) {
					textContent = shareContentItem.detail;
					if (!"".equals(imageContent))
						break B;
				}
			}
			if (isNew) {
				sharesMessageBody.setContent(shareMessage, fileName, shareContent, textContent, imageContent, isNew);
			} else {
				sharesMessageBody.setContent(shareMessage, fileName, shareContent, textContent, imageContent, isNew);
			}

			if (!isExistsImage) {
				cHeight = 0;
			}

			int textHeigth1 = (int) (displayMetrics.density * 90 + 0.5);
			int totalHeight = (int) (displayMetrics.density * 100 + 0.5f + cHeight);
			if (cHeight == 0) {
				totalHeight = (int) (displayMetrics.density * 110 + 0.5f + textHeigth1);
			}
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width1, totalHeight);
			sharesMessageBody.itemHeight = totalHeight;

			this.squareMessageListBody.containerView.addView(sharesMessageBody.cardView, layoutParams);
			sharesMessageBody.y = squareMessageListBody.height;
			sharesMessageBody.cardView.setY(sharesMessageBody.y);
			sharesMessageBody.cardView.setX(displayMetrics.density * 10 + 0.5f);

			squareMessageListBody.height = squareMessageListBody.height + totalHeight;

			this.squareMessageListBody.listItemsSequence.add(keyName);

			sharesMessageBody.cardView.setTag(R.id.tag_class, "share_view");
			sharesMessageBody.cardView.setTag("ShareMessageDetail#" + shareMessage.gsid);
			sharesMessageBody.cardView.setOnClickListener(thisController.mOnClickListener);
			sharesMessageBody.cardView.setOnTouchListener(thisController.mOnTouchListener);
			// this.controlProgress.moveTo((int) (((1 + i + 1) / total) * 100));
			// log.e((int) (((1 + i + 1) / total) * 100) + "-----progress");
		}

		this.squareMessageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
		this.squareMessageListBody.setChildrenPosition();

		thisController.scanUserCard(gson.toJson(notFriends));
	}

	public class SharesMessageBody1 extends MyListItemBody {

		SharesMessageBody1(ListBody1 listBody) {
			listBody.super();
			instance = this;
		}

		public View cardView;

		public ImageView headView;
		public TextView nickNameView;
		public TextView releaseTimeView;
		public TextView shareTextContentView;
		public ImageView shareImageContentView;
		public TextView sharePraiseNumberView;
		public ImageView sharePraiseIconView;
		public TextView shareCommentNumberView;
		public ImageView shareCommentIconView;
		public View buttonBar;
		public TextView shareStatusView;

		public View praiseAreaView;

		public DownloadFile downloadFile = null;

		public ShareMessage message;

		public String fileName;

		public int i;
		public String option;

		public SharesMessageBody1 instance;

		public View initialize(int i) {
			this.i = i;
			if (i == -1) {
				this.cardView = (ViewGroup) groupMembersView;
			} else {
				this.cardView = mInflater.inflate(R.layout.square_message_item, null);

				this.headView = (ImageView) this.cardView.findViewById(R.id.squareHead);
				this.nickNameView = (TextView) this.cardView.findViewById(R.id.share_nickName);
				this.releaseTimeView = (TextView) this.cardView.findViewById(R.id.share_releaseTime);
				this.shareTextContentView = (TextView) this.cardView.findViewById(R.id.contentText);
				this.shareImageContentView = (ImageView) this.cardView.findViewById(R.id.contentImage);
				this.sharePraiseNumberView = (TextView) this.cardView.findViewById(R.id.share_praise);
				this.sharePraiseIconView = (ImageView) this.cardView.findViewById(R.id.share_praise_icon);
				this.shareCommentNumberView = (TextView) this.cardView.findViewById(R.id.share_comment);
				this.shareCommentIconView = (ImageView) this.cardView.findViewById(R.id.share_comment_icon);
				this.buttonBar = this.cardView.findViewById(R.id.buttonBar);
				this.praiseAreaView = this.cardView.findViewById(R.id.praise_area);

				shareTextContentView.setBackgroundColor(Color.parseColor("#38000000"));
				shareTextContentView.setTextColor(Color.WHITE);

				this.shareStatusView = (TextView) this.cardView.findViewById(R.id.share_status);

				View mainContainer = cardView.findViewById(R.id.mainContainer);
				FrameLayout.LayoutParams params = (LayoutParams) mainContainer.getLayoutParams();
				if (i != 0) {
					params.topMargin = (int) (displayMetrics.density * 10 + 0.5f);
				}
				params.bottomMargin = (int) (displayMetrics.density * 10 + 0.5f);

				FrameLayout.LayoutParams imageParams = (LayoutParams) shareImageContentView.getLayoutParams();
				imageParams.topMargin = (int) (displayMetrics.density * 40 + 0.5f);
				imageParams.height = imageHeight;

				// this.itemHeight = 350 * displayMetrics.density;
			}
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(ShareMessage shareMessage, String fileName, ShareContent shareContent, String textContent, String imageContent, boolean flag) {
			if (i == -1) {
				if (flag) {
					releaseShareView.setOnClickListener(thisController.mOnClickListener);
					releaseShareView.setOnTouchListener(thisController.mOnTouchListener);
					groupHeadView.setOnClickListener(thisController.mOnClickListener);
					groupHeadView.setOnTouchListener(thisController.mOnTouchListener);
					groupCoverView.setOnClickListener(thisController.mOnClickListener);
					groupCoverView.setOnTouchListener(thisController.mOnTouchListener);
				}
			} else {
				data = parser.check();

				this.message = shareMessage;
				this.fileName = fileName;

				if (shareMessage.status != null) {
					if ("sending".equals(shareMessage.status)) {
						shareStatusView.setText("发送中...");
						shareStatusView.setVisibility(View.VISIBLE);
					} else if ("failed".equals(shareMessage.status)) {
						shareStatusView.setText("发布失败");
						shareStatusView.setVisibility(View.VISIBLE);
					}
				}
				fileHandlers.getHeadImage(fileName, this.headView, headOptions);
				if (data.relationship.friendsMap.get(shareMessage.phone) == null) {
					notFriends.add(shareMessage.phone);
					if (shareMessage.nickName != null) {
						this.nickNameView.setText(shareMessage.nickName);
					} else {
						if (shareMessage.phone.length() == 11) {
							this.nickNameView.setText(shareMessage.phone.substring(0, 4) + "***" + shareMessage.phone.substring(7));
						} else {
							this.nickNameView.setText(shareMessage.phone);
						}
					}
				} else {
					this.nickNameView.setText(data.relationship.friendsMap.get(shareMessage.phone).nickName);
				}

				this.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
				this.shareCommentNumberView.setText(shareMessage.comments.size() + "");

				if (flag) {
					this.headView.setTag("ShareMessage#" + shareMessage.phone);
					this.headView.setTag(R.id.tag_class, "share_head");
					this.headView.setTag(R.id.tag_first, shareMessage.phone);
					this.headView.setOnClickListener(thisController.mOnClickListener);
					this.headView.setOnTouchListener(thisController.mOnTouchListener);

					this.praiseAreaView.setTag("ShareMessagePraise#" + shareMessage.gsid);
					this.praiseAreaView.setTag(R.id.tag_class, "share_praise");
					this.praiseAreaView.setTag(R.id.tag_first, shareMessage.phone);
					this.praiseAreaView.setOnClickListener(thisController.mOnClickListener);
					this.praiseAreaView.setOnTouchListener(thisController.mOnTouchListener);

					// businessCardPopView.showUserCardDialogView();

					this.releaseTimeView.setText(DateUtil.formatTime(shareMessage.time));
					// if (shareContent == null) {
					// shareContent = gson.fromJson("{shareContentItems:" + shareMessage.content + "}", ShareContent.class);
					// }
					int cHeight = imageHeight;

					// List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
					// for (int i = 0; i < shareContentItems.size(); i++) {
					// ShareContentItem shareContentItem = shareContentItems.get(i);
					// if (shareContentItem.type.equals("image")) {
					// imageContent = shareContentItem.detail;
					// if (!"".equals(textContent))
					// break;
					// } else if (shareContentItem.type.equals("text")) {
					// textContent = shareContentItem.detail;
					// if (!"".equals(imageContent))
					// break;
					// }
					// }

					this.shareTextContentView.setText(Html.fromHtml(textContent));
					if (textContent.length() == 0) {
						shareTextContentView.setBackgroundColor(Color.parseColor("#00000000"));
					}
					if (imageContent.equals("")) {
						cHeight = 0;// (int) (displayMetrics.density * 30 + 0.5f);
						shareTextContentView.setBackgroundColor(Color.parseColor("#00000000"));
						shareTextContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						shareTextContentView.setTextColor(Color.parseColor("#aa000000"));
					}
					final int sHeight = cHeight;
					// ViewTreeObserver vto = shareTextContentView.getViewTreeObserver();
					FrameLayout.LayoutParams textParams = (LayoutParams) shareTextContentView.getLayoutParams();
					FrameLayout.LayoutParams buttonbarpParams = (LayoutParams) buttonBar.getLayoutParams();
					int textHeigth1 = (int) (displayMetrics.density * 90 + 0.5);
					int textHeigth2 = (int) (displayMetrics.density * 20 + 0.5);
					if (sHeight == 0) {
						textParams.topMargin = (int) (displayMetrics.density * 40 + 0.5f);
						buttonbarpParams.topMargin = (int) (displayMetrics.density * 50 + 0.5f) + textHeigth1;
					} else {
						textParams.topMargin = (int) (displayMetrics.density * 40 + 0.5f + sHeight - textHeigth2);
						buttonbarpParams.topMargin = (int) (displayMetrics.density * 40 + 0.5f) + sHeight;
						shareTextContentView.setSingleLine();
					}
					buttonbarpParams.height = (int) (displayMetrics.density * 40 + 0.5f);

					fileHandlers.getThumbleImage(imageContent, shareImageContentView, width1 / 2, imageHeight / 2, options, fileHandlers.THUMBLE_TYEP_SQUARE);

					String userPhone = data.userInformation.currentUser.phone;
					if (shareMessage.praiseusers.contains(userPhone)) {
						// this.sharePraiseIconView.setImageResource(R.drawable.praised_icon);
					} else {
						// this.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
					}
					List<Comment> comments = shareMessage.comments;
					// this.shareCommentIconView.setImageResource(R.drawable.comment_icon);
					for (int i = 0; i < comments.size(); i++) {
						Comment comment = comments.get(i);
						if (comment.phone.equals(userPhone)) {
							// this.shareCommentIconView.setImageResource(R.drawable.commented_icon);
							break;
						}
					}
				}
			}
		}
	}

	public ViewGroup pop_out_background1;
	public ViewGroup pop_out_background2;

	public void initializationSquaresDialog() {
		squareDialogView = (TouchView) mainView.mInflater.inflate(R.layout.share_group_select_dialog, null, false);

		squareDialogView.setTag(R.id.tag_class, "group_view");

		pop_out_background1 = (ViewGroup) squareDialogView.findViewById(R.id.pop_out_background1);
		pop_out_background2 = (ViewGroup) squareDialogView.findViewById(R.id.pop_out_background2);

		groupManageView = squareDialogView.findViewById(R.id.groups_manage);
		groupManageView.setTag(R.id.tag_class, "group_setting");
		groupListButtonView = squareDialogView.findViewById(R.id.groupListButton);
		groupListButtonView.setVisibility(View.GONE);
		createGroupButtonView = squareDialogView.findViewById(R.id.createGroupButton);
		createGroupButtonView.setVisibility(View.GONE);
		findMoreGroupButtonView = squareDialogView.findViewById(R.id.findMoreButton);
		groupsManageButtons = squareDialogView.findViewById(R.id.groups_manage_buttons);

		TouchView mainContentView = (TouchView) squareDialogView;
		groupsDialogContent = (ViewGroup) squareDialogView.findViewById(R.id.groupsContent);

		panelWidth = (int) (displayMetrics.widthPixels * 0.7578125f);
		panelHeight = (int) (displayMetrics.heightPixels * 0.7578125f);

		TouchView.LayoutParams mainContentParams = new TouchView.LayoutParams(panelWidth, panelHeight);

		mainContentView.setLayoutParams(mainContentParams);
		squaresListBody = new ListBody1();
		squaresListBody.initialize(displayMetrics, groupsDialogContent);
		setSquaresDialogContent();
	}

	public boolean isShowSquareDialog = false;

	public void showSquaresDialog() {
		if (!isShowSquareDialog) {
			if (data.relationship.squares.size() == 0) {
				if (groupsManageButtons.getVisibility() == View.GONE) {
					groupsManageButtons.setVisibility(View.VISIBLE);
				}
			} else {
				if (groupsManageButtons.getVisibility() == View.VISIBLE) {
					groupsManageButtons.setVisibility(View.GONE);
				}
			}
			squaresListBody.active();
			squareMessageListBody.inActive();
			mainView.mainPagerBody.inActive();
			// groupPopWindow.showAtLocation(mainView.main_container,
			// Gravity.CENTER, 0, 0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			mainView.main_container.addView(this.squareDialogView, layoutParams);
			isShowSquareDialog = true;
		}
	}

	public void dismissSquareDialog() {
		if (isShowSquareDialog) {
			squaresListBody.inActive();
			squareMessageListBody.active();
			mainView.mainPagerBody.active();
			// groupPopWindow.dismiss();
			mainView.main_container.removeView(this.squareDialogView);
			isShowSquareDialog = false;
		}
	}

	public void setSquaresDialogContent() {
		data = parser.check();

		if (data.relationship.groupsMap == null || data.localStatus.localData == null) {
			log.e("return shareMap or localData");
			return;
		}
		Group group0 = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedSquare);
		if (group0 != null) {
			this.squareTopMenuSquareName.setText(group0.name);
		}

		List<String> squares = data.relationship.squares;
		Map<String, Group> groupsMap = data.relationship.groupsMap;
		this.squaresListBody.containerView.removeAllViews();
		groupsDialogContent.removeAllViews();
		this.squaresListBody.height = 0;
		squaresListBody.listItemsSequence.clear();
		for (int i = 0; i < squares.size(); i++) {
			// boolean a = groups.get(i) == "1765";
			// log.e(a + "--------" + groups.get(i) + "---" +
			// groupsMap.get("1765"));
			Group group = groupsMap.get(squares.get(i));
			String key = "group#" + group.gid + "_" + group.name;
			GroupDialogItem groupDialogItem;
			View view = null;
			if (squaresListBody.listItemBodiesMap.get(key) != null) {
				groupDialogItem = (GroupDialogItem) squaresListBody.listItemBodiesMap.get(key);
				view = groupDialogItem.cardView;
			} else {
				groupDialogItem = new GroupDialogItem(this.squaresListBody);
				view = groupDialogItem.initialize();
				squaresListBody.listItemBodiesMap.put(key, groupDialogItem);
			}
			squaresListBody.listItemsSequence.add(key);
			groupDialogItem.setContent(group);
			// groupDialogItem.setViewLayout();

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 60), (int) (60 * displayMetrics.density));
			groupDialogItem.y = this.squaresListBody.height;
			groupDialogItem.cardView.setY(groupDialogItem.y);
			groupDialogItem.cardView.setX(0);
			this.squaresListBody.height = this.squaresListBody.height + 60 * displayMetrics.density;
			this.squaresListBody.containerView.addView(view, layoutParams);

			// onclick
			view.setTag("GroupDialogContentItem#" + group.gid);
			view.setTag(R.id.shareTopMenuGroupName, squareTopMenuSquareName);
			// listener
			view.setTag(R.id.tag_class, "group_view");
			view.setTag(R.id.tag_first, group);
			view.setOnClickListener(thisController.mOnClickListener);
			view.setOnTouchListener(thisController.mOnTouchListener);

			Log.v(tag, "this.friendListBody.height: " + this.squaresListBody.height + "    circleBody.y:  " + groupDialogItem.y);
		}
		this.squaresListBody.containerHeight = (int) (displayMetrics.heightPixels * 0.6578125f);

	}

	public void modifyCurrentShowGroup() {
		List<String> listItemsSequence = squaresListBody.listItemsSequence;
		Map<String, MyListItemBody> listItemsSequenceMap = squaresListBody.listItemBodiesMap;
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
			fileHandlers.getHeadImage(group.icon, this.groupIconView, headOptions);
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

	public int width;
	public DisplayImageOptions headOptions;

	public View shareMessageRootView;

	public PopupWindow releaseSharePopWindow;

	public View releaseShareDialogView;
	public HorizontalScrollView dialogMainContentView;

	public TouchView releaseTextButton;
	public TouchView releaseAlbumButton;
	public TouchView releaseImageViewButton;

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
}
