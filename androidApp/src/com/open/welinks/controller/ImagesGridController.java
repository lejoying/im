package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.open.welinks.R;
import com.open.welinks.view.ImagesGridView;

public class ImagesGridController {

	public String tag = "ImagesGridController";

	public Context context;
	public Activity thisActivity;
	public ImagesGridController thisController;
	public ImagesGridView thisView;

	public List<String> imagesSource = new ArrayList<String>();

	public OnItemClickListener onItemClickListener;
	public OnClickListener onClickListener;

	Handler handler = new Handler();

	public ImagesGridController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void oncreate() {
		ArrayList<String> images = thisActivity.getIntent()
				.getStringArrayListExtra("images");
		imagesSource = images;
	}

	public void initializeListeners() {
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.mConfirm)) {
					thisActivity.setResult(Activity.RESULT_OK);
					thisActivity.finish();
				}
			}
		};
		onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int postion, long id) {
				View v = view.findViewById(R.id.iv_imageContentStatus);
				if (v.getVisibility() == View.GONE) {
					v.setVisibility(View.VISIBLE);
					ImagesDirectoryController.selectedImage.add(imagesSource
							.get(postion));
				} else {
					v.setVisibility(View.GONE);
					ImagesDirectoryController.selectedImage.remove(imagesSource
							.get(postion));
				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						thisView.mConfirm.setText("确定("
								+ ImagesDirectoryController.selectedImage
										.size() + ")");
					}
				});
			}
		};
	}

	public void bindEvent() {
		thisView.mGridView.setOnItemClickListener(onItemClickListener);
		thisView.mConfirm.setOnClickListener(onClickListener);
	}
}
