package com.open.welinks.controller;

import java.util.HashMap;
import java.util.Map;

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
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.open.welinks.MainActivity;
import com.open.welinks.model.Data;
import com.open.welinks.utils.CommonNetConnection;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.utils.NetworkHandler.Settings;
import com.open.welinks.utils.SHA1;
import com.open.welinks.view.LoginView;
import com.open.welinks.view.LoginView.Status;

public class LoginController {
	public Data data = Data.getInstance();
	public String tag = "LoginController";

	public Runnable animationRunnable;

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
				} else if (view.equals(thisView.registerButton)) {
					nextAnimation(Status.verifyPhoneForRegister, thisView.card, thisView.loginOrRegister);
				} else if (view.equals(thisView.mainButton)) {
					if (thisView.status == Status.loginUsePassword) {
						String loginPhone = thisView.input1.getText().toString().trim();
						String loginPass = thisView.input2.getText().toString().trim();
						if ("".equals(loginPhone) || "".equals(loginPass)) {
							Toast.makeText(context, "帐号和密码不能为空", Toast.LENGTH_LONG).show();
							return;
						}
						requestUserAuth(loginPhone, loginPass);
					} else {
						Toast.makeText(context, "尚未处理", Toast.LENGTH_LONG).show();
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
	}

	public void requestUserAuth(final String loginPhone, final String loginPass) {
		mNetworkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				Intent intent = new Intent(thisActivity, MainActivity.class);
				intent.putExtra("phone", loginPhone);
				thisActivity.startActivity(intent);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = url_userauth;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", loginPhone);
				String passwd = msSha1.getDigestOfString(loginPass.getBytes());
				params.put("password", passwd);
				settings.params = params;
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				super.unSuccess(jData);
			}
		});
	}
}
