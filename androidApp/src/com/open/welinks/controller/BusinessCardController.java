package com.open.welinks.controller;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.AddFriendActivity;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.ChatActivity;
import com.open.welinks.ModifyInformationActivity;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog;
import com.open.welinks.view.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.view.BusinessCardView;
import com.open.welinks.view.BusinessCardView.Status;

public class BusinessCardController {

	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	public Data data = Data.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public DownloadFile downloadFile;
	public DisplayImageOptions options;

	public String key, type;

	public OnClickListener mOnClickListener;
	public DownloadListener downloadListener;
	public DisplayMetrics displayMetrics;

	public Handler handler;

	public static final int REQUESTCODE_MODIFY = 0x1, REQUESTCODE_ADD = 0x2;

	public BusinessCardController(BusinessCardActivity activity) {
		thisActivity = activity;
		thisController = this;
	}

	public void onCreate() {
		key = thisActivity.getIntent().getStringExtra("key");
		type = thisActivity.getIntent().getStringExtra("type");
		if ("point".equals(type)) {
			if (key.equals(data.userInformation.currentUser.phone)) {
				thisView.status = Status.SELF;
			} else if (data.relationship.friends.contains(key)) {
				thisView.status = Status.FRIEND;
			} else {
				thisView.status = Status.TEMPFRIEND;
			}
		} else if ("group".equals(type)) {
			if (data.relationship.groups.contains(key)) {
				thisView.status = Status.JOINEDGROUP;
			} else {
				thisView.status = Status.NOTJOINGROUP;
			}
		} else if ("square".equals(type)) {
			thisView.status = Status.SQUARE;
		}
		handler = new Handler();
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer(40)).build();
		initializeListeners();
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		thisView.spacing_one.setHeight(displayMetrics.heightPixels - thisView.spacing_two.getHeight() - thisView.infomation_layout.getHeight() - thisView.backview.getHeight() - data.tempData.statusBarHeight);
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backview)) {
					thisActivity.finish();
				} else if (view.equals(thisView.button_one)) {
					if (thisView.status.equals(Status.SELF)) {
						modifyInformation();
					} else if (thisView.status.equals(Status.FRIEND)) {
						startChat("point");
					} else if (thisView.status.equals(Status.JOINEDGROUP)) {
						startChat("group");
					} else if (thisView.status.equals(Status.TEMPFRIEND)) {
						addFriend();
					} else if (thisView.status.equals(Status.NOTJOINGROUP)) {
						joinGroup();
					} else if (thisView.status.equals(Status.SQUARE)) {
						// unused
					}
				} else if (view.equals(thisView.button_two)) {
					if (thisView.status.equals(Status.SELF)) {
						// unused
					} else if (thisView.status.equals(Status.FRIEND)) {
						modifyAlias();
					} else if (thisView.status.equals(Status.JOINEDGROUP)) {
						modifyInformation();
					} else if (thisView.status.equals(Status.TEMPFRIEND)) {
						// unused
					} else if (thisView.status.equals(Status.NOTJOINGROUP)) {
						// unused
					} else if (thisView.status.equals(Status.SQUARE)) {
						// unused
					}
				} else if (view.equals(thisView.button_three)) {
					if (thisView.status.equals(Status.SELF)) {
						// unused
					} else if (thisView.status.equals(Status.FRIEND)) {
						terminateRelationship();
					} else if (thisView.status.equals(Status.JOINEDGROUP)) {
						// unused
					} else if (thisView.status.equals(Status.TEMPFRIEND)) {
						// unused
					} else if (thisView.status.equals(Status.NOTJOINGROUP)) {
						// unused
					} else if (thisView.status.equals(Status.SQUARE)) {
						// unused
					}
				}

			}
		};
		downloadListener = new DownloadListener() {

			@Override
			public void success(DownloadFile instance, int status) {

			}

			@Override
			public void loading(DownloadFile instance, int precent, int status) {

			}

			@Override
			public void failure(DownloadFile instance, int status) {
				// TODO Auto-generated method stub
				
			}
		};
		bindEvent();
	}

	public void bindEvent() {
		thisView.backview.setOnClickListener(mOnClickListener);
		thisView.button_one.setOnClickListener(mOnClickListener);
		thisView.button_two.setOnClickListener(mOnClickListener);
		thisView.button_three.setOnClickListener(mOnClickListener);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUESTCODE_MODIFY && resultCode == Activity.RESULT_OK) {
			thisView.fillData();
		} else if (requestCode == REQUESTCODE_ADD && resultCode == Activity.RESULT_OK) {

		}
	}

	public void modifyInformation() {
		Intent intent = new Intent(thisActivity, ModifyInformationActivity.class);
		intent.putExtra("key", key);
		intent.putExtra("type", type);
		thisActivity.startActivityForResult(intent, REQUESTCODE_MODIFY);
	}

	public void startChat(String type) {
		Intent intent = new Intent(thisActivity, ChatActivity.class);
		intent.putExtra("id", key);
		intent.putExtra("type", type);
		thisActivity.startActivity(intent);
	}

	public void modifyAlias() {
		final Friend friend = thisController.data.relationship.friendsMap.get(key);
		AlertInputDialog alert = new AlertInputDialog(thisActivity);
		alert.setTitle("请输入好友的备注").setLeftButtonText("修改").setInputText(friend.alias).setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String alias = dialog.getInputText();
				friend.alias = alias;
				thisView.fillData();
				uploadAlias(alias);
			}

		}).show();

	}

	public void terminateRelationship() {
		final Friend friend = thisController.data.relationship.friendsMap.get(key);
		Alert.createDialog(thisActivity).setTitle("确定解除和" + friend.nickName + "的好友关系吗？").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				thisController.data.relationship.friendsMap.remove(key);
				thisView.status = Status.TEMPFRIEND;
				thisView.fillData();
				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("phone", data.userInformation.currentUser.phone);
				params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
				params.addBodyParameter("phoneto", "[\"" + friend.phone + "\"]");
				ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
				httpUtils.send(HttpMethod.POST, API.RELATION_DELETEFRIEND, params, responseHandlers.relation_deletefriend);
			}
		}).show();
	}

	public void setHeadImage(String fileName, ImageView view) {
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "welinks/heads/" + fileName);
		final String url = API.DOMAIN_COMMONIMAGE + "heads/" + fileName;
		final String path = file.getAbsolutePath();
		imageLoader.displayImage("file://" + path, view, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				downloadFile = new DownloadFile(url, path);
				downloadFile.view = view;
				downloadFile.setDownloadFileListener(thisController.downloadListener);
				downloadFileList.addDownloadFile(downloadFile);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}
		});
	}

	public void addFriend() {
		Intent intent = new Intent(thisActivity, AddFriendActivity.class);
		intent.putExtra("key", key);
		thisActivity.startActivityForResult(intent, REQUESTCODE_ADD);
	}

	public void joinGroup() {
		if (data.relationship.groupsMap.get(key) == null) {
			Group group = data.relationship.new Group();
			data.relationship.groupsMap.put(key, group);
			thisView.status = Status.JOINEDGROUP;
			thisView.fillData();
			HttpUtils httpUtils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("phone", data.userInformation.currentUser.phone);
			params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
			params.addBodyParameter("gid", key);
			params.addBodyParameter("members", "[\"" + data.userInformation.currentUser.phone + "\"]");
			ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
			httpUtils.send(HttpMethod.POST, API.GROUP_ADDMEMBERS, params, responseHandlers.group_addmembers);
		} else {
			Alert.showMessage("已申请加入改群组");
		}
	}

	public void uploadAlias(String alias) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("friend", key);
		params.addBodyParameter("alias", alias);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.RELATION_MODIFYALIAS, params, responseHandlers.relation_modifyAlias);

	}
}
