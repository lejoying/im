package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class AddFriendFragment extends BaseFragment implements OnClickListener {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	public Friend mAddFriend;

	private View mContent;
	private EditText mView_message;
	private View mView_send;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContent = inflater.inflate(R.layout.f_addfriend, null);
		mView_message = (EditText) mContent.findViewById(R.id.et_message);
		mView_send = mContent.findViewById(R.id.btn_send);
		mView_send.setOnClickListener(this);
		return mContent;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			final Bundle params = new Bundle();
			params.putString("phone", app.data.user.phone);
			params.putString("accessKey", app.data.user.accessKey);
			params.putString("phoneto",
					mMainModeManager.mBusinessCardFragment.mShowFriend.phone);
			params.putString("message", mView_message.getText().toString());
			mAddFriend.addMessage = mView_message.getText().toString();
			final NetConnection addConnection = new CommonNetConnection() {
				@Override
				protected void settings(Settings settings) {
					settings.url = API.RELATION_ADDFRIEND;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put(
							"phoneto",
							mMainModeManager.mBusinessCardFragment.mShowFriend.phone);
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
					mMainModeManager
							.showNext(mMainModeManager.mNewFriendsFragment);
					mMainModeManager.clearBackStack(mMainModeManager.mBackStack
							.size() - 2);
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
