package com.lejoying.mc.fragment;

import java.net.HttpURLConnection;

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

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.service.handler.MainServiceHandler;
import com.lejoying.mc.service.handler.NetworkRemain.RemainListener;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_registercode, null);
		mView_code = (EditText) mContent.findViewById(R.id.et_code);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);
		mView_phone = (TextView) mContent.findViewById(R.id.tv_phone);
		mView_sendcode = (TextView) mContent.findViewById(R.id.tv_sendcode);
		mView_phone.setText(app.registerBundle.getString("phone"));
		mView_next.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		mMCFragmentManager.setNetworkRemainListener(new RemainListener() {
			@Override
			public String setRemainType() {
				return MainServiceHandler.REMAIN_REGISTER;
			}

			@Override
			public void remain(int remain) {
				if (remain > 0) {
					mView_sendcode.setText(getString(R.string.tv_resend) + "("
							+ remain + ")");
				} else {
					mView_sendcode.setText(getString(R.string.tv_resend));
				}
			}
		});

		return mContent;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			String code = mView_code.getText().toString();
			if (code.equals("")) {
				showMsg(getString(R.string.app_codenotnull));
				return;
			}
			Bundle nextParams = new Bundle();
			nextParams
					.putString("phone", app.registerBundle.getString("phone"));
			nextParams.putString("code", code);
			MCNetTools.ajax(getActivity(), API.ACCOUNT_VERIFYCODE, nextParams,
					MCHttpTools.SEND_POST, 5000, new ResponseListener() {

						@Override
						public void success(JSONObject data) {
							try {
								app.registerBundle.putString("accessKey",
										RSAUtils.decrypt(app.config.pbKey0,
												data.getString("accessKey")));
								app.registerBundle.putString("PbKey",
										data.getString("PbKey"));

								mMCFragmentManager.replaceToContent(
										new RegisterPassFragment(), true);
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void noInternet() {
							// TODO Auto-generated method stub

						}

						@Override
						public void failed() {
							// TODO Auto-generated method stub

						}

						@Override
						public void connectionCreated(
								HttpURLConnection httpURLConnection) {
							// TODO Auto-generated method stub

						}
					});
			break;
		case R.id.tv_sendcode:
			Bundle resendParams = app.registerBundle;
			resendParams.remove("code");
			mMCFragmentManager.startNetworkForResult(API.ACCOUNT_VERIFYPHONE,
					resendParams, new NetworkStatusAdapter() {
						@Override
						public void success() {
						}
					});
			break;
		default:
			break;
		}
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_code;
	}

}
