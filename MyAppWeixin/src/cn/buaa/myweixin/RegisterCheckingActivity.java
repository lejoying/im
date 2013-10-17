package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.AccountManager;
import cn.buaa.myweixin.apiimpl.AccountManagerImpl;
import android.app.Activity;
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

		AccountManager accountManager = new AccountManagerImpl(this);

		accountManager.verifycode(map, new MCResponseAdapter(this) {
			@Override
			public void success(JSONObject data) {
				Intent intent = new Intent(RegisterCheckingActivity.this,
						RegisterSetPassActivity.class);
				Bundle bundle = new Bundle();
				try {
					String number = data.getString("phone");
					if (number.equals(registerNumber)) {
						bundle.putString("number", registerNumber);
						intent.putExtras(bundle);
						RegisterCheckingActivity.this.startActivity(intent);
					} else {
						Toast.makeText(RegisterCheckingActivity.this, "出现异常",
								Toast.LENGTH_SHORT).show();
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
