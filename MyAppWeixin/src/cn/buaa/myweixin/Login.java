package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.AccountManager;
import cn.buaa.myweixin.api.CommunityManager;
import cn.buaa.myweixin.apiimpl.AccountManagerImpl;
import cn.buaa.myweixin.apiimpl.CommunityManagerImpl;
import cn.buaa.myweixin.apiutils.Account;
import cn.buaa.myweixin.apiutils.MCTools;

public class Login extends Activity {
	public static Login instance = null;

	private EditText mUser; // ’ ∫≈±‡º≠øÚ
	private EditText mPassword; // √‹¬Î±‡º≠øÚ

	private AccountManager accountManager;
	private CommunityManager communityManager;

	private String accessKey;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mUser = (EditText) findViewById(R.id.login_user_edit);
		mPassword = (EditText) findViewById(R.id.tv_password);
		instance = this;
	}

	public void login_mainweixin(View v) {

		if ("".equals(mUser.getText().toString())
				|| "".equals(mPassword.getText().toString())) {
			new AlertDialog.Builder(Login.this)
					.setIcon(
							getResources().getDrawable(
									R.drawable.login_error_icon))
					.setTitle("µ«¬º¥ÌŒÛ").setMessage("Œ¢–≈’ ∫≈ªÚ’ﬂ√‹¬Î≤ªƒ‹Œ™ø’£¨\n«Î ‰»Î∫Û‘Ÿµ«¬º£°")
					.create().show();
			return;
		}

		accessKey = MCTools.createAccessKey();

		Map<String, String> map = new HashMap<String, String>();
		map.put("phone", mUser.getText().toString());
		map.put("password", String.valueOf(mPassword.getText().toString()));
		map.put("accessKey", accessKey);

		accountManager = new AccountManagerImpl(this);

		accountManager.auth(map, new MCResponseAdapter(this) {
			@Override
			public void success(JSONObject data) {
				try {
					JSONObject jaccount = data.getJSONObject("account");
					Account account = new Account(jaccount);
					account.setAccessKey(accessKey);
					MCTools.saveAccount(Login.this, account);
					Intent intent = new Intent();
					intent.setClass(Login.this, LoadingActivity.class);
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void login_back(View v) { // ±ÍÃ‚¿∏ ∑µªÿ∞¥≈•
		this.finish();
	}

	public void login_pw(View v) { // Õ¸º«√‹¬Î∞¥≈•
		Uri uri = Uri.parse("http://3g.qq.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
		// Intent intent = new Intent();
		// intent.setClass(Login.this,Whatsnew.class);
		// startActivity(intent);
	}
}
