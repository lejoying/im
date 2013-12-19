package com.lejoying.mc;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;

public class MainActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
		getUser();
	}

	@Override
	public Fragment setFirstPreview() {
		return new FriendsFragment();
	}

	@Override
	protected int setBackground() {
		// TODO Auto-generated method stub
		return R.drawable.card_background;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MCDataTools.saveData(this);
	}

	private void getUser() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		params.putString("target", app.data.user.phone);

		MCNetTools.ajax(this, API.ACCOUNT_GET, params, MCHttpTools.SEND_POST,
				5000, new ResponseListener() {

					@Override
					public void success(JSONObject data) {
						try {
							MCDataTools.updateUser(data
									.getJSONObject("account"));
							getCirclesAndFriends();
						} catch (JSONException e) {
						}
					}

					@Override
					public void noInternet() {
						// TODO Auto-generated method stub
					}

					@Override
					public void failed() {
						// TODO Auto-generated method stub
					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void getCirclesAndFriends() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		MCNetTools.ajax(this, API.RELATION_GETCIRCLESANDFRIENDS, params,
				MCHttpTools.SEND_POST, 5000, new ResponseListener() {
					@Override
					public void success(JSONObject data) {
						try {
							MCDataTools.saveCircles(data
									.getJSONArray("circles"));
							getMessages();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void noInternet() {
						// TODO Auto-generated method stub

					}

					@Override
					public void failed() {
						// TODO Auto-generated method stub

					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void getMessages() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		// String flag = app.data.user.flag;
		params.putString("flag", "0");
		MCNetTools.ajax(this, API.MESSAGE_GET, params, MCHttpTools.SEND_POST,
				5000, new ResponseListener() {

					@Override
					public void success(JSONObject data) {
						try {
							MCDataTools.saveMessages(data
									.getJSONArray("messages"));
							app.data.user.flag = String.valueOf(data
									.getInt("flag"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void noInternet() {
						// TODO Auto-generated method stub

					}

					@Override
					public void failed() {
						// TODO Auto-generated method stub

					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO Auto-generated method stub

					}
				});
	}
}
