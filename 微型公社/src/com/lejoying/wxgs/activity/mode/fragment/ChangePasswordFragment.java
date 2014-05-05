package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class ChangePasswordFragment extends BaseFragment implements
		OnClickListener {
	View mContent;
	LayoutInflater mInflater;

	View cp_rl_save;
	View cp_rl_cancel;
	EditText cp_et_initialpwd;
	EditText cp_et_confirmpwd;
	EditText cp_et_modifychangepwd;
	ImageView backCardView;

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = inflater.inflate(R.layout.f_changepwd, null);
		mContent.setBackgroundDrawable(new BitmapDrawable(
				app.fileHandler.bitmaps.get(app.data.user.userBackground)));
		initData();
		return mContent;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	void initData() {
		cp_rl_save = mContent.findViewById(R.id.cp_rl_save);
		cp_rl_cancel = mContent.findViewById(R.id.cp_rl_cancel);
		cp_et_initialpwd = (EditText) mContent
				.findViewById(R.id.cp_et_initialpwd);
		cp_et_confirmpwd = (EditText) mContent
				.findViewById(R.id.cp_et_confirmpwd);
		cp_et_modifychangepwd = (EditText) mContent
				.findViewById(R.id.cp_et_modifychangepwd);
		backCardView = (ImageView) mContent
				.findViewById(R.id.panel_right_button);
		backCardView.setOnClickListener(this);
		cp_rl_save.setOnClickListener(this);
		cp_rl_cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cp_rl_save:
			String initialpwd = cp_et_initialpwd.getText().toString().trim();
			String changepwd = cp_et_modifychangepwd.getText().toString()
					.trim();
			String confirmpwd = cp_et_confirmpwd.getText().toString().trim();
			if (!"".equals(initialpwd) && !"".equals(changepwd)
					&& !"".equals(confirmpwd)) {
				if (changepwd.equals(confirmpwd)) {
					modifyPassword(
							app.mSHA1.getDigestOfString(initialpwd.getBytes()),
							app.mSHA1.getDigestOfString(confirmpwd.getBytes()));
				} else {
					Alert.showMessage("输入2次密码不一致");
				}
			} else {
				Alert.showMessage("密码不能为空");
			}
			break;

		case R.id.cp_rl_cancel:
			mMainModeManager.back();
			break;
		case R.id.panel_right_button:
			mMainModeManager.back();
			break;
		default:
			break;
		}
	}

	public void modifyPassword(final String oldPassword,
			final String newPassword) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_MODIFY;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("oldpassword", oldPassword);
				JSONObject account = new JSONObject();
				try {
					account.put("password", newPassword);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				params.put("account", account.toString());
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				mMainModeManager.back();
			}
		});
	}
}
