package com.open.welinks.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
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
import com.open.welinks.customListener.ThumbleListener;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ViewManage;

public class FileHandlers {

	public String tag = "FileHandlers";
	public MyLog log = new MyLog(tag, false);

	public static FileHandlers fileHandlers;
	public static AudioHandlers audioHandlers;

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
	public File sdcardGifImageFolder;

	public Handler handler = new Handler();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public OnDownloadListener onDownloadListener;

	public Bitmap defaultBitmap;

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public static FileHandlers getInstance() {
		if (fileHandlers == null) {
			fileHandlers = new FileHandlers();
			audioHandlers = AudioHandlers.getInstance();
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
		sdcardGifImageFolder = new File(sdcardFolder, "gifs");
		if (!sdcardGifImageFolder.exists()) {
			sdcardGifImageFolder.mkdirs();
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
					imageLoader.displayImage("file://" + instance.path, imageView, instance.options, new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							if (imageView.getTag(R.id.tag_first) != null) {
								if (imageView.getTag(R.id.tag_fifth) == null) {
									float screenWidth = viewManage.mainView.displayMetrics.widthPixels;
									int height = (int) (loadedImage.getHeight() * (screenWidth / loadedImage.getWidth()));
									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) screenWidth, height);
									imageView.setLayoutParams(params);
								}
							}
						}
					});
				}
				if (instance.thumbleListener != null) {
					instance.thumbleListener.onResult();
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
				if (instance.thumbleListener != null) {
					instance.thumbleListener.onResult();
				}
			}
		};
	}

	public void getImage(String fileName, final ImageView imageView, final LayoutParams params, final int type, final DisplayImageOptions options) {
		File file = null;
		String excessivePath = "", excessiveUrl = "";
		if (fileName == null || "".equals(fileName)) {
			return;
		} else {
			if (type == DownloadFile.TYPE_IMAGE) {
				file = new File(sdcardImageFolder, fileName);
				excessivePath = file.getAbsolutePath();
				excessiveUrl = API.DOMAIN_COMMONIMAGE + "images/" + fileName;
			} else if (type == DownloadFile.TYPE_GIF_IMAGE) {
				file = new File(sdcardGifImageFolder, fileName);
				excessivePath = file.getAbsolutePath();
				excessiveUrl = API.DOMAIN_COMMONIMAGE + "gifs/" + fileName;
			}
			final String path = excessivePath;
			final String url = excessiveUrl;
			if (file.exists()) {
				imageLoader.displayImage("file://" + path, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						downloadImageFile(url, path, imageView, options, type, null);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (imageView.getTag(R.id.tag_first) != null) {
							if (params == null) {
								float screenWidth = viewManage.mainView.displayMetrics.widthPixels;
								int height = (int) (loadedImage.getHeight() * (screenWidth / loadedImage.getWidth()));
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) screenWidth, height);
								imageView.setLayoutParams(params);
							}
						}
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, type, null);
			}
		}

	}

	public void getImage(String fileName, final ImageView imageView, final DisplayImageOptions options) {
		getImage(fileName, imageView, null, DownloadFile.TYPE_IMAGE, options);
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
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_HEAD_IMAGE, null);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_HEAD_IMAGE, null);
			}
		}
	}

	public int THUMBLE_TYEP_SQUARE = 0x01;
	public int THUMBLE_TYEP_GROUP = 0x02;
	public int THUMBLE_TYEP_CHAT = 0x03;

	public void getThumbleImage(String fileName, final ImageView imageView, int width, int height, final DisplayImageOptions options, int thumbleType, final ThumbleListener thumbleListener) {
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
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_THUMBLE_IMAGE, thumbleListener);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						// log.e(path+"-complete");
						if (thumbleListener != null) {
							thumbleListener.onResult();
						}
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_THUMBLE_IMAGE, thumbleListener);
			}
		}
	}

	public void getGifImage(String fileName, GifImageView imageView) {
		File imageFile = new File(sdcardGifImageFolder, fileName);
		final String path = imageFile.getAbsolutePath();
		final String url = API.DOMAIN_COMMONIMAGE + "gifs/" + fileName;
		if (imageFile.exists()) {
			GifDrawable gifFromFile = null;
			try {
				gifFromFile = new GifDrawable(imageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			imageView.setImageDrawable(gifFromFile);
		} else {
			downloadGifFile(url, path, imageView);
		}
	}

	private void downloadGifFile(String url, String path, GifImageView imageView) {
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.path = path;
		downloadFile.view = imageView;
		downloadFile.setDownloadFileListener(new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				GifDrawable gifFromFile = null;
				try {
					gifFromFile = new GifDrawable("file://" + instance.path);
				} catch (IOException e) {
					e.printStackTrace();
				}
				((GifImageView) instance.view).setImageDrawable(gifFromFile);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
			}
		});
		downloadFileList.addDownloadFile(downloadFile);
	}

	private void downloadImageFile(String url, String path, ImageView imageView, DisplayImageOptions options, int downloadType, ThumbleListener thumbleListener) {
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = imageView;
		downloadFile.options = options;
		downloadFile.type = downloadType;
		downloadFile.thumbleListener = thumbleListener;
		downloadFile.setDownloadFileListener(onDownloadListener);
		downloadFileList.addDownloadFile(downloadFile);
	}

	public void downloadVoiceFile(File file, final String fileName, final int readSize, final boolean play) {
		DownloadFile downloadFile = new DownloadFile(API.DOMAIN_COMMONIMAGE + "voices/" + fileName, file.getAbsolutePath());
		downloadFile.setDownloadFileListener(new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				if (play) {
					audioHandlers.startPlay(fileName, readSize);
				}
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
			}
		});
		downloadFileList.addDownloadFile(downloadFile);
	}

	long startTime = 0;

	// TODO file deal with
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// long time = new Date().getTime();
		// log.e("Time:  " + (time - startTime) / 1000 + "s");
		// Runtime runtime = Runtime.getRuntime();
		// log.e("freeMemory:" + runtime.freeMemory() + ",TotalMomery:" + runtime.totalMemory() + ",MaxMomery:" + runtime.maxMemory());

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 0;

		float area = 0;
		do {
			inSampleSize++;
			area = height * width / (inSampleSize * inSampleSize);
		} while (area > 1000000);
		log.e("SampleSize1  ：" + inSampleSize + ",  height:" + height + ",  width:" + width + ",  area:" + area);
		return inSampleSize;
	}

	public ByteArrayOutputStream decodeSampledBitmapFromFileInputStream(File file, int reqWidth, int reqHeight) throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// options.inPurgeable = true;
		// options.inInputShareable = true;
		BitmapFactory.decodeStream(fileInputStream, null, options);
		try {
			fileInputStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BitmapFactory.Options options2 = new BitmapFactory.Options();
		options2.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options2.inJustDecodeBounds = false;
		FileInputStream fileInputStream1 = new FileInputStream(file);
		Bitmap bitmap = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			// options.inPreferredConfig = Config.RGB_565;
			bitmap = BitmapFactory.decodeStream(fileInputStream1, null, options2);
			log.e("SampleSize2  ：" + options2.inSampleSize + ",  height:" + options2.outHeight + ",  width:" + options2.outWidth);
			byteArrayOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
			bitmap.recycle();
			System.gc();
		} catch (OutOfMemoryError oome) {
			log.e("*************OutOfMemoryError*************");
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
