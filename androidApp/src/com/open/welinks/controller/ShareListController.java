package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.utils.MyGson;
import com.open.welinks.view.ShareListView;

public class ShareListController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ShareListController";
	public MyLog log = new MyLog(tag, true);

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public Context context;
	public ShareListView thisView;
	public ShareListController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;
	public OnDownloadListener downloadListener;

	public String key = "";
	public Friend friend;
	public User currentUser;

	public boolean isSelf = false;

	public MyGson gson = new MyGson();
	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	public int nowpage = 0;
	public int pagesize = 5;

	public List<String> shares = new ArrayList<String>();
	public Map<String, ShareMessage> sharesMap = new HashMap<String, ShareMessage>();

	public ShareListController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisController = this;
		this.thisActivity = thisActivity;
	}

	public void onCrate() {
		parser.check();
		currentUser = data.userInformation.currentUser;
		key = thisActivity.getIntent().getStringExtra("key");
		if (key.equals(currentUser.phone)) {
			isSelf = true;
		} else {
			friend = data.relationship.friendsMap.get(key);
		}
		getUserShares();
	}

	public boolean loadfinish = true;
	public OnScrollListener mOnScrollListener;
	public OnItemClickListener mOnItemClickListener;

	public int resultCodeDetail = 0x21;

	public void initializeListeners() {
		mOnItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position != 0) {
					ShareMessage message = sharesMap.get(shares.get(position - 1));
					Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
					intent.putExtra("gid", message.gid);
					intent.putExtra("sid", message.sid);
					intent.putExtra("gsid", message.gsid);
					log.e(thisController.gson.toJson(message));
					thisActivity.startActivityForResult(intent, resultCodeDetail);
				}
			}
		};
		mOnScrollListener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastItemid = thisView.listView.getLastVisiblePosition();
				if ((lastItemid + 1) == totalItemCount) {
					if (totalItemCount > 1) {
						if (loadfinish) {
							loadfinish = false;
							getUserShares();
						}
					}
				}
			}
		};
		downloadListener = new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
				((ImageView) instance.view).setBackgroundColor(Color.parseColor("#c0c0c0"));
			}

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
				super.onLoading(instance, precent, status);
				instance.view.setBackgroundColor(Color.parseColor("#c0c0c0"));
			}

			@Override
			public void onLoadingStarted(DownloadFile instance, int precent, int status) {
				super.onLoadingStarted(instance, precent, status);
				instance.view.setBackgroundColor(Color.parseColor("#c0c0c0"));
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.getTag(R.id.tag_class) != null) {
					String tag_class = (String) view.getTag(R.id.tag_class);
					if ("conver_head".equals(tag_class)) {
						Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
						intent.putExtra("key", key);
						intent.putExtra("type", "point");
						thisActivity.startActivity(intent);
					}
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.listView.setOnScrollListener(mOnScrollListener);
		thisView.listView.setOnItemClickListener(mOnItemClickListener);
	}

	public void getUserShares() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", key);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("nowpage", nowpage + "");
		params.addBodyParameter("pagesize", pagesize + "");
		HttpClient httpClient = new HttpClient();
		httpUtils.send(HttpMethod.POST, API.SHARE_GETUSERSHARES, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
				// public String gid;
				public int nowpage;
				public List<String> shares;
				public Map<String, ShareMessage> sharesMap;
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					Response response = gson.fromJson(responseInfo.result, Response.class);
					if (response.提示信息.equals("获取群分享成功")) {
						if (response.nowpage == 0) {
							shares.clear();
							if (response.shares.size() == 0) {
								nowpage = response.nowpage;
							} else {
								nowpage++;
							}
							shares.addAll(response.shares);
						} else {
							if (response.shares.size() == 0) {
								nowpage = response.nowpage;
							} else {
								nowpage++;
							}
							for (int i = 0; i < response.shares.size(); i++) {
								String gsid = response.shares.get(i);
								if (!shares.contains(gsid)) {
									shares.add(gsid);
								}
							}
						}
						if (response.shares.size() != 0) {
							sharesMap.putAll(response.sharesMap);
							taskManageHolder.fileHandler.handler.post(new Runnable() {

								@Override
								public void run() {
									thisView.shareListAdapter.notifyDataSetChanged();
								}
							});
						}
						loadfinish = true;
					} else {
						log.e(response.失败原因);
					}
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == resultCodeDetail && resultCode == Activity.RESULT_OK) {
			String gsid = data2.getStringExtra("key");
			if (gsid != null) {
				shares.remove(gsid);
				thisView.shareListAdapter.notifyDataSetChanged();
			}
		}
	}
}
