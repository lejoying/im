package com.open.welinks.view;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.controller.GroupInfoController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.oss.DownloadFileList;

public class GroupInfoView {
	public Data data = Data.getInstance();
	public String tag = "GroupInfoView";
	public MyLog log = new MyLog(tag, true);

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public Context context;
	public GroupInfoView thisView;
	public GroupInfoController thisController;
	public Activity thisActivity;

	public View backView;
	public TextView backTitleView, exitDeleteText;

	public View headOptionView;
	public ImageView headIvView;
	public View nickNameOptionView;
	public TextView nickNameView;
	public View businessOptionView;
	public TextView businessView;
	public View coverOptionView;
	public View addressOptionView;
	public TextView addressView;
	public View newMessageSettingOptionView;
	public SeekBar newMessageSettingBar;
	public View permissionOptionView;
	public View inCardOptionView;
	public SeekBar inCardBar;
	public View exit2DeleteGroup;
	public View memberListTopView;
	public LinearLayout memberListView;
	public View cardOptionView, labelLayout;
	public View borderOne, borderTwo, borderThree, borderFour, borderFive, borderSix, borderSeven, borderEight, borderNine;

	public ImageView converImageView;

	public TextView groupMemberCountView;

	public GroupInfoView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisView = this;
		this.thisActivity = thisActivity;
	}

	DisplayMetrics displayMetrics;

	public void initView() {
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		this.thisActivity.setContentView(R.layout.activity_group_info);
		this.backView = thisActivity.findViewById(R.id.backView);
		this.backTitleView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		this.headOptionView = thisActivity.findViewById(R.id.headOption);
		this.headIvView = (ImageView) thisActivity.findViewById(R.id.headIv);
		this.nickNameOptionView = thisActivity.findViewById(R.id.nickNameOption);
		this.nickNameView = (TextView) thisActivity.findViewById(R.id.nickNameTx);
		this.businessOptionView = thisActivity.findViewById(R.id.businessOption);
		this.businessView = (TextView) thisActivity.findViewById(R.id.businessTx);
		this.coverOptionView = thisActivity.findViewById(R.id.converOption);
		this.addressOptionView = thisActivity.findViewById(R.id.addressOption);
		this.addressView = (TextView) thisActivity.findViewById(R.id.addressTx);
		this.exitDeleteText = (TextView) thisActivity.findViewById(R.id.exitDeleteText);
		this.newMessageSettingOptionView = thisActivity.findViewById(R.id.newMessageSettingOption);
		this.newMessageSettingBar = (SeekBar) thisActivity.findViewById(R.id.newMessageSettingBar);
		this.newMessageSettingBar.setTag("newMessageSettingBar");
		this.permissionOptionView = thisActivity.findViewById(R.id.permissionOption);
		this.inCardOptionView = thisActivity.findViewById(R.id.inCardOption);
		this.inCardBar = (SeekBar) thisActivity.findViewById(R.id.inCardBar);
		this.inCardBar.setTag("inCardBar");
		this.exit2DeleteGroup = thisActivity.findViewById(R.id.exit2DeleteGroup);
		this.groupMemberCountView = (TextView) thisActivity.findViewById(R.id.groupMemberCount);
		this.memberListTopView = thisActivity.findViewById(R.id.memberListTop);
		this.memberListView = (LinearLayout) thisActivity.findViewById(R.id.memberList);
		this.cardOptionView = thisActivity.findViewById(R.id.cardOption);
		this.converImageView = (ImageView) thisActivity.findViewById(R.id.converImage);
		this.borderOne = thisActivity.findViewById(R.id.borderOne);
		this.borderTwo = thisActivity.findViewById(R.id.borderTwo);
		this.borderThree = thisActivity.findViewById(R.id.borderThree);
		this.borderFour = thisActivity.findViewById(R.id.borderFour);
		this.borderFive = thisActivity.findViewById(R.id.borderFive);
		this.borderSix = thisActivity.findViewById(R.id.borderSix);
		this.borderSeven = thisActivity.findViewById(R.id.borderSeven);
		this.borderEight = thisActivity.findViewById(R.id.borderEight);
		this.borderNine = thisActivity.findViewById(R.id.borderNine);
		this.labelLayout = thisActivity.findViewById(R.id.labelLayout);
	}

	public void setMembersList() {

		int size = thisController.currentGroup.members.size();
		if (size >= 5) {
			size = 5;
		}
		int height = (int) (50 * displayMetrics.density + 0.5f);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height, height);
		params.leftMargin = (int) (14 * displayMetrics.density + 0.5f);
		this.memberListView.removeAllViews();
		for (int i = 0; i < size; i++) {
			ImageView imageView = new ImageView(thisActivity);

			Friend friend = data.relationship.friendsMap.get(thisController.currentGroup.members.get(i));
			taskManageHolder.fileHandler.getHeadImage(friend.head, imageView, viewManage.options70);
			this.memberListView.addView(imageView, params);
		}
	}

	ViewManage viewManage = ViewManage.getInstance();

	public void setData() {
		if ("board".equals(thisController.type)) {
			this.backTitleView.setText("版块信息");
			this.exitDeleteText.setText("删除版块");
			this.memberListView.setVisibility(View.GONE);
			this.memberListTopView.setVisibility(View.GONE);
			this.newMessageSettingOptionView.setVisibility(View.GONE);
			this.permissionOptionView.setVisibility(View.GONE);
			this.inCardOptionView.setVisibility(View.GONE);
			this.borderOne.setVisibility(View.GONE);
			this.borderTwo.setVisibility(View.GONE);
			this.borderThree.setVisibility(View.GONE);
			this.borderFour.setVisibility(View.GONE);
			this.borderFive.setVisibility(View.GONE);
			this.borderSix.setVisibility(View.GONE);
			this.borderSeven.setVisibility(View.GONE);
			this.borderEight.setVisibility(View.GONE);
			this.borderNine.setVisibility(View.GONE);

			this.nickNameView.setText(thisController.currentBoard.name);
			taskManageHolder.fileHandler.getHeadImage(thisController.currentBoard.head, this.headIvView, viewManage.options70);
			this.businessView.setText(thisController.currentBoard.description);
			setConver();
		} else {
			this.backTitleView.setText("群组信息");
			this.nickNameView.setText(thisController.currentGroup.name);
			taskManageHolder.fileHandler.getHeadImage(thisController.currentGroup.icon, this.headIvView, viewManage.options70);
			this.businessView.setText(thisController.currentGroup.description);
			boolean isNotice = false;
			if (data.localStatus.localData != null) {
				if (data.localStatus.localData.newMessagePowerMap != null) {
					if (data.localStatus.localData.newMessagePowerMap.get(thisController.currentGroup.gid + "") != null) {
						isNotice = data.localStatus.localData.newMessagePowerMap.get(thisController.currentGroup.gid + "");
					} else {
						isNotice = true;
					}
				} else {
					isNotice = true;
					data.localStatus.localData.newMessagePowerMap = new HashMap<String, Boolean>();
				}
			} else {
				isNotice = true;
			}
			if (!isNotice) {
				this.newMessageSettingBar.setProgress(0);
			} else {
				this.newMessageSettingBar.setProgress(100);
			}
			groupMemberCountView.setText(thisController.currentGroup.members.size() + "人");
			setMembersList();
			setConver();
		}
	}

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public void setConver() {
		String tempCover = "";
		if ("board".equals(thisController.type)) {
			tempCover = thisController.currentBoard.cover;
		} else if ("group".equals(thisController.type)) {
			tempCover = thisController.currentGroup.cover;
		}
		final String cover = tempCover;
		if (cover == null || "".equals(cover)) {
			imageLoader.displayImage("drawable://" + R.drawable.tempicon, converImageView);
			return;
		}
		File file = new File(taskManageHolder.fileHandler.sdcardBackImageFolder, cover);
		final String path = file.getAbsolutePath();
		if (file.exists()) {
			imageLoader.displayImage("file://" + path, converImageView, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					downloadConver(cover, path);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				}
			});
		} else {
			downloadConver(cover, path);
		}
	}

	public void downloadConver(String converName, String path) {
		converImageView.setTag("conver");
		String url = API.DOMAIN_COMMONIMAGE + "backgrounds/" + converName;
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = converImageView;
		downloadFile.setDownloadFileListener(thisController.downloadListener);
		downloadFileList.addDownloadFile(downloadFile);
	}
}
