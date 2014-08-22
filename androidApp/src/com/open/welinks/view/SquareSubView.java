package com.open.welinks.view;

import android.util.DisplayMetrics;

import com.open.welinks.model.Data;

public class SquareSubView {


	public Data data = Data.getInstance();

	public String tag = "SquareSubView";

	public DisplayMetrics displayMetrics;

	public MainView mainView;
	
	public SquareSubView(MainView mainView) {
		this.mainView = mainView;
	}
	public void initViews() {
		this.displayMetrics = mainView.displayMetrics;
	}
}
