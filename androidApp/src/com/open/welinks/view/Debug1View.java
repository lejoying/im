package com.open.welinks.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.open.welinks.controller.Debug1Controller;
import com.open.welinks.controller.UploadMultipart;
import com.open.welinks.model.Data;

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

	public LayoutInflater mInflater;

	public enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	public Status status = Status.welcome;

	public Debug1View(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(thisActivity));

		thisActivity.setContentView(R.layout.activiry_debug1_image_list);

		listView = (ListView) thisActivity
				.findViewById(R.id.view_element_debug1_list);
		transportingList = new TransportingList();
		transportingList.initialize(listView);

		controlProgressView = thisActivity
				.findViewById(R.id.title_control_progress_container);
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
			for (int i = 0; i < thisController.imagesSource.size(); i++) {
				TransportingItem transportingItem = new TransportingItem();
				transportingItems.add(transportingItem);

				View transportingItemView = transportingItem
						.initialize(thisController.imagesSource.get(i));
				transportingList.addFooterView(transportingItemView);
			}

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

			public View initialize(HashMap<String, String> imageSource) {
				transportingItemView = mInflater.inflate(
						R.layout.view_element_debug1_list_item, null);

				String path = imageSource.get("path");
				imageView = (ImageView) transportingItemView
						.findViewById(R.id.image);
				String imageUri = "file://" + path;
				imageLoader.displayImage(imageUri, imageView, options,
						animateFirstListener);

				text_filename_view = (TextView) transportingItemView
						.findViewById(R.id.text_filename);
				text_filename_view
						.setText(path.substring(path.lastIndexOf("/") + 1));

				text_file_size_view = (TextView) transportingItemView
						.findViewById(R.id.text_file_size);
				text_file_size_view.setText(imageSource.get("size") + "k");

				text_transport_time_view = (TextView) transportingItemView
						.findViewById(R.id.text_transport_time);
				text_transport_time_view.setText("27ms");

				this.controlProgressView = transportingItemView
						.findViewById(R.id.list_item_progress_container);
				this.controlProgress = new ControlProgress();
				this.controlProgress.initialize(this.controlProgressView);

				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, 2);
				this.controlProgress.progress_line1.setLayoutParams(params);
				this.controlProgress.progress_line2.setLayoutParams(params);
				UploadMultipart uploadMultipart = thisController.uploadFile(
						path, transportingItemView);
				uploadMultipart.controlProgress = controlProgress;
				this.uploadMultipart = uploadMultipart;
				transportingItemView.setTag(path);
				// controlProgress.setTag(uploadMultipart);
				return transportingItemView;
			}
		}
	}

	public interface UploadLoading {
		public void loading(View v);
	}

	public void setUploadLoading(UploadLoading uploadLoading) {
		UploadLoading uploadLoadings = uploadLoading;
	}

	public static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
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
			thisActivity.getWindowManager().getDefaultDisplay()
					.getMetrics(displayMetrics);
			move_progress_line1 = new TranslateAnimation(103, 0, 0, 0);

			progress_line1 = (ImageView) container
					.findViewById(R.id.progress_line1);
			progress_line2 = (ImageView) container
					.findViewById(R.id.progress_line2);
			controlProgressView = container;

			width = displayMetrics.widthPixels;

		}

		public void moveTo(int targetPercentage) {
			float position = targetPercentage / 100.0f * this.width;
			move_progress_line1 = new TranslateAnimation(
					(percentage - targetPercentage) / 100.0f * width, 0, 0, 0);
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