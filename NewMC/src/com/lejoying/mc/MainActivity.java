package com.lejoying.mc;

import com.lejoying.mc.api.API;
import com.lejoying.mc.fragment.MessageFragment;
import com.lejoying.mc.utils.MCDataTools;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MainActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
		Bundle params = new Bundle();
		params.putString("phone", MCDataTools.getLoginedUser(this).getPhone());
		params.putString("accessKey", MCDataTools.getLoginedUser(this)
				.getAccessKey());
		startNetworkForResult(API.RELATION_GETCIRCLESANDFRIENDS, params,
				new ReceiverListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						// TODO Auto-generated method stub

					}
				});
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

}
