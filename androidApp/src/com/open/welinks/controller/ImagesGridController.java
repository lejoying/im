package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
	public String parentName;

	public OnItemClickListener onItemClickListener;
	public OnClickListener onClickListener;
	public OnTouchListener onTouchListener;

	Handler handler = new Handler();

	public ImagesGridController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void oncreate() {
		Intent intent = thisActivity.getIntent();
		ArrayList<String> images = intent.getStringArrayListExtra("images");
		imagesSource = images;
		parentName = intent.getStringExtra("parentName");

	}

	public void initializeListeners() {
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (view.equals(thisView.backView)) {
					int motionEvent = event.getAction();
					if (motionEvent == MotionEvent.ACTION_DOWN) {
						view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					} else if (motionEvent == MotionEvent.ACTION_UP) {
						view.setBackgroundColor(Color.parseColor("#00000000"));
					}
				}
				return false;
			}
		};
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.mConfirm)) {
					thisActivity.setResult(Activity.RESULT_OK);
					thisActivity.finish();
				} else if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.getTag(R.id.tag_class) != null) {
					String tag_class = (String) view.getTag(R.id.tag_class);
					if ("already_image".equals(tag_class)) {
						String path = (String) view.getTag(R.id.tag_first);
						thisView.alreadyListContainer.removeView(view);
						ImagesDirectoryController.instance.selectedImage.remove(path);
						thisView.mImageAdapter.notifyDataSetChanged();
					}
				}
			}
		};
		onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
				View v = view.findViewById(R.id.iv_imageContentStatus);
				if (v.getVisibility() == View.GONE) {
					v.setVisibility(View.VISIBLE);
					ImagesDirectoryController.instance.selectedImage.add(imagesSource.get(postion));
					thisView.showAlreayList();
				} else {
					v.setVisibility(View.GONE);
					ImagesDirectoryController.instance.selectedImage.remove(imagesSource.get(postion));
					thisView.showAlreayList();
				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						thisView.mConfirm.setText("确定(" + ImagesDirectoryController.instance.selectedImage.size() + ")");
					}
				});
			}
		};
	}

	public void bindEvent() {
		thisView.mGridView.setOnItemClickListener(onItemClickListener);
		thisView.mConfirm.setOnClickListener(onClickListener);
		thisView.backView.setOnClickListener(onClickListener);
		thisView.backView.setOnTouchListener(onTouchListener);
	}
}
