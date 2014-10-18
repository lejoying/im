package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.HttpClient;
import com.open.welinks.ImageScanActivity;
import com.open.welinks.R;
import com.open.welinks.SharePraiseusersActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.InnerScrollView.OnScrollChangedListener;
import com.open.welinks.customView.ShareView.onWeChatClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.Comment;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.SubData;
import com.open.welinks.model.SubData.MessageShareContent;
import com.open.welinks.model.SubData.SendShareMessage;
import com.open.welinks.view.PictureBrowseView;
import com.open.welinks.view.ShareMessageDetailView;
import com.open.welinks.view.ViewManage;

public class ShareMessageDetailController {

	public String tag = "ShareMessageDetailController";
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public SubData subData = SubData.getInstance();

	public Gson gson = new Gson();

	public Context context;
	public ShareMessageDetailView thisView;
	public ShareMessageDetailController thisController;
	public Activity thisActivity;

	public String gsid = "";
	public ShareMessage shareMessage;
	public Share share;

	public String textContent;
	public String imageContent;

	public OnClickListener mOnClickListener;
	public OnScrollChangedListener mOnScrollChangedListener;
	public OnTouchListener mOnTouchListener;
	public OnDownloadListener downloadListener;
	public TextWatcher textWatcher;
	public onWeChatClickListener mOnWeChatClickListener;

	public int IMAGEBROWSE_REQUESTCODE = 0x01;

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public User currentUser;

	public String phoneTo = "";
	public String nickNameTo = "";
	public String headTo;

	public int initialHeight;

	public String gid;

	public Bitmap WeChatBitmap;

	public ShareMessageDetailController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		thisController = this;

