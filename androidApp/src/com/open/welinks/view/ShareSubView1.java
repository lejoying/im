package com.open.welinks.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.TouchImageView;
import com.open.lib.TouchTextView;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.ShareSubController1;
import com.open.welinks.customView.ControlProgress;
import com.open.welinks.customView.ScrollListBody;
import com.open.welinks.customView.ScrollListBody.ScrollListItemBody;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Board;
import com.open.welinks.model.Data.Boards.Comment;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Relationship.GroupCircle;
import com.open.welinks.model.DataHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.SubData.ShareContentItem;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.utils.DateUtil;

public class ShareSubView1 {

	public Data data = Data.getInstance();

	public String tag = "ShareSubView";
	public MyLog log = new MyLog(tag, true);

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public DisplayMetrics displayMetrics;

	public MainView1 mainView;

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

	public DisplayImageOptions options;
	public Gson gson = new Gson();
	public Parser parser = Parser.getInstance();

	// first share View Animation true or false
	public boolean isShowFirstMessageAnimation = false;

	public GroupCircle currentGroupCircle;

	public ShareSubView1(MainView1 mainView) {
		this.mainView = mainView;
		taskManageHolder.viewManage.shareSubView = this;
	}

	public SmallBusinessCardPopView businessCardPopView;

	public View releaseChannelContainer;
	public View releaseChannelView;

	public TextView roomTextView, room;

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
		room = (TextView) this.groupMembersView.findViewById(R.id.room);

		groupCoverView = (TouchImageView) this.groupMembersView.findViewById(R.id.groupCover);
		groupCoverView.setTag(R.id.tag_class, "group_head");
		groupHeadView = (ImageView) this.groupMembersView.findViewById(R.id.group_head);
		groupHeadView.setTag(R.id.tag_class, "group_head");

		textSize = displayMetrics.scaledDensity * 18 + 0.5f;
		botton = (ImageView) shareView.findViewById(R.id.botton);

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		// myScrollImageBody = new MyScrollImageBody();
		// myScrollImageBody.initialize(groupMembersListContentView);

		releaseChannelView = mainView.mInflater.inflate(R.layout.view_release_channels, null);
		releaseChannelContainer = releaseChannelView.findViewById(R.id.releaseChannelContainer);

		mImageFile = taskManageHolder.fileHandler.sdcardHeadImageFolder;
		if (!mImageFile.exists())
			mImageFile.mkdirs();

		thisController.getUserCurrentAllGroup();

		// showShareMessages();

		initReleaseShareDialogView();

		initializationGroupsDialog();

