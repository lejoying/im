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
import android.widget.Toast;

import com.lejoying.mc.LoginActivity;
import com.lejoying.mc.MainActivity;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.fragment.BaseInterface.OnKeyDownListener;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;

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
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_registerpass, null);
		mView_pass = (EditText) mContent.findViewById(R.id.et_broadcast);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);

		mView_next.setOnClickListener(this);
		LoginActivity.stopRemain();
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
						Toast.makeText(getActivity(),
								getString(R.string.app_cancelregister),
								Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getActivity(),
						getString(R.string.app_passnotnull), Toast.LENGTH_SHORT)
						.show();
				return;
			} else if (pass.length() < 6) {
				Toast.makeText(getActivity(),
						getString(R.string.app_passlength), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			next(pass);
			break;

		default:
			break;
		}
	}

	public void next(String pass) {
		final Bundle params = app.data.registerBundle;
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

		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.ACCOUNT_MODIFY;
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
						data.user.phone = data.registerBundle
								.getString("phone");
						data.user.accessKey = data.registerBundle
								.getString("accessKey");
					}
				}, new UIModification() {

					@Override
					public void modifyUI() {
						mMCFragmentManager.startToActivity(MainActivity.class,
								true);
					}
				});
			}
		});
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_pass;
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.registerPassFragment;
	}
}
