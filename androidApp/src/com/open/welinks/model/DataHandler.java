package com.open.welinks.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.customListener.OnUploadLoadingListListener;
import com.open.welinks.customListener.OnUploadLoadingListener;
import com.open.welinks.model.Data.Boards.Board;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Event.EventMessage;
import com.open.welinks.model.Data.LocalStatus.LocalData.ShareDraft;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.SubData.SendShareMessage;
import com.open.welinks.model.SubData.ShareContentItem;
import com.open.welinks.oss.UploadMultipart;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ViewManage;

public class DataHandler {

	public static String tag = "DataUtil";
	public static Data data = Data.getInstance();
	public static Parser parser = Parser.getInstance();
	public static ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public static DataHandler instance;

	public static DataHandler getInstance() {
		if (instance == null) {
			instance = new DataHandler();
		}
		return instance;
	}

	public static void getIntimateFriends() {
		Log.e(tag, "getIntimateFriends");
		data = parser.check();
		RequestParams params = new RequestParams();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);

		HttpUtils http = new HttpUtils();

		http.send(HttpMethod.POST, API.RELATION_GETINTIMATEFRIENDS, params, responseHandlers.getIntimateFriends);
	}

	public static void getMessages(String flag) {
		if (flag.equals("")) {
			flag = "none";
		}
		data = parser.check();
		RequestParams params = new RequestParams();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("flag", flag);

		HttpUtils http = new HttpUtils();

		http.send(HttpMethod.POST, API.MESSAGE_GET, params, responseHandlers.getMessageCallBack);
	}

	public static void getUserCurrentGroupInfomation(String gid) {
		parser.check();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("gid", gid + "");

		httpUtils.send(HttpMethod.POST, API.GROUP_GET, params, responseHandlers.getGroupInfomationCallBack);
	}

	public static void getUserCurrentGroupMembers(String gid, String type) {
		parser.check();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("gid", gid);

		RequestCallBack<String> callBack = responseHandlers.getCurrentNewGroupMembersCallBack;
		if ("create".equals(type)) {
			callBack = responseHandlers.getCurrentNewGroupMembersCallBack;
		} else if ("removemembers".equals(type) || "addmembers".equals(type)) {
			callBack = responseHandlers.getCurrentGroupMembersCallBack;
		}

		httpUtils.send(HttpMethod.POST, API.GROUP_GETALLMEMBERS, params, callBack);
	}

	public static void getUserInfomation() {
		parser.check();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("target", "[\"" + user.phone + "\"]");

		httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, responseHandlers.getUserInfomation);
	}

	public static void getUserCurrentAllGroup() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);

		httpUtils.send(HttpMethod.POST, API.GROUP_GETGROUPMEMBERS, params, responseHandlers.getGroupMembersCallBack);
	}

	public static void getGroupBoards(String gid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		long time = System.currentTimeMillis();
		params.addBodyParameter("time", time + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_GETGROUPBOARDS, params, responseHandlers.group_getGroupBoards);
	}

	public static void clearInvalidGroupMessages() {
		try {
			parser.check();
			List<String> messageOrder = data.messages.messagesOrder;
			Set<String> set = new HashSet<String>();
			set.addAll(messageOrder);
			if (set.size() != messageOrder.size()) {
				data.messages.messagesOrder.clear();
				data.messages.messagesOrder.addAll(set);
			}
			List<String> messageOrder2 = new ArrayList<String>();
			for (int i = 0; i < messageOrder.size(); i++) {
				String id = (messageOrder.get(i));
				String firstName = id.substring(0, 1);
				String key = id.substring(1);
				if ("g".equals(firstName) && !data.relationship.groups.contains(key)) {
					messageOrder2.add(id);
				}
			}
			data.messages.isModified = true;
			messageOrder.removeAll(messageOrder2);
			// if (messageOrder2.size() != 0) {
			viewManage.postNotifyView("MessagesSubView");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ViewManage viewManage = ViewManage.getInstance();

	public static void clearInvalidFriendMessages() {
		try {
			parser.check();
			List<String> messageOrder = data.messages.messagesOrder;
			Set<String> set = new HashSet<String>();
			set.addAll(messageOrder);
			if (set.size() != messageOrder.size()) {
				data.messages.messagesOrder.clear();
				data.messages.messagesOrder.addAll(set);
			}
			List<String> messageOrder2 = new ArrayList<String>();
			for (int i = 0; i < messageOrder.size(); i++) {
				String id = (messageOrder.get(i));
				String firstName = id.substring(0, 1);
				String key = id.substring(1);
				if ("p".equals(firstName) && !data.relationship.friends.contains(key)) {
					messageOrder2.add(id);
				}
			}
			data.messages.isModified = true;
			messageOrder.removeAll(messageOrder2);
			// if (messageOrder2.size() != 0) {
			viewManage.postNotifyView("MessagesSubView");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearData() {
		try {

			data.userInformation.isModified = true;
			data.userInformation.currentUser.phone = "";
			data.userInformation.currentUser.accessKey = "";
			data.userInformation.currentUser.flag = "none";

			data.relationship = null;
			// data.relationship.isModified = false;
			// data.relationship.circles.clear();
			// data.relationship.friends.clear();
			// data.relationship.friendsMap.clear();
			// data.relationship.circlesMap.clear();
			// data.relationship.groups.clear();
			// data.relationship.groupsMap.clear();
			// data.relationship.squares.clear();

			data.messages = null;
			// data.messages.isModified = false;
			// data.messages.friendMessageMap.clear();
			// data.messages.groupMessageMap.clear();
			// data.messages.messagesOrder.clear();

			data.boards.isModified = false;
			data.boards.boardsMap.clear();

			data.event.isModified = false;
			data.event.groupEvents.clear();
			data.event.groupEventsMap.clear();
			data.event.userEvents.clear();
			data.event.userEventsMap.clear();

			data.localStatus.localData.currentSelectedGroup = "";
			data.localStatus.localData.currentSelectedGroupBoard = "";
			data.localStatus.localData.currentFunctionPage = "";
			data.localStatus.localData.currentGroupCircle = "";
			data.localStatus.localData.currentSearchRadius = 0;
			data.localStatus.localData.currentSearchTime = 0;
			// data.localStatus.localData.currentSelectedSquare = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean contains(List<Message> list, Message message) {
		int s = list.size();
		if (message != null) {
			for (int i = 0; i < s; i++) {
				if (equalsMessage(message, list.get(i))) {
					return true;
				}
			}
		} else {
			for (int i = 0; i < s; i++) {
				if (list.get(i) == null) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean equalsMessage(Message m, Message message) {
		boolean flag = false;
		if (m != null) {
			try {
				if (!"".equals(m.gid) && m.gid != null) {
					if (message.gid.equals(m.gid) && message.phone.equals(m.phone) && message.time.equals(m.time) && message.content.equals(m.content) && message.contentType.equals(m.contentType)) {
						flag = true;
						// Log.e("Data", "聊天记录已存在group");
					}
				} else {
					if (message.phone.equals(m.phone) && message.phoneto.equals(m.phoneto) && message.time.equals(m.time) && message.content.equals(m.content) && message.contentType.equals(m.contentType)) {
						flag = true;
						// Log.e("Data", "聊天记录已存在point");
					}
				}
			} catch (Exception e) {
				flag = false;
				Log.e("DataHandler", e.toString());
			}
		}
		return flag;
	}

	public Gson gson = new Gson();

	public ArrayList<String> sequece = null;
	public Map<String, ShareDraft> sequeceMap = null;

	public void sendShareMessage() {
		parser.check();
		try {
			sequece = data.localStatus.localData.shareReleaseSequece;
			sequeceMap = data.localStatus.localData.shareReleaseSequeceMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		final User currentUser = data.userInformation.currentUser;
		if (sequece != null && sequeceMap != null) {
			for (final String ogsid : sequece) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						final ShareDraft entity = sequeceMap.get(ogsid);
						try {
							if (!data.boards.boardsMap.get(entity.sid).shareMessagesOrder.contains(ogsid)) {
								return;
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							return;
						}
						OnUploadLoadingListListener onUploadLoadingListListener;
						onUploadLoadingListListener = new OnUploadLoadingListListener() {
							@Override
							public void onSuccess(OnUploadLoadingListListener instance) {
								super.onSuccess(instance);
								try {
									sendMessageToServer(instance.shareMessage.content, entity.gid, entity.gsid, entity.sid);
								} catch (Exception e) {
									sendMessageToServer("出现bug", entity.gid, entity.gsid, entity.sid);
									e.printStackTrace();
								}
							}
						};
						long time = new Date().getTime();

						parser.check();
						if (data.boards == null) {
							data.boards = data.new Boards();
						}
						if (data.boards.boardsMap.get(entity.sid) == null) {
							Board board = data.boards.new Board();
							data.boards.boardsMap.put(entity.sid, board);
						}
						Board board = data.boards.boardsMap.get(entity.sid);
						ShareMessage shareMessage = data.boards.new ShareMessage();
						shareMessage.mType = shareMessage.MESSAGE_TYPE_IMAGETEXT;
						shareMessage.gsid = entity.gsid;
						shareMessage.sid = entity.sid;
						shareMessage.type = "imagetext";
						shareMessage.phone = currentUser.phone;
						shareMessage.time = time;
						shareMessage.status = "sending";

						onUploadLoadingListListener.shareMessage = shareMessage;

						// ShareContent shareContent = SubData.getInstance().new ShareContent();
						List<ShareContentItem> shareContentItems = new ArrayList<ShareContentItem>();
						ShareContentItem shareContentItem = subData.new ShareContentItem();
						shareContentItem.type = "text";
						shareContentItem.detail = entity.content;
						shareContentItems.add(shareContentItem);

						if (!"".equals(entity.imagesContent)) {
							List<String> imageList = gson.fromJson(entity.imagesContent, new TypeToken<List<String>>() {
							}.getType());
							if (imageList.size() != 0) {
								onUploadLoadingListListener.totalUploadCount = imageList.size();
								copyFileToSprecifiedDirecytory(shareContentItems, imageList, onUploadLoadingListListener);
							} else {
								String content = gson.toJson(shareContentItems);
								sendMessageToServer(content, entity.gid, shareMessage.gsid, shareMessage.sid);
							}
						} else {
							String content = gson.toJson(shareContentItems);
							sendMessageToServer(content, entity.gid, shareMessage.gsid, shareMessage.sid);
						}

						String content = gson.toJson(shareContentItems);
						Log.e(tag, content);
						shareMessage.content = content;

						// To add data to the data
						if (board.shareMessagesOrder.contains(entity.gsid)) {
							board.shareMessagesOrder.add(0, shareMessage.gsid);
							data.boards.shareMessagesMap.put(shareMessage.gsid, shareMessage);
							data.boards.isModified = true;

							// Local data diaplay in MainHandler
							if ("square".equals(entity.gtype)) {
								viewManage.postNotifyView("SquareSubViewMessage");
							}
							if ("share".equals(entity.gtype)) {
								viewManage.postNotifyView("ShareSubViewMessage");
							}

						}
					}
				}).start();
			}
		}
	}

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();
	public SHA1 sha1 = new SHA1();
	public float imageHeightScale = 0.5686505598114319f;
	public OnUploadLoadingListener uploadLoadingListener;

	public void copyFileToSprecifiedDirecytory(List<ShareContentItem> shareContentItems, List<String> list, OnUploadLoadingListListener onUploadLoadingListListener) {
		// ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		// int totalLength = 0;
		for (int i = 0; i < list.size(); i++) {
			String key = list.get(i);
			String suffixName = key.substring(key.lastIndexOf("."));
			suffixName = suffixName.toLowerCase(Locale.getDefault());
			if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
				suffixName = ".osj";
			} else if (suffixName.equals(".png")) {
				suffixName = ".osp";
			}
			try {
				String fileName = "";
				File fromFile = new File(key);
				if (!fromFile.exists()) {
					return;
				}
				byte[] bytes = null;
				ShareContentItem shareContentItem = subData.new ShareContentItem();

				bytes = taskManageHolder.fileHandler.getImageFileBytes(shareContentItem, fromFile, viewManage.mainView.displayMetrics.heightPixels, viewManage.mainView.displayMetrics.heightPixels);
				// int fileLength = bytes.length;
				// totalLength += fileLength;
				// fileTotalLengthMap.put(key, fileLength);

				String sha1FileName = sha1.getDigestOfString(bytes);
				fileName = sha1FileName + suffixName;
				File toFile = new File(taskManageHolder.fileHandler.sdcardImageFolder, fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(toFile);
				StreamParser.parseToFile(bytes, fileOutputStream);

				if (i == 0) {
					int showImageWidth = viewManage.mainView.displayMetrics.widthPixels;
					File toSnapFile = new File(taskManageHolder.fileHandler.sdcardThumbnailFolder, fileName);
					int showImageHeight = (int) (viewManage.mainView.displayMetrics.widthPixels * imageHeightScale);
					taskManageHolder.fileHandler.makeImageThumbnail(fromFile, showImageWidth, showImageHeight, toSnapFile, fileName);
				}

				shareContentItem.type = "image";
				shareContentItem.detail = fileName;
				shareContentItems.add(shareContentItem);

				// uploadFileNameMap.put(key, fileName);
				UploadMultipart multipart = new UploadMultipart(key, fileName, bytes, UploadMultipart.UPLOAD_TYPE_IMAGE);
				multipart.path = key;
				taskManageHolder.uploadMultipartList.addMultipart(multipart);
				multipart.setUploadLoadingListener(onUploadLoadingListListener.uploadLoadingListener);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public SubData subData = SubData.getInstance();

	public void sendMessageToServer(String content, String gid, String gsid, String sid) {

		SendShareMessage sendShareMessage = subData.new SendShareMessage();
		sendShareMessage.type = "imagetext";
		sendShareMessage.content = content;

		parser.check();
		User currentUser = data.userInformation.currentUser;
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("ogsid", gsid);
		params.addBodyParameter("sid", sid);
		params.addBodyParameter("nickName", currentUser.nickName);
		params.addBodyParameter("head", currentUser.head);
		// params.addBodyParameter("location", currentUser.longitude + "," + currentUser.latitude);
		// params.addBodyParameter("address", currentUser.address);
		params.addBodyParameter("message", gson.toJson(sendShareMessage));

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		httpUtils.send(HttpMethod.POST, API.SHARE_SENDSHARE, params, responseHandlers.share_sendShareCallBack);
	}

	public static String switchChatMessageEvent(EventMessage event) {
		String content = "";
		String nickName = event.phone;
		Friend friend = data.relationship.friendsMap.get(nickName);
		if (friend != null) {
			nickName = friend.nickName;
			if (friend.phone.equals(data.userInformation.currentUser.phone)) {
				nickName = "您";
			}
		}
		final Group group = data.relationship.groupsMap.get(event.gid + "");
		String groupName = event.gid;
		if (group != null) {
			groupName = group.name;
		}
		String contentType = event.type;
		if ("group_addmembers".equals(contentType)) {
			content = "【" + nickName + "】 邀请了" + event.content + "个好友到 【" + groupName + "】 群组中.";
		} else if ("group_removemembers".equals(contentType)) {
			content = "【" + nickName + "】 从【" + groupName + "】 移除了" + event.content + "个好友.";
		} else if ("group_dataupdate".equals(contentType)) {
			content = "【" + nickName + "】 更新了 【" + groupName + "】 的资料信息.";
		} else if ("group_create".equals(contentType)) {
			content = "【" + nickName + "】创建了新的群组:【" + groupName + "】.";
		} else if ("group_addme".equals(contentType)) {
			if ("您".equals(nickName)) {
				content = "加入【" + groupName + "】群组.";
			} else {
				content = "【" + nickName + "】把你从添加到群组：【" + groupName + "】.";
			}
		} else if ("group_removeme".equals(contentType)) {
			content = "【" + nickName + "】退出了【" + groupName + "】群组.";
		}
		return content;
	}
}
