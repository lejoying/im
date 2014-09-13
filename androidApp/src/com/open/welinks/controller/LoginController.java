package com.open.welinks.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.MainActivity;
import com.open.welinks.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.CommonNetConnection;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.utils.NetworkHandler.Settings;
import com.open.welinks.utils.SHA1;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog;
import com.open.welinks.view.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.view.Alert.OnLoadingCancelListener;
import com.open.welinks.view.LoginView;
import com.open.welinks.view.LoginView.Status;

public class LoginController {
	public Data data = Data.getInstance();
	public String tag = "LoginController";

	public Runnable animationRunnable;
	public Runnable showSoftInputRunnable;
	public Runnable remainRegisterRunnable;
	public Runnable remainResetPasswordRunnable;
	public Runnable remainLoginRunnable;
	int remainRegister;
	int remainResetPassword;
	int remainLogin;
	String registerPhone = "";
	String resetPasswordPhone = "";
	String loginPhone = "";

	public Context context;
	public LoginView thisView;
	public LoginController thisController;
	public Activity thisActivity;

	public OnFocusChangeListener mOnFocusChangeListener;
	public OnClickListener mOnClickListener;
	public TextWatcher mTextWatcher1;
	public TextWatcher mTextWatcher2;
	public OnTouchListener onTouchListener;

	public NetworkHandler mNetworkHandler = NetworkHandler.getInstance();
	public Handler handler = new Handler();
	public String url_userauth = "http://www.we-links.com/api2/account/auth";

	public Gson gson = new Gson();

	public SHA1 msSha1 = new SHA1();

	public MySpringListener mSpringListener = new MySpringListener();

	public InputMethodManager mInputMethodManager;

