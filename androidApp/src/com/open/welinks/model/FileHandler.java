package com.open.welinks.model;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customListener.ThumbleListener;
import com.open.welinks.model.SubData.ImageListener;
import com.open.welinks.model.SubData.ShareContentItem;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.oss.DownloadFileList;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ViewManage;

public class FileHandler {

	public static String tag = "FileHandler";
	public static MyLog log = new MyLog(tag, true);

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

	public static FileHandler fileHandler;

	public static FileHandler getInstance() {
		if (fileHandler == null) {
			fileHandler = new FileHandler();
		}
		return fileHandler;
	}

	public FileResolveRunnable fileResolveRunnable;
	public FileUploadRunnable fileUploadRunnable;

	public void startLoop() {
		if (this.isIntialized) {
			log.e("startLoop");
			this.fileResolveRunnable.start();
			new Thread(this.fileUploadRunnable).start();
		}
	}

	public boolean isIntialized = false;

	public void initialize() {
		if (this.isIntialized == false) {
			this.setSdcardFile();
			this.fileResolveRunnable = new FileResolveRunnable();
			this.fileUploadRunnable = new FileUploadRunnable();
			this.myFileUploadQueue = new MyLinkedListQueue<MyFile>();
			this.myFileUploadQueue.currentRunnable = fileUploadRunnable;
			this.myFileQueue = new LinkedBlockingQueue<MyFile>();

			this.isIntialized = true;
		}
	}

	// public TaskManager mTaskManager = TaskManager.getInstance();
	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();
	public Data data = Data.getInstance();

	public LinkedBlockingQueue<MyFile> myFileQueue;

	public void pushMyFile(MyFile myFile) {
		long currentTime = SystemClock.uptimeMillis();
		myFile.startTime = currentTime;

		myFileQueue.offer(myFile);
		myFile.status.state = myFile.status.Queueing;
	}

