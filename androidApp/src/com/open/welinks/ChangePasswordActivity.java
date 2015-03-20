package com.open.welinks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.welink.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.SHA1;
import com.open.welinks.view.ViewManage;

public class ChangePasswordActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ChangePasswordActivity";
	public MyLog log = new MyLog(tag, true);

	LayoutInflater mInflater;

	View saveButton;
	View cancleButton;
	EditText initialPasswordView;
	EditText confirmPasswordView;
	EditText modifyChangePassword;
	ImageView backCardView;

	public RelativeLayout backView;
	public RelativeLayout rightContainer;
	public TextView backTitleView;
	public TextView rightTopButton;

	public OnClickListener mOnClickListener;

	public ViewManage viewManage = ViewManage.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewManage.changePasswordActivity = this;
		setContentView(R.layout.activity_modifypassword);
		initData();
		initializeListeners();
		bindEvent();
	}

	@Override
	public void finish() {
		viewManage.changePasswordActivity = null;
		super.finish();
	}

	public void modifySuccess() {
		Toast.makeText(ChangePasswordActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
		finish();
	}

	public void modifyFailed() {
		Toast.makeText(ChangePasswordActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
	}

	void initData() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		backView = (RelativeLayout) findViewById(R.id.backView);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		backTitleView.setText("修改密码");
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		saveButton = findViewById(R.id.cp_rl_save);
		cancleButton = findViewById(R.id.cp_rl_cancel);
		initialPasswordView = (EditText) findViewById(R.id.cp_et_initialpwd);
		confirmPasswordView = (EditText) findViewById(R.id.cp_et_confirmpwd);
		modifyChangePassword = (EditText) findViewById(R.id.cp_et_modifychangepwd);
		backCardView = (ImageView) findViewById(R.id.panel_right_button);
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@SuppressLint("DefaultLocale")
			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(saveButton)) {
					// String initialpwd = cp_et_initialpwd.getText().toString().trim();
					String changepwd = modifyChangePassword.getText().toString().trim();
					String confirmpwd = confirmPasswordView.getText().toString().trim();
					if (!"".equals(changepwd) && !"".equals(confirmpwd)) {// !"".equals(initialpwd) &&
						if (changepwd.equals(confirmpwd)) {
							SHA1 sha1 = new SHA1();
							modifyPassword("", sha1.getDigestOfString(confirmpwd.getBytes()).toLowerCase());
						} else {
							Toast.makeText(ChangePasswordActivity.this, "输入2次密码不一致", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(ChangePasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					}
				} else if (view.equals(cancleButton)) {
					finish();
				}
			}
		};
	}

	public void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
		this.saveButton.setOnClickListener(mOnClickListener);
		this.cancleButton.setOnClickListener(mOnClickListener);
	}

	public void modifyPassword(String oldPassword, String newPassword) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		parser.check();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		// params.addBodyParameter("oldpassword", oldPassword);
		params.addBodyParameter("account", "{\"password\":\"" + newPassword + "\"}");
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_MODIFY, params, responseHandlers.account_modify);
	}
}
