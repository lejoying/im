package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.CreateBoardActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.GroupCircle;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Boards.Board;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskContainer_Share;
import com.open.welinks.model.TaskContainer_Share.GetShares;
import com.open.welinks.model.TaskContainer_Share.Praise;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.view.ShareSectionView;
import com.open.welinks.view.ViewManage;
import com.open.welinks.view.ShareSectionView.BoardDialogItem;

public class ShareSectionController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ShareSectionController";
	public MyLog log = new MyLog(tag, false);
	public Gson gson = new Gson();

	public TaskContainer_Share mTaskContainer_Share = new TaskContainer_Share();
	// public TaskManager mTaskManager = TaskManager.getInstance();
	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();
	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public Context context;
	public ShareSectionView thisView;
	public ShareSectionController thisController;
	public Activity thisActivity;

	public OnDownloadListener downloadListener;
	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public GestureDetector mGesture;

	public View onTouchDownView, onLongPressView;

	public int SCAN_MESSAGEDETAIL = 0x01;
	public int REQUESTCODE_CREATE = 0x02;
	public String currentScanMessageKey;

	public BodyCallback shareBodyCallback, boardBodyCallback;
	public Board onTouchDownBoard;

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
						taskManageHolder.taskManager.pushTask(task);
					} else if (direction == -1) {
						nowpage++;
						task.nowpage = nowpage;
						if (taskManageHolder == null) {
							log.e("taskManageHolder");
						}
						if (taskManageHolder.taskManager == null) {
							log.e("taskManageHolder.taskManager");
						}
						taskManageHolder.taskManager.pushTask(task);
					}
					thisView.showRoomTime();
				}
			}

		};
		boardBodyCallback = new BodyCallback() {
			@Override
			public void onStopOrdering(List<String> listItemsSequence) {
				super.onStopOrdering(listItemsSequence);
				List<String> boards = new ArrayList<String>();
				for (int i = 0; i < listItemsSequence.size(); i++) {
					String key = listItemsSequence.get(i);
					boards.add(key.substring(key.indexOf("#") + 1, key.indexOf("_")));
				}
				String sequenceListString = gson.toJson(boards);
				String oldSequece = gson.toJson(thisView.currentGroup.boards);

				thisView.currentGroup.boards = boards;
				data.relationship.isModified = true;

				if (!sequenceListString.equals(oldSequece)) {
					modifyBoardsSequence(sequenceListString);
					log.e("版块顺序发生改动");
				} else {
					log.e(oldSequece + "版块顺序没有改动" + sequenceListString);
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
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_BOARD, thisView.currentBoard.sid);
						thisView.businessCardPopView.cardView.setHot(false);
						thisView.businessCardPopView.showUserCardDialogView();
					}
				} else if (view.equals(thisView.releaseShareDialogView)) {
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseTextButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "text");
					intent.putExtra("sid", thisView.currentBoard.sid);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseAlbumButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "album");
					intent.putExtra("sid", thisView.currentBoard.sid);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageViewButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "imagetext");
					intent.putExtra("sid", thisView.currentBoard.sid);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.groupListButtonView)) {
					thisView.dismissGroupBoardsDialog();
					thisActivity.startActivityForResult(new Intent(thisActivity, CreateBoardActivity.class), REQUESTCODE_CREATE);
				} else if (view.equals(thisView.createGroupButtonView)) {
					// if (data.relationship.groupCircles != null) {
					// for (String str : data.relationship.groupCircles) {
					// GroupCircle a = data.relationship.groupCirclesMap.get(str);
					// log.e(a.name + ":::::::::" + a.rid + ":::::::::::" + gson.toJson(a.groups));
					// }
					// } else {
					// log.e("null:::::::::::::::::::::::::::");
					// }

				} else if (view.equals(thisView.findMoreGroupButtonView)) {

				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("ShareMessageDetail".equals(type)) {
						Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
						intent.putExtra("gid", thisView.currentGroup.gid + "");
						intent.putExtra("sid", thisView.currentBoard.sid);
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
						praise.gid = thisView.currentGroup.gid + "";
						praise.sid = thisView.currentBoard.sid;
						praise.API = API.SHARE_ADDPRAISE;
						taskManageHolder.taskManager.pushTask(praise);

					}
				} else if (view.getTag(R.id.tag_class) != null) {
					String tag = (String) view.getTag(R.id.tag_class);
					if (tag.equals("group_setting")) {
						if (thisView.groupsManageButtons.getVisibility() == View.VISIBLE) {
							thisView.groupsManageButtons.setVisibility(View.GONE);
						} else {
							thisView.groupsManageButtons.setVisibility(View.VISIBLE);
						}
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
					} else if (view_class.equals("board_view")) {
						// log.e("---------------ondow view_class");
						// group dialog item onTouch
						onTouchDownView = view;
						isTouchDown = true;
						Object viewTag = view.getTag(R.id.tag_first);
						if (Board.class.isInstance(viewTag) == true) {
							Board board = (Board) viewTag;
							onTouchDownBoard = board;
						} else {
							thisView.dismissGroupBoardsDialog();
							Log.d(tag, "onTouch: " + (String) viewTag);
						}
					}
					if (view.equals(thisView.groupDialogView)) {
						Log.i(tag, "ACTION_DOWN---groupDialogView");
						thisView.groupDialogView.isIntercept = true;
						// onTouchDownView = view;
						isTouchDown = true;
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

		thisView.groupListBody.bodyCallback = this.boardBodyCallback;

		thisView.groupListButtonView.setOnClickListener(thisController.mOnClickListener);
		thisView.createGroupButtonView.setOnClickListener(thisController.mOnClickListener);
		thisView.findMoreGroupButtonView.setOnClickListener(thisController.mOnClickListener);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			thisView.shareMessageListBody.onTouchDown(event);
			thisView.groupListBody.onTouchDown(event);
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.shareMessageListBody.onTouchMove(event);
			thisView.groupListBody.onTouchMove(event);
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			onSingleTapUp(event);
			thisView.shareMessageListBody.onTouchUp(event);
			thisView.groupListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	private void onSingleTapUp(MotionEvent event) {
		if (onLongPressView != null) {
			if (onTouchDownBoard != null) {
				Board board = data.boards.boardsMap.get(onTouchDownBoard.sid);
				BoardDialogItem boardDialogItem = (BoardDialogItem) thisView.groupListBody.listItemBodiesMap.get("board#" + board.sid + "_" + board.name);
				boardDialogItem.gripCardBackground.setVisibility(View.INVISIBLE);

				onLongPressView = null;
				onTouchDownBoard = null;
				thisView.groupListBody.stopOrdering();
			}
		}

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
			} else if (view_class.equals("board_view")) {
				onTouchDownView.playSoundEffect(SoundEffectConstants.CLICK);
				thisView.currentBoard = (Board) onTouchDownView.getTag(R.id.tag_first);
				nowpage = 0;
				getCurrentGroupShareMessages();
				thisView.showShareMessages();
				thisView.showGroupBoards();
				thisView.dismissGroupBoardsDialog();
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
			if (onTouchDownView != null && onTouchDownBoard != null) {
				String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
				if (view_class.equals("board_view")) {

					Board board = data.boards.boardsMap.get(onTouchDownBoard.sid);
					BoardDialogItem boardDialogItem = (BoardDialogItem) thisView.groupListBody.listItemBodiesMap.get("board#" + board.sid + "_" + board.name);

					boardDialogItem.gripCardBackground.setVisibility(View.VISIBLE);

					Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
					long[] pattern = { 100, 100, 300 };
					vibrator.vibrate(pattern, -1);

					thisView.groupListBody.startOrdering("board#" + board.sid + "_" + board.name);

					onLongPressView = onTouchDownView;
					onTouchDownView = null;
				}
			}
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
		thisView.showShareMessages();
		thisView.showGroupBoards();
	}

	public void finish() {
		thisView.dismissGroupBoardsDialog();
		thisView.viewManage.shareSectionView = null;
		thisView.currentGroup.currentBoard = thisView.currentGroup.boards.get(0);
		thisView.viewManage.shareSubView.thisController.nowpage = 0;
		thisView.viewManage.shareSubView.getCurrentGroupShareMessages();
	}

	public void getCurrentGroupShareMessages() {
		if (thisView.currentGroup == null) {
			log.e(ViewManage.getErrorLineNumber() + "thisView.currentGroup == null");
			return;
		}
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", thisView.currentGroup.gid + "");
		params.addBodyParameter("sid", thisView.currentBoard.sid);
		params.addBodyParameter("nowpage", nowpage + "");
		params.addBodyParameter("pagesize", pagesize + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack2);
	}

	public void modifyBoardsSequence(String sequenceListString) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", thisView.currentGroup.gid + "");
		params.addBodyParameter("boardsequence", sequenceListString);
		params.addBodyParameter("targetphones", gson.toJson(thisView.currentGroup.members));

		httpUtils.send(HttpMethod.POST, API.SHARE_MODIFYSQUENCE, params, responseHandlers.modifyBoardSequenceCallBack);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUESTCODE_CREATE && resultCode == Activity.RESULT_OK) {
			thisView.currentBoard = data.boards.boardsMap.get(thisView.currentGroup.boards.get(thisView.currentGroup.boards.size() - 1));
			nowpage = 0;
			getCurrentGroupShareMessages();
			thisView.showShareMessages();
		}
	}

	public void onBackPressed() {
		if (thisView.isShowGroupDialog) {
			thisView.dismissGroupBoardsDialog();
		} else {
			thisActivity.finish();
		}

	}
}
