package com.lejoying.mc.fragment;

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
import com.lejoying.mc.api.API;
import com.lejoying.mc.fragment.BaseInterface.OnKeyDownListener;
import com.lejoying.mc.utils.MCStaticData;

public class RegisterPassFragment extends BaseFragment implements
		OnClickListener {
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
						showMsg(getString(R.string.app_cancelregister));
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
				showMsg(getString(R.string.app_passnotnull));
				return;
			} else if (pass.length() < 6) {
				showMsg(getString(R.string.app_passlength));
				return;
			}
			Bundle params = MCStaticData.registerBundle;
			params.remove("code");
			params.remove("usage");
			JSONObject account = new JSONObject();
			try {
				account.put("password", pass);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			params.putString("account", account.toString());
			mMCFragmentManager.startNetworkForResult(API.ACCOUNT_MODIFY,
					params, true, new NetworkStatusAdapter() {
						@Override
						public void success() {
							mMCFragmentManager.startToActivity(
									MainActivity.class, true);
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
