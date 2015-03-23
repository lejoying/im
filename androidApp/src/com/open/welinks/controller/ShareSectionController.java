package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.CreateBoardActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputCommentDialog;
import com.open.welinks.customView.Alert.AlertInputCommentDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Board;
import com.open.welinks.model.Data.Boards.Comment;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.ResponseHandlers.Share_scoreCallBack;
import com.open.welinks.model.TaskContainer_Share;
import com.open.welinks.model.TaskContainer_Share.GetShares;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.view.ShareSectionView;
import com.open.welinks.view.ShareSectionView.BoardDialogItem;
import com.open.welinks.view.ShareSectionView.SharesMessageBody;
import com.open.welinks.view.ViewManage;

public class ShareSectionController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ShareSectionController";
	public MyLog log = new MyLog(tag, true);
	public Gson gson = new Gson();

	public TaskContainer_Share mTaskContainer_Share = new TaskContainer_Share();
	// public TaskManager mTaskManager = TaskManager.getInstance();
	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();
	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	public HttpClient httpClient = HttpClient.getInstance();

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

	public void onCrate() {
		String key = thisActivity.getIntent().getStringExtra("key");
		if (key == null || "".equals(key)) {
			thisView.currentGroup = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		} else if (data.relationship.groupsMap.containsKey(key)) {
			thisView.currentGroup = data.relationship.groupsMap.get(key);
			getGroup(key);
		} else {
			getGroup(key);
		}
		mGesture = new GestureDetector(thisActivity, new GestureListener());
	}

	public class ReflashStatus {
		public int Normal = 1, Reflashing = 2, Failed = 3;
		public int state = Normal;
	}

	public ReflashStatus reflashStatus = new ReflashStatus();

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
					intent.putExtra("mode", "ShareSectionNotifyShares");
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "text");
					intent.putExtra("sid", thisView.currentBoard.sid);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseAlbumButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("mode", "ShareSectionNotifyShares");
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "album");
					intent.putExtra("sid", thisView.currentBoard.sid);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageViewButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("mode", "ShareSectionNotifyShares");
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
					if (data.relationship.groupCircles != null) {
						// for (String str : data.relationship.groupCircles) {
						// GroupCircle a = data.relationship.groupCirclesMap.get(str);
						// }
					} else {
					}

				} else if (view.equals(thisView.findMoreGroupButtonView)) {

				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					final String content = tagContent.substring(index + 1);
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
						// Praise praise = mTaskContainer_Share.new Praise();
						// praise.gsid = content;
						// praise.thisView = thisView;
						// praise.gid = thisView.currentGroup.gid + "";
						// praise.sid = thisView.currentBoard.sid;
						// praise.API = API.SHARE_ADDPRAISE;
						// taskManageHolder.taskManager.pushTask(praise);
					} else if ("DecrementView".equals(type)) {
						ShareMessage shareMessage = data.boards.shareMessagesMap.get(content);
						SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + shareMessage.gsid);
						String number = sharesMessageBody.sharePraiseNumberView.getText().toString();
						int num = Integer.valueOf(number);
						num--;
						if (shareMessage.scores == null) {
							shareMessage.scores = new HashMap<String, Data.Boards.Score>();
						}
						Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
						if (score == null) {
							score = data.boards.new Score();
						} else {
							if (score.negative > 0) {
								view.setAlpha(1f);
							} else {
								view.setAlpha(0.125f);
							}
							if (score.remainNumber == 0) {
								Toast.makeText(thisActivity, "对不起,你只能评分一次", Toast.LENGTH_SHORT).show();
								return;
							}
						}
						shareMessage.totalScore = shareMessage.totalScore - 1;
						score.phone = data.userInformation.currentUser.phone;
						score.time = new Date().getTime();
						score.negative = 1;
						score.remainNumber = 0;
						shareMessage.scores.put(score.phone, score);
						data.boards.isModified = true;
						sharesMessageBody.sharePraiseNumberView.setText(num + "");
						if (num < 10 && num >= 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-75 * thisView.displayMetrics.density);
						} else if (num < 100 && num >= 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-69 * thisView.displayMetrics.density);
						} else if (num < 1000 && num >= 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
							sharesMessageBody.sharePraiseNumberView.setText("999");
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-62 * thisView.displayMetrics.density);
						} else if (num < 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#00a800"));
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-71 * thisView.displayMetrics.density);
						}
						if (score.negative > 0) {
							view.setAlpha(1f);
						} else {
							view.setAlpha(0.125f);
						}
						view.setTag(R.id.time, null);
						modifyPraiseusersToMessage(false, shareMessage.gsid);
					} else if ("IncrementView".equals(type)) {
						ShareMessage shareMessage = data.boards.shareMessagesMap.get(content);
						SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + shareMessage.gsid);
						String number = sharesMessageBody.sharePraiseNumberView.getText().toString();
						int num = Integer.valueOf(number);
						num++;
						if (shareMessage.scores == null) {
							shareMessage.scores = new HashMap<String, Data.Boards.Score>();
						}
						Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
						if (score == null) {
							score = data.boards.new Score();
						} else {
							if (score.positive > 0) {
								view.setAlpha(1f);
							} else {
								view.setAlpha(0.125f);
							}
							if (score.remainNumber == 0) {
								Toast.makeText(thisActivity, "对不起,你只能评分一次", Toast.LENGTH_SHORT).show();
								return;
							}
						}
						shareMessage.totalScore = shareMessage.totalScore + 1;
						score.phone = data.userInformation.currentUser.phone;
						score.time = new Date().getTime();
						score.positive = 1;
						score.remainNumber = 0;
						shareMessage.scores.put(score.phone, score);
						data.boards.isModified = true;
						sharesMessageBody.sharePraiseNumberView.setText(num + "");
						if (num < 10 && num >= 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-75 * thisView.displayMetrics.density);
						} else if (num < 100 && num >= 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-69 * thisView.displayMetrics.density);
						} else if (num < 1000 && num >= 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
							sharesMessageBody.sharePraiseNumberView.setText("999");
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-62 * thisView.displayMetrics.density);
						} else if (num < 0) {
							sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#00a800"));
							sharesMessageBody.sharePraiseNumberView.setTranslationX(-71 * thisView.displayMetrics.density);
						}
						if (score.positive > 0) {
							view.setAlpha(1f);
						} else {
							view.setAlpha(0.125f);
						}
						modifyPraiseusersToMessage(true, shareMessage.gsid);
						// float alpha = sharesMessageBody.incrementView.getAlpha();
						view.setTag(R.id.time, null);
					} else if ("CommentControlView".equals(type)) {
						Alert.createInputCommentDialog(thisActivity).setOnConfirmClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(AlertInputCommentDialog dialog) {
								String commentContent = dialog.getInputText().trim();
								if ("".equals(commentContent)) {
								} else {
									// TODO
									ShareMessage shareMessage = data.boards.shareMessagesMap.get(content);
									if (shareMessage == null) {
										// log.e("-----------------null");
										return;
									}
									// SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + shareMessage.gsid);
									parser.check();
									User currentUser = data.userInformation.currentUser;
									Comment comment = data.boards.new Comment();
									comment.phone = currentUser.phone;
									comment.nickName = currentUser.nickName;
									comment.head = currentUser.head;
									comment.phoneTo = "";
									comment.nickNameTo = "";
									comment.headTo = "";
									comment.contentType = "text";
									comment.content = commentContent;
									comment.time = new Date().getTime();
									if (shareMessage.comments == null) {
										shareMessage.comments = new ArrayList<Comment>();
									}
									shareMessage.comments.add(comment);
									data.boards.isModified = true;
									thisView.showShareMessages();
									addCommentToMessage(thisView.currentGroup.gid + "", thisView.currentGroup.currentBoard, shareMessage.gsid, commentContent);
								}
							}
						}).show().requestFocus();
					} else if ("CommentHeadView".equals(type)) {
						ShareMessage shareMessage = data.boards.shareMessagesMap.get(content);
						SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + shareMessage.gsid);
						List<Comment> comments = shareMessage.comments;
						int i = (Integer) view.getTag(R.id.tag_first);
						sharesMessageBody.commentContentView.setText(comments.get(i).content);
						sharesMessageBody.commentsPointView.setX(18 * thisView.displayMetrics.density + (comments.size() - i - 1) * 45 * thisView.displayMetrics.density);
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
				isTouchDown = false;
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
					} else if (view_class.equals("DecrementView")) {
						onTouchDownView = view;
						isTouchDown = true;
						onTouchDownView.setAlpha(1f);
						// log.e("DecrementView");
						// float alpha = onTouchDownView.getAlpha();
					} else if (view_class.equals("IncrementView")) {
						onTouchDownView = view;
						isTouchDown = true;
						onTouchDownView.setAlpha(1f);
						// log.e("IncrementView");
					} else if (view_class.equals("CommentControlView")) {
						onTouchDownView = view;
						isTouchDown = true;
						onTouchDownView.setAlpha(1f);
					} else if (view_class.equals("CommentHeadView")) {
						onTouchDownView = view;
						isTouchDown = true;
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
				thisView.showShareMessages();
				thisView.showGroupBoards();
				thisView.dismissGroupBoardsDialog();
				getCurrentGroupShareMessages();
			} else if (view_class.equals("DecrementView")) {
				onTouchDownView.setTag(R.id.time, null);
				onTouchDownView.performClick();
			} else if (view_class.equals("IncrementView")) {
				onTouchDownView.setTag(R.id.time, null);
				onTouchDownView.performClick();
			} else if (view_class.equals("CommentControlView")) {
				onTouchDownView.setAlpha(0.5f);
				onTouchDownView.performClick();
			} else if (view_class.equals("CommentHeadView")) {
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
			if (onTouchDownView != null) {
				String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
				if (view_class.equals("DecrementView")) {
					String tagContent = (String) onTouchDownView.getTag();
					int index = tagContent.lastIndexOf("#");
					String content = tagContent.substring(index + 1);
					ShareMessage shareMessage = data.boards.shareMessagesMap.get(content);
					Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
					if (score != null) {
						if (score.negative > 0) {
							onTouchDownView.setAlpha(1f);
						} else {
							onTouchDownView.setAlpha(0.125f);
						}
					} else {
						onTouchDownView.setAlpha(0.125f);
					}
				} else if (view_class.equals("IncrementView")) {
					String tagContent = (String) onTouchDownView.getTag();
					int index = tagContent.lastIndexOf("#");
					String content = tagContent.substring(index + 1);
					ShareMessage shareMessage = data.boards.shareMessagesMap.get(content);
					Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
					if (score != null) {
						if (score.positive > 0) {
							onTouchDownView.setAlpha(1f);
						} else {
							onTouchDownView.setAlpha(0.125f);
						}
					} else {
						onTouchDownView.setAlpha(0.125f);
					}
				} else if (view_class.equals("CommentControlView")) {
					onTouchDownView.setAlpha(0.5f);
				}
			}
			onTouchDownView = null;
			// isTouchDown = false;
			if (thisView.selectMenuView.getVisibility() == View.VISIBLE) {
				thisView.selectMenuView.setVisibility(View.GONE);
			}
			return false;
		}
	}

	public void onResume() {
		if (thisView.currentGroup != null) {
			thisView.businessCardPopView.dismissUserCardDialogView();
			thisView.dismissReleaseShareDialogView();
			thisView.showShareMessages();
			thisView.showGroupBoards();
		}
	}

	public void finish() {
		thisView.dismissGroupBoardsDialog();
		taskManageHolder.viewManage.shareSectionView = null;
		if (thisView.currentGroup != null && thisView.currentBoard != null)
			thisView.currentGroup.currentBoard = thisView.currentBoard.sid;
		if (taskManageHolder.viewManage.shareSubView != null) {
			taskManageHolder.viewManage.shareSubView.thisController.nowpage = 0;
			taskManageHolder.viewManage.shareSubView.getCurrentGroupShareMessages();
		}
	}

	public void addCommentToMessage(String gid, String sid, String gsid, String content) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("sid", sid);
		params.addBodyParameter("gsid", gsid);
		params.addBodyParameter("nickName", currentUser.nickName);
		params.addBodyParameter("head", currentUser.head);
		params.addBodyParameter("phoneTo", "");
		params.addBodyParameter("nickNameTo", "");
		params.addBodyParameter("headTo", "");
		params.addBodyParameter("contentType", "text");
		params.addBodyParameter("content", content);
		httpUtils.send(HttpMethod.POST, API.SHARE_ADDCOMMENT, params, responseHandlers.share_addCommentCallBack);
	}

	public void modifyPraiseusersToMessage(boolean option, String gsid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gsid", gsid);
		params.addBodyParameter("option", option + "");
		Share_scoreCallBack callBack = responseHandlers.new Share_scoreCallBack();
		callBack.option = option;
		httpUtils.send(HttpMethod.POST, API.SHARE_SCORE, params, callBack);
	}

	public void getCurrentGroupShareMessages() {
		if (thisView.currentGroup == null) {
			log.e(ViewManage.getErrorLineNumber() + "thisView.currentGroup == null");
			return;
		}
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", thisView.currentGroup.gid + "");
		params.addBodyParameter("sid", thisView.currentBoard.sid);
		params.addBodyParameter("nowpage", nowpage + "");
		params.addBodyParameter("pagesize", pagesize + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack2);
	}

	public void modifyBoardsSequence(String sequenceListString) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", thisView.currentGroup.gid + "");
		params.addBodyParameter("boardsequence", sequenceListString);
		params.addBodyParameter("targetphones", gson.toJson(thisView.currentGroup.members));

		httpUtils.send(HttpMethod.POST, API.SHARE_MODIFYSQUENCE, params, responseHandlers.modifyBoardSequenceCallBack);
	}

	public void getGroup(String gid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("type", "group");
		httpUtils.send(HttpMethod.POST, API.GROUP_GET, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
				public Group group;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("获取群组信息成功".equals(response.提示信息)) {
					log.e(ViewManage.getErrorLineNumber() + "-------------------获取群组信息成功");
					Group group = response.group;
					if (group != null) {
						Group currentGroup = null;
						if (data.relationship.groups.contains(group.gid + "")) {
							currentGroup = data.relationship.groupsMap.get(group.gid + "");
						} else {
							currentGroup = data.relationship.new Group();
							data.relationship.groupsMap.put(group.gid + "", currentGroup);
						}
						currentGroup.gid = response.group.gid;
						currentGroup.icon = group.icon;
						currentGroup.name = group.name;
						currentGroup.longitude = group.longitude;
						currentGroup.latitude = group.latitude;
						currentGroup.description = group.description;
						currentGroup.createTime = group.createTime;
						currentGroup.background = group.background;
						currentGroup.boards = group.boards;
						currentGroup.labels = group.labels;
						data.relationship.isModified = true;
						thisView.currentGroup = currentGroup;
						getBoards();
					}
				} else {
					log.e(ViewManage.getErrorLineNumber() + response.失败原因);
				}
			}
		});
	}

	private void getBoards() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", thisView.currentGroup.gid + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_GETGROUPBOARDS, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
				public String gid;
				public List<String> boards;
				public Map<String, Board> boardsMap;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取版块成功")) {
					log.e(ViewManage.getErrorLineNumber() + "-------------------获取版块成功");
					data = parser.check();
					Group group = data.relationship.groupsMap.get(response.gid);
					for (Board board : response.boardsMap.values()) {
						data.boards.boardsMap.put(board.sid, board);
					}
					data.boards.isModified = true;
					if (group != null) {
						group.boards = response.boards;
						if ((group.currentBoard == null || group.currentBoard.equals("")) && group.boards.size() > 0)
							group.currentBoard = group.boards.get(0);
					}
					thisView.currentBoard = data.boards.boardsMap.get(group.currentBoard);
					thisView.showShareMessages();
					thisView.showGroupBoards();
					getCurrentGroupShareMessages();
				} else {
					log.d(ViewManage.getErrorLineNumber() + response.失败原因);

				}
			};
		});
	};

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUESTCODE_CREATE && resultCode == Activity.RESULT_OK) {
			thisView.currentBoard = data.boards.boardsMap.get(thisView.currentGroup.boards.get(thisView.currentGroup.boards.size() - 1));
			nowpage = 0;
			thisView.showShareMessages();
			getCurrentGroupShareMessages();
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