		currentUser = data.userInformation.currentUser;
	}

	public void initData() {
		parser.check();
		gid = thisActivity.getIntent().getStringExtra("gid");
		String gsid = thisActivity.getIntent().getStringExtra("gsid");
		if (gsid != null) {
			this.gsid = gsid;
			if (data.shares.shareMap.containsKey(gid)) {
				share = data.shares.shareMap.get(gid);
				shareMessage = share.shareMessagesMap.get(gsid);
			} else if (data.tempData.tempShareMessageMap.containsKey(gsid)) {
				share = data.shares.new Share();
				shareMessage = data.tempData.tempShareMessageMap.get(gsid);
				getShareMessageDetail();
			} else {
				share = data.shares.new Share();
				// getShareFromServer(gid, gsid);
				getShareMessageDetail();
			}
		}
	}

	public void initShareListener() {
		mOnWeChatClickListener = thisView.shareView.new onWeChatClickListener() {
			@Override
			public void onWeChatClick() {
				thisView.shareView.setWeChatContent(WeChatBitmap, textContent, shareMessage.phone, gid, thisController.gsid);
			}
		};
	}

	public void getShareFromServer(String gid, String gsid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("gsid", gsid);
		httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARE, params, responseHandlers.share_get);
	}

	public FileHandlers fileHandlers = FileHandlers.getInstance();

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
				fileHandlers.handler.post(new Runnable() {

					@Override
					public void run() {
						thisView.controlProgress.moveTo((int) (currentPrecent * 100));
					}
				});

				final ImageView imageView = (ImageView) instance.view;
				thisView.imageLoader.displayImage("file://" + instance.path, imageView, thisView.displayImageOptions, new SimpleImageLoadingListener() {
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
				fileHandlers.handler.post(new Runnable() {

					@Override
					public void run() {
						thisView.controlProgress.moveTo((int) (currentPrecent * 100));
					}
				});
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				// TODO Auto-generated method stub

			}
		};
		textWatcher = new TextWatcher() {

			String content = "";

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				content = s.toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				LayoutParams commentLayoutParams = thisView.commentInputView.getLayoutParams();
				commentLayoutParams.height = (int) ((45 * thisView.screenDensity + 0.5f) + thisView.commentEditTextView.getHeight() - 40);
			}

			@Override
			public void afterTextChanged(Editable s) {
				int selectionIndex = thisView.commentEditTextView.getSelectionStart();
				if (!(s.toString()).equals(content)) {
					thisView.commentEditTextView.setText(s.toString());
					thisView.commentEditTextView.setSelection(selectionIndex);
				}
				if ("".equals(thisView.commentEditTextView.getText().toString())) {
					thisView.sendCommentView.setBackgroundResource(R.drawable.comment_notselected);
					thisView.sendCommentView.setTextColor(Color.WHITE);
				} else {
					thisView.sendCommentView.setBackgroundResource(R.drawable.comment_selected);
					thisView.sendCommentView.setTextColor(Color.BLACK);
				}
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
		mOnScrollChangedListener = new OnScrollChangedListener() {

			@Override
			public void onScrollChangedListener(int l, int t, int oldl, int oldt) {
				if (thisView.commentInputView.getVisibility() == View.VISIBLE) {
					thisView.commentInputView.setVisibility(View.GONE);
				}
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.praiseIconView)) {
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
						thisView.praiseIconView.setImageResource(R.drawable.praised_icon);
					} else {
						ArrayList<String> list = new ArrayList<String>();
						for (int i = 0; i < shareMessage.praiseusers.size(); i++) {
							if (shareMessage.praiseusers.get(i).equals(currentUser.phone)) {
								list.add(shareMessage.praiseusers.get(i));
							}
						}
						shareMessage.praiseusers.removeAll(list);
						thisView.praiseIconView.setImageResource(R.drawable.praise_icon);
					}
					thisView.showPraiseUsersContent();
					modifyPraiseusersToMessage(option);
				} else if (view.equals(thisView.commentIconView)) {
					if (thisView.commentInputView.getVisibility() == View.GONE) {
						thisView.commentInputView.setVisibility(View.VISIBLE);
						int offset = thisView.mainScrollInnerView.getMeasuredHeight() - thisView.mainScrollView.getHeight();
						thisView.mainScrollView.scrollTo(0, offset);
					} else {
						thisView.commentInputView.setVisibility(View.GONE);
					}
				} else if (view.equals(thisView.praiseUserContentView)) {
					// Toast.makeText(thisActivity, "praiseUserContentView",
					// Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(thisActivity, SharePraiseusersActivity.class);
					data.tempData.praiseusersList = shareMessage.praiseusers;
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.menuImage)) {
					if (thisView.menuOptionsView.getVisibility() == View.GONE) {
						thisView.menuOptionsView.setVisibility(View.VISIBLE);
						thisView.dialogSpring.addListener(thisView.dialogSpringListener);
						thisView.dialogSpring.setCurrentValue(0);
						thisView.dialogSpring.setEndValue(1);
					} else {
						thisView.menuOptionsView.setVisibility(View.GONE);
					}
				} else if (view.equals(thisView.confirmSendCommentView)) {
					String commentContent = thisView.commentEditTextView.getText().toString().trim();
					thisView.commentEditTextView.setText("");
					thisView.commentEditTextView.setHint("添加评论 ... ...");
					if (thisView.commentInputView.getVisibility() == View.VISIBLE) {
						thisView.commentInputView.setVisibility(View.GONE);
					}
					if (thisView.inputMethodManager.isActive()) {
						thisView.inputMethodManager.hideSoftInputFromWindow(thisView.commentInputView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
					Comment comment = share.new Comment();
					comment.phone = currentUser.phone;
					comment.nickName = currentUser.nickName;
					comment.head = currentUser.head;
					comment.phoneTo = phoneTo;
					comment.nickNameTo = nickNameTo;
					comment.headTo = headTo;
					comment.contentType = "text";
					comment.content = commentContent;
					comment.time = new Date().getTime();
					shareMessage.comments.add(comment);

					thisView.notifyShareMessageComments();

					thisView.mainScrollView.fullScroll(ScrollView.FOCUS_DOWN);

					addCommentToMessage("text", commentContent);
				} else if (view.equals(thisView.deleteOptionView)) {
					deleteGroupShare();
				} else if (view.equals(thisView.shareOptionView)) {
					thisView.menuOptionsView.setVisibility(View.GONE);
					thisView.sharePopupWindow.showAtLocation(thisActivity.findViewById(R.id.mainScrollView), Gravity.CENTER, 0, 0);
				} else if (view.equals(thisView.shareView)) {
					thisView.sharePopupWindow.dismiss();
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("ShareMessageDetailImage".equals(type)) {
						data.tempData.selectedImageList = thisView.showImages;
						Intent intent = new Intent(thisActivity, ImageScanActivity.class);
						intent.putExtra("position", content);
						intent.putExtra("type", PictureBrowseView.IMAGEBROWSE_COMMON);
						thisActivity.startActivityForResult(intent, IMAGEBROWSE_REQUESTCODE);
					} else if ("ShareComment".equals(type)) {
						Comment comment = (Comment) view.getTag(R.id.commentEditTextView);
						if (thisView.commentInputView.getVisibility() == View.GONE) {
							thisView.commentInputView.setVisibility(View.VISIBLE);
						}
						if (!content.equals(currentUser.phone)) {
							phoneTo = comment.phone;
							nickNameTo = comment.nickName;
							headTo = comment.head;
							thisView.commentEditTextView.setHint("回复" + nickNameTo);
						} else {
							phoneTo = "";
							nickNameTo = "";
							headTo = "";
							thisView.commentEditTextView.setHint("添加评论 ... ...");
						}
					}
				}
			}

		};
	}

	public void bindEvent() {
		thisView.menuImage.setOnClickListener(mOnClickListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.praiseUserContentView.setOnClickListener(mOnClickListener);
		thisView.praiseIconView.setOnClickListener(mOnClickListener);
		thisView.commentIconView.setOnClickListener(mOnClickListener);
		thisView.confirmSendCommentView.setOnClickListener(mOnClickListener);
		thisView.deleteOptionView.setOnClickListener(mOnClickListener);
		thisView.shareOptionView.setOnClickListener(mOnClickListener);

		thisView.mainScrollView.setOnTouchListener(mOnTouchListener);
		thisView.detailScrollView.setOnScrollChangedListener(mOnScrollChangedListener);

		thisView.sharePopupWindow.setTouchInterceptor(mOnTouchListener);
		thisView.shareView.setOnClickListener(mOnClickListener);

		thisView.commentEditTextView.addTextChangedListener(textWatcher);

		thisView.shareView.setOnWeChatClickListener(mOnWeChatClickListener);

	}

	public void deleteGroupShare() {
		if (shareMessage.phone.equals(data.userInformation.currentUser.phone)) {
			Alert.createDialog(thisActivity).setTitle("是否删除这条分享？").setOnConfirmClickListener(new OnDialogClickListener() {
				@Override
				public void onClick(AlertInputDialog dialog) {
					RequestParams params = new RequestParams();
					HttpUtils httpUtils = new HttpUtils();
					params.addBodyParameter("phone", data.userInformation.currentUser.phone);
					params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
					params.addBodyParameter("gid", gid);
					params.addBodyParameter("gsid", gsid);

					data = parser.check();
					Share share = data.shares.shareMap.get(gid);
					share.shareMessagesOrder.remove(gsid);
					// share.shareMessagesMap.remove(gsid);
					data.shares.isModified = true;
					viewManage.postNotifyView("ShareSubViewMessage");
					httpUtils.send(HttpMethod.POST, API.SHARE_DELETE, params, responseHandlers.share_delete);
					thisActivity.finish();
				}
			}).show();
		}
	}

	public void modifyPraiseusersToMessage(boolean option) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("gsid", shareMessage.gsid);
		params.addBodyParameter("option", option + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_ADDPRAISE, params, responseHandlers.share_modifyPraiseusersCallBack);
	}

	public void addCommentToMessage(String contentType, String content) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", gid);
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
		thisView.viewManage.shareMessageDetailView = null;
		data.tempData.selectedImageList = null;
	}

	public void onWindwoFocusChanged() {
		initialHeight = thisView.commentEditTextView.getHeight();
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
			if (!"".equals(key) && !"".equals(type)) {
				if ("message".equals(type)) {
					sendToChat(key, result.getStringExtra("sendType"));
				} else if ("share".equals(type)) {
					shareToGroup(key);
				}
			}
		}

	}

	public void shareToGroup(String key) {
		parser.check();
		if (data.shares == null) {
			data.shares = data.new Shares();
		}
		if (data.shares.shareMap.get(key) == null) {
			Share share = data.shares.new Share();
			data.shares.shareMap.put(key, share);
		}
		long time = new Date().getTime();
		Share share = data.shares.shareMap.get(key);

		ShareMessage shareMessage = share.new ShareMessage();
		shareMessage.content = this.shareMessage.content;
		shareMessage.type = this.shareMessage.type;
		shareMessage.nickName = currentUser.nickName;
		shareMessage.head = currentUser.head;
		shareMessage.phone = currentUser.phone;
		shareMessage.gsid = currentUser.phone + "_" + time;
		shareMessage.mType = shareMessage.MESSAGE_TYPE_IMAGETEXT;
		shareMessage.time = time;
		shareMessage.status = "sending";

		share.shareMessagesOrder.add(0, shareMessage.gsid);
		share.shareMessagesMap.put(shareMessage.gsid, shareMessage);
		data.shares.isModified = true;

		if (data.relationship.squares.contains(key)) {
			viewManage.mainView.squareSubView.showSquareMessages();
		} else {
			viewManage.mainView.shareSubView.showShareMessages();
		}

		sendShareToServer(key, shareMessage.content, shareMessage.gsid);

	}

	public void sendShareToServer(String key, String content, String gsid) {
		SendShareMessage sendShareMessage = subData.new SendShareMessage();
		sendShareMessage.type = "imagetext";
		sendShareMessage.content = content;

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", key);
		params.addBodyParameter("ogsid", gsid);
		params.addBodyParameter("message", gson.toJson(sendShareMessage));

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		httpUtils.send(HttpMethod.POST, API.SHARE_SENDSHARE, params, responseHandlers.share_sendShareCallBack);
	}

	public void sendToChat(String key, String sendType) {
		sendChatToServer(key, sendType, addChatToLocal(key, sendType));
	}

	public String addChatToLocal(String key, String sendType) {
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

		User user = data.userInformation.currentUser;
		Message message = data.messages.new Message();
		MessageShareContent messageContent = subData.new MessageShareContent();
		messageContent.gid = gid;
		messageContent.gsid = shareMessage.gsid;
		messageContent.image = imageContent;
		messageContent.text = textContent;
		String content = gson.toJson(messageContent);
		message.content = content;
		message.contentType = "share";
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(new Date().getTime());
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

		return content;
	}

	public void sendChatToServer(String key, String sendType, String content) {
		String orderKay = "";

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();

		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("sendType", sendType);
		params.addBodyParameter("contentType", "share");
		params.addBodyParameter("content", content);
		if ("group".equals(sendType)) {
			orderKay = "g" + key;
			Group group = data.relationship.groupsMap.get(key);
			if (group == null) {
				group = data.relationship.new Group();
			}
			params.addBodyParameter("gid", key);
			params.addBodyParameter("phoneto", gson.toJson(group.members));
		} else if ("point".equals(sendType)) {
			orderKay = "p" + key;
			List<String> phoneto = new ArrayList<String>();
			phoneto.add(key);
			params.addBodyParameter("phoneto", gson.toJson(phoneto));
			params.addBodyParameter("gid", "");
		}
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.MESSAGE_SEND, params, responseHandlers.message_sendMessageCallBack);

		if (data.messages.messagesOrder.contains(orderKay)) {
			data.messages.messagesOrder.remove(orderKay);
			data.messages.messagesOrder.add(0, orderKay);
		} else {
			data.messages.messagesOrder.add(orderKay);
		}
		viewManage.messagesSubView.showMessagesSequence();

	}

	public void showTempShare() {
		shareMessage = data.tempData.tempShareMessageMap.get(gsid);
		thisView.showShareMessageDetail();
	}

	public void getShareMessageDetail() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("gsid", gsid);

		HttpClient httpClient = HttpClient.getInstance();

		httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARE, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
				public List<ShareMessage> shares;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				final Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取群分享成功")) {
					ShareMessage shareMessage = response.shares.get(0);
					boolean flag = false;
					try {
						flag = data.shares.shareMap.get(gid).shareMessagesMap.containsKey(shareMessage.gsid);
						data.shares.shareMap.get(gid).shareMessagesMap.put(shareMessage.gsid, shareMessage);
						data.shares.isModified = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					thisController.shareMessage = shareMessage;
					final boolean flag0 = flag;
					thisView.fileHandlers.handler.post(new Runnable() {
						public void run() {
							if (flag0) {
								thisView.showPraiseUsersContent();
								thisView.notifyShareMessageComments();
							} else {
								thisView.showShareMessageDetail();
							}
						}
					});
				} else {
					thisView.fileHandlers.handler.post(new Runnable() {
						public void run() {
							Toast.makeText(thisActivity, response.失败原因, Toast.LENGTH_SHORT).show();
						}
					});
					Log.e(tag, response.失败原因);
				}
			};
		});
	}
}
