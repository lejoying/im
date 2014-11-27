package com.open.welinks.controller;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.TaskContainer_Share;
import com.open.welinks.model.TaskContainer_Share.GetShares;
import com.open.welinks.model.TaskContainer_Share.Praise;
import com.open.welinks.model.TaskManager;
import com.open.welinks.view.ShareSectionView;

public class ShareSectionController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ShareSectionController";
	public MyLog log = new MyLog(tag, true);

	public TaskContainer_Share mTaskContainer_Share = new TaskContainer_Share();
	public TaskManager mTaskManager = TaskManager.getInstance();

	public Context context;
	public ShareSectionView thisView;
	public ShareSectionController thisController;
	public Activity thisActivity;

	public OnDownloadListener downloadListener;
	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public GestureDetector mGesture;

	public View onTouchDownView;

	public int SCAN_MESSAGEDETAIL = 0x01;
	public String currentScanMessageKey;

	public BodyCallback shareBodyCallback;

	public int nowpage = 0;
	public int pagesize = 10;

	public ShareSectionController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisController = this;
	}

	public class ReflashStatus {
		public int Normal = 1, Reflashing = 2, Failed = 3;
		public int state = Normal;
	}

	public ReflashStatus reflashStatus = new ReflashStatus();

	public void onCrate() {
		mGesture = new GestureDetector(thisActivity, new GestureListener());
	}

	public void getCurrentGroupShareMessages() {
		// RequestParams params = new RequestParams();
		// HttpUtils httpUtils = new HttpUtils();
		// params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		// params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		// params.addBodyParameter("gid", data.localStatus.localData.currentSelectedGroup);
		// params.addBodyParameter("sid", thisView.currentGroup.currentBoard);
		// params.addBodyParameter("nowpage", nowpage + "");
		// params.addBodyParameter("pagesize", pagesize + "");
		//
		// httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack2);
	}

	public void initializeListeners() {
		shareBodyCallback = new BodyCallback() {
			@Override
			public void onRefresh(int direction) {
				super.onRefresh(direction);
				if (reflashStatus.state != reflashStatus.Reflashing) {
					reflashStatus.state = reflashStatus.Reflashing;
					GetShares task = mTaskContainer_Share.new GetShares();
					task.API = API.SHARE_GETSHARES;
					task.gid = thisView.currentGroup.gid + "";
					task.sid = thisView.currentGroup.currentBoard;
					task.pagesize = pagesize;
					if (direction == 1) {
						nowpage = 0;
						task.nowpage = nowpage;
						mTaskManager.pushTask(task);
					} else if (direction == -1) {
						nowpage++;
						task.nowpage = nowpage;
						mTaskManager.pushTask(task);
					}
					thisView.showRoomTime();
				}
			}
		};
		downloadListener = new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
			}
		};
		mOnClickListener = new MyOnClickListener() {

			@Override
			public void onClickEffective(View view) {
				isTouchDown = false;
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.rightContainer)) {
					// if (thisView.selectMenuView.getVisibility() == View.VISIBLE) {
					// thisView.selectMenuView.setVisibility(View.GONE);
					// } else {
					// thisView.selectMenuView.setVisibility(View.VISIBLE);
					// }
					if (thisView.isShowGroupDialog) {
						thisView.dismissGroupBoardsDialog();
					} else {
						thisView.showGroupBoardsDialog();
					}
					view.setTag(R.id.time, null);
				} else if (view.equals(thisView.pop_out_background1) || view.equals(thisView.pop_out_background2)) {
					thisView.dismissGroupBoardsDialog();
				} else if (view.equals(thisView.groupDialogView)) {
					thisView.groupDialogView.isIntercept = false;
					thisView.dismissGroupBoardsDialog();
				} else if (view.equals(thisView.releaseShareView)) {
					Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
					long[] pattern = { 30, 100, 30 };
					vibrator.vibrate(pattern, -1);

					thisView.showReleaseShareDialogView();
				} else if (view.equals(thisView.groupHeadView) || view.equals(thisView.groupCoverView)) {
					if (data.localStatus.localData != null && data.localStatus.localData.currentSelectedGroup != null && !data.localStatus.localData.currentSelectedGroup.equals("")) {
						parser.check();
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_GROUP, data.localStatus.localData.currentSelectedGroup);
						thisView.businessCardPopView.cardView.setHot(false);
						thisView.businessCardPopView.showUserCardDialogView();
					}
				} else if (view.equals(thisView.releaseShareDialogView)) {
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseTextButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "text");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseAlbumButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "album");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageViewButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "imagetext");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("ShareMessageDetail".equals(type)) {
						Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
						intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
						intent.putExtra("sid", thisView.currentGroup.currentBoard);
						intent.putExtra("gsid", content);
						currentScanMessageKey = content;
						thisActivity.startActivityForResult(intent, SCAN_MESSAGEDETAIL);
						// thisActivity.overridePendingTransition(R.anim.zoomin,
						// R.anim.zoomout);
					} else if ("ShareMessage".equals(type)) {
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent("point", content);
						thisView.businessCardPopView.showUserCardDialogView();
					} else if ("SharePraise".equals(type)) {

						Praise praise = mTaskContainer_Share.new Praise();
						praise.gsid = content;
						praise.thisView = thisView;
						praise.gid = data.localStatus.localData.currentSelectedGroup;
						praise.API = API.SHARE_ADDPRAISE;
						mTaskManager.pushTask(praise);

					}
				}
				onTouchDownView = null;
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					if (isTouchDown) {
						return false;
					}
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("group_head")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("share_release")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("share_head")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("share_praise")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("share_view")) {
						onTouchDownView = view;
						// onLongPressView = view;
						isTouchDown = true;
					} else if (view_class.equals("group_view")) {
						// log.e("---------------ondow view_class");
						// group dialog item onTouch
						onTouchDownView = view;
						isTouchDown = true;
						Object viewTag = view.getTag(R.id.tag_first);
						if (Group.class.isInstance(viewTag) == true) {
							Group group = (Group) viewTag;
							Log.d(tag, "onTouch: gid:" + group.gid + "name" + group.name);

							// onTouchDownGroup = group;
						} else {
							thisView.dismissGroupBoardsDialog();
							Log.d(tag, "onTouch: " + (String) viewTag);
						}
					}
				}
				return false;
			}
		};
	}

	public boolean isTouchDown = false;

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.rightContainer.setOnClickListener(mOnClickListener);
		thisView.shareMessageListBody.bodyCallback = this.shareBodyCallback;
		thisView.pop_out_background1.setOnClickListener(mOnClickListener);
		thisView.pop_out_background2.setOnClickListener(mOnClickListener);
		thisView.groupDialogView.setOnClickListener(mOnClickListener);
		thisView.groupDialogView.setOnTouchListener(mOnTouchListener);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			thisView.shareMessageListBody.onTouchDown(event);
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.shareMessageListBody.onTouchMove(event);
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			onSingleTapUp(event);
			thisView.shareMessageListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	private void onSingleTapUp(MotionEvent event) {
		if (onTouchDownView != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("share_view")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("share_release")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_head")) {
				onTouchDownView.performClick();//
			} else if (view_class.equals("share_head")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("share_praise")) {
				onTouchDownView.performClick();
			}
			onTouchDownView = null;
		}
		isTouchDown = false;
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			ListBody1 listBody = thisView.shareMessageListBody;
			if (listBody != null) {
				if (listBody.bodyStatus.state == listBody.bodyStatus.DRAGGING) {
					listBody.onFling(velocityX, velocityY);
				} else if (listBody.bodyStatus.state == listBody.bodyStatus.FIXED) {
					listBody.onFling(velocityX, velocityY);
				} else {
					Log.i(tag, "bodyStatus error:" + listBody.bodyStatus.state);
				}
			}

			return true;
		}

		public void onLongPress(MotionEvent event) {
		}

		public boolean onDoubleTap(MotionEvent event) {

			return false;
		}

		public boolean onDoubleTapEvent(MotionEvent event) {
			return false;
		}

		public boolean onSingleTapUp(MotionEvent event) {
			return false;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			onTouchDownView = null;
			if (thisView.selectMenuView.getVisibility() == View.VISIBLE) {
				thisView.selectMenuView.setVisibility(View.GONE);
			}
			return false;
		}
	}

	public void onResume() {
		thisView.businessCardPopView.dismissUserCardDialogView();
		thisView.dismissReleaseShareDialogView();
	}

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public void modifyPraiseusersToMessage(boolean option, String gid, String gsid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("gsid", gsid);
		params.addBodyParameter("option", option + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_ADDPRAISE, params, responseHandlers.share_modifyPraiseusersCallBack);
	}

	public void finish() {
		thisView.dismissGroupBoardsDialog();
		thisView.viewManage.shareSectionView = null;
		thisView.viewManage.postNotifyView("ShareSubViewMessage");
	}
}