		businessCardPopView = new SmallBusinessCardPopView(mainView.thisActivity, mainView.main_container);
		businessCardPopView.cardView.setHot(false);
		showReleaseChannel();
	}

	public float textSize;
	public ImageView botton;

	public void setMenuNameBotton(String name) {
		int length = name.length();
		length = length > 8 ? 8 : length;
		int left = (int) (textSize * length);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) botton.getLayoutParams();
		params.leftMargin = left;
	}

	public void getCurrentGroupShareMessages() {
		thisController.getCurrentGroupShareMessages();
	}

	public void setConver() {
		final Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		File file = new File(taskManageHolder.fileHandler.sdcardBackImageFolder, group.cover);
		if (group.cover == null || "".equals(group.cover)) {
			taskManageHolder.imageLoader.displayImage("drawable://" + R.drawable.tempicon, groupCoverView);
			return;
		}
		final String path = file.getAbsolutePath();
		if (file.exists()) {
			taskManageHolder.imageLoader.displayImage("file://" + path, groupCoverView, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					downloadConver(group.cover, path);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				}
			});
		} else {
			if (group.cover != null) {
				downloadConver(group.cover, path);
			} else {
				taskManageHolder.imageLoader.displayImage("drawable://" + R.drawable.tempicon, groupCoverView);
			}
		}
	}

	public void downloadConver(String converName, String path) {
		groupCoverView.setTag("conver");
		String url = API.DOMAIN_COMMONIMAGE + "backgrounds/" + converName;
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = groupCoverView;
		downloadFile.setDownloadFileListener(thisController.downloadListener);
		taskManageHolder.downloadFileList.addDownloadFile(downloadFile);
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
				Board board = data.boards.boardsMap.get(currentGroup.currentBoard + "");
				if (board != null) {
					roomTextView.setText("上次刷新:" + DateUtil.getChatMessageListTime(board.updateTime));
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

	public void showNewStyle() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(this.displayMetrics.widthPixels, this.displayMetrics.heightPixels);
		this.mainView.main_container.addView(this.releaseChannelView, params);
		this.isShowChannel = true;
		shareMessageListBody.inActive();
		mainView.mainPagerBody.inActive();
	}

	public void dismissNewStyle() {
		this.mainView.main_container.removeView(this.releaseChannelView);
		this.isShowChannel = false;
		shareMessageListBody.active();
		mainView.mainPagerBody.active();
	}

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
			body.x = releaseChannelListBody.width;
			body.cardView.setX(body.x);
			body.cardView.setY(0);

			releaseChannelListBody.width = releaseChannelListBody.width + width;
		}
		this.releaseChannelListBody.containerHeight = (int) (this.displayMetrics.heightPixels - ViewManage.getStatusBarHeight(mainView.thisActivity));
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

		showTopMenuRoomName();
		if (data.relationship == null || data.relationship.groupCircles == null || data.relationship.groupCirclesMap == null || data.localStatus == null || data.localStatus.localData == null) {
			log.e("return groups or localData");
			return;
		}
		GroupCircle groupCircle = currentGroupCircle;
		if (groupCircle == null) {
			String gid = data.localStatus.localData.currentGroupCircle;
			if (gid == null || gid.equals("")) {
				gid = data.relationship.groupCircles.get(0);
			}
			groupCircle = data.relationship.groupCirclesMap.get(gid);
		}
		// log.e("------------------------------*******" + groupCircle);
		if (groupCircle == null || groupCircle.groups == null) {
			log.e("return groups or localData");
			return;
		}
		boolean flag0 = groupCircle.groups.contains(data.localStatus.localData.currentSelectedGroup);
		if (!flag0) {
			if (groupCircle.groups.size() == 0) {
				data.localStatus.localData.currentSelectedGroup = "";
				data.relationship.isModified = true;
			} else {
				data.localStatus.localData.currentSelectedGroup = groupCircle.groups.get(0);
			}
		}

		boolean flag = data.relationship.groups.contains(data.localStatus.localData.currentSelectedGroup);
		SharesMessageBody sharesMessageBody0 = null;
		if (flag) {
			sharesMessageBody0 = (SharesMessageBody) shareMessageListBody.listItemBodiesMap.get("message#" + "topBar");
			this.shareMessageListBody.listItemsSequence.clear();
			this.shareMessageListBody.containerView.removeAllViews();
			shareMessageView.removeAllViews();
			log.e("clear share list body1.");
			this.shareMessageListBody.height = 0;
			if (sharesMessageBody0 == null) {
				sharesMessageBody0 = new SharesMessageBody(this.shareMessageListBody);
				sharesMessageBody0.initialize(-1);
				sharesMessageBody0.itemHeight = (280 - 48) * displayMetrics.density;
			}
			sharesMessageBody0.setContent(null, "", "", "", 0, 0, 0);
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
			log.e("clear share list body2.");
			return;
		}
		currentGroup = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		if (currentGroup == null || !flag) {
			return;
		}
		if (currentGroup.currentBoard == null || "".equals(currentGroup.currentBoard)) {
			if (currentGroup.boards != null && currentGroup.boards.size() > 0) {
				currentGroup.currentBoard = currentGroup.boards.get(0);
			} else {
				DataHandler.getGroupBoards(currentGroup.gid + "");
			}
		}
		// set conver
		// TODO conver setting
		taskManageHolder.fileHandler.getHeadImage(currentGroup.icon, this.groupHeadView, taskManageHolder.viewManage.options56);
		if (currentGroup.cover != null && !currentGroup.cover.equals("")) {
			setConver();
		} else {
			log.e("cover" + currentGroup.cover);
			taskManageHolder.imageLoader.displayImage("drawable://" + R.drawable.tempicon, groupCoverView);
		}
		Board board = null;
		if (data.boards != null && data.boards.boardsMap != null) {
			board = data.boards.boardsMap.get(currentGroup.currentBoard);
			// log.e("ShareList Board:" + currentGroup.currentBoard + ",Share:" + board);
			if (board == null) {
				return;
			}
		} else {
			return;
		}

		String boardName = "";
		boardName = board.name;
		if ("主版".equals(boardName))
			boardName = "默认版块";
		room.setText(boardName);

		showRoomTime();
		List<String> shareMessagesOrder = board.shareMessagesOrder;
		Map<String, ShareMessage> shareMessagesMap = data.boards.shareMessagesMap;
		ShareMessage lastShareMessage = null;
		// int timeBarCount = 0;
		if (shareMessagesOrder == null) {
			shareMessagesOrder = new ArrayList<String>();
		}
		for (int i = 0; i < shareMessagesOrder.size(); i++) {
			String key = shareMessagesOrder.get(i);
			ShareMessage shareMessage = null;
			shareMessage = shareMessagesMap.get(key);
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
				sharesMessageBody0.setContent(shareMessage, "", "", "", 0, 0, 0);
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
			// isExists = false;

			if (shareMessage.comments != null && sharesMessageBody.commentContentView.getVisibility() == View.GONE && shareMessage.comments.size() > 0) {
				isExists = false;
			}

			if (!isExists) {
				List<ShareContentItem> shareContentItems = gson.fromJson(shareMessage.content, new TypeToken<ArrayList<ShareContentItem>>() {
				}.getType());
				String textContent = "";
				String imageContent = "";
				float ratio = 0;
				B: for (int j = 0; j < shareContentItems.size(); j++) {
					ShareContentItem shareContentItem = shareContentItems.get(j);
					if (shareContentItem.type.equals("image")) {
						imageContent = shareContentItem.detail;
						ratio = shareContentItem.ratio;
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
					int end = 4 * lineTextCount;
					if (end >= textContent.length()) {
						end = textContent.length();
					}
					String firstContent = textContent.substring(0, end);
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
					// if (textContent.indexOf("1、") == 0) {
					// log.e(textContent.substring(0, subPosition));
					// }
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
				// log.e("ratio:" + ratio);
				int lineHeight = sharesMessageBody.shareTextContentView.getLineHeight();
				int imageHeight = shareImageHeight;
				if ("".equals(imageContent)) {
					totalHeight = (int) (70 * displayMetrics.density + 0.5f) + lineCount * lineHeight;
					totalHeight += 50 * displayMetrics.density;
				} else if ("".equals(textContent)) {
					float showImageWidth = displayMetrics.widthPixels - 20 * displayMetrics.density;// + 120;
					if (ratio != 0) {
						imageHeight = (int) (showImageWidth * ratio);
					} else {
						try {
							File file = new File(taskManageHolder.fileHandler.sdcardImageFolder, imageContent);
							if (file.exists()) {
								FileInputStream fileInputStream0 = new FileInputStream(file);
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.inJustDecodeBounds = true;
								BitmapFactory.decodeStream(fileInputStream0, null, options);
								fileInputStream0.close();
								float ratio1 = (float) options.outHeight / (float) options.outWidth;
								imageHeight = (int) (showImageWidth * ratio1);
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					totalHeight = (int) (60 * displayMetrics.density + 0.5f + imageHeight);
					totalHeight += 55 * displayMetrics.density;
				} else {
					float showImageWidth = displayMetrics.widthPixels - 20 * displayMetrics.density;// + 120;
					if (ratio != 0) {
						imageHeight = (int) (showImageWidth * ratio);
					} else {
						try {
							File file = new File(taskManageHolder.fileHandler.sdcardImageFolder, imageContent);
							if (file.exists()) {
								FileInputStream fileInputStream0 = new FileInputStream(file);
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.inJustDecodeBounds = true;
								BitmapFactory.decodeStream(fileInputStream0, null, options);
								fileInputStream0.close();
								float ratio1 = (float) options.outHeight / (float) options.outWidth;
								imageHeight = (int) (showImageWidth * ratio1);
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					totalHeight = (int) (70 * displayMetrics.density + 0.5f + imageHeight) + lineCount * lineHeight;
					totalHeight += 55 * displayMetrics.density;
				}
				if (shareMessage.comments != null && shareMessage.comments.size() > 0) {
					totalHeight += 55 * displayMetrics.density;
				}
				sharesMessageBody.setContent(shareMessage, fileName, imageContent, textContent, totalHeight, imageHeight, lineCount);
			} else {
				sharesMessageBody.setContent(shareMessage, sharesMessageBody.fileName, sharesMessageBody.imageContent, sharesMessageBody.textContent, sharesMessageBody.totalHeight, sharesMessageBody.imageHeight, sharesMessageBody.lineCount);
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

		this.shareMessageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - ViewManage.getStatusBarHeight(mainView.thisActivity) - displayMetrics.density * 48);
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

		public View topView;

		public TouchImageView decrementView;
		public TouchImageView incrementView;

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

		public TouchView commentBoardView;
		public TouchTextView commentContentView;
		public TouchView commentContainer;
		public TouchImageView commentControlView;

		public int imageHeight;

		public TouchImageView commentsPointView;

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
				this.topView = this.cardView.findViewById(R.id.gshare_title);
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

				this.decrementView = (TouchImageView) this.cardView.findViewById(R.id.num_picker_decrement);
				this.incrementView = (TouchImageView) this.cardView.findViewById(R.id.num_picker_increment);

				this.commentBoardView = (TouchView) this.cardView.findViewById(R.id.commentBoard);
				this.commentBoardView.setVisibility(View.VISIBLE);
				this.commentContentView = (TouchTextView) this.cardView.findViewById(R.id.commentContent);
				this.commentContainer = (TouchView) this.cardView.findViewById(R.id.commentContainer);
				this.commentControlView = (TouchImageView) this.cardView.findViewById(R.id.commentControl);
				this.commentControlView.setAlpha(0.5f);
				this.commentsPointView = (TouchImageView) this.cardView.findViewById(R.id.commentsPoint);
				this.commentsPointView.setAlpha(0.5f);

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

		public int lineCount;

		public void setContent(ShareMessage shareMessage, String fileName, String imageContent, String textContent, int totalHeight, int imageHeight, int lineCount) {
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
				this.imageContent = imageContent;
				this.textContent = textContent;
				this.imageHeight = imageHeight;
				this.lineCount = lineCount;
				if (shareMessage.comments.size() == 0) {
					if (this.totalHeight == 0 && !textContent.equals("")) {
						this.totalHeight = (int) (totalHeight - 30 * displayMetrics.density);
					} else if (textContent.equals("")) {
						this.totalHeight = totalHeight;
					}
				} else {
					this.totalHeight = totalHeight;
				}
				if (this.totalHeight > displayMetrics.heightPixels) {
					int height = this.totalHeight - displayMetrics.heightPixels;
					this.totalHeight = displayMetrics.heightPixels;
					this.imageHeight = (int) (this.imageHeight - height);
				}
				if (shareMessage.status != null) {
					if ("sending".equals(shareMessage.status)) {
						shareStatusView.setText("发送中...");
						shareStatusView.setVisibility(View.VISIBLE);
					} else if ("failed".equals(shareMessage.status)) {
						shareStatusView.setText("发布失败");
						shareStatusView.setVisibility(View.VISIBLE);
					}
				}
				taskManageHolder.fileHandler.getHeadImage(fileName, this.headView, taskManageHolder.viewManage.options40);
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
					List<ShareContentItem> shareContentItems = gson.fromJson(shareMessage.content, new TypeToken<ArrayList<ShareContentItem>>() {
					}.getType());
					textContent = "";
					imageContent = "";
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
					this.commentBoardView.setTranslationY(this.totalHeight - 100 * displayMetrics.density);
					// this.shareTextContentView.setLines(5);
				} else if ("".equals(textContent)) {
					// this.shareTextContentView.setLines(4);
					// totalHeight = (int) (60 * displayMetrics.density + 0.5f + shareImageHeight);
					this.commentBoardView.setTranslationY(this.totalHeight - 100 * displayMetrics.density - 5 * displayMetrics.density);
				} else {
					RelativeLayout.LayoutParams params2 = (android.widget.RelativeLayout.LayoutParams) shareTextContentView.getLayoutParams();
					if (lineCount == 1) {
						params2.topMargin = (int) (12 * displayMetrics.density + 5 * displayMetrics.density) + imageHeight;
					} else {
						params2.topMargin = (int) (12 * displayMetrics.density) + imageHeight;
					}
					this.commentBoardView.setTranslationY(this.totalHeight - 100 * displayMetrics.density);
					// totalHeight = (int) (75 * displayMetrics.density + 0.5f + shareImageHeight) + lineCount * this.shareTextContentView.getLineHeight();
				}

				// FrameLayout.LayoutParams commentBoardParams = (LayoutParams) this.commentBoardView.getLayoutParams();
				// commentBoardParams.topMargin = (int) (this.totalHeight - 200 * displayMetrics.density);
				// shareImageContentView.setBackgroundResource(R.drawable.account_pop_black_background);

				FrameLayout.LayoutParams params = (LayoutParams) background_share_item.getLayoutParams();
				if ("".equals(textContent)) {
					params.height = (int) (totalHeight - 5 * displayMetrics.density);
				} else {
					params.height = totalHeight;
				}
				background_share_item.setBackgroundResource(R.drawable.background_group_share_item);
				background_share_item.setLayoutParams(params);
				// log.e("totalHeight:"+totalHeight);
				// this.shareTextContentView.setLines(lineCount);
				// this.shareTextContentView.setText(content);
				// if (content.indexOf("合伙人") == 0) {
				// log.e(this.shareTextContentView.getLineCount() + "----lines:" + lineCount);
				// }

				// File file = new File(fileHandlers.sdcardThumbnailFolder, imageContent);
				final int showImageWidth = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density);// + 120
				// final int showImageHeight = shareImageHeight;// (int)
				// (displayMetrics.density
				// * 200 +
				// 0.5f);
				RelativeLayout.LayoutParams shareImageParams = new RelativeLayout.LayoutParams(showImageWidth, imageHeight);
				// int margin = (int) ((int) displayMetrics.density * 1 + 0.5f);
				shareImageContentView.setLayoutParams(shareImageParams);
				if (!"".equals(imageContent)) {
					File file = new File(taskManageHolder.fileHandler.sdcardImageFolder, imageContent);
					if (file.exists()) {
						taskManageHolder.imageLoader.displayImage("file://" + file.getAbsolutePath(), shareImageContentView);
					} else {
						taskManageHolder.fileHandler.getThumbleImage(imageContent, shareImageContentView, showImageWidth * 2 / 3, imageHeight * 2 / 3, options, taskManageHolder.fileHandler.THUMBLE_TYEP_GROUP, null);
					}
				}
				if (shareMessage.scores == null) {
					shareMessage.scores = new HashMap<String, Data.Boards.Score>();
				}
				this.sharePraiseNumberView.setText(shareMessage.totalScore + "");
				Typeface face = Typeface.createFromAsset(thisController.thisActivity.getAssets(), "fonts/avenirroman.ttf");
				this.sharePraiseNumberView.setTypeface(face);
				// this.shareCommentNumberView.setText(shareMessage.comments.size() + "");
				// String userPhone = data.userInformation.currentUser.phone;
				// if (shareMessage.praiseusers.contains(userPhone)) {
				// this.sharePraiseIconView.setImageResource(R.drawable.praised_icon);
				// } else {
				// this.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
				// }
				this.sharePraiseIconView.setTag("SharePraise#" + shareMessage.gsid);
				this.sharePraiseIconView.setTag(R.id.tag_class, "share_praise");
				this.sharePraiseIconView.setOnClickListener(thisController.mOnClickListener);
				this.sharePraiseIconView.setOnTouchListener(thisController.mOnTouchListener);

				this.decrementView.setColorFilter(Color.parseColor("#0099cd"));
				this.decrementView.setAlpha(0.125f);

				this.incrementView.setColorFilter(Color.parseColor("#0099cd"));
				this.incrementView.setAlpha(0.125f);
				if (shareMessage.totalScore < 10 && shareMessage.totalScore >= 0) {
					sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					this.sharePraiseNumberView.setTranslationX(-75 * displayMetrics.density);
				} else if (shareMessage.totalScore < 100 && shareMessage.totalScore >= 0) {
					sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					this.sharePraiseNumberView.setTranslationX(-71 * displayMetrics.density);
				} else if (shareMessage.totalScore < 1000 && shareMessage.totalScore >= 0) {
					sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					this.sharePraiseNumberView.setText("999");
					this.sharePraiseNumberView.setTranslationX(-62 * displayMetrics.density);
				} else if (shareMessage.totalScore < 0) {
					sharePraiseNumberView.setTextColor(Color.parseColor("#00a800"));
					this.sharePraiseNumberView.setTranslationX(-71 * displayMetrics.density);
				}

				this.decrementView.setTag("DecrementView#" + shareMessage.gsid);
				this.decrementView.setTag(R.id.tag_class, "DecrementView");
				this.decrementView.setOnTouchListener(thisController.mOnTouchListener);
				this.decrementView.setOnClickListener(thisController.mOnClickListener);

				this.incrementView.setTag("IncrementView#" + shareMessage.gsid);
				this.incrementView.setTag(R.id.tag_class, "IncrementView");
				this.incrementView.setOnTouchListener(thisController.mOnTouchListener);
				this.incrementView.setOnClickListener(thisController.mOnClickListener);

				Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
				if (score != null) {
					if (score.positive > 0) {
						this.incrementView.setAlpha(1f);
					}
					if (score.negative > 0) {
						this.decrementView.setAlpha(1f);
					}
				} else {
					this.incrementView.setAlpha(0.125f);
					this.decrementView.setAlpha(0.125f);
				}
				this.topView.setTag("TopView#NONE");
				this.topView.setTag(R.id.tag_class, "TopView");
				this.topView.setOnTouchListener(thisController.mOnTouchListener);
				this.topView.setOnClickListener(thisController.mOnClickListener);
				List<Comment> comments = shareMessage.comments;
				// this.shareCommentIconView.setImageResource(R.drawable.comment_icon);

				this.commentControlView.setTag("CommentControlView#" + shareMessage.gsid);
				this.commentControlView.setTag(R.id.tag_class, "CommentControlView");
				this.commentControlView.setOnTouchListener(thisController.mOnTouchListener);
				this.commentControlView.setOnClickListener(thisController.mOnClickListener);

				if (comments.size() == 0) {
					this.commentContentView.setVisibility(View.GONE);
					this.commentsPointView.setVisibility(View.GONE);
				} else {
					this.commentContentView.setVisibility(View.VISIBLE);
					this.commentsPointView.setVisibility(View.VISIBLE);
					this.commentsPointView.setX(18 * displayMetrics.density);
				}

				if (commentContainer.getChildCount() != comments.size()) {
					this.commentContainer.removeAllViews();
					int index = 0;
					for (int i = comments.size() - 1; i >= 0; i--) {
						Comment comment = comments.get(i);
						TouchImageView imageView = new TouchImageView(thisController.thisActivity);
						int padding = (int) (6 * displayMetrics.density);
						imageView.setPadding(padding, padding, padding, padding);
						int width = (int) (45 * displayMetrics.density);
						FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(width, width);
						taskManageHolder.fileHandler.getHeadImage(comment.head, imageView, taskManageHolder.viewManage.options40);
						this.commentContainer.addView(imageView, params2);
						imageView.setX(index * 45 * displayMetrics.density + 6 * displayMetrics.density);
						index++;
						imageView.setTag("CommentHeadView#" + shareMessage.gsid);
						imageView.setTag(R.id.tag_class, "CommentHeadView");
						imageView.setTag(R.id.tag_first, i);
						imageView.setOnTouchListener(thisController.mOnTouchListener);
						imageView.setOnClickListener(thisController.mOnClickListener);
						if (i < comments.size() - 4) {
							break;
						}
						if (i == comments.size() - 1) {
							this.commentContentView.setText(comment.content);
						}
					}
					if (comments.size() > 5) {
						TextView textView = new TextView(thisController.thisActivity);
						textView.setTextColor(Color.parseColor("#33000000"));
						textView.setGravity(Gravity.CENTER);
						textView.setText("" + comments.size() + " 条...");
						int width = (int) (45 * displayMetrics.density);
						FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(width, width);
						this.commentContainer.addView(textView, params2);
						textView.setX(5 * 45 * displayMetrics.density + 10 * displayMetrics.density);
						textView.setY(4 * displayMetrics.density);
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

	public ShareSubController1 thisController;

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
		setGroupsDialogContent(null);
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

	public void setGroupsDialogContent(GroupCircle groupCircle) {
		data = parser.check();
		if (data.relationship == null || data.relationship.groupCircles == null || data.relationship.groupCirclesMap == null || data.localStatus == null || data.localStatus.localData == null) {
			log.e("return groups or localData");
			return;
		}
		if (groupCircle == null) {
			String gid = data.localStatus.localData.currentGroupCircle;
			if (gid == null || "".equals(gid))
				gid = data.relationship.groupCircles.get(0);
			groupCircle = data.relationship.groupCirclesMap.get(gid);
		}
		// log.e("------------------------------*******00000000000：" + groupCircle);
		currentGroupCircle = groupCircle;
		if (groupCircle == null || groupCircle.groups == null) {
			log.e("return groups or localData");
			return;
		}
		boolean flag = groupCircle.groups.contains(data.localStatus.localData.currentSelectedGroup);
		if (!flag) {
			if (groupCircle.groups.size() == 0) {
				data.localStatus.localData.currentSelectedGroup = "";
				data.relationship.isModified = true;
			} else {
				data.localStatus.localData.currentSelectedGroup = groupCircle.groups.get(0);
				getCurrentGroupShareMessages();
			}
		}
		Group group0 = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		if (group0 != null) {
			// data.localStatus.localData.currentSelectedGroupBoard = group0.boards.get(0);
			this.shareTopMenuGroupName.setText(group0.name);
			this.setMenuNameBotton(group0.name);
		} else {
			this.shareTopMenuGroupName.setText("暂无群组");
			this.setMenuNameBotton("暂无群组");
		}

		List<String> groups = groupCircle.groups;
		Map<String, Group> groupsMap = data.relationship.groupsMap;
		groupsDialogContent.removeAllViews();
		this.groupListBody.containerView.removeAllViews();
		this.groupListBody.height = 0;
		groupListBody.listItemsSequence.clear();
		for (int i = 0; i < groups.size(); i++) {
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
			view.setY(groupDialogItem.y);
			view.setX(0);
			this.groupListBody.height = this.groupListBody.height + 60 * displayMetrics.density;
			this.groupListBody.containerView.addView(view, layoutParams);

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

		public TextView followView;

		public Group group;

		public View initialize() {
			this.cardView = mainView.mInflater.inflate(R.layout.share_group_select_dialog_item, null);
			this.groupIconView = (ImageView) this.cardView.findViewById(R.id.groupIcon);
			this.groupNameView = (TextView) this.cardView.findViewById(R.id.groupName);
			this.groupSelectedStatusView = (ImageView) this.cardView.findViewById(R.id.groupSelectedStatus);
			this.followView = (TextView) this.cardView.findViewById(R.id.follow);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Group group) {
			data = parser.check();
			this.group = group;
			taskManageHolder.fileHandler.getHeadImage(group.icon, this.groupIconView, taskManageHolder.viewManage.options40);
			if ("follow".equals(group.relation)) {
				this.groupNameView.setTextColor(Color.parseColor("#0099cd"));
				this.followView.setVisibility(View.GONE);
			} else {
				this.followView.setVisibility(View.GONE);
			}
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

	public void showTopMenuRoomName() {
		data = parser.check();
		groupMembersListContentView.removeAllViews();
		if (data.relationship == null || data.relationship.groupsMap == null || data.localStatus == null || data.localStatus.localData == null) {
			return;
		}
		Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		if (group != null) {
			shareTopMenuGroupName.setText(group.name);
			this.setMenuNameBotton(group.name);
		} else {
			shareTopMenuGroupName.setText("暂无群组");
			this.setMenuNameBotton("暂无群组");
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
