package com.open.welinks.view;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.ImagesDirectoryController;
import com.open.welinks.view.ThreeChoicesView.OnItemClickListener;

public class ImagesDirectoryView {

	public String tag = "ImagesDirectoryView";

	public LayoutInflater mInflater;

	public Context context;
	public Activity thisActivity;
	public ImagesDirectoryController thisController;
	public ImagesDirectoryView thisView;

	public GridView mGridView;
	public RelativeLayout backView;
	public TextView titleContentView;
	public TextView backTitileView;

	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public ThreeChoicesView threeChoicesView;
	public RelativeLayout rightContainerView;

	public ImagesDirectoryView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void initViews() {
		thisActivity.setContentView(R.layout.activity_images_directory);
		mGridView = (GridView) thisActivity.findViewById(R.id.album_grid);
		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		backTitileView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		backTitileView.setText("相册");
		rightContainerView = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);

		threeChoicesView = new ThreeChoicesView(thisActivity, 1);
		threeChoicesView.setTwoChoice();
		threeChoicesView.setButtonOneText("本地");
		threeChoicesView.setButtonThreeText("云端");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightContainerView.addView(threeChoicesView, params);

		OnItemClickListener mOnItemClickListener = threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {

			}
		};
		threeChoicesView.setOnItemClickListener(mOnItemClickListener);
	}

	public class MyGridViewAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		public MyGridViewAdapter() {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024)
			// 50 Mb
					.tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove for release app
					.build();
			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);
			options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(10)).build();
			mInflater = thisActivity.getLayoutInflater();
		}

		@Override
		public int getCount() {
			return thisController.imageDirectorys.size();
		}

		@Override
		public Object getItem(int arg0) {
			return thisController.imageDirectorys.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			String directoryName = thisController.imageDirectorys.get(position);
			String path = thisController.mImageDirectorysMap.get(directoryName).get(0);
			if (convertView == null) {
				viewHolder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.grid_group_item, null);
				viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.group_image);
				viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
				viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
			}
			viewHolder.mTextViewTitle.setText(directoryName);
			viewHolder.mTextViewCounts.setText(thisController.mImageDirectorysMap.get(directoryName).size() + "");

			imageLoader.displayImage("file://" + path, viewHolder.mImageView, options, animateFirstListener);

			return convertView;
		}
	}

	public static class ViewHolder {
		public ImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
