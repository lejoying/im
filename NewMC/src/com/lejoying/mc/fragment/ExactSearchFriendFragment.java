package com.lejoying.mc.fragment;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;

public class ExactSearchFriendFragment extends BaseFragment implements
		OnClickListener {

	App app = App.getInstance();

	private View mContent;
	private EditText mView_phone;
	private View mView_search;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_searchfriend, null);
		mView_phone = (EditText) mContent.findViewById(R.id.et_phone);
		mView_search = mContent.findViewById(R.id.btn_search);
		mView_search.setOnClickListener(this);
		return mContent;
	}

	@Override
	protected EditText showSoftInputOnShow() {
		return mView_phone;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			if (mView_phone.getText().toString().equals("")) {
				showMsg("请输入好友手机号");
				return;
			}
			Bundle params = new Bundle();
			params.putString("phone", app.data.user.phone);
			params.putString("accessKey", app.data.user.accessKey);
			params.putSerializable("target", mView_phone.getText().toString());
			MCNetTools.ajax(getActivity(), API.ACCOUNT_GET, params,
					MCHttpTools.SEND_POST, 5000, new ResponseListener() {

						@Override
						public void success(JSONObject data) {
							try {
								showMsg(data.getString("失败原因"));
								return;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								app.tempFriend = MCDataTools
										.generateFriendFromJSON(data
												.getJSONObject("account"));
								app.businessCardStatus = app.SHOW_TEMPFRIEND;
								mMCFragmentManager.replaceToContent(
										new BusinessCardFragment(), true);
							} catch (JSONException e) {
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

		default:
			break;
		}
	}
}
