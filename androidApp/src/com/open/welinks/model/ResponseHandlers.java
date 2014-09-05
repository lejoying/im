package com.open.welinks.model;

import java.util.List;

import org.apache.http.Header;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.controller.Debug1Controller;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.utils.RSAUtils;
import com.open.welinks.view.ViewManage;

public class ResponseHandlers {

	public Data data = Data.getInstance();

	public String tag = "ResponseHandlers";

	public ViewManage viewManage = ViewManage.getInstance();

	public static ResponseHandlers responseHandlers;

	public static ResponseHandlers getInstance() {
		if (responseHandlers == null) {
			responseHandlers = new ResponseHandlers();
		}
		return responseHandlers;
	}

	public HttpClient httpClient = HttpClient.getInstance();

	public ResponseHandler<String> auth = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {

		}
	};

	public ResponseHandler<String> register = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {

		}
	};

	Gson gson = new Gson();

	public ResponseHandler<String> getIntimateFriends = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Relationship relationship;

		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取密友圈成功")) {
				data.relationship.circles = response.relationship.circles;
				data.relationship.circlesMap = response.relationship.circlesMap;
				data.relationship.friendsMap.putAll(response.relationship.friendsMap);
			}
			int i = 1;
			i = i + 2;
			if (data.localStatus.debugMode.equals("NONE")) {
				viewManage.postNotifyView("UserIntimateView");
			}
		}
	};

	public ResponseHandler<String> upload = httpClient.new ResponseHandler<String>() {
		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo + "-------------");
			Header[] headers = responseInfo.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				Log.e(tag, "reply upload: " + headers[i]);
			}
		}
	};

	public ResponseHandler<String> checkFile = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String filename;
			public boolean exists;
			public String signature;
			public long expires;
			public String OSSAccessKeyId;

		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			Log.e(tag, responseInfo.result);
			if (response.提示信息.equals("查找成功")) {
				if (response.exists) {

				} else if (!response.exists) {
					Log.e(tag, response.signature + "---" + response.filename + "---" + response.expires + "---" + response.OSSAccessKeyId);
					Debug1Controller.uploadImageWithInputStreamUploadEntity(response.signature, response.filename, response.expires, response.OSSAccessKeyId);
				}
			}
		}
	};

	public ResponseHandler<String> message_sendMessageCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			// public String 失败原因;
			public long time;
			// public String sendType;
			// public String gid;
			// public String phoneto;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发送成功")) {
				// if ("point".equals(response.sendType)) {
				// //
				// data.messages.friendMessageMap.get(response.phoneto).get(0).time
				// = String.valueOf(response.time);
				// } else if ("group".equals(response.sendType)) {
				// //
				// data.messages.groupMessageMap.get(response.gid).get(0).time =
				// String.valueOf(response.time);
				// }
			}
		};
	};

	// All groups of the current user
	public ResponseHandler<String> getGroupMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Relationship relationship;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取群组成员成功")) {
				if (response.relationship.groups.size() != 0) {
					data.relationship.groups = response.relationship.groups;
					data.relationship.groupsMap = response.relationship.groupsMap;
					data.relationship.friendsMap.putAll(response.relationship.friendsMap);

					// init current share
					if (!data.localStatus.localData.currentSelectedGroup.equals("")) {
						if (data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup) == null) {
							data.localStatus.localData.currentSelectedGroup = response.relationship.groups.get(0);
						}
					} else {
						data.localStatus.localData.currentSelectedGroup = response.relationship.groups.get(0);
					}
					// Set the option group dialog content
					Log.e(tag, data.relationship.groups.toString());
					viewManage.mainView.shareSubView.setGroupsDialogContent();
					viewManage.mainView.shareSubView.showShareMessages();
					viewManage.mainView.shareSubView.showGroupMembers();
					viewManage.mainView.shareSubView.getCurrentGroupShareMessages();
				}
			}
		};
	};

	public ResponseHandler<String> account_modify = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改用户信息成功")) {
				Log.e(tag, "---------------------修改用户信息成功");
			}
		};

	};
	public ResponseHandler<String> account_auth = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String uid;
			public String accessKey;
			public String PbKey;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("普通鉴权成功")) {
				String accessKey = "", phone = "";
				try {
					accessKey = RSAUtils.decrypt(response.PbKey, response.accessKey);
					phone = RSAUtils.decrypt(response.PbKey, response.uid);
				} catch (Exception e) {
					e.printStackTrace();
				}
				viewManage.loginView.thisController.loginSuccessful(phone, accessKey);
			} else {
				viewManage.loginView.thisController.loginFail(response.失败原因);
			}
		};

	};

	public ResponseHandler<String> group_addmembers = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("加入群组成功")) {
				Log.e(tag, "---------------------加入群组成功");
			}
		};

	};

	public ResponseHandler<String> group_modify = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public Group group;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改群组信息成功")) {
				data.relationship.groupsMap.put(String.valueOf(response.group.gid), response.group);
				Log.e(tag, "---------------------修改群组信息成功");
			}
		};

	};

	public ResponseHandler<String> share_sendShareCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public long time;
			public String gid;
			public String gsid;
			public String ogsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发布群分享成功")) {
				Share share = data.shares.shareMap.get(response.gid);
				ShareMessage shareMessage = share.shareMessagesMap.get(response.ogsid);
				shareMessage.gsid = response.gsid;
				shareMessage.time = response.time;
				shareMessage.status = "sent";
				int index = share.shareMessagesOrder.indexOf(response.ogsid);
				share.shareMessagesOrder.remove(index);
				share.shareMessagesOrder.add(0, response.gsid);
				share.shareMessagesMap.remove(response.ogsid);
				share.shareMessagesMap.put(shareMessage.gsid, shareMessage);
				viewManage.mainView.shareSubView.showShareMessages();
				Log.e(tag, "---------------------发送成功");
			}
		};
	};
	public ResponseHandler<String> share_getSharesCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public Share shares;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			// Log.e(tag, responseInfo.result);
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取群分享成功")) {
					Share share = data.shares.shareMap.get(response.gid);
					if (share == null) {
						share = data.shares.new Share();
						data.shares.shareMap.put(response.gid, share);
					}
					List<String> sharesOrder = response.shares.shareMessagesOrder;
					for (int i = sharesOrder.size() - 1; i >= 0; i--) {
						String key = sharesOrder.get(i);
						if (!share.shareMessagesOrder.contains(key)) {
							share.shareMessagesOrder.add(0, key);
						}
					}
					share.shareMessagesMap.putAll(response.shares.shareMessagesMap);
					viewManage.mainView.shareSubView.showShareMessages();
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		};
	};
	public ResponseHandler<String> share_modifyPraiseusersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public String gsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("点赞群分享成功")) {
				Log.e(tag, "---------------------点赞群分享成功");
			} else if (response.提示信息.equals("点赞群分享失败")) {
				Share share = data.shares.shareMap.get(response.gid);
				ShareMessage shareMessage = share.shareMessagesMap.get(response.gsid);
				shareMessage.praiseusers.remove(data.userInformation.currentUser.phone);
			}
		};
	};
	public ResponseHandler<String> share_addCommentCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public String gsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("评论群分享成功")) {
				Log.e(tag, "---------------------评论群分享成功");
			} else if (response.提示信息.equals("评论群分享失败")) {

			}
		};
	};
	public ResponseHandler<String> relation_modifyAlias = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改备注成功")) {
				Log.e(tag, "---------------------修改备注成功");
			}
		};

	};
	public ResponseHandler<String> relation_deletefriend = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("删除成功")) {
				Log.e(tag, "---------------------删除成功");
			}
		};
	};
	public ResponseHandler<String> relation_addfriend = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发送请求成功")) {
				Log.e(tag, "---------------------发送请求成功");
			}
		};
	};

	public RequestCallBack<String> modifyCircleSequenceCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改分组顺序成功")) {
				Log.e(tag, "---------------------修改分组顺序成功");
			}
		};
	};

	public RequestCallBack<String> modifyGroupSequenceCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改群组顺序成功")) {
				Log.e(tag, "---------------------修改群组顺序成功");
			}
		};
	};
}
