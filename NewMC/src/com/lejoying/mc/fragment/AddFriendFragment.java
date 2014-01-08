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
import com.lejoying.mc.data.App;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;
import com.lejoying.utils.HttpTools;

public class AddFriendFragment extends BaseFragment implements OnClickListener {

	App app = App.getInstance();

	private View mContent;
	private EditText mView_message;
	private View mView_send;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_addfriend, null);
		mView_message = (EditText) mContent.findViewById(R.id.et_message);
		mView_send = mContent.findViewById(R.id.btn_send);
		mView_send.setOnClickListener(this);
		return mContent;
	}

	@Override
	protected EditText showSoftInputOnShow() {
		return mView_message;
	}

	@Override
	public void onResume() {
		app.mark = app.addFriendFragment;
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			Bundle params = new Bundle();
			params.putString("phone", app.data.user.phone);
			params.putString("accessKey", app.data.user.accessKey);
			params.putString("phoneto", app.tempFriend.phone);
			params.putString("message", mView_message.getText().toString());
			app.tempFriend.addMessage = mView_message.getText().toString();
			MCNetTools.ajax(getActivity(), API.RELATION_ADDFRIEND, params,
					HttpTools.SEND_POST, 5000, new ResponseListener() {

						@Override
						public void success(JSONObject data) {
							System.out.println(data);
							try {
								showMsg(data.getString("失败原因"));
								return;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							getActivity().getSupportFragmentManager()
									.popBackStack();
							getActivity().getSupportFragmentManager()
									.popBackStack();
							getActivity().getSupportFragmentManager()
									.popBackStack();
							app.tempFriend.addMessage = mView_message.getText()
									.toString();
							while (app.data.newFriends.contains(app.tempFriend)) {
								app.data.newFriends.remove(app.tempFriend);
							}
							app.data.newFriends.add(0, app.tempFriend);
							mMCFragmentManager.replaceToContent(
									new NewFriendsFragment(), true);
						}

						@Override
						public void noInternet() {
							// TODO Auto-generated method stub
							System.out.println("noInternet");
						}

						@Override
						public void failed() {
							// TODO Auto-generated method stub
							System.out.println("failed");

						}

						@Override
						public void connectionCreated(
								HttpURLConnection httpURLConnection) {
							// TODO Auto-generated method stub
							System.out.println("connection");

						}
					});
			break;

		default:
			break;
		}
	}
}
