package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.mc.MainActivity;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.User;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;
import com.lejoying.utils.RSAUtils;
import com.lejoying.utils.SHA1;

public class LoginUsePassFragment extends BaseFragment implements
		OnClickListener {

	App app = App.getInstance();

	private View mContent;

	private EditText mView_phone;
	private EditText mView_pass;
	private Button mView_login;
	private Button mView_register;
	private TextView mView_clogin;
	private SHA1 mSha1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		app.dataHandler.modifyData(new Modification() {
			@Override
			public void modify(Data data) {
				data.user = new User();
				data.circles = new ArrayList<Circle>();
				data.friends = new Hashtable<String, Friend>();

				// Last messages list
				data.lastChatFriends = new ArrayList<String>();

				// new friends
				data.newFriends = new ArrayList<Friend>();

				data.nowChatFriend = null;
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContent = inflater.inflate(R.layout.f_plogin, null);

		mView_phone = (EditText) mContent.findViewById(R.id.et_plogin_phone);
		mView_pass = (EditText) mContent.findViewById(R.id.et_plogin_pass);
		mView_login = (Button) mContent.findViewById(R.id.btn_login);
		mView_register = (Button) mContent.findViewById(R.id.btn_register);
		mView_clogin = (TextView) mContent.findViewById(R.id.tv_clogin);

		if (!app.config.lastLoginPhone.equals("none")) {
			mView_phone.setText(app.config.lastLoginPhone);
		}

		mView_login.setOnClickListener(this);
		mView_register.setOnClickListener(this);
		mView_clogin.setOnClickListener(this);

		mSha1 = new SHA1();

		return mContent;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_clogin:
			mMCFragmentManager.replaceToContent(new LoginUseCodeFragment(),
					true);
			break;
		case R.id.btn_login:
			final String phone = mView_phone.getText().toString();
			String pass = mView_pass.getText().toString();
			if (phone.equals("")) {
				getString(R.string.app_phonenotnull);
				showSoftInput(mView_phone);
				return;
			}
			if (pass.equals("")) {
				getString(R.string.app_passnotnull);
				showSoftInput(mView_pass);
				return;
			}
			final Bundle params = new Bundle();
			params.putString("phone", phone);
			pass = mSha1.getDigestOfString(pass.getBytes());
			params.putString("password", pass);

			MCNetUtils.ajax(new AjaxAdapter() {

				@Override
				public void setParams(Settings settings) {
					settings.url = API.ACCOUNT_AUTH;
					settings.params = params;
				}

				@Override
				public void onSuccess(final JSONObject jData) {
					app.dataHandler.modifyData(new Modification() {

						@Override
						public void modify(Data data) {
							try {
								String accessKey = jData.getString("accessKey");
								accessKey = RSAUtils.decrypt(app.config.pbKey0,
										accessKey);
								data.user.phone = phone;
								data.user.accessKey = accessKey;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, new UIModification() {
						@Override
						public void modifyUI() {
							try {
								jData.getString(getString(R.string.app_reason));
								return;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mMCFragmentManager.startToActivity(
									MainActivity.class, true);

						}
					});
				}
			});
			break;
		case R.id.btn_register:
			mMCFragmentManager.replaceToContent(new RegisterPhoneFragment(),
					true);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_phone;
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.loginUsePassFragment;
	}

}
