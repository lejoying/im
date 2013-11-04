package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.AccountManager;
import cn.buaa.myweixin.apiimpl.AccountManagerImpl;
import cn.buaa.myweixin.apiutils.MCTools;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class SearchFriendActivity extends Activity {

	private AccountManager accountManager;
	private TextView tv_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriends);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		initView();
	}

	public void initView() {
		tv_phone = (TextView) findViewById(R.id.tv_phone);
	}

	public void searchFriend(View v) {
		accountManager = new AccountManagerImpl(this);
		Map<String, String> param = new HashMap<String, String>();
		param.put("phone", tv_phone.getText().toString());
		param.put("accessKey", MCTools.getLoginedAccount(this).getAccessKey());
		accountManager.getaccount(param, new MCResponseAdapter(this) {
			@Override
			public void success(JSONObject data) {
				try {
					JSONObject jaccount = data.getJSONObject("account");
					String nickName = jaccount.getString("nickName");
					String mainBusiness = jaccount.getString("mainBusiness");
					Intent intent = new Intent(SearchFriendActivity.this,
							FriendCallingCardActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("phone", tv_phone.getText().toString());
					bundle.putString("nickName", nickName);
					bundle.putString("mainBusiness", mainBusiness);
					bundle.putString("friendStatus", "0");
					intent.putExtras(bundle);
					SearchFriendActivity.this.startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void unsuccess(JSONObject data) {
				super.unsuccess(data);
			}
		});
	}

	public void back(View v) {
		finish();
	}
}
