package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadOssFileController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.TempData.ImageBean;

public class DownloadOssFileView {
	public Data data = Data.getInstance();
	public String tag = "DownloadOssFileView";

	public Context context;
	public DownloadOssFileView thisView;
	public DownloadOssFileController thisController;
	public Activity thisActivity;

	public DisplayImageOptions options;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public ImageLoadingListener animateFirstListener;

	public ListView listView;

	public ControlProgress titleControlProgress;
	public TransportingList transportingList;
	public View controlProgressView;

	public TextView addMonyImageUploadView;

	public TextView leftTopTextView;

	public LayoutInflater mInflater;

	public File sdFile = Environment.getExternalStorageDirectory();

	public DownloadOssFileView(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(20)).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(thisActivity));

		thisActivity.setContentView(R.layout.activity_downloadossfiles);

		leftTopTextView = (TextView) thisActivity.findViewById(R.id.leftTopText);
		leftTopTextView.setText("下载列表");
		addMonyImageUploadView = (TextView) thisActivity.findViewById(R.id.rightTopText);

		listView = (ListView) thisActivity.findViewById(R.id.view_element_debug1_list);
		transportingList = new TransportingList();
		transportingList.initialize(listView);

		controlProgressView = thisActivity.findViewById(R.id.title_control_progress_container);
		titleControlProgress = new ControlProgress();
		titleControlProgress.initialize(controlProgressView);
	}

	public class TransportingList {
		public ListView transportingList;
		public ArrayList<TransportingItem> transportingItems = new ArrayList<TransportingItem>();

		public void initialize(View container) {
			transportingList = (ListView) container;
			setContent();
			transportingList.setAdapter(null);
		}

		public void setContent() {
			for (int i = 0; i < data.localStatus.localData.prepareDownloadImagesList.size(); i++) {
				TransportingItem transportingItem = new TransportingItem();
				transportingItems.add(transportingItem);

				ImageBean imageBean = data.localStatus.localData.prepareDownloadImagesList.get(i);

				View transportingItemView = transportingItem.initialize(imageBean);
				String path = imageBean.path;
				if (imageBean.downloadFile == null) {
					imageBean.downloadFile = thisController.downloadFile(path);
				}

				imageBean.downloadFile.transportingItem = transportingItem;
				transportingItem.downloadFile = imageBean.downloadFile;
				imageBean.downloadFile.imageBean = imageBean;
				transportingItemView.setTag(path);
				// controlProgress.setTag(uploadMultipart);
				transportingList.addFooterView(transportingItemView);
			}
			TransportingItem transportingItem = new TransportingItem();
			transportingItems.add(transportingItem);
			View transportingItemView = transportingItem.initialize(null);
			transportingList.addFooterView(transportingItemView);
		}

		public class TransportingItem {

			public View transportingItemView;

			public ImageView imageView;

			public TextView text_filename_view;
			public TextView text_file_size_view;
			public TextView text_transport_time_view;
			public View controlProgressView;

			public ControlProgress controlProgress;

			public DownloadFile downloadFile;

			public ImageBean imageSource;

			public View initialize(ImageBean imageSource) {
				this.imageSource = imageSource;
				transportingItemView = mInflater.inflate(R.layout.view_element_debug1_list_item, null);
				if (imageSource == null) {
					this.controlProgressView = transportingItemView.findViewById(R.id.list_item_progress_container);
					controlProgressView.setVisibility(View.GONE);
					return transportingItemView;
				}
				String path = imageSource.path;
				imageView = (ImageView) transportingItemView.findViewById(R.id.image);

				text_filename_view = (TextView) transportingItemView.findViewById(R.id.text_filename);
				text_filename_view.setText(path.substring(path.lastIndexOf("/") + 1));

				text_file_size_view = (TextView) transportingItemView.findViewById(R.id.text_file_size);
				text_file_size_view.setText("0/" + imageSource.size + "k");

				text_transport_time_view = (TextView) transportingItemView.findViewById(R.id.text_transport_time);
				text_transport_time_view.setText("0ms");

				this.controlProgressView = transportingItemView.findViewById(R.id.list_item_progress_container);
				this.controlProgress = new ControlProgress();
				this.controlProgress.initialize(this.controlProgressView);
				if (imageSource.downloadFile != null) {
					long time = imageSource.downloadFile.time.received - imageSource.downloadFile.time.start;
					if (time < 0) {
						time = 0;
					}
					text_transport_time_view.setText(time + "ms");
					long size = Long.valueOf(imageSource.size);
					int currentUploadSize = (int) Math.floor(size * imageSource.downloadFile.uploadPrecent / 100f);
					text_file_size_view.setText(currentUploadSize / 1000 + "/" + size / 1000 + "k");
					if (imageSource.downloadFile.isDownloadStatus == DownloadFile.DOWNLOAD_SUCCESS) {
						String fileName = thisController.urlToLocalFileName(path);
						File file = new File(sdFile, "test0/" + fileName);
						imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView, options, animateFirstListener);
					}
					this.controlProgress.moveTo(imageSource.downloadFile.uploadPrecent);
				}

				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
				this.controlProgress.progress_line1.setLayoutParams(params);
				this.controlProgress.progress_line2.setLayoutParams(params);
				return transportingItemView;
			}
		}
	}

	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

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

	public class ControlProgress {

		public View controlProgressView;

		public ImageView progress_line1;

		public ImageView progress_line2;
		public TranslateAnimation move_progress_line1;

		public int percentage = 0;
		public int width = 0;

		public void initialize(View container) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			move_progress_line1 = new TranslateAnimation(103, 0, 0, 0);

			progress_line1 = (ImageView) container.findViewById(R.id.progress_line1);
			progress_line2 = (ImageView) container.findViewById(R.id.progress_line2);
			controlProgressView = container;

			width = displayMetrics.widthPixels;

		}

		public void moveTo(int targetPercentage) {
			float position = targetPercentage / 100.0f * this.width;
			move_progress_line1 = new TranslateAnimation((percentage - targetPercentage) / 100.0f * width, 0, 0, 0);
			// TODO old animation becomes memory fragment
			move_progress_line1.setStartOffset(0);
			move_progress_line1.setDuration(200);

			progress_line1.startAnimation(move_progress_line1);

			progress_line1.setX(position);
			percentage = targetPercentage;
		}

		public void setTo(int targetPercentage) {
			float position = targetPercentage / 100.0f * this.width;
			progress_line1.setX(position);
			percentage = targetPercentage;
		}
	}
}