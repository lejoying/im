package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.utils.MCTools;
import cn.buaa.myweixin.utils.MCTools.HttpStatusListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterCheckingActivity extends Activity {

	public static RegisterCheckingActivity instance = null;
	private String registerNumber;
	private TextView tv_registernumber;
	private TextView tv_checkingcode;

	private String checkingcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_checking);
		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		initView();
	}

	public void initView() {
		instance = this;
		Bundle bundle = getIntent().getExtras();
		registerNumber = bundle.getString("number");

		tv_registernumber = (TextView) findViewById(R.id.tv_registernumber);
		tv_registernumber.setText("+86" + registerNumber);

		tv_checkingcode = (TextView) findViewById(R.id.tv_checkingcode);
	}

	public void registerCheckingNext(View v) {

		checkingcode = tv_checkingcode.getText().toString();
		if (checkingcode == null || checkingcode.equals("")) {
			Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
			return;
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("code", String.valueOf(checkingcode));
		map.put("phone", String.valueOf(registerNumber));

		MCTools.postForJSON(this,
				"http://192.168.0.19:8071/api2/account/verifycode", map, true,
				new HttpStatusListener() {

					@Override
					public void shortIntervalTime() {
						// TODO Auto-generated method stub

					}

					@Override
					public void noInternet() {
						new AlertDialog.Builder(RegisterCheckingActivity.this)
						.setIcon(
								getResources()
										.getDrawable(
												R.drawable.login_error_icon))
						.setTitle("网络错误")
						.setMessage("无网络连接,请连接网络后\n重试！").create()
						.show();
					}

					@Override
					public void getJSONSuccess(JSONObject data) {
						Intent intent = new Intent(
								RegisterCheckingActivity.this,
								RegisterSetPassActivity.class);
						Bundle bundle = new Bundle();
						try {
							String info = data.getString("提示信息");
							if (info.equals("验证成功")) {
								String number = data.getString("phone");
								if (number.equals(registerNumber)) {
									bundle.putString("number", registerNumber);
									intent.putExtras(bundle);
									RegisterCheckingActivity.this
											.startActivity(intent);

								} else {
									Toast.makeText(
											RegisterCheckingActivity.this,
											"出现异常", Toast.LENGTH_SHORT).show();
								}
							} else {
								String err = data.getString("失败原因");
								new AlertDialog.Builder(RegisterCheckingActivity.this)
								.setIcon(
										getResources()
												.getDrawable(
														R.drawable.login_error_icon))
								.setTitle("验证失败")
								.setMessage(err).create()
								.show();
							}
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
