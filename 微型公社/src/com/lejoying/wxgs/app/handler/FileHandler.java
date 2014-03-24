package com.lejoying.wxgs.app.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import android.util.Base64;
import android.util.Log;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.StreamParser;

public class FileHandler {
	MainApplication app;

	public void initialize(MainApplication app) {
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
			defaultImage = BitmapFactory.decodeResource(app.getResources(),
					R.drawable.defaultimage);
		}
		if (defaultHead == null) {
			defaultHead = MCImageUtils.getCircleBitmap(BitmapFactory
					.decodeResource(app.getResources(), R.drawable.face_man),
					true, 5, Color.WHITE);
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
							bitmaps.put(imageFileName, MCImageUtils
									.getCircleBitmap(image, true, 5,
											Color.WHITE));
						}
						where = FROM_SDCARD;
					} else {
						if (getFromWebResults.get(imageFileName) == null) {
							getFromWebResults.put(imageFileName,
									new ArrayList<FileHandler.FileResult>());
						}
						getFromWebResults.get(imageFileName).add(fileResult);
						if (getImageFromWebStatus.get(imageFileName) == null
								|| getImageFromWebStatus.get(imageFileName)
										.equals("failed")) {
							getImageFileFromWeb(imageFileName, type);
						}
					}
				} else {
					if (getFromWebResults.get(imageFileName) == null) {
						getFromWebResults.put(imageFileName,
								new ArrayList<FileHandler.FileResult>());
					}
					getFromWebResults.get(imageFileName).add(fileResult);
					if (getImageFromWebStatus.get(imageFileName) == null
							|| getImageFromWebStatus.get(imageFileName).equals(
									"failed")) {
						getImageFileFromWeb(imageFileName, type);
					}
				}
			}
		}
		fileResult.onResult(where);
	}

	// key:imageFilename value:"loading"|"success"|"failed"|"error"
	Map<String, String> getImageFromWebStatus = new Hashtable<String, String>();
	Map<String, List<FileResult>> getFromWebResults = new HashMap<String, List<FileResult>>();

	private void getImageFileFromWeb(final String imageFileName, final int type) {
		getImageFromWebStatus.put(imageFileName, "loading");
		File folder = app.sdcardImageFolder;
		if (type == TYPE_IMAGE_HEAD) {
			folder = app.sdcardHeadImageFolder;
		}
		final File f = folder;
		app.networkHandler.connection(new NetConnection() {

			@Override
			protected void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				try {
					StreamParser.parseToFile(is, new FileOutputStream(new File(
							f, imageFileName)));
					httpURLConnection.disconnect();
					Bitmap bitmap = BitmapFactory.decodeFile(new File(f,
							imageFileName).getAbsolutePath());
					if (type == TYPE_IMAGE_HEAD) {
						bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5,
								Color.WHITE);
					}
					bitmaps.put(imageFileName, bitmap);

					for (final FileResult result : getFromWebResults
							.get(imageFileName)) {
						app.UIHandler.post(new Runnable() {
							@Override
							public void run() {
								result.onResult(FROM_WEB);
							}
						});
					}
				} catch (FileNotFoundException e) {
					if (getImageFromWebStatus.get(imageFileName).equals(
							"failed")) {
						getImageFromWebStatus.put(imageFileName, "error");
					}
					getImageFromWebStatus.put(imageFileName, "failed");
				}
			}

			@Override
			protected void failed(int failedType, int responseCode) {
				if (getImageFromWebStatus.get(imageFileName).equals("failed")) {
					getImageFromWebStatus.put(imageFileName, "error");
				}
				getImageFromWebStatus.put(imageFileName, "failed");
				super.failed(failedType, responseCode);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN_IMAGE + imageFileName;
				settings.method = GET;
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

	public void getVoice(final VoiceInterface getVoiceInterface) {
		final VoiceSettings settings = new VoiceSettings();
		getVoiceInterface.setParams(settings);
		final File voiceFile = new File(settings.folder, settings.fileName);
		if (voiceFile.exists()) {
			new Thread() {
				public void run() {
					try {
						FileInputStream fis = null;
						fis = new FileInputStream(voiceFile);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = fis.read(buffer)) != -1) {
							bos.write(buffer, 0, len);
						}
						bos.flush();
						byte[] data = bos.toByteArray();
						bos.close();
						fis.close();
						String base64 = Base64.encodeToString(data,
								Base64.DEFAULT);
						base64 = base64.trim();
						String sha1 = app.mSHA1.getDigestOfString(base64
								.getBytes());
						getVoiceInterface.onSuccess(
								(sha1 + settings.format).toLowerCase(), base64,
								null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	};

	@SuppressLint("DefaultLocale")
	public void saveVoice(final VoiceInterface saveVoiceInterface) {
		final VoiceSettings settings = new VoiceSettings();
		saveVoiceInterface.setParams(settings);
		File voiceFile = new File(settings.folder, settings.fileName);
		final File folder = settings.folder;
		final String voiceFileName = (settings.fileName).toLowerCase();
		if (!voiceFile.exists()) {
			new Thread() {
				@Override
				public void run() {
					app.networkHandler.connection(new NetConnection() {

						@Override
						protected void success(InputStream is,
								HttpURLConnection httpURLConnection) {
							try {
								StreamParser.parseToFile(is,
										new FileOutputStream(new File(folder,
												voiceFileName)));
								httpURLConnection.disconnect();
								saveVoiceInterface.onSuccess(null, null, true);
							} catch (FileNotFoundException e) {
								saveVoiceInterface.onSuccess(null, null, false);
								e.printStackTrace();
							}
						}

						@Override
						protected void failed(int failedType, int responseCode) {
							saveVoiceInterface.onSuccess(null, null, false);
							super.failed(failedType, responseCode);
						}

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN_IMAGE + voiceFileName;
							settings.method = GET;
						}
					});
				}
			}.start();
		} else {
			saveVoiceInterface.onSuccess(null, null, true);
		}
	}

	public class VoiceSettings {
		public String fileName;
		public String format;
		public File folder = app.sdcardVoiceFolder;
	}

	public interface VoiceInterface {
		public void setParams(VoiceSettings settings);

		public void onSuccess(String filename, String base64, Boolean flag);
	}

}
