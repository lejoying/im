package com.open.welinks.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.R;
import com.open.welinks.controller.ShareReleaseImageTextController;
import com.open.welinks.model.Data;

public class ShareReleaseImageTextView {
	public Data data = Data.getInstance();
	public String tag = "ShareReleaseImageTextView";

	public Context context;
	public ShareReleaseImageTextView thisView;
	public ShareReleaseImageTextController thisController;
	public Activity thisActivity;

	public EditText mEditTextView;
	public RelativeLayout mImagesContentView;
	public RelativeLayout mReleaseButtomBarView;

	public TextView mCancleButtonView;
	public TextView mConfirmButtonView;
	public ImageView mSelectImageButtonView;

	public DisplayMetrics displayMetrics;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	public ShareReleaseImageTextView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void initView() {
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		thisActivity.setContentView(R.layout.share_release_imagetext);
		mEditTextView = (EditText) thisActivity.findViewById(R.id.releaseTextContentView);
		mImagesContentView = (RelativeLayout) thisActivity.findViewById(R.id.releaseImagesContent);
		mReleaseButtomBarView = (RelativeLayout) thisActivity.findViewById(R.id.releaseButtomBar);
		mCancleButtonView = (TextView) thisActivity.findViewById(R.id.releaseCancel);
		mConfirmButtonView = (TextView) thisActivity.findViewById(R.id.releaseConfirm);
		mSelectImageButtonView = (ImageView) thisActivity.findViewById(R.id.selectImageButton);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).considerExifParams(true).displayer(new RoundedBitmapDisplayer(0)).build();
	}

	public void showSelectedImages() {
		this.mImagesContentView.removeAllViews();
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		if (selectedImageList.size() > 0)
			this.mImagesContentView.setVisibility(View.VISIBLE);
		for (int i = 0; i < selectedImageList.size(); i++) {
			ImageView imageView = new ImageView(context);
			int width = (int) (displayMetrics.density * 50);
			Log.e(tag, "-------000-----" + width);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
			this.mImagesContentView.addView(imageView, layoutParams);
			float x = i * (width + 2 * displayMetrics.density) + 2 * displayMetrics.density;
			if (i == 0) {
				x = 2 * displayMetrics.density;
			}
			imageView.setTranslationX(x);
			imageLoader.displayImage("file://" + selectedImageList.get(i), imageView, options);
		}
	}
}
