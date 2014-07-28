package com.lejoying.wxgs.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class AddFriendActivity extends Activity implements OnClickListener {

	MainApplication app = MainApplication.getMainApplication();
	// MainModeManager mMainModeManager;

	public Friend mAddFriend;

	private EditText mView_message;
	private View mView_send;
	LinearLayout backView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_addfriend);
		Friend friend = (Friend) getIntent().getSerializableExtra("user");
		if (friend == null) {
			return;
		}
		mAddFriend = friend;
		backView = (LinearLayout) findViewById(R.id.ll_backview);
		mView_message = (EditText) findViewById(R.id.et_message);
		mView_send = findViewById(R.id.btn_send);
		mView_send.setOnClickListener(this);
		backView.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_backview:
			finish();
			break;
		case R.id.btn_send:
			mAddFriend.addMessage = mView_message.getText().toString();
			mAddFriend.temp = true;
			final NetConnection addConnection = new CommonNetConnection() {
				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.RELATION_ADDFRIEND;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put("phoneto", mAddFriend.phone);
					params.put("message", mView_message.getText().toString());
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					mAddFriend.addMessage = mView_message.getText().toString();
					while (app.data.newFriends.contains(mAddFriend)) {
						app.data.newFriends.remove(mAddFriend);
					}
					app.data.newFriends.add(0, mAddFriend);
					// mMainModeManager
					// .showNext(mMainModeManager.mNewFriendsFragment);
					// mMainModeManager.clearBackStack(mMainModeManager.mBackStack
					// .size() - 2);
					finish();
					Alert.showMessage("请求加为好友成功");
					Alert.removeLoading();
				}

				@Override
				protected void failed(int failedType) {
					Alert.removeLoading();
					super.failed(failedType);
				}

				@Override
				protected void unSuccess(JSONObject jData) {
					Alert.removeLoading();
					super.unSuccess(jData);
				}
			};
			Alert.showLoading(new OnLoadingCancelListener() {
				@Override
				public void loadingCancel() {
					addConnection.disConnection();
				}
			});
			app.networkHandler.connection(addConnection);
		}
	}
}
