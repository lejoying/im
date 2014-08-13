package com.lejoying.wxgs.activity;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.utils.NetworkUtils;
import com.lejoying.wxgs.utils.RSAUtils;

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

		setContentView(R.layout.activity_login);

		handler = new Handler();
		Alert.initialize(this);

		Intent intent = getIntent();
		if (intent != null
				&& "ReLogin".equals(intent.getStringExtra("operation"))) {
			app.dataHandler.exclude(new Modification() {

				@Override
				public void modifyData(Data data) {
					data.clear();
					data.isClear = false;
				}
			});
			initView();
			initEvent();
			status = Status.loginUsePassword;
			setCardContent(status);
			loginOrRegister.setVisibility(View.INVISIBLE);
			loginOrRegisterButton.setVisibility(View.VISIBLE);
			card.setVisibility(View.VISIBLE);

			showSoftInputDelay(input2, 400);
		} else {
			if (app.config.lastLoginPhone != null
					&& !app.config.lastLoginPhone.equals("")) {
				final long start = System.currentTimeMillis();
				app.dataHandler.exclude(new Modification() {
					@Override
					public void modifyData(Data data) {
						readLocalData(app.config.lastLoginPhone, data);
						long end = System.currentTimeMillis();
						if (end - start < 800) {
							try {
								Thread.sleep(800 - (end - start));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					@Override
					public void modifyUI() {
						switchToShow();
					}
				});
			} else {
				switchToShow();
			}
		}
		super.onCreate(savedInstanceState);
	}

	private void readLocalData(String phone, Data data) {
		try {
			Data localData = (Data) StreamParser
					.parseToObject(openFileInput(phone));
			if (localData != null) {
				data.user = localData.user;
				data.circles = localData.circles;
				data.circlesMap = localData.circlesMap;
				data.friends = localData.friends;
				data.groups = localData.groups;
				data.currentSquare = localData.currentSquare;
				data.currentGroup=localData.currentGroup;
				data.squareFlags = localData.squareFlags;
				data.squareMessages = localData.squareMessages;
				data.squareMessagesClassify = localData.squareMessagesClassify;
				data.squareCollects = localData.squareCollects;
				data.squareMessagesMap = localData.squareMessagesMap;
				data.groupsMap = localData.groupsMap;
				data.groupFriends = localData.groupFriends;
				data.lastChatFriends = localData.lastChatFriends;
				data.newFriends = localData.newFriends;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void switchToShow() {
		if (app.data.user.phone != null && !app.data.user.phone.equals("")
				&& app.data.user.accessKey != null
				&& !app.data.user.accessKey.equals("")) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					startActivity(new Intent(LoginActivity.this,
							MainActivity.class));
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

	private void initView() {

		animationNextOut = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_next_out);
		animationNextIn = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_next_in);
		animationBackOut = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_back_out);
		animationBackIn = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.animation_back_in);

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
			if (!app.config.lastLoginPhone.equals("")) {
				input1.setText(app.config.lastLoginPhone);
			}
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
					rightBottomTextButton.setText("重新发送(" + remainResetPassword
							+ ")");
				} else {
					rightBottomTextButton.setText("发送验证码");
				}
				if (resetPasswordPhone != null
						&& !resetPasswordPhone.equals("")) {
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
			input1.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input2.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
			input1.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input2.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
		getInputMethodManager().showSoftInput(editText,
				InputMethodManager.SHOW_FORCED);
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
			flag = getInputMethodManager().hideSoftInputFromWindow(
					currentFocus.getWindowToken(), 0);
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
							rightBottomTextButton.setText("重新发送(" + remainLogin
									+ ")");
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
							rightBottomTextButton.setText("重新发送("
									+ remainRegister + ")");
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
							rightBottomTextButton.setText("重新发送("
									+ remainResetPassword + ")");
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
				if (app.config.lastLoginPhone != null
						&& !app.config.lastLoginPhone.equals("")) {

					showSoftInputDelay(input2, animationBackIn.getDuration()
							+ animationBackOut.getDuration() + 20);
				} else {
					showSoftInputDelay(input1, animationBackIn.getDuration()
							+ animationBackOut.getDuration() + 20);
				}
				break;
			case verifyPhoneForRegister:
				backAnimation(Status.loginOrRegister, loginOrRegister, card);
				break;
			case verifyPhoneForResetPassword:
				backAnimation(Status.loginUsePassword, card, card);
				if (app.config.lastLoginPhone != null
						&& !app.config.lastLoginPhone.equals("")) {
					input1.clearFocus();
					showSoftInputDelay(input2, animationBackIn.getDuration()
							+ animationBackOut.getDuration() + 20);
				} else {
					showSoftInputDelay(input1, animationBackIn.getDuration()
							+ animationBackOut.getDuration() + 20);
				}
				break;
			case setPassword:
				Alert.createDialog(LoginActivity.this).setTitle("取消注册?")
						.setOnConfirmClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(AlertInputDialog dialog) {
								app.dataHandler.exclude(new Modification() {

									@Override
									public void modifyData(Data data) {
										data.user.accessKey = "";
									}

									@Override
									public void modifyUI() {
										backAnimation(
												Status.verifyPhoneForRegister,
												card, card);
										showSoftInputDelay(
												input1,
												animationBackIn.getDuration()
														+ animationBackOut
																.getDuration()
														+ 20);
									}
								});
							}
						}).show();
				break;
			case resetPassword:
				Alert.createDialog(LoginActivity.this).setTitle("取消重置密码?")
						.setOnConfirmClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(AlertInputDialog dialog) {
								app.dataHandler.exclude(new Modification() {

									@Override
									public void modifyData(Data data) {
										data.user.accessKey = "";
									}

									@Override
									public void modifyUI() {
										backAnimation(Status.loginUsePassword,
												card, card);
										if (app.config.lastLoginPhone != null
												&& !app.config.lastLoginPhone
														.equals("")) {
											showSoftInputDelay(
													input2,
													animationBackIn
															.getDuration()
															+ animationBackOut
																	.getDuration()
															+ 20);
										} else {
											showSoftInputDelay(
													input1,
													animationBackIn
															.getDuration()
															+ animationBackOut
																	.getDuration()
															+ 20);
										}
									}
								});
							}
						}).show();
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
			if (app.config.lastLoginPhone != null
					&& !app.config.lastLoginPhone.equals("")) {

				showSoftInputDelay(input2, animationBackIn.getDuration()
						+ animationBackOut.getDuration() + 20);
			} else {
				showSoftInputDelay(input1, animationBackIn.getDuration()
						+ animationBackOut.getDuration() + 20);
			}
		} else if (v.equals(registerButton)) {
			nextAnimation(Status.verifyPhoneForRegister, card, loginOrRegister);
			if (remainRegister == 0) {
				showSoftInputDelay(input1, animationNextIn.getDuration()
						+ animationNextOut.getDuration() + 20);
			} else {

				showSoftInputDelay(input2, animationNextIn.getDuration()
						+ animationNextOut.getDuration() + 20);
			}
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
					final String loginPhone = input1.getText().toString();
					final String loginPass = input2.getText().toString();
					if (loginPhone.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_phonenotnull));
						showSoftInput(input1);
						return;
					} else if (loginPass.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_passnotnull));
						showSoftInput(input2);
						return;
					} else {
						hideSoftInput();
					}

					final NetConnection mLoginConnection = new CommonNetConnection() {
						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN + API.ACCOUNT_AUTH;
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", loginPhone);
							String passwd = app.mSHA1
									.getDigestOfString(loginPass.getBytes());
							params.put("password", passwd);
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							try {
								String rasAccessKey = jData
										.getString("accessKey");
								final String accessKey = RSAUtils.decrypt(
										app.config.pbKey0, rasAccessKey);
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										readLocalData(loginPhone, data);
										data.user.phone = loginPhone;
										data.user.accessKey = accessKey;
										app.config.lastLoginPhone = loginPhone;
									}

									@Override
									public void modifyUI() {
										startActivity(new Intent(
												LoginActivity.this,
												MainActivity.class));
										finish();
										Alert.removeLoading();
									}
								});
							} catch (Exception e) {
								Alert.removeLoading();
							}
						}

						@Override
						public void unSuccess(JSONObject jData) {
							Alert.removeLoading();
							try {
								jData.getString(app
										.getString(R.string.network_failed));
								Alert.showMessage(jData
										.getString(getString(R.string.network_failed)));
							} catch (JSONException e) {
							}
							super.unSuccess(jData);
						}

						@Override
						public void failed(int failedType) {
							Alert.removeLoading();
							if (!NetworkUtils.hasNetwork(app)) {
								Alert.showMessage(getString(R.string.alert_text_nointernet));
							} else if (failedType == FAILED_TIMEOUT) {
								Alert.showMessage(getString(R.string.alert_text_nettimeout));
							} else {
								Alert.showMessage(getString(R.string.alert_text_neterror));
							}
							super.failed(failedType);
						}
					};
					Alert.showLoading(new OnLoadingCancelListener() {
						@Override
						public void loadingCancel() {
							mLoginConnection.disConnection();
						}
					});
					app.networkHandler.connection(mLoginConnection);
				} else if (v.equals(leftBottomTextButton)) {
					nextAnimation(Status.verifyPhoneForResetPassword, card,
							card);
					showSoftInputDelay(input1, animationNextIn.getDuration()
							+ animationNextOut.getDuration() + 20);
				} else if (v.equals(rightBottomTextButton)) {
					nextAnimation(Status.verifyPhoneForLogin, card, card);
					if (remainLogin == 0) {
						showSoftInputDelay(input1,
								animationNextIn.getDuration()
										+ animationNextOut.getDuration() + 20);
					} else {

						showSoftInputDelay(input2,
								animationNextIn.getDuration()
										+ animationNextOut.getDuration() + 20);
					}
				}
				break;
			case verifyPhoneForLogin:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.loginUsePassword, card, card);
					if (app.config.lastLoginPhone != null
							&& !app.config.lastLoginPhone.equals("")) {

						showSoftInputDelay(input2,
								animationBackIn.getDuration()
										+ animationBackOut.getDuration() + 20);
					} else {
						showSoftInputDelay(input1,
								animationBackIn.getDuration()
										+ animationBackOut.getDuration() + 20);
					}

				} else if (v.equals(mainButton)) {
					final String phone = input1.getText().toString();
					final String code = input2.getText().toString();
					Pattern p = Pattern
							.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
					Matcher m = p.matcher(phone);
					if (phone.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_phonenotnull));
						showSoftInput(input1);
						return;
					} else if (!m.matches()) {
						Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
						showSoftInput(input1);
						return;
					} else if (code.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_codenotnull));
						showSoftInput(input2);
						return;
					} else {
						hideSoftInput();
					}

					final NetConnection mLoginConnection = new CommonNetConnection() {

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN + API.ACCOUNT_VERIFYCODE;
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", phone);
							params.put("code", code);
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							try {
								final String accessKey = RSAUtils.decrypt(
										app.config.pbKey0,
										jData.getString("accessKey"));
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										data.user.phone = phone;
										data.user.accessKey = accessKey;
										app.config.lastLoginPhone = phone;
									}

									@Override
									public void modifyUI() {
										cancelRemain(Status.verifyPhoneForLogin);
										Alert.removeLoading();
										startActivity(new Intent(
												LoginActivity.this,
												MainActivity.class));
										finish();
									}
								});
							} catch (Exception e) {
								Alert.removeLoading();
							}
						}

						@Override
						public void unSuccess(JSONObject jData) {
							Alert.removeLoading();
							try {
								jData.getString(app
										.getString(R.string.network_failed));
								Alert.showMessage(jData
										.getString(getString(R.string.network_failed)));
							} catch (JSONException e) {
							}
							super.unSuccess(jData);
						}

						@Override
						public void failed(int failedType) {
							Alert.removeLoading();
							if (!NetworkUtils.hasNetwork(app)) {
								Alert.showMessage(getString(R.string.alert_text_nointernet));
							} else if (failedType == FAILED_TIMEOUT) {
								Alert.showMessage(getString(R.string.alert_text_nettimeout));
							} else {
								Alert.showMessage(getString(R.string.alert_text_neterror));
							}
							super.failed(failedType);
						}
					};

					Alert.showLoading(new OnLoadingCancelListener() {

						@Override
						public void loadingCancel() {
							mLoginConnection.disConnection();
						}
					});
					app.networkHandler.connection(mLoginConnection);

				} else if (v.equals(rightBottomTextButton)) {
					final String phone = input1.getText().toString();
					if (remainLogin != 0) {
						return;
					}
					Pattern p = Pattern
							.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
					Matcher m = p.matcher(phone);
					if (phone.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_phonenotnull));
						showSoftInput(input1);
						return;
					} else if (!m.matches()) {
						Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
						showSoftInput(input1);
						return;
					} else {
						hideSoftInput();
					}
					loginPhone = phone;
					final NetConnection mSendCodeConnection = new CommonNetConnection() {

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN + API.ACCOUNT_VERIFYPHONE;
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", phone);
							params.put("usage", "login");
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							startRemain(Status.verifyPhoneForLogin);
							Alert.removeLoading();
							handler.post(new Runnable() {

								@Override
								public void run() {
									input2.requestFocus();
								}
							});
						}

						@Override
						public void unSuccess(JSONObject jData) {
							cancelRemain(Status.verifyPhoneForLogin);
							try {
								jData.getString(app
										.getString(R.string.network_failed));
								Alert.showMessage(jData
										.getString(getString(R.string.network_failed)));
							} catch (JSONException e) {
							}
							Alert.removeLoading();
							super.unSuccess(jData);
						}

						@Override
						public void failed(int failedType) {
							cancelRemain(Status.verifyPhoneForLogin);
							if (!NetworkUtils.hasNetwork(app)) {
								Alert.showMessage(getString(R.string.alert_text_nointernet));
							} else if (failedType == FAILED_TIMEOUT) {
								Alert.showMessage(getString(R.string.alert_text_nettimeout));
							} else {
								Alert.showMessage(getString(R.string.alert_text_neterror));
							}
							Alert.removeLoading();
							super.failed(failedType);
						}
					};
					Alert.showLoading(new OnLoadingCancelListener() {
						@Override
						public void loadingCancel() {
							mSendCodeConnection.disConnection();
							cancelRemain(Status.verifyPhoneForLogin);
						}
					});
					app.networkHandler.connection(mSendCodeConnection);
				}
				break;
			case verifyPhoneForRegister:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.loginOrRegister, loginOrRegister, card);
				} else if (v.equals(mainButton)) {
					final String phone = input1.getText().toString();
					final String code = input2.getText().toString();
					Pattern p = Pattern
							.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
					Matcher m = p.matcher(phone);
					if (phone.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_phonenotnull));
						showSoftInput(input1);
						return;
					} else if (!m.matches()) {
						Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
						showSoftInput(input1);
						return;
					} else if (code.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_codenotnull));
						showSoftInput(input2);
						return;
					} else {
						hideSoftInput();
					}

					final NetConnection registerConnection = new CommonNetConnection() {

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN + API.ACCOUNT_VERIFYCODE;
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", phone);
							params.put("code", code);
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							try {
								final String accessKey = RSAUtils.decrypt(
										app.config.pbKey0,
										jData.getString("accessKey"));
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										data.user.phone = phone;
										data.user.accessKey = accessKey;
									}

									@Override
									public void modifyUI() {
										cancelRemain(Status.verifyPhoneForRegister);
										Alert.removeLoading();
										nextAnimation(Status.setPassword, card,
												card);
										showSoftInputDelay(
												input1,
												animationNextIn.getDuration()
														+ animationNextOut
																.getDuration()
														+ 20);
									}
								});
							} catch (Exception e) {
								Alert.removeLoading();
							}
						}

						@Override
						public void unSuccess(JSONObject jData) {
							Alert.removeLoading();
							try {
								jData.getString(app
										.getString(R.string.network_failed));
								Alert.showMessage(jData
										.getString(getString(R.string.network_failed)));
							} catch (JSONException e) {
							}
							super.unSuccess(jData);
						}

						@Override
						public void failed(int failedType) {
							Alert.removeLoading();
							if (!NetworkUtils.hasNetwork(app)) {
								Alert.showMessage(getString(R.string.alert_text_nointernet));
							} else if (failedType == FAILED_TIMEOUT) {
								Alert.showMessage(getString(R.string.alert_text_nettimeout));
							} else {
								Alert.showMessage(getString(R.string.alert_text_neterror));
							}
							super.failed(failedType);
						}
					};

					Alert.showLoading(new OnLoadingCancelListener() {

						@Override
						public void loadingCancel() {
							registerConnection.disConnection();
						}
					});
					app.networkHandler.connection(registerConnection);
				} else if (v.equals(rightBottomTextButton)) {
					final String phone = input1.getText().toString();
					if (remainRegister != 0) {
						return;
					}
					Pattern p = Pattern
							.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
					Matcher m = p.matcher(phone);
					if (phone.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_phonenotnull));
						showSoftInput(input1);
						return;
					} else if (!m.matches()) {
						Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
						showSoftInput(input1);
						return;
					} else {
						hideSoftInput();
					}
					registerPhone = phone;
					final NetConnection mSendCodeConnection = new CommonNetConnection() {

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN + API.ACCOUNT_VERIFYPHONE;
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", phone);
							params.put("usage", "register");
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							startRemain(Status.verifyPhoneForRegister);
							Alert.removeLoading();
							handler.post(new Runnable() {

								@Override
								public void run() {
									input2.requestFocus();
								}
							});
						}

						@Override
						public void unSuccess(JSONObject jData) {
							cancelRemain(Status.verifyPhoneForRegister);
							try {
								jData.getString(app
										.getString(R.string.network_failed));
								Alert.showMessage(jData
										.getString(getString(R.string.network_failed)));
							} catch (JSONException e) {
							}
							Alert.removeLoading();
							super.unSuccess(jData);
						}

						@Override
						public void failed(int failedType) {
							cancelRemain(Status.verifyPhoneForRegister);
							if (!NetworkUtils.hasNetwork(app)) {
								Alert.showMessage(getString(R.string.alert_text_nointernet));
							} else if (failedType == FAILED_TIMEOUT) {
								Alert.showMessage(getString(R.string.alert_text_nettimeout));
							} else {
								Alert.showMessage(getString(R.string.alert_text_neterror));
							}
							Alert.removeLoading();
							super.failed(failedType);
						}
					};
					Alert.showLoading(new OnLoadingCancelListener() {
						@Override
						public void loadingCancel() {
							mSendCodeConnection.disConnection();
							cancelRemain(Status.verifyPhoneForRegister);
						}
					});
					app.networkHandler.connection(mSendCodeConnection);
				}
				break;
			case verifyPhoneForResetPassword:
				if (v.equals(rightTopTextButton)) {
					backAnimation(Status.loginUsePassword, card, card);
					if (app.config.lastLoginPhone != null
							&& !app.config.lastLoginPhone.equals("")) {

						showSoftInputDelay(input2,
								animationBackIn.getDuration()
										+ animationBackOut.getDuration() + 20);
					} else {
						showSoftInputDelay(input1,
								animationBackIn.getDuration()
										+ animationBackOut.getDuration() + 20);
					}
				} else if (v.equals(mainButton)) {
					final String phone = input1.getText().toString();
					final String code = input2.getText().toString();
					Pattern p = Pattern
							.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
					Matcher m = p.matcher(phone);
					if (phone.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_phonenotnull));
						showSoftInput(input1);
						return;
					} else if (!m.matches()) {
						Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
						showSoftInput(input1);
						return;
					} else if (code.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_codenotnull));
						showSoftInput(input2);
						return;
					} else {
						hideSoftInput();
					}

					final NetConnection resetPasswordConnection = new CommonNetConnection() {

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN + API.ACCOUNT_VERIFYCODE;
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", phone);
							params.put("code", code);
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							try {
								final String accessKey = RSAUtils.decrypt(
										app.config.pbKey0,
										jData.getString("accessKey"));
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										data.user.phone = phone;
										data.user.accessKey = accessKey;
									}

									@Override
									public void modifyUI() {
										cancelRemain(Status.verifyPhoneForResetPassword);
										Alert.removeLoading();
										nextAnimation(Status.resetPassword,
												card, card);
										showSoftInputDelay(
												input1,
												animationNextIn.getDuration()
														+ animationNextOut
																.getDuration()
														+ 20);
									}
								});
							} catch (Exception e) {
								Alert.removeLoading();
							}
						}

						@Override
						public void unSuccess(JSONObject jData) {
							Alert.removeLoading();
							try {
								jData.getString(app
										.getString(R.string.network_failed));
								Alert.showMessage(jData
										.getString(getString(R.string.network_failed)));
							} catch (JSONException e) {
							}
							super.unSuccess(jData);
						}

						@Override
						public void failed(int failedType) {
							Alert.removeLoading();
							if (!NetworkUtils.hasNetwork(app)) {
								Alert.showMessage(getString(R.string.alert_text_nointernet));
							} else if (failedType == FAILED_TIMEOUT) {
								Alert.showMessage(getString(R.string.alert_text_nettimeout));
							} else {
								Alert.showMessage(getString(R.string.alert_text_neterror));
							}
							super.failed(failedType);
						}
					};

					Alert.showLoading(new OnLoadingCancelListener() {

						@Override
						public void loadingCancel() {
							resetPasswordConnection.disConnection();
						}
					});
					app.networkHandler.connection(resetPasswordConnection);
				} else if (v.equals(rightBottomTextButton)) {
					final String phone = input1.getText().toString();
					if (remainResetPassword != 0) {
						return;
					}
					Pattern p = Pattern
							.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
					Matcher m = p.matcher(phone);
					if (phone.equals("")) {
						Alert.showMessage(getString(R.string.alert_text_phonenotnull));
						showSoftInput(input1);
						return;
					} else if (!m.matches()) {
						Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
						showSoftInput(input1);
						return;
					} else {
						hideSoftInput();
					}
					resetPasswordPhone = phone;
					final NetConnection mSendCodeConnection = new CommonNetConnection() {

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN + API.ACCOUNT_VERIFYPHONE;
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", phone);
							params.put("usage", "login");
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							startRemain(Status.verifyPhoneForResetPassword);
							Alert.removeLoading();
							handler.post(new Runnable() {

								@Override
								public void run() {
									input2.requestFocus();
								}
							});
						}

						@Override
						public void unSuccess(JSONObject jData) {
							cancelRemain(Status.verifyPhoneForResetPassword);
							try {
								jData.getString(app
										.getString(R.string.network_failed));
								Alert.showMessage(jData
										.getString(getString(R.string.network_failed)));
							} catch (JSONException e) {
							}
							Alert.removeLoading();
							super.unSuccess(jData);
						}

						@Override
						public void failed(int failedType) {
							cancelRemain(Status.verifyPhoneForResetPassword);
							if (!NetworkUtils.hasNetwork(app)) {
								Alert.showMessage(getString(R.string.alert_text_nointernet));
							} else if (failedType == FAILED_TIMEOUT) {
								Alert.showMessage(getString(R.string.alert_text_nettimeout));
							} else {
								Alert.showMessage(getString(R.string.alert_text_neterror));
							}
							Alert.removeLoading();
							super.failed(failedType);
						}
					};
					Alert.showLoading(new OnLoadingCancelListener() {
						@Override
						public void loadingCancel() {
							mSendCodeConnection.disConnection();
							cancelRemain(Status.verifyPhoneForResetPassword);
						}
					});
					app.networkHandler.connection(mSendCodeConnection);
				}
				break;
			case setPassword:
				if (v.equals(rightTopTextButton)) {
					hideSoftInput();
					Alert.createDialog(LoginActivity.this)
							.setTitle("取消注册?")
							.setOnConfirmClickListener(
									new OnDialogClickListener() {

										@Override
										public void onClick(
												AlertInputDialog dialog) {
											app.dataHandler
													.exclude(new Modification() {

														@Override
														public void modifyData(
																Data data) {
															data.user.accessKey = "";
														}

														@Override
														public void modifyUI() {
															backAnimation(
																	Status.verifyPhoneForRegister,
																	card, card);
															showSoftInputDelay(
																	input1,
																	animationBackIn
																			.getDuration()
																			+ animationBackOut
																					.getDuration()
																			+ 20);
														}
													});
										}
									}).show();
				} else if (v.equals(mainButton)) {
					final String password = input1.getText().toString();
					String password2 = input2.getText().toString();
					if (password.equals("")) {
						Alert.showMessage("请输入密码");
						showSoftInput(input1);
					} else if (password.length() < 6) {
						Alert.showMessage(getString(R.string.alert_text_passlength));
						showSoftInput(input1);
					} else if (password2.equals("")) {
						Alert.showMessage("请确认密码");
						showSoftInput(input2);
					} else if (!password.equals(password2)) {
						Alert.showMessage("两次输入的密码不一致");
						showSoftInput(input2);
					} else {
						hideSoftInput();
						final NetConnection mSetPass = new CommonNetConnection() {

							@Override
							protected void settings(Settings settings) {
								settings.url = API.DOMAIN + API.ACCOUNT_MODIFY;
								Map<String, String> params = new HashMap<String, String>();
								params.put("phone", app.data.user.phone);
								params.put("accessKey", app.data.user.accessKey);
								params.put(
										"account",
										"{\"password\":\""
												+ app.mSHA1
														.getDigestOfString(password
																.getBytes())
												+ "\"}");
								settings.params = params;
							}

							@Override
							public void success(JSONObject jData) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										startActivity(new Intent(
												LoginActivity.this,
												MainActivity.class));
										finish();
									}
								});
								Alert.removeLoading();
							}

							@Override
							public void unSuccess(JSONObject jData) {
								Alert.removeLoading();
								super.unSuccess(jData);
							}

							@Override
							public void failed(int failedType) {
								Alert.removeLoading();
								super.failed(failedType);
							}

						};

						Alert.showLoading(new OnLoadingCancelListener() {

							@Override
							public void loadingCancel() {
								mSetPass.disConnection();
							}
						});

						app.networkHandler.connection(mSetPass);
					}
				}
				break;
			case resetPassword:
				if (v.equals(rightTopTextButton)) {
					hideSoftInput();
					Alert.createDialog(LoginActivity.this)
							.setTitle("取消重置密码?")
							.setOnConfirmClickListener(
									new OnDialogClickListener() {

										@Override
										public void onClick(
												AlertInputDialog dialog) {
											app.dataHandler
													.exclude(new Modification() {

														@Override
														public void modifyData(
																Data data) {
															data.user.accessKey = "";
														}

														@Override
														public void modifyUI() {
															backAnimation(
																	Status.loginUsePassword,
																	card, card);
															if (app.config.lastLoginPhone != null
																	&& !app.config.lastLoginPhone
																			.equals("")) {
																showSoftInputDelay(
																		input2,
																		animationBackIn
																				.getDuration()
																				+ animationBackOut
																						.getDuration()
																				+ 20);
															} else {
																showSoftInputDelay(
																		input1,
																		animationBackIn
																				.getDuration()
																				+ animationBackOut
																						.getDuration()
																				+ 20);
															}
														}
													});
										}
									}).show();
				} else if (v.equals(mainButton)) {
					final String password = input1.getText().toString();
					String password2 = input2.getText().toString();
					if (password.equals("")) {
						Alert.showMessage("请输入密码");
						showSoftInput(input1);
					} else if (password.length() < 6) {
						Alert.showMessage(getString(R.string.alert_text_passlength));
						showSoftInput(input1);
					} else if (password2.equals("")) {
						Alert.showMessage("请确认密码");
						showSoftInput(input2);
					} else if (!password.equals(password2)) {
						Alert.showMessage("两次输入的密码不一致");
						showSoftInput(input2);
					} else {
						hideSoftInput();

						final NetConnection resetPasswordConnection = new CommonNetConnection() {

							@Override
							protected void settings(Settings settings) {
								settings.url = API.DOMAIN + API.ACCOUNT_MODIFY;
								Map<String, String> params = new HashMap<String, String>();
								params.put("phone", app.data.user.phone);
								params.put("accessKey", app.data.user.accessKey);
								String passwd = app.mSHA1
										.getDigestOfString(password.getBytes());
								params.put("account", "{\"password\":\""
										+ passwd + "\"}");
								settings.params = params;
							}

							@Override
							public void success(JSONObject jData) {
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										String phone = data.user.phone;
										String accessKey = data.user.accessKey;
										readLocalData(phone, data);
										data.user.phone = phone;
										data.user.accessKey = accessKey;
										app.config.lastLoginPhone = loginPhone;
									}

									@Override
									public void modifyUI() {
										startActivity(new Intent(
												LoginActivity.this,
												MainActivity.class));
										finish();
										Alert.removeLoading();
									}
								});
							}

							@Override
							public void unSuccess(JSONObject jData) {
								Alert.removeLoading();
								super.unSuccess(jData);
							}

							@Override
							public void failed(int failedType) {
								Alert.removeLoading();
								super.failed(failedType);
							}

						};

						Alert.showLoading(new OnLoadingCancelListener() {

							@Override
							public void loadingCancel() {
								resetPasswordConnection.disConnection();
							}
						});

						app.networkHandler.connection(resetPasswordConnection);
					}
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
