package com.open.welinks.model;

import java.util.ArrayList;

import android.view.View;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.view.ShareSectionView;
import com.open.welinks.view.ShareSectionView.SharesMessageBody;

public class TaskContainer_Share {
	public String tag = "TaskContainer_Share";
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public Gson gson = new Gson();
	public MyLog log = new MyLog(tag, true);

	public Task praise = new Task() {

		public String content;
		public ShareSectionView thisView;
		public View view;
		public boolean option = false;;
		public String gid;
		public String gsid;

		public ShareMessage shareMessage;

		public void modifyData() {// 主UI线程
			parser.check();
			User currentUser = data.userInformation.currentUser;
			Share share = data.shares.shareMap.get(data.localStatus.localData.currentSelectedGroup);
			shareMessage = share.shareMessagesMap.get(content);
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
			} else {
				ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < shareMessage.praiseusers.size(); i++) {
					if (shareMessage.praiseusers.get(i).equals(currentUser.phone)) {
						list.add(shareMessage.praiseusers.get(i));
					}
				}
				shareMessage.praiseusers.removeAll(list);
			}
		}

		public void modifyView() {// 主UI线程
			SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get("message#" + shareMessage.gsid);
			sharesMessageBody.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
			sharesMessageBody.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
			view.setTag(R.id.time, null);
		}

		@Override
		public void sendRequest() {
			RequestParams params = new RequestParams();
			HttpUtils httpUtils = new HttpUtils();
			params.addBodyParameter("phone", data.userInformation.currentUser.phone);
			params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
			params.addBodyParameter("gid", gid);
			params.addBodyParameter("gsid", gsid);
			params.addBodyParameter("option", option + "");

//			httpUtils.send(HttpMethod.POST, API.SHARE_ADDPRAISE, params, responseHandlers.share_modifyPraiseusersCallBack);

		}
		
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		@Override
		public void onResponceReceived(ResponseInfo<String> responseInfo) {
			parser.check();
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("点赞群分享成功")) {
				log.e(tag, "---------------------点赞操作成功");
			} else if (response.提示信息.equals("点赞群分享失败")) {
				log.e(tag, "点赞操作失败---------------------" + response.失败原因);
			}
		}

	};
	
	public class GetShares extends Task{
		
		public int nowpage = 0;
		public int pagesize = 10;
		@Override
		public void sendRequest() {
			RequestParams params = new RequestParams();
			HttpUtils httpUtils = new HttpUtils();
			params.addBodyParameter("phone", data.userInformation.currentUser.phone);
			params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
			params.addBodyParameter("gid", data.localStatus.localData.currentSelectedGroup);
			params.addBodyParameter("nowpage", nowpage + "");
			params.addBodyParameter("pagesize", pagesize + "");

			//httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack2);
		
		}

		@Override
		public void onResponceReceived(ResponseInfo<String> responseInfo) {
			
		}
		
		public void updateData() {//主UI线程

		}

		public void updateView() {//主UI线程
		}
	}

}
