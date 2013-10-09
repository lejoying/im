package cn.buaa.myweixin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.utils.HttpTools;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterSetPassActivity extends Activity {

	private final int REGISTER_NEXT = 0x22;
	private String registerNumber;
	private TextView tv_password;
	private Handler handler;
	
	private String password;

	private byte[] data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_setpass);
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
					Intent intent = new Intent(RegisterSetPassActivity.this,
							CCommunityActivity.class);
					Bundle bundle = new Bundle();
					try {
						JSONObject jo = new JSONObject(new String(data));
						String info = jo.getString("提示消息");
						if (info.equals("注册成功")) {							
								bundle.putString("number", registerNumber);
								intent.putExtras(bundle);
								RegisterSetPassActivity.this.startActivity(intent);

						} else {
							String err = jo.getString("失败原因");
							Toast.makeText(RegisterSetPassActivity.this, err,
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						Toast.makeText(RegisterSetPassActivity.this, "出现异常",
								Toast.LENGTH_SHORT).show();
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
		Bundle bundle = getIntent().getExtras();
		registerNumber = bundle.getString("number");

		tv_password = (TextView) findViewById(R.id.tv_setpass);
	}

	public void registerCheckingNext(View v) {
		boolean hasNetwork = HttpTools.hasNetwork(this);
		if (!hasNetwork)
			Toast.makeText(RegisterSetPassActivity.this, "无网络连接",
					Toast.LENGTH_SHORT).show();
		else {
			password = tv_password.getText().toString();
			if (password == null || password.equals("")) {
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				return;
			}
			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();

					Map<String, String> map = new HashMap<String, String>();
					map.put("password", String.valueOf(password));
					map.put("phone", String.valueOf(registerNumber));
					try {
						data = HttpTools
								.sendPost(
										"http://apisum.com/api2/account/verifypass",
										map);
						handler.sendEmptyMessage(REGISTER_NEXT);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	// 返回按钮
	public void register_back(View v) {
		finish();
	}

}
