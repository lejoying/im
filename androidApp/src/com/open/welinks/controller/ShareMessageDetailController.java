package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.ImageScanActivity;
import com.open.welinks.R;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputCommentDialog;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.ShareView.onWeChatClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Board;
import com.open.welinks.model.Data.Comment;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Score;
import com.open.welinks.model.Data.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Data.UserInformation.User.Location;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.ResponseHandlers.Share_scoreCallBack2;
import com.open.welinks.model.SubData;
import com.open.welinks.model.SubData.MessageShareContent;
import com.open.welinks.model.SubData.SendShareMessage;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.view.ShareMessageDetailView;
import com.open.welinks.view.ShareMessageDetailView.ShareBody;
import com.open.welinks.view.ViewManage;

public class ShareMessageDetailController {

	public String tag = "ShareMessageDetailController";
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public MyLog log = new MyLog(tag, true);

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public SubData subData = SubData.getInstance();

	public Gson gson = new Gson();

	public Context context;
	public ShareMessageDetailView thisView;
	public ShareMessageDetailController thisController;
	public Activity thisActivity;

	public String gsid = "";
	public ShareMessage shareMessage;
	public Board board;

	public String textContent;
	public String imageContent;

	public OnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public OnDownloadListener downloadListener;
	public onWeChatClickListener mOnWeChatClickListener;

	public int IMAGEBROWSE_REQUESTCODE = 0x01;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public User currentUser;

	public String phoneTo = "";
	public String nickNameTo = "";
	public String headTo;

	public String sid;

	public Bitmap WeChatBitmap;

	public ShareMessageDetailController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		thisController = this;

