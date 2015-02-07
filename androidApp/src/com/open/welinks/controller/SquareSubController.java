package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.gson.Gson;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.ClassificationRecommendationActivity;
import com.open.welinks.ShareSectionActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.view.SquareSubView;

public class SquareSubController {

	public Data data = Data.getInstance();
	public String tag = "ShareSubController";
	public MyLog log = new MyLog(tag, true);
	public Parser parser = Parser.getInstance();

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public HttpClient httpClient = HttpClient.getInstance();

	public SquareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;

	public Gson gson = new Gson();

	public SquareSubController(MainController mainController) {
		thisActivity = mainController.thisActivity;

		this.mainController = mainController;
	}

	public void initializeListeners() {

		mOnClickListener = new MyOnClickListener() {
			@Override
			public void onClickEffective(View view) {
				if (view.equals(thisView.button1)) {
					thisActivity.startActivity(new Intent(thisActivity, ClassificationRecommendationActivity.class));
				} else if (view.equals(thisView.button2)) {
					Intent intent = new Intent(thisActivity, ShareSectionActivity.class);
					intent.putExtra("key", "91");
					thisActivity.startActivity(intent);
				}
			}
		};

	}

	public void bindEvent() {
		thisView.button1.setOnClickListener(mOnClickListener);
		thisView.button2.setOnClickListener(mOnClickListener);
	}

}
