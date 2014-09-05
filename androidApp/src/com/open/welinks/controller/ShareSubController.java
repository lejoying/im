package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.viewbody.BodyCallback;
import com.open.welinks.GroupInfomationActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.ShareSubView;
import com.open.welinks.view.ShareSubView.GroupDialogItem;
import com.open.welinks.view.ShareSubView.SharesMessageBody;

public class ShareSubController {

	public Data data = Data.getInstance();
	public String tag = "ShareSubController";
	public MyLog log = new MyLog(tag, true);

	public ShareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchBackColorListener;
	public OnTouchListener mOnTouchListener;
	public DownloadListener downloadListener;
	public BodyCallback bodyCallback;

	public View onTouchDownView;
	public View onLongPressView;;

	public boolean isTouchDown = false;

	public Group onTouchDownGroup;

	public int SCAN_MESSAGEDETAIL = 0x01;
	public String currentScanMessageKey;

	public int nowpage = 0;
	public int pagesize = 20;

	public Gson gson = new Gson();

	public ShareSubController(MainController mainController) {
		thisActivity = mainController.thisActivity;

		this.mainController = mainController;
	}

	public void initializeListeners() {

		downloadListener = new DownloadListener() {

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void success(final DownloadFile instance, int status) {
				DisplayImageOptions options = thisView.options;
				if (instance.view.getTag() != null) {
					options = thisView.displayImageOptions;
				}
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						Log.e(tag, "---------------failed");
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
						instance.view.setLayoutParams(params);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				});
			}

			@Override
			public void failure(DownloadFile instance, int status) {
				if (instance.view.getTag() != null) {
					if ("image".equals(instance.view.getTag().toString())) {
						Log.e(tag, "---------------failure" + instance.view.getTag().toString());
						ImageView imageView = ((ImageView) (instance.view));
						imageView.setImageResource(R.drawable.ic_error);
						// RelativeLayout.LayoutParams params = (LayoutParams) imageView.getLayoutParams();
						// params.height = 10;
						// imageView.setLayoutParams(params);
						// imageView.setBackgroundColor(Color.RED);
					}
				}
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// return onTouchEvent(event);
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					if (isTouchDown) {
						return false;
					}
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("share_view")) {
						onTouchDownView = view;
						onLongPressView = view;
						isTouchDown = true;
					} else if (view_class.equals("group_view")) {
						// group dialog item onTouch
						onTouchDownView = view;
						isTouchDown = true;
						Object viewTag = view.getTag(R.id.tag_first);
						if (Group.class.isInstance(viewTag) == true) {
							Group group = (Group) viewTag;
							Log.d(tag, "onTouch: gid:" + group.gid + "name" + group.name);

							onTouchDownGroup = group;
						} else {
							Log.d(tag, "onTouch: " + (String) viewTag);
						}
					}

