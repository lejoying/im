package com.lejoying.wxgs.app.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.StreamParser;

public class OSSFileHandler {

	MainApplication app = MainApplication.getMainApplication();
	public static String FROM_SDCARD = "sdcard";
	public static String FROM_MEMORY = "memory";
	public static String FROM_WEB = "web";
	public static String FROM_DEFAULT = "default";

	public final int TYPE_IMAGE_COMMON = 0x01;
	public final int TYPE_IMAGE_HEAD = 0x02;
	public final int TYPE_IMAGE_BACK = 0x03;
	public final int TYPE_IMAGE_THUMBNAIL = 0x04;

	public Bitmap defaultImage;
	public Bitmap defaultHeadBoy;
	public Bitmap defaultHeadGirl;
	public Bitmap defaultBack;

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

	public Bitmaps bitmaps = new Bitmaps();

	public void getImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, "", TYPE_IMAGE_COMMON, fileResult);
	}

	public void getHeadImage(String imageFileName, String sex,FileResult fileResult) {
		borderWidth = 5;
		getImageFile(imageFileName,sex,TYPE_IMAGE_HEAD, fileResult);
	}

	public void getBackgroundImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, "", TYPE_IMAGE_BACK, fileResult);
	}

	public void getThumbnail(String imageFileName,String size, int width,
			int height, FileResult fileResult) {
		String newName[] = imageFileName.split("\\.");
		String thumbnailName = newName[0] + size + newName[1];
		String paramFormat="@"+width+"w_"+height+"h_1c_1i_50q";
		getImageFile(thumbnailName,paramFormat,TYPE_IMAGE_THUMBNAIL,
				fileResult);
	}

	public void getImageFile(String imageFileName, String mediationParam,
			int type, FileResult fileResult) {
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
							imageFileName);
				}
				if (imageFile.exists()) {
					Bitmap imageFileBitmap = BitmapFactory.decodeFile(imageFile
							.getAbsolutePath());
					if (imageFileBitmap != null) {
						if (type == TYPE_IMAGE_COMMON
								|| type == TYPE_IMAGE_BACK) {
							where = FROM_SDCARD;
							fileResult.onResult(where,imageFileBitmap);
						} else if (type == TYPE_IMAGE_HEAD) {
							bitmaps.put(imageFileName, MCImageUtils
									.getCircleBitmap(imageFileBitmap, true,
											borderWidth, Color.WHITE));
							where = FROM_SDCARD;
							fileResult.onResult(where, bitmaps.get(imageFileName));
						} else if (type == TYPE_IMAGE_THUMBNAIL) {
							bitmaps.put(imageFileName, imageFileBitmap);
							where = FROM_SDCARD;
							fileResult.onResult(where, bitmaps.get(imageFileName));
						}
					} else {
						if (fromWebResults.get(imageFileName) == null) {
							fromWebResults.put(imageFileName,
									new ArrayList<FileHandler.FileResult>());
						}
						fromWebResults.get(imageFileName).add(fileResult);
						if (imageFromWebStatus.get(imageFileName) == null
								|| imageFromWebStatus.get(imageFileName)
										.equals("failed")) {
							getImageFileFromWeb(imageFileName, mediationParam,
									type);
						}
					}
				} else {
					if (fromWebResults.get(imageFileName) == null) {
						fromWebResults.put(imageFileName,
								new ArrayList<FileHandler.FileResult>());
					}
					fromWebResults.get(imageFileName).add(fileResult);
					if (imageFromWebStatus.get(imageFileName) == null
							|| imageFromWebStatus.get(imageFileName).equals(
									"failed")) {
						getImageFileFromWeb(imageFileName, mediationParam, type);
					}
				}
			}
			fileResult.onResult(where, null);
		} else {
			fileResult.onResult(where, dImage);
		}
	}

	private void getImageFileFromWeb(final String imageFileName,
			final String mediationParam, final int type) {
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

		app.networkHandler.connection(new NetConnection() {
			@Override
			protected void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				try {
					File tempFile = new File(f, "temp_" + imageFileName);
					StreamParser
							.parseToFile(is, new FileOutputStream(tempFile));
					imageFromWebStatus.put(imageFileName, "success");
					httpURLConnection.disconnect();
					
					String fileName = imageFileName;
					File imageFile = new File(f, fileName);
					tempFile.renameTo(imageFile);

					Bitmap bitmap = BitmapFactory.decodeFile(new File(f,
							imageFileName).getAbsolutePath());
					if (type == TYPE_IMAGE_HEAD) {
						bitmap = MCImageUtils.getCircleBitmap(bitmap, true,
								borderWidth, Color.WHITE);
						bitmaps.put(imageFileName, bitmap);
					} else if (type == TYPE_IMAGE_THUMBNAIL) {
						bitmaps.put(imageFileName, bitmap);
					}
					final Bitmap bitmap0 = bitmap;
					List<FileResult> results = fromWebResults
							.get(imageFileName);
					for (final FileResult result : results) {
						app.UIHandler.post(new Runnable() {
							@Override
							public void run() {
								result.onResult(FROM_WEB, bitmap0);
							}
						});
					}
					fromWebResults.remove(imageFileName);
				} catch (FileNotFoundException e) {
					if (imageFromWebStatus.get(imageFileName).equals("failed")) {
						imageFromWebStatus.put(imageFileName, "error");
					}
					imageFromWebStatus.put(imageFileName, "failed");
				}
			}

			@Override
			protected void failed(int failedType, int responseCode) {
				if (imageFromWebStatus.get(imageFileName).equals("failed")) {
					imageFromWebStatus.put(imageFileName, "error");
				}
				imageFromWebStatus.put(imageFileName, "failed");
				super.failed(failedType, responseCode);
			}

			@Override
			protected void settings(Settings settings) {
				if(type==TYPE_IMAGE_THUMBNAIL){
					settings.url = API.DOMAIN_HANDLEIMAGE + imageFileName+mediationParam;
					settings.method = GET;
				}else{
					settings.url = API.DOMAIN_COMMONIMAGE + imageFileName;
					settings.method = GET;
				}
			}
		});
	}

	static class ImageMessageInfo {
		String fileName;
		String md5;
		byte[] data;

		ImageMessageInfo(String fileName, String md5, byte[] data) {
			this.fileName = fileName;
			this.md5 = md5;
			this.data = data;
		}
	}

	@SuppressLint("DefaultLocale")
	public static ImageMessageInfo getImageMessageInfo(File file,
			String fileName) {
		if (!file.isFile()) {
			return null;
		}
		String suffixLastName = fileName.substring(fileName.lastIndexOf("."));
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
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
			return null;
		}
		String sha1 = new com.lejoying.wxgs.utils.SHA1()
				.getDigestOfString(data).toLowerCase(Locale.getDefault())
				+ suffixLastName;
		BigInteger bigInt = new BigInteger(1, digest.digest());
		String md5 = bigInt.toString(16).toLowerCase(Locale.getDefault());
		ImageMessageInfo imageMessageInfo = new ImageMessageInfo(sha1, md5,
				data);
		return imageMessageInfo;
	}
}
