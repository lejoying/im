package com.lejoying.mc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lejoying.mc.api.API;
import com.lejoying.mc.fragment.MessageFragment;
import com.lejoying.mc.listener.NetworkStatusListener;
import com.lejoying.mc.service.NetworkService;
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
		return new MessageFragment();
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
						case NetworkService.STATUS_SUCCESS:
							getCirclesAndFriends();
							getMessages();
							break;
						case NetworkService.STATUS_UNSUCCESS:
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case NetworkService.STATUS_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case NetworkService.STATUS_FAILED:
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
						case NetworkService.STATUS_SUCCESS:

							break;
						case NetworkService.STATUS_UNSUCCESS:
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case NetworkService.STATUS_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case NetworkService.STATUS_FAILED:
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
		params.putInt("flag", 0);
		startNetwork(API.MESSAGE_GET, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case NetworkService.STATUS_SUCCESS:
							startDataProcessing(
									NetworkService.WHAT_MESSAGELIST, false);
							break;
						case NetworkService.STATUS_UNSUCCESS:
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case NetworkService.STATUS_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case NetworkService.STATUS_FAILED:
							getMessages();
							break;

						default:
							break;
						}
					}
				});

	}
}