	public LoginController(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initializeListeners() {
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					thisView.mScaleSpring.setEndValue(1);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					thisView.mScaleSpring.setEndValue(0);
				}
				return true;
			}
		};
		mOnFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (view.equals(thisView.input1)) {
					if (hasFocus && !thisView.input1.getText().toString().equals("")) {
						thisView.clearInput1.setVisibility(View.VISIBLE);
					} else {
						thisView.clearInput1.setVisibility(View.INVISIBLE);
					}
				} else if (view.equals(thisView.input2)) {
					if (hasFocus && !thisView.input2.getText().toString().equals("")) {
						thisView.clearInput2.setVisibility(View.VISIBLE);
					} else {
						thisView.clearInput2.setVisibility(View.INVISIBLE);
					}
				}
			}
		};

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.loginButton)) {
					nextAnimation(Status.loginUsePassword, thisView.card, thisView.loginOrRegister);
					showSoftInputDelay(thisView.input1, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
				} else if (view.equals(thisView.registerButton)) {
					nextAnimation(Status.verifyPhoneForRegister, thisView.card, thisView.loginOrRegister);
					showSoftInputDelay(thisView.input1, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
				} else if (view.equals(thisView.clearInput1)) {
					thisView.input1.setText("");
				} else if (view.equals(thisView.clearInput2)) {
					thisView.input2.setText("");
				} else {
					if (thisView.status == Status.loginUsePassword) {
						if (view.equals(thisView.rightTopTextButton)) {
							hideSoftInput();
							backAnimation(Status.loginOrRegister, thisView.loginOrRegister, thisView.card);
						} else if (view.equals(thisView.mainButton)) {
							if (thisView.status == Status.loginUsePassword) {
								String loginPhone = thisView.input1.getText().toString().trim();
								String loginPass = thisView.input2.getText().toString().trim();
								if (loginPhone.equals("")) {
									loginFail(thisActivity.getString(R.string.alert_text_phonenotnull));
									showSoftInput(thisView.input1);
									return;
								} else if (loginPass.equals("")) {
									loginFail(thisActivity.getString(R.string.alert_text_passnotnull));
									showSoftInput(thisView.input2);
									return;
								} else {
									hideSoftInput();
									requestUserAuth(loginPhone, loginPass);
								}
							}
						} else if (view.equals(thisView.leftBottomTextButton)) {
							nextAnimation(Status.verifyPhoneForResetPassword, thisView.card, thisView.card);
							showSoftInputDelay(thisView.input1, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
						} else if (view.equals(thisView.rightBottomTextButton)) {
							nextAnimation(Status.verifyPhoneForLogin, thisView.card, thisView.card);
							if (remainLogin == 0) {
								showSoftInputDelay(thisView.input1, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
							} else {

								showSoftInputDelay(thisView.input2, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
							}
						}
					} else if (thisView.status == Status.verifyPhoneForLogin) {
						if (view.equals(thisView.rightTopTextButton)) {
							backAnimation(Status.loginUsePassword, thisView.card, thisView.card);
							showSoftInputDelay(thisView.input1, thisView.animationBackIn.getDuration() + thisView.animationBackOut.getDuration() + 20);
						} else if (view.equals(thisView.mainButton)) {
							final String phone = thisView.input1.getText().toString();
							final String code = thisView.input2.getText().toString();
							Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
							Matcher m = p.matcher(phone);
							if (phone.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_phonenotnull));
								showSoftInput(thisView.input1);
								return;
							} else if (!m.matches()) {
								loginFail(thisActivity.getString(R.string.alert_text_phoneformaterror));
								showSoftInput(thisView.input1);
								return;
							} else if (code.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_codenotnull));
								showSoftInput(thisView.input2);
								return;
							} else {
								hideSoftInput();
								requestUserAuthWithVerifyCode(phone, code);
							}

						} else if (view.equals(thisView.rightBottomTextButton)) {
							final String phone = thisView.input1.getText().toString();
							if (remainLogin != 0) {
								return;
							}
							Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
							Matcher m = p.matcher(phone);
							if (phone.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_phonenotnull));
								showSoftInput(thisView.input1);
								return;
							} else if (!m.matches()) {
								loginFail(thisActivity.getString(R.string.alert_text_phoneformaterror));
								showSoftInput(thisView.input1);
								return;
							} else {
								hideSoftInput();
								requestUserVerifyCode(phone);
							}
							loginPhone = phone;
						}
					} else if (thisView.status == Status.verifyPhoneForRegister) {
						if (view.equals(thisView.rightTopTextButton)) {
							hideSoftInput();
							backAnimation(Status.loginOrRegister, thisView.loginOrRegister, thisView.card);
						} else if (view.equals(thisView.mainButton)) {
							final String phone = thisView.input1.getText().toString();
							final String code = thisView.input2.getText().toString();
							Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
							Matcher m = p.matcher(phone);
							if (phone.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_phonenotnull));
								showSoftInput(thisView.input1);
								return;
							} else if (!m.matches()) {
								loginFail(thisActivity.getString(R.string.alert_text_phoneformaterror));
								showSoftInput(thisView.input1);
								return;
							} else if (code.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_codenotnull));
								showSoftInput(thisView.input2);
								return;
							} else {
								hideSoftInput();
								requestUserAuthWithVerifyCode(phone, code);
							}

						} else if (view.equals(thisView.rightBottomTextButton)) {
							final String phone = thisView.input1.getText().toString();
							if (remainRegister != 0) {
								return;
							}
							Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
							Matcher m = p.matcher(phone);
							if (phone.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_phonenotnull));
								showSoftInput(thisView.input1);
								return;
							} else if (!m.matches()) {
								loginFail(thisActivity.getString(R.string.alert_text_phoneformaterror));
								showSoftInput(thisView.input1);
								return;
							} else {
								showSoftInput(thisView.input2);
								requestUserVerifyCode(phone);
							}
							registerPhone = phone;
						}
					} else if (thisView.status == Status.verifyPhoneForResetPassword) {
						if (view.equals(thisView.rightTopTextButton)) {
							backAnimation(Status.loginUsePassword, thisView.card, thisView.card);
							showSoftInputDelay(thisView.input1, thisView.animationBackIn.getDuration() + thisView.animationBackOut.getDuration() + 20);

						} else if (view.equals(thisView.mainButton)) {
							final String phone = thisView.input1.getText().toString();
							final String code = thisView.input2.getText().toString();
							Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
							Matcher m = p.matcher(phone);
							if (phone.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_phonenotnull));
								showSoftInput(thisView.input1);
								return;
							} else if (!m.matches()) {
								loginFail(thisActivity.getString(R.string.alert_text_phoneformaterror));
								showSoftInput(thisView.input1);
								return;
							} else if (code.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_codenotnull));
								showSoftInput(thisView.input2);
								return;
							} else {
								hideSoftInput();
								requestUserAuthWithVerifyCode(phone, code);
							}
						} else if (view.equals(thisView.rightBottomTextButton)) {
							final String phone = thisView.input1.getText().toString();
							if (remainResetPassword != 0) {
								return;
							}
							Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
							Matcher m = p.matcher(phone);
							if (phone.equals("")) {
								loginFail(thisActivity.getString(R.string.alert_text_phonenotnull));
								showSoftInput(thisView.input1);
								return;
							} else if (!m.matches()) {
								loginFail(thisActivity.getString(R.string.alert_text_phoneformaterror));
								showSoftInput(thisView.input1);
								return;
							} else {
								showSoftInput(thisView.input2);
								requestUserVerifyCode(phone);
							}
							resetPasswordPhone = phone;
						} else if (view.equals(thisView.leftBottomTextButton)) {
							nextAnimation(Status.verifyPhoneForResetPassword, thisView.card, thisView.card);
							showSoftInputDelay(thisView.input1, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
						}
					} else if (thisView.status == Status.setPassword) {
						if (view.equals(thisView.rightTopTextButton)) {
							hideSoftInput();
							Alert.createDialog(thisActivity).setTitle("取消注册?").setOnConfirmClickListener(new OnDialogClickListener() {

								@Override
								public void onClick(AlertInputDialog dialog) {

									backAnimation(Status.verifyPhoneForRegister, thisView.card, thisView.card);
									showSoftInputDelay(thisView.input1, thisView.animationBackIn.getDuration() + thisView.animationBackOut.getDuration() + 20);

								}
							}).show();
						} else if (view.equals(thisView.mainButton)) {
							final String password = thisView.input1.getText().toString();
							String password2 = thisView.input2.getText().toString();
							if (password.equals("")) {
								loginFail("请输入密码");
								showSoftInput(thisView.input1);
							} else if (password.length() < 6) {
								loginFail(thisActivity.getString(R.string.alert_text_passlength));
								showSoftInput(thisView.input1);
							} else if (password2.equals("")) {
								loginFail("请确认密码");
								showSoftInput(thisView.input2);
							} else if (!password.equals(password2)) {
								loginFail("两次输入的密码不一致");
								showSoftInput(thisView.input2);
							} else {
								hideSoftInput();
								modifyUserPassword(resetPasswordPhone, password);
							}
						}
					} else if (thisView.status == Status.resetPassword) {
						if (view.equals(thisView.rightTopTextButton)) {
							hideSoftInput();
							Alert.createDialog(thisActivity).setTitle("取消重置密码?").setOnConfirmClickListener(new OnDialogClickListener() {

								@Override
								public void onClick(AlertInputDialog dialog) {
									backAnimation(Status.loginUsePassword, thisView.card, thisView.card);
									showSoftInputDelay(thisView.input1, thisView.animationBackIn.getDuration() + thisView.animationBackOut.getDuration() + 20);
								}
							}).show();
						} else if (view.equals(thisView.mainButton)) {
							final String password = thisView.input1.getText().toString();
							String password2 = thisView.input2.getText().toString();
							if (password.equals("")) {
								loginFail("请输入密码");
								showSoftInput(thisView.input1);
							} else if (password.length() < 6) {
								loginFail(thisActivity.getString(R.string.alert_text_passlength));
								showSoftInput(thisView.input1);
							} else if (password2.equals("")) {
								loginFail("请确认密码");
								showSoftInput(thisView.input2);
							} else if (!password.equals(password2)) {
								loginFail("两次输入的密码不一致");
								showSoftInput(thisView.input2);
							} else {
								hideSoftInput();
								modifyUserPassword(resetPasswordPhone, password);
							}
						}
					}
				}
			}
		};

		mTextWatcher1 = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals("")) {
					thisView.clearInput1.setVisibility(View.VISIBLE);
				} else {
					thisView.clearInput1.setVisibility(View.INVISIBLE);
				}
			}
		};

		mTextWatcher2 = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals("")) {
					thisView.clearInput2.setVisibility(View.VISIBLE);
				} else {
					thisView.clearInput2.setVisibility(View.INVISIBLE);
				}
			}
		};

	}

	public void bindEvent() {
		thisView.input1.addTextChangedListener(mTextWatcher1);
		thisView.input2.addTextChangedListener(mTextWatcher2);

		thisView.input1.setOnFocusChangeListener(mOnFocusChangeListener);
		thisView.input2.setOnFocusChangeListener(mOnFocusChangeListener);

		thisView.input1.setOnClickListener(mOnClickListener);
		thisView.input2.setOnClickListener(mOnClickListener);
		thisView.loginButton.setOnClickListener(mOnClickListener);
		thisView.registerButton.setOnClickListener(mOnClickListener);
		thisView.rightTopTextButton.setOnClickListener(mOnClickListener);
		thisView.leftTopText.setOnClickListener(mOnClickListener);
		thisView.clearInput1.setOnClickListener(mOnClickListener);
		thisView.clearInput2.setOnClickListener(mOnClickListener);
		thisView.mainButton.setOnClickListener(mOnClickListener);
		thisView.leftBottomTextButton.setOnClickListener(mOnClickListener);
		thisView.rightBottomTextButton.setOnClickListener(mOnClickListener);
		thisView.mRootView.setOnTouchListener(onTouchListener);
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;
		thisView.progressBar.setProgress(0);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				backAnimation(thisView.status, thisView.loginOrRegisterButton, null);
			}
		}, 500);
	}

	public void onResume() {
		thisView.mScaleSpring.addListener(mSpringListener);
	}

	public void onPause() {
		thisView.mScaleSpring.removeListener(mSpringListener);
	}

	public void onDestroy() {

	}

	private class MySpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
			thisView.appIconToName.setScaleX(mappedValue);
			thisView.appIconToName.setScaleY(mappedValue);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (thisView.status == Status.loginUsePassword) {
				backAnimation(Status.loginOrRegister, thisView.loginOrRegister, thisView.card);
			} else if (thisView.status == Status.verifyPhoneForLogin) {
				backAnimation(Status.loginUsePassword, thisView.card, thisView.card);

			} else if (thisView.status == Status.verifyPhoneForRegister) {
				backAnimation(Status.loginOrRegister, thisView.loginOrRegister, thisView.card);

			} else if (thisView.status == Status.verifyPhoneForResetPassword) {
				backAnimation(Status.loginUsePassword, thisView.card, thisView.card);
			}
		} else {
		}
		return flag;
	}

	public void nextAnimation(final Status nextStatus, final View in, View out) {
		thisView.status = nextStatus;
		if (out != null && out.getVisibility() == View.VISIBLE) {
			out.setVisibility(View.INVISIBLE);
			out.startAnimation(thisView.animationNextOut);
		}
		if (in != null) {
			if (animationRunnable != null) {
				handler.removeCallbacks(animationRunnable);
			}
			handler.postDelayed(animationRunnable = new Runnable() {
				@Override
				public void run() {
					thisView.setCardContent(nextStatus);
					in.setVisibility(View.VISIBLE);
					in.startAnimation(thisView.animationNextIn);
				}
			}, thisView.animationNextOut.getDuration());
		}
	}

	public void backAnimation(final Status nextStatus, final View in, View out) {
		thisView.status = nextStatus;
		if (out != null && out.getVisibility() == View.VISIBLE) {
			out.setVisibility(View.INVISIBLE);
			out.startAnimation(thisView.animationBackOut);
		}
		if (in != null) {
			if (animationRunnable != null) {
				handler.removeCallbacks(animationRunnable);
			}
			handler.postDelayed(animationRunnable = new Runnable() {
				@Override
				public void run() {
					thisView.setCardContent(nextStatus);
					in.setVisibility(View.VISIBLE);
					in.startAnimation(thisView.animationBackIn);
				}
			}, thisView.animationBackOut.getDuration());
		}
		thisView.progressBar.setProgress(0);
	}

	public InputMethodManager getInputMethodManager() {
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return mInputMethodManager;
	}

	public void showSoftInput(EditText editText) {
		editText.requestFocus();
		getInputMethodManager().showSoftInput(editText, InputMethodManager.SHOW_FORCED);
	}

	public void showSoftInputDelay(final EditText editText, long delayMillis) {
		handler.postDelayed(showSoftInputRunnable = new Runnable() {
			@Override
			public void run() {
				showSoftInput(editText);
			}
		}, delayMillis);
	}

	public boolean hideSoftInput() {
		if (showSoftInputRunnable != null) {
			handler.removeCallbacks(showSoftInputRunnable);
		}
		View currentFocus = thisActivity.getCurrentFocus();
		boolean flag = false;
		if (currentFocus != null) {
			flag = getInputMethodManager().hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
		}
		return flag;
	}

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
					if (thisView.status == Status.verifyPhoneForLogin) {
						thisView.rightBottomTextButton.setText("发送验证码");
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
					if (thisView.status == Status.verifyPhoneForRegister) {
						thisView.rightBottomTextButton.setText("发送验证码");
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
					if (thisView.status == Status.verifyPhoneForResetPassword) {
						thisView.rightBottomTextButton.setText("发送验证码");
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
						if (thisView.status == Status.verifyPhoneForLogin) {
							thisView.rightBottomTextButton.setText("重新发送(" + remainLogin + ")");
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
						if (thisView.status == Status.verifyPhoneForRegister) {
							thisView.rightBottomTextButton.setText("重新发送(" + remainRegister + ")");
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
						if (thisView.status == Status.verifyPhoneForResetPassword) {
							thisView.rightBottomTextButton.setText("重新发送(" + remainResetPassword + ")");
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

	public void requestUserAuth(final String loginPhone, final String loginPass) {
		growProgressBar(30, 30);
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", loginPhone);
		params.addBodyParameter("password", msSha1.getDigestOfString(loginPass.getBytes()));
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_AUTH, params, responseHandlers.account_auth);
	}

	public void requestUserAuthWithVerifyCode(final String loginPhone, final String loginCode) {
		if (thisView.status != Status.verifyPhoneForResetPassword && thisView.status != Status.setPassword) {
			growProgressBar(30, 30);
		}
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", loginPhone);
		params.addBodyParameter("code", loginCode);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_VERIFYCODE, params, responseHandlers.account_verifycode);

	}

	public void requestUserAuthWithVerifyCodeCallBack() {
		if (thisView.status == Status.verifyPhoneForLogin) {
			loginSuccessful(loginPhone);
		} else if (thisView.status == Status.verifyPhoneForRegister) {
			cancelRemain(Status.verifyPhoneForRegister);
			Alert.removeLoading();
			nextAnimation(Status.setPassword, thisView.card, thisView.card);
			showSoftInputDelay(thisView.input1, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
		} else if (thisView.status == Status.verifyPhoneForResetPassword) {
			cancelRemain(Status.verifyPhoneForResetPassword);
			Alert.removeLoading();
			nextAnimation(Status.resetPassword, thisView.card, thisView.card);
			showSoftInputDelay(thisView.input1, thisView.animationNextIn.getDuration() + thisView.animationNextOut.getDuration() + 20);
		}

	}

	public void requestUserVerifyCode(final String phone) {

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", phone);
		if (thisView.status == Status.verifyPhoneForResetPassword || thisView.status == Status.verifyPhoneForLogin) {
			params.addBodyParameter("usage", "login");
		} else if (thisView.status == Status.verifyPhoneForRegister) {
			params.addBodyParameter("usage", "register");
		}
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_VERIFYPHONE, params, responseHandlers.account_verifyphone);

	}

	public void requestUserVerifyCodeCallBack() {
		if (thisView.status == Status.verifyPhoneForLogin) {
			startRemain(Status.verifyPhoneForLogin);
			handler.post(new Runnable() {

				@Override
				public void run() {
					thisView.input2.requestFocus();
				}
			});
		} else if (thisView.status == Status.verifyPhoneForRegister) {
			startRemain(Status.verifyPhoneForRegister);
			handler.post(new Runnable() {

				@Override
				public void run() {
					thisView.input2.requestFocus();
				}
			});
		} else if (thisView.status == Status.verifyPhoneForResetPassword) {
			startRemain(Status.verifyPhoneForResetPassword);
			handler.post(new Runnable() {

				@Override
				public void run() {
					thisView.input2.requestFocus();
				}
			});
		}

	}

	public void modifyUserPassword(final String phone, final String passWord) {
		growProgressBar(30, 30);
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", phone);
		params.addBodyParameter("password", msSha1.getDigestOfString(passWord.getBytes()));
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_MODIFYPASSWORD, params, responseHandlers.account_modifypassword);

	}

	public void modifyUserPasswordCallBack() {
		loginSuccessful(resetPasswordPhone);
	}

	public void growProgressBar(final int progress, final int max) {
		new Thread() {
			public void run() {
				for (int i = 0; i < progress; i++) {
					try {
						sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (thisView.progressBar.getProgress() < max)
								thisView.progressBar.incrementProgressBy(1);
						}
					});
				}
			};
		}.start();
	}

	public void loginSuccessful(final String phone) {
		new Thread() {
			public void run() {
				Intent intent = new Intent(thisActivity, MainActivity.class);
				intent.putExtra("phone", phone);
				for (int i = 1; i <= 50; i++) {
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					thisView.progressBar.incrementProgressBy(1);
				}
				thisActivity.startActivity(intent);
				thisActivity.finish();
			};
		}.start();
	}

	public void loginFail(String 失败原因) {
		thisView.error_message.setText(失败原因);
		new Thread() {
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						thisView.error_message.setText("");
					}
				});
			};
		}.start();
		thisView.progressBar.setProgress(0);
	}

}
