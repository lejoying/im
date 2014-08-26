package com.open.welinks.controller;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.MeSubView;


public class MeSubController {

	public Data data = Data.getInstance();
	public String tag = "MeSubController";
	public MeSubView thisView;

	public MainController mainController;
	
	public OnTouchListener onTouchListener;

	public MeSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					thisView.mMePageAppIconScaleSpring.setEndValue(1);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					thisView.mMePageAppIconScaleSpring.setEndValue(0);
				}
				return true;
			}
		};
	}

	public void bindEvent() {
		thisView.mRootView.setOnTouchListener(onTouchListener);
	}

}
