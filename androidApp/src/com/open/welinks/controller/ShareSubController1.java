package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.viewbody.BodyCallback;
import com.open.welinks.ChatActivity;
import com.open.welinks.CreateGroupStartActivity;
import com.open.welinks.FindMoreActivity;
import com.open.welinks.GroupInfoActivity;
import com.open.welinks.GroupListActivity;
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
import com.open.welinks.model.Data.Boards.Comment;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.ResponseHandlers.Share_scoreCallBack;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.view.ShareSubView1;
import com.open.welinks.view.ShareSubView1.GroupDialogItem;
import com.open.welinks.view.ShareSubView1.SharesMessageBody;
import com.open.welinks.view.ViewManage;

public class ShareSubController1 {

	public Data data = Data.getInstance();
	public String tag = "ShareSubController";
	public MyLog log = new MyLog(tag, true);
	public Parser parser = Parser.getInstance();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public ShareSubView1 thisView;
	public Context context;
	public Activity thisActivity;

	public MainController1 mainController;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	// public OnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public OnDownloadListener downloadListener;
	public BodyCallback bodyCallback;

	public MyOnClickListener mOnClickListener;

	public View onTouchDownView;
	public View onLongPressView;;

	public boolean isTouchDown = false;

	public Group onTouchDownGroup;

	public int SCAN_MESSAGEDETAIL = 0x01;
	public String currentScanMessageKey;

	public int nowpage = 0;
	public int pagesize = 10;

	public Gson gson = new Gson();

	public BodyCallback shareBodyCallback;

	public ShareSubController1(MainController1 mainController) {
		thisActivity = mainController.thisActivity;

		this.mainController = mainController;

	}

	public OnTouchListener onTouchListener2;

	public class ReflashStatus {
		public int Normal = 1, Reflashing = 2, Failed = 3;
		public int state = Normal;
	}

	public ReflashStatus reflashStatus = new ReflashStatus();

