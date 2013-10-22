package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.AccountManager;
import cn.buaa.myweixin.api.CommunityManager;
import cn.buaa.myweixin.apiimpl.AccountManagerImpl;
import cn.buaa.myweixin.apiimpl.CommunityManagerImpl;
import cn.buaa.myweixin.apiutils.Account;
import cn.buaa.myweixin.apiutils.MCTools;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterSetPassActivity extends Activity {

	private String registerNumber;
	private TextView tv_password;

	private String password;

	private AccountManager accountManager;
	private CommunityManager communityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_setpass);
		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		initView();
	}

	public void initView() {
		Bundle bundle = getIntent().getExtras();
		registerNumber = bundle.getString("number");

		tv_password = (TextView) findViewById(R.id.tv_setpass);
	}

	public void registerCheckingNext(View v) {

		password = tv_password.getText().toString();
		if (password == null || password.equals("")) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("password", String.valueOf(password));
		map.put("phone", String.valueOf(registerNumber));

		accountManager = new AccountManagerImpl(this);
		communityManager = new CommunityManagerImpl(this);

		accountManager.verifypass(map, new MCResponseAdapter(this) {

			@Override
			public void success(JSONObject data) {
				final Intent intent = new Intent(RegisterSetPassActivity.this,
						CCommunityActivity.class);
				final Bundle bundle = new Bundle();
				try {
					JSONObject jaccount = data.getJSONObject("account");

					Account account = new Account(jaccount);

					MCTools.saveAccount(RegisterSetPassActivity.this, account);

					communityManager.find(
							MCTools.getLocationParam(RegisterSetPassActivity.this),
							new MCResponseAdapter(RegisterSetPassActivity.this) {
								@Override
								public void success(JSONObject data) {
									try {

										JSONObject community = data
												.getJSONObject("community");
										bundle.putString("nowcommunity",
												community.toString());
										intent.putExtras(bundle);
										RegisterSetPassActivity.this
												.startActivity(intent);
										RegisterSetPassActivity.this.finish();
										RegisterActivity.instance.finish();
										RegisterCheckingActivity.instance
												.finish();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

								@Override
								public void unsuccess(JSONObject data) {
									try {

										JSONObject community = data
												.getJSONObject("community");
										bundle.putString("nowcommunity",
												community.toString());
										intent.putExtras(bundle);
										RegisterSetPassActivity.this
												.startActivity(intent);
										RegisterSetPassActivity.this.finish();
										RegisterActivity.instance.finish();
										RegisterCheckingActivity.instance
												.finish();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

							});
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	// 返回按钮
	public void register_back(View v) {
		finish();
	}

}
