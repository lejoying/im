package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.BaseModeManager.KeyDownListener;
import com.lejoying.wxgs.activity.mode.LoginModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class RegisterPassFragment extends BaseFragment implements
		OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	View mContentView;
	LoginModeManager mLoginMode;
	private EditText mView_pass;
	private Button mView_next;

	String mPass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_register_pass, null);
		mView_pass = (EditText) mContentView.findViewById(R.id.register_pass);
		mView_next = (Button) mContentView.findViewById(R.id.button_next);
		mView_next.setOnClickListener(this);

		mView_pass.setText("");

		mLoginMode.setKeyDownListener(new KeyDownListener() {

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				Alert.createDialog(getActivity()).setTitle("确定要退出注册码？")
						.setOnConfirmClickListener(new OnDialogClickListener() {
							@Override
							public void onClick(AlertInputDialog dialog) {
								mLoginMode.clearBackStack(1);
								mLoginMode.back();
								RegisterCodeFragment.accessKey = null;
								mLoginMode.setKeyDownListener(null);
							}
						}).show();

				return false;
			}
		});

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
		case R.id.button_next:
			mPass = mView_pass.getText().toString();
			if (mPass == null || mPass.equals("")) {
				Alert.showMessage(getString(R.string.alert_text_passnotnull));
				return;
			}
			if (mPass.length() < 6) {
				Alert.showMessage(getString(R.string.alert_text_passlength));
				return;
			}

			final NetConnection mSetPass = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.ACCOUNT_MODIFY;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", RegisterCodeFragment.accessKey);
					params.put("account", "{\"password\":\"" + app.mSHA1.getDigestOfString(mPass.getBytes()) + "\"}");
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					app.dataHandler.exclude(new Modification() {
						@Override
						public void modifyData(Data data) {
							data.user.accessKey = RegisterCodeFragment.accessKey;
							RegisterCodeFragment.accessKey = null;
						}

						@Override
						public void modifyUI() {
							MainActivity.instance.switchMode();
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
					mSetPass.disConnection();
				}
			});

			app.networkHandler.connection(mSetPass);

			break;

		default:
			break;
		}
	}
}
