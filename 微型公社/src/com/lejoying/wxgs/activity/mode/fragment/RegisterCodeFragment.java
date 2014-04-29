package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.LoginModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.utils.RSAUtils;

public class RegisterCodeFragment extends BaseFragment implements
		OnClickListener {
	MainApplication app = MainApplication.getMainApplication();

	View mContentView;
	LoginModeManager mLoginMode;

	private EditText mView_code;
	private Button mView_next;
	private TextView mView_sendcode;
	private TextView mView_phone;

	String mCode;

	public static String accessKey;

	private View backButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_register_code, null);
		mView_code = (EditText) mContentView.findViewById(R.id.register_code);
		mView_next = (Button) mContentView.findViewById(R.id.button_next);
		mView_phone = (TextView) mContentView.findViewById(R.id.sendtophone);
		mView_sendcode = (TextView) mContentView
				.findViewById(R.id.button_resend);
		mView_phone.setText(app.data.user.phone);
		mView_next.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		mView_code.setText("");

		backButton = mContentView.findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoginMode.back();
			}
		});

		mRemainListener = new RemainListener() {
			@Override
			public void onRemain(final int remain) {
				app.UIHandler.post(new Runnable() {

					@Override
					public void run() {
						if (remain != 0) {
							mView_sendcode
									.setText(getString(R.string.button_resend)
											+ "(" + remain + ")");
						} else {
							mView_sendcode
									.setText(getString(R.string.button_resend));
						}
					}
				});
			}
		};

		return mContentView;
	}

	@Override
	public void onDestroyView() {
		mRemainListener = null;
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setMode(LoginModeManager loginMode) {
		this.mLoginMode = loginMode;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next:
			mCode = mView_code.getText().toString();
			if (mCode == null || mCode.equals("")) {
				Alert.showMessage(getString(R.string.alert_text_codenotnull));
				return;
			}
			hideSoftInput();
			final NetConnection mVerifyCode = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.ACCOUNT_VERIFYCODE;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("code", mCode);
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					try {
						accessKey = RSAUtils.decrypt(app.config.pbKey0,
								jData.getString("accessKey"));
						mLoginMode.showNext(mLoginMode.mRegisterPassFragment);
						Alert.removeLoading();
						cancelRemain();
					} catch (Exception e) {
						Alert.removeLoading();
					}
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
					mVerifyCode.disConnection();
				}
			});
			app.networkHandler.connection(mVerifyCode);
			break;
		case R.id.button_resend:
			if (remain != 0) {
				return;
			}
			remain = 60;
			final NetConnection mVerifyPhone = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.ACCOUNT_VERIFYPHONE;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("usage", "register");
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					startRemain();
				}

				@Override
				public void unSuccess(JSONObject jData) {
					cancelRemain();
					super.unSuccess(jData);
				}

				@Override
				public void failed(int failedType) {
					cancelRemain();
					super.failed(failedType);
				}
			};
			app.networkHandler.connection(mVerifyPhone);
			break;
		default:
			break;
		}
	}

	public static int remain;

	interface RemainListener {
		public void onRemain(int remain);
	}

	public static RemainListener mRemainListener;

	static Timer timer;

	public static void cancelRemain() {
		if (timer != null) {
			timer.cancel();
		}
		timer = null;
		remain = 0;
		if (mRemainListener != null) {
			mRemainListener.onRemain(remain);
		}
	}

	public static void startRemain() {
		timer = new Timer();
		remain = 60;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				remain--;
				if (mRemainListener != null) {
					mRemainListener.onRemain(remain);
				}
				if (remain <= 0) {
					remain = 0;
					timer.cancel();
					return;
				}
			}
		}, 0, 1000);
	}

}
