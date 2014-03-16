package com.lejoying.mc.fragment;

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

public class RegisterPhoneFragment extends BaseFragment implements
		OnClickListener {
	App app = App.getInstance();
	private View mContent;
	private EditText mView_phone;
	private Button mView_next;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_registerphone, null);
		mView_phone = (EditText) mContent.findViewById(R.id.et_phone);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);
		mView_next.setOnClickListener(this);
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
			String phone = mView_phone.getText().toString();
			if (phone.equals("")) {
				Toast.makeText(getActivity(),
						getString(R.string.app_phonenotnull),
						Toast.LENGTH_SHORT).show();
				showSoftInput(mView_phone);
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
			next(phone);
			break;
		default:
			break;
		}
	}

	public void next(final String phone) {
		boolean flag = LoginActivity.setRemain(phone);
		if (!flag) {
			mMCFragmentManager.replaceToContent(new RegisterCodeFragment(),
					true);
			return;
		}
		final Bundle params = new Bundle();
		params.putString("phone", phone);
		params.putString("usage", "register");
		LoginActivity.startRemain();
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
						app.data.registerBundle = params;
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
						mMCFragmentManager.replaceToContent(
								new RegisterCodeFragment(), true);
					}
				});
			}
		});
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_phone;
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.registerPhoneFragment;
	}
}
