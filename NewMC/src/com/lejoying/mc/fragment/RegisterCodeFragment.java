package com.lejoying.mc.fragment;

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
import com.lejoying.mc.data.App;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.Settings;
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
		mView_phone.setText(app.data.registerBundle.getString("phone"));
		mView_next.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

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
				getString(R.string.app_codenotnull);
				return;
			}
			next(code);
			break;
		case R.id.tv_sendcode:
			Bundle resendParams = app.data.registerBundle;
			resendParams.remove("code");
			// mMCFragmentManager.startNetworkForResult(API.ACCOUNT_VERIFYPHONE,
			// resendParams, new NetworkStatusAdapter() {
			// @Override
			// public void success() {
			// }
			// });
			break;
		default:
			break;
		}
	}

	public void next(String code) {
		final Bundle nextParams = new Bundle();
		nextParams.putString("phone", app.data.registerBundle.getString("phone"));
		nextParams.putString("code", code);

		MCNetTools.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.ACCOUNT_VERIFYCODE;
				settings.params = nextParams;
			}

			@Override
			public void onSuccess(JSONObject jData) {
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
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_code;
	}

}
