package com.open.welinks;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;

public class ImageDetailFragment extends Fragment {
	// private static final String IMAGE_DATA_EXTRA = "resId";
	// private int mImageNum;
	private ImageView mImageView;
	// private ImageWorker mImageWorker;
	private ProgressBar mProgressBar;

	public String path;

	public static ImageDetailFragment newInstance(int imageNum, String path) {

		ImageDetailFragment f = new ImageDetailFragment();
		f.path = path;

		// final Bundle args = new Bundle();
		// args.putInt(IMAGE_DATA_EXTRA, imageNum);
		// f.setArguments(args);

		return f;
	}

	public ImageDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);
		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		return v;
	}

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public DisplayImageOptions options;
	public OnDownloadListener downloadListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();
		downloadListener = new OnDownloadListener() {

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, options);
			}

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
			}
		};
		if (ImageScanActivity.class.isInstance(getActivity())) {
			if (path.lastIndexOf("/") == -1) {
				File sdFile = Environment.getExternalStorageDirectory();
				File file = new File(sdFile, "welinks/images/" + path);
				path = file.getAbsolutePath();
			}
			final String path0 = path;
			final String url = API.DOMAIN_COMMONIMAGE + "images/" + path.substring(path.lastIndexOf("/") + 1);
			imageLoader.displayImage("file://" + path0, mImageView, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					mProgressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					DownloadFile downloadFile = new DownloadFile(url, path);
					downloadFile.view = view;
					downloadFile.setDownloadFileListener(downloadListener);
					downloadFileList.addDownloadFile(downloadFile);
					mProgressBar.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					mProgressBar.setVisibility(View.GONE);
				}
			});
		}
	}

	public void cancelWork() {
		mImageView.setImageDrawable(null);
		mImageView = null;
	}
}
