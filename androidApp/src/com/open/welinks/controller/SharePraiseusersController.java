package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.welinks.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.MyGson;
import com.open.welinks.view.SharePraiseusersView;

public class SharePraiseusersController {

	public Data data = Data.getInstance();
	public String tag = "SharePraiseusersController";

	public Context context;
	public SharePraiseusersView thisView;
	public SharePraiseusersController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;

	public ArrayList<String> praiseusersList;

	public String type;

	public SharePraiseusersController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		thisController = this;
	}

	public void onCreate() {
		praiseusersList = data.tempData.praiseusersList;
		data.tempData.praiseusersList = null;
		String type = thisActivity.getIntent().getStringExtra("type");
		if (type != null) {
			this.type = type;
		}
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.getTag(R.id.tag_first) != null) {
					String tagContent = (String) view.getTag(R.id.tag_first);
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("user".equals(type)) {
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent("point", content);
						thisView.businessCardPopView.showUserCardDialogView();
					}
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
	}

	public void onResume() {
		thisView.businessCardPopView.dismissUserCardDialogView();
	}

	public HttpClient httpClient = new HttpClient();
	public MyGson gson = new MyGson();

	public void getUsersData() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("target", gson.toJson(praiseusersList));

		httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public List<Friend> accounts;
			}

			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("获取用户信息成功".equals(response.提示信息)) {
					List<Friend> friends = response.accounts;
					Parser parser = Parser.getInstance();
					parser.check();
					for (int i = 0; i < friends.size(); i++) {
						Friend friend = friends.get(i);
						boolean flag = true;
						if (data.relationship.friends.contains(friend.phone)) {
							Friend friend2 = data.relationship.friendsMap.get(friend.phone);
							if (friend2 != null) {
								flag = false;
								friend2.head = friend.head;
								friend2.nickName = friend.nickName;
								friend2.mainBusiness = friend.mainBusiness;
								friend2.sex = friend.sex;
								friend2.age = Integer.valueOf(friend.age);
								friend2.createTime = friend.createTime;
								friend2.lastLoginTime = friend.lastLoginTime;
								friend2.userBackground = friend.userBackground;
							} else {
								flag = true;
							}
						}
						if (flag) {
							Friend friend2 = data.relationship.new Friend();
							friend2.phone = friend.phone;
							friend2.head = friend.head;
							friend2.nickName = friend.nickName;
							friend2.mainBusiness = friend.mainBusiness;
							friend2.sex = friend.sex;
							friend2.age = Integer.valueOf(friend.age);
							friend2.createTime = friend.createTime;
							friend2.lastLoginTime = friend.lastLoginTime;
							friend2.userBackground = friend.userBackground;
							friend2.id = friend.id;

							data.relationship.friendsMap.put(friend.phone, friend);
						}
					}
					thisView.praiseUsersAdapter.notifyDataSetChanged();
				}
			};
		});
	}
}