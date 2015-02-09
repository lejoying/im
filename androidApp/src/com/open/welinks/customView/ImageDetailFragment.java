package com.open.welinks.customView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.ImageScanActivity;
import com.open.welinks.R;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.oss.DownloadFileList;

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

	TextView fileSizeView;
	TextView fileWidthView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);
		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		fileSizeView = (TextView) v.findViewById(R.id.filesize);
		fileWidthView = (TextView) v.findViewById(R.id.filewidth);
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

			@Override
			public void onLoadingStarted(DownloadFile instance, int precent, int status) {
				// super.onLoadingStarted(instance, precent, status);
				// fileSizeView.setText("Content-Length：" + instance.bytesLength);
			}
		};
		if (ImageScanActivity.class.isInstance(getActivity())) {
			if (path.lastIndexOf("/") == -1) {
				FileHandlers fileHandlers = FileHandlers.getInstance();
				File file = new File(fileHandlers.sdcardImageFolder, path);
				path = file.getAbsolutePath();
			}
			final String path0 = path;
			final String url = API.DOMAIN_COMMONIMAGE + "images/" + path.substring(path.lastIndexOf("/") + 1);
			final File imageFile = new File(path);
			final int length = (int) imageFile.length();
			if (imageFile.exists()) {

				imageLoader.displayImage("file://" + path0, mImageView, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						mProgressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						DownloadFile downloadFile = new DownloadFile(url, path);
						downloadFile.view = mImageView;
						downloadFile.setDownloadFileListener(downloadListener);
						downloadFileList.addDownloadFile(downloadFile);
						mProgressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						mProgressBar.setVisibility(View.GONE);
						fileSizeView.setText("Content-Length：" + length);
						final BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
						try {
							FileInputStream fileInputStream = new FileInputStream(imageFile);

							BitmapFactory.decodeStream(fileInputStream, null, options);
							try {
								fileInputStream.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							fileWidthView.setText("BitmapWidth:" + loadedImage.getWidth() + ",BitmapHeight:" + loadedImage.getHeight() + "\n" + "Width:" + options.outWidth + ",Height:" + options.outHeight);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
			} else {
				DownloadFile downloadFile = new DownloadFile(url, path);
				downloadFile.view = mImageView;
				downloadFile.setDownloadFileListener(downloadListener);
				downloadFileList.addDownloadFile(downloadFile);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}
	}

	public void cancelWork() {
		mImageView.setImageDrawable(null);
		mImageView = null;
	}
}
