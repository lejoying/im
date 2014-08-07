package com.lejoying.wxgs.activity.mode.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.app.MainApplication;

public class MoreSquaresFragment extends BaseFragment implements
		OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	View mContent;

	ImageView search, delete;
	EditText findrecord;
	TextView location;
	RelativeLayout backView;
	FrameLayout map;
	ListView near;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = mInflater.inflate(R.layout.f_more_squares, null);
		initLayout();
		initEvent();
		initData();
		return mContent;
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void initEvent() {
		search.setOnClickListener(this);
		delete.setOnClickListener(this);
		backView.setOnClickListener(this);
		findrecord.addTextChangedListener(new TextWatcher() {
			String content = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				content = s.toString();

			}

			@Override
			public void afterTextChanged(Editable s) {
				if ("".equals(content)) {
					delete.setVisibility(View.GONE);
				} else {
					delete.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void initLayout() {
		search = (ImageView) mContent.findViewById(R.id.search);
		delete = (ImageView) mContent.findViewById(R.id.delete);
		findrecord = (EditText) mContent.findViewById(R.id.findrecord);
		location = (TextView) mContent.findViewById(R.id.location);
		backView = (RelativeLayout) mContent.findViewById(R.id.backView);
		map = (FrameLayout) mContent.findViewById(R.id.map);
		near = (ListView) mContent.findViewById(R.id.near);
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.search) {

		} else if (id == R.id.delete) {
			findrecord.setText("");
		} else if (id == R.id.backView) {
			mMainModeManager.back();
		}

	}
}
