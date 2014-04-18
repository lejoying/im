package com.lejoying.wxgs.activity.mode.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;

public class ChangePasswordFragment extends BaseFragment implements
		OnClickListener {
	View mContent;
	LayoutInflater mInflater;

	View cp_rl_save;
	View cp_rl_cancel;
	EditText cp_et_initialpwd;
	EditText cp_et_confirmpwd;
	EditText cp_et_modifychangepwd;

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = inflater.inflate(R.layout.f_changepwd, null);
		mContent.setBackgroundDrawable(new BitmapDrawable(
				app.fileHandler.bitmaps.get(app.data.user.userBackground)));
		initData();
		return mContent;
	}

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

	void initData() {
		cp_rl_save = mContent.findViewById(R.id.cp_rl_save);
		cp_rl_cancel = mContent.findViewById(R.id.cp_rl_cancel);
		cp_et_initialpwd = (EditText) mContent
				.findViewById(R.id.cp_et_initialpwd);
		cp_et_confirmpwd = (EditText) mContent
				.findViewById(R.id.cp_et_confirmpwd);
		cp_et_modifychangepwd = (EditText) mContent
				.findViewById(R.id.cp_et_modifychangepwd);
		cp_rl_save.setOnClickListener(this);
		cp_rl_cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cp_rl_save:
			String initialpwd=cp_et_initialpwd.getText().toString();
			String changepwd=cp_et_modifychangepwd.getText().toString();
			String confirmpwd=cp_et_confirmpwd.getText().toString();
			if(initialpwd!=null&&changepwd!=null&&confirmpwd!=null){
				if(changepwd.equals(confirmpwd)){
					
				}
			}
			
			mMainModeManager.back();
			break;

		case R.id.cp_rl_cancel:
			mMainModeManager.back();
			break;
		default:
			break;
		}
	}
}