	class FileResolveRunnable extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					MyFile myFile = myFileQueue.take();
					if (myFile == null) {
					} else {
						if (myFile.uploadFileType == myFile.UPLOAD_TYPE_IMAGE || myFile.uploadFileType == myFile.UPLOAD_TYPE_BACKGROUND || myFile.uploadFileType == myFile.UPLOAD_TYPE_HEAD) {
							resolveImageFile(myFile);
							myFile.task.currentResolveFileCount++;
							if (myFile.task.currentResolveFileCount == myFile.task.resolveFileTotal) {
								myFile.task.onLocalFilesResolved();
							}
						} else if (myFile.uploadFileType == myFile.UPLOAD_TYPE_VOICE) {
							// TODO voice
							// resolveVoiceOrGifFile(myFile, taskManageHolder.fileHandler.sdcardVoiceFolder, ".osa");
							// myFile.task.currentResolveFileCount++;
							// if (myFile.task.currentResolveFileCount == myFile.task.resolveFileTotal) {
							// myFile.task.onLocalFilesResolved();
							// }
						} else if (myFile.uploadFileType == myFile.UPLOAD_TYPE_Gif) {
							// resolveVoiceOrGifFile(myFile, taskManageHolder.fileHandler.sdcardGifImageFolder, ".osg");
							// myFile.task.currentResolveFileCount++;
							// if (myFile.task.currentResolveFileCount == myFile.task.resolveFileTotal) {
							// myFile.task.onLocalFilesResolved();
							// }
						}
						myFile.status.state = myFile.status.LocalStored;
						myFileUploadQueue.offerE(myFile);
					}
				} catch (Exception e) {
					log.e(e.toString());
					// StackTraceElement ste = new Throwable().getStackTrace()[0];
					// log.e("Exception@" + ste.getLineNumber() + "," + ExceptionHandler.printStackTrace(MainActivity.instance.thisController.context, e));
				}
			}
		}
	}

	// public LinkedBlockingQueue<MyFile> myFileUploadQueue2 = new LinkedBlockingQueue<MyFile>();
	// public LinkedList<MyFile> myFileUploadQueue = new LinkedList<MyFile>();

	public MyLinkedListQueue<MyFile> myFileUploadQueue;

	class FileUploadRunnable implements Runnable {

		@Override
		public void run() {
			log.e("while>>>>>>>>>>>>:" + myFileUploadQueue.isRunning);
			while (myFileUploadQueue.isRunning) {
				try {
					MyFile myFile = myFileUploadQueue.takeE();
					if (myFile == null) {
						log.e("Break");
						// break;
					} else {
						log.e("My:" + myFile.status.state);
						if (myFile.status.state == myFile.status.LocalStored) {
							log.e("state LocalStored");
							checkFile(myFile);
						} else if (myFile.status.state == myFile.status.Checked) {
							log.e("state Checked");
							initiateUpLoad(myFile);
						} else if (myFile.status.state == myFile.status.Initialized) {
							log.e("state Initialized");
							upLoadFile(myFile);
						} else if (myFile.status.state == myFile.status.Uploaded) {
							log.e("state Uploaded");
							completeFile(myFile);
						} else if (myFile.status.state == myFile.status.Completed) {
							log.e("state Completed");
							recordFileName(myFile);
						}
					}
				} catch (Exception e) {
					log.e("Exception:" + e.toString());
					// StackTraceElement ste = new Throwable().getStackTrace()[0];
					// log.e("Exception@" + ste.getLineNumber() + "," + ExceptionHandler.printStackTrace(MainActivity.instance.thisController.context, e));
				}
			}
		}
	}

	public void checkFile(MyFile myFile) throws Exception {
		log.e("checkFile");
		myFile.status.state = myFile.status.Checking;
		taskManageHolder.multipartUploader.checkFileExists(myFile);
	}

	public void onCheckFile(MyFile myFile) throws Exception {
		log.e("onCheckFile" + myFile.isExists);
		if (myFile.isExists) {
			myFile.status.state = myFile.status.Completed;
			myFile.bytes = null;
			taskManageHolder.taskManager.onMyFileUploaded(myFile);
			System.gc();
			Thread.sleep(50);
		} else {
			myFile.status.state = myFile.status.Checked;
			myFileUploadQueue.offerE(myFile);
		}
	}

	public void initiateUpLoad(MyFile myFile) {
		log.e("initiateUpLoad");
		myFile.status.state = myFile.status.Initializing;
		taskManageHolder.multipartUploader.initiateUpLoad(myFile);
	}

	public void onInitiateUpLoad(MyFile myFile) {
		log.e("onInitiateUpLoad");
		myFile.status.state = myFile.status.Initialized;
		myFileUploadQueue.offerE(myFile);
	}

	public void upLoadFile(MyFile myFile) throws Exception {
		log.e("upLoadFile");
		myFile.status.state = myFile.status.Uploading;
		taskManageHolder.multipartUploader.uploadParts(myFile);
	}

	public void onUpLoadFile(MyFile myFile) {
		log.e("onUpLoadFile");
		myFile.status.state = myFile.status.Uploaded;
		myFileUploadQueue.offerE(myFile);
	}

	public void completeFile(MyFile myFile) {
		log.e("completeFile");
		myFile.status.state = myFile.status.Completing;
		taskManageHolder.multipartUploader.completeFile(myFile);
	}

	public void onCompleteFile(MyFile myFile) {
		log.e("onCompleteFile");
		myFile.status.state = myFile.status.Completed;
		myFileUploadQueue.offerE(myFile);
	}

	public void recordFileName(MyFile myFile) throws Exception {
		log.e("recordFileName");
		myFile.bytes = null;
		taskManageHolder.taskManager.onMyFileUploaded(myFile);
		taskManageHolder.multipartUploader.recordFileName(myFile);
		System.gc();
		Thread.sleep(50);
	}

	private void checkUploadFileType(MyFile myFile, String path) {
		String suffixName = path.substring(path.lastIndexOf(".") + 1);
		myFile.suffixName = suffixName.toLowerCase(Locale.getDefault());
		if ("jpg".equals(suffixName) || "osj".equals(suffixName)) {
			myFile.uploadFileType = myFile.UPLOAD_TYPE_IMAGE;
			myFile.suffixName = ".osj";
		} else if ("jpeg".equals(suffixName) || "osj".equals(suffixName)) {
			myFile.uploadFileType = myFile.UPLOAD_TYPE_IMAGE;
			myFile.suffixName = ".osj";
		} else if ("png".equals(suffixName) || "osp".equals(suffixName)) {
			myFile.uploadFileType = myFile.UPLOAD_TYPE_IMAGE;
			myFile.suffixName = ".osp";
		} else if ("osa".equals(suffixName)) {
			myFile.uploadFileType = myFile.UPLOAD_TYPE_VOICE;
			myFile.suffixName = ".osa";
		} else if ("gif".equals(suffixName) || "osg".equals(suffixName)) {
			myFile.uploadFileType = myFile.UPLOAD_TYPE_Gif;
			myFile.suffixName = ".osg";
		}
	}

	void resolveVoiceOrGifFile(MyFile myFile, File folder, String suffexName) throws Exception {
		String filePath = myFile.path;
		myFile.suffixName = suffexName;
		File fromFile = new File(filePath);
		myFile.bytes = StreamParser.parseToByteArray(fromFile);
		if (myFile.bytes == null) {
			return;
		}
		myFile.shaStr = sha1.getDigestOfString(myFile.bytes);
		myFile.fileName = myFile.shaStr + myFile.suffixName;
		File toFile = new File(folder, myFile.fileName);
		fromFile.renameTo(toFile);

		myFile.bytes = null;
		System.gc();
		Thread.sleep(50);
	}

	public int CompressThreshold = 400 * 1024;
	public SHA1 sha1 = SHA1.getInstance();

	void resolveImageFile(MyFile myFile) throws Exception {
		// 计算文件后缀名
		String filePath = myFile.path;
		String suffixName = filePath.substring(filePath.lastIndexOf("."));
		myFile.suffixName = suffixName.toLowerCase(Locale.getDefault());
		if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
			myFile.suffixName = ".osj";
		} else if (suffixName.equals(".png")) {
			myFile.suffixName = ".osp";
		}
		File file = new File(filePath);

		// 压缩文件内容
		if (myFile.type.type == myFile.type.Image) {
			myFile.length = file.length();
			if (myFile.isCompression && myFile.length > CompressThreshold) {
				compressFile(myFile);
			} else {
				FileInputStream fileInputStream = new FileInputStream(file);
				myFile.bytes = StreamParser.parseToByteArray(fileInputStream);
				fileInputStream.close();
			}
		}

		// 计算sha, length值
		if (myFile.bytes == null) {
			return;
		}
		myFile.shaStr = sha1.getDigestOfString(myFile.bytes);
		myFile.length = myFile.bytes.length;

		// 转存本地文件
		myFile.fileName = myFile.shaStr + myFile.suffixName;
		File toFile = new File(sdcardImageFolder, myFile.fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(toFile);
		StreamParser.parseToFile(myFile.bytes, fileOutputStream);

		myFile.bytes = null;
		System.gc();
		Thread.sleep(50);

	}

	public void compressFile(MyFile myFile) throws Exception {
		// 计算图片宽高
		File file = new File(myFile.path);
		FileInputStream fileInputStream = new FileInputStream(file);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(fileInputStream, null, options);
		fileInputStream.close();
		int height = options.outHeight;
		int width = options.outWidth;
		// 计算缩放比例
		int inSampleSize = calculateInSampleSize(height, width);

		// 压缩图片
		BitmapFactory.Options options2 = new BitmapFactory.Options();
		options2.inSampleSize = inSampleSize;
		options2.inJustDecodeBounds = false;
		FileInputStream fileInputStream1 = new FileInputStream(file);
		Bitmap bitmap = null;

		try {
			bitmap = BitmapFactory.decodeStream(fileInputStream1, null, options2);
			// log.e("SampleSize2  ：" + options2.inSampleSize + ",  height:" + options2.outHeight + ",  width:" + options2.outWidth);
		} catch (OutOfMemoryError oome) {
			StackTraceElement ste = new Throwable().getStackTrace()[1];
			log.e("*************OutOfMemoryError*************@" + ste.getLineNumber());
			fileInputStream1.close();
			return;
		}
		fileInputStream1.close();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
		myFile.bytes = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		bitmap.recycle();
		System.gc();
		Thread.sleep(50);
	}

	@SuppressLint("SdCardPath")
	public File getSdCardFile() {
		if (sdcard != null) {
			return sdcard;
		}
		List<String> list = getExtSDCardPath();
		boolean isRun = true;
		if (list.size() > 0) {
			sdcard = new File(list.get(list.size() - 1));
			if (sdcard.isDirectory()) {
				if (sdcard.getFreeSpace() == 0) {
					isRun = true;
				} else {
					isRun = false;
				}
			}
		}
		if (isRun) {
			sdcard = Environment.getExternalStorageDirectory();
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
		}
		log.e("data:" + sdcard.getAbsolutePath());
		return sdcard;
	}

	public List<String> getExtSDCardPath() {
		List<String> lResult = new ArrayList<String>();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("extSdCard")) {
					String[] arr = line.split(" ");
					String path = arr[1];
					if (path.lastIndexOf("extSdCard") == path.length() - 9) {
						File file = new File(path);
						if (file.isDirectory()) {
							lResult.add(path);
						}
					}
				} else if (line.contains("/sdcard")) {
					String[] arr = line.split(" ");
					String path = arr[1];
					if (path.lastIndexOf("/sdcard") == path.length() - 6) {
						File file = new File(path);
						if (file.isDirectory()) {
							lResult.add(path);
						}
					} else {
						String number = path.substring(path.lastIndexOf("/sdcard") + 7);
						try {
							Integer.parseInt(number);
							File file = new File(path);
							if (file.isDirectory()) {
								lResult.add(path);
							}
						} catch (Exception e) {
						}
					}
				}
			}
			isr.close();
		} catch (Exception e) {
		}
		return lResult;
	}

	public void setSdcardFile() {
		sdcard = getSdCardFile();
		sdcardFolder = new File(sdcard, "welinks");
		if (!sdcardFolder.exists()) {
			sdcardFolder.mkdirs();
		}
		sdcardImageFolder = new File(sdcardFolder, "images");
		if (!sdcardImageFolder.exists()) {
			sdcardImageFolder.mkdirs();
		}
	}

	public int calculateInSampleSize(int height, int width) {
		int inSampleSize = 0;

		float area = 0;
		do {
			inSampleSize++;
			area = height * width / (inSampleSize * inSampleSize);
		} while (area > 1000000);// constant
		// log.e("SampleSize1  ：" + inSampleSize + ",  height:" + height + ",  width:" + width + ",  area:" + area);
		return inSampleSize;
	}

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
			StackTraceElement ste = new Throwable().getStackTrace()[1];
			log.e("Exception@" + ste.getLineNumber());
		}
		return null;
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
			StackTraceElement ste = new Throwable().getStackTrace()[1];
			log.e("Exception@" + ste.getLineNumber());
		} catch (Exception e1) {
			e1.printStackTrace();
			StackTraceElement ste = new Throwable().getStackTrace()[1];
			log.e("Exception@" + ste.getLineNumber());
		}
		try {
			fileInputStream1.close();
		} catch (IOException e) {
			e.printStackTrace();
			StackTraceElement ste = new Throwable().getStackTrace()[1];
			log.e("Exception@" + ste.getLineNumber());
		}
		return byteArrayOutputStream;
	}

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

	void test(MyFile myFile) {
		taskManageHolder.taskManager.onMyFileUploaded(myFile);
	}

	// -------------------------------------

	public FileHandler() {
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

	public OnDownloadListener onDownloadListener;

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

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
									float screenWidth = data.baseData.screenWidth;
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
					// log.e("onFailure----download:" + instance.url + "-" + instance.path + "-path");
				} else if (instance.type == DownloadFile.TYPE_IMAGE) {
					// log.e("onFailure----download:" + instance.url + "-" + instance.path + "-path");
				}
				if (instance.thumbleListener != null) {
					instance.thumbleListener.onResult();
				}
			}
		};
	}

	public void getImage(String fileName, final ImageView imageView, final LayoutParams params, final int type, final DisplayImageOptions options, final ImageListener imageListener) {
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
				final File file2 = file;
				imageLoader.displayImage("file://" + path, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						file2.delete();
						downloadImageFile(url, path, imageView, options, type, null, imageListener);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (imageView.getTag(R.id.tag_first) != null) {
							if (params == null) {
								float screenWidth = data.baseData.screenWidth - 20 * data.baseData.density;
								int height = (int) (loadedImage.getHeight() * (screenWidth / loadedImage.getWidth()));
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) screenWidth, height);
								imageView.setLayoutParams(params);
							}
							if (imageListener != null) {
								float width = loadedImage.getWidth();
								float height = loadedImage.getHeight();
								imageListener.onSuccess(height / width);
							}
						}
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, type, null, imageListener);
			}
		}

	}

	public void getBackImage(String fileName, final ImageView imageView, final DisplayImageOptions options) {
		imageLoader.displayImage("drawable://" + R.drawable.tempicon, imageView);
		if (fileName != null && !"".equals(fileName)) {
			final File imageFile = new File(sdcardBackImageFolder, fileName);
			final String path = imageFile.getAbsolutePath();
			final String url = API.DOMAIN_COMMONIMAGE + "backgrounds/" + fileName;
			if (imageFile.exists()) {
				imageLoader.displayImage("file://" + path, imageView, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						imageFile.delete();
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_IMAGE, null, null);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_IMAGE, null, null);
			}
		}
	}

	public void getImage(String fileName, final ImageView imageView, final DisplayImageOptions options, ImageListener imageListener) {
		getImage(fileName, imageView, null, DownloadFile.TYPE_IMAGE, options, imageListener);
	}

	public void getHeadImage(String fileName, final ImageView imageView, final DisplayImageOptions options) {
		getHeadImage(fileName, imageView, options, null);
	}

	public void getHeadImage(String fileName, final ImageView imageView, final DisplayImageOptions options, final ImageListener imageListener) {
		imageLoader.displayImage("drawable://" + R.drawable.face_man, imageView, options);
		if (fileName != null && !"".equals(fileName)) {
			final File imageFile = new File(sdcardHeadImageFolder, fileName);
			final String path = imageFile.getAbsolutePath();
			final String url = API.DOMAIN_COMMONIMAGE + "heads/" + fileName;
			if (imageFile.exists()) {
				imageLoader.displayImage("file://" + path, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						if (imageListener != null) {
							imageListener.onLoding();
						}
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						if (imageListener != null) {
							imageListener.onFailed();
						}
						imageFile.delete();
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_HEAD_IMAGE, null, imageListener);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						// if (imageListener != null) {
						// float width = loadedImage.getWidth();
						// float height = loadedImage.getHeight();
						// imageListener.onSuccess(height / width);
						// }
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_HEAD_IMAGE, null, imageListener);
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
		final String url = API.DOMAIN_OSS_THUMBNAIL + "images/" + fileName + "@" + width + "w_" + height + "h_1c_1e_80q";
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
						downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_THUMBLE_IMAGE, thumbleListener, null);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						// log.e(path + "-complete");
						if (thumbleListener != null) {
							thumbleListener.onResult();
						}
					}
				});
			} else {
				downloadImageFile(url, path, imageView, options, DownloadFile.TYPE_THUMBLE_IMAGE, thumbleListener, null);
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
				if (viewManage.chatView != null) {
					viewManage.chatView.mChatAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
			}
		});
		downloadFileList.addDownloadFile(downloadFile);
	}

	private void downloadImageFile(String url, String path, ImageView imageView, DisplayImageOptions options, int downloadType, ThumbleListener thumbleListener, final ImageListener imageListener) {
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = imageView;
		downloadFile.options = options;
		downloadFile.type = downloadType;
		downloadFile.thumbleListener = thumbleListener;
		downloadFile.imageListener = imageListener;
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
					taskManageHolder.audioHandler.startPlay(fileName, readSize);
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

	public ByteArrayOutputStream decodeSampledBitmapFromFileInputStream(ShareContentItem shareContentItem, File file, int reqWidth, int reqHeight) throws FileNotFoundException {
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
		if (shareContentItem != null) {
			shareContentItem.ratio = (float) options.outHeight / (float) options.outWidth;
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

	public byte[] getImageFileBytes(ShareContentItem shareContentItem, File fromFile, int width, int height) {
		long fileLength = fromFile.length();
		try {
			byte[] bytes;
			if (fileLength > 400 * 1024) {
				ByteArrayOutputStream byteArrayOutputStream = decodeSampledBitmapFromFileInputStream(shareContentItem, fromFile, width, height);
				bytes = byteArrayOutputStream.toByteArray();
				byteArrayOutputStream.close();
			} else {
				FileInputStream fileInputStream0 = new FileInputStream(fromFile);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(fileInputStream0, null, options);
				fileInputStream0.close();
				if (shareContentItem != null) {
					shareContentItem.ratio = (float) options.outHeight / (float) options.outWidth;
				}
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
