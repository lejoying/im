package com.lejoying.wxgs.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;

public class LoginActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {

	private MainApplication app = MainApplication.getMainApplication();

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

	private Status status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (app.config.isNeedToShowWelcome) {
			status = Status.welcome;
		} else {
			status = Status.start;
		}

		handler = new Handler();
		animationNextOut = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_next_out);
		animationNextIn = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_next_in);
		animationBackOut = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_back_out);
		animationBackIn = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_back_in);

		setContentView(R.layout.activity_login);

		initView();

		initEvent();

		status = Status.loginOrRegister;
		loginOrRegisterButton.setVisibility(View.VISIBLE);

		super.onCreate(savedInstanceState);
	}

	private void initView() {
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
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

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
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

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
			input1.setText("");
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
			rightBottomTextButton.setText("发送验证码");
			input1.setText("");
			input2.setText("");
			break;
		case verifyPhoneForRegister:
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
			rightBottomTextButton.setText("发送验证码");
			input1.setText("");
			input2.setText("");
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
			break;
		default:
			break;
		}
	}

	private Runnable animationRunnable;

	private void nextAnimation(final Status status, final View in, View out) {

		LoginActivity.this.status = status;
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
					setCardContent(status);
					in.setVisibility(View.VISIBLE);
					in.startAnimation(animationNextIn);
				}
			}, animationNextOut.getDuration());
		}
	}

	private void backAnimation(final Status status, final View in, View out) {
		LoginActivity.this.status = status;
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
					setCardContent(status);
					in.setVisibility(View.VISIBLE);
					in.startAnimation(animationBackIn);
				}
			}, animationBackOut.getDuration());
		}
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
				backAnimation(Status.verifyPhoneForRegister, card, card);
				break;
			case resetPassword:
				backAnimation(Status.verifyPhoneForResetPassword, card, card);
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
		} else if (v.equals(registerButton)) {
			nextAnimation(Status.verifyPhoneForRegister, card, loginOrRegister);
		} else if (v.equals(clearInput1)) {
			input1.setText("");
		} else if (v.equals(clearInput2)) {
			input2.setText("");
		} else {
			switch (status) {
			case loginUsePassword:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.loginOrRegister, loginOrRegister, card);
				} else if (v.equals(mainButton)) {
					System.out.println("点击登陆了");
				} else if (v.equals(leftBottomTextButton)) {
					nextAnimation(Status.verifyPhoneForResetPassword, card,
							card);
				} else if (v.equals(rightBottomTextButton)) {
					nextAnimation(Status.verifyPhoneForLogin, card, card);
				}
				break;
			case verifyPhoneForLogin:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.loginUsePassword, card, card);
				} else if (v.equals(mainButton)) {
					System.out.println("点击登陆了");
				} else if (v.equals(rightBottomTextButton)) {
					System.out.println("发送验证码");
				}
				break;
			case verifyPhoneForRegister:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.loginOrRegister, loginOrRegister, card);
				} else if (v.equals(mainButton)) {
					nextAnimation(Status.setPassword, card, card);
				} else if (v.equals(rightBottomTextButton)) {
					System.out.println("发送验证码");
				}
				break;
			case verifyPhoneForResetPassword:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.loginUsePassword, card, card);
				} else if (v.equals(mainButton)) {
					nextAnimation(Status.resetPassword, card, card);
				} else if (v.equals(rightBottomTextButton)) {
					System.out.println("发送验证码");
				}
				break;
			case setPassword:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.verifyPhoneForRegister, card, card);
				} else if (v.equals(mainButton)) {
					System.out.println("点击完成了");
				}
				break;
			case resetPassword:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.verifyPhoneForResetPassword, card,
							card);
				} else if (v.equals(mainButton)) {
					System.out.println("点击完成了");
				}
				break;
			default:
				break;
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
