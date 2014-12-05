package com.open.welinks.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.SystemClock;

import com.open.lib.MyLog;
import com.open.welinks.MainActivity;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;

public class FileHandler {

	public static String tag = "FileHandlers";
	public static MyLog log = new MyLog(tag, true);

	public static FileHandler fileHandlers;

	public static FileHandler getInstance() {
		if (fileHandlers == null) {
			fileHandlers = new FileHandler();

			log.e("FileHandler getInstance()》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
		}
		return fileHandlers;
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
							resolveFile(myFile);
							myFile.task.currentResolveFileCount++;
							if (myFile.task.currentResolveFileCount == myFile.task.resolveFileTotal) {
								myFile.task.onLocalFilesResolved();
							}
						} else {
							// TODO voice
						}
						myFile.status.state = myFile.status.LocalStored;
						myFileUploadQueue.offerE(myFile);
					}
				} catch (Exception e) {
					StackTraceElement ste = new Throwable().getStackTrace()[0];
					log.e("Exception@" + ste.getLineNumber() + "," + ExceptionHandler.printStackTrace(MainActivity.instance.thisController.context, e));
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

	int CompressThreshold = 400 * 1024;
	public SHA1 sha1 = SHA1.getInstance();

	void resolveFile(MyFile myFile) throws Exception {
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

	public File sdcard;
	public File sdcardFolder;
	public File sdcardImageFolder;

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

}
