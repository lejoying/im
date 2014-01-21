package com.lejoying.mc.fragment;

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
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;

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
			final Bundle params = new Bundle();
			params.putString("phone", app.data.user.phone);
			params.putString("accessKey", app.data.user.accessKey);
			params.putString("phoneto", app.data.tempFriend.phone);
			params.putString("message", mView_message.getText().toString());
			app.data.tempFriend.addMessage = mView_message.getText().toString();

			MCNetUtils.ajax(new AjaxAdapter() {

				@Override
				public void setParams(Settings settings) {
					settings.url = API.RELATION_ADDFRIEND;
					settings.params = params;
				}

				@Override
				public void onSuccess(JSONObject jData) {
					try {
						jData.getString(getString(R.string.app_reason));
						return;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					app.data.tempFriend.addMessage = mView_message.getText()
							.toString();
					while (app.data.newFriends.contains(app.data.tempFriend)) {
						app.data.newFriends.remove(app.data.tempFriend);
					}
					app.data.newFriends.add(0, app.data.tempFriend);
					app.mUIThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							getActivity().getSupportFragmentManager()
									.popBackStack();
							getActivity().getSupportFragmentManager()
									.popBackStack();
							getActivity().getSupportFragmentManager()
									.popBackStack();
							mMCFragmentManager.replaceToContent(
									new NewFriendsFragment(), true);
						}
					});
				}
			});
		}
	}
}
