package com.lejoying.mc.fragment;

import java.util.Timer;
import java.util.TimerTask;

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

import com.lejoying.mc.LoginActivity;
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

public class RegisterCodeFragment extends BaseFragment implements
		OnClickListener {
	App app = App.getInstance();
	private View mContent;
	private EditText mView_code;
	private Button mView_next;
	private TextView mView_sendcode;
	private TextView mView_phone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		startRemain();
		super.onResume();
	}

	public Timer mTimer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_registercode, null);
		mView_code = (EditText) mContent.findViewById(R.id.et_code);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);
		mView_phone = (TextView) mContent.findViewById(R.id.tv_phone);
		mView_sendcode = (TextView) mContent.findViewById(R.id.tv_sendcode);
		mView_phone.setText(app.data.registerBundle.getString("phone"));
		mView_next.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		return mContent;
	}

	public void startRemain() {
		if (LoginActivity.remainTime != 0) {
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					app.mUIThreadHandler.post(new Runnable() {
						int remainTime = LoginActivity.remainTime;

						@Override
						public void run() {
							remainTime--;
							if (isAdded()) {
								if (remainTime <= 0) {
									mView_sendcode
											.setText(getString(R.string.tv_resend));
								} else {
									mView_sendcode
											.setText(getString(R.string.tv_resend)
													+ "(" + remainTime + ")");
								}
							}
						}
					});
				}
			}, 0, 1000);
		} else {
			mView_sendcode.setText(getString(R.string.tv_resend));
		}
	}

	@Override
	public void onPause() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			String code = mView_code.getText().toString();
			if (code.equals("")) {
				Toast.makeText(getActivity(),
						getString(R.string.app_codenotnull), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			next(code);
			break;
		case R.id.tv_sendcode:
			final Bundle params = app.data.registerBundle;
			params.remove("code");
			if (LoginActivity.remainTime == 0) {
				LoginActivity.startRemain();
				startRemain();
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
						app.dataHandler.modifyData(new Modification() {

							@Override
							public void modify(Data data) {
								try {
									app.data.registerBundle.putString("code",
											jData.getString("code"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, new UIModification() {

							@Override
							public void modifyUI() {
								LoginActivity.startRemain();
								startRemain();
							}
						});
					}
				});
			}
			break;
		default:
			break;
		}
	}

	public void next(String code) {
		final Bundle nextParams = new Bundle();
		nextParams.putString("phone",
				app.data.registerBundle.getString("phone"));
		nextParams.putString("code", code);

		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.ACCOUNT_VERIFYCODE;
				settings.params = nextParams;
			}

			@Override
			public void onSuccess(JSONObject jData) {
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
				try {
					app.data.registerBundle.putString(
							"accessKey",
							RSAUtils.decrypt(app.config.pbKey0,
									jData.getString("accessKey")));
					app.data.registerBundle.putString("PbKey",
							jData.getString("PbKey"));

					mMCFragmentManager.replaceToContent(
							new RegisterPassFragment(), true);
				} catch (JSONException e) {
					Toast.makeText(getActivity(), "验证失败", Toast.LENGTH_SHORT)
							.show();
					e.printStackTrace();
				} catch (Exception e) {
					Toast.makeText(getActivity(), "验证失败", Toast.LENGTH_SHORT)
							.show();
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_code;
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.registerCodeFragment;
	}

}
