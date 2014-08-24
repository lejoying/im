package com.open.welinks.controller;

import java.util.ArrayList;

import com.open.welinks.model.Data;
import com.open.welinks.view.PictureBrowseView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class PictureBrowseController {
	public Data data = Data.getInstance();
	public String tag = "PictureBrowseController";

	public Context context;
	public PictureBrowseView thisView;
	public PictureBrowseController thisController;
	public Activity thisActivity;

	public ArrayList<String> imagesBrowseList;

	public int currentPosition;

	public PictureBrowseController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		currentPosition = Integer.valueOf(thisActivity.getIntent().getStringExtra("position"));
		if (data.tempData.selectedImageList != null)
			imagesBrowseList = data.tempData.selectedImageList;
		else
			imagesBrowseList = new ArrayList<String>();
	}

	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			currentPosition = savedInstanceState.getInt("position");
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("position", thisView.imageViewPageContent.getCurrentItem());
	}
}
