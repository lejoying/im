package com.open.welinks.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.PictureBrowseController;
import com.open.welinks.model.Data;

public class PictureBrowseView {
	public Data data = Data.getInstance();
	public String tag = "PictureBrowseView";

	public Context context;
	public PictureBrowseView thisView;
	public PictureBrowseController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;
	public ViewPager imageViewPageContent;

	public ImagePagerAdapter imagePagerAdapter;

	public DisplayImageOptions options;
	public ImageLoader imageLoader = ImageLoader.getInstance();

	public PictureBrowseView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		thisActivity.setContentView(R.layout.activity_picture_browse);
		imageViewPageContent = (ViewPager) thisActivity.findViewById(R.id.mainPagerContent);

		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();

		imagePagerAdapter = new ImagePagerAdapter(thisController.imagesBrowseList);
		imageViewPageContent.setAdapter(imagePagerAdapter);
		imageViewPageContent.setCurrentItem(thisController.currentPosition);
	}

	public void notifyAdapter() {
		imagePagerAdapter.notifyDataSetChanged();
	}

	public class ImagePagerAdapter extends PagerAdapter {

		private ArrayList<String> images;

		public ImagePagerAdapter(ArrayList<String> images) {
			this.images = images;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = mInflater.inflate(R.layout.view_picture_browse_item, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			imageLoader.displayImage("file://" + images.get(position), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
					}
					Toast.makeText(thisActivity, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}
