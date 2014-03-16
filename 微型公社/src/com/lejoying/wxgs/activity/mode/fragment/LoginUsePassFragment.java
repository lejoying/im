package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

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

public class LoginUsePassFragment extends BaseFragment implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();

	View mContentView;
	LoginModeManager mLoginMode;

	private EditText mView_phone;
	private EditText mView_pass;
	private Button mView_login;
	private Button mView_register;
	private TextView mView_clogin;

	String mLoginPhone;
	String mLoginPass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		app.dataHandler.exclude(new Modification() {
			@Override
			public void modifyData(Data data) {
				data.clear();
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_login_pass, null);

		mView_phone = (EditText) mContentView.findViewById(R.id.plogin_phone);
		mView_pass = (EditText) mContentView.findViewById(R.id.plogin_pass);
		mView_login = (Button) mContentView.findViewById(R.id.button_plogin);
		mView_register = (Button) mContentView
				.findViewById(R.id.button_register);
		mView_clogin = (TextView) mContentView
				.findViewById(R.id.button_openclogin);

		if (!app.config.lastLoginPhone.equals("")) {
			mView_phone.setText(app.config.lastLoginPhone);
		}

		mView_login.setOnClickListener(this);
		mView_register.setOnClickListener(this);
		mView_clogin.setOnClickListener(this);

		return mContentView;
	}

	@Override
	public void onResume() {
		CircleMenu.hide();
		super.onResume();
	}

	public void setMode(LoginModeManager loginMode) {
		this.mLoginMode = loginMode;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_plogin:
			mLoginPhone = mView_phone.getText().toString();
			mLoginPass = mView_pass.getText().toString();
			if (mLoginPhone.equals("")) {
				Alert.showMessage(

				getActivity().getString(R.string.alert_text_phonenotnull));
				View mCurrentFocus = getActivity().getCurrentFocus();
				if (mCurrentFocus != null) {
					mCurrentFocus.clearFocus();
				}
				showSoftInput(mView_phone);
				return;
			}
			if (mLoginPass.equals("")) {
				Alert.showMessage(getActivity().getString(
						R.string.alert_text_passnotnull));
				showSoftInput(mView_pass);
				return;
			}

			hideSoftInput();

			final NetConnection mLoginConnection = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.ACCOUNT_AUTH;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", mLoginPhone);
					params.put("password",
							app.mSHA1.getDigestOfString(mLoginPass.getBytes()));
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					try {
						String rasAccessKey = jData.getString("accessKey");
						final String accessKey = RSAUtils.decrypt(
								app.config.pbKey0, rasAccessKey);
						app.dataHandler.exclude(new Modification() {
							@Override
							public void modifyData(Data data) {
								data.user.phone = mLoginPhone;
								data.user.accessKey = accessKey;
							}

							@Override
							public void modifyUI() {
								MainActivity.instance.switchMode();
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
					mLoginConnection.disConnection();
				}
			});

			app.networkHandler.connection(mLoginConnection);

			break;
		case R.id.button_register:
			mLoginMode.showNext(mLoginMode.mRegisterPhoneFragment);
			break;
		case R.id.button_openclogin:
			mLoginMode.showNext(mLoginMode.mLoginUseCodeFragment);
			break;

		default:
			break;
		}
	}
}
