package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.ShareSubView;
import com.open.welinks.view.ShareSubView.SharesMessageBody;

public class ShareSubController {

	public Data data = Data.getInstance();
	public String tag = "ShareSubController";
	public ShareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchBackColorListener;
	public OnTouchListener mOnTouchListener;
	public DownloadListener downloadListener;
	public View onTouchDownView;

	public int SCAN_MESSAGEDETAIL = 0x01;
	public String currentScanMessageKey;

	public int nowpage = 0;
	public int pagesize = 20;

	public ShareSubController(MainController mainController) {
		thisActivity = mainController.thisActivity;

		this.mainController = mainController;

		mGesture = new GestureDetector(thisActivity, new GestureListener());
	}

	public void initializeListeners() {

		downloadListener = new DownloadListener() {

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void success(DownloadFile instance, int status) {
				DisplayImageOptions options = thisView.options;
				if (instance.view.getTag() != null) {
					options = thisView.displayImageOptions;
				}
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, options);
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// return onTouchEvent(event);
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("share_view")) {
						onTouchDownView = view;
					}
					Log.i(tag, "ACTION_DOWN");
					// thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
				}
				return false;
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.shareTopMenuGroupNameParent)) {
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
	}

	public void bindEvent() {
		thisView.shareTopMenuGroupNameParent.setOnClickListener(mOnClickListener);
		// thisView.groupDialogView.setOnClickListener(mOnClickListener);
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

	public GestureDetector mGesture;

	//
	// public boolean onTouchEvent(MotionEvent event) {
	//
	// int motionEvent = event.getAction();
	// if (motionEvent == MotionEvent.ACTION_DOWN) {
	// Log.d(tag, "Activity on touch down");
	// thisView.groupListBody.onTouchDown(event);
	// } else if (motionEvent == MotionEvent.ACTION_MOVE) {
	// thisView.groupListBody.onTouchMove(event);
	// } else if (motionEvent == MotionEvent.ACTION_UP) {
	// thisView.groupListBody.onTouchUp(event);
	// }
	// mGesture.onTouchEvent(event);
	// return true;
	// }

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.i("GestureListener", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);

			if (thisView.groupListBody.bodyStatus.state == thisView.groupListBody.bodyStatus.DRAGGING) {
				thisView.groupListBody.onFling(velocityX, velocityY);
			}
			return true;
		}
	}

	public void onScroll() {
		onTouchDownView = null;
	}

	public void onSingleTapUp(MotionEvent event) {
		if (onTouchDownView != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("share_view")) {
				onTouchDownView.performClick();
			}
		}
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
