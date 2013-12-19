package com.lejoying.mc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.api.API;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.service.handler.MainServiceHandler;
import com.lejoying.mc.service.handler.NetworkHandler.NetworkStatusListener;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCNetTools;

public class MainActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
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
		params.putString("phone", MCDataTools.getLoginUser().phone);
		params.putString("accessKey", MCDataTools.getLoginUser().accessKey);
		params.putString("target", MCDataTools.getLoginUser().phone);
		startNetwork(API.ACCOUNT_GET, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainServiceHandler.STATUS_NETWORK_SUCCESS:
							getCirclesAndFriends();
							break;
						case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainServiceHandler.STATUS_NETWORK_FAILED:
							getUser();
							break;

						default:
							break;
						}
					}
				});
	}

	private void getCirclesAndFriends() {
		Bundle params = new Bundle();
		params.putString("phone", MCDataTools.getLoginUser().phone);
		params.putString("accessKey", MCDataTools.getLoginUser().accessKey);
		startNetwork(API.RELATION_GETCIRCLESANDFRIENDS, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainServiceHandler.STATUS_NETWORK_SUCCESS:
							getMessages();
							break;
						case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainServiceHandler.STATUS_NETWORK_FAILED:
							getCirclesAndFriends();
							break;

						default:
							break;
						}
					}
				});

	}

	private void getMessages() {
		Bundle params = new Bundle();
		params.putString("phone", MCDataTools.getLoginUser().phone);
		params.putString("accessKey", MCDataTools.getLoginUser().accessKey);
		// String flag = MCDataTools.getLoginUser().flag;
		params.putString("flag", "0");
		startNetwork(API.MESSAGE_GET, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainServiceHandler.STATUS_NETWORK_SUCCESS:
							System.out.println(MCDataTools.getLoginUser().nickName);
							System.out.println(MCDataTools.getFriends().size()
									+ ":::::::::::::::");
							break;
						case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainServiceHandler.STATUS_NETWORK_FAILED:
							getMessages();
							break;
						default:
							break;
						}
					}
				});
	}
}
