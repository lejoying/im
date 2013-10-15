package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.utils.MCTools;
import cn.buaa.myweixin.utils.MCNowUser;
import cn.buaa.myweixin.utils.MCTools.HttpStatusListener;
import android.app.Activity;
import android.app.AlertDialog;
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

//		MCTools.postForJSON(this,
//				"/api2/account/verifypass", map, true,
//				true, new HttpStatusListener() {
//
//					@Override
//					public void shortIntervalTime() {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void noInternet() {
//						new AlertDialog.Builder(RegisterSetPassActivity.this)
//								.setIcon(
//										getResources().getDrawable(
//												R.drawable.login_error_icon))
//								.setTitle("网络错误")
//								.setMessage("无网络连接,请连接网络后\n重试！").create()
//								.show();
//					}
//
//					@Override
//					public void getJSONSuccess(JSONObject data) {
//						Intent intent = new Intent(
//								RegisterSetPassActivity.this,
//								CCommunityActivity.class);
//						Bundle bundle = new Bundle();
//						try {
//							String info = data.getString("提示信息");
//							if (info.equals("注册成功")) {
//								JSONObject account = data
//										.getJSONObject("account");
//
//								MCNowUser.setNowUser(
//										account.getString("phone"),
//										account.getString("head"),
//										account.getString("nickName"),
//										account.getString("mainBusiness"),
//										account.getString("status"));
//								JSONObject community = data
//										.getJSONObject("nowcommunity");
//								bundle.putString("nowcommunity",
//										community.toString());
//								intent.putExtras(bundle);
//								RegisterSetPassActivity.this
//										.startActivity(intent);
//								finish();
//								RegisterActivity.instance.finish();
//								RegisterCheckingActivity.instance.finish();
//
//							} else {
//								String err = data.getString("失败原因");
//								new AlertDialog.Builder(
//										RegisterSetPassActivity.this)
//										.setIcon(
//												getResources()
//														.getDrawable(
//																R.drawable.login_error_icon))
//										.setTitle("出错了").setMessage(err)
//										.create().show();
//							}
//						} catch (JSONException e) {
//							Toast.makeText(RegisterSetPassActivity.this,
//									"出现异常", Toast.LENGTH_SHORT).show();
//							e.printStackTrace();
//						}
//					}
//				});
	}

	// 返回按钮
	public void register_back(View v) {
		finish();
	}

}