		currentUser = data.userInformation.currentUser;
	}

	public boolean isRelation = false;

	public void initData() {
		parser.check();
		// TODO gid to sid

		sid = thisActivity.getIntent().getStringExtra("sid");

		if (sid == null || "".equals(sid)) {
			log.e(ViewManage.getErrorLineNumber() + "少传参数了");
			Toast.makeText(thisActivity, "参数缺失，分享不存在", Toast.LENGTH_SHORT).show();
			thisActivity.finish();
			return;
			// throw new IllegalArgumentException(ViewManage.getErrorLineNumber() + "少传参数了");
		}
		String gsid = thisActivity.getIntent().getStringExtra("gsid");
		if (gsid != null) {
			this.gsid = gsid;
			if (data.boards.boardsMap.containsKey(sid)) {
				board = data.boards.boardsMap.get(sid);
				shareMessage = data.boards.shareMessagesMap.get(gsid);
				getShareMessageDetail();
			} else if (data.tempData.tempShareMessageMap.containsKey(gsid)) {
				board = data.boards.new Board();
				shareMessage = data.tempData.tempShareMessageMap.get(gsid);
				getShareMessageDetail();
			} else {
				board = data.boards.new Board();
				// getShareFromServer(gid, gsid);
				shareMessage = data.tempData.tempShareMessageMap.get(gsid);
				getShareMessageDetail();
			}
		}
	}

	public void initShareListener() {
		mOnWeChatClickListener = thisView.shareView.new onWeChatClickListener() {
			@Override
			public void onWeChatClick() {
				if (thisView.imageView != null) {
					WeChatBitmap = thisView.imageView.getDrawingCache();
				}
				thisView.shareView.setWeChatContent(WeChatBitmap, textContent, shareMessage.phone, thisController.sid, thisController.gsid);
			}

			@Override
			public void onWeiboClick() {
				if (thisView.shareView != null) {
					this.sid = thisController.sid;
					this.gsid = thisController.gsid;
					this.phone = thisController.shareMessage.phone;
					if (thisView.body != null) {
						this.images = thisView.body.imageList;
						this.content = thisView.body.textContent;
					}
				} else {
					Toast.makeText(thisActivity, "不能分享空帖子.", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	public long totalLength = 0;

	public Map<String, Long> fileLengMap = new HashMap<String, Long>();
	public Map<String, Long> currentDownloadLengthMap = new HashMap<String, Long>();

	public void initializeListeners() {
		downloadListener = new OnDownloadListener() {

			@Override
			public void onLoadingStarted(DownloadFile instance, int precent, int status) {
				super.onLoadingStarted(instance, precent, status);
				totalLength += instance.bytesLength;
				fileLengMap.put(instance.url, instance.bytesLength);
			}

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				Long singleLength = fileLengMap.get(instance.url);
				currentDownloadLengthMap.put(instance.path, singleLength);

				double currentLength = 0;
				for (Entry<String, Long> entry : currentDownloadLengthMap.entrySet()) {
					String key = entry.getKey();
					Long value = entry.getValue();
					if (key != null && !"".equals(key)) {
						currentLength += value;
					}
				}
				final double currentPrecent = currentLength / totalLength;
				taskManageHolder.fileHandler.handler.post(new Runnable() {

					@Override
					public void run() {
						// thisView.controlProgress.moveTo((int) (currentPrecent * 100));
					}
				});

				final ImageView imageView = (ImageView) instance.view;
				taskManageHolder.imageLoader.displayImage("file://" + instance.path, imageView, thisView.displayImageOptions, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						int height = (int) (loadedImage.getHeight() * (thisView.screenWidth / loadedImage.getWidth()));
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) thisView.screenWidth, height);
						imageView.setLayoutParams(params);
					}
				});
			}

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
				double currentLength = 0;
				String path = instance.url;
				Long singleLength = fileLengMap.get(path);
				Long length = singleLength * precent / 100;
				currentDownloadLengthMap.put(path, length);

				for (Entry<String, Long> entry : currentDownloadLengthMap.entrySet()) {
					String key = entry.getKey();
					Long value = entry.getValue();
					if (key != null && !"".equals(key)) {
						currentLength += value;
					}
				}
				final double currentPrecent = currentLength / totalLength;
				taskManageHolder.fileHandler.handler.post(new Runnable() {

					@Override
					public void run() {
						// thisView.controlProgress.moveTo((int) (currentPrecent * 100));
					}
				});
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				// TODO Auto-generated method stub
			}
		};

		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {

				if (view.equals(thisView.sharePopupWindow)) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						thisView.sharePopupWindow.dismiss();
					}
				}
				return false;
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.menuImage)) {
					if (thisView.menuOptionsView.getVisibility() == View.GONE) {
						thisView.menuOptionsView.setVisibility(View.VISIBLE);
					} else {
						thisView.menuOptionsView.setVisibility(View.GONE);
					}
				} else if (view.equals(thisView.deleteOptionView)) {
					deleteGroupShare();
				} else if (view.equals(thisView.shareOptionView)) {
					thisView.menuOptionsView.setVisibility(View.GONE);
					thisView.sharePopupWindow.showAtLocation(thisView.maxView, Gravity.CENTER, 0, 0);
				} else if (view.equals(thisView.shareView)) {
					thisView.sharePopupWindow.dismiss();
				}
				String tag_class = (String) view.getTag(R.id.tag_class);
				if ("DecrementView".equals(tag_class)) {// -
					if (shareMessage.scores == null) {
						shareMessage.scores = new HashMap<String, Data.Score>();
					}
					User currentUser = data.userInformation.currentUser;
					Score score = shareMessage.scores.get(currentUser.phone);
					if (score == null) {
						score = data.new Score();
					} else {
						thisView.scoreState();
						if (score.remainNumber == 0) {
							Toast.makeText(thisActivity, "对不起,你只能评分一次", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					shareMessage.totalScore = shareMessage.totalScore - 1;
					score.phone = currentUser.phone;
					score.time = new Date().getTime();
					score.negative = 1;
					score.remainNumber = 0;
					shareMessage.scores.put(score.phone, score);
					data.boards.isModified = true;
					thisView.scoreState();
					modifyPraiseusersToMessage(false, shareMessage.gsid);
				} else if ("IncrementView".equals(tag_class)) {// +
					if (shareMessage.scores == null) {
						shareMessage.scores = new HashMap<String, Data.Score>();
					}
					Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
					if (score == null) {
						score = data.new Score();
					} else {
						thisView.scoreState();
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
					thisView.scoreState();
					modifyPraiseusersToMessage(true, shareMessage.gsid);
				} else if ("CommentControlView".equals(tag_class)) {
					Alert.createInputCommentDialog(thisActivity).setOnConfirmClickListener(new com.open.welinks.customView.Alert.AlertInputCommentDialog.OnDialogClickListener() {

						@Override
						public void onClick(AlertInputCommentDialog dialog) {
							// dialog.requestFocus();
							String commentContent = dialog.getInputText().trim();
							if ("".equals(commentContent)) {
							} else {
								if (shareMessage == null) {
									return;
								}
								parser.check();
								User currentUser = data.userInformation.currentUser;
								Comment comment = data.new Comment();
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
								thisView.showShareComments();
								addCommentToMessage(comment.contentType, commentContent);
							}
						}
					}).show().requestFocus();
				} else if ("TimeView".equals(tag_class)) {
					TextView textView = (TextView) view;
					long time = (Long) view.getTag(R.id.tag_first);
					int type = (Integer) view.getTag(R.id.tag_second);
					if (type == 1) {
						textView.setText(DateUtil.getNearNormalTime(time));
						view.setTag(R.id.tag_second, 2);
					} else if (type == 2) {
						textView.setText(DateUtil.getNearShareTime(time));
						view.setTag(R.id.tag_second, 1);
					}
				} else if ("HeadView".equals(tag_class)) {
					String phone = (String) view.getTag(R.id.tag_first);
					thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, phone);
					thisView.businessCardPopView.showUserCardDialogView();
				} else if ("ShareMessageDetailImage".equals(tag_class)) {
					ShareBody body = (ShareBody) view.getTag(R.id.tag_first);
					int position = (Integer) view.getTag(R.id.tag_second);
					data.tempData.selectedImageList = body.imageList;
					Intent intent = new Intent(thisActivity, ImageScanActivity.class);
					intent.putExtra("position", position + "");
					thisActivity.startActivity(intent);
				}
			}
		};
	}

	public void modifyPraiseusersToMessage(boolean option, String gsid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gsid", gsid);
		params.addBodyParameter("option", option + "");
		if (shareMessage.location != null && shareMessage.location.length >= 2) {
			params.addBodyParameter("location", "[" + shareMessage.location[0] + "," + shareMessage.location[1] + "]");
		}
		Share_scoreCallBack2 callBack = responseHandlers.new Share_scoreCallBack2();
		callBack.option = option;
		httpUtils.send(HttpMethod.POST, API.SHARE_SCORE, params, callBack);
	}

	public void bindEvent() {
		thisView.menuImage.setOnClickListener(mOnClickListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.deleteOptionView.setOnClickListener(mOnClickListener);
		thisView.shareOptionView.setOnClickListener(mOnClickListener);

		thisView.sharePopupWindow.setTouchInterceptor(mOnTouchListener);
		thisView.shareView.setOnClickListener(mOnClickListener);

		thisView.shareView.setOnWeChatClickListener(mOnWeChatClickListener);
	}

	public void deleteGroupShare() {
		if (shareMessage == null) {
			log.e("此分享==null   异常");
		} else {
			final User currentUser = data.userInformation.currentUser;
			if (currentUser.phone.equals(shareMessage.phone)) {
				Alert.createDialog(thisActivity).setTitle("是否删除这条分享？").setOnConfirmClickListener(new OnDialogClickListener() {

					@Override
					public void onClick(AlertInputDialog dialog) {
						RequestParams params = new RequestParams();
						HttpUtils httpUtils = new HttpUtils();
						params.addBodyParameter("phone", currentUser.phone);
						params.addBodyParameter("accessKey", currentUser.accessKey);
						params.addBodyParameter("sid", sid);
						params.addBodyParameter("gsid", gsid);
						if (shareMessage.location != null && shareMessage.location.length >= 2) {
							params.addBodyParameter("location", "[" + shareMessage.location[0] + "," + shareMessage.location[1] + "]");
						}
						data = parser.check();
						Board board = data.boards.boardsMap.get(sid);
						if (board != null && board.shareMessagesOrder != null) {
							board.shareMessagesOrder.remove(gsid);
						}
						// share.shareMessagesMap.remove(gsid);
						data.boards.isModified = true;
						taskManageHolder.viewManage.postNotifyView("ShareSubViewMessage");
						httpUtils.send(HttpMethod.POST, API.SHARE_DELETE, params, responseHandlers.share_delete);
						Intent intent = new Intent();
						intent.putExtra("key", gsid);
						thisActivity.setResult(Activity.RESULT_OK, intent);
						thisActivity.finish();
					}
				}).show();
			} else {
				Toast.makeText(thisActivity, "您不是管理员,不能删除他人帖子.", Toast.LENGTH_SHORT).show();
				if (thisView.menuOptionsView.getVisibility() == View.VISIBLE) {
					thisView.menuOptionsView.setVisibility(View.GONE);
				}
			}
		}
	}

	public void addCommentToMessage(String contentType, String content) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		// params.addBodyParameter("gid", gid);
		params.addBodyParameter("sid", sid);
		params.addBodyParameter("gsid", shareMessage.gsid);
		params.addBodyParameter("nickName", currentUser.nickName);
		params.addBodyParameter("head", currentUser.head);
		params.addBodyParameter("phoneTo", phoneTo);
		params.addBodyParameter("nickNameTo", nickNameTo);
		params.addBodyParameter("headTo", headTo);
		params.addBodyParameter("contentType", contentType);
		params.addBodyParameter("content", content);
		phoneTo = "";
		nickNameTo = "";
		headTo = "";
		httpUtils.send(HttpMethod.POST, API.SHARE_ADDCOMMENT, params, responseHandlers.share_addCommentCallBack);
	}

	public void finish() {
		taskManageHolder.viewManage.shareMessageDetailView = null;
		data.tempData.selectedImageList = null;
	}

	public void onBackPressed() {
		if (thisView.sharePopupWindow.isShowing()) {
			thisView.sharePopupWindow.dismiss();
		} else {
			thisActivity.finish();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		if (thisView.sharePopupWindow.isShowing()) {
			thisView.sharePopupWindow.dismiss();
		}
		if (requestCode == thisView.shareView.RESULT_SHAREVIEW && resultCode == Activity.RESULT_OK) {
			String key = result.getStringExtra("key");
			String type = result.getStringExtra("type");
			log.e(key + "--" + type);
			if (!"".equals(key) && !"".equals(type)) {
				if ("message".equals(type)) {
					sendToChat(key, result.getStringExtra("sendType"));
				} else if ("share".equals(type)) {
					shareToGroup(key);
				} else if ("location".equals(type)) {
					shareToSquare(key);
				}
			}
		}

	}

	public void shareToSquare(String key) {
		try {
			int position = Integer.valueOf(key);
			Location location = data.userInformation.currentUser.commonUsedLocations.get(position);
			if (location != null) {
				long time = new Date().getTime();

				SendShareMessage sendShareMessage = subData.new SendShareMessage();
				sendShareMessage.type = "imagetext";
				sendShareMessage.content = shareMessage.content;

				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("phone", currentUser.phone);
				params.addBodyParameter("accessKey", currentUser.accessKey);
				params.addBodyParameter("nickName", currentUser.nickName);
				params.addBodyParameter("head", currentUser.head);
				params.addBodyParameter("gid", Constant.SQUARE_SID);
				params.addBodyParameter("ogsid", currentUser.phone + "_" + time);
				params.addBodyParameter("sid", Constant.SQUARE_SID);
				params.addBodyParameter("location", "[" + location.longitude + "," + location.latitude + "]");
				params.addBodyParameter("address", location.address);
				params.addBodyParameter("message", gson.toJson(sendShareMessage));

				httpUtils.send(HttpMethod.POST, API.SHARE_SENDSHARE, params, responseHandlers.share_sendShareCallBack);
			}
		} catch (Exception e) {
			log.e("position is not a num");
		}
	}

	public void shareToGroup(final String key) {
		parser.check();
		Board board = null;
		if (data.boards == null || data.relationship.groupsMap.get(key) == null) {
			log.e("data.boards == null || data.relationship.groupsMap.get(key) == null");
			return;
		}
		if (data.relationship.groups.contains(key) && data.relationship.groupsMap.containsKey(key)) {
			Group group = data.relationship.groupsMap.get(key);
			if (group.boards.size() > 0) {
				board = data.boards.boardsMap.get(group.boards.get(0));
			} else {
				log.e("group.boards.size() == 0");
				return;
			}
		} else {
			log.e("!data.relationship.groups.contains(key) && !data.relationship.groupsMap.containsKey(key)");
			return;
		}
		if (board == null) {
			RequestParams params = new RequestParams();
			HttpUtils httpUtils = new HttpUtils();
			User currentUser = data.userInformation.currentUser;
			params.addBodyParameter("phone", currentUser.phone);
			params.addBodyParameter("accessKey", currentUser.accessKey);
			params.addBodyParameter("gid", key);
			httpUtils.send(HttpMethod.POST, API.SHARE_GETGROUPBOARDS, params, httpClient.new ResponseHandler<String>() {
				class Response {
					public String 提示信息;
					public String 失败原因;
					public String gid;
					public List<String> boards;
					public Map<String, Board> boardsMap;
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					Response response = gson.fromJson(responseInfo.result, Response.class);
					if (response.提示信息.equals("获取版块成功")) {
						data = parser.check();
						Group group = data.relationship.groupsMap.get(response.gid);
						for (Board board : response.boardsMap.values()) {
							data.boards.boardsMap.put(board.sid, board);
						}
						data.boards.isModified = true;
						if (group != null) {
							group.boards = response.boards;
							if ((group.currentBoard == null || "".equals(group.currentBoard)) && group.boards.size() > 0) {
								group.currentBoard = group.boards.get(0);
							}
						}
						Board board = data.boards.boardsMap.get(response.boards.get(0));
						if (board != null) {
							dealShareMessage(key, board);
						} else {
							log.e("board == null:::::::::::" + key);
						}
					} else {
						log.e("board == null");
					}
				}
			});
		} else {
			dealShareMessage(key, board);
		}

	}

	public void dealShareMessage(String key, Board board) {
		long time = new Date().getTime();
		ShareMessage shareMessage = data.new ShareMessage();
		shareMessage.content = this.shareMessage.content;
		shareMessage.type = this.shareMessage.type;
		shareMessage.nickName = currentUser.nickName;
		shareMessage.head = currentUser.head;
		// shareMessage.gid = gid;
		shareMessage.sid = sid;
		shareMessage.phone = currentUser.phone;
		shareMessage.gsid = currentUser.phone + "_" + time;
		shareMessage.mType = shareMessage.MESSAGE_TYPE_IMAGETEXT;
		shareMessage.time = time;
		shareMessage.status = "sending";

		if (board.shareMessagesOrder == null)
			board.shareMessagesOrder = new ArrayList<String>();
		if (data.boards.shareMessagesMap == null)
			data.boards.shareMessagesMap = new HashMap<String, ShareMessage>();

		board.shareMessagesOrder.add(0, shareMessage.gsid);
		data.boards.shareMessagesMap.put(shareMessage.gsid, shareMessage);
		data.boards.isModified = true;

		if (taskManageHolder.viewManage.mainView1 != null && taskManageHolder.viewManage.mainView1.shareSubView != null) {
			taskManageHolder.viewManage.mainView1.shareSubView.showShareMessages();
		}

		sendShareToServer(key, shareMessage.content, shareMessage.gsid, board.sid);
	}

	public void sendShareToServer(String key, String content, String gsid, String sid) {
		SendShareMessage sendShareMessage = subData.new SendShareMessage();
		sendShareMessage.type = "imagetext";
		sendShareMessage.content = content;

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("nickName", currentUser.nickName);
		params.addBodyParameter("head", currentUser.head);
		params.addBodyParameter("gid", key);
		params.addBodyParameter("ogsid", gsid);
		params.addBodyParameter("sid", sid);
		params.addBodyParameter("message", gson.toJson(sendShareMessage));

		httpUtils.send(HttpMethod.POST, API.SHARE_SENDSHARE, params, responseHandlers.share_sendShareCallBack);
	}

	public void sendToChat(String key, String sendType) {
		Message message = addChatToLocal(key, sendType);
		sendChatToServer(key, sendType, message.content, message.time);
	}

	public Message addChatToLocal(String key, String sendType) {
		parser.check();
		List<String> messagesOrder = data.messages.messagesOrder;
		String key0 = "";
		if ("point".equals(sendType)) {
			key0 = "p" + key;
			if (data.messages.messagesOrder.contains(key0)) {
				data.messages.messagesOrder.remove(key0);
			}
			messagesOrder.add(0, key0);
		} else if ("group".equals(sendType)) {
			key0 = "g" + key;
			if (data.messages.messagesOrder.contains(key0)) {
				data.messages.messagesOrder.remove(key0);
			}
			messagesOrder.add(0, key0);
		}
		data.messages.isModified = true;

		User currentUser = data.userInformation.currentUser;
		Message message = data.messages.new Message();
		MessageShareContent messageContent = subData.new MessageShareContent();
		// messageContent.gid = gid;
		messageContent.sid = sid;
		messageContent.gsid = shareMessage.gsid;
		messageContent.image = imageContent;
		messageContent.text = textContent;
		String content = gson.toJson(messageContent);
		message.content = content;
		message.contentType = "share";
		message.phone = currentUser.phone;
		message.nickName = currentUser.nickName;
		String time = new Date().getTime() + "";
		message.time = time;
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		if ("group".equals(sendType)) {
			message.gid = key;
			message.sendType = "group";
			message.phoneto = data.relationship.groupsMap.get(key).members.toString();
			if (data.messages.groupMessageMap == null) {
				data.messages.groupMessageMap = new HashMap<String, ArrayList<Message>>();
			}
			if (data.messages.groupMessageMap.get(key0) == null) {
				data.messages.groupMessageMap.put(key0, new ArrayList<Message>());
			}
			data.messages.groupMessageMap.get(key0).add(message);
		} else if ("point".equals(sendType)) {
			message.sendType = "point";
			message.phoneto = "[\"" + key + "\"]";
			if (data.messages.friendMessageMap == null) {
				data.messages.friendMessageMap = new HashMap<String, ArrayList<Message>>();
			}
			if (data.messages.friendMessageMap.get(key0) == null) {
				data.messages.friendMessageMap.put(key0, new ArrayList<Message>());
			}
			data.messages.friendMessageMap.get(key0).add(message);
		}

		return message;
	}

	public void sendChatToServer(String key, String sendType, String content, String time) {
		String orderKey = "";

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();

		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("sendType", sendType);
		params.addBodyParameter("contentType", "share");
		params.addBodyParameter("content", content);
		params.addBodyParameter("time", time);
		if ("group".equals(sendType)) {
			orderKey = "g" + key;
			Group group = data.relationship.groupsMap.get(key);
			if (group == null) {
				group = data.relationship.new Group();
			}
			params.addBodyParameter("gid", key);
			params.addBodyParameter("phoneto", gson.toJson(group.members));
		} else if ("point".equals(sendType)) {
			orderKey = "p" + key;
			params.addBodyParameter("phoneto", "[\"" + key + "\"]");
		}
		httpUtils.send(HttpMethod.POST, API.MESSAGE_SEND, params, responseHandlers.message_sendMessageCallBack);

		if (data.messages.messagesOrder.contains(orderKey)) {
			data.messages.messagesOrder.remove(orderKey);
			data.messages.messagesOrder.add(0, orderKey);
		} else {
			data.messages.messagesOrder.add(orderKey);
		}
		taskManageHolder.viewManage.postNotifyView("MessagesSubView");
	}

	public void showTempShare() {
		shareMessage = data.tempData.tempShareMessageMap.get(gsid);
		thisView.showShareMessageDetails();
	}

	HttpClient httpClient = HttpClient.getInstance();

	public void getShareMessageDetail() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("sid", sid);
		params.addBodyParameter("gsid", gsid);

		httpUtils.send(HttpMethod.POST, API.SHARE_GETBOARDSHARE, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
				public ShareMessage share;
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				final Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取群分享成功")) {
					boolean flag = false;
					ShareMessage shareMessage = null;
					ShareMessage serverMessage = response.share;
					try {
						flag = data.boards.shareMessagesMap.containsKey(serverMessage.gsid);
						shareMessage = data.boards.shareMessagesMap.get(serverMessage.gsid);
						if (shareMessage != null) {
							shareMessage.content = serverMessage.content;
							shareMessage.comments.clear();
							shareMessage.comments.addAll(serverMessage.comments);
							shareMessage.totalScore = serverMessage.totalScore;
							shareMessage.scores = serverMessage.scores;
						} else {
							shareMessage = serverMessage;
							data.boards.shareMessagesMap.put(shareMessage.gsid, shareMessage);
						}
						data.boards.isModified = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (thisController.shareMessage == null) {
						flag = false;
					}
					thisController.shareMessage = shareMessage;
					taskManageHolder.fileHandler.handler.post(new Runnable() {
						public void run() {
							thisView.showShareMessageDetails();
							thisView.showShareComments();
							thisView.scoreState();
						}
					});
				} else {
					taskManageHolder.fileHandler.handler.post(new Runnable() {
						public void run() {
							Toast.makeText(thisActivity, response.失败原因 + gsid, Toast.LENGTH_SHORT).show();
							Log.e(tag, ViewManage.getErrorLineNumber() + response.失败原因);
						}
					});
				}
			}
		});
	}

	public float xDown;
	public float xMove;
	public VelocityTracker mVelocityTracker;
	private static final int XSPEED_MIN = 200;

	private static final int XDISTANCE_MIN = 100;

	public boolean onTouchEvent(MotionEvent event) {
		createVelocityTracker(event);

		int id = event.getAction();

		if (id == MotionEvent.ACTION_DOWN) {
			xDown = event.getRawX();

		} else if (id == MotionEvent.ACTION_MOVE) {
			xMove = event.getRawX();
			int distanceX = (int) (xMove - xDown);
			int xSpeed = getScrollVelocity();

			if (distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
				finish();
			}
		} else if (id == MotionEvent.ACTION_UP) {
		}
		return true;
	}

	@SuppressLint("Recycle")
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}

	public void onResume() {
		data.tempData.selectedImageList = null;
		thisView.sharePopupWindow.dismiss();
		thisView.businessCardPopView.dismissUserCardDialogView();
	}
}
