package com.open.welinks.view;

import android.view.LayoutInflater;

import com.open.welinks.BusinessCardActivity;
import com.open.welinks.R;
import com.open.welinks.controller.BusinessCardController;

public class BusinessCardView {
	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	public LayoutInflater mInflater;

	public BusinessCardView(BusinessCardActivity activity) {
		thisActivity = activity;
		thisView = this;
		initView();
	}

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		thisActivity.setContentView(R.layout.activity_businesscard);

	}

}
