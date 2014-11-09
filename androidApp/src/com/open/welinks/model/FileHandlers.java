package com.open.welinks.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ViewManage;

public class FileHandlers {

	public String tag = "FileHandlers";
	public MyLog log = new MyLog(tag, true);

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
	public File sdcardCacheImageFolder;

	public Handler handler = new Handler();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public OnDownloadListener onDownloadListener;

	public Bitmap defaultBitmap;

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public static FileHandlers getInstance() {
		if (fileHandlers == null) {
			fileHandlers = new FileHandlers();
		}
		return fileHandlers;
	}

	@SuppressLint("SdCardPath")
	public File getSdCardFile() {
		File sdcard = Environment.getExternalStorageDirectory();
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			sdcard = new File("/sdcard/");
			log.e("sdcard  spance:" + sdcard.getFreeSpace());
			if (sdcard.getFreeSpace() == 0) {
				sdcard = new File("/sdcard1/");
				log.e("sdcard1 space:" + sdcard.getFreeSpace());
			}
			if (sdcard.getFreeSpace() == 0) {
				sdcard = new File("/sdcard2/");
				log.e("sdcard2 space:" + sdcard.getFreeSpace());
			}
		}
		return sdcard;
	}

	public FileHandlers() {
		sdcard = getSdCardFile();
		sdcardFolder = new File(sdcard, "welinks");
		if (!sdcardFolder.exists()) {
			sdcardFolder.mkdirs();
		}
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
		sdcardCacheImageFolder = new File(sdcardFolder, "cache");
		if (!sdcardCacheImageFolder.exists()) {
			sdcardCacheImageFolder.mkdirs();
		}

		initListener();
	}

	private void initListener() {
		onDownloadListener = new OnDownloadListener() {
			@Override
			public void onSuccess(final DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				if (instance.type == DownloadFile.TYPE_HEAD_IMAGE) {
					imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, instance.options);
				} else if (instance.type == DownloadFile.TYPE_THUMBLE_IMAGE) {
					imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, instance.options);
				} else if (instance.type == DownloadFile.TYPE_IMAGE) {
					final ImageView imageView = ((ImageView) instance.view);
					imageLoader.displayImage("file://" + instance.path, imageView, new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							if (imageView.getTag(R.id.tag_first) != null) {
								float screenWidth = viewManage.mainView.displayMetrics.widthPixels;
								int height = (int) (loadedImage.getHeight() * (screenWidth / loadedImage.getWidth()));
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) screenWidth, height);
								imageView.setLayoutParams(params);
							}
						}
					});
				}
				// log.e("200----download:     " + instance.url + "-" + instance.path + "-path");
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
				if (instance.type == DownloadFile.TYPE_HEAD_IMAGE) {
					// imageLoader.displayImage("drawable://" + R.drawable.face_man, imageView, options);
				} else if (instance.type == DownloadFile.TYPE_THUMBLE_IMAGE) {
					log.e("onFailure----download:" + instance.url + "-" + instance.path + "-path");
				} else if (instance.type == DownloadFile.TYPE_IMAGE) {
					log.e("onFailure----download:" + instance.url + "-" + instance.path + "-path");
				}
			}
		};
	}

	public void getImage(String fileName, final ImageView imageView, final DisplayImageOptions options) {
		if (fileName == null || "".equals(fileName)) {
			return;
		} else {
			File file = new File(sdcardImageFolder, fileName);
			final String path = file.getAbsolutePath();
			final String url = API.DOMAIN_COMMONIMAGE + "images/" + fileName;
			if (file.exists()) {
				imageLoader.displayImage("file://" + path, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_IMAGE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (imageView.getTag(R.id.tag_first) != null) {
							float screenWidth = viewManage.mainView.displayMetrics.widthPixels;
							int height = (int) (loadedImage.getHeight() * (screenWidth / loadedImage.getWidth()));
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) screenWidth, height);
							imageView.setLayoutParams(params);
						}
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_IMAGE);
			}
		}
	}

	public void getHeadImage(String fileName, final ImageView imageView, final DisplayImageOptions options) {
		imageLoader.displayImage("drawable://" + R.drawable.face_man, imageView, options);
		if (fileName != null && !"".equals(fileName)) {
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
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_HEAD_IMAGE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_HEAD_IMAGE);
			}
		}
	}

	public int THUMBLE_TYEP_SQUARE = 0x01;
	public int THUMBLE_TYEP_GROUP = 0x02;
	public int THUMBLE_TYEP_CHAT = 0x03;

	public void getThumbleImage(String fileName, final ImageView imageView, int width, int height, final DisplayImageOptions options, int thumbleType) {
		// type : square or group
		if (fileName == null || "".equals(fileName)) {
			if (thumbleType == THUMBLE_TYEP_SQUARE) {
				// imageLoader.displayImage("drawable://" + R.drawable.icon, imageView, options);
			} else if (thumbleType == THUMBLE_TYEP_GROUP) {
			} else if (thumbleType == THUMBLE_TYEP_CHAT) {
				imageView.setBackgroundColor(Color.parseColor("#990099cd"));
			}
			// imageLoader.displayImage("drawable://" + R.drawable.icon, imageView);
			return;
		}
		final String url = API.DOMAIN_OSS_THUMBNAIL + "images/" + fileName + "@" + width + "w_" + height + "h_1c_1e_100q";
		File file = null;
		if (thumbleType == THUMBLE_TYEP_SQUARE) {
			file = new File(sdcardSquareThumbnailFolder, fileName);
		} else if (thumbleType == THUMBLE_TYEP_GROUP) {
			file = new File(sdcardThumbnailFolder, fileName);
		} else if (thumbleType == THUMBLE_TYEP_CHAT) {
			file = new File(sdcardThumbnailFolder, fileName);
		}
		if (file != null) {
			final String path = file.getAbsolutePath();
			if (file.exists()) {
				imageLoader.displayImage("file://" + path, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						imageView.setBackgroundColor(Color.parseColor("#990099cd"));
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_THUMBLE_IMAGE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						log.e(path+"-complete");
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_THUMBLE_IMAGE);
			}
		}
	}

	private void downloadImageFile(String url, String path, ImageView imageView, DisplayImageOptions options, int downloadType) {
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = imageView;
		downloadFile.options = options;
		downloadFile.type = downloadType;
		downloadFile.setDownloadFileListener(onDownloadListener);
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

	// public byte[] bytes;

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
