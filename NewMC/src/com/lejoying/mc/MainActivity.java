package com.lejoying.mc;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.data.App;
import com.lejoying.mc.api.API;
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

	// private void getUser() {
	// Bundle params = new Bundle();
	// params.putString("phone", MCDataTools.data.user.phone);
	// params.putString("accessKey", MCDataTools.data.user.accessKey);
	// params.putString("target", MCDataTools.data.user.phone);
	// startNetwork(API.ACCOUNT_GET, params, false,
	// new NetworkStatusListener() {
	// @Override
	// public void onReceive(int STATUS, String log) {
	// switch (STATUS) {
	// case MainServiceHandler.STATUS_NETWORK_SUCCESS:
	// getCirclesAndFriends();
	// break;
	// case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
	// Intent intent = new Intent(MainActivity.this,
	// LoginActivity.class);
	// startActivity(intent);
	// MainActivity.this.finish();
	// break;
	// case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
	// MCNetTools.showMsg(MainActivity.this,
	// getString(R.string.app_nointernet));
	// break;
	// case MainServiceHandler.STATUS_NETWORK_FAILED:
	// getUser();
	// break;
	//
	// default:
	// break;
	// }
	// }
	// });
	// }
	//
	// private void getCirclesAndFriends() {
	// Bundle params = new Bundle();
	// params.putString("phone", MCDataTools.data.user.phone);
	// params.putString("accessKey", MCDataTools.data.user.accessKey);
	// startNetwork(API.RELATION_GETCIRCLESANDFRIENDS, params, false,
	// new NetworkStatusListener() {
	// @Override
	// public void onReceive(int STATUS, String log) {
	// switch (STATUS) {
	// case MainServiceHandler.STATUS_NETWORK_SUCCESS:
	// getMessages();
	// break;
	// case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
	// Intent intent = new Intent(MainActivity.this,
	// LoginActivity.class);
	// startActivity(intent);
	// MainActivity.this.finish();
	// break;
	// case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
	// MCNetTools.showMsg(MainActivity.this,
	// getString(R.string.app_nointernet));
	// break;
	// case MainServiceHandler.STATUS_NETWORK_FAILED:
	// getCirclesAndFriends();
	// break;
	//
	// default:
	// break;
	// }
	// }
	// });
	//
	// }
	//
	// private void getMessages() {
	// Bundle params = new Bundle();
	// params.putString("phone", MCDataTools.data.user.phone);
	// params.putString("accessKey", MCDataTools.data.user.accessKey);
	// // String flag = MCDataTools.data.user.flag;
	// params.putString("flag", "0");
	// startNetwork(API.MESSAGE_GET, params, false,
	// new NetworkStatusListener() {
	// @Override
	// public void onReceive(int STATUS, String log) {
	// switch (STATUS) {
	// case MainServiceHandler.STATUS_NETWORK_SUCCESS:
	// System.out.println(MCDataTools.data.user.nickName);
	// System.out.println(MCDataTools.data.friends.size()
	// + ":::::::::::::::");
	// break;
	// case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
	// Intent intent = new Intent(MainActivity.this,
	// LoginActivity.class);
	// startActivity(intent);
	// MainActivity.this.finish();
	// break;
	// case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
	// MCNetTools.showMsg(MainActivity.this,
	// getString(R.string.app_nointernet));
	// break;
	// case MainServiceHandler.STATUS_NETWORK_FAILED:
	// getMessages();
	// break;
	// default:
	// break;
	// }
	// }
	// });
	// }
}
