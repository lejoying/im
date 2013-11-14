package com.lejoying.mc;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.adapter.MCResponseAdapter;
import com.lejoying.api.AccountManager;
import com.lejoying.apiimpl.AccountManagerImpl;
import com.lejoying.mcutils.MCTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private final int STATE_PLOGIN = 0x001;
	private final int STATE_CLOGIN = 0x002;
	private int state;

	public static LoginActivity instance;

	private RelativeLayout rl_panel_plogin;
	private EditText et_plogin_phone;
	private EditText et_plogin_pass;
	private RelativeLayout rl_panel_clogin;
	private EditText et_clogin_phone;
	private EditText et_clogin_code;
	private TextView tv_sendcode;

	private Handler handler;
	private int resendTime;
	private boolean canSend;

	private AccountManager accountManager;

	private String phone;
	private String pass;
	private String code;
	private String accessKey;

	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		instance = this;
		initView();
	}

	public void initView() {
		state = STATE_PLOGIN;
		rl_panel_plogin = (RelativeLayout) findViewById(R.id.rl_panel_plogin);
		et_plogin_phone = (EditText) findViewById(R.id.et_plogin_phone);
		et_plogin_pass = (EditText) findViewById(R.id.et_plogin_pass);
		rl_panel_clogin = (RelativeLayout) findViewById(R.id.rl_panel_clogin);
		et_clogin_phone = (EditText) findViewById(R.id.et_clogin_phone);
		et_clogin_code = (EditText) findViewById(R.id.et_clogin_code);
		tv_sendcode = (TextView) findViewById(R.id.tv_sendcode);

		accountManager = new AccountManagerImpl(this);

		canSend = true;
		handler = MCTools.handler;
	}

	public void login(View v) {
		Map<String, String> param = new HashMap<String, String>();
		if (state == STATE_PLOGIN) {
			phone = et_plogin_phone.getText().toString();
			pass = et_plogin_pass.getText().toString();
			param.put("phone", phone);
			param.put("password", pass);
			accountManager.auth(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					try {
						accessKey = data.getString("accessKey");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intent = new Intent(LoginActivity.this,
							MessagesActivity.class);
					startActivity(intent);
					finish();
				}

			});
		} else if (state == STATE_CLOGIN) {
			phone = et_clogin_phone.getText().toString();
			code = et_clogin_code.getText().toString();
			param.put("phone", phone);
			param.put("code", code);
			accountManager.verifycode(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					try {
						accessKey = data.getString("accessKey");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intent = new Intent(LoginActivity.this,
							MessagesActivity.class);
					startActivity(intent);
					state = STATE_PLOGIN;
					finish();
				}

			});
		}
	}

	public void register(View v) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	public void sendCode(View v) {
		if (canSend) {
			phone = et_clogin_phone.getText().toString();
			canSend = false;
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone", phone);
			param.put("usage", "login");
			accountManager.verifyphone(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					System.out.println(data);
					resend();
				}

			});
		}
	}

	public void resend() {
		resendTime = 60;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (resendTime == 0) {
							tv_sendcode.setText("重新发送");
							timer.cancel();
							canSend = true;
						} else {
							tv_sendcode.setText("重新发送(" + resendTime + ")");
						}
						resendTime--;
					}
				});
			}
		}, 200, 1000);
	}

	public void cLogin(View v) {
		et_clogin_phone.setText(et_plogin_phone.getText().toString());
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.tran_out_top);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				rl_panel_plogin.setVisibility(View.GONE);
			}
		});
		rl_panel_plogin.startAnimation(animation);

		rl_panel_clogin.setVisibility(View.VISIBLE);
		Animation animation2 = AnimationUtils.loadAnimation(this,
				R.anim.tran_in_bottom);
		rl_panel_clogin.startAnimation(animation2);
		state = STATE_CLOGIN;
	}

	@Override
	public void finish() {
		if (state == STATE_CLOGIN) {
			et_plogin_phone.setText(et_clogin_phone.getText().toString());
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.tran_out_bottom);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					rl_panel_clogin.setVisibility(View.GONE);
				}
			});
			rl_panel_clogin.startAnimation(animation);

			rl_panel_plogin.setVisibility(View.VISIBLE);
			Animation animation2 = AnimationUtils.loadAnimation(this,
					R.anim.tran_in_top);
			rl_panel_plogin.startAnimation(animation2);
			state = STATE_PLOGIN;

		} else if (state == STATE_PLOGIN) {
			super.finish();
		}
	}

}
