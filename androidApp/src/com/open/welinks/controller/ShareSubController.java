package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.open.welinks.NewChatActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandlers;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.ShareSubView;
import com.open.welinks.view.ShareSubView.GroupDialogItem;
import com.open.welinks.view.ShareSubView.SharesMessageBody;

public class ShareSubController {

	public Data data = Data.getInstance();
	public String tag = "ShareSubController";
	public MyLog log = new MyLog(tag, true);
	public Parser parser = Parser.getInstance();

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public ShareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

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

	public ShareSubController(MainController mainController) {
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
							options = thisView.viewManage.options40;
						} else if ("conver".equals(tag)) {
							flag = false;
						}
					} catch (Exception e) {
					}
				}
				if (flag) {
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
							if (instance.view.getTag() != null) {
								// fileHandlers.bitmaps.put(imageUri, loadedImage);
							}
						}
					});
				} else {
					thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view);
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
					} else if (view_class.equals("share_praise")) {
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
				} else if (view.equals(thisView.groupMembersListContentView)) {
					Intent intent = new Intent(thisActivity, NewChatActivity.class);
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
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "text");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					mainController.thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseAlbumButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "album");
					intent.putExtra("sid", thisView.currentGroup.currentBoard);
					intent.putExtra("gid", data.localStatus.localData.currentSelectedGroup);
					mainController.thisActivity.startActivity(intent);
					// thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageViewButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
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
					String content = tagContent.substring(index + 1);
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
							if (group.boards == null || group.boards.size() == 0) {
								DataHandlers.getGroupBoards(group.gid + "");
							}
							// display local data
							nowpage = 0;
							thisView.showShareMessages();
							getCurrentGroupShareMessages();
							thisView.showTopMenuRoomName();
							thisView.shareMessageListBody.y = 0;
							thisView.shareMessageListBody.setChildrenPosition();
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
					} else if ("SharePraise".equals(type)) {
						parser.check();
						User currentUser = data.userInformation.currentUser;
						ShareMessage shareMessage = data.boards.shareMessagesMap.get(content);
						boolean option = false;
						if (!shareMessage.praiseusers.contains(currentUser.phone)) {
							option = true;
							boolean flag = false;
							for (int i = 0; i < shareMessage.praiseusers.size(); i++) {
								if (shareMessage.praiseusers.get(i).equals(currentUser.phone)) {
									flag = true;
									break;
								}
							}
							if (!flag) {
								shareMessage.praiseusers.add(currentUser.phone);
							}
							SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + shareMessage.gsid);
							sharesMessageBody.sharePraiseIconView.setImageResource(R.drawable.praised_icon);
							sharesMessageBody.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
						} else {
							ArrayList<String> list = new ArrayList<String>();
							for (int i = 0; i < shareMessage.praiseusers.size(); i++) {
								if (shareMessage.praiseusers.get(i).equals(currentUser.phone)) {
									list.add(shareMessage.praiseusers.get(i));
								}
							}
							shareMessage.praiseusers.removeAll(list);
							SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + shareMessage.gsid);
							sharesMessageBody.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
							sharesMessageBody.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
						}
						modifyPraiseusersToMessage(option, data.localStatus.localData.currentSelectedGroup, shareMessage.gsid);
						view.setTag(R.id.time, null);
					}
				}
				onTouchDownView = null;
				isTouchDown = false;
			}
		};
		bodyCallback = new BodyCallback() {
			@Override
			public void onStopOrdering(List<String> listItemsSequence) {
				super.onStopOrdering(listItemsSequence);
				log.e(tag, listItemsSequence.toString());
				List<String> groups = new ArrayList<String>();
				for (int i = 0; i < listItemsSequence.size(); i++) {
					String key = listItemsSequence.get(i);
					groups.add(key.substring(key.indexOf("#") + 1, key.indexOf("_")));
				}
				// modify local data
				String oldSequece = gson.toJson(data.relationship.groups);
				data.relationship.groups = groups;
				data.relationship.isModified = true;

				String sequenceListString = gson.toJson(groups);

				// modify server data
				if (!sequenceListString.equals(oldSequece)) {
					modifyGroupSequence(sequenceListString);
					log.e("群组顺序发生改动");
				} else {
					log.e("群组顺序没有改动");
				}
			}
		};
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
		params.addBodyParameter("sid", thisView.currentGroup.currentBoard);
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
			} else if (view_class.equals("share_praise")) {
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
							body.setContent(body.message, body.fileName, body.imageContent, body.textContent, body.totalHeight);
						}
					}
				}
			}
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
}
