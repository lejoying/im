package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.AccountManager;
import cn.buaa.myweixin.apiimpl.AccountManagerImpl;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	public static RegisterActivity instance = null;
	private boolean isAgreeProvision;

	private ImageView iv_agreeprovision;

	private String registerNumber;
	private EditText register_number_edit;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_mobile);
		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		initView();

	}

	public void initView() {
		instance = this;
		isAgreeProvision = true;
		iv_agreeprovision = (ImageView) findViewById(R.id.iv_agreeprovision);
		register_number_edit = (EditText) findViewById(R.id.register_number_edit);
	}

	// 点击下一步
	public void registerMobileNext(View v) {
		registerNumber = register_number_edit.getText().toString();
		if (isAgreeProvision) {

			String number = register_number_edit.getText().toString();
			if (number == null || number.equals("")) {
				Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				return;
			}

			Map<String, String> map = new HashMap<String, String>();
			map.put("phone", String.valueOf(registerNumber));

			AccountManager accountManager = new AccountManagerImpl(this);

			accountManager.verifyphone(map, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					Intent intent = new Intent(RegisterActivity.this,
							RegisterCheckingActivity.class);
					Bundle bundle = new Bundle();
					try {
						String number = data.getString("phone");
						if (number.equals(registerNumber)) {
							bundle.putString("number", registerNumber);
							intent.putExtras(bundle);
							RegisterActivity.this.startActivity(intent);

						} else {
							Toast.makeText(RegisterActivity.this, "出现异常",
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			new AlertDialog.Builder(RegisterActivity.this)
					.setIcon(
							getResources().getDrawable(
									R.drawable.login_error_icon))
					.setTitle("同意条款").setMessage("请同意使用条款和隐私政策！").create()
					.show();
		}

	}

	// 同意条款
	public void agreeProvision(View v) {
		if (isAgreeProvision) {
			iv_agreeprovision.setImageResource(R.drawable.reg_checkbox_normal);
			isAgreeProvision = false;
		} else {
			iv_agreeprovision.setImageResource(R.drawable.reg_checkbox_checked);
			isAgreeProvision = true;
		}
	}

	// 返回按钮
	public void register_back(View v) {
		finish();
	}
}
