package com.lejoying.mc.fragment;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.widget.Toast;

import com.lejoying.mc.MainActivity;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;
import com.lejoying.utils.RSAUtils;

public class LoginUseCodeFragment extends BaseFragment implements
		OnClickListener {

	App app = App.getInstance();

	private View mContent;

	private EditText mView_phone;
	private EditText mView_code;
	private Button mView_login;
	private TextView mView_sendcode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_clogin, null);

		mView_phone = (EditText) mContent.findViewById(R.id.et_clogin_phone);
		mView_code = (EditText) mContent.findViewById(R.id.et_clogin_code);
		mView_login = (Button) mContent.findViewById(R.id.btn_login);
		mView_sendcode = (TextView) mContent.findViewById(R.id.tv_sendcode);

		mView_login.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);
		tv_resend = mView_sendcode;

		if (remainTime != 0) {
			mView_phone.setText(remainPhone);
		}

		return mContent;
	}

	@Override
	public void onPause() {
		modifyView = false;
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		final String phone = mView_phone.getText().toString();
		String code = mView_code.getText().toString();
		if (phone == null || phone.equals("")) {
			Toast.makeText(getActivity(), getString(R.string.app_phonenotnull),
					Toast.LENGTH_SHORT).show();
			return;
		}
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(phone);
		if (!m.matches()) {
			Toast.makeText(getActivity(), "手机号码格式不正确", Toast.LENGTH_SHORT)
					.show();
			showSoftInput(mView_phone);
			return;
		}
		switch (v.getId()) {
		case R.id.btn_login:
			if (code == null || code.equals("")) {
				getString(R.string.app_codenotnull);
				return;
			}
			final Bundle Loginparams = new Bundle();
			Loginparams.putString("phone", phone);
			Loginparams.putString("code", code);
			MCNetUtils.ajax(new AjaxAdapter() {

				@Override
				public void setParams(Settings settings) {
					settings.url = API.ACCOUNT_VERIFYCODE;
					settings.params = Loginparams;
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
		case R.id.tv_sendcode:
			if (phone == null || phone.equals("")) {
				Toast.makeText(getActivity(),
						getString(R.string.app_phonenotnull),
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (remainTime != 0) {
				return;
			}

			final Bundle params = new Bundle();
			params.putString("phone", phone);
			params.putString("usage", "login");
			startRemain(phone);
			MCNetUtils.ajax(new AjaxAdapter() {

				@Override
				public void setParams(Settings settings) {
					settings.url = API.ACCOUNT_VERIFYPHONE;
					settings.params = params;
				}

				@Override
				public void onSuccess(final JSONObject jData) {
					try {
						final String failed = jData
								.getString(getString(R.string.app_reason));
						app.mUIThreadHandler.post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(getActivity(), failed,
										Toast.LENGTH_SHORT).show();
							}
						});
						return;
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

			break;
		default:
			break;
		}
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_phone;
	}

	@Override
	public void onResume() {
		modifyView = true;
		super.onResume();
	}

	public static Timer timer;
	public static String remainPhone;
	public static int remainTime;
	public static boolean modifyView;
	public static TextView tv_resend;

	public static void startRemain(String phone) {
		remainPhone = phone;
		remainTime = 0;
		if (remainTime == 0) {
			remainTime = 60;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				App app = App.getInstance();

				@Override
				public void run() {
					remainTime--;
					if (remainTime <= 0) {
						timer.cancel();
						if (modifyView && tv_resend != null) {
							app.mUIThreadHandler.post(new Runnable() {
								@Override
								public void run() {
									tv_resend.setText(app.context
											.getString(R.string.tv_resend));

								}
							});
						}
					} else {
						if (modifyView && tv_resend != null) {
							app.mUIThreadHandler.post(new Runnable() {
								@Override
								public void run() {
									tv_resend.setText(app.context
											.getString(R.string.tv_resend)
											+ "(" + remainTime + ")");
								}
							});
						}
					}
				}
			}, 0, 1000);
		}
	}

	public static void stopRemain() {
		if (timer != null) {
			timer.cancel();
		}
		remainTime = 0;
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.loginUseCodeFragment;
	}

}
