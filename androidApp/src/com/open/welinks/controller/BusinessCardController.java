package com.open.welinks.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.AddFriendActivity;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.ChatActivity;
import com.open.welinks.ModifyInformationActivity;
import com.open.welinks.R;
import com.open.welinks.ShareListActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.BusinessCardView;
import com.open.welinks.view.BusinessCardView.Status;
import com.open.welinks.view.ViewManage;

public class BusinessCardController {

	public String tag = "BusinessCardController";
	public MyLog log = new MyLog(tag, true);

	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public DownloadFile downloadFile;

	public String key, type;
	// public boolean isTemp;
	public File file;

	public OnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public OnDownloadListener downloadListener;
	public DisplayMetrics displayMetrics;

	public GestureDetector backDetector;

	// public Handler handler;

	public ViewManage viewManage = ViewManage.getInstance();

	public static final int REQUESTCODE_MODIFY = 0x1, REQUESTCODE_ADD = 0x2;

	public BusinessCardController(BusinessCardActivity activity) {
		thisActivity = activity;
		thisController = this;
	}

	public void onCreate() {
		key = thisActivity.getIntent().getStringExtra("key");
		type = thisActivity.getIntent().getStringExtra("type");
		checkCardTypeAndRelation(type, key);
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	}

	public void checkCardTypeAndRelation(String type, String key) {
		parser.check();
		if ("point".equals(type)) {
			if (key.equals(data.userInformation.currentUser.phone)) {
				thisView.status = Status.SELF;
			} else if (data.relationship.friends != null) {
				if (data.relationship.friends.contains(key)) {
					thisView.status = Status.FRIEND;
				} else {
					thisView.status = Status.TEMPFRIEND;
				}
			} else {
				thisView.status = Status.TEMPFRIEND;
			}
		} else if ("group".equals(type)) {
			if (data.relationship.groups != null) {
				if (data.relationship.groups.contains(key)) {
					thisView.status = Status.JOINEDGROUP;
				} else {
					thisView.status = Status.NOTJOINGROUP;
				}
			} else {
				thisView.status = Status.NOTJOINGROUP;
			}
		} else if ("square".equals(type)) {
			if (data.relationship.squares != null) {
				if (data.relationship.squares.contains(key)) {
					thisView.status = Status.SQUARE;
				} else {
					thisView.status = Status.SQUARE;
				}
			} else {
				thisView.status = Status.SQUARE;
			}
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		thisView.spacingOne.setHeight(displayMetrics.heightPixels - thisView.spacingTwo.getHeight() - thisView.infomationLayout.getHeight() - thisView.backView.getHeight() - data.tempData.statusBarHeight);
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.buttonOne)) {
					if (thisView.status.equals(Status.SELF)) {
						modifyInformation();
					} else if (thisView.status.equals(Status.FRIEND)) {
						startChat("point");
					} else if (thisView.status.equals(Status.JOINEDGROUP)) {
						startChat("group");
					} else if (thisView.status.equals(Status.TEMPFRIEND)) {
						addFriend();
					} else if (thisView.status.equals(Status.NOTJOINGROUP)) {
						joinGroup();
					} else if (thisView.status.equals(Status.SQUARE)) {
						// unused
					}
				} else if (view.equals(thisView.buttonTwo)) {
					if (thisView.status.equals(Status.SELF)) {
						// unused
					} else if (thisView.status.equals(Status.FRIEND)) {
						modifyAlias();
					} else if (thisView.status.equals(Status.JOINEDGROUP)) {
						modifyInformation();
					} else if (thisView.status.equals(Status.TEMPFRIEND)) {
						// unused
					} else if (thisView.status.equals(Status.NOTJOINGROUP)) {
						// unused
					} else if (thisView.status.equals(Status.SQUARE)) {
						// unused
					}
				} else if (view.equals(thisView.buttonThree)) {
					if (thisView.status.equals(Status.SELF)) {
						// unused
					} else if (thisView.status.equals(Status.FRIEND)) {
						terminateRelationship();
					} else if (thisView.status.equals(Status.JOINEDGROUP)) {
						// unused
					} else if (thisView.status.equals(Status.TEMPFRIEND)) {
						// unused
					} else if (thisView.status.equals(Status.NOTJOINGROUP)) {
						// unused
					} else if (thisView.status.equals(Status.SQUARE)) {
						// unused
					}
				} else if (view.equals(thisView.buttonFour)) {
					updateFriendToBlackList();
				} else if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.rightTopButton)) {
					if (thisView.status.equals(Status.SELF)) {
						modifyInformation();
					} else if (thisView.status.equals(Status.FRIEND)) {
						startChat("point");
					} else if (thisView.status.equals(Status.JOINEDGROUP)) {
						startChat("group");
					} else if (thisView.status.equals(Status.TEMPFRIEND)) {
						addFriend();
					} else if (thisView.status.equals(Status.NOTJOINGROUP)) {
						joinGroup();
					}
				} else if (view.equals(thisView.myShareView)) {
					Intent intent = new Intent(thisActivity, ShareListActivity.class);
					intent.putExtra("key", key);
					thisActivity.startActivity(intent);
				}
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				return false;
			}
		};
		downloadListener = new OnDownloadListener() {

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, viewManage.options40);
			}

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
			}
		};
	}

	public void bindEvent() {
		thisView.rightTopButton.setOnClickListener(mOnClickListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.buttonOne.setOnClickListener(mOnClickListener);
		thisView.buttonTwo.setOnClickListener(mOnClickListener);
		thisView.buttonThree.setOnClickListener(mOnClickListener);
		thisView.buttonFour.setOnClickListener(mOnClickListener);
		thisView.myShareView.setOnClickListener(mOnClickListener);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUESTCODE_MODIFY && resultCode == Activity.RESULT_OK) {
			thisView.fillData();
		} else if (requestCode == REQUESTCODE_ADD && resultCode == Activity.RESULT_OK) {

		}
	}

	public void modifyInformation() {
		Intent intent = new Intent(thisActivity, ModifyInformationActivity.class);
		intent.putExtra("key", key);
		intent.putExtra("type", type);
		thisActivity.startActivityForResult(intent, REQUESTCODE_MODIFY);
	}

	public void startChat(String type) {
		Intent intent = new Intent(thisActivity, ChatActivity.class);
		intent.putExtra("id", key);
		intent.putExtra("type", type);
		thisActivity.startActivity(intent);
	}

	public void modifyAlias() {
		final Friend friend = thisController.data.relationship.friendsMap.get(key);
		AlertInputDialog alert = new AlertInputDialog(thisActivity);
		alert.setTitle("请输入好友的备注").setLeftButtonText("修改").setInputText(friend.alias).setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String alias = dialog.getInputText();
				friend.alias = alias;
				thisView.fillData();
				viewManage.postNotifyView("UserIntimateView");
				uploadAlias(alias);
			}
		}).show();
	}

	public HttpClient httpClient = HttpClient.getInstance();
	public Gson gson = new Gson();

	public void getFriendCard(String phone) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("target", "[\"" + phone + "\"]");

		httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public List<Friend> accounts;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("获取用户信息成功".equals(response.提示信息)) {
					Friend account = response.accounts.get(0);
					Friend friend = null;
					if (data.relationship.friends.contains(account.phone)) {
						friend = data.relationship.friendsMap.get(account.phone);
					} else {
						friend = data.relationship.new Friend();
						data.relationship.friendsMap.put(account.phone, friend);
					}
					if (friend != null) {
						friend.phone = account.phone;
						friend.head = account.head;
						friend.nickName = account.nickName;
						friend.mainBusiness = account.mainBusiness;
						friend.sex = account.sex;
						friend.age = Integer.valueOf(account.age);
						friend.createTime = account.createTime;
						friend.userBackground = account.userBackground;
						friend.id = account.id;
						data.relationship.isModified = true;
					}
					User user = data.userInformation.currentUser;
					if (user.phone.equals(account.phone)) {
						user.id = account.id;
						user.head = account.head;
						user.mainBusiness = account.mainBusiness;
						user.nickName = account.nickName;
						user.sex = account.sex;
						user.createTime = account.createTime;
						user.userBackground = account.userBackground;
						data.userInformation.isModified = true;
					}
					thisView.isGetData = true;
					data.tempData.tempFriend = friend;
					thisView.fillData();
				}
			};
		});
	}

	public void getGroupCard(final String gid, String type) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", gid);
		params.addBodyParameter("type", type);

		httpUtils.send(HttpMethod.POST, API.GROUP_GET, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public Group group;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("获取群组信息成功".equals(response.提示信息)) {
					Group group = response.group;
					if (group != null) {
						Group currentGroup = null;
						if (data.relationship.groups.contains(group.gid)) {
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
						data.relationship.isModified = true;
						data.tempData.tempGroup = group;
						thisView.isGetData = true;
						thisView.fillData();
					}
				}
			};
		});
	}

	public void updateFriendToBlackList() {
		final Friend friend = thisController.data.relationship.friendsMap.get(key);
		User user = data.userInformation.currentUser;
		final List<String> blackList = user.blackList;
		String title = "";
		if (blackList.contains(key)) {
			title = "确定把" + friend.nickName + "移出黑名单吗？";
		} else {
			title = "确定把" + friend.nickName + "添加到黑名单吗？";
		}
		Alert.createDialog(thisActivity).setTitle(title).setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				boolean flag = false;
				if (blackList.contains(key)) {
					flag = false;
					blackList.remove(key);
				} else {
					flag = true;
					blackList.add(key);
				}
				data.userInformation.isModified = true;
				thisView.fillData();
				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("phone", data.userInformation.currentUser.phone);
				params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
				params.addBodyParameter("target", friend.phone);
				params.addBodyParameter("operation", flag + "");
				ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
				httpUtils.send(HttpMethod.POST, API.RELATION_BLACKLIST, params, responseHandlers.relation_blackList);
			}
		}).show();
	}

	public void terminateRelationship() {
		final Friend friend = thisController.data.relationship.friendsMap.get(key);
		Alert.createDialog(thisActivity).setTitle("确定解除和" + friend.nickName + "的好友关系吗？").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				deleteCircleFriendData(key);
				thisController.data.relationship.friends.remove(key);
				thisView.status = Status.TEMPFRIEND;
				data.tempData.tempFriend = friend;
				thisView.fillData();
				viewManage.postNotifyView("UserIntimateView");
				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("phone", data.userInformation.currentUser.phone);
				params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
				params.addBodyParameter("target", friend.phone);
				ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
				httpUtils.send(HttpMethod.POST, API.RELATION_DELETEFRIEND, params, responseHandlers.relation_deletefriend);
			}
		}).show();
	}

	public void deleteCircleFriendData(String phone) {
		data = parser.check();
		List<String> circles = data.relationship.circles;
		Map<String, Circle> circlesMap = data.relationship.circlesMap;
		for (int i = 0; i < circles.size(); i++) {
			String key1 = circles.get(i);
			Circle circle = circlesMap.get(key1);
			B: for (int j = 0; j < circle.friends.size(); j++) {
				String key2 = circle.friends.get(j);
				if (key2.equals(phone)) {
					circle.friends.remove(key2);
					break B;
				}
			}
		}
		data.relationship.isModified = true;
	}

	public void addFriend() {
		Intent intent = new Intent(thisActivity, AddFriendActivity.class);
		intent.putExtra("key", key);
		thisActivity.startActivityForResult(intent, REQUESTCODE_ADD);
	}

	public void joinGroup() {
		if (!data.relationship.groups.contains(key)) {
			Group group = data.relationship.new Group();
			data.relationship.groups.add(key);
			data.relationship.groupsMap.put(key, group);
			data.relationship.isModified = true;
			thisView.status = Status.JOINEDGROUP;
			// thisView.fillData();
			thisView.rightTopButton.setText(thisActivity.getString(R.string.business_chat_room));
			HttpUtils httpUtils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("phone", data.userInformation.currentUser.phone);
			params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
			params.addBodyParameter("gid", key);
			params.addBodyParameter("members", "[\"" + data.userInformation.currentUser.phone + "\"]");
			ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
			httpUtils.send(HttpMethod.POST, API.GROUP_ADDMEMBERS, params, responseHandlers.group_addmembers);
		} else {
			Alert.showMessage("已申请加入该房间");
		}
	}

	public void uploadAlias(String alias) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("target", key);
		params.addBodyParameter("alias", alias);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.RELATION_MODIFYALIAS, params, responseHandlers.relation_modifyAlias);
	}
}
