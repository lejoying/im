package com.open.welinks.view;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.ImageScanActivity;
import com.open.welinks.R;

public class ImageDetailFragment extends Fragment {
	private static final String IMAGE_DATA_EXTRA = "resId";
	// private int mImageNum;
	private ImageView mImageView;
	// private ImageWorker mImageWorker;
	private ProgressBar mProgressBar;

	public static String path;

	public static ImageDetailFragment newInstance(int imageNum, String path) {

		ImageDetailFragment.path = path;
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putInt(IMAGE_DATA_EXTRA, imageNum);
		f.setArguments(args);

		return f;
	}

	public ImageDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);
		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		return v;
	}

	ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (ImageScanActivity.class.isInstance(getActivity())) {
			imageLoader.displayImage(path, mImageView, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					mProgressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					mProgressBar.setVisibility(View.GONE);
				}
			});
		}
	}

	public void cancelWork() {
		// ImageWorker.cancelWork(mImageView);
		mImageView.setImageDrawable(null);
		mImageView = null;
	}
}