	public void initializeListeners() {
		onTouchListener2 = new OnTouchListener() {
			GestureDetector mGesture = new GestureDetector(thisActivity, new GestureListener());

			class GestureListener extends SimpleOnGestureListener {

				@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					thisView.showGroupsDialog();
					log.e("onSingleTapConfirmed");
					return super.onSingleTapConfirmed(e);
				}

				public boolean onSingleTapUp(MotionEvent event) {
					// log.e("onSingleTapUp");
					return false;
				}

				public boolean onDoubleTap(MotionEvent event) {
					thisView.shareMessageListBody.y = 0;
					thisView.shareMessageListBody.setChildrenPosition();
					log.e("onDoubleTap");
					return false;
				}

				public boolean onDoubleTapEvent(MotionEvent event) {
					// log.e("onDoubleTapEvent");
					return false;
				}
			}

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGesture.onTouchEvent(event);
				return true;
			}
		};

		shareBodyCallback = new BodyCallback() {
			@Override
			public void onRefresh(int direction) {
				super.onRefresh(direction);
				if (reflashStatus.state != reflashStatus.Reflashing) {
					reflashStatus.state = reflashStatus.Reflashing;
					if (direction == 1) {
						nowpage = 0;
						getCurrentGroupShareMessages();
					} else if (direction == -1) {
						nowpage++;
						getCurrentGroupShareMessages();
					}
					thisView.showRoomTime();
				}
			}
		};

		downloadListener = new OnDownloadListener() {

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onSuccess(final DownloadFile instance, int status) {
				DisplayImageOptions options = thisView.options;
				boolean flag = true;
				if (instance.view.getTag() != null) {
					try {
						String tag = (String) instance.view.getTag();
						if ("head".equals(tag)) {
							options = taskManageHolder.viewManage.options40;
						} else if ("conver".equals(tag)) {
							flag = false;
						}
					} catch (Exception e) {
					}
				}
				if (flag) {
					taskManageHolder.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, options, new SimpleImageLoadingListener() {
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
							if (instance.view.getTag() != null) {
								// fileHandlers.bitmaps.put(imageUri, loadedImage);
							}
						}
					});
				} else {
					taskManageHolder.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view);
				}
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				if (instance.view.getTag() != null) {
					if ("image".equals(instance.view.getTag().toString())) {
						Log.e(tag, "---------------failure:" + instance.view.getTag().toString());
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
						// log.e("---------------ondow view_class");
						// group dialog item onTouch
						onTouchDownView = view;
						isTouchDown = true;
						Object viewTag = view.getTag(R.id.tag_first);
						if (Group.class.isInstance(viewTag) == true) {
							Group group = (Group) viewTag;
							Log.d(tag, "onTouch: gid:" + group.gid + "name" + group.name);

							onTouchDownGroup = group;
						} else {
							thisView.dismissGroupDialog();
							Log.d(tag, "onTouch: " + (String) viewTag);
						}
					} else if (view_class.equals("group_setting")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("group_members")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("share_release")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("group_head")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("share_head")) {
						onTouchDownView = view;
						isTouchDown = true;
					} else if (view_class.equals("title_share")) {
						long currentTime = System.currentTimeMillis();
						if (Long.class.isInstance(view.getTag(R.id.tag_first)) == true) {
							long time = (Long) view.getTag(R.id.tag_first);
							if (currentTime - time < 300) {
								thisView.shareMessageListBody.y = 0;
								thisView.shareMessageListBody.setChildrenPosition();
								Toast.makeText(thisActivity, "double", Toast.LENGTH_SHORT).show();
								view.setTag(R.id.tag_first, 0);
							} else {
								view.setTag(R.id.tag_first, currentTime);
							}
						} else {
							view.setTag(R.id.tag_first, currentTime);
						}
						onTouchDownView = view;
					} else if (view_class.equals("TopView")) {
						onTouchDownView = view;
						isTouchDown = true;
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

					Log.i(tag, "ACTION_DOWN---" + view_class);
					// thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
				}
				if (action == MotionEvent.ACTION_MOVE) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("CommentHeadView")) {
						log.e("CommentHeadView--------------------Move");
					}
				}
				return false;
			}
		};

		mOnClickListener = new MyOnClickListener() {

			public void onClickEffective(View view) {
				if (view.equals(thisView.leftImageButton)) {
					Intent intent = new Intent(thisActivity, GroupInfoActivity.class);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.groupListButtonView)) {
					Intent intent = new Intent(thisActivity, GroupListActivity.class);
					intent.putExtra("type", "list_group");
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.createGroupButtonView)) {
					Intent intent = new Intent(thisActivity, CreateGroupStartActivity.class);
					thisActivity.startActivity(intent);
					// thisView.dismissGroupDialog();
				} else if (view.equals(thisView.pop_out_background1) || view.equals(thisView.pop_out_background2)) {
					thisView.dismissGroupDialog();
				} else if (view.equals(thisView.findMoreGroupButtonView)) {
					Intent intent = new Intent(thisActivity, FindMoreActivity.class);
					intent.putExtra("type", 2);
					thisActivity.startActivity(intent);
					// thisView.dismissGroupDialog();
				} else if (view.equals(thisView.shareTopMenuGroupNameParent)) {
					thisView.showGroupsDialog();
				} else if (view.equals(thisView.groupDialogView)) {
					thisView.groupDialogView.isIntercept = false;
					thisView.dismissGroupDialog();
				} else if (view.equals(thisView.groupHeadView) || view.equals(thisView.groupCoverView)) {
					if (data.localStatus.localData != null && data.localStatus.localData.currentSelectedGroup != null && !data.localStatus.localData.currentSelectedGroup.equals("")) {
						parser.check();
						log.e("gid:" + data.localStatus.localData.currentSelectedGroup);
						// Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_GROUP, data.localStatus.localData.currentSelectedGroup);
						thisView.businessCardPopView.showUserCardDialogView();
					}
				} else if (view.equals(thisView.releaseShareView)) {
					Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
					long[] pattern = { 30, 100, 30 };
					vibrator.vibrate(pattern, -1);

					thisView.showReleaseShareDialogView();
					// thisView.showNewStyle();
				} else if (view.equals(thisView.groupMembersListContentView)) {
					Intent intent = new Intent(thisActivity, ChatActivity.class);
					intent.putExtra("type", "group");
					intent.putExtra("id", data.localStatus.localData.currentSelectedGroup);
					thisActivity.startActivityForResult(intent, R.id.tag_second);
				} else if (view.equals(thisView.releaseShareDialogView)) {
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.groupManageView)) {

					if (thisView.groupsManageButtons.getVisibility() == View.VISIBLE) {
						thisView.groupsManageButtons.setVisibility(View.GONE);
					} else {
						thisView.groupsManageButtons.setVisibility(View.VISIBLE);
					}
					// Intent intent = new Intent(thisActivity,
					// GroupListActivity.class);
					// thisActivity.startActivity(intent);
					// thisView.dismissGroupDialog();
				} else if (view.equals(thisView.releaseTextButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("mode", "ShareSubViewMessage");
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "text");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					mainController.thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseAlbumButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("mode", "ShareSubViewMessage");
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "album");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					mainController.thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageViewButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("mode", "ShareSubViewMessage");
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "imagetext");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					mainController.thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					final String content = tagContent.substring(index + 1);
					// log.e(tagContent);
					if ("GroupDialogContentItem".equals(type)) {
						parser.check();
						// modify data
						thisView.dismissGroupDialog();
						if (!data.localStatus.localData.currentSelectedGroup.equals(content)) {
							data.localStatus.localData.currentSelectedGroup = content;
							// modify UI
							Group group = data.relationship.groupsMap.get(content);
							TextView shareTopMenuGroupName = (TextView) view.getTag(R.id.shareTopMenuGroupName);
							data.localStatus.localData.currentSelectedGroup = group.gid + "";
							String name = group.name;
							if (name.length() > 8) {
								name = name.substring(0, 8);
							}
							shareTopMenuGroupName.setText(name);
							thisView.modifyCurrentShowGroup();

							DataHandler.getGroupBoards(group.gid + "");

							// display local data
							nowpage = 0;
							thisView.showShareMessages();
							getCurrentGroupShareMessages();
							thisView.showTopMenuRoomName();
							thisView.shareMessageListBody.y = 0;
							thisView.shareMessageListBody.setChildrenPosition();
							taskManageHolder.viewManage.squareSubView.setConver();
						}
					} else if ("ShareMessageDetail".equals(type)) {
						Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
						intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
						intent.putExtra("sid", thisView.currentGroup.currentBoard);
						intent.putExtra("gsid", content);
						currentScanMessageKey = content;
						thisActivity.startActivityForResult(intent, SCAN_MESSAGEDETAIL);
						// thisActivity.overridePendingTransition(R.anim.zoomin,
						// R.anim.zoomout);
					} else if ("ShareMessage".equals(type)) {
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, content);
						thisView.businessCardPopView.showUserCardDialogView();
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
								dialog.requestFocus();
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
				}
				onTouchDownView = null;
				isTouchDown = false;
			}
		};
		bodyCallback = new BodyCallback() {
			@Override
			public void onStopOrdering(List<String> listItemsSequence) {
				// TODO
				super.onStopOrdering(listItemsSequence);
				log.e(tag, listItemsSequence.toString());
				List<String> groups = new ArrayList<String>();
				for (int i = 0; i < listItemsSequence.size(); i++) {
					String key = listItemsSequence.get(i);
					groups.add(key.substring(key.indexOf("#") + 1, key.indexOf("_")));
				}
				// modify local data
				String rid = String.valueOf(thisView.currentGroupCircle.rid);
				String oldSequece = gson.toJson(data.relationship.groupCirclesMap.get(rid).groups);
				data.relationship.groupCirclesMap.get(rid).groups = groups;
				data.relationship.isModified = true;

				String sequenceListString = gson.toJson(groups);

				// modify server data
				if (!sequenceListString.equals(oldSequece)) {
					modifyGroupSequence(sequenceListString, rid);
					log.e("群组顺序发生改动");
				} else {
					log.e("群组顺序没有改动");
				}
			}
		};
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

	public void bindEvent() {
		thisView.shareTitleView.setOnTouchListener(mOnTouchListener);
		thisView.shareMessageListBody.bodyCallback = this.shareBodyCallback;
		thisView.groupListBody.bodyCallback = this.bodyCallback;
		thisView.leftImageButton.setOnClickListener(mOnClickListener);
		// thisView.shareTopMenuGroupNameParent.setOnTouchListener(onTouchListener2);
		thisView.shareTopMenuGroupNameParent.setOnClickListener(mOnClickListener);
		thisView.groupDialogView.setOnClickListener(mOnClickListener);
		thisView.groupDialogView.setOnTouchListener(mOnTouchListener);
		thisView.groupManageView.setOnClickListener(mOnClickListener);
		thisView.groupManageView.setOnTouchListener(mOnTouchListener);

		thisView.groupListButtonView.setOnClickListener(mOnClickListener);
		thisView.createGroupButtonView.setOnClickListener(mOnClickListener);
		thisView.findMoreGroupButtonView.setOnClickListener(mOnClickListener);

		thisView.pop_out_background1.setOnClickListener(mOnClickListener);
		thisView.pop_out_background2.setOnClickListener(mOnClickListener);
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

	public void modifyGroupSequence(String sequenceListString, String rid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("sequence", sequenceListString);
		params.addBodyParameter("rid", rid);

		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFYGROUPCIRCLESEQUENCE, params, responseHandlers.modifyGroupSequenceCallBack);
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
		params.addBodyParameter("gid", data.localStatus.localData.currentSelectedGroup);
		params.addBodyParameter("sid", thisView.currentGroup.currentBoard);
		params.addBodyParameter("nowpage", nowpage + "");
		params.addBodyParameter("pagesize", pagesize + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack);
	}

	//
	// public void getUserCurrentAllGroup() {
	// RequestParams params = new RequestParams();
	// HttpUtils httpUtils = new HttpUtils();
	// User currentUser = data.userInformation.currentUser;
	// params.addBodyParameter("phone", currentUser.phone);
	// params.addBodyParameter("accessKey", currentUser.accessKey);
	//
	// httpUtils.send(HttpMethod.POST, API.GROUP_GETGROUPMEMBERS, params, responseHandlers.getGroupMembersCallBack);
	// }

	public void onScroll() {
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
			log.e(view_class + "---view_class");
			if (view_class.equals("share_view")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_view")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_setting")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_members")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("share_release")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_head")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("share_head")) {
				onTouchDownView.performClick();
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

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == SCAN_MESSAGEDETAIL) {
			if (thisView.shareMessageListBody != null) {
				if (thisView.shareMessageListBody.listItemsSequence.size() > 0) {
					if (currentScanMessageKey != null) {
						SharesMessageBody body = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + currentScanMessageKey);
						if (body != null) {
							log.e(body.textContent);
							body.setContent(body.message, body.fileName, body.imageContent, body.textContent, body.totalHeight, body.imageHeight, body.lineCount);
						}
					}
				}
			}
		} else if (requestCode == 510 && resultCode == Activity.RESULT_OK) {
			// TODO
			nowpage = 0;
			thisView.currentGroup.currentBoard = thisView.currentGroup.boards.get(0);
			thisView.showShareMessages();
			thisView.getCurrentGroupShareMessages();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mainController.thisView.activityStatus.state == mainController.thisView.activityStatus.SHARE) {
			if (thisView.isShowGroupDialog) {
				thisView.dismissGroupDialog();
				thisView.isShowGroupDialog = false;
				return false;
			}
		}
		return true;
	}

	public void onDestroy() {
	}

	public void onPause() {
		if (thisView.currentGroupCircle != null) {
			data.localStatus.localData.currentGroupCircle = String.valueOf(thisView.currentGroupCircle.rid);
		}
		if (thisView.currentGroup != null) {
			data.localStatus.localData.currentSelectedGroup = String.valueOf(thisView.currentGroup.gid);
			data.localStatus.localData.currentSelectedGroupBoard = thisView.currentGroup.currentBoard;
			data.localStatus.localData.isModified = true;
		}
	}
}
