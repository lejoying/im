package com.lejoying.mc.fragment;

import java.net.HttpURLConnection;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lejoying.mc.MainActivity;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.fragment.BaseInterface.OnKeyDownListener;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;
import com.lejoying.utils.HttpTools;

public class RegisterPassFragment extends BaseFragment implements
		OnClickListener {
	App app = App.getInstance();
	private View mContent;
	private EditText mView_pass;
	private Button mView_next;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.hideCircleMenu();
		mContent = inflater.inflate(R.layout.f_registerpass, null);
		mView_pass = (EditText) mContent.findViewById(R.id.et_pass);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);

		mView_next.setOnClickListener(this);

		System.out.println(app.registerBundle);
		mMCFragmentManager.setFragmentKeyDownListener(new OnKeyDownListener() {

			long cancelTime;

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					long now = new Date().getTime();
					if (now - cancelTime < 2000) {
						getActivity().getSupportFragmentManager().popBackStack(
								1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					} else {
						getString(R.string.app_cancelregister);
						cancelTime = now;
					}
				}
				return true;
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
			String pass = mView_pass.getText().toString();
			if (pass == null || pass.equals("")) {
				getString(R.string.app_passnotnull);
				return;
			} else if (pass.length() < 6) {
				getString(R.string.app_passlength);
				return;
			}
			Bundle params = app.registerBundle;
			params.remove("code");
			params.remove("usage");
			params.remove("PbKey");
			JSONObject account = new JSONObject();
			try {
				account.put("password", pass);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			params.putString("account", account.toString());
			MCNetTools.ajax(getActivity(), API.ACCOUNT_MODIFY, params,
					HttpTools.SEND_POST, 5000, new ResponseListener() {

						@Override
						public void success(JSONObject data) {
							try {
								data.getString(getString(R.string.app_reason));
								return;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							app.data.user.phone = app.registerBundle
									.getString("phone");
							app.data.user.accessKey = app.registerBundle
									.getString("accessKey");
							mMCFragmentManager.startToActivity(
									MainActivity.class, true);
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

		default:
			break;
		}
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_pass;
	}
}
