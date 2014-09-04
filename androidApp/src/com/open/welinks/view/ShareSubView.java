package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.TouchImageView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.ShareSubController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.ShareContent;
import com.open.welinks.model.Data.ShareContent.ShareContentItem;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.Comment;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MCImageUtils;

public class ShareSubView {

	public Data data = Data.getInstance();

	public String tag = "ShareSubView";
	
	public MyLog log = new MyLog(tag, true);

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	// share
	public RelativeLayout shareView;

	public ViewGroup shareMessageView;
	public ListBody1 shareMessageListBody;

	public ImageView leftImageButton;
	public RelativeLayout shareTopMenuGroupNameParent;
	public TextView shareTopMenuGroupName;

	// group
	public PopupWindow groupPopWindow;
	public View groupDialogView;

	public RelativeLayout groupsDialogContent;

	public ListBody1 groupListBody;

	// share top Bar child view
	public View groupMembersView;
	public RelativeLayout groupMembersListContentView;
	public ImageView releaseShareView;

	public int shareImageHeight;

	public float imageHeightScale = 0.5686505598114319f;

	public float panelScale = 1.010845986984816f;

	public int panelHeight;

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 9);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();

	public ShareSubView(MainView mainView) {
		this.mainView = mainView;
	}

	public Bitmap bitmap;

	public void initViews() {
		this.shareView = mainView.shareView;
		this.displayMetrics = mainView.displayMetrics;

		Resources resources = mainView.thisActivity.getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		shareImageHeight = (int) (this.displayMetrics.widthPixels * imageHeightScale);
		panelHeight = (int) (this.displayMetrics.widthPixels * panelScale);
		// Log.e(tag, "height--------------" + shareImageHeight);

		shareMessageView = (ViewGroup) shareView.findViewById(R.id.groupShareMessageContent);

		shareMessageListBody = new ListBody1();
		shareMessageListBody.initialize(displayMetrics, shareMessageView);

		leftImageButton = (ImageView) shareView.findViewById(R.id.leftImageButton);
		shareTopMenuGroupNameParent = (RelativeLayout) shareView.findViewById(R.id.shareTopMenuGroupNameParent);
		shareTopMenuGroupName = (TextView) shareView.findViewById(R.id.shareTopMenuGroupName);

		this.groupMembersView = mainView.mInflater.inflate(R.layout.share_group_members_show, null);
		groupMembersListContentView = (RelativeLayout) this.groupMembersView.findViewById(R.id.groupMembersListContent);
		releaseShareView = (ImageView) this.groupMembersView.findViewById(R.id.releaseShare);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		myScrollImageBody = new MyScrollImageBody();
		myScrollImageBody.initialize(groupMembersListContentView);
		// groupMembersListContentView.setBackgroundColor(Color.RED);
		displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		mSdCardFile = Environment.getExternalStorageDirectory();
		mImageFile = new File(mSdCardFile, "welinks/heads/");
		if (!mImageFile.exists())
			mImageFile.mkdirs();

		thisController.getUserCurrentAllGroup();

		showShareMessages();

		initReleaseShareDialogView();

		initializationGroupsDialog();

	}

	public void getCurrentGroupShareMessages() {
		thisController.getCurrentGroupShareMessages();
	}

	public void showShareMessages() {
		this.shareMessageListBody.listItemsSequence.clear();
		this.shareMessageListBody.containerView.removeAllViews();
		this.shareMessageListBody.height = 0;
		SharesMessageBody sharesMessageBody0 = null;
		sharesMessageBody0 = new SharesMessageBody(this.shareMessageListBody);
		sharesMessageBody0.initialize(-1);
		sharesMessageBody0.setContent(null);
		this.shareMessageListBody.listItemsSequence.add("message#" + "topBar");
		this.shareMessageListBody.listItemBodiesMap.put("message#" + "topBar", sharesMessageBody0);
		RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (50 * displayMetrics.density));
		sharesMessageBody0.y = 60 * displayMetrics.density * 0 + 2 * displayMetrics.density;
		sharesMessageBody0.cardView.setY(sharesMessageBody0.y);
		sharesMessageBody0.cardView.setX(0);
		this.shareMessageListBody.height = this.shareMessageListBody.height + 60 * displayMetrics.density;
		this.shareMessageListBody.containerView.addView(sharesMessageBody0.cardView, layoutParams0);

		Share share = data.shares.shareMap.get(data.localStatus.localData.currentSelectedGroup);
		if (share == null)
			return;
		List<String> sharesOrder = share.sharesOrder;
		Map<String, ShareMessage> sharesMap = share.sharesMap;
		ShareMessage lastShareMessage = null;
		int timeBarCount = 0;
		for (int i = 0; i < sharesOrder.size(); i++) {
			String key = sharesOrder.get(i);
			ShareMessage shareMessage = null;
			shareMessage = sharesMap.get(key);
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
				sharesMessageBody0.setContent(shareMessage);
				this.shareMessageListBody.listItemsSequence.add("message#" + "timeBar" + shareMessage.time);
				this.shareMessageListBody.listItemBodiesMap.put("message#" + "timeBar" + shareMessage.time, sharesMessageBody0);
				RelativeLayout.LayoutParams layoutParams_2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (40 * displayMetrics.density));
				sharesMessageBody0.y = this.shareMessageListBody.height;
				sharesMessageBody0.cardView.setY(sharesMessageBody0.y);
				sharesMessageBody0.cardView.setX(0);
				this.shareMessageListBody.height = this.shareMessageListBody.height + 50 * displayMetrics.density;
				this.shareMessageListBody.containerView.addView(sharesMessageBody0.cardView, layoutParams_2);
				i--;
				lastShareMessage = shareMessage;
				continue;
			}
			// .........................
			log.e("message#---" + shareMessage.gsid);
			String keyName = "message#" + shareMessage.gsid;
			boolean isExists = false;
			if (this.shareMessageListBody.listItemBodiesMap.get(keyName) != null) {
				sharesMessageBody = (SharesMessageBody) this.shareMessageListBody.listItemBodiesMap.get(keyName);
				isExists = true;
			} else {
				sharesMessageBody = new SharesMessageBody(this.shareMessageListBody);
				sharesMessageBody.initialize(i);
				this.shareMessageListBody.listItemBodiesMap.put(keyName, sharesMessageBody);
			}
			this.shareMessageListBody.listItemsSequence.add(keyName);
			sharesMessageBody.setContent(shareMessage);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (340 * displayMetrics.density));
			sharesMessageBody.y = this.shareMessageListBody.height;
			sharesMessageBody.cardView.setY(sharesMessageBody.y);
			sharesMessageBody.cardView.setX(0);
			// Why the object cache access to cheap 10dp view position
			if (isExists) {
				sharesMessageBody.cardView.setX(10 * displayMetrics.density);
			}
			this.shareMessageListBody.height = this.shareMessageListBody.height + 350 * displayMetrics.density;
			this.shareMessageListBody.containerView.addView(sharesMessageBody.cardView, layoutParams);
			if (i == 0) {
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
		
		
		this.shareMessageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
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

		public TextView messageTimeView;

		public DownloadFile downloadFile = null;

		public ShareMessage message;

		public int i;

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
			}
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(ShareMessage shareMessage) {
			if (i == -1) {
				// showGroupMembers(groupMembersListContentView);
				releaseShareView.setOnClickListener(thisController.mOnClickListener);
			} else if (i == -2) {
				this.messageTimeView.setText(DateUtil.formatYearMonthDay(shareMessage.time));
			} else {
				this.message = shareMessage;
				this.headView.setImageBitmap(bitmap);
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
				File sdFile = Environment.getExternalStorageDirectory();
				File file = new File(sdFile, "welinks/thumbnail/" + imageContent);
				final int showImageWidth = displayMetrics.widthPixels - (int) (22 * displayMetrics.density + 0.5f);
				final int showImageHeight = shareImageHeight;// (int) (displayMetrics.density * 200 + 0.5f);
				RelativeLayout.LayoutParams shareImageParams = new RelativeLayout.LayoutParams(showImageWidth, showImageHeight);
				// int margin = (int) ((int) displayMetrics.density * 1 + 0.5f);
				shareImageContentView.setLayoutParams(shareImageParams);
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
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(showImageWidth, height);
							shareImageContentView.setLayoutParams(params);
						}
					});
				} else {
					File file2 = new File(sdFile, "welinks/images/" + imageContent);
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

	public PopupWindow releaseSharePopWindow;

	public View releaseShareDialogView;
	public RelativeLayout dialogMainContentView;

	public RelativeLayout releaseImageTextButton;
	public RelativeLayout releaseVoiceTextButton;
	public RelativeLayout releaseVoteButton;

	public ShareSubController thisController;

	@SuppressWarnings("deprecation")
	public void initReleaseShareDialogView() {
		releaseShareDialogView = mainView.mInflater.inflate(R.layout.share_release_type_dialog, null);
		dialogMainContentView = (RelativeLayout) releaseShareDialogView.findViewById(R.id.dialogMainContent);
		releaseImageTextButton = (RelativeLayout) releaseShareDialogView.findViewById(R.id.releaseImageTextShareButton);
		releaseVoiceTextButton = (RelativeLayout) releaseShareDialogView.findViewById(R.id.releaseVoiceTextShareButton);
		releaseVoteButton = (RelativeLayout) releaseShareDialogView.findViewById(R.id.releaseVoteShareButton);

		releaseImageTextButton.setOnClickListener(thisController.mOnClickListener);
		releaseImageTextButton.setOnTouchListener(thisController.onTouchBackColorListener);
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

	@SuppressWarnings("deprecation")
	public void initializationGroupsDialog() {
		groupDialogView = mainView.mInflater.inflate(R.layout.share_group_select_dialog, null, false);
		groupPopWindow = new PopupWindow(groupDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		groupPopWindow.setBackgroundDrawable(new BitmapDrawable());
		RelativeLayout mainContentView = (RelativeLayout) groupDialogView.findViewById(R.id.mainContent);

		groupPopWindow.setOutsideTouchable(true);

		groupsDialogContent = (RelativeLayout) groupDialogView.findViewById(R.id.groupsContent);
		RelativeLayout.LayoutParams mainContentParams = (RelativeLayout.LayoutParams) mainContentView.getLayoutParams();
		mainContentParams.height = (int) (displayMetrics.heightPixels * 0.7578125f);
		mainContentParams.leftMargin = (int) (20 / displayMetrics.density + 0.5f);
		mainContentParams.rightMargin = (int) (20 / displayMetrics.density + 0.5f);
		mainContentView.setLayoutParams(mainContentParams);
		groupListBody = new ListBody1();
		groupListBody.initialize(displayMetrics, groupsDialogContent);
		setGroupsDialogContent();
		// groupsDialogContent.setOnClickListener(thisController.mOnClickListener);
		// groupsDialogContent.setOnTouchListener(thisController.mOnTouchListener);
	}

	public void showGroupsDialog() {
		if (groupPopWindow != null && !groupPopWindow.isShowing())
			groupListBody.active();
		groupPopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);
	}

	public void dismissGroupDialog() {
		if (groupPopWindow != null && groupPopWindow.isShowing())
			groupListBody.inActive();
		groupPopWindow.dismiss();
	}

	public void setGroupsDialogContent() {
		List<String> groups = data.relationship.groups;
		Map<String, Group> groupsMap = data.relationship.groupsMap;
		groupsDialogContent.removeAllViews();
		groupListBody.listItemsSequence.clear();
		for (int i = 0; i < groups.size(); i++) {
			Group group = groupsMap.get(groups.get(i));
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

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (60 * displayMetrics.density));
			groupDialogItem.y = (60 * displayMetrics.density * i);
			groupDialogItem.cardView.setY(groupDialogItem.y);
			groupDialogItem.cardView.setX(0);
			this.groupListBody.height = this.groupListBody.height + 60 * displayMetrics.density;
			this.groupListBody.containerView.addView(groupDialogItem.cardView, layoutParams);

			// onclick
			view.setTag("GroupDialogContentItem#" + group.gid);
			view.setTag(R.id.shareTopMenuGroupName, shareTopMenuGroupName);
			view.setOnClickListener(thisController.mOnClickListener);

		}
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

		public Group group;

		public View initialize() {
			this.cardView = mainView.mInflater.inflate(R.layout.share_group_select_dialog_item, null);
			this.groupIconView = (ImageView) this.cardView.findViewById(R.id.groupIcon);
			this.groupNameView = (TextView) this.cardView.findViewById(R.id.groupName);
			this.groupSelectedStatusView = (ImageView) this.cardView.findViewById(R.id.groupSelectedStatus);
			return cardView;
		}

		public void setContent(Group group) {
			this.group = group;
			Resources resources = mainView.thisActivity.getResources();
			Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
			bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
			this.groupIconView.setImageBitmap(bitmap);
			this.groupNameView.setText(group.name);
			if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
				this.groupSelectedStatusView.setVisibility(View.VISIBLE);
			} else {
				this.groupSelectedStatusView.setVisibility(View.GONE);
			}
		}

		public void setViewLayout() {
			if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
				this.groupSelectedStatusView.setVisibility(View.VISIBLE);
				this.groupNameView.setText(group.name);
			} else {
				this.groupSelectedStatusView.setVisibility(View.GONE);
			}
		}
	}

	public MyScrollImageBody myScrollImageBody;
	public int width;
	public File mSdCardFile;
	public File mImageFile;
	public DisplayImageOptions displayImageOptions;

	public void showGroupMembers() {
		groupMembersListContentView.removeAllViews();
		Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		shareTopMenuGroupName.setText(group.name);
		List<String> groupMembers = group.members;
		Map<String, Friend> friendsMap = data.relationship.friendsMap;

		width = (int) (displayMetrics.density * 40);
		for (int i = 0; i < groupMembers.size(); i++) {
			String key = groupMembers.get(i);
			Friend friend = friendsMap.get(key);

			ImageBody imageBody = new ImageBody();
			imageBody.initialize();

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
			myScrollImageBody.contentView.addView(imageBody.imageView, layoutParams);
			float x = i * (width + 2 * displayMetrics.density) + 5 * displayMetrics.density;
			if (i == 0) {
				x = 5 * displayMetrics.density;
			}
			imageBody.imageView.setX(x);// Translation
			imageBody.imageView.setImageBitmap(bitmap);
			if ("".equals(friend.head)) {
				imageBody.imageView.setImageBitmap(bitmap);
			} else {
				File currentImageFile = new File(mImageFile, friend.head);
				String filepath = currentImageFile.getAbsolutePath();
				boolean isFlag = false;
				String path = "";
				if (currentImageFile.exists()) {
					BitmapFactory.Options boptions = new BitmapFactory.Options();
					boptions.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(currentImageFile.getAbsolutePath(), boptions);
					if (boptions.outWidth > 0) {
						isFlag = true;
					}
				}
				if (isFlag) {
					path = "file://" + filepath;
				} else {
					path = API.DOMAIN_COMMONIMAGE + "heads/" + friend.head;
				}

				if (!isFlag) {
					DownloadFile downloadFile = new DownloadFile(path, filepath);
					downloadFile.view = imageBody.imageView;
					downloadFile.view.setTag("head");
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					downloadFileList.addDownloadFile(downloadFile);
				} else {
					imageLoader.displayImage(path, imageBody.imageView, displayImageOptions, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

						}
					});
				}
			}

			// imageLoader.displayImage("file://" + key, imageBody.imageView, options);
			myScrollImageBody.selectedImagesSequence.add(key);
			myScrollImageBody.selectedImagesSequenceMap.put(key, imageBody);
			imageBody.imageView.setTag(i);
			// imageBody.imageView.setOnClickListener(thisController.monClickListener);
		}
	}

	public class MyScrollImageBody {
		public ArrayList<String> selectedImagesSequence = new ArrayList<String>();
		public HashMap<String, ImageBody> selectedImagesSequenceMap = new HashMap<String, ImageBody>();

		public RelativeLayout contentView;

		public RelativeLayout initialize(RelativeLayout view) {
			this.contentView = view;
			return view;
		}

		public void recordChildrenPosition() {
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				imageBody.x = imageBody.imageView.getX();
				imageBody.y = imageBody.imageView.getY();
			}
		}

		public void setChildrenPosition(float deltaX, float deltaY) {
			float screenWidth = displayMetrics.widthPixels;
			float totalLength = selectedImagesSequence.size() * (width + 2 * displayMetrics.density) + 2 * displayMetrics.density;
			if (totalLength < screenWidth) {
				return;
			}
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				if ((imageBody.x + deltaX) < (screenWidth - totalLength))
					break;
				if (i == 0 && (imageBody.x + deltaX) > (5 * displayMetrics.density))
					break;
				imageBody.imageView.setX(imageBody.x + deltaX);
				imageBody.imageView.setY(imageBody.y + deltaY);
			}
		}
	}

	public class ImageBody {
		public int i;

		public float x;
		public float y;
		public TouchImageView imageView;

		public TouchImageView initialize() {
			this.imageView = new TouchImageView(mainView.context);
			return this.imageView;
		}
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
