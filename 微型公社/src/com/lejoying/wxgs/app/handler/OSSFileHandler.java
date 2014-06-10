package com.lejoying.wxgs.app.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;

import com.aliyun.android.oss.task.PutObjectTask;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.StreamParser;

public class OSSFileHandler {

	MainApplication app;
	public static String FROM_SDCARD = "sdcard";
	public static String FROM_MEMORY = "memory";
	public static String FROM_WEB = "web";
	public static String FROM_DEFAULT = "default";

	public final int TYPE_IMAGE_COMMON = 0x01;
	public final int TYPE_IMAGE_HEAD = 0x02;
	public final int TYPE_IMAGE_BACK = 0x03;
	public final int TYPE_IMAGE_THUMBNAIL = 0x04;
	public final int TYPE_IMAGE_SQUAREIMAGE = 0x05;

	public Bitmap defaultImage;
	public Bitmap defaultHeadBoy;
	public Bitmap defaultHeadGirl;
	public Bitmap defaultBack;
	public Bitmap defaultSquareDetailImage;

	public int borderWidth = 5;

	Map<String, String> imageFromWebStatus = new Hashtable<String, String>();
	Map<String, List<FileResult>> fromWebResults = new HashMap<String, List<FileResult>>();

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

	public void initialize(MainApplication mainApplication) {
		this.app = mainApplication;
	}

	public Bitmaps bitmaps = new Bitmaps();

