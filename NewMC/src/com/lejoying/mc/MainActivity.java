package com.lejoying.mc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.api.API;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.listener.NetworkStatusListener;
import com.lejoying.mc.service.MainService;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCNetTools;

public class MainActivity extends BaseFragmentActivity {

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

	private void getUser() {
		Bundle params = new Bundle();
		params.putString("phone", MCDataTools.getLoginedUser(this).getPhone());
		params.putString("accessKey", MCDataTools.getLoginedUser(this)
				.getAccessKey());
		params.putString("target", MCDataTools.getLoginedUser(this).getPhone());
		startNetwork(API.ACCOUNT_GET, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainService.STATUS_NETWORK_SUCCESS:
							getCirclesAndFriends();
							getMessages();
							break;
						case MainService.STATUS_NETWORK_UNSUCCESS:
							System.out.println("获取用户失败");
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainService.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainService.STATUS_NETWORK_FAILED:
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
		params.putString("phone", MCDataTools.getLoginedUser(this).getPhone());
		params.putString("accessKey", MCDataTools.getLoginedUser(this)
				.getAccessKey());
		startNetwork(API.RELATION_GETCIRCLESANDFRIENDS, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainService.STATUS_NETWORK_SUCCESS:
							startViewProcessing(MainService.NOTIFY_FRIEND,
									null, false);
							break;
						case MainService.STATUS_NETWORK_UNSUCCESS:
							System.out.println("获取好友圈失败");
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainService.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainService.STATUS_NETWORK_FAILED:
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
		params.putString("phone", MCDataTools.getLoginedUser(this).getPhone());
		params.putString("accessKey", MCDataTools.getLoginedUser(this)
				.getAccessKey());
		String flag = MCDataTools.getLoginedUser(this).getFlag();
		System.out.println(flag);
		params.putString("flag", flag);
		startNetwork(API.MESSAGE_GET, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainService.STATUS_NETWORK_SUCCESS:
							startViewProcessing(MainService.NOTIFY_MESSAGELIST,
									false);
							break;
						case MainService.STATUS_NETWORK_UNSUCCESS:
							System.out.println("获取消息失败");
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainService.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainService.STATUS_NETWORK_FAILED:
							getMessages();
							break;

						default:
							break;
						}
					}
				});

	}
}
