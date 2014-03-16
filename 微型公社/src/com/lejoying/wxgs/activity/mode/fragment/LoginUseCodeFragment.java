package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.lejoying.wxgs.activity.BaseActivity;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.LoginModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.utils.RSAUtils;

public class LoginUseCodeFragment extends BaseFragment implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();

	View mContentView;
	LoginModeManager mLoginMode;

	private EditText mView_phone;
	private EditText mView_code;
	private Button mView_login;
	private TextView mView_sendcode;

	boolean init;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		init = false;
		mContentView = inflater.inflate(R.layout.fragment_login_code, null);

		mView_phone = (EditText) mContentView.findViewById(R.id.clogin_phone);
		mView_code = (EditText) mContentView.findViewById(R.id.clogin_code);
		mView_login = (Button) mContentView.findViewById(R.id.button_clogin);
		mView_sendcode = (TextView) mContentView
				.findViewById(R.id.button_sendcode);

		mView_login.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		if (remain != 0) {
			mView_sendcode.setText(getString(R.string.button_resend) + "("
					+ remain + ")");
		}
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
									.setText(getString(R.string.button_sendcode));
						}
					}
				});
			}
		};

		return mContentView;
	}

	@Override
	public void onResume() {
		CircleMenu.showBack((BaseActivity) getActivity());
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		mRemainListener = null;
		super.onDestroyView();
	}

	public void setMode(LoginModeManager loginMode) {
		this.mLoginMode = loginMode;
	}

	String mPhone;
	String mCode;

	@Override
	public void onClick(View v) {
		mPhone = mView_phone.getText().toString();
		mCode = mView_code.getText().toString();
		if (mView_phone == null || mView_phone.equals("")) {
			Alert.showMessage(getString(R.string.alert_text_phonenotnull));
			return;
		}
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mPhone);
		if (!m.matches()) {
			Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
			mView_code.setText("");
			showSoftInput(mView_phone);
			return;
		}
		switch (v.getId()) {
		case R.id.button_clogin:
			if (mCode == null || mCode.equals("")) {
				Alert.showMessage(getString(R.string.alert_text_codenotnull));
				showSoftInput(mView_code);
				return;
			}

			hideSoftInput();

			final NetConnection mLoginConnection = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.ACCOUNT_VERIFYCODE;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", mPhone);
					params.put("code", mCode);
					settings.params = params;
				}

				@Override
				protected void success(JSONObject jData) {
					try {
						final String accessKey = RSAUtils
								.decrypt(app.config.pbKey0,
										jData.getString("accessKey"));
						app.dataHandler.exclude(new Modification() {
							@Override
							public void modifyData(Data data) {
								data.user.phone = mPhone;
								data.user.accessKey = accessKey;
							}

							@Override
							public void modifyUI() {
								MainActivity.instance.switchMode();
								Alert.removeLoading();
								cancelRemain();
							}
						});
					} catch (Exception e) {
						Alert.removeLoading();
					}
				}

				@Override
				protected void failed() {
					Alert.removeLoading();
					cancelRemain();
				}
			};

			Alert.showLoading(new OnLoadingCancelListener() {

				@Override
				public void loadingCancel() {
					mLoginConnection.disConnection();
				}
			});
			app.networkHandler.connection(mLoginConnection);

			break;
		case R.id.button_sendcode:
			if (remain != 0) {
				return;
			}
			remain = 60;
			NetConnection mSendCodeConnection = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.ACCOUNT_VERIFYPHONE;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", mPhone);
					params.put("usage", "login");
					settings.params = params;
				}

				@Override
				protected void success(JSONObject jData) {
					startRemain();
				}

				@Override
				protected void failed() {
					cancelRemain();
				}
			};

			app.networkHandler.connection(mSendCodeConnection);

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
