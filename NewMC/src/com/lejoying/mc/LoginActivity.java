package com.lejoying.mc;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.lejoying.mc.adapter.CircleMenu;
import com.lejoying.mc.adapter.MCResponseAdapter;
import com.lejoying.mc.api.AccountManager;
import com.lejoying.mc.apiimpl.AccountManagerImpl;
import com.lejoying.mc.utils.MCNetTools;

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

	private boolean finishAll;

	private CircleMenu circleMenu;

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
		handler = MCNetTools.handler;

		circleMenu = new CircleMenu(this);
	}

	public void login(View v) {
		if (et_plogin_phone.getText().toString() == null
				|| et_plogin_phone.getText().toString().equals("")) {
			MCNetTools.showMsg(this, "手机不能为空");
			et_plogin_phone.requestFocus();
			return;
		}
		Map<String, String> param = new HashMap<String, String>();
		if (state == STATE_PLOGIN) {
			if (et_plogin_pass.getText().toString() == null
					|| et_plogin_pass.getText().toString().equals("")) {
				MCNetTools.showMsg(this, "密码不能为空");
				et_plogin_pass.requestFocus();
				return;
			}
			Intent intent = new Intent(this, LoadingActivity.class);
			startActivity(intent);
			phone = et_plogin_phone.getText().toString();
			pass = et_plogin_pass.getText().toString();
			param.put("phone", phone);
			param.put("password", pass);

		} else if (state == STATE_CLOGIN) {
			if (et_clogin_code.getText().toString() == null
					|| et_clogin_code.getText().toString().equals("")) {
				MCNetTools.showMsg(this, "请输入验证码");
				et_clogin_code.requestFocus();
				return;
			}
			Intent intent = new Intent(this, LoadingActivity.class);
			startActivity(intent);
			phone = et_clogin_phone.getText().toString();
			code = et_clogin_code.getText().toString();
			param.put("phone", phone);
			param.put("code", code);
		}
		accountManager.auth(param, new MCResponseAdapter(this) {
			@Override
			public void success(final JSONObject data) {
				super.success(data);

				try {
					accessKey = data.getString("accessKey");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(LoginActivity.this,
						MessagesActivity.class);
				startActivity(intent);
				finishAll();

			}

			@Override
			public void unsuccess(JSONObject data) {
				super.unsuccess(data);
				String err = "未知原因";
				try {
					err = data.getString("失败原因");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MCNetTools.showMsg(LoginActivity.this, err);
			}

		});
	}

	public void register(View v) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	public void sendCode(View v) {
		if (canSend) {
			if (et_clogin_phone.getText().toString() == null
					|| et_clogin_phone.getText().toString().equals("")) {
				MCNetTools.showMsg(this, "手机号不能为空");
				return;
			}
			et_clogin_code.requestFocus();
			phone = et_clogin_phone.getText().toString();
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone", phone);
			param.put("usage", "login");
			accountManager.verifyphone(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					resend(60);
				}

				@Override
				public void noInternet() {
					super.noInternet();
				}

				@Override
				public void unsuccess(JSONObject data) {
					String err = "未知原因";
					try {
						err = data.getString("失败原因");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MCNetTools.showMsg(LoginActivity.this, err);
				}

				@Override
				public void failed() {
					super.failed();
				}

			});
		}
	}

	public void resend(int time) {
		canSend = false;
		resendTime = time;
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
				circleMenu.showMenu(CircleMenu.SHOW_TOP, null, true);
			}
		});
		rl_panel_plogin.startAnimation(animation);

		rl_panel_clogin.setVisibility(View.VISIBLE);
		Animation animation2 = AnimationUtils.loadAnimation(this,
				R.anim.tran_in_bottom);
		rl_panel_clogin.startAnimation(animation2);
		state = STATE_CLOGIN;

	}

	public void finishAll() {
		finishAll = true;
		finish();
	}

	@Override
	public void finish() {
		if (!finishAll) {
			if (state == STATE_CLOGIN) {
				et_plogin_phone.setText(et_clogin_phone.getText().toString());
				Animation animation = AnimationUtils.loadAnimation(this,
						R.anim.tran_out_bottom);
				circleMenu.hideMenu();
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
				et_plogin_phone.requestFocus();
				et_plogin_pass.setText("");
			} else if (state == STATE_PLOGIN) {
				super.finish();
			}
		} else {
			super.finish();
		}
	}

}
