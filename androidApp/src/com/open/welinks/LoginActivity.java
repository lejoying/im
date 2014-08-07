package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener, OnFocusChangeListener {

	// private MainApplication app = MainApplication.getMainApplication();

	private static final int NEWS = 0;
	private static final int COLOR = 1;
	private static final int RED = 2;
	private static final int BULE = 3;
	private static final int GREED = 4;
	private static final int FONT = 5;
	private static final int BIG = 6;
	private static final int SMALL = 7;

	private Handler handler;

	private View loginOrRegister;
	private View loginOrRegisterButton;
	private View loginButton;
	private View registerButton;

	private View card;
	private TextView leftTopText;
	private TextView rightTopTextButton;
	private EditText input1;
	private EditText input2;
	private View clearInput1;
	private View clearInput2;
	private TextView mainButton;
	private TextView leftBottomTextButton;
	private TextView rightBottomTextButton;

	private Animation animationNextOut;
	private Animation animationNextIn;
	private Animation animationBackOut;
	private Animation animationBackIn;

	private enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	private Status status = Status.welcome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (status == Status.welcome) {
			status = Status.welcome;
		} else {
			status = Status.start;
		}

		setContentView(R.layout.activity_login);

		handler = new Handler();

		Intent intent = getIntent();
		if (intent != null && "ReLogin".equals(intent.getStringExtra("operation"))) {
			initView();
			initEvent();
			status = Status.loginUsePassword;
			setCardContent(status);
			loginOrRegister.setVisibility(View.INVISIBLE);
			loginOrRegisterButton.setVisibility(View.VISIBLE);
			card.setVisibility(View.VISIBLE);

			showSoftInputDelay(input2, 400);
		} else {
			switchToShow();
		}
		super.onCreate(savedInstanceState);
	}

	int i = 1;
	public String tag = "LoginActivity";

	private void switchToShow() {
		if (i + 1 != 2) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					LoginActivity.this.finish();
				}
			}, 1500);
		} else {

			initView();
			initEvent();

			status = Status.loginOrRegister;
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					backAnimation(status, loginOrRegisterButton, null);
				}
			}, 500);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_debug_1, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.debug1_1) {
			Log.d(tag, "debug1.1");
		}
		return true;
	}

	private void initView() {

		animationNextOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.animation_next_out);
		animationNextIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.animation_next_in);
		animationBackOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.animation_back_out);
		animationBackIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.animation_back_in);

		loginOrRegister = findViewById(R.id.loginOrRegister);
		loginOrRegisterButton = findViewById(R.id.loginOrRegisterButton);
		loginButton = findViewById(R.id.loginButton);
		registerButton = findViewById(R.id.registerButton);

		card = findViewById(R.id.card);
		leftTopText = (TextView) findViewById(R.id.leftTopText);
		rightTopTextButton = (TextView) findViewById(R.id.rightTopTextButton);
		input1 = (EditText) findViewById(R.id.input1);
		input2 = (EditText) findViewById(R.id.input2);
		clearInput1 = findViewById(R.id.clearInput1);
		clearInput2 = findViewById(R.id.clearInput2);
		mainButton = (TextView) findViewById(R.id.mainButton);
		leftBottomTextButton = (TextView) findViewById(R.id.leftBottomTextButton);
		rightBottomTextButton = (TextView) findViewById(R.id.rightBottomTextButton);
	}

	private void initEvent() {
		input1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals("")) {
					clearInput1.setVisibility(View.VISIBLE);
				} else {
					clearInput1.setVisibility(View.INVISIBLE);
				}
			}
		});
		input2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals("")) {
					clearInput2.setVisibility(View.VISIBLE);
				} else {
					clearInput2.setVisibility(View.INVISIBLE);
				}
			}
		});
		input1.setOnFocusChangeListener(this);
		input2.setOnFocusChangeListener(this);

		input1.setOnClickListener(this);
		input2.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		rightTopTextButton.setOnClickListener(this);
		clearInput1.setOnClickListener(this);
		clearInput2.setOnClickListener(this);
		mainButton.setOnClickListener(this);
		leftBottomTextButton.setOnClickListener(this);
		rightBottomTextButton.setOnClickListener(this);
	}

	private void setCardContent(Status status) {

		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceid = telephonyManager.getDeviceId();// 获取智能设备唯一编号
		String line1Number = telephonyManager.getLine1Number();// 获取本机号码
		String imei = telephonyManager.getSimSerialNumber();// 获得SIM卡的序号
		String imsi = telephonyManager.getSubscriberId();// 得到用户Id

		switch (status) {
		case loginUsePassword:
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("登陆");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入手机号");
			input2.setHint("请输入密码");
			mainButton.setText("登陆");
			leftBottomTextButton.setVisibility(View.VISIBLE);
			leftBottomTextButton.setText("忘记密码?");
			rightBottomTextButton.setVisibility(View.VISIBLE);
			rightBottomTextButton.setText("验证码登录");
			input1.setText("13566668888");
			if (line1Number != null && line1Number != "") {
				input1.setText(line1Number);
			}
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		case verifyPhoneForLogin:
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("验证码登陆");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入手机号");
			input2.setHint("请输入验证码");
			mainButton.setText("登陆");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.VISIBLE);
			if (remainLogin != 0) {
				rightBottomTextButton.setText("重新发送(" + remainLogin + ")");
			} else {
				rightBottomTextButton.setText("发送验证码");
			}
			if (loginPhone != null && !loginPhone.equals("")) {
				input1.setText(loginPhone);
			} else {
				input1.setText("");
			}
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_NUMBER);
			break;
		case verifyPhoneForRegister:
			if (remainRegister != 0) {
				rightBottomTextButton.setText("重新发送(" + remainRegister + ")");
			} else {
				rightBottomTextButton.setText("发送验证码");
			}
			if (registerPhone != null && !registerPhone.equals("")) {
				input1.setText(registerPhone);
			} else {
				input1.setText("");
			}
		case verifyPhoneForResetPassword:
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("验证手机号");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入手机号");
			input2.setHint("请输入验证码");
			mainButton.setText("下一步");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.VISIBLE);
			if (status == Status.verifyPhoneForResetPassword) {
				if (remainResetPassword != 0) {
					rightBottomTextButton.setText("重新发送(" + remainResetPassword + ")");
				} else {
					rightBottomTextButton.setText("发送验证码");
				}
				if (resetPasswordPhone != null && !resetPasswordPhone.equals("")) {
					input1.setText(resetPasswordPhone);
				} else {
					input1.setText("");
				}
			}
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_NUMBER);
			break;
		case setPassword:
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("输入密码");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入密码");
			input2.setHint("请确认密码");
			mainButton.setText("完成");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.INVISIBLE);
			input1.setText("");
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		case resetPassword:
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("重置密码");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入密码");
			input2.setHint("请验证密码");
			mainButton.setText("完成");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.INVISIBLE);
			input1.setText("");
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		default:
			break;
		}
	}

	private InputMethodManager mInputMethodManager;
	private Runnable showSoftInputRunnable;

	private InputMethodManager getInputMethodManager() {
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return mInputMethodManager;
	}

	private void showSoftInput(EditText editText) {
		editText.requestFocus();
		getInputMethodManager().showSoftInput(editText, InputMethodManager.SHOW_FORCED);
	}

	private void showSoftInputDelay(final EditText editText, long delayMillis) {
		handler.postDelayed(showSoftInputRunnable = new Runnable() {
			@Override
			public void run() {
				showSoftInput(editText);
			}
		}, delayMillis);
	}

	private boolean hideSoftInput() {
		if (showSoftInputRunnable != null) {
			handler.removeCallbacks(showSoftInputRunnable);
		}
		View currentFocus = getCurrentFocus();
		boolean flag = false;
		if (currentFocus != null) {
			flag = getInputMethodManager().hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
		}
		return flag;
	}

	private Runnable animationRunnable;

	private void nextAnimation(final Status nextStatus, final View in, View out) {
		hideSoftInput();
		LoginActivity.this.status = nextStatus;
		if (out != null && out.getVisibility() == View.VISIBLE) {
			out.setVisibility(View.INVISIBLE);
			out.startAnimation(animationNextOut);
		}
		if (in != null) {
			if (animationRunnable != null) {
				handler.removeCallbacks(animationRunnable);
			}
			handler.postDelayed(animationRunnable = new Runnable() {
				@Override
				public void run() {
					setCardContent(nextStatus);
					in.setVisibility(View.VISIBLE);
					in.startAnimation(animationNextIn);
				}
			}, animationNextOut.getDuration());
		}
	}

	private void backAnimation(final Status nextStatus, final View in, View out) {
		hideSoftInput();
		LoginActivity.this.status = nextStatus;
		if (out != null && out.getVisibility() == View.VISIBLE) {
			out.setVisibility(View.INVISIBLE);
			out.startAnimation(animationBackOut);
		}
		if (in != null) {
			if (animationRunnable != null) {
				handler.removeCallbacks(animationRunnable);
			}
			handler.postDelayed(animationRunnable = new Runnable() {
				@Override
				public void run() {
					setCardContent(nextStatus);
					in.setVisibility(View.VISIBLE);
					in.startAnimation(animationBackIn);
				}
			}, animationBackOut.getDuration());
		}
	}

	private Runnable remainRegisterRunnable;
	private Runnable remainResetPasswordRunnable;
	private Runnable remainLoginRunnable;
	int remainRegister;
	int remainResetPassword;
	int remainLogin;
	String registerPhone = "";
	String resetPasswordPhone = "";
	String loginPhone = "";

	public void cancelRemain(Status status) {
		switch (status) {
		case verifyPhoneForLogin:
			if (remainLoginRunnable != null) {
				handler.removeCallbacks(remainLoginRunnable);
			}
			remainLogin = 0;
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (LoginActivity.this.status == Status.verifyPhoneForLogin) {
						rightBottomTextButton.setText("发送验证码");
					}
				}
			});
			break;
		case verifyPhoneForRegister:
			if (remainRegisterRunnable != null) {
				handler.removeCallbacks(remainRegisterRunnable);
			}
			remainRegister = 0;
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (LoginActivity.this.status == Status.verifyPhoneForRegister) {
						rightBottomTextButton.setText("发送验证码");
					}
				}
			});

			break;
		case verifyPhoneForResetPassword:
			if (remainResetPasswordRunnable != null) {
				handler.removeCallbacks(remainResetPasswordRunnable);
			}
			remainResetPassword = 0;
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (LoginActivity.this.status == Status.verifyPhoneForResetPassword) {
						rightBottomTextButton.setText("发送验证码");
					}
				}
			});
			break;

		default:
			break;
		}
	}

	public void startRemain(final Status status) {
		switch (status) {
		case verifyPhoneForLogin:
			if (remainLogin != 0) {
				return;
			}
			remainLogin = 60;
			handler.post(remainLoginRunnable = new Runnable() {
				@Override
				public void run() {
					if (remainLogin-- <= 0) {
						cancelRemain(status);
					} else {
						if (LoginActivity.this.status == Status.verifyPhoneForLogin) {
							rightBottomTextButton.setText("重新发送(" + remainLogin + ")");
						}
						handler.postDelayed(this, 1000);
					}
				}
			});
			break;
		case verifyPhoneForRegister:
			if (remainRegister != 0) {
				return;
			}
			remainRegister = 60;
			handler.post(remainRegisterRunnable = new Runnable() {
				@Override
				public void run() {
					if (remainRegister-- <= 0) {
						cancelRemain(status);
					} else {
						if (LoginActivity.this.status == Status.verifyPhoneForRegister) {
							rightBottomTextButton.setText("重新发送(" + remainRegister + ")");
						}
						handler.postDelayed(this, 1000);
					}
				}
			});
			break;
		case verifyPhoneForResetPassword:
			if (remainResetPassword != 0) {
				return;
			}
			remainResetPassword = 60;
			handler.post(remainResetPasswordRunnable = new Runnable() {
				@Override
				public void run() {
					if (remainResetPassword-- <= 0) {
						cancelRemain(status);
					} else {
						if (LoginActivity.this.status == Status.verifyPhoneForResetPassword) {
							rightBottomTextButton.setText("重新发送(" + remainResetPassword + ")");
						}
						handler.postDelayed(this, 1000);
					}
				}
			});
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		hideSoftInput();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switch (status) {
			case loginUsePassword:
				backAnimation(Status.loginOrRegister, loginOrRegister, card);
				break;
			case verifyPhoneForLogin:
				backAnimation(Status.loginUsePassword, card, card);
				break;
			case verifyPhoneForRegister:
				backAnimation(Status.loginOrRegister, loginOrRegister, card);
				break;
			case verifyPhoneForResetPassword:
				backAnimation(Status.loginUsePassword, card, card);
				break;
			case setPassword:
				break;
			case resetPassword:
				break;
			case welcome:
			case start:
			case loginOrRegister:
			default:
				flag = super.onKeyDown(keyCode, event);
				break;
			}
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

	@Override
	public void onClick(View v) {

		if (v.equals(loginButton)) {
			nextAnimation(Status.loginUsePassword, card, loginOrRegister);
			if (i + 1 == 2) {
				showSoftInputDelay(input2, animationBackIn.getDuration() + animationBackOut.getDuration() + 20);
			} else {
				showSoftInputDelay(input1, animationBackIn.getDuration() + animationBackOut.getDuration() + 20);
			}
		} else if (v.equals(registerButton)) {
			nextAnimation(Status.verifyPhoneForRegister, card, loginOrRegister);
			if (remainRegister == 0) {
				showSoftInputDelay(input1, animationNextIn.getDuration() + animationNextOut.getDuration() + 20);
			} else {

				showSoftInputDelay(input2, animationNextIn.getDuration() + animationNextOut.getDuration() + 20);
			}
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.equals(input1)) {
			if (hasFocus && !input1.getText().toString().equals("")) {
				clearInput1.setVisibility(View.VISIBLE);
			} else {
				clearInput1.setVisibility(View.INVISIBLE);
			}
		} else if (v.equals(input2)) {
			if (hasFocus && !input2.getText().toString().equals("")) {
				clearInput2.setVisibility(View.VISIBLE);
			} else {
				clearInput2.setVisibility(View.INVISIBLE);
			}
		}
	}
}
