package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.ImagesDirectoryController;
import com.open.welinks.controller.ImagesGridController;
import com.open.welinks.model.Data;
import com.open.welinks.model.TaskManageHolder;

public class ImagesGridView {

	public String tag = "ImagesGridView";

	public Data data = Data.getInstance();
	LayoutInflater mInflater;

	public Context context;
	public Activity thisActivity;
	public ImagesGridController thisController;
	public ImagesGridView thisView;
	
	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	DisplayImageOptions options;

	public ImageAdapter mImageAdapter;

	public GridView mGridView;
	public TextView mConfirmView;

	public RelativeLayout rightContainerView;

	public RelativeLayout backView;
	public TextView directoryNameView;
	public TextView backTitleView;

	public DisplayMetrics displayMetrics;

	public LinearLayout alreadyListContainer;
	public DisplayImageOptions smallOptions;

	public void initViews() {
		// smallOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(60)).build();
		smallOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(10)).build();

		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		thisActivity.setContentView(R.layout.activity_image_grid);
		alreadyListContainer = (LinearLayout) thisActivity.findViewById(R.id.alreadyListContainer);
		mGridView = (GridView) thisActivity.findViewById(R.id.gridview);
		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);

		directoryNameView = (TextView) thisActivity.findViewById(R.id.backTitleView);

		rightContainerView = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);

		int dp_5 = (int) (5 * displayMetrics.density);
		mConfirmView = new TextView(context);
		mConfirmView.setGravity(Gravity.CENTER);
		mConfirmView.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
		mConfirmView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mConfirmView.setText("确定(0)");
		mConfirmView.setBackgroundResource(R.drawable.textview_bg);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightContainerView.addView(mConfirmView, layoutParams);
		showAlreayList();
	}

	public void initData() {
		mInflater = thisActivity.getLayoutInflater();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		mImageAdapter = new ImageAdapter();
		mGridView.setAdapter(mImageAdapter);
		int size = 0;
		if (data.tempData.selectedImageList != null) {
			size = data.tempData.selectedImageList.size();
		} else {
			size = ImagesDirectoryController.instance.selectedImage.size();
		}
		this.mConfirmView.setText("确定(" + size + ")");
		this.directoryNameView.setText(thisController.parentName);

	}

	public void showAlreayList() {
		int width = (int) (40 * displayMetrics.density);
		int spacing = (int) (5 * displayMetrics.density);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
		layoutParams.setMargins(spacing, spacing, spacing, spacing);
		alreadyListContainer.removeAllViews();
		for (int i = 0; i < ImagesDirectoryController.instance.selectedImage.size(); i++) {
			String key = ImagesDirectoryController.instance.selectedImage.get(i);
			ImageView imageView = new ImageView(thisActivity);
			imageView.setTag(R.id.tag_class, "already_image");
			imageView.setTag(R.id.tag_first, key);
			imageView.setOnClickListener(thisController.onClickListener);
			alreadyListContainer.addView(imageView, layoutParams);
			taskManageHolder.imageLoader.displayImage("file://" + key, imageView, smallOptions);
			// fileHandlers.getHeadImagssse(key, imageView, options);
		}
	}

	public ImagesGridView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		ImageView imageStatusView;
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return thisController.imagesSource.size();
		}

		@Override
		public Object getItem(int position) {
			return thisController.imagesSource.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = mInflater.inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				holder.imageStatusView = (ImageView) view.findViewById(R.id.iv_imageContentStatus);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			String path = thisController.imagesSource.get(position);
			try {
				if (ImagesDirectoryController.instance.selectedImage == null)
					return view;
			} catch (Exception e) {
				return view;
			}
			if (ImagesDirectoryController.instance.selectedImage.contains(path)) {
				holder.imageStatusView.setVisibility(View.VISIBLE);
			} else {
				holder.imageStatusView.setVisibility(View.GONE);
			}
			taskManageHolder.imageLoader.displayImage("file://" + path, holder.imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					holder.progressBar.setProgress(0);
					holder.progressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					holder.progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					holder.progressBar.setVisibility(View.GONE);
				}
			}, new ImageLoadingProgressListener() {
				@Override
				public void onProgressUpdate(String imageUri, View view, int current, int total) {
					holder.progressBar.setProgress(Math.round(100.0f * current / total));
				}
			});

			return view;
		}
	}
}
