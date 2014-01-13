package com.lejoying.mc.data.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.widget.Toast;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.DownloadListener;

public class FileHandler {
	public App app;

	public void initailize(App app) {
		this.app = app;
	}

	public void getFile(String fileName) {
		// get from web
		// get from sdcard
		// get from mem directly

		// save to sdcard
		// save to mem

	}

	public String FROM_SDCARD = "sdcard";
	public String FROM_MEMORY = "memory";
	public String FROM_WEB = "web";
	public String FROM_DEFAULT = "default";

	public Map<String, Bitmap> bitmaps = new Hashtable<String, Bitmap>();
	// TODO to be managed by native code
	public Bitmap defaultImage;
	public Bitmap defaultHead;

	public final int TYPE_IMAGE_COMMON = 0x01;
	public final int TYPE_IMAGE_HEAD = 0x02;

	public void getImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, TYPE_IMAGE_COMMON, fileResult);
	}

	public void getHeadImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, TYPE_IMAGE_HEAD, fileResult);
	}

	// TODO sd_card space checking
	// TODO zip images in the sd_card
	public void getImageFile(String imageFileName, int type,
			FileResult fileResult) {
		// get from mem directly
		// get from sdcard

		// get from web

		// save to sdcard
		// save to mem
		if (defaultImage == null) {
			defaultImage = BitmapFactory.decodeResource(
					app.context.getResources(), R.drawable.defaultimage);
		}
		if (defaultHead == null) {
			defaultHead = MCImageTools.getCircleBitmap(BitmapFactory
					.decodeResource(app.context.getResources(),
							R.drawable.face_man), true, 5, Color.WHITE);
		}
		Bitmap dImage = defaultImage;
		if (type == TYPE_IMAGE_HEAD) {
			dImage = defaultHead;
		}
		String where = FROM_DEFAULT;
		if (bitmaps.get(imageFileName) != null
				&& !bitmaps.get(imageFileName).equals(defaultImage)) {
			// todo return bitmaps.get(imageFileName) ;
			where = FROM_MEMORY;
		} else {
			if (bitmaps.get(imageFileName) == null) {
				bitmaps.put(imageFileName, dImage);
			}
			if (!imageFileName.equals("")) {
				File imageFile = new File(app.sdcardImageFolder, imageFileName);
				if (type == TYPE_IMAGE_HEAD) {
					imageFile = new File(app.sdcardHeadImageFolder,
							imageFileName);
				}
				if (imageFile.exists()) {
					Bitmap image = BitmapFactory.decodeFile(imageFile
							.getAbsolutePath());
					if (image != null) {
						if (type == TYPE_IMAGE_COMMON) {
							bitmaps.put(imageFileName, image);
						} else if (type == TYPE_IMAGE_HEAD) {
							bitmaps.put(imageFileName, MCImageTools
									.getCircleBitmap(image, true, 5,
											Color.WHITE));
						}
						where = FROM_SDCARD;
					} else {
						if (getImageFromWebStatus.get(imageFileName) == null
								|| getImageFromWebStatus.get(imageFileName)
										.equals("failed")) {
							getImageFileFromWeb(imageFileName, type, fileResult);
						}
					}
				} else {
					if (getImageFromWebStatus.get(imageFileName) == null
							|| getImageFromWebStatus.get(imageFileName).equals(
									"failed")) {
						getImageFileFromWeb(imageFileName, type, fileResult);
					}
				}
			}
		}
		fileResult.onResult(where);
	}

	// key:imageFilename value:"loading"|"success"|"failed"
	Map<String, String> getImageFromWebStatus = new Hashtable<String, String>();

	private void getImageFileFromWeb(final String imageFileName,
			final int type, final FileResult fileResult) {
		getImageFromWebStatus.put(imageFileName, "loading");
		File folder = app.sdcardImageFolder;
		if (type == TYPE_IMAGE_HEAD) {
			folder = app.sdcardHeadImageFolder;
		}
		MCNetTools.downloadFile(app.context, app.config.DOMAIN_IMAGE,
				imageFileName, folder, null, 5000, new DownloadListener() {
					@Override
					public void success(File localFile, InputStream inputStream) {
						getImageFromWebStatus.put(imageFileName, "success");
						Bitmap bitmap = BitmapFactory.decodeFile(localFile
								.getAbsolutePath());
						if (type == TYPE_IMAGE_HEAD) {
							bitmap = MCImageTools.getCircleBitmap(bitmap, true,
									5, Color.WHITE);
						}
						bitmaps.put(imageFileName, bitmap);
						app.mUIThreadHandler.post(new Runnable() {
							public void run() {
								fileResult.onResult(FROM_WEB);
							}
						});
					}

					public void noInternet() {
						getImageFromWebStatus.put(imageFileName, "failed");
						app.mUIThreadHandler.post(new Runnable() {
							public void run() {
								Toast.makeText(app.context, "没有网络连接，网络不给力呀~",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					public void failed() {
						getImageFromWebStatus.put(imageFileName, "failed");
						app.mUIThreadHandler.post(new Runnable() {
							public void run() {
								Toast.makeText(app.context, "网络连接失败，网络不给力呀~",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO to be resolved.

					}

					@Override
					public void downloading(int progress) {
						// TODO to be resolved.
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
					byte[] imageByteArray = MCImageTools
							.getByteArrayFromBitmap(source,
									settings.compressFormat, 100);
					if (imageByteArray != null) {
						String base64 = Base64.encodeToString(imageByteArray,
								Base64.DEFAULT);
						base64 = base64.trim();
						String sha1 = app.sha1.getDigestOfString(base64
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
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								if (fileOutputStream != null) {
									try {
										fileOutputStream.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
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

	public void saveBitmapAndUpload(SaveBitmapInterface saveBitmapInterface) {

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

	public interface FileResult {
		public void onResult(String where);
	}
}
