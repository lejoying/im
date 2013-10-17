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
import cn.buaa.myweixin.apiutils.Account;
import cn.buaa.myweixin.apiutils.MCNowUser;
import cn.buaa.myweixin.apiutils.MCTools;
import cn.buaa.myweixin.utils.HttpTools;

public class Login extends Activity {
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mUser = (EditText) findViewById(R.id.login_user_edit);
		mPassword = (EditText) findViewById(R.id.tv_password);
	}

	public void login_mainweixin(View v) {

		if ("".equals(mUser.getText().toString())
				|| "".equals(mPassword.getText().toString())) {
			new AlertDialog.Builder(Login.this)
					.setIcon(
							getResources().getDrawable(
									R.drawable.login_error_icon))
					.setTitle("登录错误").setMessage("微信帐号或者密码不能为空，\n请输入后再登录！")
					.create().show();
			return;
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("phone", mUser.getText().toString());
		map.put("password", String.valueOf(mPassword.getText().toString()));

//		MCTools.sendForJSON(Login.this, "/api2/account/auth", map, true,
//				HttpTools.SEND_POST, new HttpStatusListener() {
//					public boolean noInternet() {
//						return false;
//					}
//
//					public void getJSONSuccess(String api, JSONObject data) {
//						try {
//							String info = data.getString("提示信息");
//							if (info.equals("账号登录成功")) {
//								JSONObject jaccount = data
//										.getJSONObject("account");
//								String status = jaccount.getString("status");
//								Account account = new Account();
//								String phone = jaccount.getString("phone");
//								String nickName = jaccount
//										.getString("nickName");
//								String mainBusiness = jaccount
//										.getString("mainBusiness");
//								String head = jaccount.getString("head");
//								account.setPhone(phone);
//								account.setNickName(nickName);
//								account.setHead(head);
//								account.setMainBusiness(mainBusiness);
//								account.setStatus(status);
//								MCNowUser.setNowUser(account);
//								MCTools.saveAccount(Login.this, account);
//								if (status.equals("success")) {
//									Intent intent = new Intent();
//									intent.setClass(Login.this,
//											LoadingActivity.class);
//									startActivity(intent);
//									finish();
//								}
//								if (status.equals("unjoin")) {
//									MCTools.getLocation(Login.this,
//											new GetCommunityListener() {
//												@Override
//												public void getCommunitySuccess(
//														JSONObject data) {
//													try {
//														Intent intent = new Intent();
//														Bundle bundle = new Bundle();
//														JSONObject community = null;
//
//														community = data
//																.getJSONObject("community");
//
//														bundle.putString(
//																"nowcommunity",
//																community
//																		.toString());
//														intent.putExtras(bundle);
//														intent.setClass(
//																Login.this,
//																CCommunityActivity.class);
//														startActivity(intent);
//														finish();
//													} catch (JSONException e) {
//														// TODO Auto-generated
//														// catch block
//														e.printStackTrace();
//													}
//												}
//											});
//
//								}
//							} else {
//								new AlertDialog.Builder(Login.this)
//										.setIcon(
//												getResources()
//														.getDrawable(
//																R.drawable.login_error_icon))
//										.setTitle("登录失败")
//										.setMessage("微信帐号或者密码不正确，\n请检查后重新输入！")
//										.create().show();
//
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//
//					@Override
//					public void shortIntervalTime() {
//						// TODO Auto-generated method stub
//
//					}
//				});
	}

	public void login_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void login_pw(View v) { // 忘记密码按钮
		Uri uri = Uri.parse("http://3g.qq.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
		// Intent intent = new Intent();
		// intent.setClass(Login.this,Whatsnew.class);
		// startActivity(intent);
	}
}
