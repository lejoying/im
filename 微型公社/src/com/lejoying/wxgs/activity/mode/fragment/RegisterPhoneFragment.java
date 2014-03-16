package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;
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

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BaseActivity;
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

public class RegisterPhoneFragment extends BaseFragment implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();

	View mContentView;
	LoginModeManager mLoginMode;

	private EditText mView_phone;
	private Button mView_next;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_register_phone, null);
		mView_phone = (EditText) mContentView.findViewById(R.id.register_phone);
		mView_next = (Button) mContentView.findViewById(R.id.button_next);
		mView_next.setOnClickListener(this);
		return mContentView;
	}

	@Override
	public void onResume() {
		CircleMenu.showBack((BaseActivity) getActivity());
		if (app.data.user.phone != null) {
			mView_phone.setText(app.data.user.phone);
		}
		super.onResume();
	}

	public void setMode(LoginModeManager loginMode) {
		this.mLoginMode = loginMode;
	}

	String mPhone;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next:
			mPhone = mView_phone.getText().toString();

			if (mPhone == null || mPhone.equals("")) {
				Alert.showMessage(getString(R.string.alert_text_phonenotnull));
				showSoftInput(mView_phone);
				return;
			}

			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mPhone);
			if (!m.matches()) {
				Alert.showMessage(getString(R.string.alert_text_phoneformaterror));
				showSoftInput(mView_phone);
				return;
			}

			hideSoftInput();

			if (mPhone.equals(app.data.user.phone)) {
				mLoginMode.showNext(mLoginMode.mRegisterCodeFragment);
				return;
			} else {
				RegisterCodeFragment.cancelRemain();
			}

			final NetConnection mVerifyPhone = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.ACCOUNT_VERIFYPHONE;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", mPhone);
					params.put("usage", "register");
					settings.params = params;
				}

				@Override
				protected void success(JSONObject jData) {
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							data.user.phone = mPhone;
						}

						@Override
						public void modifyUI() {
							RegisterCodeFragment.startRemain();
							mLoginMode
									.showNext(mLoginMode.mRegisterCodeFragment);
							Alert.removeLoading();

						}
					});
				}

				@Override
				protected void failed() {
					Alert.removeLoading();
					RegisterCodeFragment.cancelRemain();
				}
			};

			Alert.showLoading(new OnLoadingCancelListener() {
				@Override
				public void loadingCancel() {
					mVerifyPhone.disConnection();
				}
			});

			app.networkHandler.connection(mVerifyPhone);

			break;

		default:
			break;
		}
	}
}
