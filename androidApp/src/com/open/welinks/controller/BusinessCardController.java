package com.open.welinks.controller;

import com.open.welinks.BusinessCardActivity;
import com.open.welinks.view.BusinessCardView;

public class BusinessCardController {
	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	public BusinessCardController(BusinessCardActivity activity) {
		thisActivity = activity;
		thisController = this;
		onCreate();
	}

	public void onCreate() {
		// TODO Auto-generated method stub
		initializeListeners();
	}

	public void initializeListeners() {
		// TODO Auto-generated method stub
		bindEvent();
	}

	public void bindEvent() {
		// TODO Auto-generated method stub

	}

}
