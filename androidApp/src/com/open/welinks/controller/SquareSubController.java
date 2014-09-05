package com.open.welinks.controller;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.view.SquareSubView;

public class SquareSubController {

	public Data data = Data.getInstance();
	public String tag = "SquareSubController";

	public MyLog log = new MyLog(tag, true);

	public SquareSubView thisView;

	public MainController mainController;

	public OnClickListener mOnClickListener;
	public OnDownloadListener downloadListener;

	public SquareSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		downloadListener = new OnDownloadListener() {
			@Override
			public void onSuccess(final DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				log.e("square download success");
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, thisView.options);
			}
		};
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
