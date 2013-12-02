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

public class RegisterActivity extends Activity {

	private final int STATE_VERIFYPHONE = 0x001;
	private final int STATE_VERIFYCODE = 0x002;
	private final int STATE_SETPASS = 0x003;
	private int state;

	private RelativeLayout rl_regpanel_phone;
	private RelativeLayout rl_regpanel_code;
	private RelativeLayout rl_regpanel_pass;
	private EditText et_phone;
	private EditText et_code;
	private EditText et_pass;
	private TextView tv_resend;

	private AccountManager accountManager;

	private Handler handler;

	private int resendTime;
	private boolean canSend;

	private String phone;
	private String code;
	private String pass;
	private String accessKey;

	private Timer timer;

	private CircleMenu circleMenu;

	private boolean finishAll;

	private boolean verifyPhoneSuccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		initView();
	}

	public void initView() {
		state = STATE_VERIFYPHONE;
		rl_regpanel_phone = (RelativeLayout) findViewById(R.id.rl_regpanel_phone);
		rl_regpanel_code = (RelativeLayout) findViewById(R.id.rl_regpanel_code);
		rl_regpanel_pass = (RelativeLayout) findViewById(R.id.rl_regpanel_pass);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_code = (EditText) findViewById(R.id.et_code);
		et_pass = (EditText) findViewById(R.id.et_pass);
		tv_resend = (TextView) findViewById(R.id.tv_resend);

		accountManager = new AccountManagerImpl(this);
		handler = MCNetTools.handler;
		canSend = true;

		circleMenu = new CircleMenu(this);
		circleMenu.showMenu(CircleMenu.SHOW_TOP, null, true);
	}

	public void next(View v) {
		if (state == STATE_VERIFYPHONE) {
			String bphone = phone;
			phone = et_phone.getText().toString();
			if (bphone != null && !bphone.equals(phone)) {
				tv_resend.setText("重新发送(60)");
				if (timer != null) {
					timer.cancel();
				}
				canSend = true;
			}
			if (canSend) {
				canSend = false;
				Map<String, String> param = new HashMap<String, String>();
				param.put("phone", phone);
				param.put("usage", "register");
				accountManager.verifyphone(param, new MCResponseAdapter(this) {
					@Override
					public void success(JSONObject data) {
						Animation animation = AnimationUtils.loadAnimation(
								RegisterActivity.this, R.anim.tran_out_top);
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
								rl_regpanel_phone.setVisibility(View.GONE);
							}
						});
						rl_regpanel_phone.startAnimation(animation);

						rl_regpanel_code.setVisibility(View.VISIBLE);

						Animation animation2 = AnimationUtils.loadAnimation(
								RegisterActivity.this, R.anim.tran_in_bottom);
						rl_regpanel_code.startAnimation(animation2);
						resend();
						state = STATE_VERIFYCODE;
						verifyPhoneSuccess = true;
					}

					@Override
					public void noInternet() {
						canSend = true;
						super.noInternet();
					}

					@Override
					public void unsuccess(JSONObject data) {
						canSend = true;
						super.unsuccess(data);
					}

					@Override
					public void failed() {
						canSend = true;
						super.failed();
					}
				});
			} else if (verifyPhoneSuccess) {
				Animation animation = AnimationUtils.loadAnimation(
						RegisterActivity.this, R.anim.tran_out_top);
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
						rl_regpanel_phone.setVisibility(View.GONE);
					}
				});
				rl_regpanel_phone.startAnimation(animation);

				rl_regpanel_code.setVisibility(View.VISIBLE);

				Animation animation2 = AnimationUtils.loadAnimation(
						RegisterActivity.this, R.anim.tran_in_bottom);
				rl_regpanel_code.startAnimation(animation2);
				state = STATE_VERIFYCODE;
			}
		} else if (state == STATE_VERIFYCODE) {
			code = et_code.getText().toString();
			Map<String, String> param = new HashMap<String, String>();
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
					Animation animation = AnimationUtils.loadAnimation(
							RegisterActivity.this, R.anim.tran_out_top);
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
							rl_regpanel_code.setVisibility(View.GONE);
						}
					});
					rl_regpanel_code.startAnimation(animation);

					rl_regpanel_pass.setVisibility(View.VISIBLE);

					Animation animation2 = AnimationUtils.loadAnimation(
							RegisterActivity.this, R.anim.tran_in_bottom);
					rl_regpanel_pass.startAnimation(animation2);

					tv_resend.setText("重新发送");
					timer.cancel();
					canSend = true;
					state = STATE_SETPASS;
				}
			});
		} else if (state == STATE_SETPASS) {
			pass = et_pass.getText().toString();
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone", phone);
			param.put("accessKey", accessKey);
			JSONObject jo = new JSONObject();
			try {
				jo.put("password", pass);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			param.put("account", jo.toString());
			accountManager.modify(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					Intent intent = new Intent(RegisterActivity.this,
							MessagesActivity.class);
					startActivity(intent);
					finishAll();
					LoginActivity.instance.finishAll();
				}
			});
		}
	}

	public void resend(View v) {
		if (canSend) {
			canSend = false;
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone", phone);
			param.put("usage", "register");
			accountManager.verifyphone(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
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
							tv_resend.setText("重新发送");
							timer.cancel();
							canSend = true;
						} else {
							tv_resend.setText("重新发送(" + resendTime + ")");
						}
						resendTime--;
					}
				});
			}
		}, 200, 1000);
	}

	public void finishAll() {
		finishAll = true;
		finish();
	}

	@Override
	public void finish() {
		if (!finishAll) {
			if (state == STATE_SETPASS) {
				Animation animation = AnimationUtils.loadAnimation(
						RegisterActivity.this, R.anim.tran_out_bottom);
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
						rl_regpanel_pass.setVisibility(View.GONE);
					}
				});
				rl_regpanel_pass.startAnimation(animation);

				rl_regpanel_code.setVisibility(View.VISIBLE);

				Animation animation2 = AnimationUtils.loadAnimation(
						RegisterActivity.this, R.anim.tran_in_top);
				rl_regpanel_code.startAnimation(animation2);

				state = STATE_VERIFYCODE;
			} else if (state == STATE_VERIFYCODE) {
				Animation animation = AnimationUtils.loadAnimation(
						RegisterActivity.this, R.anim.tran_out_bottom);
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
						rl_regpanel_code.setVisibility(View.GONE);
					}
				});
				rl_regpanel_code.startAnimation(animation);

				rl_regpanel_phone.setVisibility(View.VISIBLE);

				Animation animation2 = AnimationUtils.loadAnimation(
						RegisterActivity.this, R.anim.tran_in_top);
				rl_regpanel_phone.startAnimation(animation2);
				state = STATE_VERIFYPHONE;
			} else if (state == STATE_VERIFYPHONE) {
				super.finish();
			}
		} else {
			super.finish();
		}
	}

}