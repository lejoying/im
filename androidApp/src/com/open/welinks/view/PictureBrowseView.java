package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.PictureBrowseController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.utils.ClickOperationSound;

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

	// top bar view
	public RelativeLayout backView;
	public TextView imageNumberView;
	public TextView titleView;
	public ImageView choiceCoverView;
	public ImageView deleteButtonView;

	public DisplayImageOptions options;
	public ImageLoader imageLoader = ImageLoader.getInstance();

	public DownloadFile downloadFile = null;
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public static int IMAGEBROWSE_COMMON = 0x01;
	public static int IMAGEBROWSE_OPTION = 0x02;

	public RelativeLayout rightContainer;

	public PictureBrowseView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	DisplayMetrics displayMetrics;

	public void initView() {
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		mInflater = thisActivity.getLayoutInflater();
		thisActivity.setContentView(R.layout.activity_picture_browse);
		imageViewPageContent = (ViewPager) thisActivity.findViewById(R.id.mainPagerContent);

		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		ClickOperationSound.click(thisActivity, backView);
		imageNumberView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		titleView = (TextView) thisActivity.findViewById(R.id.titleContent);
		choiceCoverView = (ImageView) thisActivity.findViewById(R.id.choiceCoverView);

		rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		// deleteButtonView = (ImageView) thisActivity.findViewById(R.id.deleteImageView);

		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();

		imagePagerAdapter = new ImagePagerAdapter(thisController.imagesBrowseList);
		imageViewPageContent.setAdapter(imagePagerAdapter);
		imageViewPageContent.setCurrentItem(thisController.currentPosition);

		imageNumberView.setText((thisController.currentPosition + 1) + "/" + thisController.imagesBrowseList.size());
		titleView.setText("浏览");
		deleteButtonView = new ImageView(context);
		deleteButtonView.setImageResource(R.drawable.image_delete);
		deleteButtonView.setPadding((int) (30 * displayMetrics.density), (int) (15 * displayMetrics.density), (int) (30 * displayMetrics.density), (int) (15 * displayMetrics.density));
		rightContainer.addView(deleteButtonView);
		if (thisController.currentType == IMAGEBROWSE_COMMON) {
			 deleteButtonView.setVisibility(View.GONE);
		} else if (thisController.currentType == IMAGEBROWSE_OPTION) {
			 deleteButtonView.setVisibility(View.VISIBLE);
		}
	}

	public void notifyAdapter() {
		imageNumberView.setText(thisController.currentPosition + 1 + "/" + thisController.imagesBrowseList.size());
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
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = mInflater.inflate(R.layout.view_picture_browse_item, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			String fileName = images.get(position);
			if (fileName.lastIndexOf("/") == -1) {
				File sdFile = Environment.getExternalStorageDirectory();
				File file = new File(sdFile, "welinks/images/" + fileName);
				fileName = file.getAbsolutePath();
			}
			final String path = fileName;
			final String url = API.DOMAIN_COMMONIMAGE + "images/" + path.substring(path.lastIndexOf("/") + 1);
			imageLoader.displayImage("file://" + path, imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					downloadFile = new DownloadFile(url, path);
					downloadFile.view = view;
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					downloadFileList.addDownloadFile(downloadFile);
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

			view.addView(imageLayout);
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
