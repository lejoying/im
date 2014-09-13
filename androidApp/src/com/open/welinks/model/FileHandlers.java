package com.open.welinks.model;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.OnDownloadListener;

public class FileHandlers {

	public static FileHandlers fileHandlers;

	public File sdcard;
	public File sdcardFolder;
	public File sdcardImageFolder;
	public File sdcardVoiceFolder;
	public File sdcardHeadImageFolder;
	public File sdcardBackImageFolder;
	public File sdcardThumbnailFolder;

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public OnDownloadListener onDownloadListener;

	public Bitmap defaultBitmap;

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	{
		onDownloadListener = new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, instance.options);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
				Log.e("FileHandlers", instance.path + "----" + instance.url);
				imageLoader.displayImage("drawable://" + R.drawable.face_man, (ImageView) instance.view, instance.options);
			}
		};
	}

	public class Bitmaps {
		public Map<String, SoftReference<Bitmap>> softBitmaps = new Hashtable<String, SoftReference<Bitmap>>();

		public void put(String key, Bitmap bitmap) {
			softBitmaps.put(key, new SoftReference<Bitmap>(bitmap));
		}

		public Bitmap get(String key) {
			if (softBitmaps.get(key) == null) {
				return null;
			}
			return softBitmaps.get(key).get();
		}
	}

	public Bitmaps bitmaps = new Bitmaps();

	public static FileHandlers getInstance() {
		if (fileHandlers == null) {
			fileHandlers = new FileHandlers();
		}
		return fileHandlers;
	}

	public FileHandlers() {
		sdcard = Environment.getExternalStorageDirectory();
		sdcardFolder = new File(sdcard, "welinks");
		if (!sdcardFolder.exists()) {
			sdcardFolder.mkdirs();
		}
		sdcardImageFolder = new File(sdcardFolder, "images");
		if (!sdcardImageFolder.exists()) {
			sdcardImageFolder.mkdirs();
		}
		sdcardVoiceFolder = new File(sdcardFolder, "voices");
		if (!sdcardVoiceFolder.exists()) {
			sdcardVoiceFolder.mkdirs();
		}
		sdcardHeadImageFolder = new File(sdcardFolder, "heads");
		if (!sdcardHeadImageFolder.exists()) {
			sdcardHeadImageFolder.mkdirs();
		}
		sdcardBackImageFolder = new File(sdcardFolder, "backgrounds");
		if (!sdcardBackImageFolder.exists()) {
			sdcardBackImageFolder.mkdirs();
		}
		sdcardThumbnailFolder = new File(sdcardFolder, "thumbnails");
		if (!sdcardThumbnailFolder.exists()) {
			sdcardThumbnailFolder.mkdirs();
		}
	}

	public void getHeadImage(String fileName, final ImageView imageView, final DisplayImageOptions options) {
		if (fileName.equals("")) {
			imageLoader.displayImage("drawable://" + R.drawable.face_man, imageView, options);
		} else {
			File imageFile = new File(sdcardHeadImageFolder, fileName);
			final String path = imageFile.getAbsolutePath();
			final String url = API.DOMAIN_COMMONIMAGE + "heads/" + fileName;
			if (imageFile.exists()) {
				imageLoader.displayImage("file://" + path, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						downloadHeadFile(url, path, imageView, options);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				});
			} else {
				downloadHeadFile(url, path, imageView, options);
			}
		}
	}

	public void downloadHeadFile(String url, String path, ImageView imageView, DisplayImageOptions options) {
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = imageView;
		downloadFile.options = options;
		downloadFile.setDownloadFileListener(new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, instance.options);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
				Log.e("FileHandlers", instance.path + "----" + instance.url);
				imageLoader.displayImage("drawable://" + R.drawable.face_man, (ImageView) instance.view, instance.options);
			}
		});
		System.out.println("--------------000------" + onDownloadListener);
		downloadFileList.addDownloadFile(downloadFile);
	}
}
