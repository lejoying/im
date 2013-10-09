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

public class RegisterCheckingActivity extends Activity {

	private final int REGISTER_NEXT = 0x22;
	private String registerNumber;
	private TextView tv_registernumber;
	private TextView tv_checkingcode;
	private Handler handler;

	private String checkingcode;

	private byte[] data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_checking);
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
					Intent intent = new Intent(RegisterCheckingActivity.this,
							RegisterSetPassActivity.class);
					Bundle bundle = new Bundle();
					try {
						JSONObject jo = new JSONObject(new String(data));
						String info = jo.getString("提示消息");
						String number = jo.getString("phone");
						if (info.equals("验证码正确")) {
							if (number.equals(registerNumber)) {
								bundle.putString("number", registerNumber);
								intent.putExtras(bundle);
								RegisterCheckingActivity.this.startActivity(intent);

							} else {
								Toast.makeText(RegisterCheckingActivity.this, "出现异常",
										Toast.LENGTH_SHORT).show();

							}
						} else {
							String err = jo.getString("失败原因");
							Toast.makeText(RegisterCheckingActivity.this, err,
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						Toast.makeText(RegisterCheckingActivity.this, "出现异常",
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

		tv_registernumber = (TextView) findViewById(R.id.tv_registernumber);
		tv_registernumber.setText("+86" + registerNumber);

		tv_checkingcode = (TextView) findViewById(R.id.tv_checkingcode);
	}

	public void registerCheckingNext(View v) {
		boolean hasNetwork = HttpTools.hasNetwork(this);
		if (!hasNetwork)
			Toast.makeText(RegisterCheckingActivity.this, "无网络连接",
					Toast.LENGTH_SHORT).show();
		else {
			checkingcode = tv_checkingcode.getText().toString();
			if (checkingcode == null || checkingcode.equals("")) {
				Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
				return;
			}
			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();

					Map<String, String> map = new HashMap<String, String>();
					map.put("code", String.valueOf(checkingcode));
					map.put("phone", String.valueOf(registerNumber));
					try {
						data = HttpTools
								.sendPost(
										"http://apisum.com/api2/account/verifycode",
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
