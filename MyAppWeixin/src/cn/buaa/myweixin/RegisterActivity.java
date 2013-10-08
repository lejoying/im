package cn.buaa.myweixin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.utils.HttpTools;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private final int REGISTER_NEXT = 0x22;

	private boolean isAgreeProvision;

	private ImageView iv_agreeprovision;

	private Handler handler;
	private byte[] data;

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
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				int what = msg.what;
				switch (what) {
				case REGISTER_NEXT:
					Intent intent = new Intent(RegisterActivity.this,
							RegisterCheckingActivity.class);
					Bundle bundle = new Bundle();
					try {
						JSONObject jo = new JSONObject(new String(data));
						String info = jo.getString("提示消息");
						String number = jo.getString("phone");
						if (info.equals("手机号验证成功")) {
							if (number.equals(registerNumber)) {
								bundle.putString("number", registerNumber);
								intent.putExtras(bundle);
								RegisterActivity.this.startActivity(intent);

							} else {
								Toast.makeText(RegisterActivity.this, "出现异常",
										Toast.LENGTH_SHORT).show();

							}
						} else {
							String err = jo.getString("失败原因");
							Toast.makeText(RegisterActivity.this, err,
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		};
	}

	public void initView() {
		isAgreeProvision = true;
		iv_agreeprovision = (ImageView) findViewById(R.id.iv_agreeprovision);
		register_number_edit = (EditText) findViewById(R.id.register_number_edit);
	}

	// 点击下一步
	public void registerMobileNext(View v) {
		if (isAgreeProvision) {
			boolean hasNetwork = HttpTools.hasNetwork(this);
			if (!hasNetwork)
				Toast.makeText(RegisterActivity.this, "无网络连接",
						Toast.LENGTH_SHORT).show();
			else {
				String number = register_number_edit.getText().toString();
				if (number == null || number.equals("")) {
					Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				registerNumber = number;
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();

						Map<String, String> map = new HashMap<String, String>();
						map.put("phone", String.valueOf(registerNumber));
						try {
							data = HttpTools
									.sendPost(
											"http://192.168.3.252:8071/api2/account/verifyphone",
											map);
							handler.sendEmptyMessage(REGISTER_NEXT);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
		} else {
			Toast.makeText(this, "请同意使用条款和隐私政策", Toast.LENGTH_SHORT).show();
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