	public void getImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, "", TYPE_IMAGE_COMMON, fileResult, null, 0);
	}

	public void getHeadImage(String imageFileName, String sex,
			FileResult fileResult) {
		borderWidth = 5;
		getImageFile(imageFileName, sex, TYPE_IMAGE_HEAD, fileResult, null, 0);
	}

	public void getBackgroundImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, "", TYPE_IMAGE_BACK, fileResult, null, 0);
	}

	public void getSquareDetailImage(String imageFileName, int width,
			FileResult fileResult) {
		getImageFile(imageFileName, "", TYPE_IMAGE_SQUAREIMAGE, fileResult,
				null, width);
	}

	public void getThumbnail(String imageFileName, String size, int width,
			int height, FileResult fileResult) {
		String newName[] = imageFileName.split("\\.");
		String thumbnailName = newName[0] + size + newName[1];
		String paramFormat = "@" + width / 10 + "w_" + height / 10
				+ "h_1c_1i_50q";
		getImageFile(imageFileName, thumbnailName, TYPE_IMAGE_THUMBNAIL,
				fileResult, paramFormat, 0);
	}

	public void getImageFile(String imageFileName, String mediationParam,
			int type, FileResult fileResult, String style, int width) {
		if (defaultImage == null) {
			defaultImage = BitmapFactory.decodeResource(app.getResources(),
					R.drawable.defaultimage);
		}
		if (defaultHeadBoy == null || defaultHeadGirl == null) {
			if ("男".equals(mediationParam)) {
				defaultHeadBoy = MCImageUtils.getCircleBitmap(
						BitmapFactory.decodeResource(app.getResources(),
								R.drawable.face_man), true, borderWidth,
						Color.WHITE);
			} else if ("女".equals(mediationParam)) {
				defaultHeadGirl = MCImageUtils.getCircleBitmap(
						BitmapFactory.decodeResource(app.getResources(),
								R.drawable.face_man), true, borderWidth,
						Color.WHITE);
			}
		}
		if (defaultBack == null) {
			defaultBack = BitmapFactory.decodeResource(app.getResources(),
					R.drawable.background1);
		}

		Bitmap dImage = defaultImage;
		if (type == TYPE_IMAGE_HEAD) {
			if ("男".equals(mediationParam)) {
				dImage = defaultHeadBoy;
			} else if ("女".equals(mediationParam)) {
				dImage = defaultHeadBoy;
			}
		}
		if (type == TYPE_IMAGE_BACK) {
			dImage = defaultBack;
		}
		if (type == TYPE_IMAGE_THUMBNAIL) {
			dImage = defaultImage;
		}
		if (type == TYPE_IMAGE_SQUAREIMAGE) {
			if (defaultSquareDetailImage == null) {
				defaultSquareDetailImage = BitmapFactory.decodeResource(
						app.getResources(), R.drawable.defaultimage);
				int height = (int) (defaultSquareDetailImage.getHeight() * (width / defaultSquareDetailImage
						.getWidth()));
				defaultSquareDetailImage = Bitmap.createScaledBitmap(
						defaultImage, (int) (width), height, true);
			}
			fileResult.onResult(FROM_DEFAULT, defaultSquareDetailImage);
		}
		String where = FROM_DEFAULT;
		if (!"".equals(imageFileName)) {
			if (bitmaps.get(imageFileName) != null
					&& !bitmaps.get(imageFileName).equals(dImage)) {
				fileResult.onResult(where, bitmaps.get(imageFileName));
				where = FROM_MEMORY;
			} else {
				bitmaps.put(imageFileName, dImage);
				File imageFile = null;
				if (type == TYPE_IMAGE_COMMON) {
					imageFile = new File(app.sdcardImageFolder, imageFileName);
				}
				if (type == TYPE_IMAGE_HEAD) {
					imageFile = new File(app.sdcardHeadImageFolder,
							imageFileName);
				}
				if (type == TYPE_IMAGE_BACK) {
					imageFile = new File(app.sdcardBackImageFolder,
							imageFileName);
				}
				if (type == TYPE_IMAGE_THUMBNAIL) {
					imageFile = new File(app.sdcardThumbnailFolder,
							mediationParam);
				}
				if (type == TYPE_IMAGE_SQUAREIMAGE) {
					imageFile = new File(app.sdcardImageFolder, imageFileName);
				}
				if (imageFile.exists()) {
					Bitmap imageFileBitmap;
					if (type == TYPE_IMAGE_SQUAREIMAGE) {
						imageFileBitmap = MCImageUtils.getZoomBitmapFromFile(
								imageFile, (int) width, 0);
					} else {
						imageFileBitmap = BitmapFactory.decodeFile(imageFile
								.getAbsolutePath());
					}
					if (imageFileBitmap != null) {
						where = FROM_SDCARD;
						if (type == TYPE_IMAGE_COMMON
								|| type == TYPE_IMAGE_BACK
								|| type == TYPE_IMAGE_SQUAREIMAGE) {
							fileResult.onResult(where, imageFileBitmap);
						} else if (type == TYPE_IMAGE_HEAD) {
							bitmaps.put(imageFileName, MCImageUtils
									.getCircleBitmap(imageFileBitmap, true,
											borderWidth, Color.WHITE));
							fileResult.onResult(where,
									bitmaps.get(imageFileName));
						} else if (type == TYPE_IMAGE_THUMBNAIL) {
							bitmaps.put(imageFileName, imageFileBitmap);
							fileResult.onResult(where,
									bitmaps.get(imageFileName));
						}
					} else {
						if (fromWebResults.get(imageFileName) == null) {
							fromWebResults.put(imageFileName,
									new ArrayList<FileResult>());
						}

						fromWebResults.get(imageFileName).add(fileResult);
						if (imageFromWebStatus.get(imageFileName) == null
								|| imageFromWebStatus.get(imageFileName)
										.equals("failed")) {
							getImageFileFromWeb(imageFileName, mediationParam,
									type, style, width);
						}
					}
				} else {
					if (fromWebResults.get(imageFileName) == null) {
						fromWebResults.put(imageFileName,
								new ArrayList<FileResult>());
					}
					fromWebResults.get(imageFileName).add(fileResult);
					if (imageFromWebStatus.get(imageFileName) == null
							|| imageFromWebStatus.get(imageFileName).equals(
									"failed")) {
						getImageFileFromWeb(imageFileName, mediationParam,
								type, style, width);
					}
				}
			}
		} else {
			fileResult.onResult(where, dImage);
		}
	}

	public interface FileResult {
		public void onResult(String where, Bitmap bitmap);
	}

	private void getImageFileFromWeb(final String imageFileName,
			final String mediationParam, final int type, final String style,
			final int width) {
		imageFromWebStatus.put(imageFileName, "loading");
		File folder = app.sdcardImageFolder;
		if (type == TYPE_IMAGE_HEAD) {
			folder = app.sdcardHeadImageFolder;
		} else if (type == TYPE_IMAGE_BACK) {
			folder = app.sdcardBackImageFolder;
		} else if (type == TYPE_IMAGE_THUMBNAIL) {
			folder = app.sdcardThumbnailFolder;
		}
		final File f = folder;
		String fileName = imageFileName;
		if (type == TYPE_IMAGE_THUMBNAIL) {
			fileName = mediationParam;
		}
		final String fileName0 = fileName;
		app.networkHandler.connection(new NetConnection() {
			@Override
			protected void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				try {
					File tempFile = new File(f, "temp_" + fileName0);
					StreamParser
							.parseToFile(is, new FileOutputStream(tempFile));
					httpURLConnection.disconnect();

					imageFromWebStatus.put(fileName0, "success");
					File imageFile = new File(f, fileName0);
					tempFile.renameTo(imageFile);

					Bitmap bitmap = BitmapFactory.decodeFile(imageFile
							.getAbsolutePath());
					if (type == TYPE_IMAGE_HEAD) {
						bitmap = MCImageUtils.getCircleBitmap(bitmap, true,
								borderWidth, Color.WHITE);
						bitmaps.put(fileName0, bitmap);
					} else if (type == TYPE_IMAGE_THUMBNAIL) {
						bitmaps.put(fileName0, bitmap);
					} else if (type == TYPE_IMAGE_SQUAREIMAGE) {
						bitmap = MCImageUtils.getZoomBitmapFromFile(imageFile,
								(int) width, 0);
					}
					final Bitmap bitmap0 = bitmap;
					List<FileResult> results = fromWebResults.get(fileName0);
					for (final FileResult result : results) {
						app.UIHandler.post(new Runnable() {
							@Override
							public void run() {
								result.onResult(FROM_WEB, bitmap0);
							}
						});
					}
					fromWebResults.remove(fileName0);
				} catch (FileNotFoundException e) {
					if (imageFromWebStatus.get(fileName0).equals("failed")) {
						imageFromWebStatus.put(fileName0, "error");
					}
					imageFromWebStatus.put(fileName0, "failed");
				}
			}

			@Override
			protected void failed(int failedType, int responseCode) {
				if (imageFromWebStatus.get(fileName0).equals("failed")) {
					imageFromWebStatus.put(fileName0, "error");
				}
				imageFromWebStatus.put(fileName0, "failed");
				super.failed(failedType, responseCode);
			}

			@Override
			protected void settings(Settings settings) {
				if (type == TYPE_IMAGE_THUMBNAIL) {
					settings.url = API.DOMAIN_HANDLEIMAGE + imageFileName
							+ style;
					settings.method = GET;
				} else {
					settings.url = API.DOMAIN_COMMONIMAGE + imageFileName;
					settings.method = GET;
				}
			}
		});
	}

	public void saveBitmap(final SaveBitmapInterface saveBitmapInterface) {
		final SaveSettings settings = new SaveSettings();
		saveBitmapInterface.setParams(settings);
		final Bitmap source = settings.source;
		if (source != null) {
			new Thread() {
				public void run() {
					byte[] imageByteArray = MCImageUtils
							.getByteArrayFromBitmap(source,
									settings.compressFormat, 100);
					if (imageByteArray != null) {
						String base64 = Base64.encodeToString(imageByteArray,
								Base64.DEFAULT);
						base64 = base64.trim();
						String sha1 = app.mSHA1.getDigestOfString(base64
								.getBytes());
						sha1 = sha1.toLowerCase(Locale.getDefault());
						String format = settings.compressFormat == settings.PNG ? ".png"
								: ".jpg";
						File localFile = new File(settings.folder, sha1
								+ format);
						if (!localFile.exists()) {
							FileOutputStream fileOutputStream = null;
							try {
								fileOutputStream = new FileOutputStream(
										localFile);
								fileOutputStream.write(imageByteArray);
								fileOutputStream.flush();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								if (fileOutputStream != null) {
									try {
										fileOutputStream.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
						saveBitmapInterface.onSuccess(sha1 + format, base64);
					}
				}
			}.start();

		}
	}

	public class SaveSettings {
		public Bitmap.CompressFormat PNG = Bitmap.CompressFormat.PNG;
		public Bitmap.CompressFormat JPG = Bitmap.CompressFormat.JPEG;
		public Bitmap source;
		public File folder = app.sdcardImageFolder;
		public Bitmap.CompressFormat compressFormat = JPG;
	}

	public interface SaveBitmapInterface {

		public void setParams(SaveSettings settings);

		public void onSuccess(String fileName, String base64);
	}

	public static class FileSettings {
		public String fileName;
		public File folder;
	}

	public interface FileInterface {
		public void setParams(FileSettings settings);

		public void onSuccess(Boolean flag, String fileName);
	}

	public void getFile(final FileInterface fileInterface) {
		FileSettings settings = new FileSettings();
		fileInterface.setParams(settings);
		final File file = new File(settings.folder, settings.fileName);
		if (!file.exists()) {
			final File folder = settings.folder;
			final String fileName = settings.fileName;
			app.networkHandler.connection(new NetConnection() {

				@Override
				protected void success(InputStream is,
						HttpURLConnection httpURLConnection) {
					try {
						File tempFile = new File(folder, "temp_" + fileName);
						StreamParser.parseToFile(is, new FileOutputStream(
								tempFile));
						httpURLConnection.disconnect();
						tempFile.renameTo(file);
						app.UIHandler.post(new Runnable() {

							@Override
							public void run() {
								fileInterface.onSuccess(true, fileName);
							}
						});
					} catch (FileNotFoundException e) {
						// voiceInterface.onSuccess(false);
						e.printStackTrace();
					}
				}

				@Override
				protected void failed(int failedType, int responseCode) {
					// voiceInterface.onSuccess(false);
					super.failed(failedType, responseCode);
				}

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN_COMMONIMAGE + fileName;
					settings.method = GET;
				}
			});
		} else {
			fileInterface.onSuccess(true, settings.fileName);
		}
	}

	public byte[] getFileBytes(File folder, String fileName) {
		File file = new File(folder, fileName);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] bytes = null;
		try {
			bytes = new byte[fileInputStream.available()];
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileInputStream.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static class ImageMessageInfo {
		public String fileName;
		public String md5;
		public byte[] data;

		ImageMessageInfo(String fileName, String md5, byte[] data) {
			this.fileName = fileName;
			this.md5 = md5;
			this.data = data;
		}
	}

	public static final int FILE_TYPE_SDIMAGE = 0x001;
	public static final int FILE_TYPE_ASSETS = 0x002;
	public static final int FILE_TYPE_SDVOICE = 0x003;

	public void getFileMessageInfo(
			final FileMessageInfoInterface fileMessageInfoInterface) {
		final FileMessageInfoSettings settings = new FileMessageInfoSettings();
		fileMessageInfoInterface.setParams(settings);
		File tempFile = null;
		if (settings.FILE_TYPE != FILE_TYPE_ASSETS) {
			tempFile = new File(settings.folder, settings.fileName);
			if (!tempFile.isFile()) {
				return;
			}
			if (!tempFile.exists()) {
				return;
			}
		}
		final File file = tempFile;
		new Thread(new Runnable() {

			@Override
			public void run() {
				String fileName = settings.fileName;
				String suffixLastName = fileName.substring(fileName
						.lastIndexOf("."));
				MessageDigest digest = null;
				InputStream in = null;
				byte buffer[] = new byte[1024];
				int len;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] data = null;
				try {
					digest = MessageDigest.getInstance("MD5");
					if (settings.FILE_TYPE != FILE_TYPE_ASSETS) {
						in = new FileInputStream(file);
					} else {
						in = app.getResources().getAssets()
								.open(settings.assetsPath + settings.fileName);
					}
					while ((len = in.read(buffer, 0, 1024)) != -1) {
						digest.update(buffer, 0, len);
						bos.write(buffer, 0, len);
					}
					bos.flush();
					data = bos.toByteArray();
					bos.close();
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String sha1 = new com.lejoying.wxgs.utils.SHA1()
						.getDigestOfString(data).toLowerCase(
								Locale.getDefault())
						+ suffixLastName;
				BigInteger bigInt = new BigInteger(1, digest.digest());
				String md5 = bigInt.toString(16).toLowerCase(
						Locale.getDefault());
				final ImageMessageInfo imageMessageInfo = new ImageMessageInfo(
						sha1, md5, data);
				app.UIHandler.post(new Runnable() {

					@Override
					public void run() {
						fileMessageInfoInterface.onSuccess(imageMessageInfo);
					}
				});
			}
		}).start();
	}

	public class FileMessageInfoSettings {
		public int FILE_TYPE;
		public String fileName;
		public File folder;
		public String assetsPath;
	}

	public interface FileMessageInfoInterface {

		public void setParams(FileMessageInfoSettings settings);

		public void onSuccess(ImageMessageInfo imageMessageInfo);
	}

	public static class UploadFileSettings {
		public String fileName;
		public ImageMessageInfo imageMessageInfo;
		public String contentType;
	}

	public interface UploadFileInterface {
		public void setParams(UploadFileSettings settings);

		public void onSuccess(Boolean flag, String fileName);
	}

	public static void uploadFile(final UploadFileInterface uploadFileInterface) {
		final UploadFileSettings settings = new UploadFileSettings();
		uploadFileInterface.setParams(settings);
		final String fileName = settings.fileName;
		final ImageMessageInfo imageMessageInfo = settings.imageMessageInfo;
		new Thread(new Runnable() {

			@Override
			public void run() {
				PutObjectTask task = new PutObjectTask(API.BUCKETNAME,
						fileName, settings.contentType);
				task.initKey(API.ACCESS_ID, API.ACCESS_KEY);
				task.setData(imageMessageInfo.data);
				String result = task.getResult().toLowerCase(
						Locale.getDefault());
				if (result.equals(imageMessageInfo.md5)) {
					uploadFileInterface.onSuccess(true, fileName);
				} else {
					uploadFileInterface.onSuccess(false, fileName);
				}
			}
		}).start();
	}

}