					Log.i(tag, "ACTION_DOWN---" + view_class);
					// thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
				}
				return false;
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.leftImageButton)) {
					Intent intent = new Intent(thisActivity, GroupInfomationActivity.class);
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.shareTopMenuGroupNameParent)) {
					thisView.showGroupsDialog();
				} else if (view.equals(thisView.groupDialogView)) {
					thisView.dismissGroupDialog();
				} else if (view.equals(thisView.releaseShareView)) {
					thisView.showReleaseShareDialogView();
				} else if (view.equals(thisView.releaseShareDialogView)) {
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageTextButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					mainController.thisActivity.startActivity(intent);
					thisView.dismissReleaseShareDialogView();
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("GroupDialogContentItem".equals(type)) {
						// modify data
						data.localStatus.localData.currentSelectedGroup = content;
						// modify UI
						thisView.dismissGroupDialog();
						Group group = data.relationship.groupsMap.get(content);
						TextView shareTopMenuGroupName = (TextView) view.getTag(R.id.shareTopMenuGroupName);
						data.localStatus.localData.currentSelectedGroup = group.gid + "";
						shareTopMenuGroupName.setText(group.name);
						thisView.modifyCurrentShowGroup();
						getCurrentGroupShareMessages();
						thisView.showGroupMembers();
					} else if ("ShareMessageDetail".equals(type)) {
						Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
						intent.putExtra("gsid", content);
						currentScanMessageKey = content;
						thisActivity.startActivityForResult(intent, SCAN_MESSAGEDETAIL);
					}
				}
			}
		};
		onTouchBackColorListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (view == thisView.releaseImageTextButton) {
					int motionEvent = event.getAction();
					if (motionEvent == MotionEvent.ACTION_DOWN) {
						view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					} else if (motionEvent == MotionEvent.ACTION_UP) {
						view.setBackgroundColor(Color.parseColor("#00000000"));
					}
				}
				return false;
			}
		};
		bodyCallback = new BodyCallback() {
			@Override
			public void onStopOrdering(List<String> listItemsSequence) {
				super.onStopOrdering(listItemsSequence);
				log.e(tag, listItemsSequence.toString());
				List<String> gidsList = new ArrayList<String>();
				for (int i = 0; i < listItemsSequence.size(); i++) {
					String key = listItemsSequence.get(i);
					gidsList.add(key.substring(key.indexOf("#") + 1, key.indexOf("_")));
				}
				// modify local data
				data.relationship.groups = gidsList;
				String sequenceListString = gson.toJson(gidsList);
				// modify server data
				modifyGroupSequence(sequenceListString);
			}
		};
	}

	public void bindEvent() {
		thisView.groupListBody.bodyCallback = this.bodyCallback;
		thisView.leftImageButton.setOnClickListener(mOnClickListener);
		thisView.shareTopMenuGroupNameParent.setOnClickListener(mOnClickListener);
		// thisView.groupDialogView.setOnClickListener(mOnClickListener);
	}

	public void modifyGroupSequence(String sequenceListString) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("sequence", sequenceListString);

		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFYGROUPSEQUENCE, params, responseHandlers.modifyGroupSequenceCallBack);
	}

	public void getCurrentGroupShareMessages() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", data.localStatus.localData.currentSelectedGroup);
		params.addBodyParameter("nowpage", nowpage + "");
		params.addBodyParameter("pagesize", pagesize + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack);
	}

	public void getUserCurrentAllGroup() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);

		httpUtils.send(HttpMethod.POST, API.GROUP_GETGROUPMEMBERS, params, responseHandlers.getGroupMembersCallBack);
	}

	public void onScroll() {
		onTouchDownView = null;
	}

	public void onLongPress(MotionEvent event) {
		if (onTouchDownView != null && onTouchDownGroup != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("group_view")) {

				Group group = data.relationship.groupsMap.get("" + onTouchDownGroup.gid);
				GroupDialogItem groupDialogItem = (GroupDialogItem) thisView.groupListBody.listItemBodiesMap.get("group#" + group.gid + "_" + group.name);

				groupDialogItem.gripCardBackground.setVisibility(View.VISIBLE);

				Vibrator vibrator = (Vibrator) this.mainController.thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
				long[] pattern = { 100, 100, 300 };
				vibrator.vibrate(pattern, -1);

				thisView.groupListBody.startOrdering("group#" + group.gid + "_" + group.name);

				onLongPressView = onTouchDownView;
				onTouchDownView = null;
			}
		}
	}

	public void onSingleTapUp(MotionEvent event) {
		if (onLongPressView != null) {
			if (onTouchDownGroup != null) {
				Group group = data.relationship.groupsMap.get("" + onTouchDownGroup.gid);
				GroupDialogItem groupDialogItem = (GroupDialogItem) thisView.groupListBody.listItemBodiesMap.get("group#" + group.gid + "_" + group.name);
				groupDialogItem.gripCardBackground.setVisibility(View.INVISIBLE);

				onLongPressView = null;
				onTouchDownGroup = null;
				thisView.groupListBody.stopOrdering();
			}
		}
		if (onTouchDownView != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("share_view")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_view")) {
				onTouchDownView.performClick();
			}
			onTouchDownView = null;
		}
		isTouchDown = false;
	}

	public void onActivityResult(int requestCode, int resultCode, Data data2) {
		if (requestCode == SCAN_MESSAGEDETAIL) {
			if (thisView.shareMessageListBody != null) {
				if (thisView.shareMessageListBody.listItemsSequence.size() > 0) {
					if (currentScanMessageKey != null) {
						SharesMessageBody body = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + currentScanMessageKey);
						if (body != null) {
							body.setContent(body.message);
						}
					}
				}
			}
		}
	}
}