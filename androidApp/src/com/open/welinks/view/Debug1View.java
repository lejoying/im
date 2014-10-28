package com.open.welinks.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.open.welinks.controller.Debug1Controller;
import com.open.welinks.controller.UploadMultipart;
import com.open.welinks.customView.ControlProgress;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.TempData.ImageBean;

public class Debug1View {
	public Data data = Data.getInstance();
	public String tag = "Debug1View";

	public Context context;
	public Debug1View thisView;
	public Debug1Controller thisController;
	public Activity thisActivity;

	public DisplayImageOptions options;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public ImageLoadingListener animateFirstListener;

	public ListView listView;

	public ControlProgress titleControlProgress;
	public TransportingList transportingList;
	public View controlProgressView;

	public TextView addMonyImageUploadView;

	public LayoutInflater mInflater;

	public RelativeLayout dialogContentView;

	public enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	public Status status = Status.welcome;

	public Debug1View(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public DisplayMetrics displayMetrics;

	public void initView() {
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mInflater = thisActivity.getLayoutInflater();
		animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(20)).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(thisActivity));

		thisActivity.setContentView(R.layout.activiry_debug1_image_list);

		dialogContentView = (RelativeLayout) thisActivity.findViewById(R.id.dialogContent);

		addMonyImageUploadView = (TextView) thisActivity.findViewById(R.id.rightTopText);

		listView = (ListView) thisActivity.findViewById(R.id.view_element_debug1_list);
		transportingList = new TransportingList();
		transportingList.initialize(listView);

		controlProgressView = thisActivity.findViewById(R.id.title_control_progress_container);
		titleControlProgress = new ControlProgress();
		titleControlProgress.initialize(controlProgressView, displayMetrics);

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
			for (int i = 0; i < data.localStatus.localData.prepareUploadImagesList.size(); i++) {
				TransportingItem transportingItem = new TransportingItem();
				transportingItems.add(transportingItem);

				ImageBean imageBean = data.localStatus.localData.prepareUploadImagesList.get(i);

				View transportingItemView = transportingItem.initialize(imageBean);
				String path = imageBean.path;
				// if (imageBean.multipart == null) {
				// imageBean.multipart = thisController.uploadFile(path);
				// }
				//
				// imageBean.multipart.transportingItem = transportingItem;
				// transportingItem.uploadMultipart = imageBean.multipart;
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

			public UploadMultipart uploadMultipart;

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
				String imageUri = "file://" + path;
				imageLoader.displayImage(imageUri, imageView, options, animateFirstListener);

				text_filename_view = (TextView) transportingItemView.findViewById(R.id.text_filename);
				text_filename_view.setText(path.substring(path.lastIndexOf("/") + 1));

				text_file_size_view = (TextView) transportingItemView.findViewById(R.id.text_file_size);
				text_file_size_view.setText("0/" + imageSource.size + "k");

				text_transport_time_view = (TextView) transportingItemView.findViewById(R.id.text_transport_time);
				text_transport_time_view.setText("0ms");

				this.controlProgressView = transportingItemView.findViewById(R.id.list_item_progress_container);
				this.controlProgress = new ControlProgress();
				this.controlProgress.initialize(this.controlProgressView, displayMetrics);
				// if (imageSource.multipart != null) {
				// long time = imageSource.multipart.time.received - imageSource.multipart.time.start;
				// if (time < 0) {
				// time = 0;
				// }
				// text_transport_time_view.setText(time + "ms");
				// long size = Long.valueOf(imageSource.size);
				// int currentUploadSize = (int) Math.floor(size * imageSource.multipart.uploadPrecent / 100f);
				// text_file_size_view.setText(currentUploadSize + "/" + size + "k");
				// this.controlProgress.moveTo(imageSource.multipart.uploadPrecent);
				// }

				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
				this.controlProgress.progress_line1.setLayoutParams(params);
				this.controlProgress.progress_line2.setLayoutParams(params);
				return transportingItemView;
			}
		}
	}

	public interface UploadLoading {
		public void loading(View v);
	}

	public void setUploadLoading(UploadLoading uploadLoading) {
		// UploadLoading uploadLoadings = uploadLoading;
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
}