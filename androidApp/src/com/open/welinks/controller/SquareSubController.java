package com.open.welinks.controller;

import android.view.View;
import android.view.View.OnClickListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.SquareSubView;

public class SquareSubController {

	public Data data = Data.getInstance();
	public String tag = "SquareSubController";
	public SquareSubView thisView;

	public MainController mainController;

	public OnClickListener mOnClickListener;
	public OnDownloadListener downloadListener;

	public SquareSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		};

	}

	public void bindEvent() {

	}
}
