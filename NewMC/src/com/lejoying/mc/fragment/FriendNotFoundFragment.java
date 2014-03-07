package com.lejoying.mc.fragment;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class FriendNotFoundFragment extends BaseFragment implements
		OnClickListener {

	App app = App.getInstance();

	private View mContent;
	private View mView_callfor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_friendnotfound, null);
		mView_callfor = mContent.findViewById(R.id.btn_callfor);
		mView_callfor.setOnClickListener(this);
		return mContent;
	}

	@Override
	protected EditText showSoftInputOnShow() {
		return null;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_callfor:

			break;

		default:
			break;
		}
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.friendNotFoundFragment;
	}

}
