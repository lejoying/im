package com.lejoying.circlesshow;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lejoying.utils.CommonNetConnection;
import com.lejoying.utils.NetworkHandler;
import com.lejoying.utils.NetworkHandler.Settings;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.UserInformation;

public class LaunchActivity extends Activity implements OnClickListener {

	String TAG = "LaunchActivity";

	NetworkHandler mNetworkHandler;
	Handler handler = new Handler();
	String url_userInfomation = "http://192.168.1.21/api2/account/getuserinfomation";
	String url_intimateFriends = "http://www.we-links.com/api2/relation/intimatefriends";

	public static Data data = Data.getInstance();
	Gson gson = new Gson();

	EditText showPhoneView;
	RelativeLayout getDataView;

	LinearLayout infomationContent;

	boolean isInit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		mNetworkHandler = new NetworkHandler(5);
		initViews();
		initEvent();
	}

	private void initEvent() {
		getDataView.setOnClickListener(this);
	}

	private void initViews() {
		showPhoneView = (EditText) findViewById(R.id.et_showPhone);
		getDataView = (RelativeLayout) findViewById(R.id.rl_getData);
		infomationContent = (LinearLayout) findViewById(R.id.infomationContent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.rl_getData) {
			String showPhone = showPhoneView.getText().toString().trim();
			if ("".equals(showPhone)) {
				Toast.makeText(LaunchActivity.this, "帐号不能为空",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// getIntimateFriendsData(showPhone);
			getUserInfomationData(showPhone);
		}
	}

	@Override
	protected void onResume() {
		infomationContent.removeAllViews();
		super.onResume();
	}

	void generateTextView(final String message) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				TextView textView = new TextView(LaunchActivity.this);
				textView.setText(message);
				infomationContent.addView(textView);
			}
		});
	}

	void getUserInfomationData(final String phone) {
		mNetworkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				generateTextView("获取个人信息成功...");
				try {
					data.userInformation = gson.fromJson(
							jData.getString("data"), UserInformation.class);
					getIntimateFriendsData(phone);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void settings(Settings settings) {
				generateTextView("正在获取个人信息...");
				settings.url = url_userInfomation;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", phone);
				params.put("accessKey", "lejoying");
				settings.params = params;
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				generateTextView("获取个人信息失败...");
				super.unSuccess(jData);
			}
		});
	}

	void getIntimateFriendsData(final String phone) {
		mNetworkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				generateTextView("获取密友成功...");
				try {
					data.relationship = gson.fromJson(jData.getString("data"),
							Relationship.class);
					isInit = true;
					generateTextView("准备初始化UI...");
					// Thread.currentThread().sleep(1000);
					Intent intent = new Intent(LaunchActivity.this,
							MainActivity.class);
					startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void settings(Settings settings) {
				generateTextView("正在获取密友...");
				settings.url = url_intimateFriends;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", phone);
				params.put("accessKey", "lejoying");
				settings.params = params;
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				generateTextView("获取密友失败...");
				super.unSuccess(jData);
			}
		});
	}
}
