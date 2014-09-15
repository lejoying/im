package com.open.welinks;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.Alert;
import com.open.welinks.view.ViewManage;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class SearchFriendActivity extends Activity {

	public Data data = Data.getInstance();
	public ViewManage viewManage = ViewManage.getInstance();
	public Handler handler;

	public Button scanBusinessCard, search;
	public EditText phoneText;
	public TextView errorText;
	public RelativeLayout backview;
	public TextView name;

	public OnClickListener mOnClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);
		viewManage.searchFriendActivity = this;
		handler = new Handler();
		initView();
		initListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		viewManage.searchFriendActivity = null;
	}

	private void initListener() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(search)) {
					String phone = phoneText.getText().toString().trim();
					if ("".equals(phone)) {
						showError("请输入手机号");
					} else if (data.userInformation.currentUser.phone.equals(phone)) {
						Intent intent = new Intent(SearchFriendActivity.this, BusinessCardActivity.class);
						intent.putExtra("key", data.userInformation.currentUser.phone);
						intent.putExtra("type", "point");
						intent.putExtra("isTemp", false);
						startActivity(intent);
					} else {
						searchFriend(phone);
					}
				} else if (view.equals(backview)) {
					finish();
				}
			}

		};
		search.setOnClickListener(mOnClickListener);
		backview.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		scanBusinessCard = (Button) findViewById(R.id.scanBusinessCard);
		search = (Button) findViewById(R.id.search);
		phoneText = (EditText) findViewById(R.id.phone);
		errorText = (TextView) findViewById(R.id.error);
		backview = (RelativeLayout) findViewById(R.id.backView);
		name = (TextView) findViewById(R.id.backTitleView);

		name.setText("查找好友");
	}

	private void searchFriend(String phone) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("target", "[\"" + phone + "\"]");
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, responseHandlers.account_get);
	}

	public void searchCallBack(String key, boolean isTemp) {
		if ("".equals(key)) {
			showError("用户不存在");
		} else {
			Intent intent = new Intent(SearchFriendActivity.this, BusinessCardActivity.class);
			intent.putExtra("key", key);
			intent.putExtra("type", "point");
			intent.putExtra("isTemp", isTemp);
			startActivity(intent);
			finish();
		}

	}

	public void showError(String error) {
		errorText.setText(error);
		new Thread() {
			public void run() {
				try {
					sleep(3000);
					handler.post(new Runnable() {

						@Override
						public void run() {
							errorText.setText("");
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
}
