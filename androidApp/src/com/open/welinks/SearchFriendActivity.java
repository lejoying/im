package com.open.welinks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welink.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.ViewManage;

public class SearchFriendActivity extends Activity {

	public Data data = Data.getInstance();
	public ViewManage viewManage = ViewManage.getInstance();
	public Handler handler;

	public Button scanBusinessCardButton, searchButton;
	public EditText phoneTextView;
	public TextView errorTextView;
	public RelativeLayout backView;
	public TextView nameView;

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
				if (view.equals(searchButton)) {
					String phone = phoneTextView.getText().toString().trim();
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
				} else if (view.equals(backView)) {
					finish();
				}
			}

		};
		searchButton.setOnClickListener(mOnClickListener);
		backView.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		scanBusinessCardButton = (Button) findViewById(R.id.scanBusinessCard);
		searchButton = (Button) findViewById(R.id.search);
		phoneTextView = (EditText) findViewById(R.id.phone);
		errorTextView = (TextView) findViewById(R.id.error);
		backView = (RelativeLayout) findViewById(R.id.backView);
		nameView = (TextView) findViewById(R.id.backTitleView);

		nameView.setText("查找好友");
	}

	private void searchFriend(String phone) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
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
		errorTextView.setText(error);
		new Thread() {
			public void run() {
				try {
					sleep(3000);
					handler.post(new Runnable() {

						@Override
						public void run() {
							errorTextView.setText("");
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
}
