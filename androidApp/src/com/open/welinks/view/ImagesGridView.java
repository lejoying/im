package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.ImagesDirectoryController;
import com.open.welinks.controller.ImagesGridController;
import com.open.welinks.model.Data;

public class ImagesGridView {

	public String tag = "ImagesGridView";

	public Data data = Data.getInstance();
	LayoutInflater mInflater;

	public Context context;
	public Activity thisActivity;
	public ImagesGridController thisController;
	public ImagesGridView thisView;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	public ImageAdapter mImageAdapter;

	public GridView mGridView;
	public TextView mConfirm;

	public ImageView backImageDirectoryView;
	public TextView directoryNameView;

	public void initViews() {
		thisActivity.setContentView(R.layout.activity_image_grid);
		mGridView = (GridView) thisActivity.findViewById(R.id.gridview);
		mConfirm = (TextView) thisActivity.findViewById(R.id.tv_confirm);
		backImageDirectoryView = (ImageView) thisActivity.findViewById(R.id.backImageDirectory);
		directoryNameView = (TextView) thisActivity.findViewById(R.id.directoryName);
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
			size = ImagesDirectoryController.selectedImage.size();
		}
		this.mConfirm.setText("确定(" + size + ")");
		this.directoryNameView.setText(thisController.parentName);

	}

	public ImagesGridView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	static class ViewHolder {
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
				if (ImagesDirectoryController.selectedImage == null)
					return view;
			} catch (Exception e) {
				return view;
			}
			if (ImagesDirectoryController.selectedImage.contains(path)) {
				holder.imageStatusView.setVisibility(View.VISIBLE);
			} else {
				holder.imageStatusView.setVisibility(View.GONE);
			}
			imageLoader.displayImage("file://" + path, holder.imageView, options, new SimpleImageLoadingListener() {
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
