package com.open.welinks.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
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
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.utils.StreamParser;

public class FileHandlers {

	public String tag = "FileHandlers";

	public static FileHandlers fileHandlers;

	public File sdcard;
	public File sdcardFolder;
	public File sdcardImageFolder;
	public File sdcardVoiceFolder;
	public File sdcardHeadImageFolder;
	public File sdcardBackImageFolder;
	public File sdcardThumbnailFolder;
	public File sdcardSquareThumbnailFolder;
	public File sdcardSaveImageFolder;

	public Handler handler = new Handler();

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
				// Log.e("FileHandlers", instance.path + "----" + instance.url);
				// imageLoader.displayImage("drawable://" + R.drawable.face_man, (ImageView) instance.view, instance.options);
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
		if (!sdcard.exists()) {
			// sdcard = Environment.getDataDirectory();
			// sdcard = Environment.getRootDirectory();
			// sdcard = MainActivity.instance.getFilesDir();
			System.out.println(sdcard.getAbsolutePath() + "---Memory");
		}
		sdcardFolder = new File(sdcard, "welinks");
		System.out.println(sdcardFolder.getAbsolutePath() + "---Memory1");
		if (!sdcardFolder.exists()) {
			boolean falg = sdcardFolder.mkdirs();
			System.out.println(sdcardFolder.exists() + "--" + falg + "-Memory3");
		}
		System.out.println(sdcardFolder.exists() + "---Memory2");
		sdcardImageFolder = new File(sdcardFolder, "images");
		if (!sdcardImageFolder.exists()) {
			sdcardImageFolder.mkdirs();
		}
		sdcardSaveImageFolder = new File(sdcardFolder, "SaveImages");
		if (!sdcardSaveImageFolder.exists()) {
			sdcardSaveImageFolder.mkdirs();
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
		sdcardSquareThumbnailFolder = new File(sdcardFolder, "squarethumbnails");
		if (!sdcardSquareThumbnailFolder.exists()) {
			sdcardSquareThumbnailFolder.mkdirs();
		}
	}

	public void getHeadImage(String fileName, final ImageView imageView, final DisplayImageOptions options) {
		imageLoader.displayImage("drawable://" + R.drawable.face_man, imageView, options);
		if (!fileName.equals("")) {
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
				// Log.e("FileHandlers", instance.path + "----" + instance.url);
				// imageLoader.displayImage("drawable://" + R.drawable.face_man, (ImageView) instance.view, instance.options);
			}
		});
		// System.out.println("--------------000------" + onDownloadListener);
		downloadFileList.addDownloadFile(downloadFile);
	}

	// TODO file deal with
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public ByteArrayOutputStream decodeSampledBitmapFromFileInputStream(File file, int reqWidth, int reqHeight) throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(fileInputStream, null, options);
		try {
			fileInputStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		options.inJustDecodeBounds = false;
		FileInputStream fileInputStream1 = new FileInputStream(file);
		Bitmap bitmap = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			bitmap = BitmapFactory.decodeStream(fileInputStream1, null, options);
			byteArrayOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
			bitmap.recycle();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			fileInputStream1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArrayOutputStream;
	}

	public ByteArrayOutputStream decodeSnapBitmapFromFileInputStream(File file, float reqWidth, float reqHeight) throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(fileInputStream, null, options);

		options.inSampleSize = calculateInSampleSize(options, (int) reqWidth, (int) reqHeight);

		options.inJustDecodeBounds = false;
		FileInputStream fileInputStream1 = new FileInputStream(file);
		Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream1, null, options);
		float ratio = reqWidth / reqHeight;
		if (options.outHeight < reqHeight) {
			reqHeight = options.outHeight;
			if (reqHeight * ratio < reqWidth) {
				reqWidth = reqHeight * ratio;
			}
		}
		if (options.outWidth < reqWidth) {
			reqWidth = options.outWidth;
			if (reqWidth / ratio < reqHeight) {
				reqHeight = reqWidth / ratio;
			}
		}

		Bitmap snapbitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) reqWidth, (int) reqHeight);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		snapbitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
		bitmap.recycle();
		snapbitmap.recycle();
		return byteArrayOutputStream;
	}

//	public byte[] bytes;

	public byte[] getImageFileBytes(File fromFile, int width, int height) {

		long fileLength = fromFile.length();
		try {
			byte[] bytes;
			if (fileLength > 400 * 1024) {
				ByteArrayOutputStream byteArrayOutputStream = decodeSampledBitmapFromFileInputStream(fromFile, width, height);
				bytes = byteArrayOutputStream.toByteArray();
				byteArrayOutputStream.close();
			} else {
				FileInputStream fileInputStream = new FileInputStream(fromFile);
				bytes = StreamParser.parseToByteArray(fileInputStream);
				fileInputStream.close();
			}
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void makeImageThumbnail(File fromFile, int showImageWidth, int showImageHeight, File toSnapFile, String fileName) {
		try {
			ByteArrayOutputStream snapByteStream = decodeSnapBitmapFromFileInputStream(fromFile, showImageWidth, showImageHeight);
			byte[] snapBytes = snapByteStream.toByteArray();
			FileOutputStream toSnapFileOutputStream = new FileOutputStream(toSnapFile);
			Log.d(tag, "file saved to " + fileName);
			StreamParser.parseToFile(snapBytes, toSnapFileOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
